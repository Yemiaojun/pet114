package com.example.wechat.service;

import com.example.wechat.model.Article;
import com.example.wechat.model.HotPushDTO;
import com.example.wechat.model.WechatAccount;
import com.example.wechat.model.WechatAccountDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class SearchService {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<?> searchInfo(String keyword, String type) {
        if ("article".equals(type)) {
            Sort sort = Sort.by(Sort.Direction.DESC, "heat");
            List<Article> articles = mongoTemplate.find(Query.query(Criteria.where("title").regex(keyword)).with(sort), Article.class);
            return convertToHotPushDTOs(articles);
        } else if ("account".equals(type)) {
            Sort sort = Sort.by(Sort.Direction.DESC, "heat");
            List<WechatAccount> accounts = mongoTemplate.find(Query.query(Criteria.where("name").regex(keyword)).with(sort), WechatAccount.class);
            return convertToAccountDTOs(accounts);
        }
        return null;
    }

    private List<HotPushDTO> convertToHotPushDTOs(List<Article> articles) {
        List<HotPushDTO> dtos = new ArrayList<>();
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            HotPushDTO dto = new HotPushDTO();
            dto.setId(article.getId());
            dto.setRank(i + 1);
            dto.setTitle(article.getTitle());
            dto.setContent(article.getContent().substring(0, Math.min(60, article.getContent().length())));
            dto.setPublicTime(article.getTime());
            dto.setAccount(article.getWriter());
            dto.setCover(article.getCover());
            dto.setUrl(article.getLink());
            dtos.add(dto);
        }
        return dtos;
    }

    private List<WechatAccountDTO> convertToAccountDTOs(List<WechatAccount> accounts) {
        List<WechatAccountDTO> dtos = new ArrayList<>();
        for (int i = 0; i < accounts.size(); i++) {
            WechatAccount account = accounts.get(i);
            WechatAccountDTO dto = new WechatAccountDTO();

            dto.setId(account.getId());
            dto.setRank(i + 1); // 排名基于列表中的位置
            dto.setName(account.getName());
            dto.setFollowers(account.getFollowers());
            dto.setTotalRead(account.getTotalRead());
            dto.setDomain(account.getDomain());
            dto.setHeadpic(account.getHeadpic());

            dtos.add(dto);
        }
        return dtos;
    }

}
