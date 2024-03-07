package team.silvertown.masil.post.dto.response;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.user.domain.User;

@Builder
public record PostDetailResponse(
    long id,
    String depth1,
    String depth2,
    String depth3,
    String depth4,
    List<KakaoPoint> path,
    String title,
    String content,
    int distance,
    int totalTime,
    boolean isPublic,
    int viewCount,
    int likeCount,
    List<PinDetailResponse> pins,
    long authorId,
    String authorName,
    String thumbnailUrl
) {

    public static PostDetailResponse from(Post post, List<PinDetailResponse> pins) {
        User author = post.getUser();

        return PostDetailResponse.builder()
            .id(post.getId())
            .depth1(post.getDepth1())
            .depth2(post.getDepth2())
            .depth3(post.getDepth3())
            .depth4(post.getDepth4())
            .path(post.getKakaoPath())
            .title(post.getTitle())
            .content(post.getContent())
            .distance(post.getDistance())
            .totalTime(post.getTotalTime())
            .isPublic(post.isPublic())
            .viewCount(post.getViewCount())
            .likeCount(post.getLikeCount())
            .pins(pins)
            .authorId(author.getId())
            .authorName(author.getNickname())
            .thumbnailUrl(post.getThumbnailUrl())
            .build();
    }

}
