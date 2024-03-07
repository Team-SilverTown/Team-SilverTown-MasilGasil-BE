package team.silvertown.masil.config.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.MacAlgorithm;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import team.silvertown.masil.user.domain.UserAuthority;
import team.silvertown.masil.user.repository.UserAuthorityRepository;
import team.silvertown.masil.user.validator.UserValidator;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String INVALID_SIGNATURE = "Invalid JWT signature.";
    private static final String EXPIRED_TOKEN = "Expired JWT token.";
    private static final String UNSUPPORTED_TOKEN = "Unsupported JWT token.";
    private static final String UNEXPECTED_TOKEN = "JWT token compact of handler are invalid.";
    private static final String USER_ID_CLAIM = "user_id";
    private static final int MILLS = 1000;

    private final long tokenValidityInMilliseconds;
    private final String issuer;
    private final MacAlgorithm algorithm;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;
    private final UserAuthorityRepository userAuthorityRepository;

    public JwtTokenProvider(
        JwtProperties jwtProperties,
        UserAuthorityRepository userAuthorityRepository
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtProperties.base64Secret());

        this.tokenValidityInMilliseconds = jwtProperties.tokenValidityInSeconds() * MILLS;
        this.issuer = jwtProperties.issuer();
        this.algorithm = Jwts.SIG.HS512;
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.jwtParser = Jwts.parser()
            .verifyWith(secretKey)
            .requireIssuer(issuer)
            .build();
        this.userAuthorityRepository = userAuthorityRepository;
    }

    public String createToken(long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        return Jwts.builder()
            .issuer(issuer)
            .issuedAt(now)
            .expiration(validity)
            .claim(USER_ID_CLAIM, userId)
            .signWith(secretKey, algorithm)
            .compact();
    }

    public Authentication getAuthentication(String token) {
        long userId = jwtParser.parseSignedClaims(token)
            .getPayload()
            .get(USER_ID_CLAIM, Long.class);

        List<UserAuthority> userAuthorities = userAuthorityRepository.findAllByUserId(userId);
        UserValidator.validateAuthority(userAuthorities);

        List<GrantedAuthority> authorities = userAuthorities.stream()
            .map(UserAuthority::getName)
            .toList();

        return new UsernamePasswordAuthenticationToken(userId, token, authorities);
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
        } catch (JwtException | IllegalArgumentException e) {
            loggingException(e);

            return false;
        }

        return true;
    }

    private void loggingException(RuntimeException e) {
        if (e instanceof SecurityException || e instanceof MalformedJwtException) {
            log.debug(INVALID_SIGNATURE, e);
        } else if (e instanceof ExpiredJwtException) {
            log.debug(EXPIRED_TOKEN, e);
        } else if (e instanceof UnsupportedJwtException) {
            log.debug(UNSUPPORTED_TOKEN, e);
        } else {
            log.debug(UNEXPECTED_TOKEN, e);
        }
    }

}
