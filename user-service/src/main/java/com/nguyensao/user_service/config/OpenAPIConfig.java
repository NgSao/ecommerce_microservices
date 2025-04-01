package com.nguyensao.user_service.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    // 🔹 Cấu hình Bearer Token
    private SecurityScheme createBearerScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
    }

    // 🔹 Cấu hình Cookie Authentication cho refreshToken
    private SecurityScheme createRefreshTokenScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("refreshToken"); // Tên cookie chứa refresh token
    }

    // 🔹 Cấu hình Cookie Authentication cho OTP
    private SecurityScheme createOtpScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .name("OTP"); // Tên cookie chứa OTP token
    }

    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    private Contact createContact() {
        return new Contact()
                .email("nguyensaovn2019@gmail.com")
                .name("Sao Nguyễn")
                .url("https://ns.vn");
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
    }

    private Info createApiInfo() {
        return new Info()
                .title("User service")
                .version("1.0")
                .contact(createContact())
                .description("This API exposes all endpoints (job hunter)")
                .termsOfService("https://ns.vn/donate")
                .license(createLicense());
    }

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(
                        createServer("http://localhost:8081", "Server URL in Development environment"),
                        createServer("https://ns.vn", "Server URL in Production environment")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .addSecurityItem(new SecurityRequirement().addList("Cookie RefreshToken"))
                .addSecurityItem(new SecurityRequirement().addList("Cookie OTP"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication", createBearerScheme())
                        .addSecuritySchemes("Cookie RefreshToken", createRefreshTokenScheme())
                        .addSecuritySchemes("Cookie OTP", createOtpScheme()));
    }
}
