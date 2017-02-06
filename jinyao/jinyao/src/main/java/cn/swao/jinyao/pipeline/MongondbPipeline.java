package cn.swao.jinyao.pipeline;

import java.util.List;

import cn.swao.jinyao.model.Activity;
import cn.swao.jinyao.model.News;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 保存到mongodb通道
 * 
 * @author ShenJX
 * @date 2017年2月6日
 * @desc
 */
public class MongondbPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Object> list = resultItems.get("list");
        Object model = resultItems.get("model");
        if (list != null) {
            list.forEach(object -> {
                handleModel(object);
            });
        } else {
            handleModel(model);
        }
    }
    
    public void handleModel(Object object){
        if (object instanceof Activity) {
            // TODO :
        } else if (object instanceof News) {
            // TODO :
        }
    }
}
