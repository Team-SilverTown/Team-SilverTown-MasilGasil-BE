package team.silvertown.masil.user.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.user.domain.User.UserBuilder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserTest {

    @Test
    void 유저_생성을_할_수_있다() {
        // given
        UserBuilder builder = User.builder();

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

}
