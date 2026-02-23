package com.aerodream.demoTask;

import com.aerodream.demoTask.Dto.Document.DocumentCreateDto;
import com.aerodream.demoTask.Dto.Document.DocumentProcessResponseDto;
import com.aerodream.demoTask.Dto.Document.DocumentWithHistoryResponseDto;
import com.aerodream.demoTask.Entity.ApproveEntity;
import com.aerodream.demoTask.Entity.DocumentEntity;
import com.aerodream.demoTask.Entity.HistoryEntity;
import com.aerodream.demoTask.Enum.DocumentStatus;
import com.aerodream.demoTask.Enum.ProcessStatus;
import com.aerodream.demoTask.Repository.ApproveRepository;
import com.aerodream.demoTask.Repository.DocumentRepository;
import com.aerodream.demoTask.Repository.HistoryRepository;
import com.aerodream.demoTask.Service.DocumentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static liquibase.parser.AbstractFormattedChangeLogParser.AUTHOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DocumentServiceTest {

    @Mock
    DocumentRepository documentRepository;

    @Mock
    HistoryRepository historyRepository;

    @Mock
    ApproveRepository approveRepository;

    @InjectMocks
    DocumentService documentService;

    @Test
    void createDocument_shouldReturnSuccess() {
        DocumentCreateDto createDto = new DocumentCreateDto("Test-author", "title");
        DocumentEntity savedEntity = new DocumentEntity();
        savedEntity.setId(1L);
        savedEntity.setUuid(UUID.randomUUID());
        savedEntity.setAuthor(createDto.author());
        savedEntity.setName(createDto.name());
        savedEntity.setStatus(DocumentStatus.DRAFT);
        savedEntity.setCreatedAt(LocalDateTime.now());

        when(documentRepository.save(any(DocumentEntity.class))).thenReturn(savedEntity);

        DocumentWithHistoryResponseDto result = documentService.createDocument(createDto);

        assertThat(result.author()).isEqualTo(createDto.author());
        assertThat(result.name()).isEqualTo(createDto.name());
        assertThat(result.status()).isEqualTo(DocumentStatus.DRAFT.name());
        assertThat(result.history().isEmpty());
        verify(documentRepository).save(any(DocumentEntity.class));
    }

    @Test
    void submitDocuments_shouldReturnSuccess_forDraftDocument() {
        Long draftId = 1L;
        when(documentRepository.updateStatusIfDraft(draftId)).thenReturn(1);

        List<DocumentProcessResponseDto> results = documentService.submitDocuments(List.of(draftId), AUTHOR);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().Id()).isEqualTo(draftId);
        assertThat(results.getFirst().status()).isEqualTo(ProcessStatus.SUCCESS.name());

        verify(documentRepository).updateTimeStamp(draftId);
        verify(historyRepository).save(any(HistoryEntity.class));
        verify(documentRepository, never()).existsById(any());
    }

    @Test
    void submitDocuments_shouldReturnConflict_forNonDraftDocument() {
        Long nonDraftId = 2L;
        when(documentRepository.updateStatusIfDraft(nonDraftId)).thenReturn(0);
        when(documentRepository.existsById(nonDraftId)).thenReturn(true);

        List<DocumentProcessResponseDto> results = documentService.submitDocuments(List.of(nonDraftId), AUTHOR);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().status()).isEqualTo(ProcessStatus.CONFLICT.name());

        verify(documentRepository, never()).updateTimeStamp(any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    void submitDocuments_shouldReturnNotFound_forNonExistentDocument() {
        Long missingId = 3L;
        when(documentRepository.updateStatusIfDraft(missingId)).thenReturn(0);
        when(documentRepository.existsById(missingId)).thenReturn(false);

        List<DocumentProcessResponseDto> results = documentService.submitDocuments(List.of(missingId), AUTHOR);

        assertThat(results).hasSize(1);
        assertThat(results.getFirst().status()).isEqualTo(ProcessStatus.NOT_FOUND.name());

        verify(documentRepository, never()).updateTimeStamp(any());
        verify(historyRepository, never()).save(any());
    }

    @Test
    void approveDocuments_shouldHandleMixedResults() {
        Long submittedId = 1L;
        Long draftId = 2L;
        Long approvedId = 3L;
        Long missingId = 4L;

        when(documentRepository.updateStatusIfSubmitted(submittedId)).thenReturn(1);
        when(documentRepository.updateStatusIfSubmitted(draftId)).thenReturn(0);
        when(documentRepository.updateStatusIfSubmitted(approvedId)).thenReturn(0);
        when(documentRepository.updateStatusIfSubmitted(missingId)).thenReturn(0);

        when(documentRepository.existsById(draftId)).thenReturn(true);
        when(documentRepository.existsById(approvedId)).thenReturn(true);
        when(documentRepository.existsById(missingId)).thenReturn(false);

        List<Long> listId = List.of(submittedId, draftId, approvedId, missingId);

        List<DocumentProcessResponseDto> results = documentService.approveDocuments(listId, AUTHOR);

        assertThat(results).hasSize(4);
        assertThat(results.get(0).status()).isEqualTo(ProcessStatus.SUCCESS.name());
        assertThat(results.get(1).status()).isEqualTo(ProcessStatus.CONFLICT.name());
        assertThat(results.get(2).status()).isEqualTo(ProcessStatus.CONFLICT.name());
        assertThat(results.get(3).status()).isEqualTo(ProcessStatus.NOT_FOUND.name());

        verify(documentRepository).updateTimeStamp(submittedId);
        verify(historyRepository).save(any(HistoryEntity.class));
        verify(approveRepository).save(any(ApproveEntity.class));

        verify(documentRepository, never()).updateTimeStamp(draftId);
        verify(documentRepository, never()).updateTimeStamp(approvedId);
        verify(documentRepository, never()).updateTimeStamp(missingId);
        verify(historyRepository, times(1)).save(any()); // только один раз для успеха
        verify(approveRepository, times(1)).save(any());
    }
}
