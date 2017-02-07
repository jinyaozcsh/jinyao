package cn.swao.jinyao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.swao.jinyao.model.BaseCatch;

public interface BaseRepository<T extends BaseCatch> extends MongoRepository<T, String> {

    T findBySourceUrl(String sourceUrl);
}
