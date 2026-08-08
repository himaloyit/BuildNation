package com.himaloyit.buildnation.cdm.fund.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFundRequest {

    @NotNull(message = "Month is mandatory")
    private LocalDate month;

    @NotBlank(message = "Fund type is mandatory")
    private String fundType;

    private UUID categoryId;

    private UUID subCategoryId;

    @NotNull(message = "Received amount is mandatory")
    @PositiveOrZero(message = "Received amount must be zero or positive")
    private BigDecimal receivedAmount;
}
