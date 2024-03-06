package team.silvertown.masil.user.util;

import java.time.Duration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
            .setConnectTimeout(Duration.ofSeconds(5))
            .setReadTimeout(Duration.ofSeconds(5))
            .additionalInterceptors(clientHttpRequestInterceptor())
            .build();
    }

    public ClientHttpRequestInterceptor clientHttpRequestInterceptor() {
        return (request, body, execution) -> {
            RetryTemplate retryTemplate = new RetryTemplate();
            retryTemplate.setRetryPolicy(new SimpleRetryPolicy(3));
            try {
                return retryTemplate.execute(context -> execution.execute(request, body));
            } catch (Throwable throwable) {
                throw new RuntimeException(throwable);
            }
        };
    }

}
