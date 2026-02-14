package com.tensorupscaler.core;

import java.awt.image.BufferedImage;

/**
 * BufferedImage <-> TensorRGB conversion.
 *
 * Convention:
 * - channels are float values in [0..255]
 * - layout is channel[y][x]
 * - alpha is ignored (RGB only)
 */
public final class SimpleTensorCodec implements TensorCodec {

    @Override
    public TensorRGB decode(BufferedImage image) {
        if (image == null) throw new IllegalArgumentException("image must not be null");

        final int w = image.getWidth();
        final int h = image.getHeight();

        final int[] argb = new int[w * h];
        image.getRGB(0, 0, w, h, argb, 0, w);

        final float[][] r = new float[h][w];
        final float[][] g = new float[h][w];
        final float[][] b = new float[h][w];

        int idx = 0;
        for (int y = 0; y < h; y++) {
            final float[] rRow = r[y];
            final float[] gRow = g[y];
            final float[] bRow = b[y];
            for (int x = 0; x < w; x++) {
                final int px = argb[idx++];
                rRow[x] = (px >>> 16) & 0xFF;
                gRow[x] = (px >>> 8) & 0xFF;
                bRow[x] = px & 0xFF;
            }
        }

        return new TensorRGB(w, h, r, g, b);
    }

    @Override
    public BufferedImage encode(TensorRGB tensor) {
        if (tensor == null) throw new IllegalArgumentException("tensor must not be null");

        final int w = tensor.width();
        final int h = tensor.height();

        final float[][] r = tensor.r();
        final float[][] g = tensor.g();
        final float[][] b = tensor.b();

        final int[] rgb = new int[w * h];
        int idx = 0;

        for (int y = 0; y < h; y++) {
            final float[] rRow = r[y];
            final float[] gRow = g[y];
            final float[] bRow = b[y];
            for (int x = 0; x < w; x++) {
                final int rr = clamp255ToInt(rRow[x]);
                final int gg = clamp255ToInt(gRow[x]);
                final int bb = clamp255ToInt(bRow[x]);
                rgb[idx++] = (rr << 16) | (gg << 8) | bb;
            }
        }

        final BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        out.setRGB(0, 0, w, h, rgb, 0, w);
        return out;
    }

    private static int clamp255ToInt(float v) {
        if (v <= 0f) return 0;
        if (v >= 255f) return 255;
        return (int) (v + 0.5f);
    }
}
