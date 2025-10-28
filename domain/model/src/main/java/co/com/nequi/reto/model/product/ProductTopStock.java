package co.com.nequi.reto.model.product;

import lombok.Builder;

@Builder(toBuilder = true)
public record ProductTopStock(
        String productName,
        String branchName,
        Integer stock
) {
}
