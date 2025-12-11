package com.guthub.guthubserver.domain.diet.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@NoArgsConstructor
public class DietLogRequestDto {

    @NotNull(message = "Food ID cannot be null")
    private Long foodId;

    @NotNull(message = "Log date cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate logDate;

    @NotNull(message = "Log time cannot be null")
    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    private LocalTime logTime;

    private Float amount; // Can be null, default is 1.0f

    @NotNull(message = "Meal type cannot be null")
    private String mealType;
}
