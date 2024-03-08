package team.silvertown.masil.image.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("spring.cloud.aws.s3")
public record S3Properties(
    String bucket,
    String endpoint
) {

}
