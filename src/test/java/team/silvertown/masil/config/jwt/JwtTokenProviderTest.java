package team.silvertown.masil.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.User;
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

    @MockBean
    OAuth2User oAuth2User;

    @Test
    @Transactional
    public void 정상적으로_토큰_생성이_가능하다() throws Exception {
        //given
        when(oAuth2User.getName()).thenReturn(VALID_OAUTH_NAME);
        User joinedUser = userService.join(oAuth2User, VALID_PROVIDER);
        Long userId = 1L;

        //when
        String token = jwtTokenProvider.createToken(joinedUser.getId());

        //then
        Long decodedUserId = (Long) jwtTokenProvider.getAuthentication(token)
            .getPrincipal();
        assertThat(userId).isEqualTo(decodedUserId);
        Collection<? extends GrantedAuthority> authorities = jwtTokenProvider.getAuthentication(
                token)
            .getAuthorities();

        List<String> authNames = authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .toList();

        assertThat(authNames).hasSize(1);
        assertThat(authNames.get(0))
            .isEqualTo(ROLE_PREFIX + Authority.RESTRICTED.name());

    }

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
