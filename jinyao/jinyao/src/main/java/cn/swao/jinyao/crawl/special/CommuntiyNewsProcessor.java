package cn.swao.jinyao.crawl.special;

import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.slf4j.*;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.swao.baselib.util.*;
import cn.swao.jinyao.model.News;
import cn.swao.jinyao.util.*;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.*;

@Service
public class CommuntiyNewsProcessor implements PageProcessor {

    private static Logger log = LoggerFactory.getLogger(CommuntiyNewsProcessor.class);

    public String url = "http://news.baidu.com/ns?word=%s&tn=newsfcu&from=news&cl=1&rn=50&ct=0&qq-pf-to=pcqq.c2c";

    public Date endParam;

    public void setEndParam(Date endParam) {
        this.endParam = endParam;
    }

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
                    log.info("第" + i + "条" + as.get(i).get());
                    log.info("第" + i + "条" + spans.get(i).get());
                    if (!Strings.isNullOrEmpty(pushTime)) {
                        Date dateTime = DateUtils.toDateTime(pushTime);
                        if (endParam != null && endParam.compareTo(dateTime) < 0) {
                            page.addTargetRequest(request);
                        }
                    }
                    page.setSkip(true);
                } catch (Exception e) {
                    log.info("第" + i + "条" + "条报错", e);
                }
            }

        } else {
            String content = null;
            String originalContent = page.getRawText();
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
                ArrayList<String> list = new ArrayList<String>();
                list.add(getImage(originalContent));
                News news = new News(title, list, originalContent, content, sourceUrl, null, "社区新闻", source, pushTime);
                news.setRegion(name);
                page.putField("model", news);
                log.info("第一：" + news);
            }
        }

    }

    public String getImage(String html) {
        Document doc = Jsoup.parse(html);
        for (Element img : doc.select("img")) {
            String tUrl = img.attr("src");
            if (Strings.isNullOrEmpty(tUrl))
                continue;
            return img.absUrl("src");
        }
        return null;
    }

}
