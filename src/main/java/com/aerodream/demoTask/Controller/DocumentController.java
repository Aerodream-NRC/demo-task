package com.aerodream.demoTask.Controller;

import com.aerodream.demoTask.Dto.Document.*;
import com.aerodream.demoTask.Enum.DocumentStatus;
import com.aerodream.demoTask.Service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping("/create")
    public ResponseEntity<DocumentWithHistoryResponseDto> createDocument(@Valid @RequestBody DocumentCreateDto createDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(documentService.createDocument(createDto));
    }

    @GetMapping("/{id:\\d+}")
    public ResponseEntity<DocumentWithHistoryResponseDto> getDocumentById(@PathVariable long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponseDto>> getDocuments(
            @RequestParam(required = false) List<Long> listId,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(documentService.getDocuments(listId, pageable));
    }

    @PatchMapping("/submit")
    public ResponseEntity<List<DocumentProcessResponseDto>> submitDocuments(
            @RequestParam List<Long> listId,
            @Valid @RequestBody DocumentProcessRequestDto requestDto
    ) {
        return ResponseEntity.ok(documentService.submitDocuments(listId, requestDto.author()));
    }

    @PatchMapping("/approve")
    public ResponseEntity<List<DocumentProcessResponseDto>> approveDocuments(
            @RequestParam List<Long> listId,
            @Valid @RequestBody DocumentProcessRequestDto requestDto
    ) {
        return ResponseEntity.ok(documentService.approveDocuments(listId, requestDto.author()));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentResponseDto>> searchDocuments(
            @RequestParam(required = false) DocumentStatus status,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @PageableDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<DocumentResponseDto> responseDto = documentService.searchDocuments(status, author, dateFrom, dateTo, pageable);
        return ResponseEntity.ok(responseDto);
    }
}