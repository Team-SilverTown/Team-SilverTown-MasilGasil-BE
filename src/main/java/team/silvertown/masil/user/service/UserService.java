package team.silvertown.masil.user.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.LoginResponseDto;
import team.silvertown.masil.user.exception.OAuthValidator;
import team.silvertown.masil.user.exception.UserValidator;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    @Transactional
    public User join(OAuth2User oAuth2User, String provider) {
        OAuthValidator.validateSocialUser(oAuth2User);
        Provider authenticatedProvider = OAuthValidator.validateProvider(provider);

        String providerId = oAuth2User.getName();

        return userRepository.findByProviderAndSocialId(authenticatedProvider, providerId)
            .map(user -> {
                log.info("already login user {}", user.getId());
                return user;
            })
            .orElseGet(() -> {
                User newUser = User.builder()
                    .socialId(providerId)
                    .provider(authenticatedProvider)
                    .build();
                User savedUser = userRepository.save(newUser);
                UserAuthority newAuthority = UserAuthority.builder()
                    .authority(Authority.RESTRICTED)
                    .user(savedUser)
                    .build();
                userAuthorityRepository.save(newAuthority);
                return savedUser;
            });
    }

    public LoginResponseDto login(String jwtToken, User user) {
        List<UserAuthority> userAuthorities = userAuthorityRepository.findByUser(user);
        UserValidator.validateAuthority(userAuthorities);

        List<String> authorities = userAuthorities.stream()
            .map(authority -> authority.getAuthority()
                .getAuthority())
            .toList();

        return new LoginResponseDto(jwtToken, authorities);
    }

}
