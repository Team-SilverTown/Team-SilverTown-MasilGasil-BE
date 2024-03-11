package team.silvertown.masil.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("cors")
public record CORSProperties(
    String localOrigin,
    String serviceOrigin
) {

}
