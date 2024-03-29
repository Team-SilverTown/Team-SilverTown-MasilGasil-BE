package team.silvertown.masil.post.service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollRequest;
import team.silvertown.masil.common.scroll.dto.ScrollResponse;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostLikeId;
import team.silvertown.masil.post.domain.PostPin;
import team.silvertown.masil.post.domain.PostViewHistory;
import team.silvertown.masil.post.dto.PostCursorDto;
import team.silvertown.masil.post.dto.request.CreatePostPinRequest;
import team.silvertown.masil.post.dto.request.CreatePostRequest;
import team.silvertown.masil.post.dto.response.CreatePostResponse;
import team.silvertown.masil.post.dto.response.PostDetailResponse;
import team.silvertown.masil.post.dto.response.PostPinDetailResponse;
import team.silvertown.masil.post.dto.response.SimplePostResponse;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.repository.PostLikeRepository;
import team.silvertown.masil.post.repository.PostPinRepository;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.post.repository.PostViewHistoryRepository;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostPinRepository postPinRepository;
    private final PostViewHistoryRepository postViewHistoryRepository;
    private final PostLikeRepository postLikeRepository;

    @Transactional
    public CreatePostResponse create(Long userId, CreatePostRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(PostErrorCode.LOGIN_USER_NOT_FOUND));
        Post post = createPost(request, user);

        savePins(request.pins(), post);

        return new CreatePostResponse(post.getId());
    }

    @Transactional
    public PostDetailResponse getById(Long userId, Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(getNotFoundException(PostErrorCode.POST_NOT_FOUND));
        List<PostPinDetailResponse> pins = PostPinDetailResponse.listFrom(post);
        PostLikeId postLikeId = new PostLikeId(userId, id);
        boolean isLiked = postLikeRepository.existsById(postLikeId);

        increasePostViewCount(post);

        return PostDetailResponse.from(post, pins, isLiked);
    }

    @Transactional(readOnly = true)
    public ScrollResponse<SimplePostResponse> getScrollByAddress(
        Long loginId,
        NormalListRequest request
    ) {
        User user = getUserIfLoggedIn(loginId);
        List<PostCursorDto> postsWithCursor = postRepository.findScrollByAddress(user, request);

        return getScrollResponse(postsWithCursor, request.getSize());
    }

    @Transactional(readOnly = true)
    public ScrollResponse<SimplePostResponse> getScrollByAuthor(
        Long loginId,
        Long userId,
        ScrollRequest request
    ) {
        User author = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(PostErrorCode.AUTHOR_NOT_FOUND));
        User loginUser = getUserIfLoggedIn(loginId);
        List<PostCursorDto> postsWithCursor = postRepository.findScrollByUser(loginUser, author,
            request);

        return getScrollResponse(postsWithCursor, request.getSize());
    }

    private Supplier<DataNotFoundException> getNotFoundException(ErrorCode errorCode) {
        return () -> new DataNotFoundException(errorCode);
    }

    private Post createPost(CreatePostRequest request, User user) {
        Post post = Post.builder()
            .user(user)
            .depth1(request.depth1())
            .depth2(request.depth2())
            .depth3(request.depth3())
            .depth4(request.depth4())
            .path(KakaoPointMapper.mapToLineString(request.path()))
            .title(request.title())
            .content(request.content())
            .distance(request.distance())
            .totalTime(request.totalTime())
            .isPublic(request.isPublic())
            .thumbnailUrl(request.thumbnailUrl())
            .build();

        return postRepository.save(post);
    }

    private void savePins(List<CreatePostPinRequest> pins, Post post) {
        if (Objects.nonNull(pins)) {
            pins.forEach(pin -> savePin(pin, post));
        }
    }

    private void savePin(CreatePostPinRequest pin, Post post) {
        PostPin postPin = createPin(pin, post);

        postPinRepository.save(postPin);
    }

    private void increasePostViewCount(Post post) {
        String ipAddress = getIpAddress();
        PostViewHistory history = new PostViewHistory(post.getId(), ipAddress);
        boolean hasHistory = postViewHistoryRepository.existsById(history.getKey());

        if (hasHistory) {
            return;
        }

        postViewHistoryRepository.save(history);
        post.increaseViewCount();
    }

    private String getIpAddress() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
            .getRequest()
            .getRemoteAddr();
    }

    private PostPin createPin(CreatePostPinRequest request, Post post) {
        User owner = post.getUser();

        return PostPin.builder()
            .userId(owner.getId())
            .point(KakaoPointMapper.mapToPoint(request.point()))
            .content(request.content())
            .thumbnailUrl(request.thumbnailUrl())
            .post(post)
            .build();
    }

    private User getUserIfLoggedIn(Long userId) {
        if (Objects.isNull(userId)) {
            return null;
        }

        return userRepository.findById(userId)
            .orElseThrow(getNotFoundException(PostErrorCode.LOGIN_USER_NOT_FOUND));
    }

    private ScrollResponse<SimplePostResponse> getScrollResponse(
        List<PostCursorDto> postsWithCursor,
        int size
    ) {
        List<SimplePostResponse> posts = postsWithCursor.stream()
            .limit(size)
            .map(PostCursorDto::post)
            .toList();
        String lastCursor = getLastCursor(postsWithCursor, size);

        return ScrollResponse.from(posts, lastCursor);
    }

    private String getLastCursor(List<PostCursorDto> postsWithCursor, int size) {
        if (postsWithCursor.size() > size) {
            return postsWithCursor.get(size - 1)
                .cursor();
        }

        return null;
    }

}
