package team.silvertown.masil.post.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Value;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.post.exception.PostErrorCode;

@Value
public class SaveLikeDto {

    boolean isLike;
    boolean isCreated;

    public SaveLikeDto(Boolean isLike, boolean isCreated) {
        Validator.notNull(isLike, PostErrorCode.NULL_IS_LIKE);

        this.isLike = isLike;
        this.isCreated = isCreated;
    }

    @JsonGetter("isLike")
    public boolean isLike() {
        return isLike;
    }

    @JsonIgnore
    public boolean isCreated() {
        return isCreated;
    }

}
