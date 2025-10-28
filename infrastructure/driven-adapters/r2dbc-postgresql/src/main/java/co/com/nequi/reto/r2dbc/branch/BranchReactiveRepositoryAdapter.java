package co.com.nequi.reto.r2dbc.branch;

import co.com.nequi.reto.model.branch.Branch;
import co.com.nequi.reto.model.branch.gateways.BranchRepository;
import co.com.nequi.reto.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BranchReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Branch,
        BranchData,
        Long,
        BranchReactiveRepository
> implements BranchRepository {
    public BranchReactiveRepositoryAdapter(BranchReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }

    @Override
    public Mono<Branch> findByFranchiseIdAndName(Long franchiseId, String name) {
        return repository.findByFranchiseIdAndName(franchiseId, name)
                .map(this::toEntity);
    }




}
