package co.com.nequi.reto.model.product.gateways;

import co.com.nequi.reto.model.product.Product;
import co.com.nequi.reto.model.product.ProductTopStock;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<Product> findByBranchIdAndProductId(Long branchId, Long productId);
    Mono<Product> save(Product product);
    Mono<Void> deleteByBranchIdAndId(Long branchId, Long productId);

    Flux<ProductTopStock> findTopStockForBranchByFranchiseId(Long franchiseId);
}
