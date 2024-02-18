package team.silvertown.masil.masil.domain;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.masil.domain.Masil.MasilBuilder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilTest {

    @Test
    void 마실_생성을_할_수_있다() {
        // given
        MasilBuilder builder = Masil.builder();

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

}
