package team.silvertown.masil.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void doubleCheck(String nickname) {
        if (userRepository.existsByNickname(nickname)) {
            throw new DuplicateResourceException(UserErrorCode.DUPLICATED_NICKNAME);
        }
    }

}
