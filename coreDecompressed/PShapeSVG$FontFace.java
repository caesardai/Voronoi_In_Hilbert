package processing.core;

import processing.xml.XMLElement;

class FontFace extends PShapeSVG
{
    int horizOriginX;
    int horizOriginY;
    int vertOriginX;
    int vertOriginY;
    int vertAdvY;
    String fontFamily;
    int fontWeight;
    String fontStretch;
    int unitsPerEm;
    int[] panose1;
    int ascent;
    int descent;
    int[] bbox;
    int underlineThickness;
    int underlinePosition;
    
    public FontFace(final PShapeSVG pShapeSVG, final XMLElement xmlElement) {
        super(pShapeSVG, xmlElement, true);
        this.unitsPerEm = xmlElement.getInt("units-per-em", 1000);
    }
    
    protected void drawShape() {
    }
}