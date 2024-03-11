package team.silvertown.masil.image.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@Tag(name = "이미지 API")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping(
        value = "/api/v1/images",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(summary = "이미지 업로드")
    @ApiResponse(
        responseCode = "201",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = ImageResponse.class)
        )
    )
    public ResponseEntity<ImageResponse> upload(
        @RequestBody(description = "MIME image type", required = true)
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
