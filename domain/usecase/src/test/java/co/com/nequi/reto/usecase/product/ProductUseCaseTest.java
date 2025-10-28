package co.com.nequi.reto.usecase.product;

import co.com.nequi.reto.model.branch.Branch;
import co.com.nequi.reto.model.branch.gateways.BranchRepository;
import co.com.nequi.reto.model.franchise.Franchise;
import co.com.nequi.reto.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.ProductTopStock;
import co.com.nequi.reto.model.product.gateways.ProductRepository;
import co.com.nequi.reto.usecase.exceptions.BranchNotExists;
import co.com.nequi.reto.usecase.exceptions.FranchiseNotExists;
import co.com.nequi.reto.usecase.exceptions.ProductNotExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private ProductUseCase useCase;

    private Product baseProduct;

    @BeforeEach
    void setUp() {
        baseProduct = Product.builder()
                .id(5L)
                .branchId(100L)
                .name("Old Name")
                .stock(10)
                .build();
    }

    @Test
    void updateProduct_shouldUpdateOnlyProvidedFields() {
        // branch exists
        when(branchRepository.findById(100L)).thenReturn(Mono.just(Branch.builder().id(100L).build()));
        // product exists in branch
        when(productRepository.findByBranchIdAndProductId(100L, 5L)).thenReturn(Mono.just(baseProduct));
        // saving should reflect partial updates
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        Product update = Product.builder()
                .id(5L)
                .branchId(100L)
                .name("New Name") // only name changes
                .build();

        StepVerifier.create(useCase.updateProduct(update))
                .expectNextMatches(p -> p.getId().equals(5L)
                        && p.getBranchId().equals(100L)
                        && p.getName().equals("New Name")
                        && p.getStock().equals(10)) // stock preserved
                .verifyComplete();
    }

    @Test
    void updateProduct_shouldError_whenBranchNotFound() {
        when(branchRepository.findById(100L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProduct(baseProduct))
                .expectError(BranchNotExists.class)
                .verify();
    }

    @Test
    void updateProduct_shouldError_whenProductNotFoundInBranch() {
        when(branchRepository.findById(100L)).thenReturn(Mono.just(Branch.builder().id(100L).build()));
        when(productRepository.findByBranchIdAndProductId(100L, 5L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateProduct(baseProduct))
                .expectError(ProductNotExists.class)
                .verify();
    }

    @Test
    void findTopStockForBranchByFranchiseId_success() {
        when(franchiseRepository.findById(7L)).thenReturn(Mono.just(Franchise.builder().id(7L).name("F1").build()));
        when(productRepository.findTopStockForBranchByFranchiseId(7L)).thenReturn(Flux.just(
                ProductTopStock.builder().productName("A").branchName("B1").stock(5).build(),
                ProductTopStock.builder().productName("C").branchName("B2").stock(3).build()
        ));

        StepVerifier.create(useCase.findTopStockForBranchByFranchiseId(7L))
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void findTopStockForBranchByFranchiseId_shouldError_whenFranchiseNotFound() {
        when(franchiseRepository.findById(7L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.findTopStockForBranchByFranchiseId(7L))
                .expectError(FranchiseNotExists.class)
                .verify();
    }
}
