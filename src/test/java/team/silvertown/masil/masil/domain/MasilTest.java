package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.OffsetDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.masil.domain.Masil.MasilBuilder;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilTest {

    User user;
    String addressDepth1;
    String addressDepth2;
    String addressDepth3;
    String addressDepth4;
    LineString path;
    String title;
    int totalTime;
    int calories;

    @BeforeEach
    void setUp() {
        user = UserTexture.createValidUser();
        addressDepth1 = MasilTexture.createAddressDepth1();
        addressDepth2 = MasilTexture.createAddressDepth2();
        addressDepth3 = MasilTexture.createAddressDepth3();
        addressDepth4 = "";
        path = MapTexture.createLineString(10000);
        title = MasilTexture.getRandomSentenceWithMax(29);
        totalTime = MasilTexture.getRandomPositive();
        calories = MasilTexture.getRandomPositive();
    }

    @Test
    void 마실_생성을_할_수_있다() {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .calories(calories)
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
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .calories(calories)
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
        String thumbnailUrl = MasilTexture.getRandomFixedSentence(1020) + MasilTexture.createUrl();
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .calories(calories)
            .thumbnailUrl(thumbnailUrl)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.THUMBNAIL_URL_TOO_LONG.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = -1)
    void 마실_거리가_잘못됐으면_마실_생성을_실패한다(Integer invalidDistance) {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .totalTime(totalTime)
            .distance(invalidDistance)
            .calories(calories)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.INVALID_DISTANCE.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = -1)
    void 마실_총_시간_형식이_잘못됐으면_마실_생성을_실패한다(Integer invalidTotalTime) {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .totalTime(invalidTotalTime)
            .distance((int) path.getLength())
            .calories(calories)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.INVALID_TOTAL_TIME.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = -1)
    void 마실_소모_칼로리가_양수가_아니면_마실_생성을_실패한다(Integer invalidCalories) {
        // given
        MasilBuilder builder = Masil.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .totalTime(totalTime)
            .distance((int) path.getLength())
            .calories(invalidCalories)
            .startedAt(OffsetDateTime.now());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.NON_POSITIVE_CALORIES.getMessage());
    }

}
