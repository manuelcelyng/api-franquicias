package co.com.nequi.reto.r2dbc.franchise;

import co.com.nequi.reto.model.franchise.Franchise;
import co.com.nequi.reto.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.reto.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Franchise,
        FranchiseData,
        Long,
        FranchiseReactiveRepository
> implements FranchiseRepository {
    public FranchiseReactiveRepositoryAdapter(FranchiseReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
    }

    @Override
    public Mono<Franchise> findByName(String name) {
        return repository.findByName(name)
                .map(d -> mapper.map(d, Franchise.class));
    }

    @Override
    public Mono<Franchise> findById(Long id) {
        return repository.findById(id)
                .map(this::toEntity);
    }
}
