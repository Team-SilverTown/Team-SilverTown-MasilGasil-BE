package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.masil.exception.MasilErrorCode;
import team.silvertown.masil.texture.MasilTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilTitleTest {

    @Test
    void 마실_제목_생성을_성공한다() {
        // given
        String title = MasilTexture.getRandomSentenceWithMax(29);

        // when
        ThrowingCallable create = () -> new MasilTitle(title);

        // then
        assertThatNoException().isThrownBy(create);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 마실_제목이_비었으면_오늘_날짜를_붙여_제목을_생성한다(String title) {
        // given
        LocalDate today = OffsetDateTime.now()
            .toLocalDate();
        String expected = today + " 산책 기록";

        // when
        MasilTitle masilTitle = new MasilTitle(title);

        // then
        String actual = masilTitle.getTitle();

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void 마실_제목이_30자를_넘으면_제목_생성을_실패한다() {
        // given
        int length = MasilTexture.getRandomInt(31, 1000);
        String title = MasilTexture.getRandomFixedSentence(length);

        // when
        ThrowingCallable create = () -> new MasilTitle(title);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MasilErrorCode.TITLE_TOO_LONG.getMessage());
    }

}
