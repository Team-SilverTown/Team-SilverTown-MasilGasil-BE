package team.silvertown.masil.post.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import team.silvertown.masil.post.exception.PostErrorCode;
import team.silvertown.masil.post.validator.PostViewHistoryValidator;

@RedisHash(value = "POST-VIEW", timeToLive = 60L * 60)
@Getter
@NoArgsConstructor
public class PostViewHistory {

    private static final String KEY_FORMAT = "%d-%s";

    @Id
    private String key;

    public PostViewHistory(Long postId, String ipAddress) {
        PostViewHistoryValidator.notNull(postId, PostErrorCode.POST_NOT_FOUND);
        PostViewHistoryValidator.ipAddress(ipAddress);

        this.key = KEY_FORMAT.formatted(postId, ipAddress);
    }

}
