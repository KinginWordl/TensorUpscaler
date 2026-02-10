package com.tensorupscaler.core;

import java.awt.image.BufferedImage;

public interface ImageProcessor {
  BufferedImage process(
    BufferedImage input,
    int scale,
    float sharpenAmount,
    float[][] kernel3x3
  );
}
