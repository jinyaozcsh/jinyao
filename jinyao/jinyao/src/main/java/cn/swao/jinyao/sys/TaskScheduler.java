package cn.swao.jinyao.sys;

import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.*;

import cn.swao.baselib.util.JSONUtils;
import cn.swao.jinyao.crawl.StartCatchService;
import cn.swao.jinyao.util.DataUtils;

@Component
public class TaskScheduler {
    private static Logger log = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private StartCatchService startCatchService;
    @Value("${swao.scheduler.timerParam:}")
    public String timerParam;

    @PostConstruct
    public void run() {
        Iterator<JsonElement> it = JSONUtils.parseArray(timerParam).iterator();
        while (it.hasNext()) {
            JsonArray jsonArray = it.next().getAsJsonArray();
            String method = jsonArray.get(0).getAsString();// 要执行的方法
            String startTime = jsonArray.get(1).getAsString();// 执行的开始时间
            String period = jsonArray.get(2).getAsString();// 执行的间隔
            String explain = jsonArray.get(3).getAsString();// 说明
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                public void run() {
                    try {
                        StartCatchService.class.getMethod(method).invoke(startCatchService);
                    } catch (Exception e) {
                        log.info("开启定时调度爬虫失败，method={},startTime={},period={},explain={}", method, startTime, period, explain);
                    }
                    log.info("定时调度爬取数据sucess：explain={}", explain);
                }
            }, DataUtils.delayed(startTime), Long.valueOf(period));// 这里设定将延时每天固定执行
            log.info("加载定时调度爬取数据：method={},startTime={},period={},explain={}，sucess", method, startTime, period, explain);
        }
    }

    @Scheduled(cron = "0 59 * * * ?")
    public void catchSoundNews() {
        startCatchService.startSoundNews();
    }

    @Scheduled(cron = "0 59 * * * ?")
    public void catchActualNews() throws Exception {
        startCatchService.startActualNews();
    }

    @Scheduled(cron = "0 0 2 * * ?") // 每天2am
    public void catchCommunityActivity() throws Exception {
        startCatchService.startCommunityActivity();
    }
}
