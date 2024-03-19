package team.silvertown.masil.post.domain;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static team.silvertown.masil.post.exception.PostErrorCode.INVALID_IP_ADDRESS;
import static team.silvertown.masil.post.exception.PostErrorCode.POST_NOT_FOUND;

import net.datafaker.Faker;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import team.silvertown.masil.common.exception.BadRequestException;

@DisplayNameGeneration(ReplaceUnderscores.class)
class PostViewHistoryTest {

    static final Faker faker = new Faker();

    @Test
    void 포스트_조회_기록_인스턴스를_생성하는데_성공한다() {
        // given
        Long postId = (long) faker.number().positive();
        String ip = faker.internet().publicIpV4Address();

        // when
        ThrowingCallable create = () -> new PostViewHistory(postId, ip);

        // then
        assertThatNoException().isThrownBy(create);
    }

    @Test
    void 포스트_id가_null일_경우_인스턴스를_생성하는데_실패한다() {
        // given
        Long postId = null;
        String ip = faker.internet().publicIpV4Address();

        // when
        ThrowingCallable create = () -> new PostViewHistory(postId, ip);

        // then
        assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(create)
            .withMessage(POST_NOT_FOUND.getMessage());
    }

    @Test
    void ip주소가_올바르지_않을_경우_인스턴스를_생성하는데_실패한다() {
        // given
        Long postId = (long) faker.number().positive();
        String ip = faker.internet().domainName();

        // when
        ThrowingCallable create = () -> new PostViewHistory(postId, ip);

        // then
        assertThatExceptionOfType(BadRequestException.class)
            .isThrownBy(create)
            .withMessage(INVALID_IP_ADDRESS.getMessage());
    }

}
