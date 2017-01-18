package cn.swao.jinyao.crawl;

import java.util.Hashtable;
import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.swao.jinyao.crawl.special.ActualNewsProcessor;
import cn.swao.jinyao.crawl.special.ActualNewsProcessor.IActualNewsSave;
import cn.swao.jinyao.crawl.special.CommunityActivityProcessor;
import cn.swao.jinyao.crawl.special.CommunityActivityProcessor.ICommunityActivitySave;
import cn.swao.jinyao.crawl.special.SoundNewsProcessor;
import cn.swao.jinyao.crawl.special.SoundNewsProcessor.ISoundNewsSave;
import us.codecraft.webmagic.Spider;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:爬虫启动类
 */
@Service
public class StartCatchService implements IActualNewsSave, ISoundNewsSave ,ICommunityActivitySave{

    @Autowired
    private ActualNewsProcessor actualNewsProcessor;

    @Autowired
    private SoundNewsProcessor soundNewsProcessor;
    
    @Autowired
    private CommunityActivityProcessor communityActivityProcessor; 

    @Test
    public void test() {
        // startActualNews();
//        startSoundNews();
        startCommunityActivity();
    }

    /**
     * 启动实时新闻抓爬
     */
    public void startActualNews() {
        ActualNewsProcessor actualNewsProcessor = new ActualNewsProcessor();
        actualNewsProcessor.setSaveCallBack(this);
        Spider.create(actualNewsProcessor).addUrl(ActualNewsProcessor.START_URL).run();
        // Spider spider = Spider.create(actualNewsProcessor);
        // spider.addUrl(ActualNewsProcessor.START_URL);
        // spider.start();

    }

    /**
     * 启动语音新闻抓爬
     */
    public void startSoundNews() {
        SoundNewsProcessor soundNewsProcessor = new SoundNewsProcessor();
        soundNewsProcessor.setSaveCallBack(this);
        Spider.create(soundNewsProcessor).addUrl(SoundNewsProcessor.START_URL).run();
        // Spider spider = Spider.create(soundNewsProcessor);
        // spider.addUrl(SoundNewsProcessor.START_URL);
        // spider.start();

    }
    
    /**
     * 启动语音新闻抓爬
     */
    public void startCommunityActivity() {
        CommunityActivityProcessor communityActivityProcessor = new CommunityActivityProcessor();
        communityActivityProcessor.setSaveCallBack(this);
        Spider.create(communityActivityProcessor).addUrl(CommunityActivityProcessor.START_URL).run();
        // Spider spider = Spider.create(soundNewsProcessor);
        // spider.addUrl(SoundNewsProcessor.START_URL);
        // spider.start();

    }

    /**
     * 实时新闻保存回调
     */
    @Override
    public void actualNewsSave(Hashtable<String, Object> table) {
        for (Map.Entry<String, Object> entry : table.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            System.out.println(key + " == " + value);
        }

        System.out.println("===============");
        // System.out.println(code);
        // System.out.println(newslink);
        // System.out.println(newstitle);
        // System.out.println(newsimg);
        // System.out.println(newszy);
        // System.out.println(newstime);
        // System.out.println(name);
        // System.out.println(content);
        // System.out.println(date);
        // System.out.println("=================");
    }

    /**
     * 语音新闻保存回调
     */
    @Override
    public void soundNewsSave(Hashtable<String, Object> table) {
        actualNewsSave(table);
    }

    /**
     * 社区活动新闻保存回调
     */
    @Override
    public void communityActivitysvae(Hashtable<String, Object> table) {
        actualNewsSave(table);
    }
}
