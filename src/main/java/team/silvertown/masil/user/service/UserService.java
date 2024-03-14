package team.silvertown.masil.user.service;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.image.service.ImageService;
import team.silvertown.masil.image.validator.ImageFileServiceValidator;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAgreement;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.dto.MeInfoResponse;
import team.silvertown.masil.user.dto.NicknameCheckResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.dto.UpdateRequest;
import team.silvertown.masil.user.dto.UpdateResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
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
    private final KakaoOAuthService kakaoOAuthService;
    private final JwtTokenProvider tokenProvider;
    private final ImageService imageService;

    @Transactional
    public LoginResponse login(String kakaoToken) {
        OAuthResponse oAuthResponse;
        try {
            oAuthResponse = kakaoOAuthService.getUserInfo(kakaoToken);
        } catch (Exception e) {
            log.error("social login error occured, the reason is: {}", e.getMessage(), e);
            throw new InvalidAuthenticationException(UserErrorCode.INVALID_OAUTH2_TOKEN);
        }

        Provider provider = Provider.get(oAuthResponse.provider());
        Optional<User> user = userRepository.findByProviderAndSocialId(provider,
            oAuthResponse.providerId());
        if (user.isPresent()) {
            return joinedUserResponse(user.get());
        }

        User justSavedUser = createAndSave(provider, oAuthResponse.providerId());
        assignDefaultAuthority(justSavedUser);
        String newUserToken = tokenProvider.createToken(justSavedUser.getId());

        return new LoginResponse(newUserToken);
    }

    private LoginResponse joinedUserResponse(User joinedUser) {
        String token = tokenProvider.createToken(joinedUser.getId());

        return new LoginResponse(token);
    }

    @Transactional
    public void onboard(OnboardRequest request, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));

        checkNickname(request.nickname());
        update(user, request);
        UserAgreement userAgreement = getUserAgreement(request, user);
        Validator.throwIf(agreementRepository.existsByUser(user),
            () -> new DuplicateResourceException(UserErrorCode.ALREADY_ONBOARDED));
        agreementRepository.save(userAgreement);

        List<UserAuthority> authorities = userAuthorityRepository.findByUser(user)
            .stream()
            .toList();
        updatingAuthority(authorities, user);
    }

    @Transactional
    public void changePublic(Long memberId) {
        User user = userRepository.findById(memberId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));
        user.toggleIsPublic();
    }

    @Transactional
    public UpdateResponse updateInfo(Long memberId, UpdateRequest updateRequest) {
        User user = userRepository.findById(memberId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));
        update(user, updateRequest);

        return UpdateResponse.from(user);
    }

    public NicknameCheckResponse checkNickname(String nickname) {
        boolean isDuplicated = userRepository.existsByNickname(nickname);
        return NicknameCheckResponse.builder()
            .nickname(nickname)
            .isDuplicated(isDuplicated)
            .build();
    }

    public MeInfoResponse getMe(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));

        return MeInfoResponse.from(user);
    }

    @Transactional
    public void updateProfile(MultipartFile profileImg, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));

        String profileUrl = getProfileUrl(profileImg);
        user.updateProfile(profileUrl);
    }

    private String getProfileUrl(MultipartFile profileImg) {
        if (Objects.nonNull(profileImg)) {
            String profileUrl;
            ImageFileServiceValidator.validateImgFile(profileImg);
            URI uploadedUri = imageService.upload(profileImg);
            profileUrl = uploadedUri.toString();

            return profileUrl;
        }

        return null;
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

    private void update(User user, OnboardRequest updateRequest) {
        user.updateNickname(updateRequest.nickname());
        user.updateSex(updateRequest.sex());
        user.updateBirthDate(updateRequest.birthDate());
        user.updateHeight(updateRequest.height());
        user.updateWeight(updateRequest.weight());
        user.updateExerciseIntensity(updateRequest.exerciseIntensity());
    }

    private void update(User user, UpdateRequest updateRequest) {
        user.updateNickname(updateRequest.nickname());
        user.updateSex(updateRequest.sex());
        user.updateBirthDate(updateRequest.birthDate());
        user.updateHeight(updateRequest.height());
        user.updateWeight(updateRequest.weight());
        user.updateExerciseIntensity(updateRequest.exerciseIntensity());
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
            .isPublic(true)
            .build();
    }

    private void assignDefaultAuthority(User user) {
        UserAuthority newAuthority = generateUserAuthority(user, Authority.RESTRICTED);
        userAuthorityRepository.save(newAuthority);
    }

    private UserAuthority generateUserAuthority(User user, Authority authority) {
        return UserAuthority.builder()
            .authority(authority)
            .user(user)
            .build();
    }

}
