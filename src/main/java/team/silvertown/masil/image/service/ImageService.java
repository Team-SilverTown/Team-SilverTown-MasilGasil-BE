package team.silvertown.masil.image.service;

import io.awspring.cloud.s3.S3Template;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.image.config.S3Properties;

@Service
@RequiredArgsConstructor
public class ImageService {

    private static final Encoder encoder = Base64.getEncoder();
    private static final String FILENAME_FORMAT = "{0}.{1}";
    private static final String URI_FORMAT = "{0}/{1}/{2}";

    private final S3Properties s3Properties;
    private final S3Template s3Template;

    public URI upload(MultipartFile file) {
        String key = generateKey(file.getOriginalFilename());

        try {
            s3Template.upload(s3Properties.bucket(), key, file.getInputStream());

            return createURI(key);
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }

    private String generateKey(String filename) {
        String name = StringUtils.getFilename(filename);
        String ext = StringUtils.getFilenameExtension(filename);

        String uuid = UUID.randomUUID()
            .toString();
        String newName = uuid + name;
        String encode = encoder.encodeToString(newName.getBytes());

        return MessageFormat.format(FILENAME_FORMAT, encode, ext);
    }

    private URI createURI(String key) {
        String uri = MessageFormat.format(
            URI_FORMAT,
            s3Properties.endpoint(),
            s3Properties.bucket(),
            key);

        return URI.create(uri);
    }

}
