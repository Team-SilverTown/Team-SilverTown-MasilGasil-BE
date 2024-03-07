package team.silvertown.masil.user.service;

import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Nested;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.user.domain.User;

@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class UserServiceTest {

    private static final Faker faker = new Faker();
    private static final String VALID_PROVIDER = "kakao";
    private static final String INVALID_PROVIDER = faker.animal()
        .name();
    private static final String VALID_KAKAO_TOKEN = faker.animal()
        .name();

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAuthorityRepository authorityRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @MockBean
    KakaoOAuthService kakaoOAuthService;


    @Nested
    class 닉네임_중복_조회_로직_테스트 {

        @Test
        public void 중복닉네임_조회시_존재하지_않는_닉네임을_조회할_경우_정상적으로_통과한다() throws Exception {
            //given
            String nickname = faker.name()
                .fullName();

            //when, then
            assertDoesNotThrow(() -> userService.checkNickname(nickname));
        }

        @Test
        public void 중복닉네임_조회시_이미_존재하는_닉네임을_조회할_경우_예외가_발생한다() throws Exception {
            //given
            String nickname = faker.name()
                .fullName();
            User user = User.builder()
                .nickname(nickname)
                .build();
            userRepository.save(user);

            //when, then
            assertThatThrownBy(() -> userService.checkNickname(nickname))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(UserErrorCode.DUPLICATED_NICKNAME.getMessage());
        }

    }

    @Nested
    @Transactional
    class 유저_소셜_로그인_회원가입_로직_테스트 {

        @Test
        public void 정상적으로_처음_회원가입하는_유저는_유저_정보를_저장한_후_정상적인_토큰을_반환한다() throws Exception {
            //given
            OAuthResponse mockOAuthResponse = new OAuthResponse(VALID_PROVIDER, "123456");
            given(kakaoOAuthService.getUserInfo(anyString())).willReturn(mockOAuthResponse);

            //when
            LoginResponse tokenResponse = userService.login(VALID_KAKAO_TOKEN);

            //then
            assertThat(tokenProvider.validateToken(tokenResponse.token())).isTrue();
        }

        @Test
        public void 토큰인증과정에서_문제가_생기면_예외가_발생한다() throws Exception {
            //given
            given(kakaoOAuthService.getUserInfo(anyString())).willThrow(RuntimeException.class);

            //when, then
            assertThatThrownBy(() ->userService.login(VALID_KAKAO_TOKEN))
                .isInstanceOf(InvalidAuthenticationException.class)
                .hasMessage(UserErrorCode.INVALID_OAUTH2_TOKEN.getMessage());
        }

        @Test
        public void 비정상적인_provider가_제공된_경우_예외가_발생한다() throws Exception {
            //given
            OAuthResponse mockOAuthResponse = new OAuthResponse(INVALID_PROVIDER, "123456");
            given(kakaoOAuthService.getUserInfo(anyString())).willReturn(mockOAuthResponse);

            //when, then
            assertThatThrownBy(() ->userService.login(VALID_KAKAO_TOKEN))
                .isInstanceOf(InvalidAuthenticationException.class)
                .hasMessage(UserErrorCode.INVALID_PROVIDER.getMessage());
        }

    }

}
