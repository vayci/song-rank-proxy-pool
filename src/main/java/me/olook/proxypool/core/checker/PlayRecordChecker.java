package me.olook.proxypool.core.checker;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.core.ProxyChecker;
import me.olook.proxypool.util.UserAgents;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 通过听歌排行榜校验代理
 * @author zhaohw
 */
@Slf4j
@Component
public class PlayRecordChecker implements ProxyChecker {

    @Autowired
    private CloseableHttpClient httpClient;

    private static final String TEST_URL = "https://music.163.com/weapi/v1/play/record?" +
            "params=OMUybrnN%2B6ELSzpZkRWe231b2b9yUKz1R40sylwNkSRXly6B1gWZm95kQ2iZuB81JnOvyLbKUqII%0D%0AjZDk" +
            "Ur4xoaKu6XQLH5W7ofChQtSucSexc13PZZvrI60tuw6aIjnCmZkyt9VFfS0uCZ8dpiB11CjQ%0D%0AiHgMNitSrMl51NOJ9" +
            "4fw2UlQMcDXLZTsXj9fpYWL&encSecKey=257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7" +
            "a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffc" +
            "a5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";

    @Override
    public boolean check(HttpHost httpHost) {
        HttpPost request = new HttpPost(TEST_URL);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost)
                .setCookieSpec(CookieSpecs.STANDARD)
                .setConnectTimeout(3000).setSocketTimeout(3000).setConnectionRequestTimeout(3000)
                .build();
        request.setConfig(requestConfig);
        addNetEaseHeader(request);
        try {
            HttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            if(jsonResponse.contains("weekData")){
                log.debug("checker pass {}",httpHost);
                return true;
            }
        } catch (IOException e) {
        }
        return false;
    }

    private static void addNetEaseHeader(HttpRequestBase request){
        request.setHeader(HttpHeaders.ACCEPT, "*/*");
        request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate,sdch");
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,en-US;q=0.7,en;q=0.3");
        request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        request.setHeader(HttpHeaders.HOST, "music.163.com");
        request.setHeader(HttpHeaders.REFERER, "http://music.163.com/");
        request.setHeader(HttpHeaders.USER_AGENT, UserAgents.randomUserAgent());
    }
}
