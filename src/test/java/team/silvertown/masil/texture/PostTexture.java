package team.silvertown.masil.texture;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostTexture extends BaseDomainTexture {

    public static Post createValidPost() {
        String content = faker.gameOfThrones()
            .quote();
        String thumbnailUrl = MasilTexture.createUrl();

        return createPostWithOptional(content, thumbnailUrl);
    }

    public static Post createPostWithAddress(
        User user,
        String depth1,
        String depth2,
        String depth3
    ) {
        LineString path = MapTexture.createLineString(100);
        String title = getRandomSentenceWithMax(29);
        int totalTime = getRandomInt(600, 4200);

        return createPost(user, depth1, depth2, depth3, "", path, title, null, null,
            (int) path.getLength(), totalTime);
    }

    public static Post createDependentPost(User user, int pathSize) {
        String addressDepth1 = MasilTexture.createAddressDepth1();
        String addressDepth2 = MasilTexture.createAddressDepth2();
        String addressDepth3 = MasilTexture.createAddressDepth3();
        LineString path = MapTexture.createLineString(pathSize);
        String title = getRandomSentenceWithMax(29);
        int totalTime = getRandomInt(600, 4200);

        return createPost(user, addressDepth1, addressDepth2, addressDepth3, "", path, title, null,
            null, (int) path.getLength(), totalTime);
    }

    public static Post createPostWithOptional(String content, String thumbnailUrl) {
        User user = UserTexture.createValidUser();
        String addressDepth1 = MasilTexture.createAddressDepth1();
        String addressDepth2 = MasilTexture.createAddressDepth2();
        String addressDepth3 = MasilTexture.createAddressDepth3();
        LineString path = MapTexture.createLineString(10);
        String title = getRandomSentenceWithMax(29);
        int totalTime = getRandomInt(600, 4200);

        return createPost(user, addressDepth1, addressDepth2, addressDepth3, "", path, title,
            content, thumbnailUrl, (int) path.getLength(), totalTime);
    }

    public static Post createPost(
        User user,
        String depth1,
        String depth2,
        String depth3,
        String depth4,
        LineString path,
        String title,
        String content,
        String thumbnailUrl,
        Integer distance,
        Integer totalTime
    ) {
        return Post.builder()
            .user(user)
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
            .build();
    }

    public static PostPin createPostPin(Post post, Long userId) {
        String content = faker.harryPotter()
            .quote();
        String url = MasilTexture.createUrl();
        Point point = MapTexture.createPoint();

        return createPostPin(post, point, userId, content, url);
    }

    public static PostPin createPostPin(
        Post post,
        Point point,
        Long userId,
        String content,
        String thumbnailUrl
    ) {
        return PostPin.builder()
            .post(post)
            .point(point)
            .userId(userId)
            .content(content)
            .thumbnailUrl(thumbnailUrl)
            .build();
    }

    public static List<PostPin> createDependentPostPins(Post post, Long userId, int size) {
        List<PostPin> postPins = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            PostPin postPin = createPostPin(post, userId);

            postPins.add(postPin);
        }

        return postPins;
    }

}
