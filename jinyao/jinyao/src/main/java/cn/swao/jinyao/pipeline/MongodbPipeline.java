package cn.swao.jinyao.pipeline;

import java.util.*;

import org.assertj.core.util.Strings;

import cn.swao.jinyao.model.Activity;
import cn.swao.jinyao.model.BaseCatch;
import cn.swao.jinyao.model.News;
import cn.swao.jinyao.repository.BaseRepository;
import cn.swao.jinyao.repository.NewsRepository;
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

            if (object instanceof News) {
                News news = (News) object;
                if (news.getNewsType().equals(News.TYPE_NEWS_ACTUAL)) {
                    String title = news.getTitle();
                    if (!Strings.isNullOrEmpty(title)) {
                        if (baseRepository instanceof NewsRepository) {
                            NewsRepository newsRepository = (NewsRepository) baseRepository;
                            if (newsRepository.findByTitle(title) == null) {
                                newsRepository.save(news);
                            }
                        }
                    }
                }
            } else {
                String sourceUrl = e.getSourceUrl();
                if (!Strings.isNullOrEmpty(sourceUrl) && this.baseRepository.findBySourceUrl(sourceUrl) == null) {
                    this.baseRepository.save(e);
                }
            }
        }
    }
}
