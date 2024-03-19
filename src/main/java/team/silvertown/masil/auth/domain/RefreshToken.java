package team.silvertown.masil.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "refreshToken", timeToLive = 86400 * 7)
@AllArgsConstructor
@Getter
public class RefreshToken {

    @Id
    private String refreshToken;

    private Long memberId;

}
