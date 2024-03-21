package team.silvertown.masil.post.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostLike;
import team.silvertown.masil.post.domain.PostLikeId;
import team.silvertown.masil.post.dto.SaveLikeDto;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.repository.PostLikeRepository;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PostLikeService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public SaveLikeDto save(Long userId, Long postId, SaveLikeDto request) {
        validateUserId(userId);

        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new DataNotFoundException(PostErrorCode.POST_NOT_FOUND));
        PostLikeId postLikeId = new PostLikeId(userId, postId);
        PostLike postLike = postLikeRepository.findById(postLikeId)
            .orElseGet(() -> saveNewPostLike(postLikeId, request));

        postLike.setIsLike(request.isLike());

        // TODO: 반정규화 필드를 활용한 성능 개선
        post.setLikeCount(postLikeRepository.countByPostId(post.getId()));

        return new SaveLikeDto(postLike.isLike(), postLike.isCreated());
    }

    private void validateUserId(Long userId) {
        boolean notExistUser = !userRepository.existsById(userId);

        Validator.throwIf(notExistUser, () -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));
    }

    private PostLike saveNewPostLike(PostLikeId postLikeId, SaveLikeDto request) {
        PostLike postLike = new PostLike(postLikeId, request.isLike(), false);
        PostLike savedPostLike = postLikeRepository.save(postLike);

        savedPostLike.setCreatedTrue();

        return savedPostLike;
    }

}
