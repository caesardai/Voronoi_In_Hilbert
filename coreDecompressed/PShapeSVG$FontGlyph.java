package processing.core;

import processing.xml.XMLElement;

public class FontGlyph extends PShapeSVG
{
    public String name;
    char unicode;
    int horizAdvX;
    
    public FontGlyph(final PShapeSVG pShapeSVG, final XMLElement xmlElement, final Font font) {
        super(pShapeSVG, xmlElement, true);
        super.parsePath();
        this.name = xmlElement.getString("glyph-name");
        final String string = xmlElement.getString("unicode");
        this.unicode = '\0';
        if (string != null) {
            if (string.length() == 1) {
                this.unicode = string.charAt(0);
            }
            else {
                System.err.println("unicode for " + this.name + " is more than one char: " + string);
            }
        }
        if (xmlElement.hasAttribute("horiz-adv-x")) {
            this.horizAdvX = xmlElement.getInt("horiz-adv-x");
        }
        else {
            this.horizAdvX = font.horizAdvX;
        }
    }
    
    protected boolean isLegit() {
        return this.vertexCount != 0;
    }
}