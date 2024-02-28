package team.silvertown.masil.image.controller;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.image.dto.ImageResponse;
import team.silvertown.masil.image.service.ImageService;

@RestController
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/api/v1/images")
    public ResponseEntity<ImageResponse> upload(@RequestPart MultipartFile file) {
        URI savedImageUri = imageService.upload(file);
        ImageResponse imageResponse = new ImageResponse(savedImageUri.toString());

        return ResponseEntity.created(savedImageUri)
            .body(imageResponse);
    }

}
