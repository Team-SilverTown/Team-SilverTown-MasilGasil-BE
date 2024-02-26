package team.silvertown.masil.user.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class UserServiceTest {

    private static final Faker faker = new Faker();

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Test
    public void 중복닉네임_조회시_존재하지_않는_닉네임을_조회할_경우_정상적으로_통과한다() throws Exception {
        //given
        String nickname = faker.name()
            .fullName();

        //when, then
        assertDoesNotThrow(() -> userService.doubleCheck(nickname));
    }

    @Test
    public void 중복닉네임_조회시_이미_존재하는_닉네임을_조회할_경우_예외가_발생한다() throws Exception {
        //given
        String nickname = faker.name()
            .fullName();
        User user = User.builder()
            .nickname(nickname)
            .build();

        //when
        userRepository.save(user);

        //then
        assertThatThrownBy(() -> userService.doubleCheck(nickname))
            .isInstanceOf(DuplicateResourceException.class)
            .hasMessage(UserErrorCode.DUPLICATED_NICKNAME.getMessage());
    }

}
