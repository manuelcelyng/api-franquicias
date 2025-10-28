package co.com.nequi.reto.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record ProductRequest(
        @NotBlank
        String name,
        @Min(0)
        Integer stock
) {
}
