package co.com.nequi.reto.usecase.branch;

import co.com.nequi.reto.model.branch.Branch;
import co.com.nequi.reto.model.branch.gateways.BranchRepository;
import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.gateways.ProductRepository;
import co.com.nequi.reto.usecase.exceptions.BranchNotExists;
import co.com.nequi.reto.usecase.exceptions.ProductAlreadyExists;
import co.com.nequi.reto.usecase.exceptions.ProductNotExists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;
    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private BranchUseCase useCase;

    private Branch branch;
    private Product product;

    @BeforeEach
    void setUp() {
        branch = Branch.builder().id(10L).name("B1").franchiseId(1L).build();
        product = Product.builder().id(5L).name("P1").stock(3).branchId(10L).build();
    }

    @Test
    void addProduct_success() {
        when(branchRepository.findById(10L)).thenReturn(Mono.just(branch));
        when(productRepository.findByBranchIdAndProductId(10L, 5L)).thenReturn(Mono.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.addProduct(10L, product))
                .expectNextMatches(p -> p.getBranchId().equals(10L) && p.getId().equals(5L))
                .verifyComplete();
    }

    @Test
    void addProduct_errorWhenBranchMissing() {
        when(branchRepository.findById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.addProduct(10L, product))
                .expectError(BranchNotExists.class)
                .verify();
    }

    @Test
    void addProduct_errorWhenProductAlreadyExists() {
        when(branchRepository.findById(10L)).thenReturn(Mono.just(branch));
        when(productRepository.findByBranchIdAndProductId(10L, 5L)).thenReturn(Mono.just(product));

        StepVerifier.create(useCase.addProduct(10L, product))
                .expectError(ProductAlreadyExists.class)
                .verify();
    }

    @Test
    void deleteProductFromBranch_success() {
        when(branchRepository.findById(10L)).thenReturn(Mono.just(branch));
        when(productRepository.findByBranchIdAndProductId(10L, 5L)).thenReturn(Mono.just(product));
        when(productRepository.deleteByBranchIdAndId(10L, 5L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProductFromBranch(10L, 5L))
                .verifyComplete();
    }

    @Test
    void deleteProductFromBranch_errorWhenBranchMissing() {
        when(branchRepository.findById(10L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProductFromBranch(10L, 5L))
                .expectError(BranchNotExists.class)
                .verify();
    }

    @Test
    void deleteProductFromBranch_errorWhenProductMissing() {
        when(branchRepository.findById(10L)).thenReturn(Mono.just(branch));
        when(productRepository.findByBranchIdAndProductId(10L, 5L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.deleteProductFromBranch(10L, 5L))
                .expectError(ProductNotExists.class)
                .verify();
    }

    @Test
    void updateBranch_success_updatesNameConditionally() {
        Branch update = Branch.builder().id(10L).name("NewName").build();
        when(branchRepository.findById(10L)).thenReturn(Mono.just(branch));
        when(branchRepository.save(any(Branch.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(useCase.updateBranch(update))
                .expectNextMatches(b -> b.getId().equals(10L) && b.getName().equals("NewName"))
                .verifyComplete();
    }

    @Test
    void updateBranch_errorWhenBranchMissing() {
        Branch update = Branch.builder().id(999L).name("X").build();
        when(branchRepository.findById(999L)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateBranch(update))
                .expectError(BranchNotExists.class)
                .verify();
    }
}
