package cn.swao.jinyao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.swao.jinyao.model.Activity;

@Repository
public interface ActivityRepository extends MongoRepository<Activity, String> {
    Activity findBySourceUrl(String sourceUrl);
}
