package team.silvertown.masil;

import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class MasilApplicationTests {

    @Test
	void 애플리케이션을_실행할_수_있다() {
        // given
        String[] args = new String[0];

        // when
        ThrowingCallable startApplication = () -> MasilApplication.main(args);

        // then
        assertThatNoException().isThrownBy(startApplication);
    }

}
