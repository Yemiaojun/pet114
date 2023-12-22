package com.example.wechat.controller;

import com.example.wechat.service.HotwordService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import utils.Result;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/hotword")
public class HotwordController {

    @Autowired
    private HotwordService hotwordService;

    @ApiOperation(value="获取前十个热点词", notes = "返回热度最高的前十个热点词")
    @GetMapping("/top")
    public String getTopTenHotwords() {
        List<String> topHotwords = hotwordService.getTopTenHotwords();
        return Result.okGetStringByData("获取前十个热点词成功", topHotwords);
    }

    @ApiOperation(value="获取特定热点词的热度数组", notes = "根据热点词获取其热度数组")
    @ApiImplicitParam(name = "word", value = "热点词", required = true, dataType = "String", paramType = "query")
    @GetMapping("/clout")
    public String getCloutByWord(@RequestParam("word") String word) {
        double[] cloutArray = hotwordService.getCloutByWord(word);
        return Result.okGetStringByData("获取热度数组成功", Arrays.asList(cloutArray));
    }

    @ApiOperation(value="测试更新热点词的热度", notes = "更新特定热点词的热度，并返回更新后的热度数组")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "word", value = "热点词", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "newClout", value = "新的热度值", required = true, dataType = "int", paramType = "query")
    })
    @PutMapping("/testUpdate")
    public String testUpdateHotword(@RequestParam("word") String word,
                                    @RequestParam("newClout") int newClout) {
        hotwordService.updateHotwordClout(word, newClout);
        double[] updatedCloutArray = hotwordService.getCloutByWord(word);
        return Result.okGetStringByData("更新热度成功", Arrays.asList(updatedCloutArray));
    }

    @ApiOperation(value="创建或更新热点词", notes = "如果热点词不存在则创建，存在则确认其存在")
    @ApiImplicitParam(name = "word", value = "热点词", required = true, dataType = "String", paramType = "query")
    @PostMapping("/createOrUpdate")
    public String createOrUpdateHotword(@RequestParam("word") String word) {
        hotwordService.createOrUpdateHotword(word);
        return Result.okGetString("热点词已创建或确认存在");
    }

    @ApiOperation(value="分析文本并更新热点词", notes = "分析给定文本，提取并更新热点词")
    @ApiImplicitParam(name = "topN", value = "提取的热点词数量", required = true, dataType = "int", paramType = "query")
    @PostMapping("/analyzeAndUpdate")
    public String analyzeAndUpdateHotwords(@RequestParam("topN") int topN) {
        hotwordService.analyzeAndUpdateHotwords(topN);
        return Result.okGetString("文本分析完成，热点词已更新");
    }


}
