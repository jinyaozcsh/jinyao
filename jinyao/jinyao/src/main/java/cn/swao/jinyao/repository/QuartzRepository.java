package cn.swao.jinyao.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import cn.swao.jinyao.model.Quartz;

@Repository
public interface QuartzRepository extends MongoRepository<Quartz, String> {

}
