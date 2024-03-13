package team.silvertown.masil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static team.silvertown.masil.texture.BaseDomainTexture.getRandomInt;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import team.silvertown.masil.user.dto.LoginResponse;
import team.silvertown.masil.user.dto.MeInfoResponse;
import team.silvertown.masil.user.dto.NicknameCheckResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.dto.OnboardRequest;
import team.silvertown.masil.user.dto.UpdateRequest;
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
    private static final String VALID_KAKAO_TOKEN = "valid token";

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

    @MockBean
    KakaoOAuthService kakaoOAuthService;

    @Nested
    class 닉네임_중복_조회_로직_테스트 {

        @Test
        public void 중복닉네임_조회시_존재하지_않는_닉네임을_조회할_경우_정상적으로_통과한다() throws Exception {
            //given
            String nickname = faker.name()
                .fullName();

            //when, then
            NicknameCheckResponse nicknameCheckResponse = userService.checkNickname(nickname);
            assertThat(nicknameCheckResponse.isDuplicated()).isFalse();
            assertThat(nicknameCheckResponse.nickname()).isEqualTo(nickname);
        }

        @Test
        public void 중복닉네임_조회시_이미_존재하는_닉네임을_조회할_경우_이미_존재하고_있음을_반환한다() throws Exception {
            //given
            String nickname = faker.name()
                .fullName();
            User user = User.builder()
                .nickname(nickname)
                .build();
            userRepository.save(user);

            //when
            NicknameCheckResponse nicknameCheckResponse = userService.checkNickname(nickname);

            //then
            assertThat(nicknameCheckResponse.isDuplicated()).isTrue();
            assertThat(nicknameCheckResponse.nickname()).isEqualTo(nickname);
        }

    }

    @Nested
    class 유저_소셜_로그인_회원가입_로직_테스트 {

        @Test
        public void 정상적으로_처음_회원가입하는_유저는_유저_정보를_저장한_후_토큰을_반환한다() throws Exception {
            //given
            OAuthResponse mockOAuthResponse = new OAuthResponse(VALID_PROVIDER, "123456");
            given(kakaoOAuthService.getUserInfo(anyString())).willReturn(mockOAuthResponse);

            //when
            LoginResponse tokenResponse = userService.login(VALID_KAKAO_TOKEN);

            //then
            assertThat(tokenProvider.validateToken(tokenResponse.token())).isTrue();
        }

        @Test
        public void 토큰인증과정에서_문제가_생기면_예외가_발생한다() throws Exception {
            //given
            given(kakaoOAuthService.getUserInfo(anyString())).willThrow(RuntimeException.class);

            //when, then
            assertThatThrownBy(() -> userService.login(VALID_KAKAO_TOKEN))
                .isInstanceOf(InvalidAuthenticationException.class)
                .hasMessage(UserErrorCode.INVALID_OAUTH2_TOKEN.getMessage());
        }

        @Test
        public void 비정상적인_provider가_제공된_경우_예외가_발생한다() throws Exception {
            //given
            OAuthResponse mockOAuthResponse = new OAuthResponse(INVALID_PROVIDER, "123456");
            given(kakaoOAuthService.getUserInfo(anyString())).willReturn(mockOAuthResponse);

            //when, then
            assertThatThrownBy(() -> userService.login(VALID_KAKAO_TOKEN))
                .isInstanceOf(InvalidAuthenticationException.class)
                .hasMessage(UserErrorCode.INVALID_PROVIDER.getMessage());
        }

    }

    @Nested
    class 유저_추가정보를_입력하는_서비스로직_테스트 {

        private static final DateTimeFormatter format = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd");
        private User unTypedUser;

        private OnboardRequest getNormalRequest() {
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
            User user = User.builder()
                .provider(Provider.KAKAO)
                .socialId(socialId)
                .build();
            UserAuthority newAuthority = UserAuthority.builder()
                .authority(Authority.RESTRICTED)
                .user(user)
                .build();
            userAuthorityRepository.save(newAuthority);
            unTypedUser = userRepository.save(user);
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
            assertThat(updatedAuthority.stream()
                .map(UserAuthority::getAuthority)
                .collect(Collectors.toList()))
                .contains(Authority.NORMAL);
        }

        @Test
        public void 성별과_운동강도를_입력하지_않은경우에도_정상적으로_업데이트된다() throws Exception {
            //given
            OnboardRequest noSexAndExerciseIntensity = new OnboardRequest(
                "nickname",
                null,
                format.format(faker.date()
                    .birthdayLocalDate(20, 40)),
                getRandomInt(170, 190),
                getRandomInt(70, 90),
                null,
                true,
                true,
                true,
                true
            );

            //when
            userService.onboard(unTypedUser.getId(), noSexAndExerciseIntensity);
            User updatedUser = userRepository.findById(unTypedUser.getId())
                .get();

            //then
            assertThat(updatedUser.getSex()).isNull();
            assertThat(updatedUser.getExerciseIntensity()).isNull();
        }

        @Test
        public void 이미_사용중인_닉네임이_라면_예외가_발생한다() throws Exception {
            //given
            User user = User.builder().nickname("nickname").build();
            userRepository.save(user);
            OnboardRequest request = getNormalRequest();
            List<UserAuthority> beforeUpdatedAuthority = userAuthorityRepository.findByUser(
                unTypedUser);
            assertThat(beforeUpdatedAuthority).hasSize(1);
            assertThat(beforeUpdatedAuthority.get(0)
                .getAuthority()).isEqualTo(Authority.RESTRICTED);

            //when, then
            assertThatThrownBy(() -> userService.onboard(unTypedUser.getId(), request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage(UserErrorCode.DUPLICATED_NICKNAME.getMessage());
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
            User user = User.builder()
                .provider(Provider.KAKAO)
                .socialId(socialId)
                .build();
            UserAuthority newAuthority = UserAuthority.builder()
                .authority(Authority.RESTRICTED)
                .user(user)
                .build();
            userAuthorityRepository.save(newAuthority);
            unTypedUser = userRepository.save(user);
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
                    me.birthDate()
                        .toString()),
                () -> assertThat(updatedUser.getHeight()).isEqualTo(me.height()),
                () -> assertThat(updatedUser.getWeight()).isEqualTo(me.weight()),
                () -> assertThat(updatedUser.getSex()
                    .name()).isEqualTo(me.sex()
                    .name()),
                () -> assertThat(updatedUser.getExerciseIntensity()
                    .name()).isEqualTo(me.exerciseIntensity()
                    .name()),
                () -> assertThat(updatedUser.getIsPublic()).isEqualTo(me.isPublic())
            );
        }

    }

    @Nested
    class 공개_비공개_여부_확인하는_테스트 {

        private User unTypedUser;

        @BeforeEach
        void setup() {
            String socialId = String.valueOf(faker.barcode());
            User user = User.builder()
                .provider(Provider.KAKAO)
                .socialId(socialId)
                .isPublic(true)
                .build();
            UserAuthority newAuthority = UserAuthority.builder()
                .authority(Authority.RESTRICTED)
                .user(user)
                .build();
            userAuthorityRepository.save(newAuthority);
            unTypedUser = userRepository.save(user);
        }

        @Test
        public void 정상적으로_공개_비공개_여부를_바꿀_수_있다() throws Exception {
            //given
            boolean beforeChangedIsPublic = unTypedUser.getIsPublic();

            //when
            userService.changePublic(unTypedUser.getId());
            User changedUser = userRepository.findById(unTypedUser.getId())
                .get();
            boolean changedIsPublic = changedUser.getIsPublic();

            //then
            assertThat(changedIsPublic).isNotEqualTo(beforeChangedIsPublic);
        }

        @Test
        public void 존재하지_않는_유저의_공개여부를_변경하려는_경우_예외가_발생한다() throws Exception {
            //given
            Long undefinedUserId = unTypedUser.getId() + 1L;

            //when, then
            assertThatThrownBy(() -> userService.changePublic(undefinedUserId))
                .isInstanceOf(DataNotFoundException.class)
                .hasMessage(UserErrorCode.USER_NOT_FOUND.getMessage());
        }

    }

    @Nested
    class 유저_정보를_수정하는_테스트 {

        private static final DateTimeFormatter format = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd");
        private User unTypedUser;

        private static UpdateRequest getNormalRequest() {
            return new UpdateRequest(
                "nickname",
                Sex.MALE.name(),
                format.format(faker.date()
                    .birthdayLocalDate(20, 40)),
                getRandomInt(170, 190),
                getRandomInt(70, 90),
                ExerciseIntensity.MIDDLE.name()
            );
        }

        @BeforeEach
        void setup() {
            String socialId = String.valueOf(faker.barcode());
            User user = User.builder()
                .provider(Provider.KAKAO)
                .isPublic(true)
                .socialId(socialId)
                .build();
            UserAuthority newAuthority = UserAuthority.builder()
                .authority(Authority.RESTRICTED)
                .user(user)
                .build();
            userAuthorityRepository.save(newAuthority);
            unTypedUser = userRepository.save(user);
        }

        @Test
        public void 정상적으로_유저정보를_내려받을_수_있다() throws Exception {
            //given
            UpdateRequest request = getNormalRequest();
            List<UserAuthority> beforeUpdatedAuthority = userAuthorityRepository.findByUser(
                unTypedUser);
            assertThat(beforeUpdatedAuthority).hasSize(1);
            assertThat(beforeUpdatedAuthority.get(0)
                .getAuthority()).isEqualTo(Authority.RESTRICTED);

            //when
            userService.updateInfo(unTypedUser.getId(), request);

            //then
            User updatedUser = userRepository.findById(unTypedUser.getId())
                .get();
            assertDoesNotThrow(() -> userService.getMe(updatedUser.getId()));
            MeInfoResponse me = userService.getMe(updatedUser.getId());
            assertAll(
                () -> assertThat(updatedUser.getNickname()).isEqualTo(me.nickname()),
                () -> assertThat(updatedUser.getBirthDate()
                    .toString()).isEqualTo(
                    me.birthDate()
                        .toString()),
                () -> assertThat(updatedUser.getHeight()).isEqualTo(me.height()),
                () -> assertThat(updatedUser.getWeight()).isEqualTo(me.weight()),
                () -> assertThat(updatedUser.getSex()
                    .name()).isEqualTo(me.sex()
                    .name()),
                () -> assertThat(updatedUser.getExerciseIntensity()
                    .name()).isEqualTo(me.exerciseIntensity()
                    .name()),
                () -> assertThat(updatedUser.getIsPublic()).isEqualTo(me.isPublic())
            );
        }

    }

}
