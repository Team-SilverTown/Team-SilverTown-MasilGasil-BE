package team.silvertown.masil.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.dto.LoginRequest;
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/v1/users/login")
    public ResponseEntity<LoginResponse> login(
        @RequestBody
        LoginRequest request
    ) {
        return ResponseEntity.ok(userService.login(request.accessToken()));
    }

    @GetMapping("/api/v1/users/check-nickname")
    public ResponseEntity<Void> nicknameCheck(
        @RequestParam
        String nickname
    ) {
        userService.checkNickname(nickname);
        return ResponseEntity.ok()
            .build();
    }

}
