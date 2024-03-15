package team.silvertown.masil.config.jwt;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.service.UserService;
import org.springframework.security.authentication.InsufficientAuthenticationException;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    public void 권한이_없는_경우_토큰이_생성되지_않는다() {
        //given
        Long userId = 1L;

        //when
        String token = jwtTokenProvider.createToken(userId, Collections.emptyList());
        LoginResponse response = jwtTokenProvider.createToken(userId);

        //then
        assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(token))
            .isInstanceOf(InsufficientAuthenticationException.class)
            .hasMessage("No authority included in JWT");
        assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(response.accessToken()))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage(UserErrorCode.AUTHORITY_NOT_FOUND.getMessage());
    }

}
