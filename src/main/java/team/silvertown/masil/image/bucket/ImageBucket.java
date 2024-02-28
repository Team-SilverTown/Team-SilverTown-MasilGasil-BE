package team.silvertown.masil.image.bucket;

import java.net.URL;
import org.springframework.web.multipart.MultipartFile;

public interface ImageBucket {

    URL upload(String key, MultipartFile file);

}
