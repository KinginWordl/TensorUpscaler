package com.tensorupscaler.core;

public interface TensorOps {
  TensorRGB upscaleBilinear(TensorRGB src, int scale);
  TensorRGB convolve3x3(TensorRGB src, float[][] kernel);
  TensorRGB lerp(TensorRGB a, TensorRGB b, float amount);

  default TensorRGB process(
    TensorRGB input,
    int scale,
    float[][] kernel3x3,
    float amount
  ) {
    TensorRGB up = upscaleBilinear(input, scale);
    if (kernel3x3 == null || amount <= 0f) return up;
    TensorRGB sharp = convolve3x3(up, kernel3x3);
    return lerp(up, sharp, amount);
  }
}
