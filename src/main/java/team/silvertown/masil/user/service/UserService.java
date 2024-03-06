package team.silvertown.masil.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtTokenProvider tokenProvider;

    public LoginResponse login(String kakaoToken) {
        OAuthResponse oAuthResponse = kakaoOAuthService.oauthResponse(kakaoToken);
        Provider provider = Provider.get(oAuthResponse.provider());
        boolean isJoined = userRepository.existsByProviderAndSocialId(provider,
            oAuthResponse.providerId());
        if (isJoined) {
            return joinedUserResponse(provider, oAuthResponse);
        }

        User justSavedUser = createAndSave(provider, oAuthResponse.providerId());
        assignDefaultAuthority(justSavedUser);
        String newUserToken = tokenProvider.createToken(justSavedUser.getId());

        return new LoginResponse(newUserToken);
    }

    private LoginResponse joinedUserResponse(Provider provider, OAuthResponse oAuthResponse) {
        User alreadyJoinedUser = createAndSave(provider, oAuthResponse.providerId());
        String token = tokenProvider.createToken(alreadyJoinedUser.getId());

        return new LoginResponse(token);
    }

    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateResourceException(UserErrorCode.DUPLICATED_NICKNAME);
        }
    }

    private User createAndSave(Provider authenticatedProvider, String providerId) {
        User newUser = create(authenticatedProvider, providerId);
        User savedUser = userRepository.save(newUser);
        assignDefaultAuthority(savedUser);
        return savedUser;
    }

    private User create(Provider authenticatedProvider, String providerId) {
        return User.builder()
            .socialId(providerId)
            .provider(authenticatedProvider)
            .build();
    }

    private void assignDefaultAuthority(User user) {
        UserAuthority newAuthority = UserAuthority.builder()
            .authority(Authority.RESTRICTED)
            .user(user)
            .build();
        userAuthorityRepository.save(newAuthority);
    }

}
