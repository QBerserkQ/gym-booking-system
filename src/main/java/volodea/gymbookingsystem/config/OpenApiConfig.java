package volodea.gymbookingsystem.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym Booking System API")
                        .description("""
                                REST API for a gym workout booking system.
                                
                                Main flow: registration → browsing available classes → booking → approval by support staff → confirmation
                                
                                Test credentials:
                                - ADMIN: admin@gmail.com | 12345
                                - Regular users can register via /api/auth/register
                                """)
                        .version("1.0")).components(
                                new Components().addSecuritySchemes(
                                        "bearerAuth", new SecurityScheme().type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer").bearerFormat("JWT")))

                .addSecurityItem(
                        new SecurityRequirement().addList("bearerAuth")
                );
    }
}
