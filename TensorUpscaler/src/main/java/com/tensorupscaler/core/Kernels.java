package com.tensorupscaler.core;

public final class Kernels {

  private Kernels() {}

  public static float[][] sharpen() {
    return new float[][] { { 0f, -1f, 0f }, { -1f, 5f, -1f }, { 0f, -1f, 0f } };
  }
}
