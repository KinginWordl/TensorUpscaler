package com.tensorupscaler.core;

/**
 * Minimal TensorMath implementation shared across codec/ops when desired.
 */
public final class SimpleTensorMath implements TensorMath {

    @Override
    public int clamp255(float v) {
        if (v <= 0f) return 0;
        if (v >= 255f) return 255;
        return Math.round(v);
    }

    @Override
    public int clampIndex(int i, int maxExclusive) {
        if (maxExclusive <= 0) throw new IllegalArgumentException("maxExclusive must be > 0");
        if (i < 0) return 0;
        if (i >= maxExclusive) return maxExclusive - 1;
        return i;
    }
}
