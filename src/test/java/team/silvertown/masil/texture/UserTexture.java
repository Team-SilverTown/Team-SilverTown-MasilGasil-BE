package team.silvertown.masil.texture;

import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserTexture extends BaseDomainTexture {

    public static User createValidUser() {
        String nickname = faker.funnyName()
            .name();
        LocalDate birthDate = faker.date()
            .birthdayLocalDate(20, 40);
        int height = getRandomInt(170, 190);
        int weight = getRandomInt(70, 90);

        return createUser(nickname, Sex.MALE, birthDate, height, weight,
            ExerciseIntensity.MIDDLE, 0, 0, true, true, Provider.KAKAO,
            String.valueOf(getRandomId()));
    }

    public static User createUser(
        String nickname,
        Sex sex,
        LocalDate birthDate,
        Integer height,
        Integer weight,
        ExerciseIntensity exerciseIntensity,
        Integer totalDistance,
        Integer totalCount,
        boolean isPublic,
        boolean isAllowingNotification,
        Provider provider,
        String socialId
    ) {
        return User.builder()
            .nickname(nickname)
            .sex(sex)
            .birthDate(birthDate)
            .height(height)
            .weight(weight)
            .exerciseIntensity(exerciseIntensity)
            .totalDistance(totalDistance)
            .totalCount(totalCount)
            .isAllowingNotification(isAllowingNotification)
            .isPublic(isPublic)
            .provider(provider)
            .socialId(socialId)
            .build();
    }

}
