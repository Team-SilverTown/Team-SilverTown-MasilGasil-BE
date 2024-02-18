package team.silvertown.masil.post.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.post.domain.PostPin.PostPinBuilder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PostPinTest {

    @Test
    void 포스트_핀_생성을_할_수_있다() {
        // given
        PostPinBuilder builder = PostPin.builder();

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

}
