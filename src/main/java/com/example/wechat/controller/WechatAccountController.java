package com.example.wechat.controller;

import com.example.wechat.model.WechatAccount;
import com.example.wechat.service.WechatAccountService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import utils.Result;

import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/wechatAccount")
public class WechatAccountController {

    @Autowired
    private WechatAccountService wechatAccountService;

    @ApiOperation(value="获取热门公众号", notes = "返回按热度排序的前二十个热门公众号")
    @GetMapping("/hotAccounts")
    public String getHotAccounts() {
        List<WechatAccount> hotAccounts = wechatAccountService.getHotAccounts();
        return Result.okGetStringByData("获取热门公众号成功", hotAccounts);
    }


    @PostMapping("/updateAccountHeat")
    public String updateAccountHeat() {
       wechatAccountService.updateAccountHeat();
        return Result.okGetString("获取热门公众号成功");
    }

}
