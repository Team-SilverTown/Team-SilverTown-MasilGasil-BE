package team.silvertown.masil.mate.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.OffsetDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MateTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class GatheringTest {

    Point point;
    String detail;
    OffsetDateTime gatheringAt;

    @BeforeEach
    void setUp() {
        point = MapTexture.createPoint();
        detail = MateTexture.createDetail();
        gatheringAt = MateTexture.getFutureDateTime();
    }

    @Test
    void 모집_장소_생성을_성공한다() {
        // given

        // when
        Gathering actual = new Gathering(point, detail, gatheringAt);

        // then
        assertThat(actual)
            .hasFieldOrPropertyWithValue("point", point)
            .hasFieldOrPropertyWithValue("detail", detail)
            .hasFieldOrPropertyWithValue("gatheringAt", gatheringAt);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 모집_장소_상세정보가_없으면_모집_장소_생성을_실패한다(String invalidDetail) {
        // given

        // when
        ThrowingCallable create = () -> new Gathering(point, invalidDetail, gatheringAt);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.BLANK_DETAIL.getMessage());
    }

    @Test
    void 모집_장소_상세정보가_50자를_넘으면_장소_생성을_실패한다() {
        // given
        String invalidDetail = MateTexture.getRandomFixedSentence(51);

        // when
        ThrowingCallable create = () -> new Gathering(point, invalidDetail, gatheringAt);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.DETAIL_TOO_LONG.getMessage());
    }

    @Test
    void 모집_시간이_현재보다_이전이면_메이트_생성을_실패한다() {
        // given
        OffsetDateTime past = MateTexture.getPastDateTime();

        // when
        ThrowingCallable create = () -> new Gathering(point, detail, past);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.GATHER_AT_PAST.getMessage());
    }

}
