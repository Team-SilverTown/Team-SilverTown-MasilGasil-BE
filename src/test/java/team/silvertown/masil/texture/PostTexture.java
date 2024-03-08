package team.silvertown.masil.texture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.LineString;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostTexture extends BaseDomainTexture {

    public static Post createValidPost() {
        String content = faker.gameOfThrones()
            .quote();
        String thumbnailUrl = MasilTexture.createUrl();

        return createPostWithOptional(content, thumbnailUrl);
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

}
