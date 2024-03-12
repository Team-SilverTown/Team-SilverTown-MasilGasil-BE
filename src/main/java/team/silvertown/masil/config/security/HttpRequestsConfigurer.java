package team.silvertown.masil.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
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

    @Override
    public void customize(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests
    ) {
        authorizeRequests
            .requestMatchers(SWAGGER)
            .permitAll()
            .requestMatchers(AUTH_RESOURCE)
            .permitAll()
            .anyRequest()
            .authenticated();
    }

}
