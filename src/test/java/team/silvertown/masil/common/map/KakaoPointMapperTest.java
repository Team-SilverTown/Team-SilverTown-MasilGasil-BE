package team.silvertown.masil.common.map;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import team.silvertown.masil.common.exception.BadRequestException;

@DisplayNameGeneration(ReplaceUnderscores.class)
class KakaoPointMapperTest {

    double dongAnLat = 37.4004;
    double dongAnLng = 126.9555;

    @Test
    void Point_변환을_성공한다() {
        // given
        KakaoPoint point = new KakaoPoint(dongAnLat, dongAnLng);

        // when
        ThrowingCallable map = () -> KakaoPointMapper.mapToPoint(point);

        // then
        assertThatNoException().isThrownBy(map);
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
        List<KakaoPoint> path = createPath(10);

        // when
        ThrowingCallable map = () -> KakaoPointMapper.mapToLineString(path);

        // then
        assertThatNoException().isThrownBy(map);
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
        List<KakaoPoint> path = createPath(2);

        // when
        ThrowingCallable create = () -> KakaoPointMapper.mapToLineString(path);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MapErrorCode.INSUFFICIENT_PATH_POINTS.getMessage());
    }

    List<KakaoPoint> createPath(int size) {
        List<KakaoPoint> path = new ArrayList<>();
        double appender = 0.002;

        for (int i = 0; i < size; i++) {
            double lat = dongAnLat + appender * i;
            double lng = dongAnLng + appender * i;

            KakaoPoint point = new KakaoPoint(lat, lng);

            path.add(point);
        }

        return path;
    }

}
