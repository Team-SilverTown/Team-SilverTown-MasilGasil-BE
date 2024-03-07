package team.silvertown.masil.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
public class HttpRequestsConfigurer
    implements
    Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    private static final String USER_INFO_REQUEST = "/api/v1/users/me";
    private static final String NORMAL_USER_ROLE = "NORMAL";
    private static final String AUTH_RESOURCE = "/api/v1/users/login";

    @Override
    public void customize(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests
    ) {
        authorizeRequests
            .requestMatchers(AUTH_RESOURCE)
            .permitAll()
            .requestMatchers(USER_INFO_REQUEST).hasRole(NORMAL_USER_ROLE)
            .anyRequest()
            .authenticated();
    }

}
