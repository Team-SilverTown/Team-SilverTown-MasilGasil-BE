package team.silvertown.masil.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.exception.UserValidator;

@Entity
@Table(name = "user_agreements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class UserAgreement extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "is_allowing_marketing")
    private Boolean isAllowingMarketing;

    @Column(name = "is_personal_info_consented")
    private Boolean isPersonalInfoConsented;

    @Column(name = "is_location_info_consented")
    private Boolean isLocationInfoConsented;

    @Column(name = "is_under_age_consent_confirmed")
    private Boolean isUnderAgeConsentConfirmed;

    @Column(name = "marketing_consented_at")
    private LocalDateTime marketingConsentedAt;

    @Builder
    private UserAgreement(
        Long id,
        User user,
        Boolean isAllowingMarketing,
        Boolean isPersonalInfoConsented,
        Boolean isLocationInfoConsented,
        Boolean isUnderAgeConsentConfirmed,
        LocalDateTime marketingConsentedAt
    ) {
        UserValidator.validateIsAllowingMarketing(isAllowingMarketing,
            UserErrorCode.INVALID_ALLOWING_MARKETING);
        UserValidator.validateIsPersonalInfoConsented(isPersonalInfoConsented,
            UserErrorCode.INVALID_PERSONAL_INFO_CONSENTED);
        UserValidator.validateIsLocationInfoConsented(isLocationInfoConsented,
            UserErrorCode.INVALID_LOCATION_INFO_CONSENTED);
        UserValidator.validateIsUnderAgeConsentConfirmed(isUnderAgeConsentConfirmed,
            UserErrorCode.INVALID_UNDER_AGE_CONSENTED);

        this.id = id;
        this.user = user;
        this.isAllowingMarketing = isAllowingMarketing;
        this.isPersonalInfoConsented = isPersonalInfoConsented;
        this.isLocationInfoConsented = isLocationInfoConsented;
        this.isUnderAgeConsentConfirmed = isUnderAgeConsentConfirmed;
        this.marketingConsentedAt = marketingConsentedAt;
    }

}
