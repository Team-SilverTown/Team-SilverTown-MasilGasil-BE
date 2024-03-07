package team.silvertown.masil.user.dto;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static team.silvertown.masil.texture.BaseDomainTexture.getRandomInt;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.repository.UserRepository;
import team.silvertown.masil.user.service.UserService;

@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class OnboardRequestTest {

    private static final Faker faker = new Faker();

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @Nested
    class 유저_추가정보를_입력하는_서비스로직_테스트 {

        private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        private static final String VALID_NICKNAME = "VALID_NAME";
        private static final String VALID_SEX = Sex.FEMALE.name();
        private static final String VALID_BIRTHDATE = format.format(faker.date()
            .birthdayLocalDate(20, 40));
        private static final int VALID_WEIGHT = getRandomInt(140, 190);
        private static final int VALID_HEIGHT = getRandomInt(40, 100);
        private static final String VALID_EXERCISE_INTENSITY = ExerciseIntensity.MIDDLE.name();
        private static final boolean CONSENT = true;
        private static final boolean DISSENT = false;
        private User unTypedUser;

        private static OnboardRequest invalidRequest(
            String nickname,
            String sex,
            String birthDate,
            Integer height,
            Integer weight,
            String exerciseIntensity,
            Boolean isAllowingMarketing,
            Boolean isPersonalInfoConsented,
            Boolean isLocationalInfoConsented,
            Boolean isUnderAgeConsentConfirmed
        ) {
            return new OnboardRequest(
                nickname,
                sex,
                birthDate,
                height,
                weight,
                exerciseIntensity,
                isAllowingMarketing,
                isPersonalInfoConsented,
                isLocationalInfoConsented,
                isUnderAgeConsentConfirmed
            );
        }

        @BeforeEach
        void setup() {
            String socialId = String.valueOf(faker.barcode());
            User user = User.builder().provider(Provider.KAKAO).socialId(socialId).build();
            unTypedUser = userRepository.save(user);
        }

        @ParameterizedTest
        @Transactional
        @EmptySource
        @ValueSource(strings = {" ", "a", "_sjf!", "ssssssssssssssssss"})
        public void 유효하지_않은_닉네임이_들어올_시_예외를_발생시킨다(String invalidNickname) throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                invalidNickname,
                VALID_SEX,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_NICKNAME.getMessage());
        }

        @ParameterizedTest
        @Transactional
        @EmptySource
        @ValueSource(strings = {" ", "not woman man", "man", "woman", "mid"})
        public void 유효하지_않은_성별이_들어올_시_예외를_발생시킨다(String invalidSex) throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                invalidSex,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_SEX.getMessage());
        }

        @ParameterizedTest
        @Transactional
        @EmptySource
        @ValueSource(strings = {" ", "1999.01.01", "199-901-01", "1999-21-11", "1999-01-011"})
        public void 유효하지_않은_생년월일이_들어올_시_예외를_발생시킨다(String invalidBirthDate) throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                invalidBirthDate,
                VALID_HEIGHT,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_BIRTH_DATE.getMessage());
        }

        @ParameterizedTest
        @Transactional
        @ValueSource(ints = {0, -1, -123})
        public void 유효하지_않은_키_값이_들어올_시_예외를_발생시킨다(Integer invalidHeight) throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                VALID_BIRTHDATE,
                invalidHeight,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_HEIGHT.getMessage());
        }

        @ParameterizedTest
        @Transactional
        @ValueSource(ints = {0, -1, -123})
        public void 유효하지_않은_몸무게_값이_들어올_시_예외를_발생시킨다(Integer invalidWeight) throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                invalidWeight,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_WEIGHT.getMessage());
        }

        @ParameterizedTest
        @Transactional
        @EmptySource
        @ValueSource(strings = {" ", "very good", "awesome", "hahahaha"})
        public void 유효하지_않은_운동강도_값이_들어올_시_예외를_발생시킨다(String invalidExerciseIntensity)
            throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                VALID_WEIGHT,
                invalidExerciseIntensity,
                CONSENT,
                CONSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_EXERCISE_INTENSITY.getMessage());
        }

        @Test
        @Transactional
        public void 개인정보_동의항목에_동의하지않으면_예외를_발생시킨다() throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                DISSENT,
                CONSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_PERSONAL_INFO_CONSENTED.getMessage());
        }

        @Test
        @Transactional
        public void 위치정보_동의항목에_동의하지않으면_예외를_발생시킨다() throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                DISSENT,
                CONSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_LOCATION_INFO_CONSENTED.getMessage());
        }

        @Test
        @Transactional
        public void 연령정보_동의항목에_동의하지않으면_예외를_발생시킨다() throws Exception {
            //given, when, then
            assertThatThrownBy(() -> invalidRequest(
                VALID_NICKNAME,
                VALID_SEX,
                VALID_BIRTHDATE,
                VALID_HEIGHT,
                VALID_WEIGHT,
                VALID_EXERCISE_INTENSITY,
                CONSENT,
                CONSENT,
                CONSENT,
                DISSENT)
            )
                .isInstanceOf(BadRequestException.class)
                .hasMessage(UserErrorCode.INVALID_UNDER_AGE_CONSENTED.getMessage());
        }

    }

}
