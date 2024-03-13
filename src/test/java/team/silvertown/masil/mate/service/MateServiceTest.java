package team.silvertown.masil.mate.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.OffsetDateTime;
import java.util.List;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.dto.request.CreateMateRequest;
import team.silvertown.masil.mate.dto.response.CreateMateResponse;
import team.silvertown.masil.mate.dto.response.MateDetailResponse;
import team.silvertown.masil.mate.dto.response.ParticipantResponse;
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
            ParticipantStatus.ACCEPTED.name());

        mateParticipantRepository.save(mateParticipant);

        CreateMateRequest request = new CreateMateRequest(post.getId(), post.getDepth1(),
            post.getDepth2(),
            post.getDepth3(), "", title, content, gatheringPlacePoint, gatheringPlaceDetail,
            gatherAt, capacity);

        // when
        ThrowingCallable create = () -> mateService.create(author.getId(), request);

        // then
        assertThatExceptionOfType(BadRequestException.class).isThrownBy(create)
            .withMessage(MateErrorCode.OVERGENERATION_IN_SIMILAR_TIME.getMessage());
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
            MateTexture.createMateParticipant(author, expected, ParticipantStatus.ACCEPTED.name()));

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

}
