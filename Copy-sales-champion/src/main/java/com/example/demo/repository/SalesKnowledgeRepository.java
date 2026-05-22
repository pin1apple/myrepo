package com.example.demo.repository;


import com.example.demo.entity.SalesKnowledge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesKnowledgeRepository extends JpaRepository<SalesKnowledge, Long> {
    List<SalesKnowledge> findByCategory(String category);
    List<SalesKnowledge> findByTitleContainingIgnoreCase(String title);
}