package cn.swao.jinyao.repository;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import cn.swao.jinyao.model.SquareDance;

/**
 * @author : kangwg 2017年2月13日
 *
 */
@Repository
public interface SquareDanceRepository extends BaseRepository<SquareDance> {
    @Query(value="{'alias':?0,'publish_time':?1}")
    SquareDance getRepeat(String alias, String publish_time);

}
