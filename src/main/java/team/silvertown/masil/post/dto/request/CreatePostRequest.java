package team.silvertown.masil.post.dto.request;

import java.util.List;
import team.silvertown.masil.common.map.KakaoPoint;
import team.silvertown.masil.common.map.MapErrorCode;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.validator.PostValidator;

public record CreatePostRequest(
    String depth1,
    String depth2,
    String depth3,
    String depth4,
    List<KakaoPoint> path,
    String title,
    String content,
    Integer distance,
    Integer totalTime,
    Boolean isPublic,
    List<CreatePostPinRequest> pins,
    String thumbnailUrl
) {

    private static final int MIN_POINT_NUM = 3;

    public CreatePostRequest {
        PostValidator.notBlank(depth1, MapErrorCode.BLANK_DEPTH1);
        PostValidator.notNull(depth2, MapErrorCode.NULL_DEPTH2);
        PostValidator.notBlank(depth3, MapErrorCode.BLANK_DEPTH3);
        PostValidator.notNull(depth4, MapErrorCode.NULL_DEPTH4);
        PostValidator.validateTitle(title);
        PostValidator.validateUrl(thumbnailUrl);
        PostValidator.notNull(distance, PostErrorCode.NON_POSITIVE_DISTANCE);
        PostValidator.notNull(totalTime, PostErrorCode.NON_POSITIVE_TOTAL_TIME);
        PostValidator.notNull(path, MapErrorCode.NULL_PATH);
        PostValidator.notUnder(path.size(), MIN_POINT_NUM, MapErrorCode.INSUFFICIENT_PATH_POINTS);
        PostValidator.notNull(isPublic, PostErrorCode.NULL_IS_PUBLIC);
    }

}
