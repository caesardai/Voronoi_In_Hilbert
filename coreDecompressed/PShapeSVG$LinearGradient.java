package processing.core;

import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import processing.xml.XMLElement;

class LinearGradient extends Gradient
{
    float x1;
    float y1;
    float x2;
    float y2;
    
    public LinearGradient(final PShapeSVG pShapeSVG, final XMLElement xmlElement) {
        super(pShapeSVG, xmlElement);
        this.x1 = PShapeSVG.getFloatWithUnit(xmlElement, "x1");
        this.y1 = PShapeSVG.getFloatWithUnit(xmlElement, "y1");
        this.x2 = PShapeSVG.getFloatWithUnit(xmlElement, "x2");
        this.y2 = PShapeSVG.getFloatWithUnit(xmlElement, "y2");
        final String string = xmlElement.getString("gradientTransform");
        if (string != null) {
            final float[] value = PShapeSVG.parseTransform(string).get((float[])null);
            this.transform = new AffineTransform(value[0], value[3], value[1], value[4], value[2], value[5]);
            final Point2D transform = this.transform.transform(new Point2D.Float(this.x1, this.y1), null);
            final Point2D transform2 = this.transform.transform(new Point2D.Float(this.x2, this.y2), null);
            this.x1 = (float)transform.getX();
            this.y1 = (float)transform.getY();
            this.x2 = (float)transform2.getX();
            this.y2 = (float)transform2.getY();
        }
    }
}