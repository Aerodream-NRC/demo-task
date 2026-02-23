package com.aerodream.demoTask.Dto.Document;

import jakarta.validation.constraints.NotBlank;

public record DocumentProcessRequestDto(
        @NotBlank(message = "author is required")
        String author
) {
}
