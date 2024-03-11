package team.silvertown.masil.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User.UserBuilder;

@DisplayNameGeneration(ReplaceUnderscores.class)
class UserTest {

    @Test
    void 유저_생성을_할_수_있다() {
        // given
        UserBuilder builder = User.builder();

        // when
        ThrowingCallable create = builder::build;

        // then
        assertThatNoException().isThrownBy(create);
    }

    @Test
    void 사용자의_산책_통계_갱신을_성공한다() {
        // given
        User user = UserTexture.createValidUser();
        int distanceBefore = user.getTotalDistance();
        int totalCountBefore = user.getTotalCount();
        int expectedDistance = UserTexture.getRandomPositive();

        // when
        user.updateStats(expectedDistance);

        // then
        assertThat(user.getTotalDistance()).isEqualTo(distanceBefore + expectedDistance);
        assertThat(user.getTotalCount()).isEqualTo(totalCountBefore + 1);
    }

    @Test
    void 사용자는_통계가_null로_생성될_수_있다() {
        // given
        User user = User.builder()
            .build();

        // when
        Integer totalDistance = user.getTotalDistance();
        Integer totalCount = user.getTotalCount();

        // then
        assertThat(totalDistance).isNull();
        assertThat(totalCount).isNull();
    }

    @Test
    void 사용자_통계가_null일_때_통계를_갱신하면_0으로_초기화_후_갱신한다() {
        // given
        User user = User.builder()
            .build();

        // when
        user.updateStats(0);

        // then
        Integer totalDistance = user.getTotalDistance();
        Integer totalCount = user.getTotalCount();

        assertThat(totalDistance).isNotNull();
        assertThat(totalCount).isNotNull();
    }

}
