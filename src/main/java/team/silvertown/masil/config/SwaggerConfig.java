package team.silvertown.masil.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "마실가실 API"
    ),
    security = @SecurityRequirement(name = "카카오 로그인")
)
public class SwaggerConfig {

}
