package com.tensorupscaler.batch;

import java.nio.file.Path;

/**
 * Represents a single image transformation request.
 * This is the contract the future "recolectar imágenes" module will produce.
 */
public record ImageTask(
        Path inputPath,
        Path outputPath,
        int scale,
        float sharpenAmount
) {
    public ImageTask {
        if (inputPath == null) throw new IllegalArgumentException("inputPath must not be null");
        if (outputPath == null) throw new IllegalArgumentException("outputPath must not be null");
        if (scale < 1) throw new IllegalArgumentException("scale must be >= 1");
        if (sharpenAmount < 0f || sharpenAmount > 1f) {
            throw new IllegalArgumentException("sharpenAmount must be in [0..1]");
        }
    }
}
