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
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.common.map.Address;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.validator.PostValidator;
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
    private String content;

    @Column(name = "thumbnail_url", length = 1024)
    private String thumbnailUrl;

    @Column(name = "distance", nullable = false)
    private Integer distance;

    @Column(name = "total_time", nullable = false)
    private Integer totalTime;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    @Column(name = "view_count", nullable = false)
    private int viewCount;

    @Column(name = "like_count", nullable = false)
    private int likeCount;

    @Builder
    private Post(
        User user,
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
        Boolean isPublic
    ) {
        PostValidator.notNull(user, PostErrorCode.NULL_USER);
        PostValidator.validateUrl(thumbnailUrl);
        PostValidator.validateTitle(title);
        PostValidator.notUnder(distance, 0, PostErrorCode.NON_POSITIVE_DISTANCE);
        PostValidator.notUnder(totalTime, 0, PostErrorCode.NON_POSITIVE_TOTAL_TIME);

        this.user = user;
        this.address = new Address(depth1, depth2, depth3, depth4);
        this.path = path;
        this.title = title;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
        this.distance = distance;
        this.totalTime = totalTime;
        this.isPublic = Objects.isNull(isPublic) || isPublic;
        this.viewCount = 0;
        this.likeCount = 0;
    }

}
