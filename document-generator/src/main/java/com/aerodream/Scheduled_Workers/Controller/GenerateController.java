package com.aerodream.Scheduled_Workers.Controller;

import com.aerodream.Scheduled_Workers.Dto.GenerateRequestDto;
import com.aerodream.Scheduled_Workers.Service.GenerationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/generate")
@RequiredArgsConstructor
public class GenerateController {

    private final GenerationService generationService;

    @PostMapping
    public ResponseEntity<Void> generateDocument(@Valid @RequestBody GenerateRequestDto requestDto) {
        generationService.generateDocument(requestDto);
        return ResponseEntity
                .accepted()
                .build();
    }
}
