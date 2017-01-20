package cn.swao.jinyao.crawl.special;

import java.util.*;
import java.util.regex.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.eastnb.middletier.model.Region;
import com.eastnb.middletier.service.RegionService;
import com.google.common.base.Strings;

import cn.edu.hfut.dmic.contentextractor.*;
import cn.swao.baselib.util.*;
import cn.swao.jinyao.util.*;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommuntiyNewsProcessor implements PageProcessor {

    static String url = "http://news.baidu.com/ns?word=%s&tn=newsfcu&from=news&cl=1&rn=50&ct=0&qq-pf-to=pcqq.c2c";

    @Autowired
    private RegionService regionService;

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
        List<Region> regionByLevel = regionService.getRegionByLevel(4);
        for (Region region : regionByLevel) {
            String name = region.getName();
            String format = String.format(url, name);
            Spider.create(new CommuntiyNewsProcessor()).addUrl(format).thread(5).run();
        }
    }

    public static void main(String[] args) {

        String format = String.format(url, "定海路街道");
        Spider.create(new CommuntiyNewsProcessor()).addUrl(format).thread(5).run();
    }

}
