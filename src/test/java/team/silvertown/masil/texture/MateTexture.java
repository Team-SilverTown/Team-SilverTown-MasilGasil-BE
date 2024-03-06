package team.silvertown.masil.texture;

import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MateTexture extends BaseDomainTexture {

    public static String createDetail() {
        return getRandomSentenceWithMax(25);
    }

    public static OffsetDateTime getPastDateTime() {
        Timestamp past = faker.date()
            .past(2, TimeUnit.MILLISECONDS);

        return past.toLocalDateTime()
            .atOffset(ZoneOffset.of("+09:00"));
    }

    public static OffsetDateTime getFutureDateTime() {
        Timestamp future = faker.date()
            .future(1, TimeUnit.MINUTES);

        return future.toLocalDateTime()
            .atOffset(ZoneOffset.of("+09:00"));
    }

    public static Mate createDependentMate(User user, Post post) {
        String addressDepth1 = MasilTexture.createAddressDepth1();
        String addressDepth2 = MasilTexture.createAddressDepth2();
        String addressDepth3 = MasilTexture.createAddressDepth3();
        String title = getRandomSentenceWithMax(30);
        String content = getRandomSentenceWithMax(10000);
        Point point = MapTexture.createPoint();
        String detail = getRandomSentenceWithMax(50);
        OffsetDateTime gatherAt = getFutureDateTime();
        int capacity = getRandomInt(1, 10);

        return createMate(user, post, addressDepth1, addressDepth2, addressDepth3, "", title,
            content, point, detail, gatherAt, capacity);
    }

    public static Mate createMate(
        User author,
        Post post,
        String depth1,
        String depth2,
        String depth3,
        String depth4,
        String title,
        String content,
        Point gatheringPlacePoint,
        String gatheringPlaceDetail,
        OffsetDateTime gatherAt,
        int capacity
    ) {
        return Mate.builder()
            .author(author)
            .post(post)
            .depth1(depth1)
            .depth2(depth2)
            .depth3(depth3)
            .depth4(depth4)
            .title(title)
            .content(content)
            .gatheringPlacePoint(gatheringPlacePoint)
            .gatheringPlaceDetail(gatheringPlaceDetail)
            .gatherAt(gatherAt)
            .capacity(capacity)
            .build();
    }

}
