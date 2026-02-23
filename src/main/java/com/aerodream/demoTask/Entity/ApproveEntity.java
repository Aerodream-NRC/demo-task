package com.aerodream.demoTask.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "approve")
public class ApproveEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(nullable = false)
    private String author;

    @Column(name = "document_id", nullable = false, unique = true)
    private long documentId;

    @Column(name = "approved_at", nullable = false)
    private LocalDateTime approvedAt;
}