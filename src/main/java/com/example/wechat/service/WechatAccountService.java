package com.example.wechat.service;

import com.example.wechat.model.Article;
import com.example.wechat.model.WechatAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WechatAccountService {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ArticleService articleService;

    public void updateAccountHeat() {
        // 首先更新所有文章的热度
        articleService.updateHeat();

        // 然后更新每个公众号的热度
        List<WechatAccount> accounts = mongoTemplate.findAll(WechatAccount.class);
        for (WechatAccount account : accounts) {
            // 重置热度
            account.setHeat(0);

            // 计算公众号下所有文章的热度总和
            List<Article> articles = mongoTemplate.find(Query.query(Criteria.where("writer").is(account.getName())), Article.class);
            int totalHeat = articles.stream().mapToInt(Article::getHeat).sum();
            account.setHeat(totalHeat);

            mongoTemplate.save(account);
        }
    }

    public List<WechatAccount> getHotAccounts() {
        Sort sort = Sort.by(Sort.Direction.DESC, "heat");
        return mongoTemplate.find(Query.query(new Criteria()).with(sort).limit(20), WechatAccount.class);
    }
}

