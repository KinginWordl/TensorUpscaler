package com.tensorupscaler.core;


public final class SimpleTensorMath implements TensorMath {

  @Override
  public int clamp255(float v) {
    if (v <= 0f) return 0;
    if (v >= 255f) return 255;
    return (int) (v + 0.5f);
  }

  @Override
  public int clampIndex(int i, int maxExclusive) {
    if (maxExclusive <= 0) throw new IllegalArgumentException(
      "maxExclusive must be > 0"
    );
    if (i < 0) return 0;
    if (i >= maxExclusive) return maxExclusive - 1;
    return i;
  }
}
