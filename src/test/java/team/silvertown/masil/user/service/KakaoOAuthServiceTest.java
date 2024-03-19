package team.silvertown.masil.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import team.silvertown.masil.auth.service.KakaoOAuthService;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserErrorCode;
import team.silvertown.masil.user.service.restTemplate.RestTemplateService;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
class KakaoOAuthServiceTest {

    private static final String VALID_TOKEN = "valid token";
    private static final String VALID_PROVIDER = "kakao";

    @MockBean
    RestTemplateService restTemplateService;

    @Autowired
    KakaoOAuthService kakaoOAuthService;

    @Test
    public void 정상적으로_소셜로그인을_끝내고_response를_반환한다() throws Exception {
        //given
        String mockResponse = "{\"id\":123456, \"properties\":{\"nickname\":\"Test User\"}}";
        ResponseEntity<String> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        given(restTemplateService.requestKaKaoInfo(VALID_TOKEN)).willReturn(mockEntity);

        //when
        OAuthResponse userInfo = kakaoOAuthService.getUserInfo(VALID_TOKEN);

        //then
        assertThat(userInfo.provider()).isEqualTo(VALID_PROVIDER);
        assertThat(userInfo.providerId()).isEqualTo("123456");
    }

    @Test
    public void 잘못된_provider_id를_전달하는_경우_exception이_발생한다() throws Exception {
        //given
        String mockResponse = "{\"id\": null, \"properties\":{\"nickname\":\"Test User\"}}";
        ResponseEntity<String> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        given(restTemplateService.requestKaKaoInfo(VALID_TOKEN)).willReturn(mockEntity);

        //when, then
        assertThatThrownBy(() -> kakaoOAuthService.getUserInfo(VALID_TOKEN))
            .isInstanceOf(InvalidAuthenticationException.class)
            .hasMessage(UserErrorCode.INVALID_OAUTH2_TOKEN.getMessage());
    }

}
