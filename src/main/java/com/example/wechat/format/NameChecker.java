package com.example.wechat.format;

import com.example.wechat.exception.DefaultException;

import java.util.regex.Pattern;

public class NameChecker {
    public static void nameIsLegal(String name) throws DefaultException {
        // 检查名称是否为空或者只包含空格
        if (name == null || name.trim().isEmpty()) throw new DefaultException("名字不能为空");

        // 检查名称长度是否在2到50之间
        if (name.length() <  1 || name.length() > 50) throw new DefaultException("名字必须为1-50字之间");

        // 检查名称中是否只包含汉语或英语字符
        Pattern pattern = Pattern.compile("^[\\u4e00-\\u9fa5a-zA-Z]+$");
        if(! pattern.matcher(name).matches()) throw new DefaultException("名字只能包含中文或英文");
    }
}
