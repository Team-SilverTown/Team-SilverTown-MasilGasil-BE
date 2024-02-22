package team.silvertown.masil.common.map;

import org.locationtech.jts.geom.Point;

public record KakaoPoint(double lat, double lng) {

    public String toRawString() {
        return this.lat + " " + this.lng;
    }

    public static KakaoPoint from(Point point) {
        return new KakaoPoint(point.getX(), point.getY());
    }

}
