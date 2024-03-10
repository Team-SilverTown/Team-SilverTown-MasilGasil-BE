package team.silvertown.masil.post.service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.common.response.ScrollResponse;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;
import team.silvertown.masil.post.dto.PostCursorDto;
import team.silvertown.masil.post.dto.request.CreatePinRequest;
import team.silvertown.masil.post.dto.request.CreateRequest;
import team.silvertown.masil.post.dto.request.NormalListRequest;
import team.silvertown.masil.post.dto.response.CreateResponse;
import team.silvertown.masil.post.dto.response.PinDetailResponse;
import team.silvertown.masil.post.dto.response.PostDetailResponse;
import team.silvertown.masil.post.dto.response.SimplePostResponse;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.repository.PostPinRepository;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final PostPinRepository postPinRepository;

    @Transactional
    public CreateResponse create(Long userId, CreateRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(throwNotFound(PostErrorCode.USER_NOT_FOUND));
        Post post = createPost(request, user);

        savePins(request.pins(), post);

        return new CreateResponse(post.getId());
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getById(Long id) {
        Post post = postRepository.findById(id)
            .orElseThrow(throwNotFound(PostErrorCode.POST_NOT_FOUND));
        List<PinDetailResponse> pins = PinDetailResponse.listFrom(post);

        return PostDetailResponse.from(post, pins);
    }

    @Transactional(readOnly = true)
    public ScrollResponse<SimplePostResponse> getSliceByAddress(
        Long userId,
        NormalListRequest request
    ) {
        User user = getUserIfLoggedIn(userId);
        List<PostCursorDto> postsWithCursor = postRepository.findSliceBy(user, request);

        return getScrollResponse(postsWithCursor, request.size());
    }

    private Supplier<DataNotFoundException> throwNotFound(ErrorCode errorCode) {
        return () -> new DataNotFoundException(errorCode);
    }

    private Post createPost(CreateRequest request, User user) {
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

    private void savePins(List<CreatePinRequest> pins, Post post) {
        if (Objects.nonNull(pins)) {
            pins.forEach(pin -> savePin(pin, post));
        }
    }

    private void savePin(CreatePinRequest pin, Post post) {
        PostPin postPin = createPin(pin, post);

        postPinRepository.save(postPin);
    }

    private PostPin createPin(CreatePinRequest request, Post post) {
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
            .orElseThrow(throwNotFound(PostErrorCode.USER_NOT_FOUND));
    }

    private ScrollResponse<SimplePostResponse> getScrollResponse(
        List<PostCursorDto> postsWithCursor,
        int size
    ) {
        boolean hasNext = postsWithCursor.size() > size;

        if (hasNext) {
            postsWithCursor.remove(size);
        }

        List<SimplePostResponse> posts = postsWithCursor.stream()
            .map(PostCursorDto::post)
            .toList();
        String lastCursor = getLastCursor(postsWithCursor, hasNext);

        return ScrollResponse.from(posts, lastCursor);
    }

    private String getLastCursor(List<PostCursorDto> postsWithCursor, boolean hasNext) {
        if (hasNext) {
            return postsWithCursor.get(postsWithCursor.size() - 1)
                .cursor();
        }

        return null;
    }

}
