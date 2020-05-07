package me.olook.proxypool.core.checker;

import lombok.extern.slf4j.Slf4j;
import me.olook.proxypool.util.UserAgents;
import me.olook.proxypool.core.ProxyChecker;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhaohw
 */
@Slf4j
@Component
public class CommentChecker implements ProxyChecker {


    @Autowired
    private CloseableHttpClient httpClient;

    private static final String TEST_URL ="https://music.163.com/weapi/v1/resource/comments/R_SO_4_157276?limit=20&offset=0";

    @Override
    public boolean check(HttpHost httpHost) {
        Map<String,String> params = new HashMap<>(5);
        params.put("rid","R_SO_4_157276");
        params.put("limit","20");
        params.put("offset","0");
        params.put("total","false");
        params.put("csrf_token","");
        params.put("params","tigs5rM7ORUdFu2FDUN4gPpSj5bpRSVgWB4UE4mYEnW6663rc0++YVFFPasP7yaOdk/2SgfOMhvwqvcm+D89B5" +
                "ww0mhFLl46oWc+kOx1Dd+Xdae/zW07OVD8scgWK93Et+4oKle1rvg+CrX3GPnl07tsqtJBtnBl0nO+CBNCLMfo3YIN+CNptxuLQBe74ksV");
        params.put("encSecKey","257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7a41f831f6394d5a3fd2e38" +
                "81736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffca5eca9a0dba6d6f7249b06f5965ec" +
                "fff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c");

        RequestConfig.Builder builder = RequestConfig.custom();
        builder.setConnectTimeout(3000);
        builder.setConnectionRequestTimeout(3000);
        builder.setSocketTimeout(3000);
        if(httpHost!=null){
            builder.setProxy(httpHost);
        }
        RequestConfig proxyConfig = builder.build();
        HttpPost request = new HttpPost(TEST_URL);
        request.setConfig(proxyConfig);
        addNetEaseHeader(request);
        request.setEntity(new UrlEncodedFormEntity(addPostParam(params), Charsets.UTF_8));
        HttpResponse response = null;
        try {
            response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            if(jsonResponse.contains("\"code\":200")){
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

    private static List<BasicNameValuePair> addPostParam(Map<String,String> params){
        List<BasicNameValuePair> pairList = new ArrayList<BasicNameValuePair>();
        params.forEach((key, value) ->{
            pairList.add(new BasicNameValuePair(key, value));
        });
        return pairList;
    }
}
