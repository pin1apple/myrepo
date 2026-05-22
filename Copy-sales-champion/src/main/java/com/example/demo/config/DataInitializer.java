package com.example.demo.config;

import com.example.demo.entity.SalesKnowledge;
import com.example.demo.repository.SalesKnowledgeRepository;
import com.example.demo.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private SalesKnowledgeRepository knowledgeRepository;

    @Autowired
    private RagService ragService;

    @Override
    public void run(String... args) throws Exception {
        if (knowledgeRepository.count() == 0) {
            System.out.println("初始化示例知识库数据...");

            SalesKnowledge k1 = new SalesKnowledge();
            k1.setTitle("优秀开场白技巧");
            k1.setContent("开场白是销售对话的关键。好的开场白应该：1. 简洁明了，控制在30秒内；2. 突出价值，让客户感受到你能解决什么问题；3. 引发兴趣，通过提问或分享成功案例吸引客户注意；4. 建立信任，展示专业性和可靠性。示例：'王总您好，我了解到贵公司在客户服务方面投入很大，我们帮助过类似企业将客户满意度提升了30%，今天想和您分享一下这些成功经验...'");
            k1.setCategory("opening");
            k1.setTags("开场白,电话销售,初次接触");

            SalesKnowledge k2 = new SalesKnowledge();
            k2.setTitle("价格异议处理方法");
            k2.setContent("当客户说'太贵了'时，不要急于降价。应该：1. 确认客户的真实顾虑，是真的预算问题还是价值认知问题；2. 重新强调产品价值和ROI；3. 分解成本，让客户看到每天的实际投入很小；4. 提供灵活的付款方案；5. 对比竞争对手，突出差异化优势。关键话术：'我理解您对价格的关注，让我们一起来看看这个投资能为您带来什么回报...'");
            k2.setCategory("objection");
            k2.setTags("价格异议,谈判,成交");

            SalesKnowledge k3 = new SalesKnowledge();
            k3.setTitle("SPIN提问技巧");
            k3.setContent("SPIN是一种强大的销售提问框架：S(Situation)情境问题 - 了解客户现状；P(Problem)难点问题 - 发现客户痛点；I(Implication)暗示问题 - 放大问题的影响；N(Need-payoff)需求效益问题 - 引导客户说出解决方案的价值。使用顺序很重要，先从情境问题开始，逐步深入到需求效益问题，让客户自己意识到解决问题的必要性。");
            k3.setCategory("questioning");
            k3.setTags("SPIN,提问技巧,需求挖掘");

            SalesKnowledge k4 = new SalesKnowledge();
            k4.setTitle("成交信号识别");
            k4.setContent("客户发出以下信号时可以考虑成交：1. 询问具体细节（价格、交付时间、售后服务等）；2. 开始想象使用场景（'这个功能怎么用？'）；3. 征求其他人意见（'我需要和团队商量一下'）；4. 重复确认某个优点；5. 身体语言变得积极；6. 问题从'要不要买'转向'怎么买'。此时应该主动提出成交：'如果您没有其他疑问，我们现在就可以安排合同事宜...'");
            k4.setCategory("closing");
            k4.setTags("成交信号,闭环,签约");

            knowledgeRepository.saveAll(List.of(k1, k2, k3, k4));

            ragService.addDocuments(List.of(
                    k1.getTitle() + "\n" + k1.getContent(),
                    k2.getTitle() + "\n" + k2.getContent(),
                    k3.getTitle() + "\n" + k3.getContent(),
                    k4.getTitle() + "\n" + k4.getContent()
            ));

            System.out.println("✅ 知识库初始化完成！");
        }
    }
}