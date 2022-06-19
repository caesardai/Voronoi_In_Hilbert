package processing.core;

import java.awt.image.WritableRaster;
import java.awt.image.BufferedImage;

class ImageCache
{
    PImage source;
    boolean tinted;
    int tintedColor;
    int[] tintedPixels;
    BufferedImage image;
    
    public ImageCache(final PImage source) {
        this.source = source;
    }
    
    public void delete() {
    }
    
    public void update(final boolean tinted, final int tintedColor) {
        int imageType = 2;
        final boolean b = (tintedColor & 0xFF000000) == 0xFF000000;
        if (this.source.format == 1 && (!tinted || (tinted && b))) {
            imageType = 1;
        }
        final boolean b2 = this.image != null && this.image.getType() != imageType;
        if (this.image == null || b2) {
            this.image = new BufferedImage(this.source.width, this.source.height, imageType);
        }
        final WritableRaster raster = this.image.getRaster();
        if (tinted) {
            if (this.tintedPixels == null || this.tintedPixels.length != this.source.width) {
                this.tintedPixels = new int[this.source.width];
            }
            final int n = tintedColor >> 24 & 0xFF;
            final int n2 = tintedColor >> 16 & 0xFF;
            final int n3 = tintedColor >> 8 & 0xFF;
            final int n4 = tintedColor & 0xFF;
            if (imageType == 1) {
                int n5 = 0;
                for (int i = 0; i < this.source.height; ++i) {
                    for (int j = 0; j < this.source.width; ++j) {
                        final int n6 = this.source.pixels[n5++];
                        this.tintedPixels[j] = ((n2 * (n6 >> 16 & 0xFF) & 0xFF00) << 8 | (n3 * (n6 >> 8 & 0xFF) & 0xFF00) | (n4 * (n6 & 0xFF) & 0xFF00) >> 8);
                    }
                    raster.setDataElements(0, i, this.source.width, 1, this.tintedPixels);
                }
            }
            else if (imageType == 2) {
                int n7 = 0;
                for (int k = 0; k < this.source.height; ++k) {
                    if (this.source.format == 1) {
                        final int n8 = tintedColor & 0xFF000000;
                        for (int l = 0; l < this.source.width; ++l) {
                            final int n9 = this.source.pixels[n7++];
                            this.tintedPixels[l] = (n8 | (n2 * (n9 >> 16 & 0xFF) & 0xFF00) << 8 | (n3 * (n9 >> 8 & 0xFF) & 0xFF00) | (n4 * (n9 & 0xFF) & 0xFF00) >> 8);
                        }
                    }
                    else if (this.source.format == 2) {
                        for (int n10 = 0; n10 < this.source.width; ++n10) {
                            final int n11 = this.source.pixels[n7++];
                            this.tintedPixels[n10] = ((n * (n11 >> 24 & 0xFF) & 0xFF00) << 16 | (n2 * (n11 >> 16 & 0xFF) & 0xFF00) << 8 | (n3 * (n11 >> 8 & 0xFF) & 0xFF00) | (n4 * (n11 & 0xFF) & 0xFF00) >> 8);
                        }
                    }
                    else if (this.source.format == 4) {
                        final int n12 = tintedColor & 0xFFFFFF;
                        for (int n13 = 0; n13 < this.source.width; ++n13) {
                            this.tintedPixels[n13] = ((n * this.source.pixels[n7++] & 0xFF00) << 16 | n12);
                        }
                    }
                    raster.setDataElements(0, k, this.source.width, 1, this.tintedPixels);
                }
            }
        }
        else {
            raster.setDataElements(0, 0, this.source.width, this.source.height, this.source.pixels);
        }
        this.tinted = tinted;
        this.tintedColor = tintedColor;
    }
}