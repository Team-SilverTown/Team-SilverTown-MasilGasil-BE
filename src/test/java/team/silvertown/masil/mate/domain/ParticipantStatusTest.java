package team.silvertown.masil.mate.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.texture.MateTexture;

@DisplayNameGeneration(ReplaceUnderscores.class)
class ParticipantStatusTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 값이_입력되지_않으면_메이트_참여_상태_반환을_실패한다(String blankValue) {
        // given

        // when
        ThrowingCallable get = () -> ParticipantStatus.get(blankValue);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(get)
            .withMessage(MateErrorCode.BLANK_STATUS.getMessage());
    }

    @Test
    void 잘못된_값이_입력되면_메이트_참여_상태_반환을_실패한다() {
        // given
        String invalidValue = MateTexture.getRandomSentenceWithMax(12);

        // when
        ThrowingCallable get = () -> ParticipantStatus.get(invalidValue);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(get)
            .withMessage(MateErrorCode.INVALID_STATUS.getMessage());
    }

}
