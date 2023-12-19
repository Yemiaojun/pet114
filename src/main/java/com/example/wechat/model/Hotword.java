package com.example.wechat.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;
import java.util.Arrays;

@Document
public class Hotword {
    @Id
    private ObjectId id; // 自动生成的MongoDB ObjectId
    private String word; // 热点词
    private double[] cloutList = new double[5]; // 保存最近5个热度值的数组
    private int totalAppearance; // 在文章中的总出现次数
    private double currentHeat; // 当前热度

    public Hotword() {
        // 默认构造函数
        Arrays.fill(cloutList, 0); // 初始化数组值为0
    }

    public Hotword(String word, int totalAppearance) {
        this.word = word;
        this.totalAppearance = totalAppearance;
        Arrays.fill(cloutList, 0); // 初始化数组值为0
    }

    // Getter 和 Setter 方法
    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public double[] getCloutList() {
        return cloutList;
    }

    public void setCloutList(double[] cloutList) {
        this.cloutList = cloutList;
    }

    public int getTotalAppearance() {
        return totalAppearance;
    }

    public void setTotalAppearance(int totalAppearance) {
        this.totalAppearance = totalAppearance;
    }

    public double getCurrentHeat() {
        return currentHeat;
    }

    public void setCurrentHeat(double currentHeat) {
        this.currentHeat = currentHeat;
    }

    // 增加当前热度
    public void addHeat(double heat) {
        this.currentHeat += heat;
    }

    // 更新热度值到cloutList并重置当前热度
    public void updateAndResetHeat() {
        updateClout(this.currentHeat); // 注意：此处将double转换为int，可能会有精度损失
        this.currentHeat = 0.0;
    }

    // 更新热度值
    public void updateClout(double clout) {
        // 向右移动数组中的元素，移除最后一个元素
        for (int i = cloutList.length - 1; i > 0; i--) {
            cloutList[i] = cloutList[i - 1];
        }
        // 在数组的开头添加最新的热度
        cloutList[0] = clout;
    }
}
