package team.silvertown.masil.config.jwt;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.service.UserService;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class JwtTokenProviderTest {

    private static final String VALID_PROVIDER = "kakao";
    private static final String VALID_OAUTH_NAME = "valid oauth name";
    private static final String ROLE_PREFIX = "ROLE_";

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Autowired
    UserService userService;

    @Test
    public void 권한이_없는_경우_토큰이_생성되지_않는다() throws Exception {
        //given
        Long userId = 1L;

        //when
        String token = jwtTokenProvider.createToken(userId);

        //then
        assertThatThrownBy(() -> jwtTokenProvider.getAuthentication(token))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage(UserErrorCode.AUTHORITY_NOT_FOUND.getMessage());
    }

}
