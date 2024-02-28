package team.silvertown.masil.image.service;

import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Template s3;

    @Value("${spring.cloud.aws.s3.bucket}")
    private String bucket;

    public URI upload(MultipartFile file) {
        String key = generateKey(file.getOriginalFilename());

        try {
            S3Resource resource = s3.upload(bucket, key, file.getInputStream());

            return resource.getURI();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private @NonNull String generateKey(String filename) {
        return filename;
    }

}
