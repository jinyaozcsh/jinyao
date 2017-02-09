package cn.swao.jinyao.crawl.special;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.bag.SynchronizedSortedBag;
import org.springframework.stereotype.Service;

import cn.swao.baselib.util.ArrayUtils;
import cn.swao.baselib.util.DateUtils;
import cn.swao.framework.api.CustomBizException;
import cn.swao.framework.util.WebUtils;
import cn.swao.jinyao.model.Activity;
import cn.swao.jinyao.pipeline.MongodbPipeline;
import cn.swao.jinyao.repository.ActivityRepository;
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
                String activityAddress = (String) request.getExtra("activityAddress");
                String activityArea = (String) request.getExtra("activityArea");
                String activityIconUrl = (String) request.getExtra("activityIconUrl");
                // Object activityId = request.getExtra("activityId");
                String activityName = (String) request.getExtra("activityName");
                String activityStartTime = (String) request.getExtra("activityStartTime");
                String activityEndTime = (String) request.getExtra("activityEndTime");
                String activityUrl = page.getUrl().get();

                // 解析内容页面
                String phone = page.getHtml().xpath("//*[@id='allInfo']//p[@class='phone']/span/text()").get();
                String content = page.getHtml().xpath("//*[@id='allInfo']//div[@class='ad_intro']/html()").get();
                String date = page.getHtml().xpath("//*[@id='allInfo']//p[@class='time']/span/text()").get().replace("\u00A0", "").trim();
                String time = page.getHtml().xpath("//*[@id='allInfo']//p[@class='period']/span/text()").get().trim();
                Date[] beginTimeAndEndTime = getBeginTimeAndEndTime(date, time);
                Activity activity = new Activity();
                activity.setTitle(activityName);
                activity.setAddress(activityAddress);
                activity.setBeginTime(beginTimeAndEndTime[0]);
                activity.setEndTime(beginTimeAndEndTime[1]);
                activity.setCoverImage(getImageUrl(activityIconUrl));
                activity.setOriginalContent(content);
                activity.setCleanedContent(content);
                activity.setPhone(phone);
                activity.setSourceUrl(activityUrl);
                activity.setRegion(activityArea.split(",")[1]);
                activity.setType(Activity.TYPE_COMMUNITY);
                page.putField("model", activity);
            }
        }
    }

    public Date[] getBeginTimeAndEndTime(String date, String time) {
        String[] result = new String[2];
        String[] times = time.split("-");
        // 处理时间
        if (times.length != 2) {
            new CustomBizException("时间解析错误");
        } else {
            result[0] = times[0];
            result[1] = times[1];
        }
        // 处理日期
        if (date.contains("-")) {
            String[] dates = date.split("-");
            result[0] = dates[0].trim() + result[0];
            result[1] = dates[1].trim() + result[1];

        } else {
            result[0] = date.trim() + result[0];
            result[1] = date.trim() + result[1];
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日HH:mm");
        Date[] dateResult = new Date[2];
        try {
            dateResult[0] = dateFormat.parse(result[0]);
            dateResult[1] = dateFormat.parse(result[1]);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateResult;
    }

    public static void main(String[] args) {
        Spider spider = Spider.create(new CommunityActivityProcessor());
        spider.addUrl(CommunityActivityProcessor.START_URL);
        spider.thread(10).run();
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
