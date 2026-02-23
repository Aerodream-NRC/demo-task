package com.aerodream.demoTask.Dto.Exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        String code,
        String message,
        LocalDateTime timeStamp
) {
}
