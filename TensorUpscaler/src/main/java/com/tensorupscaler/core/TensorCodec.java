package com.tensorupscaler.core;

import java.awt.image.BufferedImage;

public interface TensorCodec {
  TensorRGB decode(BufferedImage image);
  BufferedImage encode(TensorRGB tensor);
}
