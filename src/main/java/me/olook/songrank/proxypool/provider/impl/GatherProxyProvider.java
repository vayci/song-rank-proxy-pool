package me.olook.songrank.proxypool.provider.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.olook.songrank.proxypool.netease.NetEaseApiUrl;
import me.olook.songrank.proxypool.netease.NetEaseEncryptUtil;
import me.olook.songrank.proxypool.netease.UserAgents;
import me.olook.songrank.proxypool.provider.BaseVpnProxyProvider;
import org.apache.commons.codec.Charsets;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author zhaohw
 * @date 2019-05-06 16:02
 */
@Slf4j
public class GatherProxyProvider extends BaseVpnProxyProvider {

    private static final String TEST_URL = "https://music.163.com/weapi/v1/play/record?" +
            "params=OMUybrnN%2B6ELSzpZkRWe231b2b9yUKz1R40sylwNkSRXly6B1gWZm95kQ2iZuB81JnOvyLbKUqII%0D%0AjZDk" +
            "Ur4xoaKu6XQLH5W7ofChQtSucSexc13PZZvrI60tuw6aIjnCmZkyt9VFfS0uCZ8dpiB11CjQ%0D%0AiHgMNitSrMl51NOJ9" +
            "4fw2UlQMcDXLZTsXj9fpYWL&encSecKey=257348aecb5e556c066de214e531faadd1c55d814f9be95fd06d6bff9f4c7" +
            "a41f831f6394d5a3fd2e3881736d94a02ca919d952872e7d0a50ebfa1769a7a62d512f5f1ca21aec60bc3819a9c3ffc" +
            "a5eca9a0dba6d6f7249b06f5965ecfff3695b54e1c28f3f624750ed39e7de08fc8493242e26dbc4484a01c76f739e135637c";

    public String requestForPayload(Integer index) {
        String url = "http://www.gatherproxy.com/proxylist/anonymity/?t=Elite";
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
            log.error("数据请求出错");
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

    public void checkAndSaveProxy(HttpHost httpHost){
        HttpPost request = new HttpPost(TEST_URL);
        RequestConfig requestConfig = RequestConfig.custom().setProxy(httpHost)
                .setConnectTimeout(3000).setSocketTimeout(3000).setConnectionRequestTimeout(3000)
                .build();
        request.setConfig(requestConfig);
        addNetEaseHeader(request);
        try {
            HttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity(), Charsets.UTF_8);
            if(jsonResponse.contains("weekData")){
               log.info("accept proxy {}",httpHost);
            }
        } catch (IOException e) {
//            log.info("discard proxy {}",httpHost);
        }
    }

    private static void addNetEaseHeader(HttpRequestBase request){
        request.setHeader(HttpHeaders.ACCEPT, "*/*");
        request.setHeader(HttpHeaders.ACCEPT_ENCODING, "gzip,deflate,sdch");
        request.setHeader(HttpHeaders.ACCEPT_LANGUAGE, "zh-CN,en-US;q=0.7,en;q=0.3");
        request.setHeader(HttpHeaders.CONNECTION, "keep-alive");
        request.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded; charset=UTF-8");
        request.setHeader(HttpHeaders.HOST, "music.163.com");
        request.setHeader(HttpHeaders.REFERER, "http://music.163.com/");
        request.setHeader(HttpHeaders.USER_AGENT,UserAgents.randomUserAgent());
    }

    public static void main(String[] args) {
        Map<String,String> map = new HashMap<>();
        map.put("type","1");
        map.put("limit","1000");
        map.put("offset","0");
        map.put("total","true");
        map.put("csrf_token","");
        map.put("uid","35063071");
        String json = JSONObject.toJSONString(map);
        String urlParams = NetEaseEncryptUtil.getUrlParams(json);
        String url = NetEaseApiUrl.RECORD+urlParams;
        System.out.println(url);
    }

}
