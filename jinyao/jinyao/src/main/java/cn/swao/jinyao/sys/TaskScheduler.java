package cn.swao.jinyao.sys;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

@Component
@Configurable
@EnableScheduling
public class TaskScheduler {
    private static Logger log = LoggerFactory.getLogger(TaskScheduler.class);
}
