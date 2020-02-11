package me.olook.proxypool.core;

import org.apache.http.HttpHost;

public interface ProxySink {

    void persistent(HttpHost httpHost);
}
