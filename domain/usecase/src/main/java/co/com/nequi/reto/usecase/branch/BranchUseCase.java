package co.com.nequi.reto.usecase.branch;

import co.com.nequi.reto.model.branch.Branch;
import co.com.nequi.reto.model.branch.gateways.BranchRepository;
import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.gateways.ProductRepository;
import co.com.nequi.reto.usecase.exceptions.BranchNotExists;
import co.com.nequi.reto.usecase.exceptions.ProductAlreadyExists;
import co.com.nequi.reto.usecase.exceptions.ProductNotExists;
import co.com.nequi.reto.usecase.exceptions.TypeErrors;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class BranchUseCase {

    private final BranchRepository branchRepository;
    private final ProductRepository productRepository;

    public Mono<Product> addProduct(Long branchId, Product product) {
        Long productId = product.getId();
        // refactorizar, separando en dos monos e implementar las busquedas en paralelo
        // refactorizar cómo se indico el de franchise
        return branchRepository.findById(branchId)
                .hasElement()
                .flatMap(exists -> exists
                        ? productRepository.findByBranchIdAndProductId(branchId, productId)
                        : Mono.error(new BranchNotExists(
                        TypeErrors.BRANCH_NOT_EXISTS.getCode(),
                        TypeErrors.BRANCH_NOT_EXISTS.getMessage())))
                .hasElement()
                .flatMap(exists -> exists
                        ? Mono.error(new ProductAlreadyExists(
                        TypeErrors.PRODUCT_ALREADY_EXISTS.getCode(),
                        TypeErrors.PRODUCT_ALREADY_EXISTS.getMessage()))
                        : productRepository.save(
                        product.toBuilder().branchId(branchId).build())
                );
    }


    public Mono<Void> deleteProductFromBranch(Long branchId, Long productId) {
        return branchRepository.findById(branchId)
                .switchIfEmpty(Mono.error(new BranchNotExists(TypeErrors.BRANCH_NOT_EXISTS.getCode(), TypeErrors.BRANCH_NOT_EXISTS.getMessage())))
                .flatMap(branch -> productRepository.findByBranchIdAndProductId(branchId,productId))
                .switchIfEmpty(Mono.error(new ProductNotExists(TypeErrors.PRODUCT_NOT_EXISTS.getCode(), TypeErrors.PRODUCT_NOT_EXISTS.getMessage())))
                .flatMap(product -> productRepository.deleteByBranchIdAndId(branchId, productId));

    }


    public Mono<Branch> updateBranch(Branch branchUpdate) {
        return branchRepository.findById(branchUpdate.getId())
                .switchIfEmpty(Mono.error(new BranchNotExists(TypeErrors.BRANCH_NOT_EXISTS.getCode(), TypeErrors.BRANCH_NOT_EXISTS.getMessage())))
                .flatMap(existingBranch -> {
                    Branch.BranchBuilder builder = existingBranch.toBuilder();
                    if (branchUpdate.getName() != null  && !branchUpdate.getName().isBlank()){
                        builder.name(branchUpdate.getName());
                    }
                    return branchRepository.save(builder.build());
                });
    }
}
