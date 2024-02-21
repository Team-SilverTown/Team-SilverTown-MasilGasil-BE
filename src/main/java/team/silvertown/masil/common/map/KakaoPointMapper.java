package team.silvertown.masil.common.map;

import java.util.List;
import java.util.StringJoiner;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import team.silvertown.masil.common.validator.Validator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KakaoPointMapper {

    private static final WKTReader wktReader = new WKTReader();
    private static final int MIN_POINT_NUM = 3;
    private static final String POINT_PREFIX = "POINT(";
    private static final String LINESTRING_PREFIX = "LINESTRING(";
    private static final String LINESTRING_DELIM = ", ";
    private static final String GEOMETRY_SUFFIX = ")";
    private static final String POINT_MAPPING_FAILED = "Point 변환을 실패했습니다 - ";
    private static final String LINESTRING_MAPPING_FAILED = "LineString 변환을 실패했습니다 - ";

    public static Point mapToPoint(KakaoPoint point) {
        Validator.notNull(point, MapErrorCode.NULL_KAKAO_POINT);

        try {
            return (Point) wktReader.read(POINT_PREFIX + point.toRawString() + GEOMETRY_SUFFIX);
        } catch (ParseException e) {
            throw new RuntimeException(POINT_MAPPING_FAILED + e.getMessage());
        }
    }

    public static LineString mapToLineString(List<KakaoPoint> path) {
        Validator.notNull(path, MapErrorCode.NULL_KAKAO_POINT);
        Validator.notUnder(path.size(), MIN_POINT_NUM, MapErrorCode.INSUFFICIENT_PATH_POINTS);

        StringJoiner stringJoiner = new StringJoiner(LINESTRING_DELIM, LINESTRING_PREFIX,
            GEOMETRY_SUFFIX);

        path.stream()
            .map(KakaoPoint::toRawString)
            .forEach(stringJoiner::add);

        try {
            return (LineString) wktReader.read(stringJoiner.toString());
        } catch (ParseException e) {
            throw new RuntimeException(LINESTRING_MAPPING_FAILED + e.getMessage());
        }
    }

}
