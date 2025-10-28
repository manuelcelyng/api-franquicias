package co.com.nequi.reto.r2dbc.franchise;

import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface FranchiseReactiveRepository extends ReactiveCrudRepository<FranchiseData, Long>, ReactiveQueryByExampleExecutor<FranchiseData> {

    Mono<FranchiseData> findByName(String name);
}
