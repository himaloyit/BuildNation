package com.himaloyit.buildnation.ui.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Mirrors the {@code ApiResponse<T>} wrapper every backend service returns
 * (e.g. com.himaloyit.buildnation.sac.rbac.domain.model.ApiResponse).
 */
@Data
@NoArgsConstructor
public class ApiResponseDTO<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
}
