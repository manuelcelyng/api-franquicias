package co.com.nequi.reto.api.mappers;

import co.com.nequi.reto.api.dto.ProductRequest;
import co.com.nequi.reto.api.dto.ProductResponse;
import co.com.nequi.reto.api.mappers.product.ProductDtoMapper;
import co.com.nequi.reto.model.product.Product;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ProductDtoMapperTest {

    private final ProductDtoMapper mapper = Mappers.getMapper(ProductDtoMapper.class);

    @Test
    void toModel_shouldIgnoreIdAndBranchId_andMapNameAndStock() {
        ProductRequest req = ProductRequest.builder()
                .name("Mouse")
                .stock(15)
                .build();

        Product model = mapper.toModel(req);

        assertThat(model.getId()).isNull();
        assertThat(model.getBranchId()).isNull();
        assertThat(model.getName()).isEqualTo("Mouse");
        assertThat(model.getStock()).isEqualTo(15);
    }

    @Test
    void toResponse_shouldUseProvidedBranchId_andMapNameAndStock() {
        Product model = Product.builder()
                .id(5L)
                .name("Keyboard")
                .stock(7)
                .branchId(99L)
                .build();

        ProductResponse resp = mapper.toResponse(model);

        assertThat(resp.name()).isEqualTo("Keyboard");
        assertThat(resp.stock()).isEqualTo(7);
    }
}
