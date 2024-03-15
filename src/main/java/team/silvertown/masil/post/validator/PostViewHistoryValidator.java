package team.silvertown.masil.post.validator;

import com.google.common.net.InetAddresses;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.post.exception.PostErrorCode;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PostViewHistoryValidator extends Validator {

    public static void ipAddress(String ipAddress) {
        throwIf(!InetAddresses.isInetAddress(ipAddress),
            () -> new BadRequestException(PostErrorCode.INVALID_IP_ADDRESS));
    }

}
