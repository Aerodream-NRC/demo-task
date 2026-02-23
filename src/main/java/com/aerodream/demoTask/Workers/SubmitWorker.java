package com.aerodream.demoTask.Workers;

import com.aerodream.demoTask.Enum.DocumentStatus;
import com.aerodream.demoTask.Repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SubmitWorker extends Worker {

    public SubmitWorker(DocumentRepository documentRepository,
                        WebClient webClient,
                        @Value("${workers.submit.batch-size}") int batchSize) {
        super(documentRepository, webClient, DocumentStatus.DRAFT, "SUBMIT-worker", "api/documents/submit");
        this.batchSize = batchSize;
    }

    private final int batchSize;

    @Scheduled(fixedDelayString = "${workers.submit.fixed-delay}")
    public void run() {
        processBatch(batchSize, workerName);
    }
}
