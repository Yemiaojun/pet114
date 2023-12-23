package com.example.wechat.controller;

import com.example.wechat.service.DataUpdateService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import utils.Result;

@RestController
@RequestMapping("/dataUpdate")
public class DataUpdateController {

    @Autowired
    private DataUpdateService dataUpdateService;

    @ApiOperation(value="更新所有数据", notes = "触发文章、热点词和公众号热度的集中更新")
    @GetMapping("/updateAll")
    public String updateAllData() {
        dataUpdateService.updateAllData();
        return Result.okGetString("所有数据更新成功");
    }
}
