package co.com.nequi.reto.api.mappers.branch;

import co.com.nequi.reto.api.dto.BranchRequest;
import co.com.nequi.reto.api.dto.BranchResponse;
import co.com.nequi.reto.model.branch.Branch;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {
                BranchRequest.class,
                Branch.class,
                BranchResponse.class
        },
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface BranchDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "franchiseId", ignore = true)
    Branch toModel(BranchRequest branchRequest);

    BranchResponse toResponse(Branch branch);
}
