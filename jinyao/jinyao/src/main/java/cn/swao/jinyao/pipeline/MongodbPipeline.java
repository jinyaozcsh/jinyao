package cn.swao.jinyao.pipeline;

import java.util.*;

import org.assertj.core.util.Strings;

import cn.swao.jinyao.model.BaseCatch;
import cn.swao.jinyao.repository.BaseRepository;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 保存到mongodb通道
 * 
 * @author ShenJX
 * @date 2017年2月6日
 * @desc
 */
public class MongodbPipeline<T extends BaseRepository<E>, E extends BaseCatch> implements Pipeline {
    private T baseRepository;

    public MongodbPipeline(T baseRepository) {
        this.baseRepository = baseRepository;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void process(ResultItems resultItems, Task task) {
        Object model = resultItems.getAll().get("model");
        if (model instanceof List) {
            ((List) model).forEach(object -> {
                handleModel(object);
            });
        } else {
            handleModel(model);
        }
    }

    public void handleModel(Object object) {

        if (object instanceof BaseCatch) {
            E e = (E) object;
            e.setCreateTime(new Date());
            String sourceUrl = e.getSourceUrl();
            if (!Strings.isNullOrEmpty(sourceUrl) && this.baseRepository.findBySourceUrl(sourceUrl) == null) {
                this.baseRepository.save(e);
            }
        }
        /*
         * if (object instanceof Activity) { Activity activity = (Activity) object; if (!Strings.isNullOrEmpty(activity.getSourceUrl()) && this.activityRepository.findBySourceUrl(activity.getSourceUrl()) == null) { this.activityRepository.save(activity); } } else if (object instanceof News) { News news = (News) object; if (!Strings.isNullOrEmpty(news.getSourceUrl()) && this.newsRepository.findBySourceUrl(news.getSourceUrl()) == null) { this.newsRepository.save(news); } }
         */
    }
}
