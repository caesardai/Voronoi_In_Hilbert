package processing.core;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import processing.xml.XMLElement;

class RadialGradient extends Gradient
{
    float cx;
    float cy;
    float r;
    
    public RadialGradient(final PShapeSVG pShapeSVG, final XMLElement xmlElement) {
        super(pShapeSVG, xmlElement);
        this.cx = PShapeSVG.getFloatWithUnit(xmlElement, "cx");
        this.cy = PShapeSVG.getFloatWithUnit(xmlElement, "cy");
        this.r = PShapeSVG.getFloatWithUnit(xmlElement, "r");
        final String string = xmlElement.getString("gradientTransform");
        if (string != null) {
            final float[] value = PShapeSVG.parseTransform(string).get((float[])null);
            this.transform = new AffineTransform(value[0], value[3], value[1], value[4], value[2], value[5]);
            final Point2D transform = this.transform.transform(new Point2D.Float(this.cx, this.cy), null);
            final Point2D transform2 = this.transform.transform(new Point2D.Float(this.cx + this.r, this.cy), null);
            this.cx = (float)transform.getX();
            this.cy = (float)transform.getY();
            this.r = (float)(transform2.getX() - transform.getX());
        }
    }
}