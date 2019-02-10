package com.github.lbpmodding.lbpproxy;

import com.github.lbpmodding.lbpproxy.handler.ResourceUploadHandler;
import lombok.extern.slf4j.Slf4j;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@Slf4j
public class LittleBigPlanetProxy {
    private HttpProxyServer proxyServer;
    private InetSocketAddress address;
    private ProxyFilterSource filterSource;

    public LittleBigPlanetProxy(InetSocketAddress address) {
        proxyServer = null;
        this.address = address;
        this.filterSource = new ProxyFilterSource();
        // Register built-in handlers
        filterSource.registerPostHandler(".*/upload/.*", new ResourceUploadHandler());
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
