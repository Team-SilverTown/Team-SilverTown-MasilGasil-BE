package team.silvertown.masil.common.map;

import org.locationtech.jts.geom.Coordinate;

public record KakaoPoint(double lat, double lng) {

    public String toRawString() {
        return this.lat + " " + this.lng;
    }

    public static KakaoPoint from(Coordinate coordinate) {
        return new KakaoPoint(coordinate.getX(), coordinate.getY());
    }

}
