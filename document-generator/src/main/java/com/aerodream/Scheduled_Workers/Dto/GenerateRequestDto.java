package com.aerodream.Scheduled_Workers.Dto;

public record GenerateRequestDto(
        int count,
        String authorPrefix,
        String namePrefix
) {
}
