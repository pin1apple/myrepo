
package com.example.demo.repository;

import com.example.demo.entity.AnalysisReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalysisReportRepository extends JpaRepository<AnalysisReport, Long> {
    AnalysisReport findBySessionId(Long sessionId);
}