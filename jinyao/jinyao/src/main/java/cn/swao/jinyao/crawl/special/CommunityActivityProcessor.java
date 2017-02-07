package cn.swao.jinyao.crawl.special;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.springframework.stereotype.Service;

import cn.swao.baselib.util.ArrayUtils;
import cn.swao.framework.util.WebUtils;
import cn.swao.jinyao.model.Activity;
import cn.swao.jinyao.util.DataUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author ShenJX
 * @date 2017年1月18日
 * @desc 社区活动抓取
 */
@Service
public class CommunityActivityProcessor implements PageProcessor {

    // 启动url
    public static final String START_URL = "http://www.wenhuayun.cn/frontIndex/activityQueryList.do";
    // json
    private static final String FORMAT_JSON_URL = "http://www.wenhuayun.cn/frontIndex/activityQueryList.do?page=%s";

    private static final String JSON_REGX = "http://www\\.wenhuayun\\.cn/frontIndex/activityQueryList";

    // 图片URL format
    private static final String FORMAT_IMG_URL = "http://img1.wenhuayun.cn/%s";

    // 页数
    private int pageCount = 1;

    // 爬取数据的天数
    private int day = 1;

    // 是否结束
    private boolean isEnd = false;

    // 内容URL format
    private static final String FORMAT_CONTENT_URL = "http://www.wenhuayun.cn/frontActivity/frontActivityDetail.do?activityId=%s";

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void process(Page page) {
        if (!isEnd) {
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
                            String activityIconUrl = getRealUrl(FORMAT_IMG_URL, ArrayUtils.getMapString(info, "activityIconUrl"));
                            String activityId = ArrayUtils.getMapString(info, "activityId");
                            String activityEndTime = ArrayUtils.getMapString(info, "activityEndTime");
                            Long activityUpdateTime = ArrayUtils.getMapLong(info, "activityUpdateTime");
                            // 在抓取天数范围内添加
                            if (DataUtils.isWithinTheDateRange(new Date(), activityUpdateTime, day)) {
                                // 添加正文请求链接
                                Request request = new Request(getRealUrl(FORMAT_CONTENT_URL, activityId));
                                request.putExtra("activityAddress", activityAddress);
                                request.putExtra("activityArea", activityArea);
                                request.putExtra("activityIconUrl", activityIconUrl);
                                request.putExtra("activityId", activityId);
                                request.putExtra("activityName", activityName);
                                request.putExtra("activityStartTime", activityStartTime);
                                request.putExtra("activityEndTime", activityEndTime);
                                page.addTargetRequest(request);
                            } else {
                                page.setSkip(true);
                                isEnd = true;
                            }
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
                // Object activityId = request.getExtra("activityId");
                Object activityName = request.getExtra("activityName");
                Object activityStartTime = request.getExtra("activityStartTime");
                Object activityEndTime = request.getExtra("activityEndTime");
                String activityUrl = page.getUrl().get();

                // 解析内容页面
                String phone = page.getHtml().xpath("//*[@id='allInfo']//p[@class='phone']/span/text()").get();
                String content = page.getHtml().xpath("//*[@id='allInfo']//div[@class='ad_intro']/html()").get();
                Activity activity = new Activity();
                activity.setTitle((String) activityName);
                activity.setAddress((String) activityAddress);
                activity.setBeginTime((String) activityStartTime);
                activity.setEndTime((String) activityEndTime);
                activity.setCoverImage(getImageUrl((String) activityIconUrl));
                activity.setOriginalContent(content);
                activity.setCleanedContent(content);
                activity.setPhone(phone);
                activity.setSourceUrl(activityUrl);
                activity.setRegion((String) activityArea);
                activity.setType(Activity.TYPE_COMMUNITY);
                page.putField("model", activity);
            }
        }
    }

    // 图片403
    public static String getImageUrl(String imgUrl) {
        int index = imgUrl.lastIndexOf(".");
        String suffix = imgUrl.substring(index, imgUrl.length());
        String prefix = imgUrl.substring(0, index);
        return prefix + "_300_300" + suffix;
    }

    @Override
    public Site getSite() {
        return Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setDomain("www.wenhuayun.cn").addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8").setSleepTime(1000).setRetryTimes(3);
    }

    private String getRealUrl(String format, Object arg) {
        return String.format(format, arg);
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }
}
