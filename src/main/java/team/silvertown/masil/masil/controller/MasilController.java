package team.silvertown.masil.masil.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.masil.dto.CreateRequest;
import team.silvertown.masil.masil.dto.CreateResponse;
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
        CreateResponse createResponse = masilService.create(1L, request);
        URI uri = URI.create("/api/v1/masils/" + createResponse.id());

        return ResponseEntity.created(uri)
            .body(createResponse);
    }

}
