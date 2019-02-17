package com.github.lbpmodding.lbpproxy;

import com.github.lbpmodding.lbpproxy.handler.ResourceUploadHandler;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class LittleBigPlanetProxy {

    public static final String API_BASE_URL = "http://littlebigplanetps3.online.scee.com:10060/LITTLEBIGPLANETPS3_XML";

    private static final Logger log = LoggerFactory.getLogger(LittleBigPlanetProxy.class);

    private HttpProxyServer proxyServer;
    private InetSocketAddress address;
    private ProxyFilterSource filterSource;

    public LittleBigPlanetProxy(InetSocketAddress address) {
        proxyServer = null;
        this.address = address;
        this.filterSource = new ProxyFilterSource();
        // Register built-in handlers
        filterSource.registerPostHandler(API_BASE_URL + "/upload/.*", new ResourceUploadHandler());
    }

    public static void main(String[] args) {
        InetSocketAddress address;
        if (args.length == 0) {
            address = new InetSocketAddress(8080);
        } else {
            try {
                address = new InetSocketAddress(InetAddress.getByName(args[0]), args.length > 1 ? Integer.parseInt(args[1]) : 8080);
            } catch (UnknownHostException e) {
                log.error("Invalid bind address!", e);
                return;
            }
        }
        new LittleBigPlanetProxy(address).start();
    }

    public void start() {
        log.info("Starting the proxy...");
        proxyServer = DefaultHttpProxyServer.bootstrap()
                .withAddress(address)
                .withFiltersSource(filterSource)
                .start();
    }

    public void stop() {
        proxyServer.stop();
        proxyServer = null;
    }

    public boolean isRunning() {
        return proxyServer != null;
    }
}
