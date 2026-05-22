
package com.example.demo.repository;

import com.example.demo.entity.SalesSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesSessionRepository extends JpaRepository<SalesSession, Long> {
    List<SalesSession> findByStatus(String status);
    List<SalesSession> findBySessionDateAfterOrderBySessionDateDesc(java.time.LocalDateTime date);
}