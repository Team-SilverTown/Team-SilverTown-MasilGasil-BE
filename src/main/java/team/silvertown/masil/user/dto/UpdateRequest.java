package team.silvertown.masil.user.dto;

public record UpdateRequest(
    String nickname,
    String sex,
    String birthDate,
    int height,
    int weight,
    String exerciseIntensity
) {

    public static UpdateRequest fromOnboardRequest(OnboardRequest onboardRequest) {
        return new UpdateRequest(
            onboardRequest.nickname(),
            onboardRequest.sex(),
            onboardRequest.birthDate(),
            onboardRequest.height(),
            onboardRequest.weight(),
            onboardRequest.exerciseIntensity()
        );
    }

}
