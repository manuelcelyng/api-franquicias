package co.com.nequi.reto.api;

import co.com.nequi.reto.api.dto.FranchiseRequest;
import co.com.nequi.reto.api.dto.FranchiseResponse;
import co.com.nequi.reto.api.errors.GlobalErrorHandlerConfig;
import co.com.nequi.reto.api.mappers.branch.BranchDtoMapper;
import co.com.nequi.reto.api.mappers.franchise.FranchiseDtoMapper;
import co.com.nequi.reto.api.mappers.product.ProductDtoMapper;
import co.com.nequi.reto.model.franchise.Franchise;
import co.com.nequi.reto.usecase.branch.BranchUseCase;
import co.com.nequi.reto.usecase.franchise.FranchiseUseCase;
import co.com.nequi.reto.usecase.exceptions.FranchiseAlreadyExists;
import co.com.nequi.reto.usecase.exceptions.TypeErrors;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalErrorHandlerConfig.class, HandlerIntegrationTest.TestConfig.class})
@Import({GlobalErrorHandlerConfig.class})
class HandlerIntegrationTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private FranchiseDtoMapper franchiseDtoMapper;

    @Autowired
    private BranchDtoMapper branchDtoMapper;

    @Autowired
    private FranchiseUseCase franchiseUseCase;

    @Autowired
    private ProductDtoMapper productDtoMapper;

    @Autowired
    private BranchUseCase branchUseCase;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(franchiseDtoMapper, branchDtoMapper, franchiseUseCase, productDtoMapper, branchUseCase);
    }

    @Test
    void createFranchise_success() {
        // given
        Franchise domain = Franchise.builder().name("BrandX").build();
        Franchise saved = Franchise.builder().id(10L).name("BrandX").build();
        FranchiseResponse response = new FranchiseResponse(10L, "BrandX");

        when(franchiseDtoMapper.toModel(any(FranchiseRequest.class))).thenReturn(domain);
        when(franchiseUseCase.createFranchise(domain)).thenReturn(Mono.just(saved));
        when(franchiseDtoMapper.toResponse(saved)).thenReturn(response);

        client.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"BrandX\"}")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.name").isEqualTo("BrandX");
    }

    @Test
    void createFranchise_validationError() {
        client.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.code").isEqualTo("VALIDATION_ERROR")
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.details[0].field").exists();
    }

    @Test
    void createFranchise_emptyBody_illegalArgument() {
        client.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_ARGUMENT")
                .jsonPath("$.message").isEqualTo("RequestBody is required!");
    }

    @Test
    void createFranchise_businessException() {
        Franchise domain = Franchise.builder().name("BrandX").build();
        when(franchiseDtoMapper.toModel(any(FranchiseRequest.class))).thenReturn(domain);
        when(franchiseUseCase.createFranchise(domain))
                .thenReturn(Mono.error(new FranchiseAlreadyExists(
                        TypeErrors.FRANCHISE_ALREADY_EXISTS.getCode(),
                        TypeErrors.FRANCHISE_ALREADY_EXISTS.getMessage()
                )));

        client.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"BrandX\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("FRN_002")
                .jsonPath("$.message").isEqualTo("The franchise already exists");
    }

    @Test
    void createFranchise_unexpectedException() {
        Franchise domain = Franchise.builder().name("BrandX").build();
        when(franchiseDtoMapper.toModel(any(FranchiseRequest.class))).thenReturn(domain);
        when(franchiseUseCase.createFranchise(domain))
                .thenReturn(Mono.error(new RuntimeException("boom")));

        client.post()
                .uri("/api/v1/franchise")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"BrandX\"}")
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INTERNAL_ERROR");
    }

    @Configuration
    static class TestConfig {
        @Bean
        Validator validator() {
            LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
            factoryBean.afterPropertiesSet();
            return factoryBean;
        }

        @Bean
        FranchiseDtoMapper franchiseDtoMapper() {
            return Mockito.mock(FranchiseDtoMapper.class);
        }

        @Bean
        BranchDtoMapper branchDtoMapper() {
            return Mockito.mock(BranchDtoMapper.class);
        }

        @Bean
        ProductDtoMapper productDtoMapper() {
            return Mockito.mock(ProductDtoMapper.class);
        }

        @Bean
        FranchiseUseCase franchiseUseCase() {
            return Mockito.mock(FranchiseUseCase.class);
        }

        @Bean
        BranchUseCase branchUseCase() {
            return Mockito.mock(BranchUseCase.class);
        }

        @Bean
        co.com.nequi.reto.usecase.product.ProductUseCase productUseCase() {
            return Mockito.mock(co.com.nequi.reto.usecase.product.ProductUseCase.class);
        }
    }
}
