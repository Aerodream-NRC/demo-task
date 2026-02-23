package com.aerodream.demoTask.Dto.Document;

import java.time.LocalDateTime;

public record DocumentResponseDto(
        String uuid,
        String author,
        String name,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
