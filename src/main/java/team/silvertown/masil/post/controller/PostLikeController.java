package team.silvertown.masil.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.post.dto.SaveLikeDto;
import team.silvertown.masil.post.service.PostLikeService;

@RestController
@RequiredArgsConstructor
@Tag(name = "산책로 좋아요 API")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @PutMapping(
        value = "/api/v1/posts/{postId}/likes",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "산책로 포스트 좋아요")
    @ApiResponses(
        {
            @ApiResponse(
                responseCode = "200",
                description = "좋아요 데이터 변경",
                content = @Content(schema = @Schema(implementation = SaveLikeDto.class))
            ),
            @ApiResponse(
                responseCode = "201",
                description = "최초 좋아요 데이터 생성",
                content = @Content(schema = @Schema(implementation = SaveLikeDto.class))
            ),
        }
    )
    public ResponseEntity<SaveLikeDto> save(
        @AuthenticationPrincipal
        Long userId,
        @PathVariable
        Long postId,
        @RequestBody
        SaveLikeDto request
    ) {
        SaveLikeDto response = postLikeService.save(userId, postId, request);

        return createResponseEntity(response.isCreated(), postId)
            .body(response);
    }

    private BodyBuilder createResponseEntity(Boolean isCreated, Long postId) {
        if (Objects.nonNull(isCreated) && isCreated) {
            URI uri = URI.create("/api/v1/posts/" + postId);

            return ResponseEntity.created(uri);
        }

        return ResponseEntity.ok();
    }

}
