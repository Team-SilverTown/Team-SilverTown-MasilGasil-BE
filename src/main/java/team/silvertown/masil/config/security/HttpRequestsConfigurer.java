package team.silvertown.masil.config.security;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

@Configuration
public class HttpRequestsConfigurer
    implements Customizer<AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry> {

    private static final String AUTH_RESOURCE = "/oauth2/**";

    @Override
    public void customize(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorizeRequests) {
        authorizeRequests
            .requestMatchers(AUTH_RESOURCE)
            .permitAll()
            .anyRequest()
            .authenticated();
    }

}
