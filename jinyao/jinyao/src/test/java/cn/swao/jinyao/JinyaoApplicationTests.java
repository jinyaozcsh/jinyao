package cn.swao.jinyao;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.mongodb.MongoClient;
import com.mongodb.client.*;

import cn.swao.jinyao.repository.NewsRepository;
import cn.swao.jinyao.repository.model.News;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JinyaoApplicationTests {

    @Autowired
    private NewsRepository newsRepository;
    @Autowired
   private  MongoClient MongoClient;

    @Test
    public void mogoSave(){
        newsRepository.save(new News("hello"));
        News news = newsRepository.findBySourceUrl("hello");
        MongoDatabase mongoDatabase = MongoClient.getDatabase("eastnb");
        MongoCollection<Document> collection = mongoDatabase.getCollection("news");
        Document next = collection.find().iterator().next();
        System.out.println(news.getSourceUrl()+"   "+news.getId());
    }
}
