package team.silvertown.masil.image.exception;

import java.net.URISyntaxException;
import java.util.Objects;

public class UncheckedURISyntaxException extends RuntimeException {

    public UncheckedURISyntaxException(String message, URISyntaxException cause) {
        super(message, Objects.requireNonNull(cause));
    }

}
