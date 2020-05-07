package me.olook.proxypool.core.provider;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.config.ProxyPoolProperties;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhaohw
 * @date 2019-05-06 16:02
 */
@Slf4j
@Component
public class GatherProxyProvider {

    @Autowired
    private ProxyPoolProperties properties;

    @Autowired
    private RequestConfig requestConfig;

    @Autowired
    private CloseableHttpClient httpClient;

    public String requestForPayload(Integer index) {
        String url = "http://www.proxygather.com/proxylist/anonymity/?t=Elite";
        HttpPost request = new HttpPost(url);
        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
        pairList.add(new BasicNameValuePair("Uptime", "0"));
        pairList.add(new BasicNameValuePair("Type", "elite"));
        pairList.add(new BasicNameValuePair("PageIdx", index.toString()));
        request.setConfig(requestConfig);
        request.setEntity(new UrlEncodedFormEntity(pairList, Charsets.UTF_8));
        try {
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
        } catch (IOException e) {
            log.error("data acquisition error , check your network ");
            return null;
        }
    }

    public List<HttpHost> resolveProxy(String payload) {
        if(payload == null){ return Collections.emptyList(); }
        Document parse = Jsoup.parse(payload);
        Elements trs = parse.select("tr");
        List<Element> collect = trs.stream().filter(e -> {
            try{
                return "script".equals(e.childNode(3).childNode(0).nodeName());
            }catch (Exception e1){ return false; }
        }).collect(Collectors.toList());

        return collect.stream().map(e -> {
            String ip = e.child(1).data().replace("document.write('", "").replace("')", "");
            String portStr = e.child(2).data().replace("document.write(gp.dep('", "").replace("'))", "");
            Integer port = Integer.parseInt(portStr, 16);
            return new HttpHost(ip.toString(),port);
        }).collect(Collectors.toList());
    }
}
