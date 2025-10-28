package co.com.nequi.reto.api;

import co.com.nequi.reto.api.dto.*;
import co.com.nequi.reto.api.errors.GlobalErrorHandlerConfig;
import co.com.nequi.reto.api.mappers.branch.BranchDtoMapper;
import co.com.nequi.reto.api.mappers.franchise.FranchiseDtoMapper;
import co.com.nequi.reto.api.mappers.product.ProductDtoMapper;
import co.com.nequi.reto.model.branch.Branch;
import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.ProductTopStock;
import co.com.nequi.reto.usecase.branch.BranchUseCase;
import co.com.nequi.reto.usecase.franchise.FranchiseUseCase;
import co.com.nequi.reto.usecase.product.ProductUseCase;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {RouterRest.class, Handler.class, GlobalErrorHandlerConfig.class, HandlerEndpointsTest.TestConfig.class})
@Import({GlobalErrorHandlerConfig.class})
class HandlerEndpointsTest {

    @Autowired
    private WebTestClient client;

    @Autowired
    private FranchiseDtoMapper franchiseDtoMapper;
    @Autowired
    private BranchDtoMapper branchDtoMapper;
    @Autowired
    private ProductDtoMapper productDtoMapper;

    @Autowired
    private FranchiseUseCase franchiseUseCase;
    @Autowired
    private BranchUseCase branchUseCase;
    @Autowired
    private ProductUseCase productUseCase;

    @BeforeEach
    void resetMocks() {
        Mockito.reset(franchiseDtoMapper, branchDtoMapper, productDtoMapper,
                franchiseUseCase, branchUseCase, productUseCase);
    }

    @Test
    void addBranch_success() {
        BranchRequest req = BranchRequest.builder().name("Store 1").build();
        Branch domainIn = Branch.builder().name("Store 1").build();
        Branch domainOut = Branch.builder().id(55L).name("Store 1").build();
        BranchResponse resp = new BranchResponse(55L, "Store 1");

        when(branchDtoMapper.toModel(any(BranchRequest.class))).thenReturn(domainIn);
        when(franchiseUseCase.addBranch(99L, domainIn)).thenReturn(Mono.just(domainOut));
        when(branchDtoMapper.toResponse(domainOut)).thenReturn(resp);

        client.post().uri("/api/v1/franchise/{id}/branch", 99)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Store 1\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(55)
                .jsonPath("$.name").isEqualTo("Store 1");
    }

