package team.silvertown.masil.common.map;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.validator.Validator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class KakaoPointMapper {

    private static final int MIN_POINT_NUM = 2;
    private static final WKTReader wktReader = new WKTReader();

    public static Point mapToPoint(KakaoPoint point) {
        Validator.notNull(point, () -> new BadRequestException(MapErrorCode.NULL_KAKAO_POINT));

        try {
            return (Point) wktReader.read("POINT(" + point.toRawString() + ")");
        } catch (ParseException e) {
            throw new RuntimeException("Point 변환을 실패했습니다");
        }
    }

    public static LineString mapToLineString(List<KakaoPoint> path) {
        Validator.throwIf(path.size() < MIN_POINT_NUM,
            () -> new BadRequestException(MapErrorCode.INSUFFICIENT_PATH_POINTS));

        StringBuilder stringBuilder = new StringBuilder("LINESTRING(");

        path.forEach(point -> stringBuilder.append(point.toRawString())
            .append(", "));
        stringBuilder.replace(stringBuilder.length() - 3, stringBuilder.length() - 1, ")");

        try {
            return (LineString) wktReader.read(stringBuilder.toString());
        } catch (ParseException e) {
            throw new RuntimeException("LineString 변환을 실패했습니다");
        }
    }

}
