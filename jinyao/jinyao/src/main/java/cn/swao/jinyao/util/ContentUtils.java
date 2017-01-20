package cn.swao.jinyao.util;

import java.util.List;
import java.util.regex.*;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;

public class ContentUtils {

    public static String outerHtml(String content) {
        Pattern compile = Pattern.compile("<div.*?</div>");
        Matcher matcher = compile.matcher(content);
        content = matcher.replaceAll("");
        return content;
    }

    public static void simpleHtml(String content) {

        Document doc = Jsoup.parse(content);
        Element body = doc.body();
        List<Node> childNodes = body.childNodes();
        String str = "";
        for (Node n : childNodes) {
            String nodeName = n.nodeName();
            if (nodeName.equals("img")) {
                str += n.outerHtml();
            }
        }
    }

    public static void dealNodes(List<Node> childNodes, String str) {
        for (Node n : childNodes) {
            String nodeName = n.nodeName();
            if (nodeName.equals("p") || nodeName.equals("img")) {
            }
        }
    }

}
