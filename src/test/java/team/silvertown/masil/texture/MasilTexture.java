package team.silvertown.masil.texture;

import java.time.OffsetDateTime;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.user.domain.User;

public final class MasilTexture extends BaseDomainTexture {

    private MasilTexture() {
    }

    public static String createAddressDepth1() {
        return faker.address()
            .state();
    }

    public static String createAddressDepth2() {
        return faker.address()
            .city();
    }

    public static String createAddressDepth3() {
        return faker.address()
            .streetName();
    }

    public static Masil createValidMasil() {
        long postId = getRandomId();
        String content = faker.gameOfThrones()
            .quote();
        String thumbnailUrl = faker.internet()
            .webdomain();

        return createMasil(postId, content, thumbnailUrl);
    }

    public static Masil createMasil(Long postId, String content, String thumbnailUrl) {
        User user = UserTexture.createValidUser();
        String addressDepth1 = createAddressDepth1();
        String addressDepth2 = createAddressDepth2();
        String addressDepth3 = createAddressDepth3();
        LineString path = MapTexture.createLineString(10);
        String title = faker.book()
            .title();
        int totalTime = faker.number()
            .numberBetween(10, 70);

        return createMasil(user, postId, addressDepth1, addressDepth2, addressDepth3, "", path,
            title, content, thumbnailUrl, (int) path.getLength(), totalTime, OffsetDateTime.now());
    }

    public static Masil createMasil(
        User user,
        Long postId,
        String depth1,
        String depth2,
        String depth3,
        String depth4,
        LineString path,
        String title,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime,
        OffsetDateTime startedAt
    ) {
        return Masil.builder()
            .user(user)
            .postId(postId)
            .depth1(depth1)
            .depth2(depth2)
            .depth3(depth3)
            .depth4(depth4)
            .path(path)
            .title(title)
            .content(content)
            .thumbnailUrl(thumbnailUrl)
            .distance(distance)
            .totalTime(totalTime)
            .startedAt(startedAt)
            .build();
    }

}
