package com.tensorupscaler.core;

// import java.awt.image.BufferedImage;

public record TensorRGB(
  int width,
  int height,
  float[][] r,
  float[][] g,
  float[][] b
) {
  public TensorRGB {
    if (width <= 0 || height <= 0) throw new IllegalArgumentException(
      "Invalid dimensions"
    );
    requireShape(r, width, height, "r");
    requireShape(g, width, height, "g");
    requireShape(b, width, height, "b");
  }

  private static void requireShape(float[][] c, int w, int h, String name)
    throws IllegalArgumentException {
    if (c == null || c.length != h) throw new IllegalArgumentException(
      "Channel " + name + " has invalid height"
    );
    for (int y = 0; y < h; y++) {
      if (c[y] == null || c[y].length != w) {
        throw new IllegalArgumentException(
          "Channel " + name + " has invalid width at row " + y
        );
      }
    }
  }
}
