package team.silvertown.masil.texture;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.user.domain.Authority;
import team.silvertown.masil.user.domain.User;
import team.silvertown.masil.user.domain.UserAuthority;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserAuthorityTexture {

    public static UserAuthority generateRestrictAuthority(User user) {
        return UserAuthority.builder()
            .authority(Authority.RESTRICTED)
            .user(user)
            .build();
    }

    public static UserAuthority generateNormalAuthority(User user) {
        return UserAuthority.builder()
            .authority(Authority.NORMAL)
            .user(user)
            .build();
    }

}
