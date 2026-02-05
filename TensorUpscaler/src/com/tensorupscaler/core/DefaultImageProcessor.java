package com.tensorupscaler.core;

import java.awt.image.BufferedImage;
import java.util.Objects;

public final class DefaultImageProcessor implements ImageProcessor {

  private final TensorCodec codec;
  private final TensorOps ops;

  public DefaultImageProcessor(TensorCodec codec, TensorOps ops) {
    this.codec = Objects.requireNonNull(codec);
    this.ops = Objects.requireNonNull(ops);
  }

  @Override
  public BufferedImage process(
    BufferedImage input,
    int scale,
    float sharpenAmount,
    float[][] kernel3x3
  ) throws IllegalArgumentException {
    Objects.requireNonNull(input, "input");
    if (scale < 1) throw new IllegalArgumentException("scale must be >= 1");
    if (
      sharpenAmount < 0f || sharpenAmount > 1f
    ) throw new IllegalArgumentException("sharpenAmount must be in [0..1]");

    TensorRGB t = codec.decode(input);
    TensorRGB out = ops.process(t, scale, kernel3x3, sharpenAmount);
    return codec.encode(out);
  }
}
