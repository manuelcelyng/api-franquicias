package co.com.nequi.reto.api.config;

import co.com.nequi.reto.api.Handler;
import co.com.nequi.reto.api.RouterRest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import jakarta.validation.Validator;

@ContextConfiguration(classes = {RouterRest.class, Handler.class})
@WebFluxTest
@Import({CorsConfig.class, SecurityHeadersConfig.class})
class ConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private co.com.nequi.reto.api.mappers.franchise.FranchiseDtoMapper franchiseDtoMapper;

    @MockBean
    private co.com.nequi.reto.usecase.franchise.FranchiseUseCase franchiseUseCase;

    @MockBean
    private Validator validator;

    @MockBean
    private co.com.nequi.reto.api.mappers.branch.BranchDtoMapper branchDtoMapper;

    @MockBean
    private co.com.nequi.reto.api.mappers.product.ProductDtoMapper productDtoMapper;

    @MockBean
    private co.com.nequi.reto.usecase.branch.BranchUseCase branchUseCase;

    @MockBean
    private co.com.nequi.reto.usecase.product.ProductUseCase productUseCase;

    @Test
    void corsConfigurationShouldAllowOrigins() {
        webTestClient.get()
                .uri("/api/v1/check")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin");
    }

}