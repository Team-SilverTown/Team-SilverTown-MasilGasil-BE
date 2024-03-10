package team.silvertown.masil.texture;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.masil.domain.Masil;
import team.silvertown.masil.masil.domain.MasilPin;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MasilTexture extends BaseDomainTexture {

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

    public static String createUrl() {
        return faker.internet()
            .webdomain();
    }

    public static Masil createValidMasil() {
        long postId = getRandomId();
        String content = faker.gameOfThrones()
            .quote();
        String thumbnailUrl = createUrl();

        return createMasilWithOptional(postId, content, thumbnailUrl);
    }

    public static Masil createDependentMasil(User user, int pathSize) {
        String addressDepth1 = createAddressDepth1();
        String addressDepth2 = createAddressDepth2();
        String addressDepth3 = createAddressDepth3();
        LineString path = MapTexture.createLineString(pathSize);
        int totalTime = getRandomInt(600, 4200);
        int calories = getRandomInt(100, 1500);

        return createMasil(user, null, addressDepth1, addressDepth2, addressDepth3, "", path, null,
            null, (int) path.getLength(), totalTime, calories, OffsetDateTime.now());
    }

    public static Masil createMasilWithStartedAt(User user, OffsetDateTime startedAt) {
        String addressDepth1 = createAddressDepth1();
        String addressDepth2 = createAddressDepth2();
        String addressDepth3 = createAddressDepth3();
        LineString path = MapTexture.createLineString(1000);
        int totalTime = getRandomInt(600, 4200);
        int calories = getRandomInt(100, 1500);

        return createMasil(user, null, addressDepth1, addressDepth2, addressDepth3, "", path, null,
            null, (int) path.getLength(), totalTime, calories, startedAt);
    }

    public static Masil createMasilWithOptional(Long postId, String content, String thumbnailUrl) {
        User user = UserTexture.createValidUser();
        String addressDepth1 = createAddressDepth1();
        String addressDepth2 = createAddressDepth2();
        String addressDepth3 = createAddressDepth3();
        LineString path = MapTexture.createLineString(10);
        int totalTime = getRandomInt(600, 4200);
        int calories = getRandomInt(500, 1500);

        return createMasil(user, postId, addressDepth1, addressDepth2, addressDepth3, "", path,
            content, thumbnailUrl, (int) path.getLength(), totalTime, calories,
            OffsetDateTime.now());
    }

    public static Masil createMasil(
        User user,
        Long postId,
        String depth1,
        String depth2,
        String depth3,
        String depth4,
        LineString path,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime,
        Integer calories,
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
            .content(content)
            .thumbnailUrl(thumbnailUrl)
            .distance(distance)
            .totalTime(totalTime)
            .calories(calories)
            .startedAt(startedAt)
            .build();
    }

    public static MasilPin createMasilPin(Masil masil, Long userId) {
        Point point = MapTexture.createPoint();

        return createMasilPin(masil, point, null, null, userId);
    }

    public static MasilPin createMasilPin(
        Masil masil,
        Point point,
        String content,
        String thumbnailUrl,
        Long userId
    ) {
        return MasilPin.builder()
            .userId(userId)
            .point(point)
            .content(content)
            .thumbnailUrl(thumbnailUrl)
            .masil(masil)
            .build();
    }

    public static List<MasilPin> createDependentMasilPins(Masil masil, Long userId, int size) {
        List<MasilPin> masilPins = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            MasilPin masilPin = createMasilPin(masil, userId);

            masilPins.add(masilPin);
        }

        return masilPins;
    }

}
