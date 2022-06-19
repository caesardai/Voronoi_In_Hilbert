package processing.core;

import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.Paint;

class RadialGradientPaint implements Paint
{
    float cx;
    float cy;
    float radius;
    float[] offset;
    int[] color;
    int count;
    float opacity;
    
    public RadialGradientPaint(final float cx, final float cy, final float radius, final float[] offset, final int[] color, final int count, final float opacity) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.offset = offset;
        this.color = color;
        this.count = count;
        this.opacity = opacity;
    }
    
    public PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints) {
        return (PaintContext)new RadialGradientPaint.RadialGradientContext(this);
    }
    
    public int getTransparency() {
        return 3;
    }
}