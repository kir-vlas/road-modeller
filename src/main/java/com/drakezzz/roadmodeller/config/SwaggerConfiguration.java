package com.drakezzz.roadmodeller.config;

import com.drakezzz.roadmodeller.utils.StreamUtils;
import com.fasterxml.classmate.TypeResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import springfox.documentation.PathProvider;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.RequestHandlerProvider;
import springfox.documentation.spi.service.contexts.Defaults;
import springfox.documentation.spring.web.DocumentationCache;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;
import springfox.documentation.spring.web.plugins.DocumentationPluginsManager;
import springfox.documentation.spring.web.scanners.ApiDocumentationScanner;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

import java.util.List;

@EnableSwagger2WebFlux
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket swaggerSpringMvcPlugin() {
        final ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Road Modeller API v 1.0")
                .description("RESTful API для управления моделями")
                // Указываем информацию о версии сборки
//                .version(String.format("Version %s (commit %s on branch %s at %s)",
//                        buildProperties.getVersion(), gitProperties.getCommitId(), gitProperties.getBranch(), gitProperties.getCommitTime()))
                .build();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                // Убираем из документации встроенные контроллеры спринга
                .paths(StreamUtils.not(PathSelectors.regex("/actuator/*")))
                .paths(StreamUtils.not(PathSelectors.regex("/error")))
                .paths(StreamUtils.not(PathSelectors.regex("/")))
                // .paths(PathSelectors.any())
                .build();
    }

    @Bean
    public DocumentationPluginsBootstrapper swaggerDocumentationStarter(DocumentationPluginsManager documentationPluginsManager,
                                                                        List<RequestHandlerProvider> handlerProviders,
                                                                        DocumentationCache scanned,
                                                                        ApiDocumentationScanner resourceListing,
                                                                        TypeResolver typeResolver,
                                                                        Defaults defaults,
                                                                        PathProvider pathProvider,
                                                                        Environment environment) {
        return new DocumentationPluginsBootstrapper(documentationPluginsManager, handlerProviders, scanned, resourceListing, typeResolver, defaults, pathProvider, environment);
    }

}
