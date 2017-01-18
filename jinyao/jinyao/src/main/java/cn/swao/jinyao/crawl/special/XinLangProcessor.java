package cn.swao.jinyao.crawl.special;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:新浪新闻抓爬
 */
public class XinLangProcessor implements PageProcessor {
    private Site site = Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setSleepTime(1000).setTimeOut(1000 * 10).setRetryTimes(10).setCycleRetryTimes(10);

    private static Spider spider = null;
    // 分页大小
    private static final int PAGE_SIZE = 20;
    // 入口url前缀
    // 社会
    // private static final String START_URL_PREFIX =
    // "http://api.roll.news.sina.com.cn/zt_list";
    // 科技
    private static final String START_URL_PREFIX = "http://feed.mix.sina.com.cn/api/roll/get";

    // 需要格式化的参数
    private static final String FORMAT_START_URL_ARGS = "pageid=1&lid=21&num=" + PAGE_SIZE + "&versionNumber=1.2.8&encode=utf-8&page=%s";
    private static String startUrl = null;

    private static String formatPageUrl = null;
    // 社会
    // private static String JS_URL =
    // "http://api\\.roll\\.news\\.sina\\.com\\.cn/zt_list";
    // 科技
    private static String JS_URL = "http://feed\\.mix\\.sina\\.com\\.cn/api/roll/get";

    @Override
    public void process(Page page) {
        if (page.getUrl().regex(JS_URL).match()) {
            String json = page.getJson().get();
            try {
                JSONObject jsonObject = new JSONObject(json);
                JSONObject jsonResult = jsonObject.getJSONObject("result");
                JSONArray jsonArray = jsonResult.getJSONArray("data");
                // 无数据时候结束
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonNews = jsonArray.getJSONObject(i);
                        String url = getRightJsonString(jsonNews.getString("url"));
                        String title = getRightJsonString(jsonNews.getString("title"));
                        String keywords = getRightJsonString(jsonNews.getString("keywords"));
                        String media_name = getRightJsonString(jsonNews.getString("media_name"));
                        // String createtime = jsonNews.getString("createtime");
                        // 添加ajax中的链接
                        Request request = new Request(url);
                        request.putExtra("title", title);
                        request.putExtra("keywords", keywords);
                        request.putExtra("source", media_name);
                        page.addTargetRequest(request);
                    }
                    // 获取下一页链接
                    String nextPageUrl = getNextPageUrl(formatPageUrl);
                    // 添加下一页的链接
                    page.addTargetRequest(nextPageUrl);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("没有获取到json");
                // 获取下一页链接
                String nextPageUrl = getNextPageUrl(formatPageUrl);
                // 添加下一页的链接
                page.addTargetRequest(nextPageUrl);
            } finally {
                page.setSkip(true);
            }

        } else {
            try {
                News news = ContentExtractor.getNewsByHtml(page.getHtml().get());
                page.putField("url", page.getUrl().get());
                page.putField("time", news.getTime());
                page.putField("content", news.getContent());
                page.putField("title", news.getTitle());
                page.putField("type", "社会");
                Request request = page.getRequest();
                page.putField("title", request.getExtra("title").toString());
                page.putField("keywords", request.getExtra("keywords").toString());
                page.putField("source", request.getExtra("source").toString());
                System.out.println("=====================");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String formatAgrs = String.format(FORMAT_START_URL_ARGS, count);
        startUrl = getEndcodedUrl(START_URL_PREFIX, formatAgrs);
        formatPageUrl = startUrl.substring(0, startUrl.length() - 1);
        spider = Spider.create(new XinLangProcessor()).addPipeline(new ConsolePipeline()).addPipeline(new JsonFilePipeline("d:/news/xinlang/keji")).addUrl(startUrl).thread(100);
        spider.start();
    }

    private static int count = 5;

    /**
     * 获取下一页数据
     */
    private static synchronized String getNextPageUrl(String format) {
        count++;
        String url = format + count;
        return url;
    }

    /**
     * 获取编码后的url
     * 
     * @param prefixString url前缀, ?前的url(不包含'?')
     * @param argsString 参数字符串, ?后的字符串(不包含'?')
     * @return 编码后的url
     * @throws UnsupportedEncodingException
     */
    public static String getEndcodedUrl(String prefixString, String argsString) throws UnsupportedEncodingException {
        String[] args = argsString.split("&");
        String[] encodeArgs = new String[args.length];
        for (int i = 0; i < encodeArgs.length; i++) {
            String arg = args[i];
            int index = arg.indexOf("=");
            String result = args[i].substring(index + 1, arg.length());
            String prefix = arg.substring(0, index + 1);
            encodeArgs[i] = prefix + URLEncoder.encode(result, "utf-8");
        }
        String resultArgs = String.join("&", encodeArgs);
        return prefixString + "?" + resultArgs;
    }

    /**
     * unicode解码
     * 
     * @param str
     * @return
     */
    public static String decodeUnicode(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher(str);
        int start = 0;
        int start2 = 0;
        StringBuffer sb = new StringBuffer();
        while (m.find(start)) {
            start2 = m.start();
            if (start2 > start) {
                String seg = str.substring(start, start2);
                sb.append(seg);
            }
            String code = m.group(1);
            int i = Integer.valueOf(code, 16);
            byte[] bb = new byte[4];
            bb[0] = (byte) ((i >> 8) & 0xFF);
            bb[1] = (byte) (i & 0xFF);
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append(String.valueOf(set.decode(b)).trim());
            start = m.end();
        }
        start2 = str.length();
        if (start2 > start) {
            String seg = str.substring(start, start2);
            sb.append(seg);
        }
        return sb.toString();
    }

    /**
     * 获取解码
     * 
     * @param json
     * @return
     */
    public String getRightJsonString(String json) {
        String unicodeString = decodeUnicode(json);
        // String result = unicodeString.replace("\\", "");
        return unicodeString;
    }
}
