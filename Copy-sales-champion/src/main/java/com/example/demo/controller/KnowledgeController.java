package com.example.demo.controller;

import com.example.demo.dto.ApiResponse;
import com.example.demo.dto.KnowledgeBaseRequest;
import com.example.demo.entity.SalesKnowledge;
import com.example.demo.repository.SalesKnowledgeRepository;
import com.example.demo.service.RagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/knowledge")
@CrossOrigin(origins = "*")
@Slf4j
public class KnowledgeController {

    @Autowired
    private SalesKnowledgeRepository knowledgeRepository;

    @Autowired
    private RagService ragService;

    @PostMapping("/add")
    public ApiResponse<SalesKnowledge> addKnowledge(@RequestBody KnowledgeBaseRequest request) {
        try {
            SalesKnowledge knowledge = new SalesKnowledge();
            knowledge.setTitle(request.getTitle());
            knowledge.setContent(request.getContent());
            knowledge.setCategory(request.getCategory());
            knowledge.setTags(request.getTags());

            knowledge = knowledgeRepository.save(knowledge);

            String document = request.getTitle() + "\n" + request.getContent();
            ragService.addDocuments(List.of(document));

            return ApiResponse.success(knowledge);
        } catch (Exception e) {
            log.error("添加知识库失败", e);
            return ApiResponse.error("添加知识库失败: " + e.getMessage());
        }
    }

    @GetMapping("/list")
    public ApiResponse<List<SalesKnowledge>> listKnowledge(
            @RequestParam(value = "category", required = false) String category) {
        try {
            List<SalesKnowledge> knowledgeList;
            if (category != null && !category.isEmpty()) {
                knowledgeList = knowledgeRepository.findByCategory(category);
            } else {
                knowledgeList = knowledgeRepository.findAll();
            }
            return ApiResponse.success(knowledgeList);
        } catch (Exception e) {
            return ApiResponse.error("获取知识库失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ApiResponse<List<SalesKnowledge>> searchKnowledge(@RequestParam String keyword) {
        try {
            List<SalesKnowledge> knowledgeList = knowledgeRepository.findByTitleContainingIgnoreCase(keyword);
            return ApiResponse.success(knowledgeList);
        } catch (Exception e) {
            return ApiResponse.error("搜索知识库失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteKnowledge(@PathVariable Long id) {
        try {
            knowledgeRepository.deleteById(id);
            return ApiResponse.success(null);
        } catch (Exception e) {
            return ApiResponse.error("删除知识库失败: " + e.getMessage());
        }
    }
}