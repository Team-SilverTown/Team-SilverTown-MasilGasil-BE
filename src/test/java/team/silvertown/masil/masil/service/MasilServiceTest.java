package team.silvertown.masil.masil.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.persistence.EntityManager;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Coordinate;
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
import team.silvertown.masil.masil.dto.RecentMasilResponse;
import team.silvertown.masil.masil.dto.SimpleMasilResponse;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.repository.MasilPinRepository;
import team.silvertown.masil.masil.repository.MasilRepository;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilServiceTest {

    @Autowired
    MasilService masilService;

    @Autowired
    MasilRepository masilRepository;

    @Autowired
    MasilPinRepository masilPinRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

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
        user = userRepository.save(UserTexture.createValidUser());
        addressDepth1 = MasilTexture.createAddressDepth1();
        addressDepth2 = MasilTexture.createAddressDepth2();
        addressDepth3 = MasilTexture.createAddressDepth3();
        path = MapTexture.createPath(10000);
        title = MasilTexture.getRandomSentenceWithMax(29);
        distance = MasilTexture.getRandomPositive();
        totalTime = MasilTexture.getRandomPositive();
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
        long invalidId = MasilTexture.getRandomId();
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
        Masil masil = MasilTexture.createDependentMasil(user, 10000);
        Masil expected = masilRepository.save(masil);
        int pinSize = 10;
        List<MasilPin> masilPins = MasilTexture.createDependentMasilPins(expected, user.getId(),
            pinSize);

        masilPinRepository.saveAll(masilPins);
        entityManager.clear();

        // when
        MasilResponse actual = masilService.getById(user.getId(), expected.getId());

        // then
        assertThat(actual).extracting("id", "depth1", "depth2", "depth3", "depth4", "title",
                "distance", "totalTime")
            .containsExactly(masil.getId(), masil.getDepth1(), masil.getDepth2(), masil.getDepth3(),
                masil.getDepth4(), masil.getTitle(), masil.getDistance(), masil.getTotalTime());
        assertThat(actual.pins()).hasSize(pinSize);
    }

    @Test
    void 사용자가_존재하지_않으면_마실_단일_조회를_실패한다() {
        // given
        long invalidId = MasilTexture.getRandomId();

        // when
        ThrowingCallable getById = () -> masilService.getById(invalidId, invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getById)
            .withMessage(MasilErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 마실이_존재하지_않으면_마실_단일_조회를_실패한다() {
        // given
        long invalidId = MasilTexture.getRandomId();

        // when
        ThrowingCallable getById = () -> masilService.getById(user.getId(), invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getById)
            .withMessage(MasilErrorCode.MASIL_NOT_FOUND.getMessage());
    }

    @Test
    void 로그인한_사용자와_마실_소유자가_다르면_마실_단일_조회를_실패한다() {
        // given
        User differntUser = userRepository.save(UserTexture.createValidUser());
        Masil masil = MasilTexture.createDependentMasil(user, 10000);
        Masil expected = masilRepository.save(masil);
        int pinSize = 10;
        List<MasilPin> masilPins = MasilTexture.createDependentMasilPins(expected, user.getId(),
            pinSize);

        masilPinRepository.saveAll(masilPins);
        entityManager.clear();

        // when
        ThrowingCallable getById = () -> masilService.getById(differntUser.getId(),
            expected.getId());

        // then
        assertThatExceptionOfType(ForbiddenException.class).isThrownBy(getById)
            .withMessage(MasilErrorCode.USER_NOT_AUTHORIZED_FOR_MASIL.getMessage());
    }

    @Test
    void 마실이_없으면_최근_마실_조회_시_빈_값을_명시한다() {
        // given

        // when
        RecentMasilResponse response = masilService.getRecent(user.getId(), null);

        // then
        assertThat(response.masils()).isNotNull();
        assertThat(response.isEmpty()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = {5, 10, 15})
    void 최근_마실_조회를_성공한다(int masilSize) {
        // given
        List<Masil> masils = creataeMasils(user.getId(), masilSize);

        masils.sort((a, b) -> Math.toIntExact(b.getId() - a.getId()));

        int expectedSize = Math.min(masilSize, 10);
        Masil firstExpected = masils.get(0);
        Masil lastExpected = masils.get(expectedSize - 1);

        // when
        RecentMasilResponse response = masilService.getRecent(user.getId(), null);

        // then
        List<SimpleMasilResponse> actual = response.masils();
        SimpleMasilResponse firstActual = actual.get(0);
        SimpleMasilResponse lastActual = actual.get(expectedSize - 1);

        assertThat(actual).hasSize(expectedSize);
        assertThat(firstActual.id()).isEqualTo(firstExpected.getId());
        assertThat(lastActual.id()).isEqualTo(lastExpected.getId());
    }

    @Test
    void 로그인한_사용자가_존재하지_않으면_최근_마실_조회를_실패한다() {
        // given
        long invalidId = MasilTexture.getRandomId();

        // when
        ThrowingCallable getRecent = () -> masilService.getRecent(invalidId, null);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getRecent)
            .withMessage(MasilErrorCode.USER_NOT_FOUND.getMessage());
    }

    List<Masil> creataeMasils(long userId, int size) {
        List<Masil> masils = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Masil masil = MasilTexture.createDependentMasil(user, 100);
            Masil saved = masilRepository.save(masil);
            List<MasilPin> masilPins = MasilTexture.createDependentMasilPins(saved, userId, 5);

            masilPinRepository.saveAll(masilPins);
            masils.add(saved);
        }

        entityManager.clear();

        return masils;
    }

    List<CreatePinRequest> createPinRequests(int size, int maxPathPoints) {
        List<CreatePinRequest> pinRequests = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Coordinate coordinate = MapTexture.createPoint()
                .getCoordinate();
            KakaoPoint point = KakaoPoint.from(coordinate);
            CreatePinRequest createPinRequest = new CreatePinRequest(point, null, null);

            pinRequests.add(createPinRequest);
        }

        return pinRequests;
    }

}
