package com.aerodream.demoTask.Dto.tests;

public record ConcurrentTestRequest(
        long documentId,
        int threads,
        int attempt
) {
}
