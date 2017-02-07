package cn.swao.jinyao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.swao.jinyao.model.News;

@Repository
public interface NewsRepository extends MongoRepository<News, String> {
    News findBySourceUrl(String sourceUrl);
}
