package com.aerodream.demoTask.Service;

import com.aerodream.demoTask.Dto.Document.DocumentProcessResponseDto;
import com.aerodream.demoTask.Dto.tests.ConcurrentTestRequest;
import com.aerodream.demoTask.Dto.tests.ConcurrentTestResponse;
import com.aerodream.demoTask.Entity.DocumentEntity;
import com.aerodream.demoTask.Enum.ProcessStatus;
import com.aerodream.demoTask.Repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConcurrentTestService {

    private final DocumentService documentService;

    private final DocumentRepository documentRepository;

    public ConcurrentTestResponse runConcurrentApprovalTest(ConcurrentTestRequest request) {
        int totalAttempts = request.attempt() * request.threads();
        ExecutorService executorService = Executors.newFixedThreadPool(request.threads());

        List<CompletableFuture<ProcessStatus>> futures = new ArrayList<>();

        List<Long> listId = new ArrayList<>();
        listId.add(request.documentId());

        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger conflict = new AtomicInteger(0);
        AtomicInteger error = new AtomicInteger(0);

        for (int i = 0; i < request.threads(); i++) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                for (int j = 0; j < request.attempt(); j++) {
                    try {
                        switch (ProcessStatus.valueOf(documentService
                                .approveDocuments(listId, Thread.currentThread().getName())
                                .getFirst()
                                .status())) {
                            case SUCCESS -> success.incrementAndGet();
                            case CONFLICT -> conflict.incrementAndGet();
                            default -> error.incrementAndGet();
                        }
                    } catch (Exception e) {
                        error.incrementAndGet();
                    }
                }
                return null;
            }, executorService));
        }

        for (CompletableFuture<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Concurrent test interrupted", e);
            }
        }
        executorService.shutdown();

        DocumentEntity document = documentRepository.findById(request.documentId())
                .orElse(null);

        String finalStatus = document != null ? document.getStatus().name() : "NOT_FOUND";

        executorService.shutdown();
        return new ConcurrentTestResponse(totalAttempts, success.get(), conflict.get(), error.get(), finalStatus);
    }
}
