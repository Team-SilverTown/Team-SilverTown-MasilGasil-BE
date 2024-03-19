package team.silvertown.masil.mate.repository.participant;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.domain.ParticipantStatus;
import team.silvertown.masil.mate.domain.QMate;
import team.silvertown.masil.mate.domain.QMateParticipant;
import team.silvertown.masil.user.domain.QUser;
import team.silvertown.masil.user.domain.User;

@Repository
@RequiredArgsConstructor
public class MateParticipantQueryRepositoryImpl implements MateParticipantQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMateParticipant mateParticipant = QMateParticipant.mateParticipant;
    private final QMate mate = QMate.mate;

    @Override
    public boolean existsInSimilarTime(User user, OffsetDateTime gatheringAt) {
        OffsetDateTime beforeHour = gatheringAt.minusHours(1);
        OffsetDateTime afterHour = gatheringAt.plusHours(1);
        BooleanBuilder condition = new BooleanBuilder()
            .and(mateParticipant.user.eq(user))
            .and(mateParticipant.status.eq(ParticipantStatus.ACCEPTED))
            .and(mate.gathering.gatheringAt.between(beforeHour, afterHour));

        List<MateParticipant> participants = jpaQueryFactory
            .selectFrom(mateParticipant)
            .join(mateParticipant.mate, mate)
            .where(condition)
            .limit(1)
            .fetch();

        return !participants.isEmpty();
    }

    @Override
    public List<MateParticipant> findAllByMate(Mate mate) {
        QUser user = QUser.user;

        return jpaQueryFactory
            .selectFrom(mateParticipant)
            .join(mateParticipant.user, user)
            .fetchJoin()
            .where(mateParticipant.mate.eq(mate))
            .fetch();
    }

    @Override
    public Optional<MateParticipant> findByIdWithMate(Long id) {
        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(mateParticipant)
            .join(mateParticipant.mate, mate)
            .fetchJoin()
            .where(mateParticipant.id.eq(id))
            .fetchFirst());
    }

}
