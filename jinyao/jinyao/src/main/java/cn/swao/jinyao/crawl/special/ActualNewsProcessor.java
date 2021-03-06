package cn.swao.jinyao.crawl.special;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.openxml4j.opc.internal.unmarshallers.PackagePropertiesUnmarshaller;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.beust.jcommander.Strings;

import cn.swao.jinyao.model.News;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:实时新闻抓爬
 */

/**
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:实时新闻抓取
 * 
 */
@Service
public class ActualNewsProcessor implements PageProcessor {

    // Json数据入口
    public static final String START_URL = "http://www.eastday.com/eastday/shouye/node670813/sst/index_T901.html";

    // 内容详情界面url
    public static final String FORMAT_CONTENT_URL = "http://share.eastday.com/newsapi/api/news/Detailmain/%s";

    // 返回码
    public static final int CODE_SUCCESS = 1;

    public static final int CODE_FAIL = 0;

    // 图片地址前缀
    public static final String IMG_PREFIX = "http://sh.eastday.com";

    @Override
    public Site getSite() {
        return Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setSleepTime(0).setRetryTimes(3);
    }

    @Override
    public void process(Page page) {
        if (page.getUrl().get().equals(START_URL)) {
            try {
                // 解析json
                JSONObject jsonObject = new JSONObject(page.getJson().get().replace("\n", ""));
                JSONArray jsonArray = jsonObject.getJSONArray("newslist");
                List<News> list = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject newsJsonObjcet = jsonArray.getJSONObject(i);
                    String newstitle = newsJsonObjcet.getString("newstitle");
                    String newsimg = newsJsonObjcet.getString("newsimg");
                    String newszy = newsJsonObjcet.getString("newszy");
                    String newslink = newsJsonObjcet.getString("newslink");
                    String newstime = newsJsonObjcet.getString("newstime");
                    // 内容界面不为空
                    if (!Strings.isStringEmpty(newslink)) {
                        Request request = new Request(getContentUrl(newslink));
                        request.putExtra("newstitle", newstitle);
                        request.putExtra("newsimg", newsimg);
                        request.putExtra("newszy", newszy);
                        request.putExtra("newslink", newslink);
                        request.putExtra("newstime", newstime);
                        page.addTargetRequest(request);
                    } else {
                        // 新闻链接为空
                        // 直接保存当前的属性
                        News news = new News();
                        news.setNewsTime(newstime);
                        news.setNewsType(News.TYPE_NEWS_ACTUAL);
                        List<String> listImage = new ArrayList<>();
                        listImage.add(newsimg);
                        news.setCoverImage(listImage);
                        news.setTitle(newstitle);
                        news.setSummary(newszy);
                        list.add(news);
                    }
                }
                page.putField("modle", list);
            } catch (Exception e) {
               e.printStackTrace();
            }
        } else {
            // 获取额外数据
            Request request = page.getRequest();
            String newstitle = request.getExtra("newstitle").toString();
            String newsimg = request.getExtra("newsimg").toString();
            String newszy = request.getExtra("newszy").toString();
            String newslink = request.getExtra("newslink").toString();
            String newstime = request.getExtra("newstime").toString();

            JSONObject jsonObject = new JSONObject(page.getJson().get());
            int code = Integer.parseInt(jsonObject.getString("Code"));
            if (code == CODE_SUCCESS) {
                JSONObject dataObject = jsonObject.getJSONObject("Data");
                String content = dataObject.getString("content");
                content = addImgPrefix(IMG_PREFIX, content);
                String name = dataObject.getString("name");
                String date = dataObject.getString("date");

                News news = new News();
                news.setNewsTime(date);
                news.setNewsType(News.TYPE_NEWS_ACTUAL);
                List<String> listImage = new ArrayList<>();
                listImage.add(newsimg);
                news.setCoverImage(listImage);
                news.setTitle(name);
                news.setSummary(newszy);
                news.setSourceUrl(newslink);
                news.setCleanedContent(content);
                news.setOriginalContent(content);
                page.putField("model", news);
            } else {
                // 无详情界面
                News news = new News();
                news.setNewsTime(newstime);
                news.setNewsType(News.TYPE_NEWS_ACTUAL);
                List<String> listImage = new ArrayList<>();
                listImage.add(newsimg);
                news.setCoverImage(listImage);
                news.setTitle(newstitle);
                news.setSummary(newszy);
                page.putField("model", news);
            }

        }
    }

    private String getContentUrl(String newsLink) {
        int start = newsLink.lastIndexOf("/") + 1;
        int end = newsLink.lastIndexOf(".");
        String code = newsLink.substring(start, end);
        return String.format(FORMAT_CONTENT_URL, code);
    }

    /**
     * 给正文的所有img标签的链接添加前缀
     * 
     * @param content 正文
     * @return
     */
    public String addImgPrefix(String prefix, String content) {
        String result = content;
        Pattern pattern = Pattern.compile("<img src=\"(\\S+)\"");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            String img = matcher.group(1);
            if (!img.contains("http://")) {
                result = result.replace(img, prefix + img);
            }
        }
        return result;
    }
}
