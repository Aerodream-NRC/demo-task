package com.aerodream.demoTask.Workers;

import com.aerodream.demoTask.Dto.Document.DocumentProcessResponseDto;
import com.aerodream.demoTask.Dto.tests.SubmitRequest;
import com.aerodream.demoTask.Enum.DocumentStatus;
import com.aerodream.demoTask.Enum.ProcessStatus;
import com.aerodream.demoTask.Repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
public abstract class Worker {

    protected final DocumentRepository documentRepository;
    protected final WebClient webClient;
    protected final DocumentStatus status;
    protected final String workerName;
    protected final String uri;

    public void processBatch(int batchSize, String workerName) {
        log.info("{} started. Looking for document with status {}", workerName, status);
        List<Long> listId = documentRepository.findIdByStatus(status, Pageable.ofSize(batchSize));
        long documentsCount = documentRepository.countByStatus(status);

        if (listId.isEmpty()) {
            log.info("{}: no documents found", workerName);
            return;
        }

        log.info("{}: found {} documents, processing batch {}", workerName, documentsCount, listId.size());

        try {
            SubmitRequest request = new SubmitRequest(listId, workerName);

            List<DocumentProcessResponseDto> results = webClient.patch()
                    .uri(uriBuilder -> uriBuilder
                            .path(this.uri)
                            .queryParam("listId", listId)
                            .build())
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<DocumentProcessResponseDto>>() {
                    })
                    .block();
            if (results != null) {
                long success = results.stream()
                        .filter(c -> c.status().equals(ProcessStatus.SUCCESS.name()))
                        .count();
                long conflict = results.stream()
                        .filter(c -> c.status().equals(ProcessStatus.CONFLICT.name()))
                        .count();
                long notFound = results.stream()
                        .filter(c -> c.status().equals(ProcessStatus.NOT_FOUND.name()))
                        .count();
                long error = results.size() - success - conflict - notFound;

                log.info("{} completed. Total: {}, Success: {}, Conflict: {}, Not Found: {}, Error: {}, Remaining: {}",
                        workerName, results.size(), success, conflict, notFound, error, documentsCount - results.size());
            }
        } catch (Exception e) {
            log.error("{}: unexpected error during batch processing", workerName, e);
        }
    }
}
