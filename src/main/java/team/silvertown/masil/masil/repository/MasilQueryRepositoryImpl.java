package team.silvertown.masil.masil.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.QMasil;
import team.silvertown.masil.masil.dto.MasilDailyDetailDto;
import team.silvertown.masil.masil.dto.MasilDailyDto;
import team.silvertown.masil.user.domain.User;

@Repository
@RequiredArgsConstructor
public class MasilQueryRepositoryImpl implements MasilQueryRepository {

    private static final int DEFAULT_RECENT_SIZE = 10;

    private final JPAQueryFactory jpaQueryFactory;
    private final QMasil masil = QMasil.masil;

    public List<Masil> findRecent(User user, Integer size) {
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

    @Override
    public List<MasilDailyDto> findInGivenPeriod(
        User user,
        OffsetDateTime startDateTime,
        OffsetDateTime endDateTime
    ) {
        BooleanBuilder condition = new BooleanBuilder();
        StringTemplate startDate = convertToLocalDate(masil.startedAt);

        condition.and(masil.user.eq(user))
            .and(masil.startedAt.between(startDateTime, endDateTime));

        return jpaQueryFactory
            .selectFrom(masil)
            .where(condition)
            .orderBy(masil.startedAt.asc())
            .transform(
                GroupBy.groupBy(startDate)
                    .as(projectDailyDetail())
            )
            .entrySet()
            .stream()
            .map(entry -> new MasilDailyDto(entry.getKey(), entry.getValue()))
            .toList();
    }

    private StringTemplate convertToLocalDate(DateTimePath<OffsetDateTime> dateTime) {
        return Expressions.stringTemplate(
            "DATE_FORMAT({0}, '%Y-%m-%d')",
            dateTime
        );
    }

    private GroupExpression<MasilDailyDetailDto, List<MasilDailyDetailDto>> projectDailyDetail() {
        return GroupBy.list(
            Projections.constructor(
                MasilDailyDetailDto.class,
                masil.id,
                masil.address,
                masil.content,
                masil.thumbnailUrl,
                masil.distance,
                masil.totalTime,
                masil.calories
            )
        );
    }

}
