package team.silvertown.masil.user.service.restTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@AutoConfigureMockMvc
@DisplayNameGeneration(ReplaceUnderscores.class)
@SpringBootTest
class RestTemplateServiceTest {

    @MockBean
    RestTemplate restTemplate;

    @Autowired
    RestTemplateService restTemplateService;

    private static final String VALID_TOKEN = "valid token";
    private static final String KAKAO_USER_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";


    @Test
    public void 정상적으로_값을_받은_경우_response_반환에_성공한다() throws Exception {
        //given
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + VALID_TOKEN);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        String mockResponse = "{\"id\":123456, \"properties\":{\"nickname\":\"Test User\"}}";
        ResponseEntity<String> mockEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);
        given(restTemplate.exchange(
            KAKAO_USER_INFO_REQUEST_URL,
            HttpMethod.POST,
            accountInfoRequest,
            String.class
        )).willReturn(mockEntity);

        //when, then
        Assertions.assertDoesNotThrow(() ->restTemplateService.requestKaKaoInfo(VALID_TOKEN));
    }
}
