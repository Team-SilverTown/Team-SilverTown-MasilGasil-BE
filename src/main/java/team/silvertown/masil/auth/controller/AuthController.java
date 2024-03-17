package team.silvertown.masil.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.auth.service.AuthService;
import team.silvertown.masil.auth.dto.LoginResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "토큰 관련 API")
public class AuthController {

    private static final String ACCESS_TOKEN_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN_HEADER = "Refresh-Token";

    private final AuthService authService;

    @PostMapping("/api/v1/auth/login")
    @Operation(summary = "카카오 토큰으로 로그인")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginResponse.class)
        )
    )
    @SecurityRequirements(
        @SecurityRequirement(name = "토큰 받아오기")
    )
    public ResponseEntity<LoginResponse> login(
        @RequestHeader(HttpHeaders.AUTHORIZATION)
        @Parameter(hidden = true)
        String accessToken
    ) {
        return ResponseEntity.ok(authService.login(accessToken));
    }

    @GetMapping("api/v1/users/auth/refresh")
    @Operation(summary = "리프레시 토큰으로 새 토큰 받기")
    @SecurityRequirements(
        {
            @SecurityRequirement(name = "카카오 토큰 로그인"),
            @SecurityRequirement(name = "리프레시 토큰")
        }
    )

    public ResponseEntity<Void> refresh(
        HttpServletRequest request
    ) {
        String refreshToken = request.getHeader(REFRESH_TOKEN_HEADER);
        String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String newToken = authService.refresh(refreshToken, accessToken);

        return ResponseEntity.ok()
            .header(HttpHeaders.AUTHORIZATION, ACCESS_TOKEN_PREFIX + newToken)
            .build();
    }

}
