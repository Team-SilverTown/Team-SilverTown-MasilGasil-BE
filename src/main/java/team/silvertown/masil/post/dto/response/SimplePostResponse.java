package team.silvertown.masil.post.dto.response;

import lombok.Getter;
import team.silvertown.masil.common.map.Address;

@Getter
public final class SimplePostResponse {

    private final Long id;
    private final Address address;
    private final String title;
    private final String content;
    private final Integer totalTime;
    private final Integer distance;
    private final Integer viewCount;
    private final Integer likeCount;
    private final String thumbnailUrl;
    private final boolean isLiked;
    private final boolean hasMate;

    public SimplePostResponse(
        Long id,
        Address address,
        String title,
        String content,
        Integer totalTime,
        Integer distance,
        Integer viewCount,
        Integer likeCount,
        String thumbnailUrl
    ) {
        this(id, address, title, content, totalTime, distance, viewCount, likeCount, thumbnailUrl,
            false);
    }

    public SimplePostResponse(
        Long id,
        Address address,
        String title,
        String content,
        Integer totalTime,
        Integer distance,
        Integer viewCount,
        Integer likeCount,
        String thumbnailUrl,
        boolean isLiked
    ) {
        this.id = id;
        this.address = address;
        this.title = title;
        this.content = content;
        this.totalTime = totalTime;
        this.distance = distance;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.thumbnailUrl = thumbnailUrl;
        this.isLiked = isLiked;
        this.hasMate = false;
    }

}
