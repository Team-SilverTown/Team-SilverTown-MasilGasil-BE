package team.silvertown.masil.post.domain;

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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.Address;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.user.domain.User;

@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

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

    @Column(name = "is_public", nullable = false, columnDefinition = "DEFAULT 1")
    private boolean isPublic = true;

    @Column(name = "view_count", nullable = false, columnDefinition = "DEFAULT 0")
    private int viewCount = 0;

    @Column(name = "like_count", nullable = false, columnDefinition = "DEFAULT 1")
    private int likeCount = 0;

}
