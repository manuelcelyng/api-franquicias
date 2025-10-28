package co.com.nequi.reto.r2dbc.branch;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface BranchReactiveRepository extends ReactiveCrudRepository<BranchData, Long>, ReactiveQueryByExampleExecutor<BranchData> {
    Mono<BranchData> findByFranchiseIdAndName(Long franchiseId, String name);
}
