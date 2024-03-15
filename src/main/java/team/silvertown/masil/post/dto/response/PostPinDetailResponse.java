package team.silvertown.masil.post.dto.response;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;

@Builder
public record PostPinDetailResponse(
    long id,
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public static PostPinDetailResponse from(PostPin pin) {
        return PostPinDetailResponse.builder()
            .id(pin.getId())
            .point(pin.getSimplePoint())
            .content(pin.getContent())
            .thumbnailUrl(pin.getThumbnailUrl())
            .build();
    }

    public static List<PostPinDetailResponse> listFrom(Post post) {
        return post.getPostPins()
            .stream()
            .map(PostPinDetailResponse::from)
            .toList();
    }

}
