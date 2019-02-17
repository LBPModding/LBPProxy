package com.github.lbpmodding.lbpproxy;

import com.github.lbpmodding.lbpproxy.handler.RequestHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class ProxyFilterSource extends HttpFiltersSourceAdapter {

    private static final Logger log = LoggerFactory.getLogger(LittleBigPlanetProxy.class);

    private Map<Pattern, RequestHandler> getHandlers;
    private Map<Pattern, RequestHandler> postHandlers;

    public ProxyFilterSource() {
        getHandlers = new HashMap<>();
        postHandlers = new HashMap<>();
    }

    public void registerGetHandler(String regex, RequestHandler handler) {
        getHandlers.put(Pattern.compile(regex), handler);
    }

    public void registerPostHandler(String regex, RequestHandler handler) {
        postHandlers.put(Pattern.compile(regex), handler);
    }

    // Always aggregate requests
    @Override
    public int getMaximumRequestBufferSizeInBytes() {
        return Integer.MAX_VALUE;
    }

    // Process requests
    @Override
    public HttpFilters filterRequest(HttpRequest request, ChannelHandlerContext context) {
        if (request.method() == HttpMethod.GET) {
            for (Map.Entry<Pattern, RequestHandler> current : getHandlers.entrySet()) {
                if (current.getKey().matcher(request.uri()).matches()) {
                    return fullRequestHandler(request, context, current.getValue());
                }
            }
        } else if (request.method() == HttpMethod.POST) {
            for (Map.Entry<Pattern, RequestHandler> current : postHandlers.entrySet()) {
                if (current.getKey().matcher(request.uri()).matches()) {
                    return fullRequestHandler(request, context, current.getValue());
                }
            }
        }
        // Unhandled request
        return null;
    }

    private HttpFilters fullRequestHandler(HttpRequest request, ChannelHandlerContext context, RequestHandler handler) {
        return new HttpFiltersAdapter(request, context) {
            @Override
            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                if (httpObject instanceof FullHttpRequest) {
                    handler.handle((FullHttpRequest) request);
                } else {
                    log.warn("Unexpected non-aggregated http request! Ignored.");
                }
                return null;
            }
        };
    }
}
