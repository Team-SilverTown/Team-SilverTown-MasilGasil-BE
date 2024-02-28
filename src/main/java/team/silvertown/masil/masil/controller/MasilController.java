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
import team.silvertown.masil.masil.dto.request.CreateRequest;
import team.silvertown.masil.masil.dto.request.PeriodRequest;
import team.silvertown.masil.masil.dto.response.CreateResponse;
import team.silvertown.masil.masil.dto.response.MasilResponse;
import team.silvertown.masil.masil.dto.response.PeriodResponse;
import team.silvertown.masil.masil.dto.response.RecentMasilResponse;
import team.silvertown.masil.masil.service.MasilService;

@RestController
@RequiredArgsConstructor
public class MasilController {

    public final MasilService masilService;

    @PostMapping("/api/v1/masils")
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
    public ResponseEntity<RecentMasilResponse> getRecent(
        @AuthenticationPrincipal
        Long userId,
        Integer size
    ) {
        RecentMasilResponse response = masilService.getRecent(userId, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/masils/period")
    public ResponseEntity<PeriodResponse> getInGivenPeriod(
        @AuthenticationPrincipal
        Long userId,
        PeriodRequest request
    ) {
        PeriodResponse response = masilService.getInGivenPeriod(userId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/v1/masils/{id}")
    public ResponseEntity<MasilResponse> getById(
        @AuthenticationPrincipal
        Long userId,
        @PathVariable
        Long id
    ) {
        MasilResponse response = masilService.getById(userId, id);

        return ResponseEntity.ok(response);
    }

}
