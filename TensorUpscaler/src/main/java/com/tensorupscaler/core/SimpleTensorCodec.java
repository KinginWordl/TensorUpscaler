package com.tensorupscaler.core;

import java.awt.image.BufferedImage;

public class SimpleTensorCodec implements TensorCodec {

    @Override
    public TensorRGB decode(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        
        float[][] r = new float[h][w];
        float[][] g = new float[h][w];
        float[][] b = new float[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int rgb = image.getRGB(x, y);

                
                r[y][x] = (rgb >> 16) & 0xFF;
                g[y][x] = (rgb >> 8) & 0xFF;
                b[y][x] = rgb & 0xFF;
            }
        }
        
       
        return new TensorRGB(w, h, r, g, b);
    }

    
    @Override
    public BufferedImage encode(TensorRGB tensor) {
        int w = tensor.width();
        int h = tensor.height();
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        
        float[][] rMat = tensor.r();
        float[][] gMat = tensor.g();
        float[][] bMat = tensor.b();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
               
                int r = clamp(rMat[y][x]);
                int g = clamp(gMat[y][x]);
                int b = clamp(bMat[y][x]);

               
                int rgb = (r << 16) | (g << 8) | b;
                img.setRGB(x, y, rgb);
            }
        }
        return img;
    }

    private int clamp(float v) {
        return Math.min(255, Math.max(0, Math.round(v)));
    }
}