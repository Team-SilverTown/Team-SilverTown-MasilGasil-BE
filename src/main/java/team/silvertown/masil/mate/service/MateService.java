package team.silvertown.masil.mate.service;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.dto.request.CreateMateParticipantRequest;
import team.silvertown.masil.mate.dto.request.CreateMateRequest;
import team.silvertown.masil.mate.dto.response.CreateMateParticipantResponse;
import team.silvertown.masil.mate.dto.response.CreateMateResponse;
import team.silvertown.masil.mate.dto.response.MateDetailResponse;
import team.silvertown.masil.mate.dto.response.ParticipantResponse;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.repository.mate.MateRepository;
import team.silvertown.masil.mate.repository.participant.MateParticipantRepository;
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
        boolean isParticipating = mateParticipantRepository.existsInSimilarTime(author,
            request.gatheringAt());

        if (isParticipating) {
            throw new BadRequestException(MateErrorCode.PARTICIPATING_AROUND_SIMILAR_TIME);
        }

        Post post = postRepository.findById(request.postId())
            .orElseThrow(getNotFoundException(MateErrorCode.POST_NOT_FOUND));
        Mate mate = createMate(author, post, request);

        createMateParticipant(author, mate, ParticipantStatus.ACCEPTED, null);

        return new CreateMateResponse(mate.getId());
    }

    @Transactional(readOnly = true)
    public MateDetailResponse getDetailById(Long id) {
        Mate mate = mateRepository.findDetailById(id)
            .orElseThrow(getNotFoundException(MateErrorCode.MATE_NOT_FOUND));
        List<ParticipantResponse> participants = mateParticipantRepository.findAllByMate(mate)
            .stream()
            .map(ParticipantResponse::from)
            .toList();

        return MateDetailResponse.from(mate, participants);
    }

    @Transactional(readOnly = true)
    public CreateMateParticipantResponse applyParticipation(
        Long userId,
        Long id,
        CreateMateParticipantRequest request
    ) {
        User user = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MateErrorCode.USER_NOT_FOUND));
        Mate mate = mateRepository.findById(id)
            .orElseThrow(getNotFoundException(MateErrorCode.MATE_NOT_FOUND));

        boolean participatesAround = mateParticipantRepository.existsInSimilarTime(user,
            mate.getGatheringAt());

        if (participatesAround) {
            throw new DuplicateResourceException(MateErrorCode.PARTICIPATING_AROUND_SIMILAR_TIME);
        }

        MateParticipant mateParticipant = createMateParticipant(user, mate,
            ParticipantStatus.REQUESTED, request.message());

        return new CreateMateParticipantResponse(mateParticipant.getId());
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

}
