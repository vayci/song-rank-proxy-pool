package me.olook.proxypool.core.provider;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.core.ProxyProvider;
import me.olook.proxypool.task.DingTalkNotice;
import org.apache.commons.codec.Charsets;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhaohw
 * @date 2020-02-05 23:35
 */
@Slf4j
@Component
public class ProxyListOrgProvider implements ProxyProvider {

    @Autowired
    private RequestConfig requestConfig;

    @Autowired
    private CloseableHttpClient httpClient;

    @Autowired
    private DingTalkNotice dingTalkNotice;

    @Override
    public String requestForPayload(Integer index) {
        String url = "https://proxy-list.org/english/index.php?p="+index;
        if(index == 1){
            url = "https://proxy-list.org/english/index.php";
        }
        HttpGet request = new HttpGet(url);
        request.setConfig(requestConfig);
        try {
            HttpResponse response = httpClient.execute(request);
            return EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
        } catch (IOException e) {
            log.error("data error [{}], {}", url, e.getMessage());
            dingTalkNotice.send(e.getMessage());
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public List<HttpHost> resolveProxy(String payload) {
        List<HttpHost> result = new ArrayList<HttpHost>();
        String reg = "Proxy\\('(.*?)'\\)";
        Matcher m = Pattern.compile(reg).matcher(payload);
        while (m.find()) {
            String r = m.group(1);
            String s = new String(Base64.decodeBase64(r));
            String[] split = s.split(":");
            HttpHost httpHost = new HttpHost(split[0], Integer.parseInt(split[1]));
            result.add(httpHost);
        }
        return result;
    }

}
