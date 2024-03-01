package team.silvertown.masil.post.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.post.domain.Post.PostBuilder;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.PostTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PostTest {

    User user;
    String addressDepth1;
    String addressDepth2;
    String addressDepth3;
    String addressDepth4;
    LineString path;
    String title;
    int totalTime;

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
    }

    @Test
    void 산책로_포스트_생성을_할_수_있다() {
        // given
        PostBuilder builder = Post.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @Test
    void 유저가_확인되지_않으면_산책로_포스트_생성을_실패한다() {
        // given
        PostBuilder builder = Post.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.NULL_USER.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 제목이_없으면_산책로_포스트_생성을_실패한다(String invalidTitle) {
        // given
        PostBuilder builder = Post.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(invalidTitle)
            .distance((int) path.getLength())
            .totalTime(totalTime);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.BLANK_TITLE.getMessage());
    }

    @Test
    void 제목이_30자를_넘으면_산책로_포스트_생성을_실패한다() {
        // given
        String longTitle = PostTexture.getRandomFixedSentence(31);
        PostBuilder builder = Post.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(longTitle)
            .distance((int) path.getLength())
            .totalTime(totalTime);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.TITLE_TOO_LONG.getMessage());
    }

    @Test
    void 썸네일_Url이_1024자를_넘으면_산책로_포스트_생성을_실패한다() {
        // given
        String thumbnailUrl = MasilTexture.getRandomFixedSentence(1020) + MasilTexture.createUrl();
        PostBuilder builder = Post.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .path(path)
            .title(title)
            .distance((int) path.getLength())
            .totalTime(totalTime)
            .thumbnailUrl(thumbnailUrl);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.THUMBNAIL_URL_TOO_LONG.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = -1)
    void 산책_거리가_잘못됐으면_산책로_포스트_생성을_실패한다(Integer invalidDistance) {
        // given
        PostBuilder builder = Post.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .title(title)
            .totalTime(totalTime)
            .distance(invalidDistance);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.INVALID_DISTANCE.getMessage());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(ints = -1)
    void 산책_총_시간_형식이_잘못됐으면_산책로_포스트_생성을_실패한다(Integer invalidTotalTime) {
        // given
        PostBuilder builder = Post.builder()
            .user(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4(addressDepth4)
            .path(path)
            .title(title)
            .totalTime(invalidTotalTime)
            .distance((int) path.getLength());

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(PostErrorCode.INVALID_TOTAL_TIME.getMessage());
    }

}
