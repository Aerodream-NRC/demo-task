package com.aerodream.demoTask.Dto.Document;

import jakarta.validation.constraints.NotBlank;

public record DocumentCreateDto(
        @NotBlank(message = "author is required")
        String author,
        @NotBlank(message = "name is required")
        String name
) {
}
