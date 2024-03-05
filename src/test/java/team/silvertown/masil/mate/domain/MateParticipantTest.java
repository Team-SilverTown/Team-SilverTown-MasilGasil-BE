package team.silvertown.masil.mate.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.mate.domain.MateParticipant.MateParticipantBuilder;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.texture.MateTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MateParticipantTest {

    User user;
    Mate mate;

    @BeforeEach
    void setUp() {
        user = UserTexture.createValidUser();
        User author = UserTexture.createValidUser();
        // TODO: post texture
        Post post = Post.builder()
            .user(user)
            .id(1L)
            .build();
        mate = MateTexture.createDependentMate(author, post);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "ACCEPTED"})
    void 메이트_참여자를_생성할_수_있다(String status) {
        // given
        MateParticipantBuilder builder = MateParticipant.builder()
            .user(user)
            .mate(mate)
            .status(status);

        // when
        MateParticipant actual = builder.build();

        // then
        ParticipantStatus expected = "ACCEPTED".equals(status) ? ParticipantStatus.ACCEPTED
            : ParticipantStatus.REQUESTED;

        assertThat(actual.getStatus()).isEqualTo(expected);
    }

    @Test
    void 대상_참여자가_없으면_메이트_참여자_생성을_실패한다() {
        // given
        MateParticipantBuilder builder = MateParticipant.builder()
            .mate(mate);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.NULL_USER.getMessage());
    }

    @Test
    void 대상_메이트가_없으면_메이트_참여자_생성을_실패한다() {
        // given
        MateParticipantBuilder builder = MateParticipant.builder()
            .user(user);

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.NULL_MATE.getMessage());
    }

}
