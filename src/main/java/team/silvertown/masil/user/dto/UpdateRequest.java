package team.silvertown.masil.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateRequest {

    private String nickname;
    private String sex;
    private String birthDate;
    private int height;
    private int weight;
    private String exerciseIntensity;

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
