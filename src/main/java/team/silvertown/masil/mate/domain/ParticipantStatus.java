package team.silvertown.masil.mate.domain;

import java.util.Arrays;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.mate.validator.MateValidator;

public enum ParticipantStatus {
    REQUESTED,
    ACCEPTED;

    public static ParticipantStatus get(String value) {
        return Arrays.stream(ParticipantStatus.values())
            .filter(status -> status.isMatching(value))
            .findFirst()
            .orElseThrow(() -> new BadRequestException(MateErrorCode.INVALID_STATUS));
    }

    private boolean isMatching(String value) {
        MateValidator.notBlank(value, MateErrorCode.BLANK_PARTICIPANT_STATUS);

        return value.equals(this.name());
    }
}
