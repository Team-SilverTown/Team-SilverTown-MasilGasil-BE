package team.silvertown.masil.texture;

import java.sql.Date;
import java.time.LocalDate;
import team.silvertown.masil.user.domain.ExerciseIntensity;
import team.silvertown.masil.user.domain.Provider;
import team.silvertown.masil.user.domain.Sex;
import team.silvertown.masil.user.domain.User;

public final class UserTexture extends BaseDomainTexture {

    private UserTexture() {
    }

    public static User createValidUser() {
        String nickname = faker.funnyName()
            .name();
        LocalDate birthDate = faker.date()
            .birthdayLocalDate(20, 40);
        int height = faker.number()
            .numberBetween(170, 190);
        int weight = faker.number()
            .numberBetween(60, 90);

        return createUser(nickname, Sex.MALE, Date.valueOf(birthDate), height, weight,
            ExerciseIntensity.MIDDLE, 0, 0, true, true, Provider.KAKAO,
            String.valueOf(getRandomId()));
    }

    public static User createUser(
        String nickname,
        Sex sex,
        Date birthDate,
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
