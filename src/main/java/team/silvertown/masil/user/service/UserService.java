package team.silvertown.masil.user.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.security.exception.OAuthValidator;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAgreement;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.LoginResponseDto;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.exception.UserValidator;
import team.silvertown.masil.user.repository.UserAgreementRepository;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAgreementRepository agreementRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    private static UserAuthority generateUserAuthority(User user, Authority authority) {
        return UserAuthority.builder()
            .authority(authority)
            .user(user)
            .build();
    }

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

    @Transactional
    public void onboard(long userId, OnboardRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));

        checkNickname(request.nickname());
        user.update(request);
        UserAgreement userAgreement = getUserAgreement(request, user);
        agreementRepository.save(userAgreement);

        List<UserAuthority> authorities = userAuthorityRepository.findByUser(user)
            .stream()
            .toList();
        updatingAuthority(authorities, user);
    }

    public void checkNickname(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateResourceException(UserErrorCode.DUPLICATED_NICKNAME);
        }
    }

    private void updatingAuthority(List<UserAuthority> authorities, User user) {
        if (authorities.size() == 1 && authorities.get(0)
            .getAuthority() == Authority.RESTRICTED) {
            UserAuthority normalAuthority = generateUserAuthority(user, Authority.NORMAL);
            userAuthorityRepository.save(normalAuthority);
        }
    }

    private UserAgreement getUserAgreement(OnboardRequest request, User user) {
        LocalDateTime marketingConsentedAt = request.isAllowingMarketing() ? LocalDateTime.now()
            : null;
        UserValidator.validateIsPersonalInfoConsented(request.isPersonalInfoConsented(),
            UserErrorCode.INVALID_PERSONAL_INFO_CONSENTED);
        UserValidator.validateIsLocationInfoConsented(request.isLocationInfoConsented(),
            UserErrorCode.INVALID_LOCATION_INFO_CONSENTED);
        UserValidator.validateIsUnderAgeConsentConfirmed(request.isUnderAgeConsentConfirmed(),
            UserErrorCode.INVALID_UNDER_AGE_CONSENTED);

        UserAgreement userAgreement = UserAgreement.builder()
            .user(user)
            .isAllowingMarketing(request.isAllowingMarketing())
            .isLocationInfoConsented(request.isLocationInfoConsented())
            .isPersonalInfoConsented(request.isPersonalInfoConsented())
            .isUnderAgeConsentConfirmed(request.isUnderAgeConsentConfirmed())
            .marketingConsentedAt(marketingConsentedAt)
            .build();

        return userAgreement;
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
        UserAuthority newAuthority = generateUserAuthority(user, Authority.RESTRICTED);
        userAuthorityRepository.save(newAuthority);
    }

}
