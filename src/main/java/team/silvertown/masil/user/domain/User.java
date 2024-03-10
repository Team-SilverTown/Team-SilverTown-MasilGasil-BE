package team.silvertown.masil.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.common.validator.DateValidator;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.validator.UserValidator;

@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "sex", columnDefinition = "CHAR(8)")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "exercise_intensity", columnDefinition = "VARCHAR(15)")
    @Enumerated(EnumType.STRING)
    private ExerciseIntensity exerciseIntensity;

    @Column(name = "profileImg", columnDefinition = "VARCHAR(500)")
    private String profileImg;

    @Column(name = "total_distance")
    private Integer totalDistance;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "is_allowing_notification")
    private Boolean isAllowingNotification;

    @Column(name = "provider", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "social_id", length = 50)
    private String socialId;

    public void update(OnboardRequest request) {
        UserValidator.validateNickname(request.nickname(), UserErrorCode.INVALID_NICKNAME);
        UserValidator.validateSex(request.sex(), UserErrorCode.INVALID_SEX);
        UserValidator.validateBirthDate(request.birthDate(), UserErrorCode.INVALID_BIRTH_DATE);
        UserValidator.validateHeight(request.height(), UserErrorCode.INVALID_HEIGHT);
        UserValidator.validateWeight(request.weight(), UserErrorCode.INVALID_WEIGHT);
        UserValidator.validateExerciseIntensity(request.exerciseIntensity());

        this.nickname = request.nickname();
        this.sex = Sex.valueOf(request.sex());
        this.birthDate = DateValidator.parseDate(request.birthDate(),
            UserErrorCode.INVALID_BIRTH_DATE);
        this.height = request.height();
        this.weight = request.weight();
        this.exerciseIntensity = ExerciseIntensity.valueOf(request.exerciseIntensity());
    }

    public void updateProfile(String profileImg) {
        this.profileImg = profileImg;
    }

}
