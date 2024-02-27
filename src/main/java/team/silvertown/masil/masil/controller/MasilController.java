package team.silvertown.masil.masil.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.masil.dto.CreateRequest;
import team.silvertown.masil.masil.dto.CreateResponse;
import team.silvertown.masil.masil.dto.MasilResponse;
import team.silvertown.masil.masil.dto.RecentMasilResponse;
import team.silvertown.masil.masil.service.MasilService;

@RestController
@RequiredArgsConstructor
public class MasilController {

    public final MasilService masilService;

    @PostMapping("/api/v1/masils")
    public ResponseEntity<CreateResponse> create(
        @RequestBody
        CreateRequest request
    ) {
        // TODO: Replace the temp user id to the one actual after login applied
        CreateResponse createResponse = masilService.create(2L, request);
        URI uri = URI.create("/api/v1/masils/" + createResponse.id());

        return ResponseEntity.created(uri)
            .body(createResponse);
    }

    @GetMapping("/api/v1/masils/recent")
    public ResponseEntity<RecentMasilResponse> getRecent(
        @AuthenticationPrincipal
        Long userId,
        Integer size
    ) {
        RecentMasilResponse response = masilService.getRecent(userId, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/masils/{id}")
    public ResponseEntity<MasilResponse> getById(
        @PathVariable
        Long id
    ) {
        // TODO: Replace the temp user id to the one actual after login applied
        MasilResponse response = masilService.getById(2L, id);

        return ResponseEntity.ok(response);
    }

}
