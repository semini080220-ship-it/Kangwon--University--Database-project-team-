package com.samcheok.ecotour.repository;

import com.samcheok.ecotour.domain.VisitLog;
import com.samcheok.ecotour.domain.VisitType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitLogRepository extends JpaRepository<VisitLog, Long> {

    List<VisitLog> findByUserId(Long userId);

    long countByUserIdAndVisitType(Long userId, VisitType visitType);

    /** 동일 관광지·동일 유형 중복 인증 방지용. */
    boolean existsByUserIdAndAttractionIdAndVisitType(Long userId, Long attractionId, VisitType visitType);
}
