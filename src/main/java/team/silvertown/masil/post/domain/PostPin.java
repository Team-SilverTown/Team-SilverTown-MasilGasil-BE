package team.silvertown.masil.post.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.validator.PostValidator;

@Entity
@Table(name = "post_pins")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class PostPin extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "point", nullable = false)
    private Point point;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "thumbnail_url", length = 1024)
    private String thumbnailUrl;

    @Builder
    private PostPin(
        Post post,
        Long userId,
        Point point,
        String content,
        String thumbnailUrl
    ) {
        PostValidator.notNull(post, PostErrorCode.NULL_MASIL);
        PostValidator.validateUrl(thumbnailUrl);
        PostValidator.validatePinOwner(post, userId);

        this.post = post;
        this.userId = userId;
        this.point = point;
        this.content = content;
        this.thumbnailUrl = thumbnailUrl;
    }

    public KakaoPoint getSimplePoint() {
        return KakaoPoint.from(this.point.getCoordinate());
    }

}
