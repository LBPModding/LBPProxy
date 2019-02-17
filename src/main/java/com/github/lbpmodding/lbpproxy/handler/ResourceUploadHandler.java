package com.github.lbpmodding.lbpproxy.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lbpmodding.lbpproxy.LittleBigPlanetProxy;
import com.github.lbpmodding.lbpproxy.data.DecompressedResource;
import com.github.lbpmodding.lbpproxy.service.ResourceCompressionService;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ResourceUploadHandler implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(LittleBigPlanetProxy.class);

    private ObjectMapper objectMapper = new ObjectMapper();
    private ResourceCompressionService compressionService = new ResourceCompressionService();

    @Override
    public void handle(FullHttpRequest request) {
        log.info("Upload detected: " + request.uri());
        String resourceId = request.uri().split("/upload/")[1];
        ByteBuf content = request.content();
        DecompressedResource resource;
        try {
            resource = compressionService.detectAndDecompress(resourceId, content);
        } catch (IOException e) {
            log.error("Unable to decompress resource data!", e);
            return;
        }
        if (resource == null) {
            log.warn("Unknown resource type!");
            return;
        }
        switch (resource.getType()) {
            case LEVEL:
                log.info("Level resource");
                break;
            case PLAN:
                log.info("Plan resource");
                handlePlan(resource);
                break;
            case TEXTURE:
                log.info("Texture resource");
                handleTexture(resource);
                break;
            case PHOTO:
                log.info("Photo resource");
                handleImage(resource);
                break;
        }
    }

    private void handlePlan(DecompressedResource resource) {
        Path plansFolder = Paths.get("./output/resources/plan/");
        try {
            Files.createDirectories(plansFolder);
            Path outputFile = plansFolder.resolve(resource.getId() + ".pln");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, resource.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Path descriptorFolder = Paths.get("./output/resources/descriptor/");
        try {
            Files.createDirectories(descriptorFolder);
            Path outputFile = descriptorFolder.resolve(resource.getId() + ".json");
            Files.deleteIfExists(outputFile);
            objectMapper.writeValue(outputFile.toFile(), resource.getDescriptor());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleTexture(DecompressedResource resource) {
        Path thumbnailsFolder = Paths.get("./output/resources/tex/");
        try {
            Files.createDirectories(thumbnailsFolder);
            Path outputFile = thumbnailsFolder.resolve(resource.getId() + ".dds");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, resource.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleImage(DecompressedResource resource) {
        Path imageFolder = Paths.get("./output/resources/img/");
        try {
            Files.createDirectories(imageFolder);
            Path outputFile = imageFolder.resolve(resource.getId() + ".jpeg");
            Files.deleteIfExists(outputFile);
            Files.write(outputFile, resource.getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
