package cn.swao.jinyao.sys;

import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import cn.swao.framework.def.StatusEnum;
import cn.swao.jinyao.crawl.StartCatchService;
import cn.swao.jinyao.model.*;
import cn.swao.jinyao.repository.*;
import cn.swao.jinyao.util.DataUtils;

@Component
public class TaskScheduler {
    private static Logger log = LoggerFactory.getLogger(TaskScheduler.class);

    @Autowired
    private StartCatchService startCatchService;

    @Autowired
    private QuartzRepository quartzRepository;
    @Autowired
    public QuartzLogRepository quartzLogRepository;

    //@PostConstruct
    public void doScheduleAuto() {
        List<Quartz> quartzList = quartzRepository.findAll();
        for (Quartz quartz : quartzList) {
            int status = quartz.getStatus();
            if (StatusEnum.VALID.getValue() == status) {
                addSchedule(quartz);
            }
        }
    }

    public void doScheduleManual(String method) {
        log.info("定时调度爬取数据start,method={}", method);
        QuartzLog quartzLog = new QuartzLog(new Date(), method, 1, 1, "sucess");
        try {
            StartCatchService.class.getMethod(method).invoke(startCatchService);
        } catch (Exception e) {
            log.info("定时调度爬取数据method={}", method);
            quartzLog.setResult(2);
            quartzLog.setMessage("fail");
        }
        this.quartzLogRepository.save(quartzLog);
        log.info("定时调度爬取数据sucess,method={}", method);
    }

    public void addSchedule(Quartz quartz) {

        String method = quartz.getMethod();// 要执行的方法
        String startTime = quartz.getStartTime();// 执行的开始时间
        String period = quartz.getPeriod();// 执行的间隔
        String explain = quartz.getExplain();// 说明
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                QuartzLog quartzLog = new QuartzLog(new Date(), method, 1, 0, "sucess");
                try {
                    StartCatchService.class.getMethod(method).invoke(startCatchService);
                } catch (Exception e) {
                    quartzLog.setResult(2);
                    quartzLog.setMessage("fail");
                    log.info("开启定时调度爬虫失败，method={},startTime={},period={},explain={}", method, startTime, period, explain);
                }
                log.info("定时调度爬取数据sucess：explain={}", explain);
                quartzLogRepository.save(quartzLog);
            }

        }, DataUtils.delayed(startTime), Long.valueOf(period));// 这里设定将延时每天固定执行
        log.info("加载定时调度爬取数据：method={},startTime={},period={},explain={}，sucess", method, startTime, period, explain);

    }

}
