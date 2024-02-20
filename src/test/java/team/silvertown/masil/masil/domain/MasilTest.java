package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.sql.Date;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.masil.domain.Masil.MasilBuilder;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilTest {

    static final Faker faker = new Faker(Locale.KOREA);

    User user;
    String addressDepth1;
    String addressDepth2;
    String addressDepth3;
    LineString path;
    String title;
    int totalTime;

    @BeforeEach
    void setUp() {
        user = createUser();
        addressDepth1 = faker.address()
            .state();
        addressDepth2 = faker.address()
            .city();
        addressDepth3 = faker.address()
            .streetName();
        path = createLineString(10);
        title = faker.book()
            .title();
        totalTime = faker.number()
            .numberBetween(10, 70);
    }

    @Test
    void 마실_생성을_할_수_있다() {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @Test
    void 유저가_확인되지_않으면_마실_생성을_실패한다() {
        // given
        MasilBuilder builder = Masil.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.NULL_USER.getMessage());
    }

    @Test
    void 썸네일_Url이_1024자를_넘으면_마실_생성을_실패한다() {
        // given
        String thumbnailUrl = faker.lorem()
            .sentence(1020) + faker.internet()
            .domainName();
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .thumbnailUrl(thumbnailUrl)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.THUMBNAIL_URL_TOO_LONG.getMessage());
    }

    @Test
    void 마실_거리가_입력되지_않으면_마실_생성을_실패한다() {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .totalTime(totalTime)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.NULL_DISTANCE.getMessage());
    }

    @Test
    void 마실_총_시간이_입력되지_않으면_마실_생성을_실패한다() {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.NULL_TOTAL_TIME.getMessage());
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

    LineString createLineString(int size) {
        List<KakaoPoint> path = new ArrayList<>();
        double dongAnLat = 37.4004;
        double dongAnLng = 126.9555;
        double appender = 0.002;

        for (int i = 0; i < size; i++) {
            dongAnLat += appender * i;
            dongAnLng += appender * i;

            KakaoPoint point = new KakaoPoint(dongAnLat, dongAnLng);

            path.add(point);
        }

        return KakaoPointMapper.mapToLineString(path);
    }

}
