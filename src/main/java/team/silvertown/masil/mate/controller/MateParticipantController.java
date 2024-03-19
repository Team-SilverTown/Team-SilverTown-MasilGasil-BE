package team.silvertown.masil.mate.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.mate.dto.request.CreateMateParticipantRequest;
import team.silvertown.masil.mate.dto.response.CreateMateParticipantResponse;
import team.silvertown.masil.mate.service.MateService;

@RestController
@RequiredArgsConstructor
@Tag(name = "메이트 참여자 신청 관련 API")
public class MateParticipantController {

    private final MateService mateService;

    @PostMapping("/api/v1/mates/{id}/participants")
    @Operation(summary = "메이트 참여 신청")
    @ApiResponse(
        responseCode = "201",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = CreateMateParticipantResponse.class)
        )
    )
    public ResponseEntity<CreateMateParticipantResponse> applyParticipation(
        @AuthenticationPrincipal
        Long userId,
        @PathVariable
        Long id,
        @RequestBody
        CreateMateParticipantRequest request
    ) {
        CreateMateParticipantResponse response = mateService.applyParticipation(
            userId, id, request);
        URI uri = URI.create("/api/v1/mates/" + id);

        return ResponseEntity.created(uri)
            .body(response);
    }

    @PutMapping("/api/v1/mates/{id}/participants/{participantId}")
    @Operation(summary = "메이트 참여 요청 수락")
    @ApiResponse(
        responseCode = "204",
        description = "메이트 참여 요청 수락 성공"
    )
    public ResponseEntity<Void> acceptParticipant(
        @AuthenticationPrincipal
        Long authorId,
        @PathVariable
        Long id,
        @PathVariable
        Long participantId
    ) {
        mateService.acceptParticipation(authorId, id, participantId);

        return ResponseEntity.noContent()
            .build();
    }

}
