package cn.swao.jinyao.util;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.*;

public class MongodbUtils {

    public static MongoCursor<Document> getMongoCursor(String collectionName) {
        MongoCollection<Document> collection = getCollection(collectionName);
        return collection.find().iterator();
    }

    public static MongoDatabase getMongoDatabase() {
        // 连接到数据库
        MongoDatabase mongoDatabase = null;
        try {
            MongoClient mongoClient = new MongoClient("123.46.123.222", 27017);
            mongoDatabase = mongoClient.getDatabase("wnb");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mongoDatabase;
    }

    public static MongoCollection<Document> getCollection(String collectionName) {
        MongoDatabase mongoDatabase = getMongoDatabase();
        MongoCollection<Document> collection = mongoDatabase.getCollection(collectionName);
        return collection;
    }

    public static MongoCursor<Document> getCollectionIt(String str) {
        MongoCollection<Document> collection = MongodbUtils.getCollection(str);
        MongoCursor<Document> it = collection.find().iterator();
        return it;
    }
}
