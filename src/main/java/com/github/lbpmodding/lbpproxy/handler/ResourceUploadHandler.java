package com.github.lbpmodding.lbpproxy.handler;

import com.github.lbpmodding.lbpproxy.data.ResourceType;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.InflaterInputStream;

@Slf4j
public class ResourceUploadHandler implements RequestHandler {

    @Override
    public void handle(FullHttpRequest request) {
        log.info("Upload detected: " + request.uri());
        ByteBuf content = request.content();
        String header = content.toString(0, 3, CharsetUtil.UTF_8);
        ResourceType type = ResourceType.fromHeader(header);
        if (type == null) {
            log.warn("UNKNOWN resource type " + header + "!");
            return;
        }
        ByteBuf data = content.skipBytes(3);
        switch (type) {
            case LEVEL:
                log.info("Level resource!");
                break;
            case PLAN:
                log.info("Plan resource!");
                handlePlan(data);
                break;
            case TEX:
                log.info("Tex resource!");
                break;
        }
    }

    // Only supports sequencer data atm
    private void handlePlan(ByteBuf data) {
        try (ByteArrayInputStream byteInput = new ByteArrayInputStream(data.array())) {
            byteInput.skip(26); // Compressed data offset
            try (InflaterInputStream inflaterInput = new InflaterInputStream(byteInput)) {
                byte[] inflatedData = inflaterInput.readAllBytes();
                log.info("Inflated data: " + new String(inflatedData));
            }
        } catch (IOException e) {
            log.info("Not valid sequencer data, ignoring");
        }
    }
}
