package cn.swao.jinyao.catsh.special;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * @author ShenJX
 * @date 2017年1月17日
 * @desc 语音新闻抓取
 */
public class SoundNewsProcessor implements PageProcessor {

	// 保存回调接口
	public ISoundNewsSave saveCallBack = null;

	// Json数据入口
	public static final String START_URL = "http://listen.eastday.com/node2/node3/n1416/index1416_t81.html";

	// 内容详情界面url
	public static final String FORMAT_CONTENT_URL = "http://share.eastday.com/newsapi/api/news/detail/%s";

	// 返回码
	public static final String CODE_SUCCESS = "1";

	public static final String CODE_FAIL = "0";

	// 页数
	public static int pageCount = 0;

	// json数据的地址格式
	public static final String FORMAT_URL = "http://listen.eastday.com%s";

	@Override
	public void process(Page page) {

	}

	@Override
	public Site getSite() {
		return Site.me()
				.setUserAgent(
						"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36")
				.setSleepTime(1000).setRetryTimes(3);
	}

	private String getRealUrl(String format, String arg) {
		return String.format(format, arg);
	}

	public interface ISoundNewsSave {
		void soundNewsSave();
	}

	public void setSaveCallBack(ISoundNewsSave saveCallBack) {
		this.saveCallBack = saveCallBack;
	}
}
