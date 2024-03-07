package team.silvertown.masil.user.service.restTemplate;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class RestTemplateService {

    private static final String KAKAO_USER_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private final RestTemplate restTemplate;

    public ResponseEntity<String> requestKaKaoInfo(
        String kakaoToken
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", AUTHORIZATION_PREFIX + kakaoToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        return restTemplate.exchange(
            KAKAO_USER_INFO_REQUEST_URL,
            HttpMethod.POST,
            accountInfoRequest,
            String.class
        );
    }

}
