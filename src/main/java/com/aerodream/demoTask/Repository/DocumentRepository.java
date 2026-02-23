package com.aerodream.demoTask.Repository;

import com.aerodream.demoTask.Entity.DocumentEntity;
import com.aerodream.demoTask.Enum.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<DocumentEntity, Long>, JpaSpecificationExecutor<DocumentEntity> {

    Page<DocumentEntity> findByIdIn(List<Long> listId, Pageable pageable);

    long countByStatus(DocumentStatus status);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.updatedAt = CURRENT_TIMESTAMP WHERE d.id = :id")
    void updateTimeStamp(@Param("id") Long id);

    @Query("SELECT d.id FROM DocumentEntity d WHERE d.status = :status ORDER BY d.createdAt")
    List<Long> findIdByStatus(@Param("status")DocumentStatus status, Pageable pageable);

    @Query("SELECT d.id FROM DocumentEntity d WHERE d.status = :status ORDER BY d.createdAt")
    List<Long> findIdByStatus(@Param("status")DocumentStatus status);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.status = 'SUBMITTED' WHERE d.id IN :listId AND d.status = 'DRAFT'")
    int updateStatusIfDraft(@Param("ListId") List<Long> listId);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.status = 'APPROVED' WHERE d.id IN :listId AND d.status = 'SUBMITTED'")
    int updateStatusIfSubmitted(@Param("listId") List<Long> listId);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.status = 'SUBMITTED' WHERE d.id = :id AND d.status = 'DRAFT'")
    int updateStatusIfDraft(@Param("id") Long id);

    @Modifying
    @Query("UPDATE DocumentEntity d SET d.status = 'APPROVED' WHERE d.id = :id AND d.status = 'SUBMITTED'")
    int updateStatusIfSubmitted(@Param("id") Long id);
}
