package team.silvertown.masil.post.domain;

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
import team.silvertown.masil.post.domain.PostPin.PostPinBuilder;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.PostTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PostPinTest {

    Post post;
    Point point;
    long userId;

    @BeforeEach
    void setUp() {
        post = PostTexture.createValidPost();
        point = MapTexture.createPoint();
        userId = PostTexture.getRandomId();
    }

    @Test
    void 포스트_핀_생성을_할_수_있다() {
        // given
        PostPinBuilder builder = PostPin.builder()
            .post(post)
            .point(point);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @ParameterizedTest
    @NullSource
    void 산책로_포스트가_확인이_안되면_핀_생성을_실패한다(Post nullPost) {
        // given
        PostPinBuilder builder = PostPin.builder()
            .userId(userId)
            .point(point)
            .post(nullPost);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.NULL_MASIL.getMessage());
    }

    @Test
    void 썸네일_Url이_1024자를_넘으면_포스트_핀_생성을_실패한다() {
        // given
        String thumbnailUrl = PostTexture.getRandomFixedSentence(1020) + MasilTexture.createUrl();
        PostPinBuilder builder = PostPin.builder()
            .post(post)
            .userId(userId)
            .point(point)
            .thumbnailUrl(thumbnailUrl);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.THUMBNAIL_URL_TOO_LONG.getMessage());
    }

    @Test
    void 산책로_포스트_기록자와_핀의_사용자가_다르면_포스트_핀_생성을_실패한다() {
        // given
        PostPinBuilder builder = PostPin.builder()
            .post(post)
            .userId(userId)
            .point(point);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(ForbiddenException.class).isThrownBy(create)
            .withMessage(PostErrorCode.PIN_OWNER_NOT_MATCHING.getMessage());
    }

}
