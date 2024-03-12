package team.silvertown.masil.image.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import net.datafaker.Faker;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import team.silvertown.masil.image.config.S3Properties;
import team.silvertown.masil.test.LocalstackTest;

@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores.class)
class ImageServiceTest extends LocalstackTest {

    static final Faker faker = new Faker();

    @Autowired
    ImageService imageService;

    @Test
    void 이미지를_업로드_할_수_있다() {
        // given
        String filename = faker.file()
            .fileName("", null, "jpg", "");
        String content = faker.book()
            .title();
        MultipartFile file = new MockMultipartFile(filename, content.getBytes());

        // when
        URI uri = imageService.upload(file);

        // then
        assertThat(uri.toString()).startsWith(getS3Endpoint());
    }

    @TestConfiguration
    static class Config {

        @Bean
        public S3Properties s3Properties() {
            return new S3Properties(BUCKET_NAME, getS3Endpoint());
        }

    }

}
