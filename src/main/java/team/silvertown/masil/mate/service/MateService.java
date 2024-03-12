package team.silvertown.masil.mate.service;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.ErrorCode;
import team.silvertown.masil.common.map.KakaoPointMapper;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.dto.request.CreateRequest;
import team.silvertown.masil.mate.dto.response.CreateResponse;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.repository.MateParticipantRepository;
import team.silvertown.masil.mate.repository.MateRepository;
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
    public CreateResponse create(Long userId, CreateRequest request) {
        User author = userRepository.findById(userId)
            .orElseThrow(getNotFoundException(MateErrorCode.USER_NOT_FOUND));
        boolean isParticipating = mateParticipantRepository.existsInSimilarTime(author,
            request.gatheringAt());

        if (isParticipating) {
            throw new BadRequestException(MateErrorCode.OVERGENERATION_IN_SIMILAR_TIME);
        }

        Post post = postRepository.findById(request.postId())
            .orElseThrow(getNotFoundException(MateErrorCode.POST_NOT_FOUND));
        Mate mate = createMate(author, post, request);

        createMateParticipant(author, mate, ParticipantStatus.ACCEPTED.name());

        return new CreateResponse(mate.getId());
    }

    private Supplier<DataNotFoundException> getNotFoundException(ErrorCode errorCode) {
        return () -> new DataNotFoundException(errorCode);
    }

    private Mate createMate(User author, Post post, CreateRequest request) {
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

    private void createMateParticipant(User user, Mate mate, String status) {
        MateParticipant mateParticipant = MateParticipant.builder()
            .user(user)
            .mate(mate)
            .status(status)
            .build();

        mateParticipantRepository.save(mateParticipant);
    }

}
