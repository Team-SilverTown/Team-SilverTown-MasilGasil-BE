package team.silvertown.masil.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.dto.SaveLikeDto;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.texture.BaseDomainTexture;
import team.silvertown.masil.texture.PostTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostLikeServiceTest {

    @Autowired
    EntityManager entityManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostLikeService postLikeService;

    User user;
    Post post;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserTexture.createValidUser());
        post = postRepository.save(PostTexture.createValidPost());

        entityManager.clear();
    }

    @ParameterizedTest(name = "{0} 좋아요 생성")
    @ValueSource(booleans = {true, false})
    void 게시글_좋아요_데이터를_생성할_수_있다(boolean expected) {
        // given
        SaveLikeDto request = new SaveLikeDto(expected, false);

        // when
        SaveLikeDto response = postLikeService.save(user.getId(), post.getId(), request);

        // then
        int postLikesExpected = post.getLikeCount() + (expected ? 1 : 0);
        int postLikesActual = postRepository.findById(post.getId())
            .orElseThrow()
            .getLikeCount();

        assertThat(response.isCreated()).isTrue();
        assertThat(response.isLike()).isEqualTo(expected);
        assertThat(postLikesExpected).isEqualTo(postLikesActual);
    }

    @ParameterizedTest(name = "좋아요를 {0}로 수정")
    @ValueSource(booleans = {true, false})
    void 게시글_좋아요_데이터를_수정할_수_있다(boolean expected) {
        // given
        postLikeService.save(user.getId(), post.getId(), new SaveLikeDto(!expected, false));
        entityManager.clear();

        SaveLikeDto request = new SaveLikeDto(expected, false);

        // when
        SaveLikeDto response = postLikeService.save(user.getId(), post.getId(), request);

        // then
        int postLikesExpected = expected ? 1 : 0;
        int postLikesActual = postRepository.findById(post.getId())
            .orElseThrow()
            .getLikeCount();

        assertThat(response.isCreated()).isFalse();
        assertThat(response.isLike()).isEqualTo(expected);
        assertThat(postLikesExpected).isEqualTo(postLikesActual);
    }

    @Test
    void 존재하지_않는_유저일_경우_예외가_발생한다() {
        // given
        long userId = BaseDomainTexture.getRandomLong(user.getId() + 1, Long.MAX_VALUE);
        long postId = post.getId();
        SaveLikeDto request = new SaveLikeDto(true, false);

        // when
        ThrowingCallable likePost = () -> postLikeService.save(userId, postId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class)
            .isThrownBy(likePost)
            .withMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 존재하지_않는_포스트일_경우_예외가_발생한다() {
        // given
        long userId = user.getId();
        long postId = BaseDomainTexture.getRandomLong(post.getId() + 1, Long.MAX_VALUE);
        SaveLikeDto request = new SaveLikeDto(true, false);

        // when
        ThrowingCallable likePost = () -> postLikeService.save(userId, postId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class)
            .isThrownBy(likePost)
            .withMessage(PostErrorCode.POST_NOT_FOUND.getMessage());
    }

}
