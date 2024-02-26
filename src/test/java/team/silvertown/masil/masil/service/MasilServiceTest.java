package team.silvertown.masil.masil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ForbiddenException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;
import team.silvertown.masil.masil.dto.CreatePinRequest;
import team.silvertown.masil.masil.dto.CreateRequest;
import team.silvertown.masil.masil.dto.CreateResponse;
import team.silvertown.masil.masil.dto.MasilResponse;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.repository.MasilPinRepository;
import team.silvertown.masil.masil.repository.MasilRepository;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilServiceTest {

    static final Faker faker = new Faker(Locale.KOREA);

    static double dongAnLat = 37.4004;
    static double dongAnLng = 126.9555;
    static double appender = 0.002;

    @Autowired
    MasilService masilService;

    @Autowired
    MasilRepository masilRepository;

    @Autowired
    MasilPinRepository masilPinRepository;

    @Autowired
    UserRepository userRepository;

    User user;
    String addressDepth1;
    String addressDepth2;
    String addressDepth3;
    List<KakaoPoint> path;
    String title;
    int distance;
    int totalTime;

    @BeforeEach
    void setUp() {
        user = userRepository.save(createUser());
        addressDepth1 = faker.address()
            .state();
        addressDepth2 = faker.address()
            .city();
        addressDepth3 = faker.address()
            .streetName();
        path = createPath(10000);
        title = faker.lorem()
            .maxLengthSentence(29);
        distance = faker.number()
            .numberBetween(1000, 1500);
        totalTime = faker.number()
            .numberBetween(10, 70);
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 0})
    void 마실_생성을_성공한다(int expectedPinCount) {
        // given
        List<CreatePinRequest> pinRequests = createPinRequests(expectedPinCount, 10000);
        CreateRequest request = new CreateRequest(addressDepth1, addressDepth2, addressDepth3,
            "", path, title, null, distance, totalTime,
            OffsetDateTime.now(), pinRequests, null, null);

        // when
        CreateResponse expected = masilService.create(user.getId(), request);

        // then
        Masil actual = masilRepository
            .findById(expected.id())
            .orElseThrow();
        List<MasilPin> actualPins = masilPinRepository.findAll();

        assertThat(actual.getId()).isEqualTo(expected.id());
        assertThat(actualPins.size()).isEqualTo(expectedPinCount);
    }

    @Test
    void 사용자가_존재하지_않으면_마실_생성을_실패한다() {
        // given
        long invalidId = faker.random()
            .nextLong(Long.MAX_VALUE);
        CreateRequest request = new CreateRequest(addressDepth1, addressDepth2, addressDepth3,
            "", path, title, null, distance, totalTime, OffsetDateTime.now(), null, null, null);

        // when
        ThrowingCallable create = () -> masilService.create(invalidId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 마실_단일_조회를_성공한다() {
        // given
        List<CreatePinRequest> pinRequests = createPinRequests(10, 10000);
        CreateRequest request = new CreateRequest(addressDepth1, addressDepth2, addressDepth3,
            "", path, title, null, distance, totalTime,
            OffsetDateTime.now(), pinRequests, null, null);
        CreateResponse expected = masilService.create(user.getId(), request);

        // when
        MasilResponse actual = masilService.getById(user.getId(), expected.id());

        // then
        assertThat(actual).extracting("id", "title", "distance", "totalTime")
            .containsExactly(expected.id(), title, distance, totalTime);
        assertThat(actual.pins()).hasSize(pinRequests.size());
    }

    @Test
    void 사용자가_존재하지_않으면_마실_단일_조회를_실패한다() {
        // given
        long invalidId = faker.random()
            .nextLong(Long.MAX_VALUE);

        // when
        ThrowingCallable getById = () -> masilService.getById(invalidId, invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getById)
            .withMessage(MasilErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 마실이_존재하지_않으면_마실_단일_조회를_실패한다() {
        // given
        long invalidId = faker.random()
            .nextLong(Long.MAX_VALUE);

        // when
        ThrowingCallable getById = () -> masilService.getById(user.getId(), invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getById)
            .withMessage(MasilErrorCode.MASIL_NOT_FOUND.getMessage());
    }

    @Test
    void 로그인한_사용자와_마실_소유자가_다르면_마실_단일_조회를_실패한다() {
        // given
        User differntUser = userRepository.save(createUser());
        List<CreatePinRequest> pinRequests = createPinRequests(10, 10000);
        CreateRequest request = new CreateRequest(addressDepth1, addressDepth2, addressDepth3,
            "", path, title, null, distance, totalTime,
            OffsetDateTime.now(), pinRequests, null, null);
        CreateResponse expected = masilService.create(user.getId(), request);

        // when
        ThrowingCallable getById = () -> masilService.getById(differntUser.getId(), expected.id());

        // then
        assertThatExceptionOfType(ForbiddenException.class).isThrownBy(getById)
            .withMessage(MasilErrorCode.USER_NOT_AUTHORIZED_FOR_MASIL.getMessage());
    }

    User createUser() {
        String nickname = faker.funnyName()
            .name();
        LocalDate birthDate = faker.date()
            .birthdayLocalDate(20, 40);
        int height = faker.number()
            .numberBetween(170, 190);
        int weight = faker.number()
            .numberBetween(60, 90);
        long socialId = faker.random()
            .nextLong(Long.MAX_VALUE);

        return User.builder()
            .nickname(nickname)
            .exerciseIntensity(ExerciseIntensity.MIDDLE)
            .sex(Sex.MALE)
            .birthDate(Date.valueOf(birthDate))
            .height(height)
            .weight(weight)
            .provider(Provider.KAKAO)
            .socialId(String.valueOf(socialId))
            .build();
    }

    List<CreatePinRequest> createPinRequests(int size, int maxPathPoints) {
        List<CreatePinRequest> pinRequests = new ArrayList<>();
        double maxAppender = appender * maxPathPoints;
        double lat = faker.random()
            .nextDouble(dongAnLat, dongAnLat * maxAppender);
        double lng = faker.random()
            .nextDouble(dongAnLng, dongAnLng * maxAppender);

        for (int i = 0; i < size; i++) {
            KakaoPoint point = new KakaoPoint(lat, lng);
            CreatePinRequest createPinRequest = new CreatePinRequest(point, null, null);

            pinRequests.add(createPinRequest);
        }

        return pinRequests;
    }

    List<KakaoPoint> createPath(int size) {
        List<KakaoPoint> path = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double lat = dongAnLat + appender * i;
            double lng = dongAnLng + appender * i;

            KakaoPoint point = new KakaoPoint(lat, lng);

            path.add(point);
        }

        return path;
    }

}
