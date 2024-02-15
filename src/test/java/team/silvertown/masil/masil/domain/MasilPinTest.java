package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.masil.domain.MasilPin.MasilPinBuilder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilPinTest {

    @Test
    void 마실_핀_생성을_할_수_있다() {
        // given
        MasilPinBuilder builder = MasilPin.builder();

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

}
