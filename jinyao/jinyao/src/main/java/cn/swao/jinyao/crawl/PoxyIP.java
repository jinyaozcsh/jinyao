package cn.swao.jinyao.crawl;

import java.util.*;

import org.apache.http.HttpHost;

import cn.swao.baselib.util.NetUtils;

/**
 * @author : kangwg 2017年2月15日
 *
 */
public class PoxyIP {

    public static List<String> getIdp() {
        String url = "http://api.ip.data5u.com/dynamic/get.html?order=" + "a8cf8a880c0a602b999e9b0fb9057213" + "&ttl";
        String data = NetUtils.httpGetString(url, "getIp");
        List<String> ipList = new ArrayList<String>();
        if (data != null) {
            String[] res = data.split("\n");
            for (String ip : res) {
                try {
                    String[] parts = ip.split(",");
                    if (Integer.parseInt(parts[1]) > 0) {
                        ipList.add(parts[0]);
                    }
                } catch (Exception e) {
                }
            }
        }
        return ipList;
    }

    public static HttpHost getpoxy() {
        List<String> ipList = getIdp();
        if (ipList == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                getpoxy();
            }
        }
        String ipport = (String) ipList.get(0);
        HttpHost httpHost = new HttpHost(ipport.split(":")[0], Integer.parseInt(ipport.split(":")[1]));
        return httpHost;
    }

}
