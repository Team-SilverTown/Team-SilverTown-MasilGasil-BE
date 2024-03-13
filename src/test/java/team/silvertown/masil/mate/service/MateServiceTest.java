package team.silvertown.masil.mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.OffsetDateTime;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollResponse;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.dto.request.CreateMateParticipantRequest;
import team.silvertown.masil.mate.dto.request.CreateMateRequest;
import team.silvertown.masil.mate.dto.response.CreateMateParticipantResponse;
import team.silvertown.masil.mate.dto.response.CreateMateResponse;
import team.silvertown.masil.mate.dto.response.MateDetailResponse;
import team.silvertown.masil.mate.dto.response.ParticipantResponse;
import team.silvertown.masil.mate.dto.response.SimpleMateResponse;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.repository.mate.MateRepository;
import team.silvertown.masil.mate.repository.participant.MateParticipantRepository;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.texture.MapTexture;
import team.silvertown.masil.texture.MateTexture;
import team.silvertown.masil.texture.PostTexture;
import team.silvertown.masil.texture.UserTexture;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@Transactional
@DisplayNameGeneration(ReplaceUnderscores.class)
class MateServiceTest {

    @Autowired
    MateService mateService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PostRepository postRepository;

    @Autowired
    MateRepository mateRepository;

    @Autowired
    MateParticipantRepository mateParticipantRepository;

    User author;
    Post post;
    String title;
    String content;
    KakaoPoint gatheringPlacePoint;
    String gatheringPlaceDetail;
    OffsetDateTime gatherAt;
    Integer capacity;

    @BeforeEach
    void setUp() {
        author = userRepository.save(UserTexture.createValidUser());
        // TODO: apply post texture
        post = postRepository.save(PostTexture.createDependentPost(author, 100));
        title = MateTexture.getRandomSentenceWithMax(30);
        content = MateTexture.getRandomSentenceWithMax(1000);
        gatheringPlacePoint = MapTexture.createKakaoPoint();
        gatheringPlaceDetail = MateTexture.getRandomSentenceWithMax(50);
        gatherAt = MateTexture.getFutureDateTime();
        capacity = MateTexture.getRandomInt(1, 10);
    }

    @Test
    void 메이트_모집_생성을_성공한다() {
        // given
        CreateMateRequest request = new CreateMateRequest(post.getId(), post.getDepth1(),
            post.getDepth2(),
            post.getDepth3(), "", title, content, gatheringPlacePoint, gatheringPlaceDetail,
            gatherAt, capacity);

        // when
        CreateMateResponse expected = mateService.create(author.getId(), request);

        // then
        Mate actual = mateRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow();
        MateParticipant actualParticipant = mateParticipantRepository.findAll()
            .stream()
            .findFirst()
            .orElseThrow();

        assertThat(expected.id()).isEqualTo(actual.getId());
        assertThat(actualParticipant)
            .hasFieldOrPropertyWithValue("user", author)
            .hasFieldOrPropertyWithValue("status", ParticipantStatus.ACCEPTED);
    }

