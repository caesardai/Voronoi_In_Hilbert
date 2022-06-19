package processing.core;

import java.awt.image.WritableRaster;
import java.awt.image.Raster;
import java.awt.image.ColorModel;
import java.awt.PaintContext;

public class LinearGradientContext implements PaintContext
{
    int ACCURACY;
    float tx1;
    float ty1;
    float tx2;
    float ty2;
    
    public LinearGradientContext(final float tx1, final float ty1, final float tx2, final float ty2) {
        this.ACCURACY = 2;
        this.tx1 = tx1;
        this.ty1 = ty1;
        this.tx2 = tx2;
        this.ty2 = ty2;
    }
    
    public void dispose() {
    }
    
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }
    
    public Raster getRaster(final int n, final int n2, final int n3, final int n4) {
        final WritableRaster compatibleWritableRaster = this.getColorModel().createCompatibleWritableRaster(n3, n4);
        final int[] iArray = new int[n3 * n4 * 4];
        float n5 = this.tx2 - this.tx1;
        float n6 = this.ty2 - this.ty1;
        final float n7 = (float)Math.sqrt(n5 * n5 + n6 * n6);
        if (n7 != 0.0f) {
            n5 /= n7;
            n6 /= n7;
        }
        final int n8 = (int)PApplet.dist(this.tx1, this.ty1, this.tx2, this.ty2) * this.ACCURACY;
        if (n8 <= 0) {
            int n9 = 0;
            for (int i = 0; i < n4; ++i) {
                for (int j = 0; j < n3; ++j) {
                    iArray[n9++] = 0;
                    iArray[n9++] = 0;
                    iArray[n9++] = 0;
                    iArray[n9++] = 255;
                }
            }
        }
        else {
            final int[][] array = new int[n8][4];
            int n10 = 0;
            for (int k = 1; k < LinearGradientPaint.this.count; ++k) {
                final int n11 = LinearGradientPaint.this.color[k - 1];
                final int n12 = LinearGradientPaint.this.color[k];
                final int n13 = (int)(LinearGradientPaint.this.offset[k] * (n8 - 1));
                for (int l = n10; l <= n13; ++l) {
                    final float norm = PApplet.norm((float)l, (float)n10, (float)n13);
                    array[l][0] = (int)PApplet.lerp((float)(n11 >> 16 & 0xFF), (float)(n12 >> 16 & 0xFF), norm);
                    array[l][1] = (int)PApplet.lerp((float)(n11 >> 8 & 0xFF), (float)(n12 >> 8 & 0xFF), norm);
                    array[l][2] = (int)PApplet.lerp((float)(n11 & 0xFF), (float)(n12 & 0xFF), norm);
                    array[l][3] = (int)(PApplet.lerp((float)(n11 >> 24 & 0xFF), (float)(n12 >> 24 & 0xFF), norm) * LinearGradientPaint.this.opacity);
                }
                n10 = n13;
            }
            int n14 = 0;
            for (int n15 = 0; n15 < n4; ++n15) {
                for (int n16 = 0; n16 < n3; ++n16) {
                    int n17 = (int)(((n + n16 - this.tx1) * n5 + (n2 + n15 - this.ty1) * n6) * this.ACCURACY);
                    if (n17 < 0) {
                        n17 = 0;
                    }
                    if (n17 > array.length - 1) {
                        n17 = array.length - 1;
                    }
                    iArray[n14++] = array[n17][0];
                    iArray[n14++] = array[n17][1];
                    iArray[n14++] = array[n17][2];
                    iArray[n14++] = array[n17][3];
                }
            }
        }
        compatibleWritableRaster.setPixels(0, 0, n3, n4, iArray);
        return compatibleWritableRaster;
    }
}