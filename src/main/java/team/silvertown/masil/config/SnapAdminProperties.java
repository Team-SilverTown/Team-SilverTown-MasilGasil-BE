package team.silvertown.masil.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("snapadmin")
public record SnapAdminProperties(
    String baseUrl
) {

}
