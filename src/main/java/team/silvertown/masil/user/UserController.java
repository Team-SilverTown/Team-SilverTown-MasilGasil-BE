package team.silvertown.masil.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team.silvertown.masil.user.dto.UpdateInfoRequest;
import team.silvertown.masil.user.dto.UpdateInfoResponse;
import team.silvertown.masil.user.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PutMapping
    public ResponseEntity<UpdateInfoResponse> update(
        @RequestBody
        UpdateInfoRequest request
    ) {
        return ResponseEntity.ok(userService.updateInfo(1L, request));
    }

}
