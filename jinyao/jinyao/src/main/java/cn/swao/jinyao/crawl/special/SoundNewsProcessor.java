package cn.swao.jinyao.crawl.special;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import org.jboss.netty.channel.SucceededChannelFuture;
import org.springframework.stereotype.Service;

import cn.swao.framework.api.CustomBizException;
import cn.swao.framework.util.WebUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author ShenJX
 * @date 2017年1月17日
 * @desc 语音新闻抓取
 */
@Service
public class SoundNewsProcessor implements PageProcessor {

    // 保存回调接口
    public ISoundNewsSave saveCallBack = null;

    // Json数据入口
    public static final String START_URL = "http://listen.eastday.com/node2/node3/n1416/index1416_t81.html";

    // 匹配json数据的正则
    public static final String JSON_REGX = "http://listen\\.eastday\\.com/node2/node3/n1416/index1416_t81";
    // json url
    public static final String FORMAT_JSON_URL = "http://listen.eastday.com/node2/node3/n1416/index1416_t81p%s.html";

    // 内容详情界面url
    public static final String FORMAT_CONTENT_URL = "http://share.eastday.com/newsapi/api/news/detail/%s";

    // 返回码
    public static final int CODE_SUCCESS = 1;

    public static final int CODE_FAIL = 0;;

    // 页数
    public static int pageCount = 0;

    // json数据的地址格式
    public static final String FORMAT_URL = "http://listen.eastday.com%s";

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void process(Page page) {
        if (page.getUrl().regex(JSON_REGX).match()) {
            synchronized (page) {
                pageCount++;
            }
            // 添加下一页json数据
            page.addTargetRequest(getRealUrl(FORMAT_JSON_URL, pageCount));
            String jsonString = page.getJson().get();
            try {
                Map map = WebUtils.getJsonParams(jsonString);
                List<Map<String, String>> list = (List<Map<String, String>>) map.get("newslist");
                list.forEach(news -> {
                    String newsid = news.get("newsid");
                    String newstitle = news.get("newstitle");
                    String newsurl = getRealUrl(FORMAT_URL, news.get("newstitle"));
                    String imgurl = getRealUrl(FORMAT_URL, news.get("imgurl1"));
                    String source = news.get("source");

                    Request request = new Request(getRealUrl(FORMAT_CONTENT_URL, newsid));
                    request.putExtra("newsid", newsid);
                    request.putExtra("newstitle", newstitle);
                    request.putExtra("newsurl", newsurl);
                    request.putExtra("imgurl", imgurl);
                    request.putExtra("source", source);
                    page.addTargetRequest(request);
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 内容界面
            Request request = page.getRequest();
            String newsid = request.getExtra("newsid").toString();
            String newstitle = request.getExtra("newstitle").toString();
            String newsurl = request.getExtra("newsurl").toString();
            String imgurl = request.getExtra("imgurl").toString();
            String source = request.getExtra("source").toString();
            String jsonString = page.getJson().get();
            try {
                Map map = WebUtils.getJsonParams(jsonString);
                int code = Integer.parseInt(map.get("Code").toString());
                if (code == CODE_SUCCESS) {
                    Map<String, String> data = (Map<String, String>) map.get("Data");
                    String name = data.get("name");
                    String audio = data.get("audio");
                    String content = data.get("content");
                    Hashtable<String, Object> table = new Hashtable<>();
                    table.put("newsid", newsid);
                    table.put("newstitle", newstitle);
                    table.put("newsurl", newsurl);
                    table.put("imgurl", imgurl);
                    table.put("source", source);
                    table.put("Code", code);
                    table.put("name", name);
                    table.put("audio", audio);
                    table.put("content", content);
                    saveCallBack.soundNewsSave(table);
                } else {
                    new CustomBizException("获取数据错误");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setSleepTime(0).setRetryTimes(3);
    }

    private String getRealUrl(String format, Object arg) {
        return String.format(format, arg);
    }

    public interface ISoundNewsSave {
        void soundNewsSave(Hashtable<String, Object> table);
    }

    public void setSaveCallBack(ISoundNewsSave saveCallBack) {
        this.saveCallBack = saveCallBack;
    }
}
