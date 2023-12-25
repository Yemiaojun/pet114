package com.example.wechat.service;

import com.example.wechat.model.Article;
import com.example.wechat.model.Hotword;
import com.example.wechat.model.HotwordDTO;
import keyword.Keyword;
import keyword.TFIDFAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class HotwordService {

    @Autowired
    private MongoTemplate mongoTemplate;

    // 返回前十个热点词（仅word字段），按最新热度排序
    public List<String> getTopTenHotwords() {
        Query query = new Query().with(Sort.by(Sort.Direction.DESC, "cloutList[0]")).limit(10);
        List<Hotword> hotwords = mongoTemplate.find(query, Hotword.class);
        return hotwords.stream().map(Hotword::getWord).collect(Collectors.toList());
    }

    // 根据word返回对应热点词的热度数组
    public double[] getCloutByWord(String word) {
        Hotword hotword = mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Hotword.class);
        return (hotword != null) ? hotword.getCloutList() : new double[5];
    }


    // 更新指定热点词的热度
    public void updateHotwordClout(String word, double newClout) {
        Hotword hotword = mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Hotword.class);
        if (hotword != null) {
            double[] cloutList = hotword.getCloutList();
            // 向右移动数组中的元素，移除最后一个元素
            for (int i = cloutList.length - 1; i > 0; i--) {
                cloutList[i] = cloutList[i - 1];
            }
            cloutList[0] = newClout; // 在数组的开头添加最新的热度
            hotword.setCloutList(cloutList);
            hotword.setCurrentHeat(newClout);
            mongoTemplate.save(hotword);
        }
    }

    public void createOrUpdateHotword(String word) {
        Hotword hotword = mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Hotword.class);

        // 如果不存在，创建新的Hotword
        if (hotword == null) {
            hotword = new Hotword();
            hotword.setWord(word);
            double[] initialClout = new double[]{0, 0, 0, 0, 0};
            hotword.setCloutList(initialClout);
            hotword.setTotalAppearance(0); // 初始出现次数设置为0
            hotword.setCurrentHeat(0);
            mongoTemplate.save(hotword);
        }
        // 如果已存在，无需额外操作
    }

    public void analyzeTextAndCreateOrUpdateHotwords(int topN) {
        String content = readContentFromFile("src/test.txt");
        TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
        List<Keyword> keywords = tfidfAnalyzer.analyze(content, topN);

        for (Keyword keyword : keywords) {
            createOrUpdateHotword(keyword.getName());
        }
    }

    private String readContentFromFile(String filePath) {
        try {
            return Files.lines(Paths.get(filePath)).collect(Collectors.joining(" "));
        } catch (IOException e) {
            throw new RuntimeException("读取文件时出错: " + e.getMessage());
        }
    }

    public void analyzeAndUpdateHotwords(int topN) {
        String content = readContentFromFile("src/test.txt");
        TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
        List<Keyword> keywords = tfidfAnalyzer.analyze(content, topN);

        for (Keyword keyword : keywords) {
            Hotword hotword = getOrCreateHotword(keyword.getName());
            hotword.addHeat(keyword.getTfidfvalue());
            mongoTemplate.save(hotword);
        }
    }

    private Hotword getOrCreateHotword(String word) {
        Hotword hotword = mongoTemplate.findOne(Query.query(Criteria.where("word").is(word)), Hotword.class);
        if (hotword == null) {
            hotword = new Hotword(word, 0);
        }
        return hotword;
    }

  //未实现服务
 // 先更新词汇今天热度为0.6x上一天，统计昨天一天文章中的热点词之后，把新的值加上去开头

    public void updateData(String content,int topN) {
        TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
        List<Keyword> keywords = tfidfAnalyzer.analyze(content, topN);

        for (Keyword keyword : keywords) {
            Hotword hotword = getOrCreateHotword(keyword.getName());
            hotword.addHeat(keyword.getTfidfvalue());
            mongoTemplate.save(hotword);
        }
    }


    public void processArticlesAndUpdateHotwords() {
        List<Hotword> allHotwords = mongoTemplate.findAll(Hotword.class);
        for (Hotword hotword : allHotwords) {
            hotword.setCurrentHeat(0);
            mongoTemplate.save(hotword);
        }
        // 获取所有未处理过的文章
        List<Article> articles = mongoTemplate.find(Query.query(Criteria.where("visit").is(false)), Article.class);

        for (Article article : articles) {
            // 处理每篇文章
            updateData(article.getContent(), 10);
            article.setVisit(true); // 标记为已处理
            mongoTemplate.save(article);
        }

        // 更新所有Hotword的热度
        List<Hotword> hotwords = mongoTemplate.findAll(Hotword.class);
        for (Hotword hotword : hotwords) {
            double newClout = 0.6 * hotword.getCloutList()[0] + hotword.getCurrentHeat();
            updateHotwordClout(hotword.getWord(), newClout);
        }
    }

    public List<HotwordDTO> getTopHotwords() {
        // 从数据库中获取所有热词，并按照currentHeat字段降序排序
        Sort sort = Sort.by(Sort.Direction.DESC, "currentHeat");
        List<Hotword> hotwords = mongoTemplate.find(Query.query(new Criteria()).with(sort), Hotword.class);

        // 过滤掉只包含数字和标点的热词
        Pattern pattern = Pattern.compile("[^\\d\\p{Punct}！ ） ]");
        List<Hotword> filteredHotwords = hotwords.stream()
                .filter(hotword -> pattern.matcher(hotword.getWord()).find())
                .limit(15)
                .collect(Collectors.toList());

        // 转换为DTO
        List<HotwordDTO> hotwordDTOs = new ArrayList<>();
        for (int i = 0; i < filteredHotwords.size(); i++) {
            Hotword hotword = filteredHotwords.get(i);
            HotwordDTO dto = new HotwordDTO();
            dto.setId(hotword.getId());
            dto.setRank(i + 1);
            dto.setContent(hotword.getWord());
            dto.setHeat(hotword.getCurrentHeat()); // 使用currentHeat作为热度值
            dto.setTrend(hotword.getCurrentHeat() > hotword.getCloutList()[1] ? "上升" : "下降"); // 比较趋势
            hotwordDTOs.add(dto);
        }
        return hotwordDTOs;
    }






}
