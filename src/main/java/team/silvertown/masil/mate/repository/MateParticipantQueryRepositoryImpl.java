package team.silvertown.masil.mate.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.OffsetDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.domain.QMate;
import team.silvertown.masil.mate.domain.QMateParticipant;
import team.silvertown.masil.user.domain.User;

@Repository
@RequiredArgsConstructor
public class MateParticipantQueryRepositoryImpl implements MateParticipantQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMateParticipant mateParticipant = QMateParticipant.mateParticipant;

    @Override
    public boolean existsInSimilarTime(User user, OffsetDateTime gatheringAt) {
        QMate mate = QMate.mate;
        BooleanBuilder condition = new BooleanBuilder();
        OffsetDateTime beforeHour = gatheringAt.minusHours(1);
        OffsetDateTime afterHour = gatheringAt.plusHours(1);

        condition
            .and(mateParticipant.user.eq(user))
            .and(mateParticipant.status.eq(ParticipantStatus.ACCEPTED))
            .and(mate.gathering.gatheringAt.between(beforeHour, afterHour));

        List<MateParticipant> participants = jpaQueryFactory
            .selectFrom(mateParticipant)
            .join(mate)
            .where(condition)
            .limit(1L)
            .fetch();

        return participants.isEmpty();
    }

}
