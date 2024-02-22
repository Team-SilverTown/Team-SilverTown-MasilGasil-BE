package team.silvertown.masil.config.oauth2;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.dto.LoginResponseDto;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.user.service.UserService;

// 인증이 완료됐을 때 호출되는 핸들러
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final ObjectMapper objectMapper;

    // 인증이 완료되고 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request, HttpServletResponse response,
        Authentication oauth2Authentication
    ) throws ServletException, IOException {
        Object principal = oauth2Authentication.getPrincipal();
        log.info("OAuth2 authentication success: {}", principal);

        if (oauth2Authentication instanceof OAuth2AuthenticationToken oauth2Token) {
            OAuth2User oAuth2User = oauth2Token.getPrincipal();
            String provider = oauth2Token.getAuthorizedClientRegistrationId();
            User user = processUserOAuth2UserJoin(oAuth2User, provider);
            String jwtToken = tokenProvider.createToken(user.getId());

            LoginResponseDto loginResponseDto = userService.login(jwtToken, user);
            String loginResponse = objectMapper.writeValueAsString(loginResponseDto);

            setResponseBody(response, loginResponse);
        }
    }

    private static void setResponseBody(HttpServletResponse response, String loginResponse)
        throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter()
            .write(loginResponse);
        response.getWriter()
            .flush();
        response.getWriter()
            .close();
    }

    private User processUserOAuth2UserJoin(OAuth2User oAuth2User, String provider) {
        return userService.join(oAuth2User, provider);
    }

}
