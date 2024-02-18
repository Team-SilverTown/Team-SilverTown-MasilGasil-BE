package team.silvertown.masil.common.map;

import java.util.List;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public final class KakaoPointMapper {

    private static final WKTReader wktReader = new WKTReader();

    private KakaoPointMapper() {
    }

    public static Point mapToPoint(KakaoPoint point) {
        try {
            return (Point) wktReader.read("POINT(" + point.toRawString() + ")");
        } catch (ParseException e) {
            throw new RuntimeException("Wrong Point format");
        }
    }

    public static LineString mapToLineString(List<KakaoPoint> path) {
        StringBuilder stringBuilder = new StringBuilder("LINESTRING(");

        path.forEach(point -> stringBuilder.append(point.toRawString())
            .append(", "));
        stringBuilder.replace(stringBuilder.length() - 3, stringBuilder.length() - 1, ")");

        try {
            return (LineString) wktReader.read(stringBuilder.toString());
        } catch (ParseException e) {
            throw new RuntimeException("Wrong LineString format");
        }
    }

}
