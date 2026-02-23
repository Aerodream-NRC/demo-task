package com.aerodream.demoTask.Dto.tests;

import java.util.List;

public record SubmitRequest(
        List<Long> listId,
        String author
) {
}
