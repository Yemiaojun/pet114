package utils;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.eq;

public class QuestionImporter {

    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
        MongoDatabase database = mongoClient.getDatabase("pet1");
        MongoCollection<Document> categoriesCollection = database.getCollection("category");
        MongoCollection<Document> questionsCollection = database.getCollection("question");

        String raw_data = """
                传染病
                题干: 犬瘟热的主要传播途径是什么？
                选项: ["空气传播", "直接接触", "通过跳蚤", "食物和水"]
                答案: A
                分值: 5
                """; // 你的数据字符串

        String[] parts = raw_data.trim().split("\n\n");
        String currentCategoryName = null;

        for (String part : parts) {
            String[] lines = part.split("\n");
            String firstLine = lines[0];

            if (!firstLine.startsWith("题干: ")) {
                currentCategoryName = firstLine;
                continue;
            }

            Document category = categoriesCollection.find(eq("name", currentCategoryName)).first();
            ObjectId category_id;

            if (category == null) {
                category_id = new ObjectId();
                categoriesCollection.insertOne(new Document("_id", category_id).append("name", currentCategoryName));
            } else {
                category_id = category.getObjectId("_id");
            }

            Document questionData = new Document()
                    .append("stem", lines[0].split(": ", 2)[1])
                    .append("optionList", lines[1].split(": ", 2)[1])
                    .append("answer", lines[2].split(": ", 2)[1])
                    .append("score", Integer.parseInt(lines[3].split(": ", 2)[1]))
                    .append("category", category_id) // 直接存储 ObjectId
                    .append("visible", true);

            questionsCollection.insertOne(questionData);
        }

        System.out.println("数据导入完成。");
        mongoClient.close();
    }
}
