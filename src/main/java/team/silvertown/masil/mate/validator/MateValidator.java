package team.silvertown.masil.mate.validator;

import io.micrometer.common.util.StringUtils;
import java.time.OffsetDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.ForbiddenException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.mate.domain.Mate;
import team.silvertown.masil.mate.domain.MateParticipant;
import team.silvertown.masil.mate.exception.MateErrorCode;
import team.silvertown.masil.user.domain.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MateValidator extends Validator {

    private static final int MAX_TITLE_LENGTH = 30;
    private static final int MAX_DETAIL_LENGTH = 50;
    private static final int MAX_CAPACITY = 10;
    private static final int MAX_MESSAGE_LENGTH = 255;

    public static void validateTitle(String title) {
        notBlank(title, MateErrorCode.BLANK_TITLE);
        notOver(title.length(), MAX_TITLE_LENGTH, MateErrorCode.TITLE_TOO_LONG);
    }

    public static void validateDetail(String detail) {
        notBlank(detail, MateErrorCode.BLANK_DETAIL);
        notOver(detail.length(), MAX_DETAIL_LENGTH, MateErrorCode.DETAIL_TOO_LONG);
    }

    public static void validateGatheringAt(OffsetDateTime gatheringAt) {
        OffsetDateTime now = OffsetDateTime.now();

        throwIf(gatheringAt.isBefore(now),
            () -> new BadRequestException(MateErrorCode.GATHER_AT_PAST));
    }

    public static void validateCapacity(Integer capacity) {
        notNull(capacity, MateErrorCode.NULL_CAPACITY);
        notUnder(capacity, 0, MateErrorCode.NON_POSITIVE_CAPACITY);
        notOver(capacity, MAX_CAPACITY, MateErrorCode.CAPACITY_TOO_LARGE);
    }

    public static void validateMessage(String message) {
        if (StringUtils.isNotBlank(message)) {
            notOver(message.length(), MAX_MESSAGE_LENGTH, MateErrorCode.MESSAGE_TOO_LONG);
        }
    }

    public static void validateParticipantAcceptance(
        Long authorId,
        Long mateId,
        MateParticipant mateParticipant
    ) {
        Mate mate = mateParticipant.getMate();

        validateAuthForManipulation(authorId, mate);
        validateParticipantUnderMate(mateId, mate);
    }

    public static void validateParticipantDeletion(
        User user,
        Long mateId,
        MateParticipant mateParticipant
    ) {
        Mate mate = mateParticipant.getMate();

        validateParticipantUnderMate(mateId, mate);

        boolean isNotOwner = !user.equals(mateParticipant.getUser());

        if (isNotOwner) {
            validateAuthForManipulation(user.getId(), mate);
        }
    }

    private static void validateParticipantUnderMate(
        Long mateId,
        Mate mate
    ) {
        notNull(mateId, MateErrorCode.NULL_MATE);

        boolean isNotMatching = !mateId.equals(mate.getId());

        throwIf(isNotMatching,
            () -> new BadRequestException(MateErrorCode.PARTICIPANT_MATE_NOT_MATCHING));
    }

    private static void validateAuthForManipulation(Long authorId, Mate mate) {
        notNull(authorId, MateErrorCode.NULL_AUTHOR);

        boolean isNotAuthor = !authorId.equals(mate.getAuthor().getId());

        throwIf(isNotAuthor,
            () -> new ForbiddenException(MateErrorCode.USER_NOT_AUTHORIZED_FOR_MATE));
    }

}
