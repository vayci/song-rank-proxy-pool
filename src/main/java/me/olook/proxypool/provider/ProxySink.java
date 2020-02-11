package me.olook.proxypool.provider;

import org.apache.http.HttpHost;

public interface ProxySink {

    void persistent(HttpHost httpHost);
}
