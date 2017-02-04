package cn.swao.jinyao.crawl.special;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.impl.client.CloseableHttpClient;
import org.assertj.core.util.Strings;
import org.junit.Test;

import cn.swao.baselib.util.ArrayUtils;
import cn.swao.framework.util.WebUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author ShenJX
 * @date 2017年1月18日
 * @desc 社区活动抓去
 */
public class CommunityActivityProcessor implements PageProcessor {

    private ICommunityActivitySave saveCallBack = null;

    // 启动url
    public static final String START_URL = "http://www.wenhuayun.cn/frontIndex/activityQueryList.do";
    // json
    private static final String FORMAT_JSON_URL = "http://www.wenhuayun.cn/frontIndex/activityQueryList.do?page=%s";

    private static final String JSON_REGX = "http://www\\.wenhuayun\\.cn/frontIndex/activityQueryList";

    // 图片URL format
    private static final String FORMAT_IMG_URL = "http://img1.wenhuayun.cn/%s";

    // 页数
    private static int pageCount = 0;

    // 内容URL format
    private static final String FORMAT_CONTENT_URL = "http://www.wenhuayun.cn/frontActivity/frontActivityDetail.do?activityId=%s";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void process(Page page) {
        if (page.getUrl().regex(JSON_REGX).match()) {
            try {
                Map map = WebUtils.getJsonParams(page.getJson().get());
                List<Map<Object, Object>> list = (List<Map<Object, Object>>) map.get("list");
                // 数据不为空
                if (!list.isEmpty()) {
                    synchronized (this) {
                        pageCount++;
                    }
                    // 添加下一页数据
                    page.addTargetRequest(getRealUrl(FORMAT_JSON_URL, pageCount));

                    list.forEach(info -> {
                        String activityAddress = ArrayUtils.getMapString(info, "activityAddress");
                        String activityArea = ArrayUtils.getMapString(info, "activityArea");
                        String activityStartTime = ArrayUtils.getMapString(info, "activityStartTime");
                        String activityName = ArrayUtils.getMapString(info, "activityName");
                        String activitySite = ArrayUtils.getMapString(info, "activitySite");
                        String activityIconUrl = getRealUrl(FORMAT_IMG_URL, ArrayUtils.getMapString(info, "activityIconUrl"));
                        String activityId = ArrayUtils.getMapString(info, "activityId");
                        String activityEndTime = ArrayUtils.getMapString(info, "activityEndTime");
                        // 添加正文请求链接
                        Request request = new Request(getRealUrl(FORMAT_CONTENT_URL, activityId));
                        request.putExtra("activityAddress", activityAddress);
                        request.putExtra("activityArea", activityArea);
                        request.putExtra("activityIconUrl", activityIconUrl);
                        request.putExtra("activityId", activityId);
                        request.putExtra("activityName", activityName);
                        request.putExtra("activitySite", activitySite);
                        request.putExtra("activityStartTime", activityStartTime);
                        request.putExtra("activityEndTime", activityEndTime);
                        page.addTargetRequest(request);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 正文
            Request request = page.getRequest();
            Object activityAddress = request.getExtra("activityAddress");
            Object activityArea = request.getExtra("activityArea");
            Object activityIconUrl = request.getExtra("activityIconUrl");
            Object activityId = request.getExtra("activityId");
            Object activityName = request.getExtra("activityName");
            Object activityStartTime = request.getExtra("activityStartTime");
            Object activityEndTime = request.getExtra("activityEndTime");
            Object activitySite = request.getExtra("activitySite");
            String activityUrl = page.getUrl().get();

            // 解析内容页面
            String phone = page.getHtml().xpath("//*[@id='allInfo']//p[@class='phone']/span/text()").get();
            String content = page.getHtml().xpath("//*[@id='allInfo']//div[@class='ad_intro']/html()").get();
            Hashtable<String, Object> table = new Hashtable<>();
            table.put("activityAddress", activityAddress);
            table.put("activityName", activityName);
            table.put("activityArea", activityArea);
            table.put("activityIconUrl", activityIconUrl);
            table.put("activityId", activityId);
            table.put("activityStartTime", activityStartTime);
            // 可能为空
            if (activityEndTime != null) {
                table.put("activityEndTime", activityEndTime);
            }
            if (activitySite != null) {
                table.put("activitySite", activitySite);
            }
            table.put("activityUrl", activityUrl);
            table.put("phone", phone);
            table.put("content", content);
            this.saveCallBack.communityActivitysvae(table);
        }
    }

    @Override
    public Site getSite() {
        return Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setDomain("www.wenhuayun.cn").addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").setSleepTime(1000).setRetryTimes(3);
    }

    private String getRealUrl(String format, Object arg) {
        return String.format(format, arg);
    }

    public interface ICommunityActivitySave {
        void communityActivitysvae(Hashtable<String, Object> table);
    }

    public void setSaveCallBack(ICommunityActivitySave saveCallBack) {
        this.saveCallBack = saveCallBack;
    }

}
