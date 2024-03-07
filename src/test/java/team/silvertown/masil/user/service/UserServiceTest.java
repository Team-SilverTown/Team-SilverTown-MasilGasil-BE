package team.silvertown.masil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static team.silvertown.masil.texture.BaseDomainTexture.getRandomInt;

import java.time.format.DateTimeFormatter;
import java.util.List;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.DataNotFoundException;
import team.silvertown.masil.common.exception.DuplicateResourceException;
import team.silvertown.masil.config.jwt.JwtTokenProvider;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.dto.MeInfoResponse;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.repository.UserRepository;

@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
@Transactional
class UserServiceTest {

    private static final Faker faker = new Faker();
    private static final String VALID_PROVIDER = "kakao";
    private static final String INVALID_PROVIDER = faker.animal()
        .name();

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAuthorityRepository authorityRepository;

    @Autowired
    JwtTokenProvider tokenProvider;

    @Autowired
    UserAuthorityRepository userAuthorityRepository;

    @Mock
    OAuth2User oAuth2User;

    @Nested
    class 닉네임_중복_조회_로직_테스트 {

        @Test
        public void 중복닉네임_조회시_존재하지_않는_닉네임을_조회할_경우_정상적으로_통과한다() throws Exception {
            //given
            String nickname = faker.name()
                .fullName();

            //when, then
            assertDoesNotThrow(() -> userService.checkNickname(nickname));
        }

        @Test
        public void 중복닉네임_조회시_이미_존재하는_닉네임을_조회할_경우_예외가_발생한다() throws Exception {
            //given
            String nickname = faker.name()
                .fullName();
            User user = User.builder()
                .nickname(nickname)
                .build();
            userRepository.save(user);

            //when, then
            assertThatThrownBy(() -> userService.checkNickname(nickname))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(UserErrorCode.DUPLICATED_NICKNAME.getMessage());
        }

    }

    @Nested
    class 유저_소셜_로그인_회원가입_로직_테스트 {

        @Test
        public void 정상적으로_처음_회원가입하는_유저는_유저_정보를_저장한_후_유저객체를_반환한다() throws Exception {
            //given
            authorityRepository.deleteAll();
            userRepository.deleteAll();

            String socialId = String.valueOf(faker.barcode());
            given(oAuth2User.getName()).willReturn(socialId);

            //when
            User joinedUser = userService.join(oAuth2User, VALID_PROVIDER);

            //then
            assertThat(joinedUser.getSocialId()).isEqualTo(socialId);
        }

