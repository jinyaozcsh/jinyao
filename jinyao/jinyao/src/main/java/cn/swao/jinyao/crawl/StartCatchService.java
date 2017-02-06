package cn.swao.jinyao.crawl;

import java.util.Hashtable;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.swao.baselib.util.JSONUtils;
import cn.swao.jinyao.crawl.special.ActualNewsProcessor;
import cn.swao.jinyao.crawl.special.CommunityActivityProcessor;
import cn.swao.jinyao.crawl.special.SoundNewsProcessor;
import cn.swao.jinyao.pipeline.MongondbPipeline;
import cn.swao.jinyao.util.FileUtils;
import us.codecraft.webmagic.Spider;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:爬虫启动类
 */
@Service
public class StartCatchService implements MongondbPipeline {

    @Autowired
    private ActualNewsProcessor actualNewsProcessor;

    @Autowired
    private SoundNewsProcessor soundNewsProcessor;

    @Autowired
    private CommunityActivityProcessor communityActivityProcessor;

    @PostConstruct
    public void setPipeline() {
        actualNewsProcessor.setMongondbPipeline(this);
        communityActivityProcessor.setMongondbPipeline(this);
        communityActivityProcessor.setMongondbPipeline(this);
    }

    /**
     * 启动实时新闻抓爬
     */
    public void startActualNews() {
        Spider spider = Spider.create(actualNewsProcessor);
        spider.addUrl(ActualNewsProcessor.START_URL);
        spider.run();
    }

    /**
     * 启动语音新闻抓爬
     */
    public void startSoundNews() {
        Spider spider = Spider.create(soundNewsProcessor);
        spider.addUrl(SoundNewsProcessor.START_URL);
        spider.run();
    }

    /**
     * 启动社区新闻抓爬
     */
    public void startCommunityActivity() {
        communityActivityProcessor.setDay(1);
        Spider spider = Spider.create(communityActivityProcessor);
        spider.addUrl(CommunityActivityProcessor.START_URL);
        spider.thread(10).run();
    }

    @Override
    public synchronized void save(String className, Hashtable<Object, Object> table) {
        switch (className) {
        case "CommunityActivityProcessor":
            FileUtils.putFile("D:CommunityActivityProcessor.txt", JSONUtils.toJson(table));
            break;
        case "SoundNewsProcessor":
            FileUtils.putFile("D:SoundNewsProcessor.txt", JSONUtils.toJson(table));
            break;
        case "ActualNewsProcessor":
            FileUtils.putFile("D:ActualNewsProcessor.txt", JSONUtils.toJson(table));
            break;
        default:
            break;
        }
    }
}
