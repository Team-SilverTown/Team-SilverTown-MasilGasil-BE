package team.silvertown.masil.common.map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.texture.MapTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class KakaoPointMapperTest {

    @Test
    void Point_변환을_성공한다() {
        // given
        double dongAnLat = 37.4004;
        double dongAnLng = 126.9555;
        KakaoPoint expected = new KakaoPoint(dongAnLat, dongAnLng);

        // when
        Point actual = KakaoPointMapper.mapToPoint(expected);

        // then
        assertThat(actual.toString()).contains(expected.toRawString());
    }

    @ParameterizedTest
    @NullSource
    void 카카오_포인트가_Null이면_Point_변환을_실패한다(KakaoPoint point) {
        // given

        // when
        ThrowingCallable map = () -> KakaoPointMapper.mapToPoint(point);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(map)
            .withMessage(MapErrorCode.NULL_KAKAO_POINT.getMessage());
    }

    @Test
    void Line_String_변환을_성공한다() {
        // given
        List<KakaoPoint> path = MapTexture.createPath(10);

        // when
        LineString lineString = KakaoPointMapper.mapToLineString(path);

        // then
        String asText = lineString.toString();
        List<String> points = path.stream()
            .map(KakaoPoint::toRawString)
            .toList();

        assertThat(asText).contains(points);
    }

    @ParameterizedTest
    @NullSource
    void 경로가_Null이면_Line_String_변환을_실패한다(List<KakaoPoint> path) {
        // given

        // when
        ThrowingCallable map = () -> KakaoPointMapper.mapToLineString(path);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(map)
            .withMessage(MapErrorCode.NULL_KAKAO_POINT.getMessage());
    }

    @Test
    void 경로의_포인트가_2개_이하면_Line_String_변환을_실패한다() {
        // given
        List<KakaoPoint> path = MapTexture.createPath(2);

        // when
        ThrowingCallable create = () -> KakaoPointMapper.mapToLineString(path);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MapErrorCode.INSUFFICIENT_PATH_POINTS.getMessage());
    }

}
