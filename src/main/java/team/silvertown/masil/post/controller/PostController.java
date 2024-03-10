package team.silvertown.masil.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.common.response.ScrollResponse;
import team.silvertown.masil.post.dto.request.CreatePostRequest;
import team.silvertown.masil.post.dto.request.NormalListRequest;
import team.silvertown.masil.post.dto.request.OrderType;
import team.silvertown.masil.post.dto.response.CreatePostResponse;
import team.silvertown.masil.post.dto.response.PostDetailResponse;
import team.silvertown.masil.post.dto.response.SimplePostResponse;
import team.silvertown.masil.post.service.PostService;

@RestController
@RequiredArgsConstructor
@Tag(name = "산책로 포스트 관련 API")
public class PostController {

    private final PostService postService;

    @PostMapping("/api/v1/posts")
    @Operation(summary = "산책로 포스트 생성")
    @ApiResponse(
        responseCode = "201",
        headers = {
            @Header(
                name = "해당 산책로 포스트 조회 API",
                description = "/api/v1/posts/{id}"
            )
        },
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreatePostResponse.class)
        )
    )
    public ResponseEntity<CreatePostResponse> create(
        @AuthenticationPrincipal
        Long userId,
        @RequestBody
        CreatePostRequest request
    ) {
        CreatePostResponse response = postService.create(userId, request);
        URI uri = URI.create("/api/v1/posts/" + response.id());

        return ResponseEntity.created(uri)
            .body(response);
    }

    @GetMapping("/api/v1/posts/{id}")
    @Operation(summary = "산책로 포스트 상세조회")
    @ApiResponse(
        responseCode = "200",
        useReturnTypeSchema = true
    )
    public ResponseEntity<PostDetailResponse> getById(
        @PathVariable
        Long id
    ) {
        PostDetailResponse response = postService.getById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/api/v1/posts", produces = "application/json")
    @Operation(summary = "산책로 목록 조회")
    @ApiResponse(responseCode = "200")
    @SecurityRequirements()
    public ResponseEntity<ScrollResponse<SimplePostResponse>> getSliceByAddress(
        @AuthenticationPrincipal
        Long userId,
        @RequestParam
        String depth1,
        @RequestParam
        String depth2,
        @RequestParam
        String depth3,
        @RequestParam(required = false, defaultValue = "LATEST")
        @Parameter(schema = @Schema(implementation = OrderType.class))
        String order,
        @RequestParam(required = false)
        String cursor,
        @RequestParam(required = false, defaultValue = "10")
        Integer size
    ) {
        NormalListRequest request = NormalListRequest.builder()
            .depth1(depth1)
            .depth2(depth2)
            .depth3(depth3)
            .order(OrderType.get(order))
            .cursor(cursor)
            .size(size)
            .build();
        ScrollResponse<SimplePostResponse> response = postService.getSliceByAddress(userId,
            request);

        return ResponseEntity.ok(response);
    }

}
