package co.com.nequi.reto.usecase.franchise;

import co.com.nequi.reto.model.branch.Branch;
import co.com.nequi.reto.model.branch.gateways.BranchRepository;
import co.com.nequi.reto.model.franchise.Franchise;
import co.com.nequi.reto.model.franchise.gateways.FranchiseRepository;
import co.com.nequi.reto.usecase.exceptions.*;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final BranchRepository branchRepository;

    public Mono<Franchise> createFranchise(Franchise franchise) {
        return franchiseRepository.findByName(franchise.getName())
                .switchIfEmpty(franchiseRepository.save(franchise))
                .flatMap(exists ->
                        Mono.error(new FranchiseAlreadyExists(
                                        TypeErrors.FRANCHISE_ALREADY_EXISTS.getCode(),
                                        TypeErrors.FRANCHISE_ALREADY_EXISTS.getMessage())
                         ));
    }

    // Cambiar al use case de branch
    public Mono<Branch> addBranch(Long franchiseId, Branch branch) {
        String name = branch.getName();
        return branchRepository.findByFranchiseIdAndName(franchiseId, name)
                .hasElement()
                .flatMap(exists -> exists
                        ? Mono.error(new BranchNotExists(
                                TypeErrors.BRANCH_ALREADY_EXISTS.getCode(),
                                TypeErrors.BRANCH_ALREADY_EXISTS.getMessage()))
                        : branchRepository.save(
                                branch.getFranchiseId() != null && branch.getFranchiseId().equals(franchiseId)
                                        ? branch
                                        : branch.toBuilder().franchiseId(franchiseId).build()
                        )
                );
    }

    // dejar validacion del campo name soolo desde el entrypoint
    public Mono<Franchise> updateFranchise(Franchise franchiseUpdate ) {

        return franchiseRepository.findById(franchiseUpdate.getId())
                .switchIfEmpty(Mono.error(new FranchiseNotExists(TypeErrors.FRANCHISE_NOT_EXISTS.getCode(), TypeErrors.FRANCHISE_NOT_EXISTS.getMessage())))
                .flatMap(existingFranchise -> {
                    Franchise.FranchiseBuilder  builder  = existingFranchise.toBuilder();
                    if (franchiseUpdate.getName() != null  && !franchiseUpdate.getName().isBlank()){
                        builder.name(franchiseUpdate.getName());
                    }
                    return franchiseRepository.save(builder.build());
                });

    }



}
