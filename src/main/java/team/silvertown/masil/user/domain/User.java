package team.silvertown.masil.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.sql.Date;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.BaseEntity;

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
    private Date birthDate;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "exercise_intensity", columnDefinition = "VARCHAR(15)")
    @Enumerated(EnumType.STRING)
    private ExerciseIntensity exerciseIntensity;

    @Column(name = "total_distance")
    private Integer totalDistance;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "is_allowing_notification")
    private boolean isAllowingNotification;

    @Column(name = "provider", columnDefinition = "VARCHAR(20)")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "social_id", length = 50)
    private String socialId;

}
