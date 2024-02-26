package team.silvertown.masil.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.dto.UpdateInfoRequest;
import team.silvertown.masil.user.dto.UpdateInfoResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UpdateInfoResponse updateInfo(long userId, UpdateInfoRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new DataNotFoundException(UserErrorCode.USER_NOT_FOUND));

        user.update(request);
        return null;
    }

}
