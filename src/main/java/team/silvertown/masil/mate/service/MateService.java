package team.silvertown.masil.mate.service;

import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollRequest;
import team.silvertown.masil.common.scroll.dto.ScrollResponse;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.dto.MateCursorDto;
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
import team.silvertown.masil.mate.validator.MateValidator;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.repository.PostRepository;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class MateService {

    private final MateRepository mateRepository;
    private final MateParticipantRepository mateParticipantRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CreateMateResponse create(Long userId, CreateMateRequest request) {
        User author = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MateErrorCode.USER_NOT_FOUND));
        boolean isParticipatingAnother = mateParticipantRepository.existsInSimilarTime(author,
            request.gatheringAt());

        MateValidator.validateSoleParticipation(isParticipatingAnother);

        Post post = postRepository.findById(request.postId())
            .orElseThrow(getNotFoundException(MateErrorCode.POST_NOT_FOUND));
        Mate mate = createMate(author, post, request);

        createMateParticipant(author, mate, ParticipantStatus.ACCEPTED, null);

        return new CreateMateResponse(mate.getId());
    }

    @Transactional
    public MateDetailResponse getDetailById(Long userId, Long id) {
        Mate mate = mateRepository.findDetailById(id)
            .orElseThrow(getNotFoundException(MateErrorCode.MATE_NOT_FOUND));
        boolean isAuthor = Objects.equals(userId, mate.getAuthor().getId());
        List<ParticipantResponse> participants = mateParticipantRepository.findAllByMate(mate)
            .stream()
            .map(isAuthor ? ParticipantResponse::withMessageFrom
                : ParticipantResponse::withoutMessageFrom)
            .toList();

        mate.closeIfPassed();

        return MateDetailResponse.from(mate, participants);
    }

    @Transactional
    public ScrollResponse<SimpleMateResponse> getScrollByAddress(NormalListRequest request) {
        List<MateCursorDto> matesWithCursor = mateRepository.findScrollByAddress(request);

        return getScrollResponse(matesWithCursor, request.getSize());
    }

    @Transactional
    public ScrollResponse<SimpleMateResponse> getScrollByPost(Long postId, ScrollRequest request) {
        Post post = postRepository.findById(postId)
            .orElseThrow(getNotFoundException(MateErrorCode.POST_NOT_FOUND));
        List<MateCursorDto> matesWithCursor = mateRepository.findScrollByPost(post, request);

        return getScrollResponse(matesWithCursor, request.getSize());
    }

    @Transactional
    public CreateMateParticipantResponse applyParticipation(
        Long userId,
        Long id,
        CreateMateParticipantRequest request
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MateErrorCode.USER_NOT_FOUND));
        Mate mate = mateRepository.findById(id)
            .orElseThrow(getNotFoundException(MateErrorCode.MATE_NOT_FOUND));

        boolean isParticipatingAnother = mateParticipantRepository.existsInSimilarTime(user,
            mate.getGatheringAt());

        MateValidator.validateSoleParticipation(isParticipatingAnother);

        MateParticipant mateParticipant = createMateParticipant(user, mate,
            ParticipantStatus.REQUESTED, request.message());

        return new CreateMateParticipantResponse(mateParticipant.getId());
    }

    @Transactional
    public void acceptParticipation(Long authorId, Long id, Long participantId) {
        MateParticipant mateParticipant = mateParticipantRepository.findWithMateById(participantId)
            .orElseThrow(getNotFoundException(MateErrorCode.PARTICIPANT_NOT_FOUND));

        MateValidator.validateParticipantAcceptance(authorId, id, mateParticipant);

        boolean isParticipatingAnother = mateParticipantRepository.existsInSimilarTime(
            mateParticipant.getUser(), mateParticipant.getMate().getGatheringAt());

        MateValidator.validateSoleParticipation(isParticipatingAnother);

        mateParticipant.acceptParticipant();
    }

    @Transactional
    public void deleteParticipantById(Long userId, Long id, Long participantId) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MateErrorCode.USER_NOT_FOUND));
        MateParticipant mateParticipant = mateParticipantRepository.findById(participantId)
            .orElseThrow(getNotFoundException(MateErrorCode.PARTICIPANT_NOT_FOUND));

        MateValidator.validateParticipantDeletion(user, id, mateParticipant);

        mateParticipantRepository.delete(mateParticipant);
    }

    private Supplier<DataNotFoundException> getNotFoundException(ErrorCode errorCode) {
        return () -> new DataNotFoundException(errorCode);
    }

    private Mate createMate(User author, Post post, CreateMateRequest request) {
        Point point = KakaoPointMapper.mapToPoint(request.gatheringPlacePoint());
        Mate mate = Mate.builder()
            .author(author)
            .post(post)
            .depth1(request.depth1())
            .depth2(request.depth2())
            .depth3(request.depth3())
            .depth4(request.depth4())
            .title(request.title())
            .content(request.content())
            .gatheringPlacePoint(point)
            .gatheringPlaceDetail(request.gatheringPlaceDetail())
            .gatheringAt(request.gatheringAt())
            .capacity(request.capacity())
            .build();

        return mateRepository.save(mate);
    }

    private MateParticipant createMateParticipant(
        User user,
        Mate mate,
        ParticipantStatus status,
        String message
    ) {
        MateParticipant mateParticipant = MateParticipant.builder()
            .user(user)
            .mate(mate)
            .status(status)
            .message(message)
            .build();

        return mateParticipantRepository.save(mateParticipant);
    }

    private ScrollResponse<SimpleMateResponse> getScrollResponse(
        List<MateCursorDto> matesWithCursor,
        int size
    ) {
        List<SimpleMateResponse> mates = matesWithCursor.stream()
            .limit(size)
            .map(this::getSimpleMate)
            .toList();
        String lastCursor = getLastCursor(matesWithCursor, size);

        return ScrollResponse.from(mates, lastCursor);
    }

    private String getLastCursor(List<MateCursorDto> matesWithCursor, int size) {
        if (matesWithCursor.size() > size) {
            return matesWithCursor.get(size - 1)
                .cursor();
        }

        return null;
    }

    private SimpleMateResponse getSimpleMate(MateCursorDto mateCursorDto) {
        Mate mate = mateCursorDto.mate();

        mate.closeIfPassed();

        return SimpleMateResponse.from(mate);
    }

    private ParticipantResponse getParticipantResponse(
        boolean isAuthor,
        MateParticipant mateParticipant
    ) {
        return isAuthor ? ParticipantResponse.withMessageFrom(mateParticipant)
            : ParticipantResponse.withoutMessageFrom(mateParticipant);
    }

}
