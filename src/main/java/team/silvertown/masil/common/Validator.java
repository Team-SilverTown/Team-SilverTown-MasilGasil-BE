package team.silvertown.masil.common;

import java.util.Objects;
import java.util.function.Supplier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Validator {

    public static void throwIf(boolean state, Supplier<RuntimeException> exceptionSupplier) {
        if (state) {
            throw exceptionSupplier.get();
        }
    }

    public static void notNull(Object object, Supplier<RuntimeException> exceptionSupplier) {
        throwIf(Objects.isNull(object), exceptionSupplier);
    }

    public static void notBlank(String string, Supplier<RuntimeException> exceptionSupplier) {
        notNull(string, exceptionSupplier);
        throwIf(string.isBlank(), exceptionSupplier);
    }

    public static void notOver(int size, int max, Supplier<RuntimeException> exceptionSupplier) {
        throwIf(size > max, exceptionSupplier);
    }

    public static void notUnder(int size, int min, Supplier<RuntimeException> exceptionSupplier) {
        throwIf(size < min, exceptionSupplier);
    }

    public static void range(
        int size,
        int min,
        int max,
        Supplier<RuntimeException> exceptionSupplier
    ) {
        notOver(size, max, exceptionSupplier);
        notUnder(size, min, exceptionSupplier);
    }

    public static void notMatching(
        @Nullable
        String string,
        @Nullable
        String pattern,
        Supplier<RuntimeException> exceptionSupplier
    ) {
        notNull(string, exceptionSupplier);
        throwIf(!string.matches(Objects.requireNonNull(pattern)), exceptionSupplier);
    }

}
