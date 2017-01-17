package cn.swao.jinyao.cash;

import java.io.IOException;
import java.net.*;
import java.util.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.google.gson.*;

import cn.swao.baselib.util.JSONUtils;
import cn.swao.framework.util.WebUtils;

public class WebCash {

    public static String titlehead = "http://xxbs.sh.gov.cn:8080/weixinpage/HandlerOne.ashx?name=%s";
    public static String detail = "http://xxbs.sh.gov.cn:8080/weixinpage/HandlerTwo.ashx?name=%s&lineid=%s";

    public static String getBusInfo(String content) throws MalformedURLException, IOException {
        String econtent = URLEncoder.encode(content, "UTF-8");
        String headUrl = String.format(titlehead, econtent);
        JsonObject jsonObject = getJson(headUrl);
        String start_stop = jsonObject.get("start_stop").getAsString();
        String end_stop = jsonObject.get("end_stop").getAsString();
        String line_id = jsonObject.get("line_id").getAsString();
        line_id = URLEncoder.encode(line_id.trim(), "UTF-8");

        String detailUrl = String.format(detail, econtent, line_id);
        jsonObject = getJson(detailUrl);
        JsonArray asJsonArray = jsonObject.get("lineResults0").getAsJsonObject().get("stops").getAsJsonArray();
        Iterator<JsonElement> it = asJsonArray.iterator();
        StringBuffer sb = new StringBuffer();
        sb.append(content + "公交车经过的站台有" + it.next().getAsJsonObject().get("zdmc").getAsString());
        while (it.hasNext()) {
            JsonElement next = it.next();
            String zdmc = next.getAsJsonObject().get("zdmc").getAsString();
            sb.append("、" + zdmc);
        }
        return sb.toString();
    }

    public static JsonObject getJson(String headUrl) throws IOException, MalformedURLException {
        Document document = Jsoup.parse(new URL(headUrl), 60000);
        Elements select = document.select("body");
        String text = select.text();
        Map jsonParams = null;
        try {
            jsonParams = WebUtils.getJsonParams(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObject jsonObject = JSONUtils.toJsonObject(jsonParams);
        return jsonObject;
    }
}
