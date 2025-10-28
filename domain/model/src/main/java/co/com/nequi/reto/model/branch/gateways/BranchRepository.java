package co.com.nequi.reto.model.branch.gateways;

import co.com.nequi.reto.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {
    Mono<Branch> save(Branch branch);
    Mono<Branch> findByFranchiseIdAndName(Long franchiseId, String name);
    Mono<Branch> findById(Long id);


}
