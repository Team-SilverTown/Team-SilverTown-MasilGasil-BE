package team.silvertown.masil;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.silvertown.masil.config.jwt.JwtProperties;

@TestConfiguration
public class JwtTestConfig {

    private static final String JWT_ISSUER = "test issuer";
    private static final String BASE64_SECRET = "testSecrettestSecrettestSecrettestSecrettestSecrettestSecrettestSecrettestSecrettestSecrettestSecret";
    private static final Long TOKEN_VALIDITY_IN_SECONDS = 1000L;

    @Bean
    public JwtProperties jwtProperties() {
        return new JwtProperties(JWT_ISSUER,
            BASE64_SECRET,
            TOKEN_VALIDITY_IN_SECONDS);
    }

}
