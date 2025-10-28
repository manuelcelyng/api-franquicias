package co.com.nequi.reto.api.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder(toBuilder = true)
public record BranchRequest(
        @NotBlank
        String name
) {
}
