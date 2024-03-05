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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.masil.dto.CreateRequest;
import team.silvertown.masil.masil.dto.CreateResponse;
import team.silvertown.masil.masil.dto.MasilResponse;
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
        @RequestBody
        CreateRequest request
    ) {
        // TODO: Replace the temp user id to the one actual after login applied
        CreateResponse createResponse = masilService.create(1L, request);
        URI uri = URI.create("/api/v1/masils/" + createResponse.id());

        return ResponseEntity.created(uri)
            .body(createResponse);
    }

    @GetMapping("/api/v1/masils/{id}")
    @Operation(summary = "마실 단일 조회")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MasilResponse.class)
        )
    )
    public ResponseEntity<MasilResponse> getById(
        @PathVariable
        Long id
    ) {
        // TODO: Replace the temp user id to the one actual after login applied
        MasilResponse response = masilService.getById(1L, id);

        return ResponseEntity.ok(response);
    }

}
