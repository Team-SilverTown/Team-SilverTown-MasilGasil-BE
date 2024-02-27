package team.silvertown.masil.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.LoginResponseDto;
import team.silvertown.masil.security.exception.OAuthValidator;
import team.silvertown.masil.user.exception.UserValidator;
import team.silvertown.masil.user.repository.UserAuthorityRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    public LoginResponseDto login(String jwtToken, User user) {
        List<UserAuthority> userAuthorities = userAuthorityRepository.findByUser(user);
        UserValidator.validateAuthority(userAuthorities);

        return new LoginResponseDto(jwtToken);
    }

    @Transactional
    public User join(OAuth2User oAuth2User, String provider) {
        OAuthValidator.validateSocialUser(oAuth2User);
        Provider authenticatedProvider = OAuthValidator.validateProvider(provider);

        String providerId = oAuth2User.getName();

        return userRepository.findByProviderAndSocialId(authenticatedProvider, providerId)
            .orElseGet(() -> createAndSave(authenticatedProvider, providerId));
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

    public void doubleCheck(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateResourceException(UserErrorCode.DUPLICATED_NICKNAME);
        }
    }

}
