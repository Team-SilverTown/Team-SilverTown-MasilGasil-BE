package team.silvertown.masil.mate.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.mate.dto.request.CreateRequest;
import team.silvertown.masil.mate.dto.response.CreateResponse;
import team.silvertown.masil.mate.service.MateService;

@RestController
@RequiredArgsConstructor
public class MateController {

    private final MateService mateService;

    @PostMapping("/api/v1/mates")
    public ResponseEntity<CreateResponse> create(
        @AuthenticationPrincipal
        Long userId,
        @RequestBody
        CreateRequest request
    ) {
        CreateResponse response = mateService.create(userId, request);
        URI uri = URI.create("/api/v1/mates/" + response.id());

        return ResponseEntity.created(uri)
            .body(response);
    }

}
