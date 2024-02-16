package team.silvertown.masil.common.map;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public record KakaoPoint(double lat, double lng) {

    public Point toPoint(WKTReader wktReader) {
        try {
            return (Point) wktReader.read("POINT(" + this.toRawString() + ")");
        } catch (ParseException e) {
            // Exception 작업 후 수정 예정
            throw new IllegalArgumentException("Wrong point format");
        }
    }

    public String toRawString() {
        return this.lat + " " + this.lng;
    }

}
