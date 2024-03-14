package team.silvertown.masil.image.validator;

import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.common.exception.BadRequestException;
import team.silvertown.masil.common.validator.Validator;
import team.silvertown.masil.image.exception.ImageErrorCode;

public class ImageFileServiceValidator extends Validator {

    public static void validateImgFile(MultipartFile file) {
        throwIf(file.isEmpty(), () -> new BadRequestException(ImageErrorCode.FILE_IS_EMPTY));
        String contentType = file.getContentType();
        throwIf(!ImageFileType.isImage(contentType),
            () -> new BadRequestException(ImageErrorCode.NOT_SUPPORTED_CONTENT));
    }

}
