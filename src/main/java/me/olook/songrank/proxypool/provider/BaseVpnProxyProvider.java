package me.olook.songrank.proxypool.provider;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * @author zhaohw
 * @date 2019-05-06 16:04
 */
public abstract class BaseVpnProxyProvider {

    private PoolingHttpClientConnectionManager connectionManager =new PoolingHttpClientConnectionManager();

    protected CloseableHttpClient httpClient = HttpClientBuilder.create()
            .setMaxConnTotal(200).setMaxConnPerRoute(20)
            .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(5000).build())
            .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy()).build();


    private HttpHost vpnConfig = new HttpHost("127.0.0.1",10809);

    protected RequestConfig requestConfig = RequestConfig.custom().setProxy(vpnConfig)
            .setConnectTimeout(3000).setSocketTimeout(3000).setConnectionRequestTimeout(3000)
            .build();
}
