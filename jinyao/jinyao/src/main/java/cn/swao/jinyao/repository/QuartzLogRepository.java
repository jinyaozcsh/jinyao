package cn.swao.jinyao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import cn.swao.jinyao.model.QuartzLog;

public interface QuartzLogRepository extends MongoRepository<QuartzLog, String> {

}
