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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.Address;
import team.silvertown.masil.common.BaseEntity;
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

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT")
    private String text;

    @Column(name = "thumbnail_url", length = 1024)
    private String thumbnailUrl;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    @Column(name = "total_time", nullable = false)
    private Integer totalTime;

    @Column(name = "started_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    private OffsetDateTime startedAt;

}
