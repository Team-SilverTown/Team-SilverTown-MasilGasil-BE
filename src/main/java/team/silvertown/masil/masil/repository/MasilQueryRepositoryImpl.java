package team.silvertown.masil.masil.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.QMasil;
import team.silvertown.masil.user.domain.User;

@Repository
public class MasilQueryRepositoryImpl implements MasilQueryRepository {

    private static final int DEFAULT_RECENT_SIZE = 10;

    private final JPAQueryFactory jpaQueryFactory;

    public MasilQueryRepositoryImpl(EntityManager entityManager) {
        this.jpaQueryFactory = new JPAQueryFactory(entityManager);
    }

    public List<Masil> findRecent(User user, Integer size) {
        QMasil masil = QMasil.masil;
        int limit = DEFAULT_RECENT_SIZE;

        if (Objects.nonNull(size) && size != 0) {
            limit = size;
        }

        return jpaQueryFactory
            .selectFrom(masil)
            .where(masil.user.eq(user))
            .limit(limit)
            .orderBy(masil.id.desc())
            .fetch();
    }

}
