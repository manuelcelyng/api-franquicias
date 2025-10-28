package co.com.nequi.reto.api.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;


@Builder
public record FranchiseRequest(
        @NotBlank
        String name
) {
}
