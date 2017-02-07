package cn.swao.jinyao.crawl.special;

import java.util.*;

import org.springframework.stereotype.Service;

import cn.swao.baselib.util.JSONUtils;
import cn.swao.framework.util.WebUtils;
import cn.swao.jinyao.model.News;
import cn.swao.jinyao.util.FileUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;

@Service
public class QuxianProcessor implements PageProcessor {

    public String url = "http://city.eastday.com/eastday/n1002826/n1004932/n1004933/n1007783/index_t1282.html";
    public String detailUrl = "http://share.eastday.com/newsapi/api/news/Detailmain/%s";

    int index = 0;

    @Override
    public Site getSite() {
        return Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setSleepTime(0).setRetryTimes(3);
    }

    @Override
    public void process(Page page) {

        if (page.getUrl().regex(url).match() || page.getUrl().get().contains("http://city.eastday.com/eastday/n1002826/n1004932/n1004933/n1007783/index_t1282")) {
            index++;
            StringBuffer sb = new StringBuffer(url);
            sb.insert(url.lastIndexOf(".html"), "p" + index);
            String jsonUrl = sb.toString();

            String str = page.getJson().get();
            Map<String, Object> map = null;
            try {
                map = WebUtils.getJsonParams(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            List<Map<String, String>> se = (List<Map<String, String>>) map.get("newslist");
            for (Map<String, String> param : se) {
                String title = param.get("newstitle");
                String newsurl = param.get("newsurl");
                String pushTime = param.get("createtime");
                String img = param.get("imgurl1");
                System.out.println(newsurl);
                try {
                    newsurl = String.format(detailUrl, newsurl.substring(newsurl.lastIndexOf("/") + 1, newsurl.lastIndexOf("_")));
                    Request request = new Request(newsurl);
                    request.putExtra("title", title);
                    request.putExtra("newsurl", newsurl);
                    request.putExtra("pushTime", pushTime);
                    request.putExtra("img", img);
                    request.putExtra("jsonUrl", jsonUrl);
                    page.addTargetRequest(request);
                } catch (Exception e) {
                }
            }
            page.addTargetRequest(jsonUrl);
            page.setSkip(true);
        } else {
            String str = page.getJson().get();
            Map<String, Object> jsonParams = null;
            try {
                jsonParams = WebUtils.getJsonParams(str);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (jsonParams != null && jsonParams.get("Success").toString().equals("true")) {
                Request request = page.getRequest();
                String title = request.getExtra("title").toString();
                String newsurl = request.getExtra("newsurl").toString();
                String pushTime = request.getExtra("pushTime").toString();
                String img = request.getExtra("img").toString();
                Map<String, String> obj = (Map<String, String>) jsonParams.get("Data");
                String content = obj.get("content");
                ArrayList<String> list = new ArrayList<String>();
                list.add(img);
                News news = new News(title, list, null, content, newsurl, null, "区县新闻", "东方网", pushTime, new Date());
                page.putField("model", news);
            }
        }

    }

}
