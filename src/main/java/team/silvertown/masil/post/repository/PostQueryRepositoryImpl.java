package team.silvertown.masil.post.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ConstructorExpression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringExpression;
import com.querydsl.core.types.dsl.StringExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.micrometer.common.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.post.domain.QPost;
import team.silvertown.masil.post.dto.PostCursorDto;
import team.silvertown.masil.post.dto.request.NormalListRequest;
import team.silvertown.masil.post.dto.request.PostOrderType;
import team.silvertown.masil.post.dto.response.SimplePostResponse;
import team.silvertown.masil.user.domain.User;

@Repository
@RequiredArgsConstructor
public class PostQueryRepositoryImpl implements PostQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QPost post = QPost.post;

    @Override
    public List<PostCursorDto> findSliceBy(User user, NormalListRequest request) {
        // TODO: 좋아요 구현 후 로그인한 사용자 본인이 좋아요한 포스트인지 쿼리 추가
        BooleanExpression cursorCondition = getCursorFilter(request.order(), request.cursor());
        BooleanBuilder condition = new BooleanBuilder()
            .and(cursorCondition)
            .and(post.isPublic.isTrue())
            .and(post.address.depth1.eq(request.depth1()))
            .and(post.address.depth2.eq(request.depth2()))
            .and(post.address.depth3.eq(request.depth3()));
        OrderSpecifier<?> orderTarget = decideOrderTarget(request.order());
        StringExpression cursor = getCursor(request.order());

        return queryPostsWith(user, cursor, condition, orderTarget, request.size());
    }

    private List<PostCursorDto> queryPostsWith(
        User user,
        StringExpression cursor,
        BooleanBuilder condition,
        OrderSpecifier<?> orderTarget,
        int size
    ) {
        return jpaQueryFactory
            .select(projectPostCursor(cursor))
            .from(post)
            .where(condition)
            .orderBy(orderTarget, post.id.desc())
            .limit(size + 1)
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
            return StringExpressions.lpad(post.likeCount.stringValue(), 5, '0')
                .concat(StringExpressions.lpad(post.id.stringValue(), 11, '0'));
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