        @Test
        public void 이미_가입한_유저의_경우_유저정보만_찾은_뒤_반환한다() throws Exception {
            //given
            authorityRepository.deleteAll();
            userRepository.deleteAll();

            String socialId = String.valueOf(faker.barcode());
            given(oAuth2User.getName()).willReturn(socialId);

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
            //given
            String socialId = String.valueOf(faker.barcode());
            given(oAuth2User.getName()).willReturn(socialId);

            //when, then
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
            given(oAuth2User.getName()).willReturn(socialId);

            //when
            User joinedUser = userService.join(oAuth2User, VALID_PROVIDER);
            User findUser = userRepository.findById(joinedUser.getId())
                .get();
            List<UserAuthority> userAuthorities = authorityRepository.findByUser(findUser);

            //then
            assertThat(userAuthorities).hasSize(1);
            assertThat(userAuthorities.get(0)
                .getAuthority()
                .name()).isEqualTo(Authority.RESTRICTED.name());
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

        @Nested
        class 유저_추가정보를_입력하는_서비스로직_테스트 {

            private static final DateTimeFormatter format = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd");
            private User unTypedUser;

            private static OnboardRequest getNormalRequest() {
                return new OnboardRequest(
                    "nickname",
                    Sex.MALE.name(),
                    format.format(faker.date()
                        .birthdayLocalDate(20, 40)),
                    getRandomInt(170, 190),
                    getRandomInt(70, 90),
                    ExerciseIntensity.MIDDLE.name(),
                    true,
                    true,
                    true,
                    true
                );
            }

            @BeforeEach
            void setup() {
                String socialId = String.valueOf(faker.barcode());
                given(oAuth2User.getName()).willReturn(socialId);
                unTypedUser = userService.join(oAuth2User, "kakao");
            }

            @Test
            public void 정상적으로_추가정보를_작성한_경우_회원정보가_제대로_업데이트_되고_모든_서비스를_이용할_수_있다() throws Exception {
                //given
                OnboardRequest request = getNormalRequest();
                List<UserAuthority> beforeUpdatedAuthority = userAuthorityRepository.findByUser(
                    unTypedUser);
                assertThat(beforeUpdatedAuthority).hasSize(1);
                assertThat(beforeUpdatedAuthority.get(0)
                    .getAuthority()).isEqualTo(Authority.RESTRICTED);

                //when
                userService.onboard(unTypedUser.getId(), request);

                //then
                User updatedUser = userRepository.findById(unTypedUser.getId())
                    .get();
                assertAll(
                    () -> assertThat(updatedUser.getNickname()).isEqualTo(request.nickname()),
                    () -> assertThat(updatedUser.getBirthDate()
                        .toString()).isEqualTo(
                        request.birthDate()),
                    () -> assertThat(updatedUser.getHeight()).isEqualTo(request.height()),
                    () -> assertThat(updatedUser.getWeight()).isEqualTo(request.weight()),
                    () -> assertThat(updatedUser.getSex()
                        .name()).isEqualTo(request.sex()),
                    () -> assertThat(updatedUser.getExerciseIntensity()
                        .name()).isEqualTo(request.exerciseIntensity())
                );
                List<UserAuthority> updatedAuthority = userAuthorityRepository.findByUser(
                    unTypedUser);
                assertThat(updatedAuthority).hasSize(2);
                assertThat(updatedAuthority.get(1)
                    .getAuthority()).isEqualTo(Authority.NORMAL);

            }

        }

        @Nested
        class 유저정보를_내려받는_테스트 {

            private static final DateTimeFormatter format = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd");
            private User unTypedUser;

            private static OnboardRequest getNormalRequest() {
                return new OnboardRequest(
                    "nickname",
                    Sex.MALE.name(),
                    format.format(faker.date()
                        .birthdayLocalDate(20, 40)),
                    getRandomInt(170, 190),
                    getRandomInt(70, 90),
                    ExerciseIntensity.MIDDLE.name(),
                    true,
                    true,
                    true,
                    true
                );
            }

            @BeforeEach
            void setup() {
                String socialId = String.valueOf(faker.barcode());
                given(oAuth2User.getName()).willReturn(socialId);
                unTypedUser = userService.join(oAuth2User, "kakao");
            }


            @Test
            public void 정상적으로_유저정보를_내려받을_수_있다() throws Exception {
                //given
                OnboardRequest request = getNormalRequest();
                List<UserAuthority> beforeUpdatedAuthority = userAuthorityRepository.findByUser(
                    unTypedUser);
                assertThat(beforeUpdatedAuthority).hasSize(1);
                assertThat(beforeUpdatedAuthority.get(0)
                    .getAuthority()).isEqualTo(Authority.RESTRICTED);

                //when
                userService.onboard(unTypedUser.getId(), request);

                //then
                User updatedUser = userRepository.findById(unTypedUser.getId())
                    .get();
                assertDoesNotThrow(() -> userService.getMe(updatedUser.getId()));
                MeInfoResponse me = userService.getMe(updatedUser.getId());
                assertAll(
                    () -> assertThat(updatedUser.getNickname()).isEqualTo(me.nickname()),
                    () -> assertThat(updatedUser.getBirthDate()
                        .toString()).isEqualTo(
                        me.birthDate().toString()),
                    () -> assertThat(updatedUser.getHeight()).isEqualTo(me.height()),
                    () -> assertThat(updatedUser.getWeight()).isEqualTo(me.weight()),
                    () -> assertThat(updatedUser.getSex()
                        .name()).isEqualTo(me.sex().name()),
                    () -> assertThat(updatedUser.getExerciseIntensity()
                        .name()).isEqualTo(me.exerciseIntensity().name())
                );
            }

        }

    }

}
