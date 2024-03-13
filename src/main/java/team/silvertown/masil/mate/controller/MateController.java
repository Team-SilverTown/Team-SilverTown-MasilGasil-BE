package team.silvertown.masil.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.common.scroll.dto.NormalListRequest;
import team.silvertown.masil.common.scroll.dto.ScrollResponse;
import team.silvertown.masil.mate.dto.request.CreateMateRequest;
import team.silvertown.masil.mate.dto.response.CreateMateResponse;
import team.silvertown.masil.mate.dto.response.MateDetailResponse;
import team.silvertown.masil.mate.dto.response.SimpleMateResponse;
import team.silvertown.masil.mate.service.MateService;

@RestController
@RequiredArgsConstructor
@Tag(name = "메이트 모집 관련 API")
public class MateController {

    private final MateService mateService;

    @PostMapping("/api/v1/mates")
    @Operation(summary = "메이트 모집 생성")
    @ApiResponse(
        responseCode = "201",
        headers = @Header(
            name = "해당 메이트 모집 조회 API",
            description = "/api/mates/{id}"
        ),
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateMateResponse.class)
        )
    )
    public ResponseEntity<CreateMateResponse> create(
        @AuthenticationPrincipal
        Long userId,
        @RequestBody
        CreateMateRequest request
    ) {
        CreateMateResponse response = mateService.create(userId, request);
        URI uri = URI.create("/api/v1/mates/" + response.id());

        return ResponseEntity.created(uri)
            .body(response);
    }

    @GetMapping("/api/v1/mates/{id}")
    @Operation(summary = "메이트 상세 조회")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MateDetailResponse.class)
        )
    )
    @SecurityRequirements
    public ResponseEntity<MateDetailResponse> getDetailById(
        @PathVariable
        Long id
    ) {
        MateDetailResponse response = mateService.getDetailById(id);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/mates")
    @Operation(summary = "메이트 목록 조회")
    @ApiResponse(responseCode = "200")
    @Parameters(
        {
            @Parameter(
                name = "depth1",
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string")
            ),
            @Parameter(
                name = "depth2",
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string")
            ),
            @Parameter(
                name = "depth3",
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string")
            ),
            @Parameter(
                name = "cursor",
                in = ParameterIn.QUERY,
                schema = @Schema(type = "string")
            ),
            @Parameter(
                name = "size",
                in = ParameterIn.QUERY,
                schema = @Schema(type = "integer", format = "int32", defaultValue = "10")
            )
        }
    )
    @SecurityRequirements
    public ResponseEntity<ScrollResponse<SimpleMateResponse>> getScrollBy(
        @RequestParam(required = false)
        Long postId,
        @Parameter(hidden = true)
        NormalListRequest request
    ) {
        ScrollResponse<SimpleMateResponse> response = getScrollResponse(postId, request);

        return ResponseEntity.ok(response);
    }

    private ScrollResponse<SimpleMateResponse> getScrollResponse(
        Long postId,
        NormalListRequest request
    ) {
        if (Objects.isNull(postId)) {
            return mateService.getScrollByAddress(request);
        }

        return mateService.getScrollByPost(postId, request.getScrollRequest());
    }

}
