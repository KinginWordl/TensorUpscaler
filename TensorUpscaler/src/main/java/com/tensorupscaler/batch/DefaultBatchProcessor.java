package com.tensorupscaler.batch;

import com.tensorupscaler.core.ImageProcessor;
import com.tensorupscaler.core.Kernels;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import javax.imageio.ImageIO;

/**
 * Simple default batch processor using ImageIO and the core ImageProcessor.
 * The "collector" module can be added later to create tasks from folders/drag-drop/etc.
 */
public final class DefaultBatchProcessor implements BatchProcessor {

    private final ImageProcessor processor;

    public DefaultBatchProcessor(ImageProcessor processor) {
        this.processor = Objects.requireNonNull(processor);
    }

    @Override
    public void processAll(List<ImageTask> tasks) throws IOException {
        Objects.requireNonNull(tasks, "tasks");
        for (ImageTask task : tasks) {
            processOne(task);
        }
    }

    private void processOne(ImageTask task) throws IOException {
        final Path in = task.inputPath();
        final Path out = task.outputPath();

        final BufferedImage input = ImageIO.read(in.toFile());
        if (input == null) throw new IOException("Unsupported image or unreadable: " + in);

        final BufferedImage result =
                processor.process(input, task.scale(), task.sharpenAmount(), Kernels.sharpen());

        final String format = guessFormat(out);
        ImageIO.write(result, format, out.toFile());
    }

    private static String guessFormat(Path out) {
        final String name = out.getFileName().toString().toLowerCase();
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) return "jpg";
        if (name.endsWith(".bmp")) return "bmp";
        if (name.endsWith(".tif") || name.endsWith(".tiff")) return "tiff";
        return "png";
    }
}
