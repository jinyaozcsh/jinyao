package cn.swao.jinyao.search;

import java.net.URLEncoder;
import java.util.*;
import java.util.regex.*;

import org.bson.Document;

import com.google.gson.JsonObject;
import com.mongodb.client.*;

import cn.swao.baselib.util.*;
import cn.swao.jinyao.util.MongodbUtils;

public class BusSearchService {

    String detailBusInfo = "http://xxbs.sh.gov.cn:8080/weixinpage/HandlerThree.ashx?name=%s&lineid=%s&stopid=%s&direction=%s";

    public String getBusInfo(String str, Double[] d) throws Exception {
        Pattern p = Pattern.compile("\\d{1,}");
        str = bulidTextZHToALB(str);
        Matcher m = p.matcher(str);
        if (m.find()) {
            String group = m.group();
            int BusStatus = getSearchBusStatus(str);
            return getTimeBus(group, d, BusStatus);
        }
        return null;
    }

    public String getTimeBus(String str, Double[] d, int status) throws Exception {
        String result = "";
        MongoCursor<Document> it = MongodbUtils.getCollectionIt("bus_line");
        String rode = str + "路";
        if (status == 2) {
            while (it.hasNext()) {
                Document next = it.next();
                String name = next.get("name").toString();
                if (rode.equals(name)) {
                    String line_id = next.get("line_id").toString();
                    System.out.println(next);
                    if (next.get("direction") != null) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> dirction = next.get("direction", List.class);
                        for (int i = 0; i < dirction.size(); i++) {
                            Map<String, Object> map = dirction.get(i);
                            result += str + "路公交车从%s开往%s,起始发车时间%s,最晚发车时间%s,";
                            String end_stop = map.get("end_stop").toString();
                            List<Map<String, Object>> stop_list = (List<Map<String, Object>>) map.get("stop_list");
                            String start_stop = map.get("start_stop").toString();
                            String early_time = map.get("early_time").toString();
                            String late_time = map.get("late_time").toString();
                            result = String.format(result, start_stop, end_stop, early_time, late_time);
                            result += "离你最近的站是%s,公交车还有%s站，约%s分钟到达";
                            double distance = 1000000000000000L;
                            String id = null;
                            String distance_stop = null;
                            for (Map<String, Object> detailMap : stop_list) {
                                String detail_stop_name = detailMap.get("stop_name").toString();
                                double stop_lng = (double) detailMap.get("stop_lng");
                                double stop_lat = (double) detailMap.get("stop_lat");
                                String stop_id = detailMap.get("stop_id").toString();
                                double distanceOfMeter = GeoUtils.getDistanceOfMeter(d[0], d[1], stop_lat, stop_lng);
                                if (distanceOfMeter < distance) {
                                    distance = distanceOfMeter;
                                    id = stop_id;
                                    distance_stop = detail_stop_name;
                                }
                            }
                            String url = String.format(detailBusInfo, URLEncoder.encode(str + "路", "UTF-8"), URLEncoder.encode(line_id, "UTF-8"), id, i, "");
                            JsonObject json = NetUtils.httpGet(url, "busSearch");
                            JsonObject searchDetail = json.get("cars").getAsJsonArray().get(0).getAsJsonObject();

                            searchDetail.get("terminal").getAsString();
                            String stopdis = searchDetail.get("stopdis").getAsString();
                            String time = searchDetail.get("time").getAsString();
                            int t = Integer.parseInt(time) / 60;
                            result = String.format(result, distance_stop, stopdis, t);
                        }

                    } else {
                        result = "未查询到对应信息";
                    }
                    break;
                }
            }
        } else if (status == 1) {
            while (it.hasNext()) {
                Document next = it.next();
                String name = next.get("name").toString();
                if (rode.equals(name)) {
                    Object obj = next.get("direction");
                    if (next.get("direction") != null) {
                        @SuppressWarnings("unchecked")
                        List<Map<String, Object>> dirction = next.get("direction", List.class);
                        for (Map<String, Object> map : dirction) {
                            result += str + "路公交车从%s开往%s,起始发车时间%s,最晚发车时间%s";
                            String end_stop = map.get("end_stop").toString();
                            List<Map<String, String>> stop_list = (List<Map<String, String>>) map.get("stop_list");
                            String start_stop = map.get("start_stop").toString();
                            String early_time = map.get("early_time").toString();
                            String late_time = map.get("late_time").toString();
                            result = String.format(result, start_stop, end_stop, early_time, late_time);
                            result += "途中经过的站台有";
                            for (Map<String, String> detailMap : stop_list) {
                                String detail_stop_name = detailMap.get("stop_name");
                                result += detail_stop_name + ",";
                            }
                        }
                    } else {
                        result = "未查询到对应信息";
                    }
                }
            }
        } else if (status == 0) {
            result = "未查询到对应信息";
        }
        return result;
    }

    public int getBusCore(String str) {

        int score = 0;
        Pattern p = Pattern.compile("\\d{1,}");
        Matcher m = p.matcher(str);
        if (m.find()) {
            String group = m.group();
            score += 5;
        }
        if (str.contains("公交")) {
            score += 10;
            if (str.contains("公交车"))
                score += 15;
            /*
             * if (str.contains("路")) score += 5;
             */
        }
        if (str.contains("多")) {
            score += 5;
            if (str.contains("多久") || str.contains("多长"))
                score += 5;
            if (str.contains("还"))
                score += 5;
        }
        if (str.contains("还")) {
            score += 5;
        }
        if (str.contains("时间")) {
            score += 8;
            if (str.contains("多久") || str.contains("多长") || str.contains("多少"))
                score += 10;
        }
        if (str.contains("时候")) {
            score += 5;
            if (str.contains("什么"))
                score += 10;
        }
        /*
         * if (str.contains("路")) { score += 5; if (m.find()) { score += 15; } if (str.contains("车")) score += 5; }
         */
        if (str.contains("来") || str.contains("到"))
            score += 8;
        return score;
    }

    public int getSearchBusStatus(String str) {
        int busCore = WordCore.getBusCore(str);
        int status = 0;
        if (busCore > 30) {
            status = 2;
        } else if (busCore <= 30 && busCore > 5) {
            status = 1;
        } else {
            status = 0;
        }
        return status;
    }

    public static String bulidTextZHToALB(String text) {  
        Pattern p = Pattern.compile(numRegex);  
        Matcher m = p.matcher(text);  
          
        while(m.find()) {  
            String numZH = m.group();  
            if(numZH.length() !=1 || numMap.containsKey(numZH) || zhTen.equals(numZH)) {  
                String numALB = NumZHToALB(numZH);  
                text = text.replaceFirst(numZH, numALB);  
            }  
        }  
          
        return text;  
    }  
      
    private static String NumZHToALB(String numZH) {  
        int numALB = 0;  
        int formIndex = 0;  
        for(String unitNum : unitNumMap.keySet()) {  
            int index = numZH.indexOf(unitNum);  
            if(index != -1 ) {  
                numALB += NumZHToALB(numZH.substring(formIndex, index),  unitNumMap.get(unitNum));  
                formIndex = index + 1;  
            }  
        }  
          
        numALB += NumZHToALB(numZH.substring(formIndex),  1);  
        return String.valueOf(numALB);  
    }  
      
    private static int NumZHToALB(String numZH, int unitNum) {  
        int length = numZH.length();  
        int numALB = 0;  
        if(length != 0) {  
            int fromIndex = 0;  
            for(String unit : unitMap.keySet()) {  
                int index = numZH.indexOf(unit, fromIndex);  
                if(index != -1) {  
                    fromIndex = index + unit.length();  
                    String prevChar = zhOne;  
                    if(index != 0 && numMap.containsKey(prevChar)) {  
                        prevChar = String.valueOf(numZH.charAt(index - 1));  
                    }   
                    numALB += numMap.get(prevChar) * unitMap.get(unit);  
                }  
            }  
              
            String lastChar = String.valueOf(numZH.charAt(length - 1));  
            if(numMap.containsKey(lastChar)) {  
                String pChar = zhTen;  
                if(length != 1) {  
                    pChar = String.valueOf(numZH.charAt(length - 2));  
                    if(zhZero.equals(pChar)) {  
                        pChar = zhTen;  
                    }  
                }  
                numALB += numMap.get(lastChar) * unitMap.get(pChar)/10;  
            }  
        }  
          
        return numALB * unitNum;  
    }  
      
    private static String encodeUnicode(String gbString) {     
        char[] utfBytes = gbString.toCharArray();     
        String unicodeBytes = "";     
        for (int i : utfBytes) {     
            String hexB = Integer.toHexString(i);     
            if (hexB.length() <= 2) {     
                hexB = "00" + hexB;     
            }     
            unicodeBytes = unicodeBytes + "\\u" + hexB;     
        }  
        return unicodeBytes;  
    }  
      
    private static final String zhZero = "零";  
    private static final String zhOne = "一";  
    private static final String zhTen = "十";  
      
    private static final Map<String, Integer> numMap = new HashMap<String, Integer>();  
    static {  
        numMap.put("零", 0);  
        numMap.put("一", 1);  
        numMap.put("二", 2);  
        numMap.put("三", 3);  
        numMap.put("四", 4);  
        numMap.put("五", 5);  
        numMap.put("六", 6);  
        numMap.put("七", 7);  
        numMap.put("八", 8);  
        numMap.put("九", 9);  
    }  
      
    private static final Map<String, Integer> unitNumMap = new LinkedHashMap<String, Integer>();  
    static {  
        unitNumMap.put("亿", 100000000);  
        unitNumMap.put("万", 10000);  
    }  
      
    private static final Map<String, Integer> unitMap = new LinkedHashMap<String, Integer>();  
    static {  
        unitMap.put("千", 1000);  
        unitMap.put("百", 100);  
        unitMap.put("十", 10);  
    }  
      
    private static String numRegex;  
    static {  
        numRegex = "[";  
        for(String s : numMap.keySet()) {  
            numRegex += encodeUnicode(s);  
        }  
        for(String s : unitMap.keySet()) {  
            numRegex += encodeUnicode(s);  
        }  
        for(String s : unitNumMap.keySet()) {  
            numRegex += encodeUnicode(s);  
        }  
        numRegex += "]+";  
    }  

    public static void main(String[] args) throws Exception {
        //System.out.println(bulidTextZHToALB("一十三如"));
         System.out.println(new BusSearchService().getBusInfo("六十四路公交车什么时候到", new Double[] { 31.21336130000, 121.39351510000 }));
    }
}
