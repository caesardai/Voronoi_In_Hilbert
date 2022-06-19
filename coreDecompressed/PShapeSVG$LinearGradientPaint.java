package processing.core;

import java.awt.geom.Point2D;
import java.awt.PaintContext;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.Paint;

class LinearGradientPaint implements Paint
{
    float x1;
    float y1;
    float x2;
    float y2;
    float[] offset;
    int[] color;
    int count;
    float opacity;
    
    public LinearGradientPaint(final float x1, final float y1, final float x2, final float y2, final float[] offset, final int[] color, final int count, final float opacity) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.offset = offset;
        this.color = color;
        this.count = count;
        this.opacity = opacity;
    }
    
    public PaintContext createContext(final ColorModel colorModel, final Rectangle rectangle, final Rectangle2D rectangle2D, final AffineTransform affineTransform, final RenderingHints renderingHints) {
        final Point2D transform = affineTransform.transform(new Point2D.Float(this.x1, this.y1), null);
        final Point2D transform2 = affineTransform.transform(new Point2D.Float(this.x2, this.y2), null);
        return (PaintContext)new LinearGradientPaint.LinearGradientContext(this, (float)transform.getX(), (float)transform.getY(), (float)transform2.getX(), (float)transform2.getY());
    }
    
    public int getTransparency() {
        return 3;
    }
}