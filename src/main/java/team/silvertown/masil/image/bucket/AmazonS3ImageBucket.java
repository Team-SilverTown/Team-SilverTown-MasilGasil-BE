package team.silvertown.masil.image.bucket;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamSource;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
public class AmazonS3ImageBucket implements ImageBucket {

    private final String name;
    private final AmazonS3 amazonS3;

    @Override
    public URL upload(String key, MultipartFile file) {
        ObjectMetadata metadata = new ObjectMetadata();

        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());

        PutObjectRequest request = new PutObjectRequest(name, key, getInputStream(file), metadata);

        amazonS3.putObject(request);

        return amazonS3.getUrl(name, key);
    }

    private InputStream getInputStream(InputStreamSource source) {
        try {
            return source.getInputStream();
        } catch (IOException e) {
            throw new UncheckedIOException(e.getMessage(), e);
        }
    }


}
