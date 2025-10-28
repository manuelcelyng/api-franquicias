package co.com.nequi.reto.api.mappers.product;

import co.com.nequi.reto.api.dto.ProductRequest;
import co.com.nequi.reto.api.dto.ProductResponse;
import co.com.nequi.reto.api.dto.ProductUpdateRequest;
import co.com.nequi.reto.model.product.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",
        uses = {
                Product.class,
                ProductRequest.class,
                ProductResponse.class,
                ProductUpdateRequest.class
        },
        unmappedTargetPolicy = ReportingPolicy.ERROR,
        builder = @Builder(disableBuilder = true)
)
public interface ProductDtoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    Product toModel(ProductRequest productRequest);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "branchId", ignore = true)
    Product toModel(ProductUpdateRequest productRequestUpdate);

    // Clean approach: the mapper stays dumb; pass branchName explicitly from the caller (use case/handler)

    ProductResponse toResponse(Product product);


}
