package team.silvertown.masil.image.service;

import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.text.MessageFormat;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.image.config.S3Properties;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final S3Properties s3Properties;
    private final S3Template s3Template;

    public URI upload(MultipartFile file) {
        String key = generateKey(file.getOriginalFilename());

        try {
            s3Template.upload(s3Properties.bucket(), key, file.getInputStream());

            return getUri(key);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private @NonNull String generateKey(String filename) {
        return filename;
    }

    private URI getUri(String key) {
        String uri = MessageFormat.format(
            "{0}/{1}/{2}",
            s3Properties.endpoint(),
            s3Properties.bucket(),
            key);

        return URI.create(uri);
    }

}
