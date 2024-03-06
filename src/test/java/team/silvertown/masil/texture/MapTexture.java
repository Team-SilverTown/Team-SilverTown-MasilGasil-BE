package team.silvertown.masil.texture;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.KakaoPointMapper;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MapTexture {

    private static double dongAnLat = 37.4004;
    private static double dongAnLng = 126.9555;
    private static double appender = 0.002;

    public static List<KakaoPoint> createPath(int size) {
        List<KakaoPoint> path = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            double lat = dongAnLat + appender * i;
            double lng = dongAnLng + appender * i;

            KakaoPoint point = new KakaoPoint(lat, lng);

            path.add(point);
        }

        return path;
    }

    public static LineString createLineString(int size) {
        List<KakaoPoint> path = createPath(size);

        return KakaoPointMapper.mapToLineString(path);
    }

    public static Point createPoint() {
        KakaoPoint point = createKakaoPoint();

        return KakaoPointMapper.mapToPoint(point);
    }

    public static KakaoPoint createKakaoPoint() {
        Random random = new Random();
        double latitude = random.nextDouble(-90, 90);
        double longitude = random.nextDouble(-180, 180);
        return new KakaoPoint(latitude, longitude);
    }

}
