package com.aerodream.demoTask.Exception;

public class DocumentNotFoundException extends RuntimeException {

    public DocumentNotFoundException(Long id) {
        super("Document not found with ID: " + id);
    }
}