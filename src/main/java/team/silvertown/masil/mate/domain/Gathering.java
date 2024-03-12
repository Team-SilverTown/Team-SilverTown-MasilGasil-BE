package team.silvertown.masil.mate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.time.OffsetDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.mate.validator.MateValidator;

@Embeddable
@NoArgsConstructor
@Getter
public class Gathering {

    @Column(name = "gathering_place_point", nullable = false)
    private Point point;

    @Column(name = "gathering_place_detail", length = 50, nullable = false)
    private String detail;

    @Column(name = "gathering_at", nullable = false, columnDefinition = "TIMESTAMP(6)")
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE)
    private OffsetDateTime gatheringAt;

    public Gathering(Point point, String detail, OffsetDateTime gatheringAt) {
        MateValidator.validateDetail(detail);
        MateValidator.validateGatheringAt(gatheringAt);

        this.point = point;
        this.detail = detail;
        this.gatheringAt = gatheringAt;
    }

    public KakaoPoint getKakaoPoint() {
        return KakaoPoint.from(this.point.getCoordinate());
    }

}
