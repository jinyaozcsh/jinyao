package cn.swao.jinyao.catsh;

import javax.management.JMException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.swao.jinyao.catsh.special.ActualNewsProcessor;
import cn.swao.jinyao.catsh.special.ActualNewsProcessor.IActualNewsSave;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:爬虫启动类
 */
@Service
public class StartCatchService implements IActualNewsSave {

    // @Autowired
    // private ActualNewsProcessor actualNewsProcessor;
    
    @Test
    public void test() {
        startActualNews();
    }
    
    /**
     * 启动实时新闻抓爬
     */
    public void startActualNews() {
        ActualNewsProcessor actualNewsProcessor = new ActualNewsProcessor();
        actualNewsProcessor.setIActualNewsSave(this);
//        Spider spider = Spider.create(actualNewsProcessor);
//        spider.addUrl(ActualNewsProcessor.START_URL);
//        spider.start();
        
        Spider.create(actualNewsProcessor).addUrl(ActualNewsProcessor.START_URL).thread(5).run();
        System.out.println("start==========");
    }

    @Override
    public void actualNewsSave(String newstitle, String newsimg, String newszy, String newstime, String newslink, String name, String content, String date, String code) {
        System.out.println(code);
        System.out.println(newslink);
        System.out.println(newstitle);
        System.out.println(newsimg);
        System.out.println(newszy);
        System.out.println(newstime);
        System.out.println(name);
        System.out.println(content);
        System.out.println(date);
        System.out.println("=================");
    }
}
