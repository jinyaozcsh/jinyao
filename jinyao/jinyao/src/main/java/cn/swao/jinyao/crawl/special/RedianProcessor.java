package cn.swao.jinyao.crawl.special;

import java.util.*;

import org.springframework.stereotype.Service;

import cn.swao.baselib.util.*;
import cn.swao.framework.util.WebUtils;
import cn.swao.jinyao.model.News;
import cn.swao.jinyao.util.FileUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;

@Service
public class RedianProcessor implements PageProcessor {

    String detailUrl = "https://newswifiapi.dftoutiao.com/jsonnew/newsinfo?rowkey=%s&url=%s";

    public String url = "https://newswifiapi.dftoutiao.com/jsonnew/refreshjp?type=shanghai&qid=eastnb";
    String nexturl = "https://newswifiapi.dftoutiao.com/jsonnew/nextjp?type=shanghai&qid=eastnb&startkey=";

    @Override
    public Site getSite() {
        Site site = Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").setSleepTime(0).setRetryTimes(3);
        return site;
    }

    @Override
    public void process(Page page) {
        if (page.getUrl().get().equals(url) || page.getUrl().get().contains(nexturl)) {

            String str = page.getJson().removePadding("null").get();
            Map<String, Object> map = null;
            try {
                map = WebUtils.getJsonParams(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Map<String, Object>> se = (List<Map<String, Object>>) map.get("data");
            String rowkey = null;
            for (Map<String, Object> param : se) {
                String title = param.get("topic").toString();
                rowkey = param.get("rowkey").toString();
                String pushTime = param.get("date").toString();
                String source = param.get("source").toString();
                String url = param.get("url").toString();
                List<Map<String, String>> miniimg = (List<Map<String, String>>) param.get("miniimg");
                List<String> img = new ArrayList<String>();
                for (Map<String, String> mapimg : miniimg) {
                    img.add(mapimg.get("src"));
                }
                String sourceUrl = String.format(detailUrl, rowkey, url);
                Request request = new Request(sourceUrl);
                request.putExtra("title", title);
                request.putExtra("sourceUrl", sourceUrl);
                request.putExtra("pushTime", pushTime);
                request.putExtra("img", img);
                request.putExtra("source", source);
                page.addTargetRequest(request);
            }
            if (rowkey != null) {
                page.addTargetRequest(nexturl + rowkey);
            }
            page.setSkip(true);
        } else {
            String str = page.getJson().removePadding("null").get();
            Map<String, Object> jsonParams = null;
            try {
                jsonParams = WebUtils.getJsonParams(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonParams != null && jsonParams.get("stat").toString().equals("1")) {
                Request request = page.getRequest();
                String title = request.getExtra("title").toString();
                String newsurl = request.getExtra("sourceUrl").toString();
                String pushTime = request.getExtra("pushTime").toString();
                String source = request.getExtra("source").toString();
                List img = (List) request.getExtra("img");
                Map<String, String> obj = (Map<String, String>) jsonParams.get("data");
                String content = obj.get("content");
                content = HtmlUtils.simplifyContent(content);
                News news = new News(title, img, content, content, newsurl, null, "hot", source, pushTime);
                page.putField("model", news);
            }
        }

    }

}
