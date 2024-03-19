package team.silvertown.masil.config.security;

import java.text.MessageFormat;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import tech.ailef.snapadmin.external.SnapAdminProperties;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(SnapAdminProperties.class)
public class HttpRequestsConfigurer
    implements
    Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    private static final String[] SWAGGER = {
        "/docs",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-resources/**"
    };
    private static final String AUTH_RESOURCE = "/api/v1/users/login";
    private static final String[] GET_PERMIT_ALL_RESOURCES = {
        // health check
        "/healthy",

        // users
        "api/v1/users/**",

        // posts
        "/api/v1/posts",
        "/api/v1/posts/**",

        // mates
        "/api/v1/mates",
        "/api/v1/mates/**"
    };
    private static final String USER_ME_RESOURCE = "/api/v1/users/me";
    private static final String ADMIN_PANEL = "/{0}/**";

    private final SnapAdminProperties snapAdminProperties;

    @Override
    public void customize(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests
    ) {
        authorizeRequests
            .requestMatchers(SWAGGER)
            .permitAll()
            .requestMatchers(AUTH_RESOURCE)
            .permitAll()
            .requestMatchers(HttpMethod.GET, GET_PERMIT_ALL_RESOURCES)
            .permitAll()
            .requestMatchers(MessageFormat.format(ADMIN_PANEL, snapAdminProperties.getBaseUrl()))
            .permitAll()
            .requestMatchers(HttpMethod.GET, USER_ME_RESOURCE)
            .authenticated()
            .anyRequest()
            .authenticated();
    }

}
