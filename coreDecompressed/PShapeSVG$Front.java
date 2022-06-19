package processing.core;

import processing.xml.XMLElement;
import java.util.HashMap;

public class Font extends PShapeSVG
{
    public PShapeSVG.FontFace face;
    public HashMap<String, PShapeSVG.FontGlyph> namedGlyphs;
    public HashMap<Character, PShapeSVG.FontGlyph> unicodeGlyphs;
    public int glyphCount;
    public PShapeSVG.FontGlyph[] glyphs;
    public PShapeSVG.FontGlyph missingGlyph;
    int horizAdvX;
    
    public Font(final PShapeSVG pShapeSVG, final XMLElement xmlElement) {
        super(pShapeSVG, xmlElement, false);
        final XMLElement[] children = xmlElement.getChildren();
        this.horizAdvX = xmlElement.getInt("horiz-adv-x", 0);
        this.namedGlyphs = new HashMap<String, PShapeSVG.FontGlyph>();
        this.unicodeGlyphs = new HashMap<Character, PShapeSVG.FontGlyph>();
        this.glyphCount = 0;
        this.glyphs = new PShapeSVG.FontGlyph[children.length];
        for (int i = 0; i < children.length; ++i) {
            final String name = children[i].getName();
            final XMLElement xmlElement2 = children[i];
            if (name.equals("glyph")) {
                final PShapeSVG.FontGlyph fontGlyph = new PShapeSVG.FontGlyph((PShapeSVG)this, (PShapeSVG)this, xmlElement2, this);
                if (fontGlyph.isLegit()) {
                    if (fontGlyph.name != null) {
                        this.namedGlyphs.put(fontGlyph.name, fontGlyph);
                    }
                    if (fontGlyph.unicode != '\0') {
                        this.unicodeGlyphs.put(new Character(fontGlyph.unicode), fontGlyph);
                    }
                }
                this.glyphs[this.glyphCount++] = fontGlyph;
            }
            else if (name.equals("missing-glyph")) {
                this.missingGlyph = new PShapeSVG.FontGlyph((PShapeSVG)this, (PShapeSVG)this, xmlElement2, this);
            }
            else if (name.equals("font-face")) {
                this.face = new PShapeSVG.FontFace((PShapeSVG)this, (PShapeSVG)this, xmlElement2);
            }
            else {
                System.err.println("Ignoring " + name + " inside <font>");
            }
        }
    }
    
    protected void drawShape() {
    }
    
    public void drawString(final PGraphics pGraphics, final String s, final float n, final float n2, final float n3) {
        pGraphics.pushMatrix();
        final float n4 = n3 / this.face.unitsPerEm;
        pGraphics.translate(n, n2);
        pGraphics.scale(n4, -n4);
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            final PShapeSVG.FontGlyph fontGlyph = this.unicodeGlyphs.get(new Character(charArray[i]));
            if (fontGlyph != null) {
                fontGlyph.draw(pGraphics);
                pGraphics.translate((float)fontGlyph.horizAdvX, 0.0f);
            }
            else {
                System.err.println("'" + charArray[i] + "' not available.");
            }
        }
        pGraphics.popMatrix();
    }
    
    public void drawChar(final PGraphics pGraphics, final char value, final float n, final float n2, final float n3) {
        pGraphics.pushMatrix();
        final float n4 = n3 / this.face.unitsPerEm;
        pGraphics.translate(n, n2);
        pGraphics.scale(n4, -n4);
        final PShapeSVG.FontGlyph fontGlyph = this.unicodeGlyphs.get(new Character(value));
        if (fontGlyph != null) {
            pGraphics.shape((PShape)fontGlyph);
        }
        pGraphics.popMatrix();
    }
    
    public float textWidth(final String s, final float n) {
        float n2 = 0.0f;
        final char[] charArray = s.toCharArray();
        for (int i = 0; i < charArray.length; ++i) {
            final PShapeSVG.FontGlyph fontGlyph = this.unicodeGlyphs.get(new Character(charArray[i]));
            if (fontGlyph != null) {
                n2 += fontGlyph.horizAdvX / (float)this.face.unitsPerEm;
            }
        }
        return n2 * n;
    }
}