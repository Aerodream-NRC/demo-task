package com.aerodream.Scheduled_Workers.Service;

import com.aerodream.Scheduled_Workers.Dto.DocumentWithHistoryResponseDto;
import com.aerodream.Scheduled_Workers.Dto.GenerateRequestDto;
import com.aerodream.Scheduled_Workers.Dto.WebClientDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerationService {

    private final WebClient webClient;

    public void generateDocument(GenerateRequestDto requestDto) {
        log.info("Generating {} documents in progress", requestDto.count());
        sendGenerateToService(requestDto);
    }

    private void sendGenerateToService(GenerateRequestDto requestDto) {
        long start = System.currentTimeMillis();
        int total = requestDto.count();
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CompletionService<DocumentWithHistoryResponseDto> completionService = new ExecutorCompletionService<>(executor);

        for (int i = 0; i < total; i++) {
            int idx = i;
            completionService.submit(() -> {
                return webClient.post()
                        .uri("/api/documents/create")
                        .bodyValue(new WebClientDto(
                                requestDto.authorPrefix() + idx,
                                requestDto.namePrefix() + idx))
                        .retrieve()
                        .bodyToMono(DocumentWithHistoryResponseDto.class)
                        .block();
            });
        }

        for (int i = 0; i < total; i++) {
            try {
                Future<DocumentWithHistoryResponseDto> future = completionService.take();
                future.get();
                log.info("Generated {}/{} documents", i + 1, total);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Interrupted while waiting for result", e);
                break;
            } catch (ExecutionException e) {
                log.error("Error generating document: {}", e.getCause().getMessage());
                log.info("Generated {}/{} documents with error", i + 1, total);
            }
        }
        executor.shutdown();
        long duration = System.currentTimeMillis() - start;
        log.info("{} documents created for {} ms",
                requestDto.count(), duration);
    }
}
