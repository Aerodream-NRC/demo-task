package com.aerodream.demoTask.Entity.Specification;

import com.aerodream.demoTask.Entity.DocumentEntity;
import com.aerodream.demoTask.Enum.DocumentStatus;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

public class DocumentSpecifications {

    public static Specification<DocumentEntity> hasStatus(DocumentStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<DocumentEntity> authorContains(String author) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(author)) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase() + "%");
        };
    }

    public static Specification<DocumentEntity> createdAtBetween(LocalDateTime dateFrom,
                                                                 LocalDateTime dateTo) {
        return (root, query, criteriaBuilder) -> {
            if (dateFrom == null && dateTo == null) return null;
            if (dateFrom != null && dateTo != null) return criteriaBuilder.between(root.get("createdAt"), dateFrom, dateTo);
            else if (dateFrom != null) return criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), dateFrom);
            else return criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), dateTo);
        };
    }
}
