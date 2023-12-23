package com.example.wechat.controller;

import com.example.wechat.service.SearchService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utils.Result;

import java.util.List;

@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    private SearchService searchService;

    @ApiOperation(value="搜索文章或公众号信息", notes = "根据关键词和类型搜索文章或公众号")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword", value = "关键词", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型（article或account）", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/searchInfo")
    public String searchInfo(@RequestParam("keyword") String keyword, @RequestParam("type") String type) {
        List<?> results = searchService.searchInfo(keyword, type);
        return Result.okGetStringByData("搜索结果", results);
    }
}
