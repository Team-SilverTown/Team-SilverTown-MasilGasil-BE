package team.silvertown.masil.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;
import javax.crypto.SecretKey;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import team.silvertown.masil.config.jwt.JwtProperties;

@Configuration
public class JwtTestConfig {

    private static final String JWT_ISSUER = "test issuer";
    private static final Long TOKEN_VALIDITY_IN_SECONDS = 1000L;

    @Bean
    public JwtProperties jwtProperties() {
        String secretWord = "secret".repeat(20);
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretWord));
        String base64Secret = Base64.getEncoder()
            .encodeToString(secretKey.getEncoded());

        return new JwtProperties(JWT_ISSUER,
            base64Secret,
            TOKEN_VALIDITY_IN_SECONDS);
    }

}
