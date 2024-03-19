package team.silvertown.masil.mate.repository.mate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollRequest;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.QMate;
import team.silvertown.masil.mate.dto.MateCursorDto;
import team.silvertown.masil.mate.dto.response.SimpleMateResponse;
import team.silvertown.masil.post.domain.Post;
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

    @Override
    public List<MateCursorDto> findScrollByAddress(NormalListRequest request) {
        BooleanBuilder condition = getBasicCondition(request.getScrollRequest());

        if (request.isBasedOnAddress()) {
            condition
                .and(mate.address.depth1.eq(request.getDepth1()))
                .and(mate.address.depth2.eq(request.getDepth2()))
                .and(mate.address.depth3.eq(request.getDepth3()));
        }

        return queryMatesWith(condition, request.getSize());
    }

    @Override
    public List<MateCursorDto> findScrollByPost(Post post, ScrollRequest request) {
        BooleanBuilder condition = getBasicCondition(request)
            .and(mate.post.eq(post));

        return queryMatesWith(condition, request.getSize());
    }

    private BooleanBuilder getBasicCondition(ScrollRequest request) {
        return new BooleanBuilder()
            .and(getCursorFilter(request.getCursor()));
    }

    private BooleanExpression getCursorFilter(String cursor) {
        if (StringUtils.isBlank(cursor) || cursor.equals("0")) {
            return null;
        }

        return mate.id.lt(Long.parseLong(cursor));
    }

    private List<MateCursorDto> queryMatesWith(BooleanBuilder condition, int size) {
        QUser author = new QUser("author");

        return jpaQueryFactory
            .select(projectMateCursor(mate.id.stringValue()))
            .from(mate)
            .join(mate.author, author)
            .where(condition)
            .orderBy(mate.id.desc())
            .limit(size + 1)
            .fetch();
    }

    private ConstructorExpression<MateCursorDto> projectMateCursor(StringExpression cursor) {
        return Projections.constructor(
            MateCursorDto.class,
            projectSimpleMate(),
            cursor
        );
    }

    private ConstructorExpression<SimpleMateResponse> projectSimpleMate() {
        return Projections.constructor(
            SimpleMateResponse.class,
            mate.id,
            mate.title,
            mate.gathering.gatheringAt,
            mate.status,
            mate.capacity,
            mate.author.id,
            mate.author.nickname,
            mate.author.profileImg
        );
    }

}
