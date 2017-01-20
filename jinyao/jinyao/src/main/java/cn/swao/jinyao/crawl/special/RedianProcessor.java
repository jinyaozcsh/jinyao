package cn.swao.jinyao.crawl.special;

import java.util.*;

import cn.swao.baselib.util.JSONUtils;
import cn.swao.framework.util.WebUtils;
import cn.swao.jinyao.util.FileUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;

public class RedianProcessor implements PageProcessor {

    String detailUrl = "https://newswifiapi.dftoutiao.com/jsonnew/newsinfo?rowkey=%s&url=%s";

    public static String url = "https://newswifiapi.dftoutiao.com/jsonnew/refreshjp?type=shanghai&qid=eastnb";
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
                String sourceUrl = String.format(detailUrl, rowkey, url);
                Request request = new Request(sourceUrl);
                request.putExtra("title", title);
                request.putExtra("sourceUrl", sourceUrl);
                request.putExtra("pushTime", pushTime);
                request.putExtra("img", miniimg);
                page.addTargetRequest(request);
            }
            if (rowkey != null) {
                page.addTargetRequest(nexturl + rowkey);
            }
        } else {
            String str = page.getJson().removePadding("null").get();
            Map<String, Object> jsonParams = null;
            try {
                jsonParams = WebUtils.getJsonParams(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonParams != null && jsonParams.get("stat").toString().equals("1")) {
                Map<String, Object> map = new HashMap<String, Object>();
                Request request = page.getRequest();
                String title = request.getExtra("title").toString();
                String newsurl = request.getExtra("sourceUrl").toString();
                String pushTime = request.getExtra("pushTime").toString();
                List img = (List) request.getExtra("img");
                Map<String, String> obj = (Map<String, String>) jsonParams.get("data");
                String content = obj.get("content");
                map.put("title", title);
                map.put("sourceUrl", newsurl);
                map.put("pushTime", pushTime);
                map.put("img", img);
                map.put("content", content);
                FileUtils.putFile("D:/redian.txt", JSONUtils.toJson(map));
            }
        }

    }

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "D:\\git\\jinyao\\jinyao\\jinyao\\jssecacerts");
        Spider.create(new RedianProcessor()).addUrl(url).thread(5).run();
    }

}
