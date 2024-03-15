package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.ForbiddenException;
import team.silvertown.masil.masil.domain.MasilPin.MasilPinBuilder;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilPinTest {

    Masil masil;
    Point point;
    long userId;

    @BeforeEach
    void setUp() {
        masil = MasilTexture.createValidMasil();
        point = MapTexture.createPoint();
        userId = MasilTexture.getRandomId();
    }

    @Test
    void 마실_핀_생성을_할_수_있다() {
        // given
        MasilPinBuilder builder = MasilPin.builder()
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
        String thumbnailUrl = MasilTexture.getRandomFixedSentence(1020) + MasilTexture.createUrl();
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

    @Test
    void 마실_기록자와_핀의_사용자가_다르면_마실_핀_생성을_실패한다() {
        // given
        MasilPinBuilder builder = MasilPin.builder()
            .masil(masil)
            .userId(userId)
            .point(point);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(ForbiddenException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.PIN_OWNER_NOT_MATCHING.getMessage());
    }

}
