package com.tensorupscaler.batch;

import java.io.IOException;
import java.util.List;

/**
 * Batch transformation contract. The "recolectar imágenes" module can build a list of tasks,
 * then call an implementation of this interface.
 */
public interface BatchProcessor {
    void processAll(List<ImageTask> tasks) throws IOException;
}
