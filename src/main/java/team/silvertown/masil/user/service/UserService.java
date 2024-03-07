package team.silvertown.masil.user.service;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.security.exception.OAuthValidator;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAgreement;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.dto.MeInfoResponse;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserAgreementRepository;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;
import team.silvertown.masil.user.validator.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAgreementRepository agreementRepository;
    private final UserAuthorityRepository userAuthorityRepository;

    public LoginResponse login(String jwtToken, User user) {
        List<UserAuthority> userAuthorities = userAuthorityRepository.findByUser(user);
        UserValidator.validateAuthority(userAuthorities);

        return new LoginResponse(jwtToken);
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
        Validator.throwIf(agreementRepository.existsByUser(user),
            () -> new DuplicateResourceException(UserErrorCode.ALREADY_ONBOARDED));
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
        boolean hasNormalAuthority = authorities.stream()
            .map(UserAuthority::getAuthority)
            .anyMatch(a -> a.equals(Authority.NORMAL));

        if (!hasNormalAuthority) {
            UserAuthority normalAuthority = generateUserAuthority(user, Authority.NORMAL);
            userAuthorityRepository.save(normalAuthority);
        }
    }

    private UserAgreement getUserAgreement(OnboardRequest request, User user) {
        OffsetDateTime marketingConsentedAt = request.isAllowingMarketing() ? OffsetDateTime.now()
            : null;

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

    public MeInfoResponse getMe(Long memberId) {
        User user = userRepository.findById(memberId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));

        return MeInfoResponse.from(user);
    }

    private static UserAuthority generateUserAuthority(User user, Authority authority) {
        return UserAuthority.builder()
            .authority(authority)
            .user(user)
            .build();
    }

}
