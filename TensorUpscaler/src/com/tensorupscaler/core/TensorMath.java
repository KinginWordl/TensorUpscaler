package com.tensorupscaler.core;

public interface TensorMath {
  int clamp255(float v);

  int clampIndex(int i, int maxExclusive);

  default void requireKernel3x3(float[][] k) throws IllegalArgumentException {
    if (k == null || k.length != 3) throw new IllegalArgumentException(
      "Kernel must be 3x3"
    );
    for (int i = 0; i < 3; i++) {
      if (k[i] == null || k[i].length != 3) throw new IllegalArgumentException(
        "Kernel must be 3x3"
      );
    }
  }
}
