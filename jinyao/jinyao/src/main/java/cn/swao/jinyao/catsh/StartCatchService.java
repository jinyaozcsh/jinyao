package cn.swao.jinyao.catsh;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.swao.jinyao.catsh.special.ActualNewsProcessor;
import cn.swao.jinyao.catsh.special.ActualNewsProcessor.IActualNewsSave;
import cn.swao.jinyao.catsh.special.SoundNewsProcessor;
import cn.swao.jinyao.catsh.special.SoundNewsProcessor.ISoundNewsSave;
import us.codecraft.webmagic.Spider;

/**
 * 
 * @author ShenJX
 * @date 2017年1月17日
 * @desc desc:爬虫启动类
 */
@Service
public class StartCatchService implements IActualNewsSave, ISoundNewsSave {

	@Autowired
	private ActualNewsProcessor actualNewsProcessor;

	@Autowired
	private SoundNewsProcessor soundNewsProcessor;

	@Test
	public void test() {
		// startActualNews();
		startSoundNews();
	}

	/**
	 * 启动实时新闻抓爬
	 */
	public void startActualNews() {
		ActualNewsProcessor actualNewsProcessor = new ActualNewsProcessor();
		actualNewsProcessor.setSaveCallBack(this);
		Spider.create(actualNewsProcessor).addUrl(ActualNewsProcessor.START_URL).run();
		// Spider spider = Spider.create(actualNewsProcessor);
		// spider.addUrl(ActualNewsProcessor.START_URL);
		// spider.start();

	}

	/**
	 * 启动语音新闻抓爬
	 */
	public void startSoundNews() {
		SoundNewsProcessor soundNewsProcessor = new SoundNewsProcessor();
		soundNewsProcessor.setSaveCallBack(this);
		Spider.create(soundNewsProcessor).addUrl(SoundNewsProcessor.START_URL).run();
		// Spider spider = Spider.create(soundNewsProcessor);
		// spider.addUrl(SoundNewsProcessor.START_URL);
		// spider.start();

	}

	/**
	 * 实时新闻保存回调
	 */
	@Override
	public void actualNewsSave(String newstitle, String newsimg, String newszy, String newstime, String newslink,
			String name, String content, String date, String code) {
		System.out.println(code);
		System.out.println(newslink);
		System.out.println(newstitle);
		System.out.println(newsimg);
		System.out.println(newszy);
		System.out.println(newstime);
		System.out.println(name);
		System.out.println(content);
		System.out.println(date);
		System.out.println("=================");
	}

	/**
	 * 语音新闻保存回调
	 */
	@Override
	public void soundNewsSave() {

	}
}
