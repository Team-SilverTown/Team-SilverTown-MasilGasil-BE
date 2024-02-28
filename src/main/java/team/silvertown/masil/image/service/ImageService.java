package team.silvertown.masil.image.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.image.bucket.ImageBucket;
import team.silvertown.masil.image.exception.UncheckedURISyntaxException;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageBucket imageBucket;

    public URI upload(MultipartFile file) {
        String key = generateKey(file.getOriginalFilename());
        URL url = imageBucket.upload(key, file);

        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            throw new UncheckedURISyntaxException(e.getMessage(), e);
        }
    }

    private String generateKey(String filename) {
        return filename;
    }


}