    @Test
    void 로그인한_사용자를_확인할_수_없으면_메이트_생성을_실패한다() {
        // given
        long invalidId = MateTexture.getRandomId();
        CreateMateRequest request = new CreateMateRequest(post.getId(), post.getDepth1(),
            post.getDepth2(),
            post.getDepth3(), "", title, content, gatheringPlacePoint, gatheringPlaceDetail,
            gatherAt, capacity);

        // when
        ThrowingCallable create = () -> mateService.create(invalidId, request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
            .withMessage(MateErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 생성하려는_메이트_모집_시간의_2시간_이내에_참여하는_메이트가_있으면_생성을_실패한다() {
        // given
        User anotherUser = userRepository.save(UserTexture.createValidUser());
        Post anotherPost = postRepository.save(PostTexture.createDependentPost(anotherUser, 100));
        Mate anotherMate = mateRepository.save(
            MateTexture.createDependentMate(anotherUser, anotherPost, gatherAt.plusMinutes(30)));
        MateParticipant mateParticipant = MateTexture.createMateParticipant(author, anotherMate,
            ParticipantStatus.ACCEPTED);

        mateParticipantRepository.save(mateParticipant);

        CreateMateRequest request = new CreateMateRequest(post.getId(), post.getDepth1(),
            post.getDepth2(),
            post.getDepth3(), "", title, content, gatheringPlacePoint, gatheringPlaceDetail,
            gatherAt, capacity);

        // when
        ThrowingCallable create = () -> mateService.create(author.getId(), request);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.PARTICIPATING_AROUND_SIMILAR_TIME.getMessage());
    }

    @Test
    void 메이트의_산책로_포스트가_존재하지_않으면_메이트_생성을_실패한다() {
        // given
        long invalidId = MateTexture.getRandomId();
        CreateMateRequest request = new CreateMateRequest(invalidId, post.getDepth1(),
            post.getDepth2(),
            post.getDepth3(), "", title, content, gatheringPlacePoint, gatheringPlaceDetail,
            gatherAt, capacity);

        // when
        ThrowingCallable create = () -> mateService.create(author.getId(), request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(create)
            .withMessage(MateErrorCode.POST_NOT_FOUND.getMessage());
    }

    @Test
    void 메이트_모집_상세_조회를_성공한다() {
        // given
        Mate expected = mateRepository.save(MateTexture.createDependentMate(author, post));
        MateParticipant savedAuthor = mateParticipantRepository.save(
            MateTexture.createMateParticipant(author, expected, ParticipantStatus.ACCEPTED));

        // when
        MateDetailResponse actual = mateService.getDetailById(expected.getId());

        // then
        ParticipantResponse expectedAuthor = ParticipantResponse.from(savedAuthor);

        assertThat(actual)
            .hasFieldOrPropertyWithValue("id", expected.getId())
            .hasFieldOrPropertyWithValue("participants", List.of(expectedAuthor))
            .hasFieldOrPropertyWithValue("authorId", author.getId())
            .hasFieldOrPropertyWithValue("authorNickname", author.getNickname());
    }

    @Test
    void 메이트가_존재하지_않으면_메이트_상세_조회를_실패한다() {
        // given
        long invalidId = MateTexture.getRandomId();

        // when
        ThrowingCallable getDetailById = () -> mateService.getDetailById(invalidId);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(getDetailById)
            .withMessage(MateErrorCode.MATE_NOT_FOUND.getMessage());
    }

    @Test
<<<<<<< HEAD
    void 메이트_참여_요청을_성공한다() {
        // give
        User user = userRepository.save(UserTexture.createValidUser());
        Mate mate = mateRepository.save(MateTexture.createDependentMate(author, post));
        mateParticipantRepository.save(MateTexture.createMateParticipant(this.author, mate,
            ParticipantStatus.ACCEPTED));
        String message = MateTexture.getRandomSentenceWithMax(255);
        CreateMateParticipantRequest request = new CreateMateParticipantRequest(message);

        // when
        CreateMateParticipantResponse actual = mateService.applyParticipation(user.getId(),
            mate.getId(), request);

        // then
        MateParticipant expected = mateParticipantRepository.findById(actual.id())
            .orElseThrow();

        assertThat(actual.id()).isEqualTo(expected.getId());
        assertThat(expected.getStatus()).isEqualTo(ParticipantStatus.REQUESTED);
=======
    void 메이트_모집을_최신순_조회를_성공한다() {
        // given
        int totalSize = MateTexture.getRandomInt(21, 99);

        createMatesAndGetLastId(totalSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(post.getDepth1())
            .depth2(post.getDepth2())
            .depth3(post.getDepth3())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimpleMateResponse> actual = mateService.getScrollByAddress(request);

        // then
        String expectedLastCursor = getLastLatestCursor(expectedSize - 1);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 다음_커서_기반으로_산책로_포스트_최신순_조회를_성공한다() {
        // given
        int totalSize = MateTexture.getRandomInt(21, 99);
        long lastId = createMatesAndGetLastId(totalSize);
        int expectedSize = 10;
        String idCursor = String.valueOf(lastId - expectedSize + 1);
        NormalListRequest request = NormalListRequest.builder()
            .depth1(post.getDepth1())
            .depth2(post.getDepth2())
            .depth3(post.getDepth3())
            .cursor(idCursor)
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimpleMateResponse> actual = mateService.getScrollByAddress(request);

        // then
        String expectedLastCursor = getLastLatestCursor(
            (int) (lastId - (Integer.parseInt(idCursor) - expectedSize)));

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    @Test
    void 메이트_모집이_없어도_목록_조회를_성공한다() {
        // given
        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(post.getDepth1())
            .depth2(post.getDepth2())
            .depth3(post.getDepth3())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimpleMateResponse> actual = mateService.getScrollByAddress(request);

        // then
        assertThat(actual.isEmpty()).isTrue();
        assertThat(actual.contents()).isEmpty();
        assertThat(actual.nextCursor()).isNull();
    }

    @Test
    void 조회_대상_메이트_모집_수가_조회_사이즈보다_작으면_다음_커서가_없다() {
        // given
        int actualSize = MateTexture.getRandomInt(1, 9);

        createMatesAndGetLastId(actualSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(post.getDepth1())
            .depth2(post.getDepth2())
            .depth3(post.getDepth3())
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimpleMateResponse> actual = mateService.getScrollByAddress(request);

        // then
        assertThat(actual.contents()).hasSize(actualSize);
        assertThat(actual.nextCursor()).isNull();
>>>>>>> 4447400 (feat: 메이트 목록 조회 서비스 구현)
    }

    @ParameterizedTest
    @NullAndEmptySource
<<<<<<< HEAD
    @ValueSource(strings = " ")
    void 메세지가_빈_값이어도_메이트_참가_요청을_성공한다(String blankMessage) {
        // given
        User user = userRepository.save(UserTexture.createValidUser());
        Mate mate = mateRepository.save(MateTexture.createDependentMate(author, post));
        mateParticipantRepository.save(MateTexture.createMateParticipant(this.author, mate,
            ParticipantStatus.ACCEPTED));

        CreateMateParticipantRequest request = new CreateMateParticipantRequest(blankMessage);

        // when
        CreateMateParticipantResponse actual = mateService.applyParticipation(user.getId(),
            mate.getId(), request);

        // then
        MateParticipant expected = mateParticipantRepository.findById(actual.id())
            .orElseThrow();

        assertThat(actual.id()).isEqualTo(expected.getId());
        assertThat(expected.getStatus()).isEqualTo(ParticipantStatus.REQUESTED);
    }

    @Test
    void 로그인한_사용자가_존재하지_않으면_메이트_참가_신청을_실패한다() {
        // given
        long invalidId = MateTexture.getRandomId();
        String message = MateTexture.getRandomSentenceWithMax(255);
        CreateMateParticipantRequest request = new CreateMateParticipantRequest(message);

        // when
        ThrowingCallable apply = () -> mateService.applyParticipation(invalidId, invalidId,
            request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(apply)
            .withMessage(MateErrorCode.USER_NOT_FOUND.getMessage());
    }

    @Test
    void 메이트가_존재하지_않으면_메이트_참가_신청을_실패한다() {
        // given
        User user = userRepository.save(UserTexture.createValidUser());
        long invalidId = MateTexture.getRandomId();
        String message = MateTexture.getRandomSentenceWithMax(255);
        CreateMateParticipantRequest request = new CreateMateParticipantRequest(message);

        // when
        ThrowingCallable apply = () -> mateService.applyParticipation(user.getId(), invalidId,
            request);

        // then
        assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(apply)
            .withMessage(MateErrorCode.MATE_NOT_FOUND.getMessage());
    }

    @Test
    void 해당_메이트_모집_시간_이후_1시간_내_참여하는_메이트가_있으면_메이트_참가_요청을_실패한다() {
        // given
        User user = userRepository.save(UserTexture.createValidUser());
        Mate mate = mateRepository.save(MateTexture.createDependentMate(author, post));
        int future = MateTexture.getRandomInt(1, 60);
        Mate anotherMate = mateRepository.save(MateTexture.createDependentMate(author, post,
            mate.getGatheringAt().plusMinutes(future)));
        MateParticipant authorParticipation = MateTexture.createMateParticipant(this.author, mate,
            ParticipantStatus.ACCEPTED);
        MateParticipant anotherParticipation = MateTexture.createMateParticipant(user, anotherMate,
            ParticipantStatus.ACCEPTED);

        mateParticipantRepository.saveAll(List.of(authorParticipation, anotherParticipation));

        String message = MateTexture.getRandomSentenceWithMax(255);
        CreateMateParticipantRequest request = new CreateMateParticipantRequest(message);

        // when
        ThrowingCallable apply = () -> mateService.applyParticipation(user.getId(), mate.getId(),
            request);

        // then
        assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(apply)
            .withMessage(MateErrorCode.PARTICIPATING_AROUND_SIMILAR_TIME.getMessage());
    }

    @Test
    void 해당_메이트_모집_시간_이전_1시간_내_참여하는_메이트가_있으면_메이트_참가_요청을_실패한다() {
        // given
        User user = userRepository.save(UserTexture.createValidUser());
        Mate mate = mateRepository.save(
            MateTexture.createDependentMate(author, post, OffsetDateTime.now().plusHours(2)));
        int past = MateTexture.getRandomInt(1, 60);
        Mate anotherMate = mateRepository.save(MateTexture.createDependentMate(author, post,
            mate.getGatheringAt().minusMinutes(past)));
        MateParticipant authorParticipation = MateTexture.createMateParticipant(this.author, mate,
            ParticipantStatus.ACCEPTED);
        MateParticipant anotherParticipation = MateTexture.createMateParticipant(user, anotherMate,
            ParticipantStatus.ACCEPTED);

        mateParticipantRepository.saveAll(List.of(authorParticipation, anotherParticipation));

        String message = MateTexture.getRandomSentenceWithMax(255);
        CreateMateParticipantRequest request = new CreateMateParticipantRequest(message);

        // when
        ThrowingCallable apply = () -> mateService.applyParticipation(user.getId(), mate.getId(),
            request);

        // then
        assertThatExceptionOfType(DuplicateResourceException.class).isThrownBy(apply)
            .withMessage(MateErrorCode.PARTICIPATING_AROUND_SIMILAR_TIME.getMessage());
=======
    @ValueSource(strings = {" ", "0"})
    void 커서가_빈_값이거나_0이면_처음부터_목록_조회한다(String cursor) {
        // given
        int totalSize = MateTexture.getRandomInt(21, 99);

        createMatesAndGetLastId(totalSize);

        int expectedSize = 10;
        NormalListRequest request = NormalListRequest.builder()
            .depth1(post.getDepth1())
            .depth2(post.getDepth2())
            .depth3(post.getDepth3())
            .cursor(cursor)
            .size(expectedSize)
            .build();

        // when
        ScrollResponse<SimpleMateResponse> actual = mateService.getScrollByAddress(request);

        // then
        String expectedLastCursor = getLastLatestCursor(expectedSize - 1);

        assertThat(actual.contents()).hasSize(expectedSize);
        assertThat(actual.nextCursor()).contains(expectedLastCursor);
    }

    long createMatesAndGetLastId(int size) {
        List<Mate> mates = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            mates.add(
                MateTexture.createMateWithAddress(author, post, post.getDepth1(), post.getDepth2(),
                    post.getDepth3()));
        }

        List<Mate> saved = mateRepository.saveAll(mates);

        return saved.get(saved.size() - 1)
            .getId();
    }

    private String getLastLatestCursor(int skipSize) {
        List<Mate> mates = mateRepository.findAll();

        Collections.reverse(mates);

        return mates.stream()
            .skip(skipSize)
            .findFirst()
            .orElseThrow()
            .getId()
            .toString();
>>>>>>> 4447400 (feat: 메이트 목록 조회 서비스 구현)
    }

}
