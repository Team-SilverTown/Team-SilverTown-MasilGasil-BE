package team.silvertown.masil.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "마실가실 API"
    ),
    security = @SecurityRequirement(name = "카카오 로그인")
)
@SecurityScheme(
    name = "카카오 로그인",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "${spring.security.oauth2.client.provider.kakao.authorization-uri}",
            tokenUrl = "${spring.security.oauth2.client.provider.kakao.token-uri}"
        )
    )
)
public class SwaggerConfig {

}
