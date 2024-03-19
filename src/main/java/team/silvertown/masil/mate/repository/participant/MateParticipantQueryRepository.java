package team.silvertown.masil.mate.repository.participant;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.user.domain.User;

public interface MateParticipantQueryRepository {

    boolean existsInSimilarTime(User user, OffsetDateTime gatheringAt);

    List<MateParticipant> findAllByMate(Mate mate);

    Optional<MateParticipant> findByIdWithMate(Long id);

}
