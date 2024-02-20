package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.masil.domain.MasilPin.MasilPinBuilder;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilPinTest {

    static final Faker faker = new Faker(Locale.KOREA);

    Masil masil;
    Point point;
    long userId;

    @BeforeEach
    void setUp() {
        masil = createMasil();
        point = createPoint();
        userId = faker.random()
            .nextLong(Long.MAX_VALUE);
    }

    @Test
    void 마실_핀_생성을_할_수_있다() {
        // given
        MasilPinBuilder builder = MasilPin.builder()
            .userId(userId)
            .masil(masil)
            .point(point);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @ParameterizedTest
    @NullSource
    void 마실이_확인이_안되면_마실_핀_생성을_실패한다(Masil nullMasil) {
        // given
        MasilPinBuilder builder = MasilPin.builder()
            .userId(userId)
            .point(point)
            .masil(nullMasil);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.NULL_MASIL.getMessage());
    }

    @Test
    void 썸네일_Url이_1024자를_넘으면_마실_핀_생성을_실패한다() {
        // given
        String thumbnailUrl = faker.lorem()
            .sentence(1020) + faker.internet()
            .domainName();
        MasilPinBuilder builder = MasilPin.builder()
            .masil(masil)
            .userId(userId)
            .point(point)
            .thumbnailUrl(thumbnailUrl);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.THUMBNAIL_URL_TOO_LONG.getMessage());
    }

    Masil createMasil() {
        User user = createUser();
        String addressDepth1 = faker.address()
            .state();
        String addressDepth2 = faker.address()
            .city();
        String addressDepth3 = faker.address()
            .streetName();
        LineString path = createLineString(10);
        String title = faker.book()
            .title();
        int totalTime = faker.number()
            .numberBetween(10, 70);

        return Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .build();
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

    Point createPoint() {
        double dongAnLat = 37.4004;
        double dongAnLng = 126.9555;
        KakaoPoint point = new KakaoPoint(dongAnLat, dongAnLng);

        return KakaoPointMapper.mapToPoint(point);
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
