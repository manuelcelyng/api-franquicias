package co.com.nequi.reto.api;

import co.com.nequi.reto.api.errors.GlobalErrorHandlerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

import jakarta.validation.Validator;

@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalErrorHandlerConfig.class})
@WebFluxTest
class RouterRestTest {

    @Autowired
    private WebTestClient webTestClient;

    // Satisfacer dependencias del Handler sin afectar la ruta GET
    @MockBean
    private Validator validator;

    @MockBean
    private co.com.nequi.reto.api.mappers.franchise.FranchiseDtoMapper franchiseDtoMapper;

    @MockBean
    private co.com.nequi.reto.api.mappers.branch.BranchDtoMapper branchDtoMapper;

    @MockBean
    private co.com.nequi.reto.api.mappers.product.ProductDtoMapper productDtoMapper;

    @MockBean
    private co.com.nequi.reto.usecase.franchise.FranchiseUseCase franchiseUseCase;

    @MockBean
    private co.com.nequi.reto.usecase.branch.BranchUseCase branchUseCase;

    @MockBean
    private co.com.nequi.reto.usecase.product.ProductUseCase productUseCase;

    @Test
    void livenessCheck_shouldReturnOk() {
        webTestClient.get()
                .uri("/api/v1/check")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("I'm alive!");
    }
}
