package com.aerodream.demoTask.Entity;

import com.aerodream.demoTask.Enum.HistoryAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "history")
public class HistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "document_id", nullable = false)
    private long documentId;

    @Column(nullable = false)
    private String author;

    @Column(name = "happend_at", nullable = false)
    private LocalDateTime happendAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HistoryAction action;

    private String description;

    @Override
    public String toString() {
        return "HistoryEntity{" +
                "documentId=" + documentId +
                ", author='" + author + '\'' +
                ", happendAt=" + happendAt +
                ", action=" + action +
                ", description='" + description + '\'' +
                '}';
    }
}
