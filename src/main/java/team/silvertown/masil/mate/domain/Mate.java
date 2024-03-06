package team.silvertown.masil.mate.domain;

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
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.BaseEntity;
import team.silvertown.masil.common.map.Address;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.validator.MateValidator;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.user.domain.User;

@Entity
@Table(name = "mates")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Mate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @Embedded
    @Getter(AccessLevel.NONE)
    private Address address;

    @Column(name = "title", length = 30, nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Embedded
    @Getter(AccessLevel.NONE)
    private GatheringPlace gatheringPlace;

    @Column(name = "gather_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private OffsetDateTime gatherAt;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Builder
    private Mate(
        User author,
        Post post,
        String depth1,
        String depth2,
        String depth3,
        String depth4,
        String title,
        String content,
        Point gatheringPlacePoint,
        String gatheringPlaceDetail,
        OffsetDateTime gatherAt,
        Integer capacity
    ) {
        MateValidator.notNull(author, MateErrorCode.NULL_AUTHOR);
        MateValidator.notNull(post, MateErrorCode.NULL_POST);
        MateValidator.validateTitle(title);
        MateValidator.notBlank(content, MateErrorCode.BLANK_CONTENT);
        MateValidator.validateGatherAt(gatherAt);
        MateValidator.validateCapacity(capacity);

        this.author = author;
        this.post = post;
        this.address = new Address(depth1, depth2, depth3, depth4);
        this.title = title;
        this.content = content;
        this.gatheringPlace = new GatheringPlace(gatheringPlacePoint, gatheringPlaceDetail);
        this.gatherAt = gatherAt;
        this.capacity = capacity;
    }

    public String getDepth1() {
        return this.address.getDepth1();
    }

    public String getDepth2() {
        return this.address.getDepth2();
    }

    public String getDepth3() {
        return this.address.getDepth3();
    }

    public String getDepth4() {
        return this.address.getDepth4();
    }

    public KakaoPoint getGatheringPlacePoint() {
        Point point = this.gatheringPlace.getPoint();

        return KakaoPoint.from(point.getCoordinate());
    }

    public String getGatheringPlaceDetail() {
        return this.gatheringPlace.getDetail();
    }

}
