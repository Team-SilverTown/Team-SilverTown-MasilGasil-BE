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
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;
import team.silvertown.masil.post.dto.request.CreatePinRequest;
import team.silvertown.masil.post.dto.request.CreateRequest;
import team.silvertown.masil.post.dto.response.CreateResponse;
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

}
