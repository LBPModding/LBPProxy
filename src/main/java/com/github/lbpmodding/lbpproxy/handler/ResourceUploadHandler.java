package com.github.lbpmodding.lbpproxy.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lbpmodding.lbpproxy.data.DecompressedResource;
import com.github.lbpmodding.lbpproxy.data.ResourceType;
import com.github.lbpmodding.lbpproxy.service.ResourceCompressionService;
import com.github.lbpmodding.lbpproxy.utility.HexUtilities;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ResourceUploadHandler implements RequestHandler {
    private static final byte[] JPEG_HEADER = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};

    private ObjectMapper objectMapper = new ObjectMapper();
    private ResourceCompressionService compressionService = new ResourceCompressionService();

    @Override
    public void handle(FullHttpRequest request) {
        log.info("Upload detected: " + request.uri());
        String resourceId = request.uri().split("/upload/")[1];
        ByteBuf content = request.content();
        // Check the header for raw files
        byte[] header = new byte[4];
        content.getBytes(0, header);
        if (HexUtilities.checkHeader(header, JPEG_HEADER)) {
            log.info("Image resource!");
            handleImage(resourceId, content);
            return;
        }
        String magic = new String(header, StandardCharsets.UTF_8);
        ResourceType type = ResourceType.fromMagic(magic);
        if (type == null) {
            log.warn("UNKNOWN resource type with magic value '" + magic + "'!");
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

    private void handlePlan(String resourceId, ByteBuf source) {
        DecompressedResource resource;
        try {
            resource = compressionService.decompress(source);
        } catch (IOException e) {
            log.error("Unable to decompress PLN data!", e);
            return;
        }
        Path plansFolder = Paths.get("./output/resources/plan/");
        try {
            Files.createDirectories(plansFolder);
            Path outputFile = plansFolder.resolve(resourceId + ".pln");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, resource.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path descriptorFolder = Paths.get("./output/resources/descriptor/");
        try {
            Files.createDirectories(descriptorFolder);
            Path outputFile = descriptorFolder.resolve(resourceId + ".json");
            Files.deleteIfExists(outputFile);
            objectMapper.writeValue(outputFile.toFile(), resource.getDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTex(String resourceId, ByteBuf source) {
        DecompressedResource resource;
        try {
            resource = compressionService.decompressSimple(source);
        } catch (IOException e) {
            log.error("Unable to decompress TEX data!", e);
            return;
        }
        Path thumbnailsFolder = Paths.get("./output/resources/tex/");
        try {
            Files.createDirectories(thumbnailsFolder);
            Path outputFile = thumbnailsFolder.resolve(resourceId + ".dds");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, resource.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleImage(String resourceId, ByteBuf source) {
        byte[] data = ByteBufUtil.getBytes(source);
        Path imageFolder = Paths.get("./output/resources/img/");
        try {
            Files.createDirectories(imageFolder);
            Path outputFile = imageFolder.resolve(resourceId + ".jpeg");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
