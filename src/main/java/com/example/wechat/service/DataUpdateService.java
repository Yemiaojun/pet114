package com.example.wechat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DataUpdateService {

    @Autowired
    private ArticleService articleService;
    @Autowired
    private WechatAccountService wechatAccountService;
    @Autowired
    private HotwordService hotwordService;

    public void updateAllData() {
        // 更新文章热度

        // 处理文章并更新热点词
        hotwordService.processArticlesAndUpdateHotwords();

        // 更新公众号热度
        wechatAccountService.updateAccountHeat();
    }
}
