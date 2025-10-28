package co.com.nequi.reto.api.mappers.franchise;


import co.com.nequi.reto.api.dto.FranchiseRequest;
import co.com.nequi.reto.api.dto.FranchiseResponse;
import co.com.nequi.reto.model.franchise.Franchise;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring"
        , uses = {
        FranchiseRequest.class,
        Franchise.class,
        FranchiseResponse.class
}
        , unmappedTargetPolicy = ReportingPolicy.ERROR
        , builder = @Builder(disableBuilder = true)
)
public interface FranchiseDtoMapper {

    @Mapping(target = "id", ignore = true)
    Franchise toModel(FranchiseRequest franchiseRequest);
    FranchiseResponse toResponse(Franchise franchise);
}
