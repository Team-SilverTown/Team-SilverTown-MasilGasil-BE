package team.silvertown.masil.mate.dto.request;

import team.silvertown.masil.mate.validator.MateValidator;

public record CreateMateParticipantRequest(String message) {

    public CreateMateParticipantRequest {
        MateValidator.validateMessage(message);
    }

}
