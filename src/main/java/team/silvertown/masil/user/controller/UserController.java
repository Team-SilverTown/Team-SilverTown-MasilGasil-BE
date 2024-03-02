package team.silvertown.masil.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("api/v1/users/extra-info")
    public ResponseEntity<Void> onboard(
        @RequestBody
        OnboardRequest request
    ) {
        Long userId = 1L;
        userService.onboard(userId, request);
        return ResponseEntity.ok()
            .build();
    }

}

