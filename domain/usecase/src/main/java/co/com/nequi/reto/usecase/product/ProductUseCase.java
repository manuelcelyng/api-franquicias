package co.com.nequi.reto.usecase.product;

import co.com.nequi.reto.model.branch.gateways.BranchRepository;
import co.com.nequi.reto.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.ProductTopStock;
import co.com.nequi.reto.model.product.gateways.ProductRepository;
import co.com.nequi.reto.usecase.branch.BranchUseCase;
import co.com.nequi.reto.usecase.exceptions.BranchNotExists;
import co.com.nequi.reto.usecase.exceptions.FranchiseNotExists;
import co.com.nequi.reto.usecase.exceptions.ProductNotExists;
import co.com.nequi.reto.usecase.exceptions.TypeErrors;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;


    public Mono<Product> updateProduct(Product product) {

        return branchRepository.findById(product.getBranchId())
                .switchIfEmpty(Mono.error(new BranchNotExists(TypeErrors.BRANCH_NOT_EXISTS.getCode(), TypeErrors.BRANCH_NOT_EXISTS.getMessage())))
                .flatMap(b -> productRepository.findByBranchIdAndProductId(b.getId(), product.getId()))
                .switchIfEmpty(Mono.error(new ProductNotExists(TypeErrors.PRODUCT_NOT_EXISTS.getCode(), TypeErrors.PRODUCT_NOT_EXISTS.getMessage())))
                .flatMap(existingProduct -> {
                    // Usa el producto existente como base y actualiza solo los campos no nulos
                    Product.ProductBuilder builder = existingProduct.toBuilder();

                    if (product.getStock() != null) {
                        builder.stock(product.getStock());
                    }

                    if (product.getName() != null && !product.getName().isBlank()) {
                        builder.name(product.getName());
                    }

                    return productRepository.save(builder.build());
                });
    }


    public Flux<ProductTopStock> findTopStockForBranchByFranchiseId(Long franchiseId) {
        return franchiseRepository.findById(franchiseId)
                .switchIfEmpty(Mono.error(new FranchiseNotExists(TypeErrors.FRANCHISE_NOT_EXISTS.getCode(), TypeErrors.FRANCHISE_NOT_EXISTS.getMessage())))
                .flatMapMany(franchise -> productRepository.findTopStockForBranchByFranchiseId(franchiseId));


    }


}
