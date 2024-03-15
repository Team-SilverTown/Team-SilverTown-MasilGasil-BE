package team.silvertown.masil.common.validator;

import java.util.Objects;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.exception.ErrorCode;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Validator {

    public static void throwIf(boolean state, Supplier<RuntimeException> exceptionSupplier) {
        if (state) {
            throw exceptionSupplier.get();
        }
    }

    public static void notNull(Object object, ErrorCode errorCode) {
        throwIf(Objects.isNull(object), supplyBadRequest(errorCode));
    }

    public static void notBlank(String string, ErrorCode errorCode) {
        notNull(string, errorCode);
        throwIf(string.isBlank(), supplyBadRequest(errorCode));
    }

    public static void notOver(Integer size, int max, ErrorCode errorCode) {
        notNull(size, errorCode);
        throwIf(size > max, supplyBadRequest(errorCode));
    }

    public static void notUnder(Integer size, int min, ErrorCode errorCode) {
        notNull(size, errorCode);
        throwIf(size < min, supplyBadRequest(errorCode));
    }

    public static void range(int size, int min, int max, ErrorCode errorCode) {
        notOver(size, max, errorCode);
        notUnder(size, min, errorCode);
    }

    public static void notMatching(
        @Nullable
        String string,
        @Nullable
        String pattern,
        ErrorCode errorCode
    ) {
        notNull(string, errorCode);
        throwIf(!string.matches(Objects.requireNonNull(pattern)), supplyBadRequest(errorCode));
    }

    private static Supplier<RuntimeException> supplyBadRequest(ErrorCode errorCode) {
        return () -> new BadRequestException(errorCode);
    }

}
