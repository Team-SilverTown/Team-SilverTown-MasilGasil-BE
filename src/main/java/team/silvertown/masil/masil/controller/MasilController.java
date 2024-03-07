package team.silvertown.masil.masil.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.masil.dto.request.CreateRequest;
import team.silvertown.masil.masil.dto.request.PeriodRequest;
import team.silvertown.masil.masil.dto.response.CreateResponse;
import team.silvertown.masil.masil.dto.response.MasilDetailResponse;
import team.silvertown.masil.masil.dto.response.PeriodResponse;
import team.silvertown.masil.masil.dto.response.RecentMasilResponse;
import team.silvertown.masil.masil.service.MasilService;

@RestController
@RequiredArgsConstructor
@Tag(name = "마실 관련 API")
public class MasilController {

    public final MasilService masilService;


    @PostMapping("/api/v1/masils")
    @Operation(summary = "마실 생성")
    @ApiResponse(
        responseCode = "201",
        headers = {
            @Header(
                name = "해당 마실 조회 API",
                description = "/api/v1/masils/{id}"
            )
        },
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateResponse.class)
        )
    )
    public ResponseEntity<CreateResponse> create(
        @AuthenticationPrincipal
        Long userId,
        @RequestBody
        CreateRequest request
    ) {
        CreateResponse createResponse = masilService.create(userId, request);
        URI uri = URI.create("/api/v1/masils/" + createResponse.id());

        return ResponseEntity.created(uri)
            .body(createResponse);
    }

    @GetMapping("/api/v1/masils/recent")
    @Operation(summary = "최근 마실 조회")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = RecentMasilResponse.class)
        )
    )
    public ResponseEntity<RecentMasilResponse> getRecent(
        @AuthenticationPrincipal
        Long userId,
        Integer size
    ) {
        RecentMasilResponse response = masilService.getRecent(userId, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/masils/period")
    @Operation(summary = "기간 별 마실 조회")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PeriodResponse.class)
        )
    )
    public ResponseEntity<PeriodResponse> getInGivenPeriod(
        @AuthenticationPrincipal
        Long userId,
        PeriodRequest request
    ) {
        PeriodResponse response = masilService.getInGivenPeriod(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/masils/{id}")
    @Operation(summary = "마실 단일 조회")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MasilDetailResponse.class)
        )
    )
    public ResponseEntity<MasilDetailResponse> getById(
        @AuthenticationPrincipal
        Long userId,
        @PathVariable
        Long id
    ) {
        MasilDetailResponse response = masilService.getById(userId, id);

        return ResponseEntity.ok(response);
    }

}
