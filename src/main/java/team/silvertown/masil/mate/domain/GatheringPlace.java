package team.silvertown.masil.mate.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.mate.validator.MateValidator;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class GatheringPlace {

    @Column(name = "gathering_place_point", nullable = false)
    private Point point;

    @Column(name = "gathering_place_detail", length = 50, nullable = false)
    private String detail;

    public GatheringPlace(Point point, String detail) {
        MateValidator.validateDetail(detail);

        this.point = point;
        this.detail = detail;
    }

}
