package team.silvertown.masil.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.dto.OnboardRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("api/v1/users/extra-info")
    public ResponseEntity<Void> onboard(
        @RequestBody
        OnboardRequest request,
        @AuthenticationPrincipal
        Long userId
    ) {
        userService.onboard(userId, request);
        return ResponseEntity.ok()
            .build();
    }

    @GetMapping("api/v1/users/check-nickname")
    public ResponseEntity<Void> nicknameCheck(
        @RequestParam
        String nickname
    ) {
        userService.checkNickname(nickname);
        return ResponseEntity.ok()
            .build();
    }

}

