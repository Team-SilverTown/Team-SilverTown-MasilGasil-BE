package team.silvertown.masil.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("api/v1/users/check-nickname")
    public ResponseEntity<Void> nicknameDoubleCheck(@RequestParam String nickname){
        userService.checkNickname(nickname);
        return ResponseEntity.ok().build();
    }

}

