package team.silvertown.masil.common.healthy;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HealthCheckController {

    private static final String HEALTHY = "healthy";

    @GetMapping("/healthy")
    public ResponseEntity<HealthCheckResponse> checkHealthStatus() {
        return ResponseEntity.ok(new HealthCheckResponse(HEALTHY));
    }

}
