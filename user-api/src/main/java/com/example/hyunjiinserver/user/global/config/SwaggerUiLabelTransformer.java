package com.example.hyunjiinserver.user.global.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.resource.ResourceTransformerChain;
import org.springframework.web.servlet.resource.TransformedResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SwaggerUiLabelTransformer extends SwaggerIndexPageTransformer {

    private static final String SWAGGER_CSS_FILE = "swagger-ui.css";
    private static final String RESPONSE_LABEL_STYLES = """

            /* Distinguish documented response examples from a live server response. */
            .swagger-ui .responses-wrapper > .opblock-section-header > h4 {
              font-size: 0;
            }

            .swagger-ui .responses-wrapper > .opblock-section-header > h4::after {
              content: "응답 종류 및 문서 예시";
              font-size: 20px;
            }

            .swagger-ui .responses-inner h4::after {
              content: " · 실제 서버 응답";
              color: #1b7f4b;
              font-size: 13px;
              font-weight: 700;
            }
            """;

    public SwaggerUiLabelTransformer(
            SwaggerUiConfigProperties swaggerUiConfigProperties,
            SwaggerUiOAuthProperties swaggerUiOAuthProperties,
            SwaggerWelcomeCommon swaggerWelcomeCommon,
            ObjectMapperProvider objectMapperProvider
    ) {
        super(
                swaggerUiConfigProperties,
                swaggerUiOAuthProperties,
                swaggerWelcomeCommon,
                objectMapperProvider
        );
    }

    @Override
    public Resource transform(
            HttpServletRequest request,
            Resource resource,
            ResourceTransformerChain transformerChain
    ) throws IOException {
        if (SWAGGER_CSS_FILE.equals(resource.getFilename())) {
            String css = resource.getContentAsString(StandardCharsets.UTF_8);
            return new TransformedResource(
                    resource,
                    appendResponseLabelStyles(css).getBytes(StandardCharsets.UTF_8)
            );
        }
        return super.transform(request, resource, transformerChain);
    }

    static String appendResponseLabelStyles(String css) {
        return css + RESPONSE_LABEL_STYLES;
    }
}
