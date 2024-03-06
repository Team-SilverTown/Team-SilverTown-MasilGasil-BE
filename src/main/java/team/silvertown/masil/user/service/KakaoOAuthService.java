package team.silvertown.masil.user.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import team.silvertown.masil.security.exception.InvalidAuthenticationException;
import team.silvertown.masil.user.dto.KakaoResponse;
import team.silvertown.masil.user.dto.OAuthResponse;
import team.silvertown.masil.user.exception.UserErrorCode;

@Service
@Slf4j
@RequiredArgsConstructor
public class KakaoOAuthService implements OAuthService {

    private static final String KAKAO_USER_INFO_REQUEST_URL = "https://kapi.kakao.com/v2/user/me";
    private static final String KAKAO_PROVIDER = "kakao";
    private static final String AUTHORIZATION_PREFIX = "Bearer ";

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public OAuthResponse oauthResponse(String kakaoToken) {
        return OAuthResponse.from(getUserInfo(kakaoToken));
    }

    private HashMap<String, Object> getUserInfo(String kakaoToken) {
        HashMap<String, Object> userInfo = new HashMap<>();

        ResponseEntity<String> accountInfoResponse = requestKaKaoInfo(kakaoToken, restTemplate);
        String json = accountInfoResponse.getBody();

        try {
            KakaoResponse kakaoResponse = objectMapper.readValue(json, KakaoResponse.class);
            Long id = kakaoResponse.getId();

            userInfo.put("id", id);
            userInfo.put("provider", KAKAO_PROVIDER);

            return userInfo;
        } catch (IOException e) {
            throw new InvalidAuthenticationException(UserErrorCode.INVALID_OAUTH2_TOKEN);
        }
    }

    private ResponseEntity<String> requestKaKaoInfo(
        String kakaoToken,
        RestTemplate rt
    ) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", AUTHORIZATION_PREFIX + kakaoToken);
        log.info("header check = {}", headers);
        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        return rt.exchange(
            KAKAO_USER_INFO_REQUEST_URL,
            HttpMethod.POST,
            accountInfoRequest,
            String.class
        );
    }

}
