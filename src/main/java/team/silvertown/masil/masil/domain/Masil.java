package team.silvertown.masil.masil.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.common.map.Address;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.masil.validator.MasilValidator;
import team.silvertown.masil.user.domain.User;

@Entity
@Table(name = "masils")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Masil extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @Column(name = "post_id")
    private Long postId;

    @Embedded
    private Address address;

    @Column(name = "path", nullable = false)
    private LineString path;

    @Embedded
    private MasilTitle title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 1024)
    private String thumbnailUrl;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    @Column(name = "total_time", nullable = false)
    private Integer totalTime;

    @Column(name = "started_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private OffsetDateTime startedAt;

    @Builder
    private Masil(
        User user,
        Long postId,
        String depth1,
        String depth2,
        String depth3,
        String depth4,
        LineString path,
        String title,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime,
        OffsetDateTime startedAt
    ) {
        MasilValidator.notNull(user, MasilErrorCode.NULL_USER);
        MasilValidator.validateUrl(thumbnailUrl);
        MasilValidator.notNull(distance, MasilErrorCode.NULL_DISTANCE);
        MasilValidator.notNull(totalTime, MasilErrorCode.NULL_TOTAL_TIME);

        this.user = user;
        this.postId = postId;
        this.address = new Address(depth1, depth2, depth3, depth4);
        this.path = path;
        this.title = new MasilTitle(title);
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.distance = distance;
        this.totalTime = totalTime;
        this.startedAt = startedAt;
    }

}
