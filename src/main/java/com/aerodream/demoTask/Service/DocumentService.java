package com.aerodream.demoTask.Service;

import com.aerodream.demoTask.Dto.Document.DocumentCreateDto;
import com.aerodream.demoTask.Dto.Document.DocumentProcessResponseDto;
import com.aerodream.demoTask.Dto.Document.DocumentResponseDto;
import com.aerodream.demoTask.Dto.Document.DocumentWithHistoryResponseDto;
import com.aerodream.demoTask.Entity.ApproveEntity;
import com.aerodream.demoTask.Entity.DocumentEntity;
import com.aerodream.demoTask.Entity.HistoryEntity;
import com.aerodream.demoTask.Entity.Specification.DocumentSpecifications;
import com.aerodream.demoTask.Enum.DocumentStatus;
import com.aerodream.demoTask.Enum.HistoryAction;
import com.aerodream.demoTask.Enum.ProcessStatus;
import com.aerodream.demoTask.Exception.DocumentNotFoundException;
import com.aerodream.demoTask.Repository.ApproveRepository;
import com.aerodream.demoTask.Repository.DocumentRepository;
import com.aerodream.demoTask.Repository.HistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final ApproveRepository approveRepository;
    private final HistoryRepository historyRepository;

    @Transactional
    public DocumentWithHistoryResponseDto createDocument(DocumentCreateDto createDto) {
        long start = System.currentTimeMillis();

        DocumentEntity entity = new DocumentEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setName(createDto.name());
        entity.setStatus(DocumentStatus.DRAFT);
        entity.setAuthor(createDto.author());

        DocumentEntity savedDocument = documentRepository.save(entity);

        long duration = System.currentTimeMillis() - start;
        log.info("Created document with ID: {} and UUID: {}, for {} ms", savedDocument.getId(), savedDocument.getUuid(), duration);
        return convertToDtoWithHistory(savedDocument, new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public DocumentWithHistoryResponseDto getDocumentById(long id) {
        log.info("Fetching document with ID: {}", id);
        DocumentEntity entity = documentRepository.findById(id)
                .orElseThrow(
                        () -> new DocumentNotFoundException(id)
                );
        List<HistoryEntity> historyList = historyRepository.findAllByDocumentId(id);
        return convertToDtoWithHistory(entity, historyList);
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> getDocuments(List<Long> listId, Pageable pageable) {
        log.info("Fetching {} documents", pageable.getPageSize());
        Page<DocumentEntity> entities;

        if (listId == null) entities = documentRepository.findAll(pageable);
        else entities = documentRepository.findByIdIn(listId, pageable);

        return entities.map(this::convertToDto);
    }

    @Transactional
    public List<DocumentProcessResponseDto> submitDocuments(List<Long> listId, String author) {
        long start = System.currentTimeMillis();

        if (listId.size() > 1000 || listId.isEmpty()) {
            throw new IllegalArgumentException("List size must be between 1 adn 1000");
        }

        List<DocumentProcessResponseDto> submissionResults = new ArrayList<>();

        for (Long id : listId) {
            ProcessStatus status = submitDocument(id, author);
            submissionResults.add(new DocumentProcessResponseDto(id, status.toString()));
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Submitted {} documents for {} ms", listId.size(), duration);
        return submissionResults;
    }

    @Transactional
    public List<DocumentProcessResponseDto> approveDocuments(List<Long> listId, String author) {
        long start = System.currentTimeMillis();

        if (listId.size() > 1000 || listId.isEmpty()) {
            throw new IllegalArgumentException("List size must be between 1 adn 1000");
        }

        List<DocumentProcessResponseDto> approvedResult = new ArrayList<>();

        for (Long id : listId) {
            ProcessStatus status = approveResult(id, author);
            approvedResult.add(new DocumentProcessResponseDto(id, status.toString()));
        }

        long duration = System.currentTimeMillis() - start;
        log.info("Approved {} documents for {} ms", listId.size(), duration);
        return approvedResult;
    }

    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> searchDocuments(DocumentStatus status,
                                                     String author,
                                                     LocalDateTime dateFrom,
                                                     LocalDateTime dateTo,
                                                     Pageable pageable) {
        Specification<DocumentEntity> specification = Specification
                .where(DocumentSpecifications.hasStatus(status))
                .and(DocumentSpecifications.authorContains(author))
                .and(DocumentSpecifications.createdAtBetween(dateFrom, dateTo));
        return documentRepository.findAll(specification, pageable)
                .map(this::convertToDto);
    }

    private ProcessStatus approveResult(Long documentId, String author) {
        if (documentRepository.updateStatusIfSubmitted(documentId) == 1) {
            createHistory(documentId, author, HistoryAction.APPROVE);
            createApprove(documentId, author);
            documentRepository.updateTimeStamp(documentId);
            return ProcessStatus.SUCCESS;
        } else return documentRepository.existsById(documentId)
                ? ProcessStatus.CONFLICT
                : ProcessStatus.NOT_FOUND;
    }

    private ProcessStatus submitDocument(Long documentId, String author) {
        if (documentRepository.updateStatusIfDraft(documentId) == 1) {
            createHistory(documentId, author, HistoryAction.SUBMIT);
            documentRepository.updateTimeStamp(documentId);
            return ProcessStatus.SUCCESS;
        } else return documentRepository.existsById(documentId)
                ? ProcessStatus.CONFLICT
                : ProcessStatus.NOT_FOUND;
    }

    private void createApprove(Long documentId, String author) {
        ApproveEntity approve = new ApproveEntity();
        approve.setApprovedAt(LocalDateTime.now());
        approve.setAuthor(author);
        approve.setDocumentId(documentId);
        approveRepository.save(approve);
    }

    private void createHistory(Long documentId, String author, HistoryAction action) {
        HistoryEntity history = new HistoryEntity();
        history.setDocumentId(documentId);
        history.setAuthor(author);
        history.setAction(action);
        history.setHappendAt(LocalDateTime.now());
        historyRepository.save(history);
    }

    private DocumentWithHistoryResponseDto convertToDtoWithHistory(DocumentEntity entity, List<HistoryEntity> historyList) {
        return new DocumentWithHistoryResponseDto(
                entity.getUuid().toString(),
                entity.getAuthor(),
                entity.getName(),
                entity.getStatus().toString(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                historyList.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList())
        );
    }

    private DocumentResponseDto convertToDto(DocumentEntity entity) {
        return new DocumentResponseDto(
                entity.getUuid().toString(),
                entity.getAuthor(),
                entity.getName(),
                entity.getStatus().toString(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
