package team.silvertown.masil.mate.repository;

import java.time.OffsetDateTime;
import team.silvertown.masil.user.domain.User;

public interface MateParticipantQueryRepository {

    boolean existsInSimilarTime(User user, OffsetDateTime gatheringAt);

}
