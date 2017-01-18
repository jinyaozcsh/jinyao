package cn.swao.jinyao.crawl.special;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.management.JMException;

import org.apache.commons.collections.bag.SynchronizedSortedBag;

import cn.edu.hfut.dmic.contentextractor.ContentExtractor;
import cn.edu.hfut.dmic.contentextractor.News;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:腾讯新闻抓爬
 */
public class TengXunProcessor implements PageProcessor {
    
	private static final String StartUrlFormat = "http://roll.news.qq.com/interface/roll.php?cata=newsgn,newsgj,newssh,milite&site=news&mode=1&of=json&page=%s&date=%s";

	// 起始页
	private static int pageCount = 0;

	// 成功返回码
	private static final String SUCCESS = "0";

	// json匹配正则
	private static final String JSON_URL_REGEX = "http://roll\\.news\\.qq\\.com/interface/roll\\.php";

	// 日期计数
	private static int dateCount = 0;

	// 新闻的日期范围 如 50天内的新闻
	private static int dateSize = 100;

	// 线程数
	private static final int THREAD_NUM = 1000;

	private static final String path = "d:/news/tengxun/test/";

	private static final String REFERER = "http://roll.news.qq.com/";

	// 获取下一页
	private static synchronized int getPageCount(boolean isList) {
		if (isList) {
			pageCount = 0;
		}
		return ++pageCount;
	}

	public static void main(String[] args) throws JMException {
		Spider spider = Spider.create(new TengXunProcessor()).addPipeline(new JsonFilePipeline(path))
				.addPipeline(new ConsolePipeline()).thread(THREAD_NUM);
		int count = getPageCount(false);
		String date = getDateString();
		String url = getFormatUrl(StartUrlFormat, count, date);
		Request request = new Request(url);
		request.putExtra("date", date);
		spider.addRequest(request);
		SpiderMonitor.instance().register(spider);
		spider.start();
	}

	// 获取下一页数据
	public static synchronized String getFormatUrl(String format, Object... arg) {
		String formatString = format;
		return String.format(formatString, arg);
	}

	@Override
	public void process(Page page) {
		if (page.getUrl().regex(JSON_URL_REGEX).match()) {
			String code = page.getJson().jsonPath("$.response.code").get();
			if (code.equals(SUCCESS)) {
				// 有数据
				// 添加下一页json数据链接
				// date不变 page+1;
				int count = getPageCount(false);
				String date = page.getRequest().getExtra("date").toString();
				Request jsonRequest = new Request(getFormatUrl(StartUrlFormat, count, date));
				jsonRequest.putExtra("date", date);
				page.addTargetRequest(jsonRequest);

				// 解析json
				String data = page.getJson().jsonPath("$.data.article_info").get();
				Html dataHtml = new Html(data);
				List<Selectable> list = dataHtml.xpath("//ul//li").nodes();
				list.forEach(e -> {
					String t_tit = e.xpath("//*[@class='t-tit']/text()").get();
					String t_time = e.xpath("//*[@class='t-time']/text()").get();
					String title = e.xpath("//a/text()").get();
					String url = e.links().get();
					// 添加目标新闻链接
					Request request = new Request(url);
					request.putExtra("t_tit", t_tit);
					request.putExtra("t_time", t_time);
					request.putExtra("title", title);
					request.putExtra("date", date);
					page.addTargetRequest(request);
				});
			} else {
				// 当前日期没有数据了
				// page重置,date+1
				// synchronized (TengXunProcessor.class) {

				// TODO
				if (dateCount < dateSize) {
					// 在需要抓去的天数范围内
					int count = getPageCount(true);
					String date = getDateString();
					Request jsonRequest = new Request(getFormatUrl(StartUrlFormat, count, date));
					jsonRequest.putExtra("date", date);
					page.addTargetRequest(jsonRequest);
				}
				// }
			}
			page.setSkip(true);
		} else {
			// 新闻
			try {
				News news = ContentExtractor.getNewsByHtml(page.getHtml().get());
				Request request = page.getRequest();
				page.putField("t_tit", request.getExtra("t_tit"));
				page.putField("t_time", request.getExtra("t_time"));
				page.putField("title", request.getExtra("title"));
				page.putField("date", request.getExtra("date"));
				page.putField("url", page.getUrl().get());
				String content = news.getContent();
				if (content.length() < 200) {
					page.setSkip(true);
				}
				// 去掉视屏正在加载
				Pattern pattern = Pattern.compile("自动播放.+< >");
				Matcher matcher = pattern.matcher(content);
				content = matcher.replaceAll("");
				page.putField("content", content);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public Site getSite() {
		return site;
	}

	public static synchronized String getDateString() {
		// 当天的日期
		Date currentDate = new Date();
		// 当天的时间戳
		long currentTime = currentDate.getTime();
		// 一天的毫秒数
		long dayTime = 1000 * 60 * 60 * 24;
		// 需要减去的毫秒数
		long needTime = currentTime - dayTime * dateCount;
		dateCount++;
		Date resultDate = new Date(needTime);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = dateFormat.format(resultDate);
		return dateString;
	}

	private Site site = Site.me().addHeader("Referer", REFERER)
			.setUserAgent(
					"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
			.setSleepTime(1000).setTimeOut(1000 * 10).setRetryTimes(10).setCycleRetryTimes(10);
}
