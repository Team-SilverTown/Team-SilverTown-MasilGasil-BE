package team.silvertown.masil.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;
import team.silvertown.masil.post.dto.request.CreatePinRequest;
import team.silvertown.masil.post.dto.request.CreateRequest;
import team.silvertown.masil.post.dto.response.CreateResponse;
import team.silvertown.masil.post.dto.response.PostResponse;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.repository.PostPinRepository;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MasilTexture;
import team.silvertown.masil.texture.PostTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class PostServiceTest {

    @Autowired
    PostService postService;

    @Autowired
    PostRepository postRepository;

    @Autowired
    PostPinRepository postPinRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EntityManager entityManager;

    User user;
    String addressDepth1;
    String addressDepth2;
    String addressDepth3;
    List<KakaoPoint> path;
    String title;
    int distance;
    int totalTime;

    @BeforeEach
    void setUp() {
        user = userRepository.save(UserTexture.createValidUser());
        addressDepth1 = MasilTexture.createAddressDepth1();
        addressDepth2 = MasilTexture.createAddressDepth2();
        addressDepth3 = MasilTexture.createAddressDepth3();
        path = MapTexture.createPath(10000);
        title = PostTexture.getRandomSentenceWithMax(29);
        distance = PostTexture.getRandomPositive();
        totalTime = PostTexture.getRandomPositive();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 0})
    void 산책로_포스트_생성을_성공한다(int expectedPinCount) {
        // given
        List<CreatePinRequest> pinRequests = createPinRequests(expectedPinCount, 10000);
        CreateRequest request = new CreateRequest(addressDepth1, addressDepth2, addressDepth3,
            "", path, title, null, distance, totalTime, true, pinRequests, null);

        // when
        CreateResponse expected = postService.create(user.getId(), request);

        // then
        Post actual = postRepository.findById(expected.id())
            .orElseThrow();
        List<PostPin> actualPins = postPinRepository.findAll();

        assertThat(actual.getId()).isEqualTo(expected.id());
        assertThat(actualPins.size()).isEqualTo(expectedPinCount);
    }

    @Test
    void 사용자가_존재하지_않으면_산책로_포스트_생성을_실패한다() {
        // given
        long invalidId = PostTexture.getRandomId();
        CreateRequest request = new CreateRequest(addressDepth1, addressDepth2, addressDepth3,
            "", path, title, null, distance, totalTime, true, null, null);

        // when
        ThrowingCallable create = () -> postService.create(invalidId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
            .withMessage(PostErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 산책로_포스트_단일_조회를_성공한다() {
        // given
        Post post = PostTexture.createDependentPost(user, 10000);
        Post expected = postRepository.save(post);
        int pinSize = 10;
        User author = post.getUser();
        List<PostPin> postPins = PostTexture.createDependentPostPins(expected, author.getId(),
            pinSize);

        postPinRepository.saveAll(postPins);
        entityManager.clear();

        // when
        PostResponse actual = postService.getById(expected.getId());

        // then
        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expected.getId())
            .hasFieldOrPropertyWithValue("depth1", expected.getDepth1())
            .hasFieldOrPropertyWithValue("depth2", expected.getDepth2())
            .hasFieldOrPropertyWithValue("depth3", expected.getDepth3())
            .hasFieldOrPropertyWithValue("depth4", expected.getDepth4())
            .hasFieldOrPropertyWithValue("title", expected.getTitle())
            .hasFieldOrPropertyWithValue("distance", expected.getDistance())
            .hasFieldOrPropertyWithValue("totalTime", expected.getTotalTime())
            .hasFieldOrPropertyWithValue("isPublic", expected.isPublic())
            .hasFieldOrPropertyWithValue("viewCount", expected.getViewCount())
            .hasFieldOrPropertyWithValue("likeCount", expected.getLikeCount())
            .hasFieldOrPropertyWithValue("authorId", user.getId())
            .hasFieldOrPropertyWithValue("authorName", user.getNickname());
        assertThat(actual.pins()).hasSize(pinSize);
    }

    @Test
    void 사용자가_존재하지_않으면_마실_단일_조회를_실패한다() {
        // given
        long invalidId = MasilTexture.getRandomId();

        // when
        ThrowingCallable getById = () -> postService.getById(invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getById)
            .withMessage(PostErrorCode.POST_NOT_FOUND.getMessage());
    }

    List<CreatePinRequest> createPinRequests(int size, int maxPathPoints) {
        List<CreatePinRequest> pinRequests = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Coordinate coordinate = MapTexture.createPoint()
                .getCoordinate();
            KakaoPoint point = KakaoPoint.from(coordinate);
            CreatePinRequest createPinRequest = new CreatePinRequest(point, null, null);

            pinRequests.add(createPinRequest);
        }

        return pinRequests;
    }

}
