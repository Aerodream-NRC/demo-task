package com.aerodream.Scheduled_Workers.Dto;

import java.time.LocalDateTime;
import java.util.List;

public record DocumentWithHistoryResponseDto(
        String uuid,
        String author,
        String name,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<String> history
) {
}