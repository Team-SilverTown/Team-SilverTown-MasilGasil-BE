package team.silvertown.masil.image.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public class ImageFileValidator implements ConstraintValidator<ImageFile, MultipartFile> {

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (Objects.isNull(file) || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();

        return ImageFileType.isImage(contentType);
    }

}
