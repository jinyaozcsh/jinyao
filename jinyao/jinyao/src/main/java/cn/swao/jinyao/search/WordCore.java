package cn.swao.jinyao.search;

import java.util.regex.*;

public class WordCore {

    public static int getBusCore(String str) {

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
           /* if (str.contains("路"))
                score += 5;*/
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
        /*if (str.contains("路")) {
            score += 5;
            if (m.find()) {
                score += 15;
            }
            if (str.contains("车"))
                score += 5;
        }*/
        if (str.contains("来") || str.contains("到"))
            score += 8;
        return score;
    }

    public static int getSearchBusStatus(String str) {
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

}
