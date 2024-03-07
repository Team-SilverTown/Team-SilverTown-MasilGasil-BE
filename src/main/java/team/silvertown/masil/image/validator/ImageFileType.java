package team.silvertown.masil.image.validator;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ImageFileType {
    APNG("image/apng"),
    AVIF("image/avif"),
    GIF("image/gif"),
    JPEG("image/jpeg"),
    PNG("image/png"),
    SVG_XML("image/svg+xml"),
    WEBP("image/webp");

    private final String type;

    ImageFileType(String type) {
        this.type = type;
    }

    public static boolean isImage(String contentType) {
        return Arrays.stream(ImageFileType.values())
            .map(ImageFileType::getType)
            .anyMatch(type -> type.equals(contentType));
    }
}
