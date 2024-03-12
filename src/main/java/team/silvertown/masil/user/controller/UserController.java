package team.silvertown.masil.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.dto.MeInfoResponse;
import team.silvertown.masil.user.dto.NicknameCheckResponse;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.dto.UpdateRequest;
import team.silvertown.masil.user.dto.UpdateResponse;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 관련 API")
public class UserController {

    private final UserService userService;

    @PutMapping("api/v1/users/extra-info")
    @Operation(summary = "유저 추가 정보 입력")
    @ApiResponse(
        responseCode = "204",
        description = "추가 정보 입력 성공"
    )
    public ResponseEntity<Void> onboard(
        @RequestBody
        OnboardRequest request,
        @AuthenticationPrincipal
        Long userId
    ) {
        userService.onboard(userId, request);

        return ResponseEntity.noContent()
            .build();
    }

    @GetMapping("api/v1/users/check-nickname")
    @Operation(summary = "닉네임 중복 검사")
    @ApiResponse(
        responseCode = "200",
        description = "중복되는 닉네임 없음"
    )
    public ResponseEntity<NicknameCheckResponse> nicknameCheck(
        @RequestParam
        String nickname
    ) {
        return ResponseEntity.ok(userService.checkNickname(nickname));
    }

    @GetMapping("/api/v1/users/me")
    @Operation(summary = "로그인 유저 본인 조회")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = MeInfoResponse.class)
        )
    )
    public ResponseEntity<MeInfoResponse> getMyInfo(
        @AuthenticationPrincipal
        Long memberId
    ) {
        return ResponseEntity.ok(userService.getMe(memberId));
    }

    @PostMapping("/api/v1/users/login")
    @SecurityRequirement(name = "토큰 받아오기")
    @Operation(summary = "카카오 토큰으로 로그인")
    @ApiResponse(
        responseCode = "200",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = LoginResponse.class)
        )
    )
    public ResponseEntity<LoginResponse> login(
        @RequestHeader(HttpHeaders.AUTHORIZATION)
        @Parameter(hidden = true)
        String accessToken
    ) {
        return ResponseEntity.ok(userService.login(accessToken));
    }

    @PatchMapping("/api/v1/users/is-public")
    @Operation(summary = "계정 공개여부 변경")
    @ApiResponse(
        responseCode = "204",
        description = "계정 공개/비공개 여부 변경 완료"
    )
    public ResponseEntity<Void> changePublic(
        @AuthenticationPrincipal
        Long memberId
    ) {
        userService.changePublic(memberId);

        return ResponseEntity.noContent()
            .build();
    }

    @PutMapping("/api/v1/users")
    @SecurityRequirement(name = "토큰 받아오기")
    @Operation(summary = "카카오 토큰으로 로그인")
    @ApiResponse(
        responseCode = "200",
        description = "유저 정보 업데이트 요청",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = UpdateResponse.class)
        )
    )
    public ResponseEntity<UpdateResponse> updateInfo(
        @RequestBody
        UpdateRequest updateRequest,
        @AuthenticationPrincipal
        Long memberId
    ) {
        return ResponseEntity.ok(userService.updateInfo(memberId, updateRequest));
    }

}
