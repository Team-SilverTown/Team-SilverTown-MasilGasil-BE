package team.silvertown.masil.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.user.domain.UserAuthority.UserAuthorityBuilder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserAuthorityTest {

    @Test
    void 유저_권한을_생성할_수_있다() {
        // given
        UserAuthorityBuilder builder = UserAuthority.builder();

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @Test
    void 유저_권한을_조회한다() {
        // given
        UserAuthority userAuthority = UserAuthority.builder()
            .authority(Authority.RESTRICTED)
            .build();

        // when
        String role = userAuthority.getRole();

        // then
        assertThat(role).isEqualTo("ROLE_RESTRICTED");
    }

}
