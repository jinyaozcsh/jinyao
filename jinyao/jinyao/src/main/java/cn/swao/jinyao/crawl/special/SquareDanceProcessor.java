package cn.swao.jinyao.crawl.special;

import java.util.*;

import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.gson.*;

import cn.swao.baselib.util.*;
import cn.swao.jinyao.crawl.PoxyIP;
import cn.swao.jinyao.model.SquareDance;
import cn.swao.jinyao.pipeline.JsonFilePipeline;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.*;

@Service
public class SquareDanceProcessor implements PageProcessor {

    public String baseUrl = "http://www.999d.com/index.php?v=video&tid=0&type=new&difficulty=0&ajax=1&page=%s&_=%s";
    public String doman = "http://www.999d.com";
    public String songUrl = "http://www.999d.com/song/%s.html";
    public Site site;

    @Override
    public Site getSite() {
        site = Site.me().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36").setHttpProxy(PoxyIP.getpoxy()).setSleepTime(2000).setTimeOut(5000).setRetryTimes(5);
        return site;
    }

    @Override
    public void process(Page page) {
        // site.setHttpProxy(PoxyIP.getpoxy());
        String baseListUrl = page.getUrl().get();
        // json列表
        if (baseListUrl.contains("http://www.999d.com/index.php?")) {
            page.setSkip(true);
            String jsonString = page.getJson().toString();
            List<Object> fromJson = JSONUtils.fromJson(jsonString, List.class);
            Iterator<JsonElement> it = JSONUtils.parseArray(jsonString).iterator();

            while (it.hasNext()) {
                SquareDance squareDance = new SquareDance();
                JsonObject jsonObject = it.next().getAsJsonObject();
                String alias = jsonObject.get("alias").getAsString();// 别名
                String title = jsonObject.get("title").getAsString();
                String[] split = alias.split(" ");
                try {
                    String dance_team = split[0];
                    squareDance.setTeam(dance_team);
                    String dance_name = split[1];
                    squareDance.setName(dance_name);
                    String action = split[2];
                    squareDance.setType(action);
                } catch (Exception e) {
                }
                String thumb = jsonObject.get("thumb").getAsString();// 视频标题
                String video_html = doman + jsonObject.get("url").getAsString();// sourceUrl
                String play_count = jsonObject.get("play_count").getAsString();
                String updatetime = jsonObject.get("updatetime").getAsString();
                String song_title = StringUtils.getString(jsonObject.get("song_title"));
                String album_id = StringUtils.getString(jsonObject.get("album_id"));
                squareDance.setAlias(alias);
                squareDance.setThumb(thumb);
                squareDance.setSourceUrl(video_html);
                squareDance.setPlay_count(play_count);
                squareDance.setPublish_time(updatetime);
                squareDance.setSong_title(song_title);
                squareDance.setJsonString(fromJson);
                squareDance.setTitle(title);
                Request request = new Request(video_html);
                request.putExtra("album_id", album_id);
                request.putExtra("squareDance", squareDance);
                page.addTargetRequest(request);
            }

            // 视频
        } else if (baseListUrl.contains("http://www.999d.com/video")) {
            page.setSkip(true);
            Request request = page.getRequest();
            String album_id = request.getExtra("album_id").toString();
            SquareDance squareDance = (SquareDance) request.getExtra("squareDance");
            Html html = page.getHtml();
            String video_url = getVideoUrl(html);
            // String song_html = html.xpath("//*[@id=\"song_download_" + id + "\"]/a/@href").get();
            String down_video_url = html.xpath("/html/body/div[3]/div[4]/div[5]/a").get();
            down_video_url = getSongUrl(down_video_url);
            if (!Strings.isNullOrEmpty(album_id)) {
                String song_html = String.format(songUrl, album_id.replaceAll("\"", ""));
                System.out.println("----------------------" + song_html);
                squareDance.setVideo_url(video_url);
                squareDance.setSong_html(song_html);
                // squareDance.setDown_video_url(down_video_url);
                request.setUrl(song_html);
                request.putExtra("squareDance", squareDance);
                page.addTargetRequest(request);
            }
            // 音频
        } else if (baseListUrl.contains("http://www.999d.com/song")) {
            Request request = page.getRequest();
            SquareDance squareDance = (SquareDance) request.getExtra("squareDance");
            Html html = page.getHtml();
            List<Selectable> nodes = html.xpath("//*[@id=\"playlist\"]//div").nodes();
            List<Map<String, String>> list = new ArrayList<Map<String, String>>();
            for (Selectable selectable : nodes) {
                Selectable xpath = selectable.xpath("/div/@class");
                if (xpath.get().contains("songs song")) {
                    Map<String, String> map = new HashMap<String, String>();
                    String singer = selectable.nodes().get(0).xpath("//[@class=mv-info]/div/span/text()").get();
                    String song_name = selectable.nodes().get(0).xpath("//[@class=mv-info]/div/p/text()").get();
                    String down_song_url = selectable.xpath("//[@class=mv-operator]/a").get();
                    down_song_url = getSongUrl(down_song_url);
                    map.put("singer", singer);
                    map.put("song_name", song_name);
                    map.put("down_song_url", down_song_url);
                    list.add(map);
                }
            }
            squareDance.setSongList(list);
            squareDance.setStatus(1);
            page.putField("model", squareDance);
        } else {
            page.setSkip(true);
        }

    }

    public String getSongUrl(String down_song_url) {
        down_song_url = down_song_url.substring(down_song_url.indexOf("?"), down_song_url.indexOf("\">"));
        down_song_url = doman + "/api" + down_song_url.replaceAll("\"", "");
        down_song_url = down_song_url.replaceAll("amp;", "").replaceAll("&quot;", "").replaceAll(";", "");
        return down_song_url;
    }

    public String getVideoUrl(Html html) {
        String script = html.xpath("//[@class=tv]/script[2]").get();
        String substring = script.substring(script.indexOf("webkit_playlist = ") + "webkit_playlist = ".length(), script.indexOf("var loop =")).trim();
        substring = substring.replace(";", "");
        JsonArray parseArray = JSONUtils.parseArray(substring);
        String video_url = null;
        if (parseArray.size() == 1) {
            video_url = parseArray.get(0).getAsString();
        } else {
            video_url = parseArray.get(1).getAsString();
        }
        return video_url.replaceAll("'", "");
    }

    public static void main(String[] args) {
        SquareDanceProcessor squareDanceProcessor = new SquareDanceProcessor();
        long currentTimeMillis = System.currentTimeMillis();
        for (int i = 1; i < 2; i++) {
            String format = String.format(squareDanceProcessor.baseUrl, i, currentTimeMillis + i);
            Spider.create(squareDanceProcessor).addPipeline(new JsonFilePipeline("c:/squaredance11.txt")).addUrl(format).thread(1).run();
        }
    }

}
