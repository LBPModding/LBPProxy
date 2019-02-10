package com.github.lbpmodding.lbpproxy.handler;

import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestHandler {

    void handle(FullHttpRequest request);
}
