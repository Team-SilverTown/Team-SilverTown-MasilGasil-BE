package team.silvertown.masil.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
@Tag(name = "회원 관련 API")
public class UserController {

    private final UserService userService;

    @GetMapping("api/v1/users/check-nickname")
    @Operation(summary = "닉네임 중복 검사")
    @ApiResponse(
        responseCode = "204",
        description = "중복되는 닉네임 없음",
        content = @Content(
            mediaType = "application/json"
        )
    )
    public ResponseEntity<Void> nicknameCheck(
        @RequestParam
        String nickname
    ) {
        userService.checkNickname(nickname);
        return ResponseEntity.noContent().build();
    }

}

