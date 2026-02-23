package com.aerodream.demoTask.Workers;

import com.aerodream.demoTask.Enum.DocumentStatus;
import com.aerodream.demoTask.Repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ApproveWorker extends Worker {

    public ApproveWorker(DocumentRepository documentRepository,
                         WebClient webClient,
                         @Value("${workers.approve.batch-size}") int batchSize) {
        super(documentRepository, webClient, DocumentStatus.SUBMITTED, "APPROVE-worker", "api/documents/approve");
        this.batchSize = batchSize;
    }

    private final int batchSize;

    @Scheduled(fixedDelayString = "${workers.approve.fixed-delay}")
    public void run() {
        processBatch(batchSize, workerName);
    }
}
