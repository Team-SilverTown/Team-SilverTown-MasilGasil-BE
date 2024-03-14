package team.silvertown.masil.mate.repository.mate;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.QMate;
import team.silvertown.masil.user.domain.QUser;

@Repository
@RequiredArgsConstructor
public class MateQueryRepositoryImpl implements MateQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QMate mate = QMate.mate;

    @Override
    public Optional<Mate> findDetailById(Long id) {
        QUser author = new QUser("author");

        return Optional.ofNullable(jpaQueryFactory
            .selectFrom(mate)
            .join(mate.author, author)
            .fetchJoin()
            .where(mate.id.eq(id))
            .fetchFirst());
    }

}
