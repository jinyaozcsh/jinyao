package cn.swao.jinyao.crawl;

import java.io.IOException;
import java.net.*;
import java.util.*;

import javax.annotation.PostConstruct;

import org.apache.http.HttpHost;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import cn.swao.baselib.util.NetUtils;

/**
 * @author : kangwg 2017年2月15日
 *
 */
@Service
public class NetPoxyService {

    private static Logger log = LoggerFactory.getLogger(NetPoxyService.class);

    private String poxyUrl = "http://api.ip.data5u.com/dynamic/get.html?order=%s&ttl";

    private int INVALECOUNT = 0;

    @Value("${swao.jinyao.data5u.order:}")
    private String order;

    @PostConstruct
    public void init() {
        poxyUrl = String.format(poxyUrl, order);
    }

    public List<String> getIpList() {
        String data = NetUtils.httpGetString(poxyUrl, "getPoxyIp");
        List<String> ipList = new ArrayList<String>();
        try {
            String[] res = data.split("\n");
            for (String ip : res) {
                String[] parts = ip.split(",");
                if (Integer.parseInt(parts[1]) > 0) {
                    ipList.add(parts[0]);
                }
            }
        } catch (Exception e) {
            if (INVALECOUNT < 500) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e1) {
                }
                INVALECOUNT++;
                return getIpList();
            }
            log.error("获取代理失败", e);
        }
        INVALECOUNT = 0;
        return ipList;
    }

    public InetSocketAddress getInetSocketAddress() {
        List<String> ipList = getIpList();
        int index = (int) (Math.random() * (ipList.size()));
        String ipport = ipList.get(index);
        log.info("获取代理：{}", ipport);
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ipport.split(":")[0], Integer.parseInt(ipport.split(":")[1]));
        return inetSocketAddress;
    }

    public HttpHost getpoxy() {
        InetSocketAddress inetSocketAddress = getInetSocketAddress();
        HttpHost httpHost = new HttpHost(inetSocketAddress.getHostName(), inetSocketAddress.getPort());
        return httpHost;
    }

    public boolean isHttpExist(String url) throws IOException {
        URL link = new URL(url);
        Proxy proxy = new Proxy(Proxy.Type.HTTP, getInetSocketAddress());
        HttpURLConnection con = (HttpURLConnection) link.openConnection(proxy);
        return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
    }

}
