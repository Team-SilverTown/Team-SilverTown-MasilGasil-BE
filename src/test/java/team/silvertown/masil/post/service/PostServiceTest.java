package team.silvertown.masil.post.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.locationtech.jts.geom.Coordinate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.scroll.OrderType;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollRequest;
import team.silvertown.masil.common.scroll.dto.ScrollResponse;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;
import team.silvertown.masil.post.dto.request.CreatePostPinRequest;
import team.silvertown.masil.post.dto.request.CreatePostRequest;
import team.silvertown.masil.post.dto.response.CreatePostResponse;
import team.silvertown.masil.post.dto.response.PostDetailResponse;
import team.silvertown.masil.post.dto.response.SimplePostResponse;
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

        entityManager.clear();
    }

    @ParameterizedTest
    @ValueSource(ints = {10, 0})
    void 산책로_포스트_생성을_성공한다(int expectedPinCount) {
        // given
        List<CreatePostPinRequest> pinRequests = createPinRequests(expectedPinCount, 10000);
        CreatePostRequest request = new CreatePostRequest(addressDepth1, addressDepth2,
            addressDepth3,
            "", path, title, null, distance, totalTime, true, pinRequests, null);

        // when
        CreatePostResponse expected = postService.create(user.getId(), request);

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
        CreatePostRequest request = new CreatePostRequest(addressDepth1, addressDepth2,
            addressDepth3,
            "", path, title, null, distance, totalTime, true, null, null);

        // when
        ThrowingCallable create = () -> postService.create(invalidId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
            .withMessage(PostErrorCode.LOGIN_USER_NOT_FOUND.getMessage());
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
        PostDetailResponse actual = postService.getById(expected.getId());

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
    void 사용자가_존재하지_않으면_산책로_포스트_단일_조회를_실패한다() {
        // given
        long invalidId = MasilTexture.getRandomId();

        // when
        ThrowingCallable getById = () -> postService.getById(invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getById)
            .withMessage(PostErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    void 산책로_포스트_최신순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);
        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.LATEST.name())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        String expectedLastCursor = getLastLatestCursor(expectedSize - 1);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 다음_커서_기반으로_산책로_포스트_최신순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);
        long lastId = createPostsAndGetLastId(totalSize);
        int expectedSize = 10;
        String idCursor = String.valueOf(lastId - expectedSize + 1);
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.LATEST.name())
            .cursor(idCursor)
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        String expectedLastCursor = getLastLatestCursor(
            (int) (lastId - (Integer.parseInt(idCursor) - expectedSize)));

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 산책로_포스트_인기순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);
        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.MOST_POPULAR.name())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        Post actualLast = getLastMostPopularPost(expectedSize - 1);
        String likeCount = String.valueOf(actualLast.getLikeCount());
        String id = String.valueOf(actualLast.getId());

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(likeCount, id);
    }

    @Test
    void 다음_커서_기반으로_산책로_포스트_인기순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);
        long lastId = createPostsAndGetLastId(totalSize);
        int expectedSize = 10;
        String idCursor = String.valueOf(lastId - expectedSize + 1);
        String cursor = "0000000000000000".substring(0, 16 - idCursor.length()) + idCursor;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.MOST_POPULAR.name())
            .cursor(cursor)
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        Post actualLast = getLastMostPopularPost(
            (int) (lastId - (Integer.parseInt(idCursor) - expectedSize)));
        String likeCount = String.valueOf(actualLast.getLikeCount());
        String id = String.valueOf(actualLast.getId());

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(likeCount, id);
    }

    @Test
    void 산책로_포스트가_없어도_목록_조회를_성공한다() {
        // given
        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.LATEST.name())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual.contents()).isEmpty();
        assertThat(actual.nextCursor()).isNull();
    }

    @Test
    void 조회_대상_산책로_포스트_수가_조회_사이즈보다_작으면_다음_커서가_없다() {
        // given
        int actualSize = PostTexture.getRandomInt(1, 9);

        createPostsAndGetLastId(actualSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.LATEST.name())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        List<Post> expected = postRepository.findAll();

        Collections.reverse(expected);

        assertThat(actual.contents()).hasSize(actualSize);
        assertThat(actual.nextCursor()).isNull();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void 커서가_빈_값이면_처음부터_목록_조회한다(String cursor) {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);

        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .order(OrderType.LATEST.name())
            .cursor(cursor)
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        String expectedLastCursor = getLastLatestCursor(expectedSize - 1);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 정렬_순서가_없으면_산책로_포스트를_최신순으로_조회한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);

        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(addressDepth1)
            .depth2(addressDepth2)
            .depth3(addressDepth3)
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAddress(null,
            request);

        // then
        String expectedLastCursor = getLastLatestCursor(expectedSize - 1);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 특정_사용자의_산책로_포스트_최신순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);

        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        ScrollRequest request = new ScrollRequest(OrderType.LATEST.name(), null, expectedSize);

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAuthor(null,
            user.getId(), request);

        // then
        String expectedLastCursor = getLastLatestCursor(expectedSize - 1);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).isEqualTo(expectedLastCursor);
    }

    @Test
    void 다음_커서_기반으로_특정_사용자의_산책로_포스트_최신순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);
        long lastId = createPostsAndGetLastId(totalSize);
        int expectedSize = 10;
        String idCursor = String.valueOf(lastId - expectedSize + 1);
        ScrollRequest request = new ScrollRequest(OrderType.LATEST.name(), idCursor,
            expectedSize);

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAuthor(null,
            user.getId(), request);

        // then
        String expectedLastCursor = getLastLatestCursor(
            (int) (lastId - (Integer.parseInt(idCursor) - expectedSize)));
        System.out.println(expectedLastCursor);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 특정_사용자의_산책로_포스트_인기순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);

        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        ScrollRequest request = new ScrollRequest(OrderType.MOST_POPULAR.name(), null,
            expectedSize);

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAuthor(null,
            user.getId(), request);

        // then
        Post actualLast = getLastMostPopularPost(expectedSize - 1);
        String likeCount = String.valueOf(actualLast.getLikeCount());
        String id = String.valueOf(actualLast.getId());

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(likeCount, id);
    }

    @Test
    void 다음_커서_기반으로_특정_사용자의_산책로_포스트_인기순_조회를_성공한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);
        long lastId = createPostsAndGetLastId(totalSize);
        int expectedSize = 10;
        String idCursor = String.valueOf(lastId - expectedSize + 1);
        String cursor = "0000000000000000".substring(0, 16 - idCursor.length()) + idCursor;
        ScrollRequest request = new ScrollRequest(OrderType.MOST_POPULAR.name(), cursor,
            expectedSize);

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAuthor(null,
            user.getId(), request);

        // then
        Post actualLast = getLastMostPopularPost(
            (int) (lastId - (Integer.parseInt(idCursor) - expectedSize)));
        String likeCount = String.valueOf(actualLast.getLikeCount());
        String id = String.valueOf(actualLast.getId());

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(likeCount, id);
    }

    @Test
    void 로그인_사용자와_조회_대상_사용자가_같으면_공개_여부에_관계없이_조회한다() {
        // given
        int totalPublicPostSize = PostTexture.getRandomInt(1, 7);

        createPostsAndGetLastId(totalPublicPostSize);
        postRepository.save(PostTexture.createPrivatePost(user));

        int expectedSize = 10;
        ScrollRequest request = new ScrollRequest(OrderType.LATEST.name(), null,
            expectedSize);

        // when
        ScrollResponse<SimplePostResponse> actual = postService.getScrollByAuthor(user.getId(),
            user.getId(), request);

        // then
        assertThat(actual.contents()).hasSize(totalPublicPostSize + 1);
    }

    @Test
    void 사용자를_확인할_수_없으면_특정_사용자_포스트_목록_조회를_실패한다() {
        // given
        int totalSize = PostTexture.getRandomInt(21, 99);

        createPostsAndGetLastId(totalSize);

        int expectedSize = 10;
        ScrollRequest request = new ScrollRequest(OrderType.LATEST.name(), null, expectedSize);
        long invalidId = PostTexture.getRandomId();

        // when
        ThrowingCallable getScroll = () -> postService.getScrollByAuthor(null, invalidId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getScroll)
            .withMessage(PostErrorCode.AUTHOR_NOT_FOUND.getMessage());
    }

    long createPostsAndGetLastId(int size) {
        List<Post> posts = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            posts.add(PostTexture.createPostWithAddress(user, addressDepth1, addressDepth2,
                addressDepth3));
        }

        List<Post> saved = postRepository.saveAll(posts);

        return saved.get(saved.size() - 1)
            .getId();
    }

    List<CreatePostPinRequest> createPinRequests(int size, int maxPathPoints) {
        List<CreatePostPinRequest> pinRequests = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            Coordinate coordinate = MapTexture.createPoint()
                .getCoordinate();
            KakaoPoint point = KakaoPoint.from(coordinate);
            CreatePostPinRequest createPinRequest = new CreatePostPinRequest(point, null, null);

            pinRequests.add(createPinRequest);
        }

        return pinRequests;
    }

    private String getLastLatestCursor(int skipSize) {
        List<Post> posts = postRepository.findAll();

        Collections.reverse(posts);

        return posts.stream()
            .skip(skipSize)
            .findFirst()
            .orElseThrow()
            .getId()
            .toString();
    }

    private Post getLastMostPopularPost(int skipSize) {
        List<Post> posts = postRepository.findAll();

        return posts.stream()
            .sorted(this::sortByPopularity)
            .skip(skipSize)
            .findFirst()
            .orElseThrow();
    }

    private int sortByPopularity(Post a, Post b) {
        int likeDifference = b.getLikeCount() - a.getLikeCount();

        if (likeDifference == 0) {
            return Math.toIntExact(b.getId() - a.getId());
        }

        return likeDifference;
    }

}
