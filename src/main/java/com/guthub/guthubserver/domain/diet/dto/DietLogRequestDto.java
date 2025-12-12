package com.guthub.guthubserver.domain.diet.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DietLogRequestDto {

    @NotNull(message = "Log date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate logDate;

    @Valid
    @NotNull(message = "Diet log items cannot be null")
    @Size(min = 1, message = "At least one diet log item is required")
    private List<DietLogItemDto> items;
}
