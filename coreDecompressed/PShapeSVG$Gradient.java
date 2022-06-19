package processing.core;

import java.util.HashMap;
import processing.xml.XMLElement;
import java.awt.geom.AffineTransform;

static class Gradient extends PShapeSVG
{
    AffineTransform transform;
    float[] offset;
    int[] color;
    int count;
    
    public Gradient(final PShapeSVG pShapeSVG, final XMLElement xmlElement) {
        super(pShapeSVG, xmlElement, true);
        final XMLElement[] children = xmlElement.getChildren();
        this.offset = new float[children.length];
        this.color = new int[children.length];
        for (int i = 0; i < children.length; ++i) {
            final XMLElement xmlElement2 = children[i];
            if (xmlElement2.getName().equals("stop")) {
                String s = xmlElement2.getString("offset");
                float n = 1.0f;
                if (s.endsWith("%")) {
                    n = 100.0f;
                    s = s.substring(0, s.length() - 1);
                }
                this.offset[this.count] = PApplet.parseFloat(s) / n;
                final HashMap<String, String> styleAttributes = PShapeSVG.parseStyleAttributes(xmlElement2.getString("style"));
                String s2 = styleAttributes.get("stop-color");
                if (s2 == null) {
                    s2 = "#000000";
                }
                String s3 = styleAttributes.get("stop-opacity");
                if (s3 == null) {
                    s3 = "1";
                }
                this.color[this.count] = ((int)(PApplet.parseFloat(s3) * 255.0f) << 24 | Integer.parseInt(s2.substring(1), 16));
                ++this.count;
            }
        }
        this.offset = PApplet.subset(this.offset, 0, this.count);
        this.color = PApplet.subset(this.color, 0, this.count);
    }
}