package team.silvertown.masil.post.dto.response;

import java.util.List;
import lombok.Builder;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.post.domain.Post;
import team.silvertown.masil.post.domain.PostPin;

@Builder
public record PinDetailResponse(
    long id,
    KakaoPoint point,
    String content,
    String thumbnailUrl
) {

    public static PinDetailResponse from(PostPin pin) {
        return PinDetailResponse.builder()
            .id(pin.getId())
            .point(pin.getSimplePoint())
            .content(pin.getContent())
            .thumbnailUrl(pin.getThumbnailUrl())
            .build();
    }

    public static List<PinDetailResponse> listFrom(Post post) {
        return post.getPostPins()
            .stream()
            .map(PinDetailResponse::from)
            .toList();
    }

}
