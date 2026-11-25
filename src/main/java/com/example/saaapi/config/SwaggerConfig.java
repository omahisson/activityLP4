package com.example.saaapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.spi.service.contexts.SecurityContext;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket documentacao(){
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors
                        .basePackage("com.example.saaapi.api.controller"))
                .paths(PathSelectors.any())
                .build()
                .securityContexts(Arrays.asList(contextoSeguranca()))
                .securitySchemes(Arrays.asList(chaveApi()))
                .apiInfo(informacoesApi());
    }

    private ApiInfo informacoesApi(){
        return new ApiInfoBuilder()
                .title("Armazenamento de Material Radioativo API")
                .description("API acadêmica para cadastro de materiais, locais e registros de armazenamento")
                .version("1.0")
                .contact(contato())
                .build();
    }

    private Contact contato(){
        return new Contact("Trabalho acadêmico", "", "");
    }


    public ApiKey chaveApi(){
        return new ApiKey("JWT", "Authorization", "header");
    }

    private SecurityContext contextoSeguranca(){
        return SecurityContext.builder()
                .securityReferences(autorizacoesPadrao())
                .forPaths(PathSelectors.any())
                .build();
    }

    private List<SecurityReference> autorizacoesPadrao(){
        AuthorizationScope escopoAutorizacao = new AuthorizationScope(
                "global", "accessEverything");
        AuthorizationScope[] escopos = new AuthorizationScope[1];
        escopos[0] = escopoAutorizacao;
        SecurityReference referencia = new SecurityReference("JWT", escopos);
        List<SecurityReference> autorizacoes = new ArrayList<>();
        autorizacoes.add(referencia);
        return autorizacoes;
    }

}
