package com.aerodream.demoTask.Repository;

import com.aerodream.demoTask.Entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, Long> {
    List<HistoryEntity> findAllByDocumentId(Long DocumentId);
}
