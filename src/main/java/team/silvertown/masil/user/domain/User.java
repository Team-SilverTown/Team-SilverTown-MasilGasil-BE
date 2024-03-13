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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.common.validator.DateValidator;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.validator.UserValidator;

@Entity
@Table(name = "users")
@NoArgsConstructor
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

    @Column(name = "total_calories")
    private Integer totalCalories;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "is_allowing_notification")
    private Boolean isAllowingNotification;

    @Column(name = "provider", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "social_id", length = 50)
    private String socialId;

    public void updateNickname(String nickname) {
        UserValidator.validateNickname(nickname, UserErrorCode.INVALID_NICKNAME);
        this.nickname = nickname;
    }

    public void updateSex(String sex) {
        Sex validatedSex = Sex.get(sex);
        this.sex = validatedSex;
    }

    public void updateBirthDate(String birthDate) {
        UserValidator.validateBirthDate(birthDate, UserErrorCode.INVALID_BIRTH_DATE);
        this.birthDate = DateValidator.parseDate(birthDate, UserErrorCode.INVALID_BIRTH_DATE);

    }

    public void updateHeight(Integer height) {
        UserValidator.validateHeight(height, UserErrorCode.INVALID_HEIGHT);
        this.height = height;
    }

    public void updateWeight(Integer weight) {
        UserValidator.validateWeight(weight, UserErrorCode.INVALID_WEIGHT);
        this.weight = weight;
    }

    public void updateExerciseIntensity(String exerciseIntensity) {
        ExerciseIntensity validatedIntensity = ExerciseIntensity.get(exerciseIntensity);
        this.exerciseIntensity = validatedIntensity;
    }

    public void toggleIsPublic() {
        this.isPublic = !this.isPublic;
    }

    public void updateStats(int distance, int calories) {
        initializeStats();

        this.totalDistance += distance;
        this.totalCalories += calories;
        this.totalCount++;
    }

    private void initializeStats() {
        if (hasNullStats()) {
            this.totalCount = 0;
            this.totalDistance = 0;
            this.totalCalories = 0;
        }
    }

    private boolean hasNullStats() {
        return Objects.isNull(this.totalDistance)
            || Objects.isNull(this.totalCount)
            || Objects.isNull(this.totalCalories);
    }

}
