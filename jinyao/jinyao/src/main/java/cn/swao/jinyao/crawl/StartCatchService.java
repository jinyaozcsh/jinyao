package cn.swao.jinyao.crawl;

import java.io.File;
import java.util.*;

import javax.annotation.PostConstruct;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;

import cn.swao.baselib.util.*;
import cn.swao.jinyao.crawl.special.*;
import cn.swao.jinyao.model.News;
import cn.swao.jinyao.pipeline.MongodbPipeline;
import cn.swao.jinyao.repository.*;
import cn.swao.jinyao.util.FileUtils;
import us.codecraft.webmagic.Spider;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:爬虫启动类
 */
@Service
public class StartCatchService {

    private static Logger log = LoggerFactory.getLogger(StartCatchService.class);

    @Autowired
    private ActualNewsProcessor actualNewsProcessor;
    @Autowired
    private SoundNewsProcessor soundNewsProcessor;
    @Autowired
    private CommunityActivityProcessor communityActivityProcessor;
    @Autowired
    private CommuntiyNewsProcessor communtiyNewsProcessor;
    @Autowired
    private QuxianProcessor quxianProcessor;
    @Autowired
    private RedianProcessor redianProcessor;
    @Autowired
    private NewsRepository newsRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Value("${swao.storage.path:}")
    public String path;

    /**
     * 启动实时新闻抓爬
     */
    public void startActualNews() {
        Spider spider = Spider.create(actualNewsProcessor);
        spider.addUrl(ActualNewsProcessor.START_URL);
        spider.addPipeline(new MongodbPipeline());
        spider.run();
    }

    /**
     * 启动语音新闻抓爬
     */
    public void startSoundNews() {
        Spider spider = Spider.create(soundNewsProcessor);
        spider.addUrl(SoundNewsProcessor.START_URL);
        spider.addPipeline(new MongodbPipeline());
        spider.run();
    }

    /**
     * 启动社区新闻抓爬
     */
    public void startCommunityActivity() {
        communityActivityProcessor.setDay(1);
        Spider spider = Spider.create(communityActivityProcessor);
        spider.addUrl(CommunityActivityProcessor.START_URL);
        spider.addPipeline(new MongodbPipeline());
        spider.thread(10).run();
    }

    public void catchcommuntiyNews() {
        String[] regions = { "定海路街道", "大桥街道", "平凉路街道", "江浦路街道", "控江路街道", "延吉新村街道", "长白新村街道", "四平路街道", "殷行街道", "五角场街道", "五角场镇", "新江湾城街道", "长寿路街道", "曹杨新村街道", "长风新村街道", "宜川路街道", "甘泉路街道", "石泉路街道", "真如镇街道", "万里街道", "长征镇", "桃浦镇", "外滩街道", "南京东路街道", "半淞园路街道", "小东门街道", "老西门街道", "豫园街道", "打浦桥街道", "淮海中路街道", "瑞金二路街道", "五里桥街道", "湖南路街道", "天平路街道", "枫林路街道", "徐家汇街道", "斜土路街道", "长桥街道", "漕河泾街道", "康健新村街道", "虹梅路街道", "田林街道", "凌云路街道", "龙华街道", "华泾镇", "华阳路街道", "新华路街道", "江苏路街道", "天山路街道", "周家桥街道", "虹桥街道", "仙霞新村街道", "程家桥街道", "北新泾街道", "新泾镇", "嘉定镇街道", "新成路街道", "真新街道", "马陆镇", "南翔镇", "江桥镇", "安亭镇", "外冈镇", "徐行镇", "华亭镇", "江宁路街道", "静安寺街道", "南京西路街道", "曹家渡街道", "石门二路街道", "天目西路街道", "北站街道", "宝山路街道", "芷江西路街道", "共和新路街道", "大宁路街道", "彭浦新村街道", "临汾路街道", "彭浦镇", "四川北路街道", "提篮桥街道", "欧阳路街道", "广中路街道", "凉城新村街道", "嘉兴路街道", "曲阳路街道", "江湾镇街道",
                "江川路街道", "古美街道", "新虹街道", "浦锦街道", "莘庄镇", "七宝镇", "浦江镇", "梅陇镇", "虹桥镇", "马桥镇", "吴泾镇", "华漕镇", "颛桥镇", "吴淞街道", "张庙街道", "友谊路街道", "庙行镇", "罗店镇", "大场镇", "顾村镇", "罗泾镇", "杨行镇", "月浦镇", "淞南镇", "高境镇", "潍坊新村街道", "陆家嘴街道", "塘桥街道", "周家渡街道", "东明路街道", "洋泾街道", "上钢新村街道", "沪东新村街道", "金杨新村街道", "浦兴路街道", "南码头路街道", "花木街道", "川沙新镇", "合庆镇", "曹路镇", "高东镇", "高桥镇", "高行镇", "金桥镇", "张江镇", "唐镇", "北蔡镇", "三林镇", "惠南镇", "新场镇", "大团镇", "周浦镇", "航头镇", "康桥镇", "宣桥镇", "祝桥镇", "泥城镇", "书院镇", "万祥镇", "老港镇", "南汇新城镇", "石化街道", "枫泾镇", "朱泾镇", "亭林镇", "漕泾镇", "山阳镇", "金山卫镇", "张堰镇", "廊下镇", "吕港镇", "岳阳街道", "中山街道", "永丰街道", "方松街道", "九里亭街道", "广富林街道", "九亭镇", "泗泾镇", "泖港镇", "车墩镇", "洞泾镇", "叶榭镇", "新桥镇", "石湖荡镇", "新浜镇", "佘山镇", "小昆山镇", "夏阳街道", "盈浦街道", "香花桥街道", "赵巷镇", "徐泾镇", "华新镇", "重固镇", "白鹤镇", "朱家角镇", "练塘镇", "金泽镇", "西渡街道", "南桥镇", "庄行镇", "金汇镇", "柘林镇",
                "青村镇", "奉城镇", "四团镇", "海湾镇", "城桥镇", "堡镇", "庙镇", "中兴镇", "新河镇", "三星镇", "向化镇", "绿华镇", "建设镇", "陈家镇", "竖新镇", "港西镇", "港沿镇", "新海镇", "东平镇", "长兴镇", "新村乡", "横沙乡", "菊园新区街道" };
        communtiyNewsProcessor.setEndParam(DateUtils.addDate(new Date(), 1));
        for (String region : regions) {
            String format = String.format(communtiyNewsProcessor.url, region);
            // String format = String.format(url, "定海路街道");
            Spider.create(communtiyNewsProcessor).addPipeline(new MongodbPipeline<NewsRepository,News>(newsRepository)).addUrl(format).thread(5).run();
        }
    }

    public void catchQuxian() {
        Spider.create(new QuxianProcessor()).addPipeline(new MongodbPipeline<NewsRepository,News>(newsRepository)).addUrl(quxianProcessor.url).thread(5).run();
    }

    public void catchRedian() throws Exception {
        /*
         * File file = new File(path, "jssecacerts"); if (!file.exists()) { InstallCert.createCert(file, "newswifiapi.dftoutiao.com"); log.info("获取安全证书{}", file.getAbsolutePath()); } System.setProperty("javax.net.ssl.trustStore", file.getAbsolutePath());
         */
        Spider.create(new RedianProcessor()).addPipeline(new MongodbPipeline<NewsRepository,News>(newsRepository)).addUrl(redianProcessor.url).thread(5).run();
    }

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
