package co.com.nequi.reto.api.dto;

import lombok.Builder;

@Builder(toBuilder = true)
public record BranchResponse(Long id, String name) {}
