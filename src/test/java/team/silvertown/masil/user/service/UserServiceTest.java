package team.silvertown.masil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.core.user.OAuth2User;
import team.silvertown.masil.JwtTestConfig;
import team.silvertown.masil.OAuth2TestConfig;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;

@Import(value = {JwtTestConfig.class, OAuth2TestConfig.class})
@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class UserServiceTest {

    private static final Faker faker = new Faker();
    private static final String VALID_PROVIDER = "kakao";
    private static final String INVALID_PROVIDER = faker.animal()
        .name();
    private static final String AUTHORITY_PREFIX = "ROLE_";

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAuthorityRepository authorityRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Mock
    OAuth2User oAuth2User;


    @Test
    public void 정상적으로_처음_회원가입하는_유저는_유저_정보를_저장한_후_유저객체를_반환한다() throws Exception {
        //given
        authorityRepository.deleteAll();
        userRepository.deleteAll();

        String socialId = String.valueOf(faker.barcode());
        when(oAuth2User.getName()).thenReturn(socialId);

        //when
        User joinedUser = userService.join(oAuth2User, VALID_PROVIDER);

        //then
        assertThat(joinedUser.getSocialId()).isEqualTo(socialId);
    }

    @Test
    public void 이미_가입한_유저의_경우_유저정보만_찾은_뒤_return한다() throws Exception {
        //given
        authorityRepository.deleteAll();
        userRepository.deleteAll();

        String socialId = String.valueOf(faker.barcode());
        when(oAuth2User.getName()).thenReturn(socialId);

        User user = User.builder()
            .provider(Provider.get(VALID_PROVIDER))
            .socialId(socialId)
            .build();

        userRepository.save(user);
        List<User> beforeLogin = userRepository.findAll();

        //when
        User joinedUser = userService.join(oAuth2User, VALID_PROVIDER);
        List<User> afterLogin = userRepository.findAll();

        //then
        assertThat(joinedUser.getSocialId()).isEqualTo(socialId);
        assertThat(beforeLogin.size()).isEqualTo(afterLogin.size());
    }

    @Test
    public void 비정상적인_provider로_회원가입하는_유저는_회원가입에_실패한다() throws Exception {
        //given, when
        String socialId = String.valueOf(faker.barcode());
        when(oAuth2User.getName()).thenReturn(socialId);

        //then
        assertThatThrownBy(() -> userService.join(oAuth2User, INVALID_PROVIDER))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(UserErrorCode.INVALID_PROVIDER.getMessage());
    }

    @Test
    public void OAuth2User_객체가_null로_전달되면_예외가_발생한다() throws Exception {
        //given, when
        oAuth2User = null;

        //then
        assertThatThrownBy(() -> userService.join(oAuth2User, INVALID_PROVIDER))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(UserErrorCode.INVALID_OAUTH2_TOKEN.getMessage());
    }

    @Test
    public void OAuth2User의_name값이_null로_전달되면_예외가_발생한다() throws Exception {
        //given, when
        when(oAuth2User.getName()).thenReturn(null);

        //then
        assertThatThrownBy(() -> userService.join(oAuth2User, INVALID_PROVIDER))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(UserErrorCode.INVALID_OAUTH2_TOKEN.getMessage());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = "invalid provider")
    public void 잘못된_Provider_값이_전달되면_예외가_발생한다(String provider) throws Exception {
        //given, when,
        String socialId = String.valueOf(faker.barcode());
        when(oAuth2User.getName()).thenReturn(socialId);

        // then
        assertThatThrownBy(() -> userService.join(oAuth2User, provider))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(UserErrorCode.INVALID_PROVIDER.getMessage());
    }

    @Test
    public void 정상적으로_가입_후_첫_로그인에_성공하는_경우_restrict_권한만_가진다() throws Exception {
        //given
        String socialId = String.valueOf(faker.barcode());
        when(oAuth2User.getName()).thenReturn(socialId);

        //when
        User joinedUser = userService.join(oAuth2User, VALID_PROVIDER);
        User findUser = userRepository.findById(joinedUser.getId())
            .get();
        List<UserAuthority> userAuthorities = authorityRepository.findByUser(findUser);

        //then
        assertThat(userAuthorities).hasSize(1);
        assertThat(userAuthorities.get(0).getAuthority().name()).isEqualTo(Authority.RESTRICTED.name());
    }

    @Test
    public void 권한이_없는_유저는_로그인_중_권한_예외가_발생한다() throws Exception {
        //given
        String socialId = String.valueOf(faker.barcode());

        User user = User.builder()
            .provider(Provider.get(VALID_PROVIDER))
            .socialId(socialId)
            .build();

        User savedUser = userRepository.save(user);
        String token = tokenProvider.createToken(savedUser.getId());

        //when, then
        assertThatThrownBy(() -> userService.login(token, savedUser))
            .isInstanceOf(DataNotFoundException.class)
            .hasMessage(UserErrorCode.AUTHORITY_NOT_FOUND.getMessage());
    }

}
