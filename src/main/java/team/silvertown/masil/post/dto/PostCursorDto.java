package team.silvertown.masil.post.dto;

import team.silvertown.masil.post.dto.response.SimplePostResponse;

public record PostCursorDto(
    SimplePostResponse post,
    String cursor
) {

}
