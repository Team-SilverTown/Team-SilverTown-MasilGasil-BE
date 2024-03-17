package team.silvertown.masil.auth.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.auth.domain.RefreshToken;
import team.silvertown.masil.auth.repository.RefreshTokenRepository;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.auth.dto.LoginResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;
import team.silvertown.masil.user.service.KakaoOAuthService;
import team.silvertown.masil.user.service.UserService;

@Service
@RequiredArgsConstructor
public class AuthService {

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
            LoginResponse loginResponse = joinedUserResponse(user.get());
            saveRefreshToken(loginResponse, user.get());
            return loginResponse;
        }

        User justSavedUser = userService.createAndSave(provider, oAuthResponse.providerId());
        List<Authority> authorities = userService.getUserAuthorities(justSavedUser);
        LoginResponse loginResponse = tokenProvider.createToken(justSavedUser.getId(), authorities);
        saveRefreshToken(loginResponse, justSavedUser);

        return loginResponse;
    }

    public String refresh(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new InvalidAuthenticationException(UserErrorCode.INVALID_JWT_TOKEN);
        }
        User user = getUserFromRefreshToken(refreshToken);
        List<Authority> userAuthorities = userService.getUserAuthorities(user);

        return tokenProvider.createAccessToken(user.getId(), userAuthorities);
    }

    private void saveRefreshToken(LoginResponse loginResponse, User justSavedUser) {
        refreshTokenRepository.save(
            new RefreshToken(loginResponse.refreshToken(), justSavedUser.getId()));
    }

    private User getUserFromRefreshToken(String refreshToken) {
        RefreshToken tokenInRedis = refreshTokenRepository.findById(refreshToken)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.REFRESH_TOKEN_NOT_FOUND));
        Long userId = tokenInRedis.getMemberId();
        return userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }

    private LoginResponse joinedUserResponse(User joinedUser) {
        List<Authority> authorities = userService.getUserAuthorities(joinedUser);

        return tokenProvider.createToken(joinedUser.getId(), authorities);
    }

}
