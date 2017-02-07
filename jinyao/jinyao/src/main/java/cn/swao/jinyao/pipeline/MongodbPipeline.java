package cn.swao.jinyao.pipeline;

import java.util.List;

import org.assertj.core.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.swao.jinyao.model.Activity;
import cn.swao.jinyao.model.News;
import cn.swao.jinyao.repository.*;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.*;

/**
 * 保存到mongodb通道
 * 
 * @author ShenJX
 * @date 2017年2月6日
 * @desc
 */
@Component
public class MongodbPipeline implements Pipeline {

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private NewsRepository newsRepository;

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
        if (object instanceof Activity) {
            Activity activity = (Activity) object;
            if (!Strings.isNullOrEmpty(activity.getSourceUrl()) && this.activityRepository.findBySourceUrl(activity.getSourceUrl()) == null) {
                this.activityRepository.save(activity);
            }
        } else if (object instanceof News) {
            News news = (News) object;
            if (!Strings.isNullOrEmpty(news.getSourceUrl()) && this.newsRepository.findBySourceUrl(news.getSourceUrl()) == null) {
                this.newsRepository.save(news);
            }
        }
    }
}
