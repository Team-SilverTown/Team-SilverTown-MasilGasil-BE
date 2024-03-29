package team.silvertown.masil.auth.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.auth.domain.RefreshToken;
import team.silvertown.masil.auth.dto.LoginResponse;
import team.silvertown.masil.auth.exception.AuthErrorCode;
import team.silvertown.masil.auth.jwt.JwtTokenProvider;
import team.silvertown.masil.auth.repository.RefreshTokenRepository;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;
import team.silvertown.masil.user.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String ACCESS_TOKEN_PREFIX = "Bearer ";

    private final UserRepository userRepository;
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final KakaoOAuthService kakaoOAuthService;

    @Transactional
    public LoginResponse login(String kakaoToken) {
        OAuthResponse oAuthResponse = kakaoOAuthService.getUserInfo(kakaoToken);
        Provider provider = Provider.get(oAuthResponse.provider());
        Optional<User> user = userRepository.findByProviderAndSocialId(provider,
            oAuthResponse.providerId());

        if (user.isPresent()) {
            LoginResponse loginResponse = returnLoginResponse(user.get());
            saveRefreshToken(loginResponse, user.get());
            return loginResponse;
        }

        User newUser = userService.createAndSave(provider, oAuthResponse.providerId());
        LoginResponse loginResponse = returnLoginResponse(newUser);
        saveRefreshToken(loginResponse, newUser);

        return loginResponse;
    }

    public String refresh(String refreshToken, String accessToken) {
        validateToken(accessToken, refreshToken);
        User user = getUserFromRefreshToken(refreshToken);
        List<Authority> userAuthorities = userService.getUserAuthorities(user);

        return ACCESS_TOKEN_PREFIX + tokenProvider.createAccessToken(user.getId(), userAuthorities);
    }

    private void validateToken(String accessToken, String refreshToken) {
        Validator.notNull(accessToken, AuthErrorCode.INVALID_JWT_TOKEN);
        Validator.notNull(refreshToken, AuthErrorCode.INVALID_JWT_TOKEN);

        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidAuthenticationException(AuthErrorCode.INVALID_JWT_TOKEN);
        }
    }

    private void saveRefreshToken(LoginResponse loginResponse, User justSavedUser) {
        refreshTokenRepository.save(
            new RefreshToken(loginResponse.refreshToken(), justSavedUser.getId()));
    }

    private User getUserFromRefreshToken(String refreshToken) {
        RefreshToken tokenInRedis = refreshTokenRepository.findById(refreshToken)
            .orElseThrow(() -> new BadRequestException(AuthErrorCode.REFRESH_TOKEN_NOT_FOUND));
        Long userId = tokenInRedis.getMemberId();

        return userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }

    private LoginResponse returnLoginResponse(User joinedUser) {
        List<Authority> authorities = userService.getUserAuthorities(joinedUser);
        String accessToken = tokenProvider.createAccessToken(joinedUser.getId(), authorities);
        String refreshToken = tokenProvider.createRefreshToken(joinedUser.getId());

        return new LoginResponse(accessToken, refreshToken);
    }

}
