package cn.swao.jinyao.cash;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;

import org.bson.Document;

import com.beust.jcommander.Strings;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.google.gson.*;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.CRF.CRFSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.mongodb.client.*;
import com.mongodb.client.model.InsertOneModel;

import antlr.StringUtils;
import cn.swao.baselib.util.*;
import cn.swao.jinyao.hanlp.HanlpSearchService;
import cn.swao.jinyao.util.MongodbUtils;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.selenium.SeleniumDownloader;
import us.codecraft.webmagic.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.processor.example.GithubRepoPageProcessor;
import us.codecraft.webmagic.selector.*;

public class CashNews implements PageProcessor {

	private String[] urls = { "http://temp.163.com/special/00804KVA/cm_yaowen.js",
			"http://public.house.163.com/special/03531F4E/index_news.js",
			"http://temp.163.com/special/00804KVA/cm_shehui.js", "http://temp.163.com/special/00804KVA/cm_guonei.js",
			"http://temp.163.com/special/00804KVA/cm_guoji.js", "http://temp.163.com/special/00804KVA/cm_dada.js",
			"http://temp.163.com/special/00804KVA/cm_dujia.js", "http://temp.163.com/special/00804KVA/cm_war.js",
			"http://temp.163.com/special/00804KVA/cm_money.js", "http://temp.163.com/special/00804KVA/cm_tech.js",
			"http://temp.163.com/special/00804KVA/cm_sports.js", "http://temp.163.com/special/00804KVA/cm_ent.js",
			"http://temp.163.com/special/00804KVA/cm_lady.js", "http://temp.163.com/special/00804KVA/cm_auto.js",
			"http://temp.163.com/special/00804KVA/cm_houseshanghai.js",
			"http://temp.163.com/special/00804KVA/cm_hangkong.js",
			"http://temp.163.com/special/00804KVA/cm_jiankang.js" };
	private Site site = Site.me().setRetryTimes(5).setSleepTime(1000).setCharset("gbk");

	static Map<String, String> map = new HashMap<String, String>();
	static List<String> urlList = new ArrayList<String>();

