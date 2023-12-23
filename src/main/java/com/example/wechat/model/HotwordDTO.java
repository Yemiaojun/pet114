package com.example.wechat.model;

import lombok.Data;
import org.bson.types.ObjectId;
@Data
public class HotwordDTO {
    private ObjectId id;
    private int rank;
    private String content;
    private double heat;
    private String trend;
}
