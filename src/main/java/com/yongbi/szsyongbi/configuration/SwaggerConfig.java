package com.yongbi.szsyongbi.configuration;

import com.yongbi.szsyongbi.configuration.security.AuthenticationFilter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.tags.Tag;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import java.util.List;
import java.util.Optional;

@SecurityScheme(
        name = "bearer-key",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
@Configuration
public class SwaggerConfig {
    private final ApplicationContext applicationContext;

    public SwaggerConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .tags(tagList())
                .info(apiInfo());
    }

    @Bean
    public OpenApiCustomizer springSecurityLoginEndpointCustomizer() {
        final var filterChainProxy = applicationContext.getBean(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME, FilterChainProxy.class);
        return openAPI -> {
            for (SecurityFilterChain filterChain : filterChainProxy.getFilterChains()) {
                Optional<AuthenticationFilter> optionalFilter = filterChain.getFilters().stream()
                                .filter(AuthenticationFilter.class::isInstance)
                                .map(AuthenticationFilter.class::cast)
                                .findAny();

                if (optionalFilter.isPresent()) {
                    final var operation = new Operation();
                    final var loginSchema = loginSchema();

                    final var requestBody = new RequestBody().content(
                            new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                                    new MediaType().schema(loginSchema)));
                    operation.requestBody(requestBody);

                    final var apiResponses = new ApiResponses();
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.OK.value()),
                            new ApiResponse().description("로그인 성공").content(loginContent(true, "로그인에 성공했습니다.")));
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                            new ApiResponse().description("로그인 요청 형식이 잘못되었습니다.").content(loginContent(false, "비밀번호는 필수값입니다.")));
                    apiResponses.addApiResponse(String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                            new ApiResponse().description("유효하지 않은 인증 정보입니다.").content(loginContent(false, "자격 증명에 실패하였습니다.")));

                    operation.responses(apiResponses);
                    operation.addTagsItem("회원");
                    operation.setSummary("로그인");
                    operation.setDescription("유효한 AccessToken을 얻기위한 로그인 절차입니다. 발급된 토큰은 발급 시점으로부터 10분간 유요합니다.");

                    final var pathItem = new PathItem().post(operation);
                    openAPI.getPaths().addPathItem("/szs/login", pathItem);
                    openAPI.getComponents().getSchemas().put(loginSchema.getTitle(), loginSchema);
                }
            }
        };
    }

    private Schema<?> loginSchema() {
        return new ObjectSchema()
                .title("LoginRequest")
                .description("로그인 요청에 사용되는 객체입니다.")
                .addProperty("userId", new StringSchema().description("사용자 아이디").example("kw68"))
                .addProperty("password", new StringSchema().description("사용자 패스워드").example("123456"))
                .addRequiredItem("userId")
                .addRequiredItem("password");
    }

    private Content loginContent(boolean result, String message) {
        final var schema = new ObjectSchema();
        schema.addProperty("result", new BooleanSchema().example(result).description("요청 상태"));
        schema.addProperty("accessToken", new StringSchema().description("로그인이 성공한 경우 유효한 토큰이 반환되며, 10분 간 유효합니다."));
        schema.addProperty("message", new StringSchema().example(message));

        return new Content().addMediaType(org.springframework.http.MediaType.APPLICATION_JSON_VALUE,
                new MediaType().schema(schema));
    }

    private List<Tag> tagList() {
        final var authenticationTag = new Tag();
        authenticationTag.setName("회원");
        authenticationTag.setDescription("회원가입과 로그인입니다.");

        return List.of(
                authenticationTag
        );
    }

    private Info apiInfo() {
        return new Info()
                .title("삼점삼 채용과제 스크랩 API")
                .description("특정 사용자에 대한 회원가입과 로그인을 통한 토큰 발급, 공제 정보 스크랩 후 저장과 결정 세액 계산 기능을 제공합니다.")
                .version("1.0.0");
    }
}
