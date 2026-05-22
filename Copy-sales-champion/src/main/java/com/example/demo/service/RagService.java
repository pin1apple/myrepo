
package com.example.demo.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RagService {

    @Autowired
    private OpenAiChatModel chatModel;

    @Autowired
    private EmbeddingModel embeddingModel;

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final List<TextSegment> allSegments;

    public RagService() {
        this.embeddingStore = new InMemoryEmbeddingStore<>();
        this.allSegments = new ArrayList<>();
    }

    public void addDocuments(List<String> documents) {
        try {
            DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);

            for (String doc : documents) {
                Document document = Document.from(doc);
                List<TextSegment> segments = splitter.split(document);

                for (TextSegment segment : segments) {
                    allSegments.add(segment);
                    Embedding embedding = embeddingModel.embed(segment).content();
                    embeddingStore.add(embedding, segment);
                }
            }

            log.info("成功添加 {} 个文档片段到向量存储", documents.size());
        } catch (Exception e) {
            log.error("添加文档失败", e);
        }
    }

    public String queryWithRag(String question) {
        try {
            StringBuilder relevantContext = new StringBuilder();

            Embedding questionEmbedding = embeddingModel.embed(question).content();

            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(questionEmbedding)
                    .maxResults(5)
                    .minScore(0.6)
                    .build();

            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
            List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();

            if (matches != null && !matches.isEmpty()) {
                relevantContext.append("相关知识点：\n\n");
                for (int i = 0; i < matches.size(); i++) {
                    EmbeddingMatch<TextSegment> match = matches.get(i);
                    relevantContext.append((i + 1)).append(". ").append(match.embedded().text()).append("\n\n");
                }
            }

            String prompt;
            if (relevantContext.length() > 0) {
                prompt = String.format(
                        "你是一个专业的销售AI助手。请基于以下相关知识回答问题：\n\n%s\n\n用户问题：%s\n\n请结合上述知识，提供专业、实用的销售建议。如果知识与问题无关，请基于你的专业知识回答。",
                        relevantContext.toString(), question
                );
            } else {
                prompt = String.format(
                        "你是一个专业的销售AI助手。请回答以下问题：%s\n\n请提供专业、实用的销售建议。",
                        question
                );
            }

            return chatModel.generate(prompt);
        } catch (Exception e) {
            log.error("RAG查询失败", e);
            return "抱歉，AI服务暂时不可用：" + e.getMessage();
        }
    }
}