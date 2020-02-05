package me.olook.proxypool;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhaohw
 * @date 2019-10-09 15:42
 */
@Getter
@Setter
@Component
@ToString
@ConfigurationProperties(prefix = "proxy")
public class ProxyPoolProperties {

    private String key = "PROXY_POOL";

    private Integer limit = 100;

    /* VPN setting */

    private boolean enableVpn = false;

    private String vpnHost = "127.0.0.1";

    private Integer vpnPort = 10808;

    /* sync task setting */

    private ProxyPoolProperties.Sync sync = new ProxyPoolProperties.Sync();

    /* gather proxy task setting */

    private ProxyPoolProperties.Gather gather = new ProxyPoolProperties.Gather();

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Sync{

        private boolean periodSync = true;

        private boolean proxySync = true;
    }

    @Getter
    @Setter
    @ToString
    @NoArgsConstructor
    public static class Gather{

        private Integer startPage = 1;

        private Integer endPage = 10;

        private Integer interval = 80;

    }
}
