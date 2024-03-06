package team.silvertown.masil.image.controller;

import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.common.exception.ErrorResponse;
import team.silvertown.masil.image.dto.ImageResponse;
import team.silvertown.masil.image.exception.ImageErrorCode;
import team.silvertown.masil.image.service.ImageService;
import team.silvertown.masil.image.validator.ImageFile;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/api/v1/images")
    public ResponseEntity<ImageResponse> upload(
        @RequestPart
        @Valid
        @ImageFile
        MultipartFile file
    ) {
        URI imageUri = imageService.upload(file);
        ImageResponse imageResponse = new ImageResponse(imageUri.toString());

        return ResponseEntity.created(imageUri)
            .body(imageResponse);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidationException(
        HandlerMethodValidationException ignored
    ) {
        ImageErrorCode errorCode = ImageErrorCode.NOT_SUPPORTED_CONTENT;
        ErrorResponse response = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());

        return ResponseEntity.badRequest()
            .body(response);
    }

}
