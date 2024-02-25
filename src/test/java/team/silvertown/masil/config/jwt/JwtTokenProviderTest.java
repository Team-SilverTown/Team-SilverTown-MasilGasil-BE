package team.silvertown.masil.config.jwt;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class JwtTokenProviderTest {

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Test
    public void 정상적으로_토큰_생성이_가능하다() throws Exception {
        //given
        Long userId = 1L;

        //when
        String token = jwtTokenProvider.createToken(userId);

        //then
        Long decodedUserId = (Long) jwtTokenProvider.getAuthentication(token)
            .getPrincipal();
        assertThat(userId).isEqualTo(decodedUserId);

    }

}
