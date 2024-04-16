package com.example.wechat.service;

import com.example.wechat.model.File;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileService {
    Logger logger = LoggerFactory.getLogger(FileService.class);
    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFsOperations operations;



    public String uploadFile(MultipartFile file) throws IOException {
        String userId = UUID.randomUUID().toString();
        DBObject dbObject = new BasicDBObject();
        dbObject.put("fileName", file.getOriginalFilename());
        dbObject.put("contentType", file.getContentType());
        dbObject.put("size", file.getSize());
        dbObject.put("userId", userId);
        var objectId = gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), dbObject);
        logger.info(objectId.toString());
        return objectId.toString();
    }

    public File displayFile(String id) throws IOException {
        GridFSFile gridFSFile = gridFsTemplate.findOne(new Query(Criteria.where("_id").is(new ObjectId(id))));
        Document fileMetadata = gridFSFile.getMetadata();
        var contentType = (String) fileMetadata.get("contentType");
        InputStream inputStream = operations.getResource(gridFSFile).getInputStream();
        File file = new File(gridFSFile.getFilename(), contentType, inputStream.readAllBytes());
        return file;
    }

}
