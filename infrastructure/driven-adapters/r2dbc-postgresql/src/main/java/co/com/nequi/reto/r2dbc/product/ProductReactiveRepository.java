package co.com.nequi.reto.r2dbc.product;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductData, Long>, ReactiveQueryByExampleExecutor<ProductData> {

    Mono<ProductData> findByBranchIdAndId(Long branchId, Long productId);

    Mono<Void> deleteByBranchIdAndId(Long branchId, Long productId);


}
