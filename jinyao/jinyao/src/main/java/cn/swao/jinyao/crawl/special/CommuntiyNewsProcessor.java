package cn.swao.jinyao.crawl.special;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.base.Strings;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.swao.baselib.util.*;
import cn.swao.jinyao.util.*;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommuntiyNewsProcessor implements PageProcessor {

    static String url = "http://news.baidu.com/ns?word=%s&tn=newsfcu&from=news&cl=1&rn=50&ct=0&qq-pf-to=pcqq.c2c";

    @Override
    public Site getSite() {
        return Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setSleepTime(0).setRetryTimes(3).setTimeOut(15000);
    }

    @Override
    public void process(Page page) {

        if (page.getUrl().get().contains("http://news.baidu.com/ns?word=")) {
            String str = page.getJson().removePadding("document.write").get();
            if (!Strings.isNullOrEmpty(str) && str.indexOf("'") != -1) {
                str = str.substring(1, str.length() - 1);
            }
            Document doc = Jsoup.parse(str);

            Html html = new Html(doc);
            List<Selectable> as = html.xpath("[@class=\"baidu\"]").xpath("/div/a").nodes();
            List<Selectable> spans = html.xpath("[@class=\"baidu\"]").xpath("/div/span").nodes();
            for (int i = 0; i < spans.size(); i++) {
                try {
                    String text = spans.get(i).xpath("/span/text()").get();
                    // Pattern p = Pattern.compile("(\\S+)(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2})");

                    String[] split = text.split("\u00A0");
                    String source = split[0];
                    String pushTime = split[1];

                    String url = as.get(i).links().get();
                    String title = as.get(i).xpath("/a/text()").get();
                    Request request = new Request(url);
                    request.putExtra("source", source);
                    request.putExtra("pushTime", pushTime);
                    request.putExtra("url", url);
                    request.putExtra("title", title);
                    String reqUrl = page.getUrl().get();
                    Map<String, String> urlRequest = UrlUtils.URLRequest(reqUrl);
                    request.putExtra("name", urlRequest.get("word"));
                    System.out.println("第" + i + "条" + as.get(i).get());
                    System.out.println("第" + i + "条" + spans.get(i).get());
                    page.addTargetRequest(request);
                } catch (Exception e) {
                    System.out.println("第" + i + "条报错");
                    e.printStackTrace();
                }
            }

        } else {
            String content = null;
            try {
                // content = page.getHtml().smartContent().get();

                content = ContentExtractor.getContentElementByHtml(page.getRawText()).toString();
                content = HtmlUtils.simplifyContent(content);

            } catch (Exception e) {
                e.printStackTrace();
            }
            if (content != null) {
                Request request = page.getRequest();
                String source = request.getExtra("source").toString();
                String pushTime = request.getExtra("pushTime").toString();
                String sourceUrl = request.getExtra("url").toString();
                String title = request.getExtra("title").toString();
                String name = request.getExtra("name").toString();
                Map<String, String> map = new HashMap<String, String>();
                map.put("source", source);
                map.put("pushTime", pushTime);
                map.put("sourceUrl", sourceUrl);
                map.put("title", title);
                map.put("name", name);
                map.put("content", content);
                FileUtils.putFile("D:/communtiyNews.txt", JSONUtils.toJson(map));
                System.out.println(JSONUtils.toJson(map));
            }
        }

    }

    @Test
    public void run() {
        String[] regions = { "定海路街道", "大桥街道", "平凉路街道", "江浦路街道", "控江路街道", "延吉新村街道", "长白新村街道", "四平路街道", "殷行街道", "五角场街道", "五角场镇", "新江湾城街道", "长寿路街道", "曹杨新村街道", "长风新村街道", "宜川路街道", "甘泉路街道", "石泉路街道", "真如镇街道", "万里街道", "长征镇", "桃浦镇", "外滩街道", "南京东路街道", "半淞园路街道", "小东门街道", "老西门街道", "豫园街道", "打浦桥街道", "淮海中路街道", "瑞金二路街道", "五里桥街道", "湖南路街道", "天平路街道", "枫林路街道", "徐家汇街道", "斜土路街道", "长桥街道", "漕河泾街道", "康健新村街道", "虹梅路街道", "田林街道", "凌云路街道", "龙华街道", "华泾镇", "华阳路街道", "新华路街道", "江苏路街道", "天山路街道", "周家桥街道", "虹桥街道", "仙霞新村街道", "程家桥街道", "北新泾街道", "新泾镇", "嘉定镇街道", "新成路街道", "真新街道", "马陆镇", "南翔镇", "江桥镇", "安亭镇", "外冈镇", "徐行镇", "华亭镇", "江宁路街道", "静安寺街道", "南京西路街道", "曹家渡街道", "石门二路街道", "天目西路街道", "北站街道", "宝山路街道", "芷江西路街道", "共和新路街道", "大宁路街道", "彭浦新村街道", "临汾路街道", "彭浦镇", "四川北路街道", "提篮桥街道", "欧阳路街道", "广中路街道", "凉城新村街道", "嘉兴路街道", "曲阳路街道", "江湾镇街道",
                "江川路街道", "古美街道", "新虹街道", "浦锦街道", "莘庄镇", "七宝镇", "浦江镇", "梅陇镇", "虹桥镇", "马桥镇", "吴泾镇", "华漕镇", "颛桥镇", "吴淞街道", "张庙街道", "友谊路街道", "庙行镇", "罗店镇", "大场镇", "顾村镇", "罗泾镇", "杨行镇", "月浦镇", "淞南镇", "高境镇", "潍坊新村街道", "陆家嘴街道", "塘桥街道", "周家渡街道", "东明路街道", "洋泾街道", "上钢新村街道", "沪东新村街道", "金杨新村街道", "浦兴路街道", "南码头路街道", "花木街道", "川沙新镇", "合庆镇", "曹路镇", "高东镇", "高桥镇", "高行镇", "金桥镇", "张江镇", "唐镇", "北蔡镇", "三林镇", "惠南镇", "新场镇", "大团镇", "周浦镇", "航头镇", "康桥镇", "宣桥镇", "祝桥镇", "泥城镇", "书院镇", "万祥镇", "老港镇", "南汇新城镇", "石化街道", "枫泾镇", "朱泾镇", "亭林镇", "漕泾镇", "山阳镇", "金山卫镇", "张堰镇", "廊下镇", "吕港镇", "岳阳街道", "中山街道", "永丰街道", "方松街道", "九里亭街道", "广富林街道", "九亭镇", "泗泾镇", "泖港镇", "车墩镇", "洞泾镇", "叶榭镇", "新桥镇", "石湖荡镇", "新浜镇", "佘山镇", "小昆山镇", "夏阳街道", "盈浦街道", "香花桥街道", "赵巷镇", "徐泾镇", "华新镇", "重固镇", "白鹤镇", "朱家角镇", "练塘镇", "金泽镇", "西渡街道", "南桥镇", "庄行镇", "金汇镇", "柘林镇",
                "青村镇", "奉城镇", "四团镇", "海湾镇", "城桥镇", "堡镇", "庙镇", "中兴镇", "新河镇", "三星镇", "向化镇", "绿华镇", "建设镇", "陈家镇", "竖新镇", "港西镇", "港沿镇", "新海镇", "东平镇", "长兴镇", "新村乡", "横沙乡", "菊园新区街道" };
        for (String region : regions) {
            String format = String.format(url, region);
            Spider.create(new CommuntiyNewsProcessor()).addUrl(format).thread(5).run();
        }
    }

    public static void main(String[] args) {

        String format = String.format(url, "定海路街道");
        Spider.create(new CommuntiyNewsProcessor()).addUrl(format).thread(5).run();
    }

}
