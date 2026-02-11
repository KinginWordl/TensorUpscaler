package com.tensorupscaler.core;

public class SimpleTensorMath implements TensorMath {

  
    @Override
    public int clamp255(float v) {
        return Math.min(255, Math.max(0, Math.round(v)));
    }

    
    @Override
    public int clampIndex(int i, int maxExclusive) {
        return Math.max(0, Math.min(i, maxExclusive - 1));
    }
}