package com.aerodream.demoTask.Dto.tests;

public record ConcurrentTestResponse(
        int totalAttempts,
        int success,
        int conflict,
        int error,
        String finalStatus
) {
}
