package com.tensorupscaler.core;


public final class SimpleTensorOps implements TensorOps {

  @Override
  public TensorRGB upscaleBilinear(TensorRGB src, int scale)
    throws IllegalArgumentException {
    if (src == null) throw new IllegalArgumentException("src must not be null");
    if (scale < 1) throw new IllegalArgumentException("scale must be >= 1");
    if (scale == 1) return src;

    final int w = src.width();
    final int h = src.height();
    final int ow = w * scale;
    final int oh = h * scale;

    final float[][] or = new float[oh][ow];
    final float[][] og = new float[oh][ow];
    final float[][] ob = new float[oh][ow];

    final float[][] r = src.r();
    final float[][] g = src.g();
    final float[][] b = src.b();

    for (int y2 = 0; y2 < oh; y2++) {
      final float fy = y2 / (float) scale;
      final int y0 = (int) Math.floor(fy);
      final int y1 = Math.min(y0 + 1, h - 1);
      final float dy = fy - y0;

      for (int x2 = 0; x2 < ow; x2++) {
        final float fx = x2 / (float) scale;
        final int x0 = (int) Math.floor(fx);
        final int x1 = Math.min(x0 + 1, w - 1);
        final float dx = fx - x0;

        final float w11 = (1f - dx) * (1f - dy);
        final float w12 = dx * (1f - dy);
        final float w21 = (1f - dx) * dy;
        final float w22 = dx * dy;

        float vr =
          w11 * r[y0][x0] + w12 * r[y0][x1] + w21 * r[y1][x0] + w22 * r[y1][x1];
        float vg =
          w11 * g[y0][x0] + w12 * g[y0][x1] + w21 * g[y1][x0] + w22 * g[y1][x1];
        float vb =
          w11 * b[y0][x0] + w12 * b[y0][x1] + w21 * b[y1][x0] + w22 * b[y1][x1];

        or[y2][x2] = clamp255(vr);
        og[y2][x2] = clamp255(vg);
        ob[y2][x2] = clamp255(vb);
      }
    }

    return new TensorRGB(ow, oh, or, og, ob);
  }

  @Override
  public TensorRGB convolve3x3(TensorRGB src, float[][] kernel)
    throws IllegalArgumentException {
    if (src == null) throw new IllegalArgumentException("src must not be null");
    requireKernel3x3(kernel);

    final int w = src.width();
    final int h = src.height();

    final float[][] outR = new float[h][w];
    final float[][] outG = new float[h][w];
    final float[][] outB = new float[h][w];

    final float[][] r = src.r();
    final float[][] g = src.g();
    final float[][] b = src.b();

    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        float ar = 0f,
          ag = 0f,
          ab = 0f;

        for (int ky = 0; ky < 3; ky++) {
          final int sy = clampIndex(y + ky - 1, h);
          final float[] rRow = r[sy];
          final float[] gRow = g[sy];
          final float[] bRow = b[sy];

          final float[] kRow = kernel[ky];

          for (int kx = 0; kx < 3; kx++) {
            final int sx = clampIndex(x + kx - 1, w);
            final float kv = kRow[kx];

            ar += kv * rRow[sx];
            ag += kv * gRow[sx];
            ab += kv * bRow[sx];
          }
        }

        outR[y][x] = clamp255(ar);
        outG[y][x] = clamp255(ag);
        outB[y][x] = clamp255(ab);
      }
    }

    return new TensorRGB(w, h, outR, outG, outB);
  }

  @Override
  public TensorRGB lerp(TensorRGB a, TensorRGB b, float amount)
    throws IllegalArgumentException {
    if (a == null) throw new IllegalArgumentException("a must not be null");
    if (b == null) throw new IllegalArgumentException("b must not be null");
    if (a.width() != b.width() || a.height() != b.height()) {
      throw new IllegalArgumentException("Tensor dimensions must match");
    }
    if (amount < 0f || amount > 1f) throw new IllegalArgumentException(
      "amount must be in [0..1]"
    );

    final int w = a.width();
    final int h = a.height();

    final float[][] outR = new float[h][w];
    final float[][] outG = new float[h][w];
    final float[][] outB = new float[h][w];

    final float inv = 1f - amount;

    final float[][] ar = a.r();
    final float[][] ag = a.g();
    final float[][] ab = a.b();

    final float[][] br = b.r();
    final float[][] bg = b.g();
    final float[][] bb = b.b();

    for (int y = 0; y < h; y++) {
      final float[] arRow = ar[y];
      final float[] agRow = ag[y];
      final float[] abRow = ab[y];

      final float[] brRow = br[y];
      final float[] bgRow = bg[y];
      final float[] bbRow = bb[y];

      final float[] orRow = outR[y];
      final float[] ogRow = outG[y];
      final float[] obRow = outB[y];

      for (int x = 0; x < w; x++) {
        orRow[x] = clamp255(inv * arRow[x] + amount * brRow[x]);
        ogRow[x] = clamp255(inv * agRow[x] + amount * bgRow[x]);
        obRow[x] = clamp255(inv * abRow[x] + amount * bbRow[x]);
      }
    }

    return new TensorRGB(w, h, outR, outG, outB);
  }

  private static float clamp255(float v) {
    if (v <= 0f) return 0f;
    if (v >= 255f) return 255f;
    return v;
  }

  private static int clampIndex(int i, int maxExclusive) {
    if (i < 0) return 0;
    if (i >= maxExclusive) return maxExclusive - 1;
    return i;
  }

  private static void requireKernel3x3(float[][] k)
    throws IllegalArgumentException {
    if (k == null || k.length != 3) throw new IllegalArgumentException(
      "Kernel must be 3x3"
    );
    for (int i = 0; i < 3; i++) {
      if (k[i] == null || k[i].length != 3) throw new IllegalArgumentException(
        "Kernel must be 3x3"
      );
    }
  }

  /*
   *
   *
   * TensorOps ops = new SimpleTensorOps();
   * float[][] kernel = new float[][]{
   *   {0f,-1f,0f},
   *   {-1f,5f,-1f},
   *   {0f,-1f,0f}
   * };
   * TensorRGB out = ops.process(inputTensor, 2, kernel, 0.6f);
   */
}