	public void getUrls() {
		if (urlList.isEmpty()) {
			Map<String, String> mapUrl = new HashMap<String, String>();
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_yaowen.js", "要闻");
			mapUrl.put("http://public.house.163.com/special/03531F4E/index_news.js", "本地");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_shehui.js", "社会");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_guonei.js", "国内");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_guoji.js", "国际");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_dada.js", "哒哒");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_dujia.js", "独家");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_war.js", "军事");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_money.js", "财经");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_tech.js", "科技");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_sports.js", "体育");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_ent.js", "娱乐");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_lady.js", "时尚");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_auto.js", "汽车");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_houseshanghai.js", "房产");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_hangkong.js", "航空");
			mapUrl.put("http://temp.163.com/special/00804KVA/cm_jiankang.js", "健康");
			map.putAll(mapUrl);
			for (int i = 2; i <= 30; i++) {
				for (String key : mapUrl.keySet()) {
					StringBuffer sb = new StringBuffer(key);
					if (i < 10) {
						sb.insert(sb.indexOf(".js"), "_0" + i);
					} else {
						sb.insert(sb.indexOf(".js"), "_" + i);
					}
					map.put(sb.toString(), mapUrl.get(key));
				}

			}
		}
		urlList.addAll(map.keySet());
	}

	/*
	 * public static void main(String[] args) throws MalformedURLException,
	 * IOException { new CashNews().getUrls(); read(); Spider.create(new
	 * CashNews()).addUrl("http://news.163.com/").addPipeline(new
	 * JsonFilePipeline("D:/FilesOutput/")) .setDownloader(new
	 * SeleniumDownloader("E:/work/package/chromedriver.exe"))
	 * .thread(5).start(); }
	 */

	@Override
	public Site getSite() {
		return site;
	}

	@Override
	public void process(Page page) {
		if (page.getUrl().get().equals("http://news.163.com/")) {
			// List<String> all =
			// page.getHtml().xpath("//*[@id=\"index2016_wrap\"]/div[2]/div[2]/div[3]/div[2]/div[5]/div/ul/li[1]/div").links().all();
			page.addTargetRequests(detailUrl);
		} else {
			/*
			 * try { Json json = page.getJson(); String rawText =
			 * json.removePadding("data_callback").get(); JsonArray parse =
			 * JSONUtils.parseArray(rawText); Iterator<JsonElement> it =
			 * parse.iterator(); while (it.hasNext()) { JsonElement next =
			 * it.next(); String docurl =
			 * next.getAsJsonObject().get("docurl").getAsString(); String
			 * commenturl =
			 * next.getAsJsonObject().get("commenturl").getAsString(); if
			 * (!Strings.isStringEmpty(docurl)) { Map<String, String> sourceUrl
			 * = new HashMap<String, String>(); sourceUrl.put("docurl", docurl);
			 * sourceUrl.put("commenturl", commenturl);
			 * sourceUrl.put("sourceUrl", page.getUrl().get());
			 * putFile("D:/news.txt", JSONUtils.toJson(sourceUrl)); } } } catch
			 * (Exception e) { e.printStackTrace(); page.setSkip(true);
			 * System.out.println(page.getUrl().get()); } }
			 */

			String onetitle = page.getHtml().xpath("//*[@id=\"ne_wrap\"]/body/div[4]/div[1]/div[1]/a[2]/text()").get();
			String twotile = page.getHtml().xpath("//*[@id=\"ne_wrap\"]/body/div[4]/div[1]/div[1]/a[3]/text()").get();
			String title = page.getHtml().xpath("//*[@id=\"epContentLeft\"]/h1/text()").get();
			String source = page.getHtml().xpath("//*[@id=\"ne_article_source\"]/text()").get();
			String content = page.getHtml().xpath("//*[@id=\"endText\"]").get();
			String date = page.getHtml().xpath("//*[@id=\"epContentLeft\"]/div[1]/div[1]/text()").get();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("onetitle", onetitle);
			map.put("twotile", twotile);
			map.put("title", title);
			map.put("source", source);
			map.put("content", content);
			map.put("sourceUrl", page.getUrl().get());
			map.put("date", date);
			map.put("type", detailUrlMap.get(page.getUrl().get()));
			if (content != null) {
				putFile("D:/newsdetail.txt", JSONUtils.toJson(map));
			}
			page.putField("news", map);

		}
	}

	public synchronized void putFile(String filePath, String str) {
		if (str == null) {
			return;
		}
		try {
			FileWriter out = new FileWriter(filePath, true);
			out.write(str + "\n");
			out.flush();
			out.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static HttpURLConnection getcon(String link) {
		int min = 2 + (int) Math.random() * 3;
		try {
			Thread.sleep(min * 1000L);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		HttpURLConnection conn = null;
		try {
			URL url = new URL(link);
			conn = (HttpURLConnection) url.openConnection();
			conn.connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static InputStream getIn(String link) {
		InputStream in = null;
		HttpURLConnection con = getcon(link);
		if (con != null) {
			try {
				in = con.getInputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return in;
	}

	static List<String> detailUrl = new ArrayList<String>();
	static Map<String, String> detailUrlMap = new HashMap<String, String>();

	public static void read() {
		try {
			BufferedReader r = new BufferedReader(new FileReader("D:/news.txt"));
			String str = null;
			while ((str = r.readLine()) != null) {
				JsonObject parse = JSONUtils.parse(str);
				detailUrl.add(parse.get("docurl").getAsString());
				detailUrlMap.put(parse.get("docurl").getAsString(), map.get(parse.get("sourceUrl").getAsString()));
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		/*
		 * for (int i = 0; i < baKeyword.length; i++) { try { baKeyword[i] =
		 * (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
		 * } catch (Exception e) { e.printStackTrace(); } }
		 */
		try {
			s = new String(s.getBytes(), "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	public static void main(String[] args) {
		CashNews cashNews = new CashNews();
		cashNews.read2();
	}

	public static String delHTMLTag(String htmlStr) {
		String regEx_script = "<script[^>]*?>[\\s\\S]*?<\\/script>"; // 定义script的正则表达式
		String regEx_style = "<style[^>]*?>[\\s\\S]*?<\\/style>"; // 定义style的正则表达式
		String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

		Pattern p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
		Matcher m_script = p_script.matcher(htmlStr);
		htmlStr = m_script.replaceAll(""); // 过滤script标签

		Pattern p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
		Matcher m_style = p_style.matcher(htmlStr);
		htmlStr = m_style.replaceAll(""); // 过滤style标签

		Pattern p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
		Matcher m_html = p_html.matcher(htmlStr);
		htmlStr = m_html.replaceAll(""); // 过滤html标签

		return htmlStr.trim(); // 返回文本字符串
	}

	public void read2() {
		try {
			BufferedReader r = new BufferedReader(new FileReader("D:/newsdetail.txt"));
			String str = null;
			MongoCollection<Document> collection = MongodbUtils.getCollection("163news");
			Date date = new Date();
			while ((str = r.readLine()) != null) {
				Map fromJson = JSONUtils.fromJson(str, Map.class);
				String content = fromJson.get("content").toString();
				content = toStringHex(content);
				content = delHTMLTag(content);
				fromJson.put("content", content);
				String content_nsortAndcrf = JSONUtils.toJson(HanlpSearchService.nAndcrf(content));
				fromJson.put("content_nsortAndcrf", content_nsortAndcrf);
				fromJson.put("create_date", date);
				Document d = new Document();
				d.putAll(fromJson);
				collection.insertOne(d);
			}
			r.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getCRF(String content) {
		List<String> word = new ArrayList<String>();
		Segment segment = new CRFSegment();
		segment.enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true)
				.enablePartOfSpeechTagging(true);
		for (Term term : segment.seg(content)) {
			word.add(term.word);
		}
		return JSONUtils.toJson(word);
	}

}
