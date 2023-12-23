package com.example.wechat.controller;

// 导入声明...
import com.example.wechat.model.HotPushDTO;
import com.example.wechat.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utils.Result;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.util.List;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    @ApiOperation(value="获取热门推送", notes = "返回按热度排序的热门推送列表")
    @GetMapping("/hotPushes")
    public String getHotPushes() {
        List<HotPushDTO> hotPushes = articleService.getHotPushes();
        return Result.okGetStringByData("获取热门推送成功", hotPushes);
    }

    @ApiOperation(value="获取公众号历史推送", notes = "返回指定公众号的历史推送列表，按发表时间排序")
    @ApiImplicitParam(name = "accountName", value = "公众号名称", required = true, dataType = "String", paramType = "query")
    @GetMapping("/historyPushList")
    public String getHistoryPushList(@RequestParam("accountName") String accountName) {
        List<HotPushDTO> historyPushes = articleService.getHistoryPushList(accountName);
        return Result.okGetStringByData("获取公众号历史推送成功", historyPushes);
    }



}
