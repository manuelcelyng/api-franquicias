package co.com.nequi.reto.api.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder(toBuilder = true)
public record ProductUpdateRequest(
        @Min(value = 0, message = "Stock must be greater than or equal to 0")
        Integer stock,
        String name  // Optional field
) {
}