    @Test
    void addBranch_pathVarNotNumber_should400() {
        client.post().uri("/api/v1/franchise/{id}/branch", "abc")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"x\"}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_ARGUMENT");
    }

    @Test
    void updateFranchise_success() {
        FranchiseRequest req = FranchiseRequest.builder().name("NewF").build();
        when(franchiseDtoMapper.toModel(any(FranchiseRequest.class)))
                .thenReturn(co.com.nequi.reto.model.franchise.Franchise.builder().name("NewF").build());
        when(franchiseUseCase.updateFranchise(any()))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));
        when(franchiseDtoMapper.toResponse(any()))
                .thenReturn(new FranchiseResponse(1L, "NewF"));

        client.patch().uri("/api/v1/franchise/{franchiseId}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"NewF\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(1)
                .jsonPath("$.name").isEqualTo("NewF");
    }

    @Test
    void addProductToBranch_success() {
        ProductRequest req = ProductRequest.builder().name("P1").stock(5).build();
        Product in = Product.builder().name("P1").stock(5).build();
        Product out = Product.builder().id(7L).name("P1").stock(5).branchId(10L).build();
        ProductResponse resp = new ProductResponse("P1", 5);

        when(productDtoMapper.toModel(any(ProductRequest.class))).thenReturn(in);
        when(branchUseCase.addProduct(10L, in)).thenReturn(Mono.just(out));
        when(productDtoMapper.toResponse(out)).thenReturn(resp);

        client.post().uri("/api/v1/branch/{id}/product", 10)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"P1\",\"stock\":5}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("P1")
                .jsonPath("$.stock").isEqualTo(5);
    }

    @Test
    void deleteProductFromBranch_success204() {
        when(branchUseCase.deleteProductFromBranch(10L, 5L)).thenReturn(Mono.empty());

        client.delete().uri("/api/v1/branch/{branchId}/product/{productId}", 10, 5)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void updateBranch_success() {
        BranchRequest req = BranchRequest.builder().name("Renamed").build();
        Branch model = Branch.builder().name("Renamed").build();
        Branch out = Branch.builder().id(10L).name("Renamed").franchiseId(1L).build();
        BranchResponse resp = new BranchResponse(10L, "Renamed");

        when(branchDtoMapper.toModel(any(BranchRequest.class))).thenReturn(model);
        when(branchUseCase.updateBranch(any())).thenReturn(Mono.just(out));
        when(branchDtoMapper.toResponse(out)).thenReturn(resp);

        client.patch().uri("/api/v1/branch/{branchId}/franchise/{franchiseId}", 10, 1)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"Renamed\"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(10)
                .jsonPath("$.name").isEqualTo("Renamed");
    }

    @Test
    void updateProduct_success() {
        ProductUpdateRequest req = ProductUpdateRequest.builder().name("NewName").stock(9).build();
        Product model = Product.builder().name("NewName").stock(9).build();
        Product saved = Product.builder().id(5L).branchId(10L).name("NewName").stock(9).build();
        ProductResponse resp = new ProductResponse("NewName", 9);

        when(productDtoMapper.toModel(any(ProductUpdateRequest.class))).thenReturn(model);
        when(productUseCase.updateProduct(any(Product.class))).thenReturn(Mono.just(saved));
        when(productDtoMapper.toResponse(saved)).thenReturn(resp);

        client.patch().uri("/api/v1/product/{productId}/branch/{branchId}", 5, 10)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"name\":\"NewName\",\"stock\":9}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("NewName")
                .jsonPath("$.stock").isEqualTo(9);
    }

    @Test
    void topProducts_success() {
        when(productUseCase.findTopStockForBranchByFranchiseId(77L))
                .thenReturn(Flux.just(
                        ProductTopStock.builder().productName("A").branchName("B1").stock(3).build(),
                        ProductTopStock.builder().productName("C").branchName("B2").stock(2).build()
                ));

        client.get().uri("/api/v1/product/top?franchiseId={id}", 77)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$[0].productName").isEqualTo("A")
                .jsonPath("$[1].branchName").isEqualTo("B2");
    }

    @Test
    void topProducts_missingQueryParam_should400() {
        client.get().uri("/api/v1/product/top")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_ARGUMENT")
                .jsonPath("$.message").isEqualTo("Query param 'franchiseId' is required");
    }

    @Test
    void topProducts_nonNumericQueryParam_should400() {
        client.get().uri("/api/v1/product/top?franchiseId=abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.code").isEqualTo("INVALID_ARGUMENT")
                .jsonPath("$.message").isEqualTo("Query param 'franchiseId' must be a number");
    }

    @Configuration
    static class TestConfig {
        @Bean
        Validator validator() {
            LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
            factoryBean.afterPropertiesSet();
            return factoryBean;
        }

        @Bean FranchiseDtoMapper franchiseDtoMapper() { return Mockito.mock(FranchiseDtoMapper.class); }
        @Bean BranchDtoMapper branchDtoMapper() { return Mockito.mock(BranchDtoMapper.class); }
        @Bean ProductDtoMapper productDtoMapper() { return Mockito.mock(ProductDtoMapper.class); }
        @Bean FranchiseUseCase franchiseUseCase() { return Mockito.mock(FranchiseUseCase.class); }
        @Bean BranchUseCase branchUseCase() { return Mockito.mock(BranchUseCase.class); }
        @Bean ProductUseCase productUseCase() { return Mockito.mock(ProductUseCase.class); }
    }
}
