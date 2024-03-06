package team.silvertown.masil.mate.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.OffsetDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.mate.domain.Mate.MateBuilder;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.MateTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MateTest {

    User user;
    Post post;
    String addressDepth1;
    String addressDepth2;
    String addressDepth3;
    String title;
    String content;
    Point gatheringPlacePoint;
    String gatheringPlaceDetail;
    OffsetDateTime gatheringAt;
    Integer capacity;

    @BeforeEach
    void setUp() {
        user = UserTexture.createValidUser();
        // TODO: post texture
        post = Post.builder()
            .id(1L)
            .user(user)
            .build();
        addressDepth1 = MasilTexture.createAddressDepth1();
        addressDepth2 = MasilTexture.createAddressDepth2();
        addressDepth3 = MasilTexture.createAddressDepth3();
        title = MateTexture.getRandomSentenceWithMax(30);
        content = MateTexture.getRandomSentenceWithMax(1000);
        gatheringPlacePoint = MapTexture.createPoint();
        gatheringPlaceDetail = MateTexture.getRandomSentenceWithMax(50);
        gatheringAt = MateTexture.getFutureDateTime();
        capacity = MateTexture.getRandomInt(1, 10);
    }

    @Test
    void 메이트_생성을_성공한다() {
        // given
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(capacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @Test
    void 사용자가_확인되지_않으면_메이트_생성을_실패한다() {
        // given
        MateBuilder builder = Mate.builder()
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(capacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.NULL_AUTHOR.getMessage());
    }

    @Test
    void 산책로_포스트가_확인되지_않으면_메이트_생성을_실패한다() {
        // given
        MateBuilder builder = Mate.builder()
            .author(user)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(capacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.NULL_POST.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 메이트_모집_제목이_없으면_메이트_생성을_실패한다(String blankTitle) {
        // given
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(blankTitle)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(capacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.BLANK_TITLE.getMessage());
    }

    @Test
    void 메이트_모집_제목이_50자_이상이면_메이트_생성을_실패한다() {
        // given
        String invalidTitle = MateTexture.getRandomFixedSentence(51);
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(invalidTitle)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(capacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.TITLE_TOO_LONG.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 메이트_모집_내용이_없으면_메이트_생성을_실패한다(String blankContent) {
        // given
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(blankContent)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(capacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.BLANK_CONTENT.getMessage());
    }

    @Test
    void 메이트_모집_정원이_없으면_메이트_생성을_실패한다() {
        // given
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.NULL_CAPACITY.getMessage());
    }

    @Test
    void 메이트_모집_정원이_양수가_아니면_메이트_생성을_실패한다() {
        // given
        int negativeCapacity = MateTexture.getRandomNegative();
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(negativeCapacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.NON_POSITIVE_CAPACITY.getMessage());
    }

    @Test
    void 메이트_모집_정원이_10명을_초과하면_메이트_생성을_실패한다() {
        // given
        int excessiveCapacity = MateTexture.getRandomInt(11, Integer.MAX_VALUE);
        MateBuilder builder = Mate.builder()
            .author(user)
            .post(post)
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .depth4("")
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatheringAt(gatheringAt)
            .capacity(excessiveCapacity);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.CAPACITY_TOO_LARGE.getMessage());
    }

}
