package com.example.wechat.service;

import com.example.wechat.model.Article;
import com.example.wechat.model.HotPushDTO;
import com.example.wechat.model.WechatAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ArticleService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void updateHeat() {
        List<Article> articles = mongoTemplate.findAll(Article.class);

        LocalDateTime now = LocalDateTime.now();
        for (Article article : articles) {
            LocalDateTime publishTime = convertToLocalDateTime(article.getTime());
            long daysSincePublished = Duration.between(publishTime, now).toDays();
            int newHeat = calculateHeat(article, daysSincePublished);
            article.setHeat(newHeat);
            mongoTemplate.save(article);
        }
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    private int calculateHeat(Article article, long daysSincePublished) {
        // 示例计算公式，可以根据需要调整
        double timeFactor = Math.exp(-0.05 * daysSincePublished); // 时间因子，随时间减小
        int interactionFactor = article.getLikes() + article.getRead(); // 互动因子，点赞数和阅读量

        // 计算热度值，确保一个月前的文章热度趋近于零
        return (int) (interactionFactor * timeFactor);
    }

    public List<?> searchInfo(String keyword, String type) {
        if ("article".equals(type)) {
            return mongoTemplate.find(Query.query(Criteria.where("title").regex(keyword)), Article.class);
        } else if ("account".equals(type)) {
            return mongoTemplate.find(Query.query(Criteria.where("name").regex(keyword)), WechatAccount.class);
        }
        return null;
    }

    public List<HotPushDTO> getHotPushes() {
        Sort sort = Sort.by(Sort.Direction.DESC, "heat");
        List<Article> articles = mongoTemplate.find(Query.query(new Criteria()).with(sort).limit(15), Article.class);

        List<HotPushDTO> hotPushDTOs = new ArrayList<>();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            HotPushDTO dto = new HotPushDTO();
            dto.setId(article.getId());
            dto.setRank(i + 1);
            dto.setTitle(article.getTitle());
            dto.setContent(article.getContent().substring(0, Math.min(20, article.getContent().length())));
            dto.setPublicTime(article.getTime());
            dto.setAccount(article.getWriter());
            dto.setCover(article.getCover());
            dto.setUrl(article.getLink());
            hotPushDTOs.add(dto);
        }
        return hotPushDTOs;
    }

    public List<HotPushDTO> getHistoryPushList(String accountName) {
        Sort sort = Sort.by(Sort.Direction.DESC, "time");
        List<Article> articles = mongoTemplate.find(Query.query(Criteria.where("writer").is(accountName)).with(sort), Article.class);

        List<HotPushDTO> hotPushDTOs = new ArrayList<>();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            HotPushDTO dto = new HotPushDTO();
            dto.setId(article.getId());
            dto.setRank(i + 1);
            dto.setTitle(article.getTitle());
            dto.setContent(article.getContent().substring(0, Math.min(20, article.getContent().length())));
            dto.setPublicTime(article.getTime());
            dto.setAccount(article.getWriter());
            dto.setCover(article.getCover());
            dto.setUrl(article.getLink());
            hotPushDTOs.add(dto);
        }
        return hotPushDTOs;
    }


}
