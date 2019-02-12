package com.github.lbpmodding.lbpproxy.handler;

import com.github.lbpmodding.lbpproxy.CustomCompressionUtilities;
import com.github.lbpmodding.lbpproxy.data.ResourceType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.InflaterInputStream;

@Slf4j
public class ResourceUploadHandler implements RequestHandler {

    @Override
    public void handle(FullHttpRequest request) {
        log.info("Upload detected: " + request.uri());
        String resourceId = request.uri().split("/upload/")[1];
        ByteBuf content = request.content();
        String magic = content.toString(0, 4, CharsetUtil.UTF_8);
        ResourceType type = ResourceType.fromHeader(magic);
        if (type == null) {
            log.warn("UNKNOWN resource type with magic '" + magic + "'!");
            return;
        }
        ByteBuf dataSource = content.skipBytes(4); // Skip magic
        switch (type) {
            case LEVEL:
                log.info("Level resource!");
                break;
            case PLAN:
                log.info("Plan resource!");
                handlePlan(resourceId, dataSource);
                break;
            case TEX:
                log.info("Tex resource!");
                handleTex(resourceId, dataSource);
                break;
        }
    }

    // Only supports sequencer data atm
    private void handlePlan(String resourceId, ByteBuf source) {
        try (ByteArrayInputStream byteInput = new ByteArrayInputStream(ByteBufUtil.getBytes(source))) {
            byteInput.skip(26); // Compressed data offset
            try (InflaterInputStream inflaterInput = new InflaterInputStream(byteInput)) {
                byte[] inflatedData = inflaterInput.readAllBytes();
                log.info("Inflated data: " + new String(inflatedData));
            }
        } catch (IOException e) {
            log.info("Not valid sequencer data, ignoring");
        }
    }

    private void handleTex(String resourceId, ByteBuf source) {
        byte[] decompressedData;
        try {
            decompressedData = CustomCompressionUtilities.decompressMinimal(source);
        } catch (IOException e) {
            log.error("Unable to decompress TEX data!", e);
            return;
        }
        Path thumbnailsFolder = Paths.get("./output/resources/img/");
        try {
            Files.createDirectories(thumbnailsFolder);
            Path outputFile = thumbnailsFolder.resolve(resourceId + ".dds");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, decompressedData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
