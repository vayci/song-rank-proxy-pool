package me.olook.proxypool.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaohw
 * @date 2019-05-06 16:04
 */
@Slf4j
@Setter
@Configuration
public class RequestConfiguration {

    private final ProxyPoolProperties properties;

    public RequestConfiguration(ProxyPoolProperties properties) {
        this.properties = properties;
    }

    @Bean
    public PoolingHttpClientConnectionManager clientConnectionManager(){
        return new PoolingHttpClientConnectionManager();
    }

    @Bean
    public CloseableHttpClient closeableHttpClient(){
        return HttpClientBuilder.create()
                .setMaxConnTotal(100).setMaxConnPerRoute(10)
                .setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(3000).build())
                .setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy())
                .setConnectionManager(clientConnectionManager()).build();
    }

    @Bean
    public RequestConfig requestConfig(ProxyPoolProperties properties){
        log.info("load config properties {}",properties);
        HttpHost httpHost = null;;
        if (properties != null && properties.isEnableVpn()) {
            httpHost = new HttpHost(properties.getVpnHost(),properties.getVpnPort());
        }
        return RequestConfig.custom().setProxy(httpHost)
                .setConnectTimeout(3000).setSocketTimeout(3000).setConnectionRequestTimeout(3000)
                .build();
    }

}
