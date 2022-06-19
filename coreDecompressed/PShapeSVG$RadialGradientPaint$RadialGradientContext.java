package processing.core;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.PaintContext;

public class RadialGradientContext implements PaintContext
{
    int ACCURACY;
    
    public RadialGradientContext() {
        this.ACCURACY = 5;
    }
    
    public void dispose() {
    }
    
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }
    
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        final WritableRaster compatibleWritableRaster = this.getColorModel().createCompatibleWritableRaster(n3, n4);
        final int n5 = (int)RadialGradientPaint.this.radius * this.ACCURACY;
        final int[][] array = new int[n5][4];
        int n6 = 0;
        for (int i = 1; i < RadialGradientPaint.this.count; ++i) {
            final int n7 = RadialGradientPaint.this.color[i - 1];
            final int n8 = RadialGradientPaint.this.color[i];
            final int n9 = (int)(RadialGradientPaint.this.offset[i] * (n5 - 1));
            for (int j = n6; j <= n9; ++j) {
                final float norm = PApplet.norm((float)j, (float)n6, (float)n9);
                array[j][0] = (int)PApplet.lerp((float)(n7 >> 16 & 0xFF), (float)(n8 >> 16 & 0xFF), norm);
                array[j][1] = (int)PApplet.lerp((float)(n7 >> 8 & 0xFF), (float)(n8 >> 8 & 0xFF), norm);
                array[j][2] = (int)PApplet.lerp((float)(n7 & 0xFF), (float)(n8 & 0xFF), norm);
                array[j][3] = (int)(PApplet.lerp((float)(n7 >> 24 & 0xFF), (float)(n8 >> 24 & 0xFF), norm) * RadialGradientPaint.this.opacity);
            }
            n6 = n9;
        }
        final int[] iArray = new int[n3 * n4 * 4];
        int n10 = 0;
        for (int k = 0; k < n4; ++k) {
            for (int l = 0; l < n3; ++l) {
                final int min = PApplet.min((int)(PApplet.dist(RadialGradientPaint.this.cx, RadialGradientPaint.this.cy, (float)(n + l), (float)(n2 + k)) * this.ACCURACY), array.length - 1);
                iArray[n10++] = array[min][0];
                iArray[n10++] = array[min][1];
                iArray[n10++] = array[min][2];
                iArray[n10++] = array[min][3];
            }
        }
        compatibleWritableRaster.setPixels(0, 0, n3, n4, iArray);
        return compatibleWritableRaster;
    }
}