package co.com.nequi.reto.api.mappers;

import co.com.nequi.reto.api.dto.FranchiseRequest;
import co.com.nequi.reto.api.dto.FranchiseResponse;
import co.com.nequi.reto.api.mappers.franchise.FranchiseDtoMapper;
import co.com.nequi.reto.model.franchise.Franchise;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class FranchiseDtoMapperTest {

    private final FranchiseDtoMapper mapper = Mappers.getMapper(FranchiseDtoMapper.class);

    @Test
    void toModel_shouldMapName_andIgnoreId() {
        FranchiseRequest request = FranchiseRequest.builder().name("BrandX").build();

        Franchise model = mapper.toModel(request);

        assertThat(model.getId()).isNull();
        assertThat(model.getName()).isEqualTo("BrandX");
    }

    @Test
    void toResponse_shouldMapFields() {
        Franchise model = Franchise.builder().id(2L).name("BrandY").build();

        FranchiseResponse response = mapper.toResponse(model);

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.name()).isEqualTo("BrandY");
    }
}
