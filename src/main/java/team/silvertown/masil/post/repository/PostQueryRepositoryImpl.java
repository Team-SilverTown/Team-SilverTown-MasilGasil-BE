package team.silvertown.masil.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.common.response.ScrollRequest;
import team.silvertown.masil.post.domain.QPost;
import team.silvertown.masil.post.dto.PostCursorDto;
import team.silvertown.masil.post.dto.request.NormalListRequest;
import team.silvertown.masil.post.dto.request.PostOrderType;
import team.silvertown.masil.post.dto.response.SimplePostResponse;
import team.silvertown.masil.user.domain.User;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private static final int LIKE_COUNT_CURSOR_LENGTH = 5;
    private static final int ID_CURSOR_LENGTH = 11;
    private static final char PADDING = '0';

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;

    @Override
    public List<PostCursorDto> findScrollByAddress(User user, NormalListRequest request) {
        // TODO: 좋아요 구현 후 로그인한 사용자 본인이 좋아요한 포스트인지 쿼리 추가
        Predicate openness = getOpenness(user, null);
        BooleanBuilder condition = getBasicCondition(request.getScrollRequest(), openness);

        condition
            .and(post.address.depth1.eq(request.getDepth1()))
            .and(post.address.depth2.eq(request.getDepth2()))
            .and(post.address.depth3.eq(request.getDepth3()));

        return queryPostsWith(user, condition, request.getScrollRequest());
    }

    @Override
    public List<PostCursorDto> findScrollByUser(
        User loginUser,
        User author,
        ScrollRequest request
    ) {
        Predicate openness = getOpenness(loginUser, author);
        BooleanBuilder condition = getBasicCondition(request, openness);

        return queryPostsWith(loginUser, condition, request);
    }

    private Predicate getOpenness(User loginUser, User author) {
        if (Objects.nonNull(loginUser) && Objects.equals(loginUser, author)) {
            return null;
        }

        return post.isPublic.isTrue();
    }

    private BooleanBuilder getBasicCondition(ScrollRequest request, Predicate openness) {
        BooleanExpression cursorCondition = getCursorFilter(request.getOrder(),
            request.getCursor());

        return new BooleanBuilder()
            .and(cursorCondition)
            .and(openness);
    }

    private List<PostCursorDto> queryPostsWith(
        User user,
        BooleanBuilder condition,
        ScrollRequest request
    ) {
        OrderSpecifier<?> orderTarget = decideOrderTarget(request.getOrder());
        StringExpression cursor = getCursor(request.getOrder());

        return jpaQueryFactory
            .select(projectPostCursor(cursor))
            .from(post)
            .where(condition)
            .orderBy(orderTarget, post.id.desc())
            .limit(request.getSize() + 1)
            .fetch();
    }

    private BooleanExpression getCursorFilter(PostOrderType order, String cursor) {
        if (StringUtils.isBlank(cursor)) {
            return null;
        }

        if (PostOrderType.isMostPopular(order)) {
            StringExpression toScan = getCursor(order);

            return toScan.lt(cursor);
        }

        long idCursor = Long.parseLong(cursor);

        if (idCursor == 0) {
            return null;
        }

        return post.id.lt(idCursor);
    }

    private OrderSpecifier<?> decideOrderTarget(PostOrderType order) {
        if (PostOrderType.isMostPopular(order)) {
            return post.likeCount.desc();
        }

        return new OrderSpecifier<>(Order.ASC, Expressions.nullExpression());

    }

    private StringExpression getCursor(PostOrderType order) {
        if (PostOrderType.isMostPopular(order)) {
            return StringExpressions.lpad(post.likeCount.stringValue(), LIKE_COUNT_CURSOR_LENGTH,
                    PADDING)
                .concat(StringExpressions.lpad(post.id.stringValue(), ID_CURSOR_LENGTH, PADDING));
        }

        return post.id.stringValue();
    }

    private ConstructorExpression<PostCursorDto> projectPostCursor(StringExpression cursor) {
        return Projections.constructor(
            PostCursorDto.class,
            projectSimplePost(),
            cursor
        );
    }

    private ConstructorExpression<SimplePostResponse> projectSimplePost() {
        return Projections.constructor(
            SimplePostResponse.class,
            post.id,
            post.address,
            post.title,
            post.content,
            post.totalTime,
            post.distance,
            post.viewCount,
            post.likeCount,
            post.thumbnailUrl
        );
    }

}
