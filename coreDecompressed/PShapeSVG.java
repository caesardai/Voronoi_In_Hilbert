// 
// Decompiled by Procyon v0.5.36
// 

package processing.core;

import java.util.HashMap;
import java.awt.Paint;
import processing.xml.XMLElement;

public class PShapeSVG extends PShape
{
    XMLElement element;
    float opacity;
    float strokeOpacity;
    float fillOpacity;
    PShapeSVG.Gradient strokeGradient;
    Paint strokeGradientPaint;
    String strokeName;
    PShapeSVG.Gradient fillGradient;
    Paint fillGradientPaint;
    String fillName;
    
    public PShapeSVG(final PApplet pApplet, final String s) {
        this(new XMLElement(pApplet, s));
    }
    
    public PShapeSVG(final XMLElement xmlElement) {
        this(null, xmlElement, true);
        if (!xmlElement.getName().equals("svg")) {
            throw new RuntimeException("root is not <svg>, it's <" + xmlElement.getName() + ">");
        }
        final String string = xmlElement.getString("viewBox");
        if (string != null) {
            final int[] int1 = PApplet.parseInt(PApplet.splitTokens(string));
            this.width = (float)int1[2];
            this.height = (float)int1[3];
        }
        final String string2 = xmlElement.getString("width");
        final String string3 = xmlElement.getString("height");
        if (string2 != null) {
            this.width = parseUnitSize(string2);
            this.height = parseUnitSize(string3);
        }
        else if (this.width == 0.0f || this.height == 0.0f) {
            PGraphics.showWarning("The width and/or height is not readable in the <svg> tag of this file.");
            this.width = 1.0f;
            this.height = 1.0f;
        }
    }
    
    public PShapeSVG(final PShapeSVG parent, final XMLElement element, final boolean b) {
        this.parent = parent;
        if (parent == null) {
            this.stroke = false;
            this.strokeColor = -16777216;
            this.strokeWeight = 1.0f;
            this.strokeCap = 1;
            this.strokeJoin = 8;
            this.strokeGradient = null;
            this.strokeGradientPaint = null;
            this.strokeName = null;
            this.fill = true;
            this.fillColor = -16777216;
            this.fillGradient = null;
            this.fillGradientPaint = null;
            this.fillName = null;
            this.strokeOpacity = 1.0f;
            this.fillOpacity = 1.0f;
            this.opacity = 1.0f;
        }
        else {
            this.stroke = parent.stroke;
            this.strokeColor = parent.strokeColor;
            this.strokeWeight = parent.strokeWeight;
            this.strokeCap = parent.strokeCap;
            this.strokeJoin = parent.strokeJoin;
            this.strokeGradient = parent.strokeGradient;
            this.strokeGradientPaint = parent.strokeGradientPaint;
            this.strokeName = parent.strokeName;
            this.fill = parent.fill;
            this.fillColor = parent.fillColor;
            this.fillGradient = parent.fillGradient;
            this.fillGradientPaint = parent.fillGradientPaint;
            this.fillName = parent.fillName;
            this.opacity = parent.opacity;
        }
        this.element = element;
        this.name = element.getString("id");
        if (this.name != null) {
            while (true) {
                final String[] match = PApplet.match(this.name, "_x([A-Za-z0-9]{2})_");
                if (match == null) {
                    break;
                }
                this.name = this.name.replace(match[0], "" + (char)PApplet.unhex(match[1]));
            }
        }
        this.visible = !element.getString("display", "inline").equals("none");
        final String string = element.getString("transform");
        if (string != null) {
            this.matrix = (PMatrix)parseTransform(string);
        }
        if (b) {
            this.parseColors(element);
            this.parseChildren(element);
        }
    }
    
    protected void parseChildren(final XMLElement xmlElement) {
        final XMLElement[] children = xmlElement.getChildren();
        this.children = new PShape[children.length];
        this.childCount = 0;
        final XMLElement[] array = children;
        for (int length = array.length, i = 0; i < length; ++i) {
            final PShape child = this.parseChild(array[i]);
            if (child != null) {
                this.addChild(child);
            }
        }
        this.children = (PShape[])PApplet.subset((Object)this.children, 0, this.childCount);
    }
    
    protected PShape parseChild(final XMLElement xmlElement) {
        final String name = xmlElement.getName();
        PShapeSVG pShapeSVG = null;
        if (name.equals("g")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
        }
        else if (name.equals("defs")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
        }
        else if (name.equals("line")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parseLine();
        }
        else if (name.equals("circle")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parseEllipse(true);
        }
        else if (name.equals("ellipse")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parseEllipse(false);
        }
        else if (name.equals("rect")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parseRect();
        }
        else if (name.equals("polygon")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parsePoly(true);
        }
        else if (name.equals("polyline")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parsePoly(false);
        }
        else if (name.equals("path")) {
            pShapeSVG = new PShapeSVG(this, xmlElement, true);
            pShapeSVG.parsePath();
        }
        else {
            if (name.equals("radialGradient")) {
                return (PShape)new PShapeSVG.RadialGradient(this, this, xmlElement);
            }
            if (name.equals("linearGradient")) {
                return (PShape)new PShapeSVG.LinearGradient(this, this, xmlElement);
            }
            if (name.equals("font")) {
                return (PShape)new PShapeSVG.Font(this, this, xmlElement);
            }
            if (name.equals("metadata")) {
                return null;
            }
            if (name.equals("text")) {
                PGraphics.showWarning("Text and fonts in SVG files are not currently supported, convert text to outlines instead.");
            }
            else if (name.equals("filter")) {
                PGraphics.showWarning("Filters are not supported.");
            }
            else if (name.equals("mask")) {
                PGraphics.showWarning("Masks are not supported.");
            }
            else if (name.equals("pattern")) {
                PGraphics.showWarning("Patterns are not supported.");
            }
            else if (!name.equals("stop")) {
                if (!name.equals("sodipodi:namedview")) {
                    PGraphics.showWarning("Ignoring <" + name + "> tag.");
                }
            }
        }
        return pShapeSVG;
    }
    
    protected void parseLine() {
        this.primitive = 4;
        this.family = 1;
        this.params = new float[] { getFloatWithUnit(this.element, "x1"), getFloatWithUnit(this.element, "y1"), getFloatWithUnit(this.element, "x2"), getFloatWithUnit(this.element, "y2") };
    }
    
    protected void parseEllipse(final boolean b) {
        this.primitive = 31;
        this.family = 1;
        (this.params = new float[4])[0] = getFloatWithUnit(this.element, "cx");
        this.params[1] = getFloatWithUnit(this.element, "cy");
        float n;
        float floatWithUnit;
        if (b) {
            floatWithUnit = (n = getFloatWithUnit(this.element, "r"));
        }
        else {
            n = getFloatWithUnit(this.element, "rx");
            floatWithUnit = getFloatWithUnit(this.element, "ry");
        }
        final float[] params = this.params;
        final int n2 = 0;
        params[n2] -= n;
        final float[] params2 = this.params;
        final int n3 = 1;
        params2[n3] -= floatWithUnit;
        this.params[2] = n * 2.0f;
        this.params[3] = floatWithUnit * 2.0f;
    }
    
    protected void parseRect() {
        this.primitive = 30;
        this.family = 1;
        this.params = new float[] { getFloatWithUnit(this.element, "x"), getFloatWithUnit(this.element, "y"), getFloatWithUnit(this.element, "width"), getFloatWithUnit(this.element, "height") };
    }
    
    protected void parsePoly(final boolean close) {
        this.family = 2;
        this.close = close;
        final String string = this.element.getString("points");
        if (string != null) {
            final String[] splitTokens = PApplet.splitTokens(string);
            this.vertexCount = splitTokens.length;
            this.vertices = new float[this.vertexCount][2];
            for (int i = 0; i < this.vertexCount; ++i) {
                final String[] split = PApplet.split(splitTokens[i], ',');
                this.vertices[i][0] = Float.valueOf(split[0]);
                this.vertices[i][1] = Float.valueOf(split[1]);
            }
        }
    }
    
    protected void parsePath() {
        this.family = 2;
        this.primitive = 0;
        final String string = this.element.getString("d");
        if (string == null || PApplet.trim(string).length() == 0) {
            return;
        }
        final char[] charArray = string.toCharArray();
        final StringBuffer sb = new StringBuffer();
        int n = 0;
        for (int i = 0; i < charArray.length; ++i) {
            final char c = charArray[i];
            int n2 = 0;
            if (c == 'M' || c == 'm' || c == 'L' || c == 'l' || c == 'H' || c == 'h' || c == 'V' || c == 'v' || c == 'C' || c == 'c' || c == 'S' || c == 's' || c == 'Q' || c == 'q' || c == 'T' || c == 't' || c == 'Z' || c == 'z' || c == ',') {
                n2 = 1;
                if (i != 0) {
                    sb.append("|");
                }
            }
            if (c == 'Z' || c == 'z') {
                n2 = 0;
            }
            if (c == '-' && n == 0 && (i == 0 || charArray[i - 1] != 'e')) {
                sb.append("|");
            }
            if (c != ',') {
                sb.append(c);
            }
            if (n2 != 0 && c != ',' && c != '-') {
                sb.append("|");
            }
            n = n2;
        }
        final String[] splitTokens = PApplet.splitTokens(sb.toString(), "| \t\n\r\f ");
        this.vertices = new float[splitTokens.length][2];
        this.vertexCodes = new int[splitTokens.length];
        float n3 = 0.0f;
        float n4 = 0.0f;
        int j = 0;
        int n5 = 0;
        int n6 = 0;
        while (j < splitTokens.length) {
            int char1 = splitTokens[j].charAt(0);
            if (((char1 >= 48 && char1 <= 57) || char1 == 45) && n5 != 0) {
                char1 = n5;
                --j;
            }
            else {
                n5 = char1;
            }
            switch (char1) {
                case 77: {
                    n3 = PApplet.parseFloat(splitTokens[j + 1]);
                    n4 = PApplet.parseFloat(splitTokens[j + 2]);
                    this.parsePathMoveto(n3, n4);
                    n5 = 76;
                    j += 3;
                    continue;
                }
                case 109: {
                    n3 += PApplet.parseFloat(splitTokens[j + 1]);
                    n4 += PApplet.parseFloat(splitTokens[j + 2]);
                    this.parsePathMoveto(n3, n4);
                    n5 = 108;
                    j += 3;
                    continue;
                }
                case 76: {
                    n3 = PApplet.parseFloat(splitTokens[j + 1]);
                    n4 = PApplet.parseFloat(splitTokens[j + 2]);
                    this.parsePathLineto(n3, n4);
                    j += 3;
                    continue;
                }
                case 108: {
                    n3 += PApplet.parseFloat(splitTokens[j + 1]);
                    n4 += PApplet.parseFloat(splitTokens[j + 2]);
                    this.parsePathLineto(n3, n4);
                    j += 3;
                    continue;
                }
                case 72: {
                    n3 = PApplet.parseFloat(splitTokens[j + 1]);
                    this.parsePathLineto(n3, n4);
                    j += 2;
                    continue;
                }
                case 104: {
                    n3 += PApplet.parseFloat(splitTokens[j + 1]);
                    this.parsePathLineto(n3, n4);
                    j += 2;
                    continue;
                }
                case 86: {
                    n4 = PApplet.parseFloat(splitTokens[j + 1]);
                    this.parsePathLineto(n3, n4);
                    j += 2;
                    continue;
                }
                case 118: {
                    n4 += PApplet.parseFloat(splitTokens[j + 1]);
                    this.parsePathLineto(n3, n4);
                    j += 2;
                    continue;
                }
                case 67: {
                    final float float1 = PApplet.parseFloat(splitTokens[j + 1]);
                    final float float2 = PApplet.parseFloat(splitTokens[j + 2]);
                    final float float3 = PApplet.parseFloat(splitTokens[j + 3]);
                    final float float4 = PApplet.parseFloat(splitTokens[j + 4]);
                    final float float5 = PApplet.parseFloat(splitTokens[j + 5]);
                    final float float6 = PApplet.parseFloat(splitTokens[j + 6]);
                    this.parsePathCurveto(float1, float2, float3, float4, float5, float6);
                    n3 = float5;
                    n4 = float6;
                    j += 7;
                    n6 = 1;
                    continue;
                }
                case 99: {
                    final float n7 = n3 + PApplet.parseFloat(splitTokens[j + 1]);
                    final float n8 = n4 + PApplet.parseFloat(splitTokens[j + 2]);
                    final float n9 = n3 + PApplet.parseFloat(splitTokens[j + 3]);
                    final float n10 = n4 + PApplet.parseFloat(splitTokens[j + 4]);
                    final float n11 = n3 + PApplet.parseFloat(splitTokens[j + 5]);
                    final float n12 = n4 + PApplet.parseFloat(splitTokens[j + 6]);
                    this.parsePathCurveto(n7, n8, n9, n10, n11, n12);
                    n3 = n11;
                    n4 = n12;
                    j += 7;
                    n6 = 1;
                    continue;
                }
                case 83: {
                    float n13;
                    float n14;
                    if (n6 == 0) {
                        n13 = n3;
                        n14 = n4;
                    }
                    else {
                        final float n15 = this.vertices[this.vertexCount - 2][0];
                        final float n16 = this.vertices[this.vertexCount - 2][1];
                        final float n17 = this.vertices[this.vertexCount - 1][0];
                        final float n18 = this.vertices[this.vertexCount - 1][1];
                        n13 = n17 + (n17 - n15);
                        n14 = n18 + (n18 - n16);
                    }
                    final float float7 = PApplet.parseFloat(splitTokens[j + 1]);
                    final float float8 = PApplet.parseFloat(splitTokens[j + 2]);
                    final float float9 = PApplet.parseFloat(splitTokens[j + 3]);
                    final float float10 = PApplet.parseFloat(splitTokens[j + 4]);
                    this.parsePathCurveto(n13, n14, float7, float8, float9, float10);
                    n3 = float9;
                    n4 = float10;
                    j += 5;
                    n6 = 1;
                    continue;
                }
                case 115: {
                    float n19;
                    float n20;
                    if (n6 == 0) {
                        n19 = n3;
                        n20 = n4;
                    }
                    else {
                        final float n21 = this.vertices[this.vertexCount - 2][0];
                        final float n22 = this.vertices[this.vertexCount - 2][1];
                        final float n23 = this.vertices[this.vertexCount - 1][0];
                        final float n24 = this.vertices[this.vertexCount - 1][1];
                        n19 = n23 + (n23 - n21);
                        n20 = n24 + (n24 - n22);
                    }
                    final float n25 = n3 + PApplet.parseFloat(splitTokens[j + 1]);
                    final float n26 = n4 + PApplet.parseFloat(splitTokens[j + 2]);
                    final float n27 = n3 + PApplet.parseFloat(splitTokens[j + 3]);
                    final float n28 = n4 + PApplet.parseFloat(splitTokens[j + 4]);
                    this.parsePathCurveto(n19, n20, n25, n26, n27, n28);
                    n3 = n27;
                    n4 = n28;
                    j += 5;
                    n6 = 1;
                    continue;
                }
                case 81: {
                    final float float11 = PApplet.parseFloat(splitTokens[j + 1]);
                    final float float12 = PApplet.parseFloat(splitTokens[j + 2]);
                    final float float13 = PApplet.parseFloat(splitTokens[j + 3]);
                    final float float14 = PApplet.parseFloat(splitTokens[j + 4]);
                    this.parsePathQuadto(float11, float12, float13, float14);
                    n3 = float13;
                    n4 = float14;
                    j += 5;
                    n6 = 1;
                    continue;
                }
                case 113: {
                    final float n29 = n3 + PApplet.parseFloat(splitTokens[j + 1]);
                    final float n30 = n4 + PApplet.parseFloat(splitTokens[j + 2]);
                    final float n31 = n3 + PApplet.parseFloat(splitTokens[j + 3]);
                    final float n32 = n4 + PApplet.parseFloat(splitTokens[j + 4]);
                    this.parsePathQuadto(n29, n30, n31, n32);
                    n3 = n31;
                    n4 = n32;
                    j += 5;
                    n6 = 1;
                    continue;
                }
                case 84: {
                    float n33;
                    float n34;
                    if (n6 == 0) {
                        n33 = n3;
                        n34 = n4;
                    }
                    else {
                        final float n35 = this.vertices[this.vertexCount - 2][0];
                        final float n36 = this.vertices[this.vertexCount - 2][1];
                        final float n37 = this.vertices[this.vertexCount - 1][0];
                        final float n38 = this.vertices[this.vertexCount - 1][1];
                        n33 = n37 + (n37 - n35);
                        n34 = n38 + (n38 - n36);
                    }
                    final float float15 = PApplet.parseFloat(splitTokens[j + 1]);
                    final float float16 = PApplet.parseFloat(splitTokens[j + 2]);
                    this.parsePathQuadto(n33, n34, float15, float16);
                    n3 = float15;
                    n4 = float16;
                    j += 3;
                    n6 = 1;
                    continue;
                }
                case 116: {
                    float n39;
                    float n40;
                    if (n6 == 0) {
                        n39 = n3;
                        n40 = n4;
                    }
                    else {
                        final float n41 = this.vertices[this.vertexCount - 2][0];
                        final float n42 = this.vertices[this.vertexCount - 2][1];
                        final float n43 = this.vertices[this.vertexCount - 1][0];
                        final float n44 = this.vertices[this.vertexCount - 1][1];
                        n39 = n43 + (n43 - n41);
                        n40 = n44 + (n44 - n42);
                    }
                    final float n45 = n3 + PApplet.parseFloat(splitTokens[j + 1]);
                    final float n46 = n4 + PApplet.parseFloat(splitTokens[j + 2]);
                    this.parsePathQuadto(n39, n40, n45, n46);
                    n3 = n45;
                    n4 = n46;
                    j += 3;
                    n6 = 1;
                    continue;
                }
                case 90:
                case 122: {
                    this.close = true;
                    ++j;
                    continue;
                }
                default: {
                    final String join = PApplet.join(PApplet.subset(splitTokens, 0, j), ",");
                    final String join2 = PApplet.join(PApplet.subset(splitTokens, j), ",");
                    System.err.println("parsed: " + join);
                    System.err.println("unparsed: " + join2);
                    if (splitTokens[j].equals("a") || splitTokens[j].equals("A")) {
                        throw new RuntimeException("Sorry, elliptical arc support for SVG files is not yet implemented (See issue #130 for updates)");
                    }
                    throw new RuntimeException("shape command not handled: " + splitTokens[j]);
                }
            }
        }
    }
    
    private void parsePathVertex(final float n, final float n2) {
        if (this.vertexCount == this.vertices.length) {
            final float[][] vertices = new float[this.vertexCount << 1][2];
            System.arraycopy(this.vertices, 0, vertices, 0, this.vertexCount);
            this.vertices = vertices;
        }
        this.vertices[this.vertexCount][0] = n;
        this.vertices[this.vertexCount][1] = n2;
        ++this.vertexCount;
    }
    
    private void parsePathCode(final int n) {
        if (this.vertexCodeCount == this.vertexCodes.length) {
            this.vertexCodes = PApplet.expand(this.vertexCodes);
        }
        this.vertexCodes[this.vertexCodeCount++] = n;
    }
    
    private void parsePathMoveto(final float n, final float n2) {
        if (this.vertexCount > 0) {
            this.parsePathCode(4);
        }
        this.parsePathCode(0);
        this.parsePathVertex(n, n2);
    }
    
    private void parsePathLineto(final float n, final float n2) {
        this.parsePathCode(0);
        this.parsePathVertex(n, n2);
    }
    
    private void parsePathCurveto(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.parsePathCode(1);
        this.parsePathVertex(n, n2);
        this.parsePathVertex(n3, n4);
        this.parsePathVertex(n5, n6);
    }
    
    private void parsePathQuadto(final float n, final float n2, final float n3, final float n4) {
        this.parsePathCode(2);
        this.parsePathVertex(n, n2);
        this.parsePathVertex(n3, n4);
    }
    
    protected static PMatrix2D parseTransform(String trim) {
        trim = trim.trim();
        PMatrix2D pMatrix2D = null;
        int index;
        for (int n = 0; (index = trim.indexOf(41, n)) != -1; n = index + 1) {
            final PMatrix2D singleTransform = parseSingleTransform(trim.substring(n, index + 1));
            if (pMatrix2D == null) {
                pMatrix2D = singleTransform;
            }
            else {
                pMatrix2D.apply(singleTransform);
            }
        }
        return pMatrix2D;
    }
    
    protected static PMatrix2D parseSingleTransform(final String str) {
        final String[] match = PApplet.match(str, "[,\\s]*(\\w+)\\((.*)\\)");
        if (match == null) {
            System.err.println("Could not parse transform " + str);
            return null;
        }
        final float[] float1 = PApplet.parseFloat(PApplet.splitTokens(match[2], ", "));
        if (match[1].equals("matrix")) {
            return new PMatrix2D(float1[0], float1[2], float1[4], float1[1], float1[3], float1[5]);
        }
        if (match[1].equals("translate")) {
            return new PMatrix2D(1.0f, 0.0f, float1[0], 0.0f, 1.0f, (float1.length == 2) ? float1[1] : float1[0]);
        }
        if (match[1].equals("scale")) {
            return new PMatrix2D(float1[0], 0.0f, 0.0f, 0.0f, (float1.length == 2) ? float1[1] : float1[0], 0.0f);
        }
        if (match[1].equals("rotate")) {
            final float n = float1[0];
            if (float1.length == 1) {
                final float cos = PApplet.cos(n);
                final float sin = PApplet.sin(n);
                return new PMatrix2D(cos, -sin, 0.0f, sin, cos, 0.0f);
            }
            if (float1.length == 3) {
                final PMatrix2D pMatrix2D = new PMatrix2D(0.0f, 1.0f, float1[1], 1.0f, 0.0f, float1[2]);
                pMatrix2D.rotate(float1[0]);
                pMatrix2D.translate(-float1[1], -float1[2]);
                return pMatrix2D;
            }
        }
        else {
            if (match[1].equals("skewX")) {
                return new PMatrix2D(1.0f, 0.0f, 1.0f, PApplet.tan(float1[0]), 0.0f, 0.0f);
            }
            if (match[1].equals("skewY")) {
                return new PMatrix2D(1.0f, 0.0f, 1.0f, 0.0f, PApplet.tan(float1[0]), 0.0f);
            }
        }
        return null;
    }
    
    protected void parseColors(final XMLElement xmlElement) {
        if (xmlElement.hasAttribute("opacity")) {
            this.setOpacity(xmlElement.getString("opacity"));
        }
        if (xmlElement.hasAttribute("stroke")) {
            this.setColor(xmlElement.getString("stroke"), false);
        }
        if (xmlElement.hasAttribute("stroke-opacity")) {
            this.setStrokeOpacity(xmlElement.getString("stroke-opacity"));
        }
        if (xmlElement.hasAttribute("stroke-width")) {
            this.setStrokeWeight(xmlElement.getString("stroke-width"));
        }
        if (xmlElement.hasAttribute("stroke-linejoin")) {
            this.setStrokeJoin(xmlElement.getString("stroke-linejoin"));
        }
        if (xmlElement.hasAttribute("stroke-linecap")) {
            this.setStrokeCap(xmlElement.getString("stroke-linecap"));
        }
        if (xmlElement.hasAttribute("fill")) {
            this.setColor(xmlElement.getString("fill"), true);
        }
        if (xmlElement.hasAttribute("fill-opacity")) {
            this.setFillOpacity(xmlElement.getString("fill-opacity"));
        }
        if (xmlElement.hasAttribute("style")) {
            final String[] splitTokens = PApplet.splitTokens(xmlElement.getString("style"), ";");
            for (int i = 0; i < splitTokens.length; ++i) {
                final String[] splitTokens2 = PApplet.splitTokens(splitTokens[i], ":");
                splitTokens2[0] = PApplet.trim(splitTokens2[0]);
                if (splitTokens2[0].equals("fill")) {
                    this.setColor(splitTokens2[1], true);
                }
                else if (splitTokens2[0].equals("fill-opacity")) {
                    this.setFillOpacity(splitTokens2[1]);
                }
                else if (splitTokens2[0].equals("stroke")) {
                    this.setColor(splitTokens2[1], false);
                }
                else if (splitTokens2[0].equals("stroke-width")) {
                    this.setStrokeWeight(splitTokens2[1]);
                }
                else if (splitTokens2[0].equals("stroke-linecap")) {
                    this.setStrokeCap(splitTokens2[1]);
                }
                else if (splitTokens2[0].equals("stroke-linejoin")) {
                    this.setStrokeJoin(splitTokens2[1]);
                }
                else if (splitTokens2[0].equals("stroke-opacity")) {
                    this.setStrokeOpacity(splitTokens2[1]);
                }
                else if (splitTokens2[0].equals("opacity")) {
                    this.setOpacity(splitTokens2[1]);
                }
            }
        }
    }
    
    void setOpacity(final String s) {
        this.opacity = PApplet.parseFloat(s);
        this.strokeColor = ((int)(this.opacity * 255.0f) << 24 | (this.strokeColor & 0xFFFFFF));
        this.fillColor = ((int)(this.opacity * 255.0f) << 24 | (this.fillColor & 0xFFFFFF));
    }
    
    void setStrokeWeight(final String s) {
        this.strokeWeight = parseUnitSize(s);
    }
    
    void setStrokeOpacity(final String s) {
        this.strokeOpacity = PApplet.parseFloat(s);
        this.strokeColor = ((int)(this.strokeOpacity * 255.0f) << 24 | (this.strokeColor & 0xFFFFFF));
    }
    
    void setStrokeJoin(final String s) {
        if (!s.equals("inherit")) {
            if (s.equals("miter")) {
                this.strokeJoin = 8;
            }
            else if (s.equals("round")) {
                this.strokeJoin = 2;
            }
            else if (s.equals("bevel")) {
                this.strokeJoin = 32;
            }
        }
    }
    
    void setStrokeCap(final String s) {
        if (!s.equals("inherit")) {
            if (s.equals("butt")) {
                this.strokeCap = 1;
            }
            else if (s.equals("round")) {
                this.strokeCap = 2;
            }
            else if (s.equals("square")) {
                this.strokeCap = 4;
            }
        }
    }
    
    void setFillOpacity(final String s) {
        this.fillOpacity = PApplet.parseFloat(s);
        this.fillColor = ((int)(this.fillOpacity * 255.0f) << 24 | (this.fillColor & 0xFFFFFF));
    }
    
    void setColor(String replaceAll, final boolean b) {
        final int n = this.fillColor & 0xFF000000;
        boolean b2 = true;
        int n2 = 0;
        String substring = "";
        PShapeSVG.Gradient gradient = null;
        Paint calcGradientPaint = null;
        if (replaceAll.equals("none")) {
            b2 = false;
        }
        else if (replaceAll.equals("black")) {
            n2 = n;
        }
        else if (replaceAll.equals("white")) {
            n2 = (n | 0xFFFFFF);
        }
        else if (replaceAll.startsWith("#")) {
            if (replaceAll.length() == 4) {
                replaceAll = replaceAll.replaceAll("^#(.)(.)(.)$", "#$1$1$2$2$3$3");
            }
            n2 = (n | (Integer.parseInt(replaceAll.substring(1), 16) & 0xFFFFFF));
        }
        else if (replaceAll.startsWith("rgb")) {
            n2 = (n | parseRGB(replaceAll));
        }
        else if (replaceAll.startsWith("url(#")) {
            substring = replaceAll.substring(5, replaceAll.length() - 1);
            final PShape child = this.findChild(substring);
            if (child instanceof PShapeSVG.Gradient) {
                gradient = (PShapeSVG.Gradient)child;
                calcGradientPaint = this.calcGradientPaint(gradient);
            }
            else {
                System.err.println("url " + substring + " refers to unexpected data: " + child);
            }
        }
        if (b) {
            this.fill = b2;
            this.fillColor = n2;
            this.fillName = substring;
            this.fillGradient = gradient;
            this.fillGradientPaint = calcGradientPaint;
        }
        else {
            this.stroke = b2;
            this.strokeColor = n2;
            this.strokeName = substring;
            this.strokeGradient = gradient;
            this.strokeGradientPaint = calcGradientPaint;
        }
    }
    
    protected static int parseRGB(final String s) {
        final int[] int1 = PApplet.parseInt(PApplet.splitTokens(s.substring(s.indexOf(40) + 1, s.indexOf(41)), ", "));
        return int1[0] << 16 | int1[1] << 8 | int1[2];
    }
    
    protected static HashMap<String, String> parseStyleAttributes(final String s) {
        final HashMap<String, String> hashMap = new HashMap<String, String>();
        final String[] split = s.split(";");
        for (int i = 0; i < split.length; ++i) {
            final String[] split2 = split[i].split(":");
            hashMap.put(split2[0], split2[1]);
        }
        return hashMap;
    }
    
    protected static float getFloatWithUnit(final XMLElement xmlElement, final String s) {
        final String string = xmlElement.getString(s);
        return (string == null) ? 0.0f : parseUnitSize(string);
    }
    
    protected static float parseUnitSize(final String s) {
        final int n = s.length() - 2;
        if (s.endsWith("pt")) {
            return PApplet.parseFloat(s.substring(0, n)) * 1.25f;
        }
        if (s.endsWith("pc")) {
            return PApplet.parseFloat(s.substring(0, n)) * 15.0f;
        }
        if (s.endsWith("mm")) {
            return PApplet.parseFloat(s.substring(0, n)) * 3.543307f;
        }
        if (s.endsWith("cm")) {
            return PApplet.parseFloat(s.substring(0, n)) * 35.43307f;
        }
        if (s.endsWith("in")) {
            return PApplet.parseFloat(s.substring(0, n)) * 90.0f;
        }
        if (s.endsWith("px")) {
            return PApplet.parseFloat(s.substring(0, n));
        }
        return PApplet.parseFloat(s);
    }
    
    protected Paint calcGradientPaint(final PShapeSVG.Gradient gradient) {
        if (gradient instanceof PShapeSVG.LinearGradient) {
            final PShapeSVG.LinearGradient linearGradient = (PShapeSVG.LinearGradient)gradient;
            return (Paint)new PShapeSVG.LinearGradientPaint(this, linearGradient.x1, linearGradient.y1, linearGradient.x2, linearGradient.y2, linearGradient.offset, linearGradient.color, linearGradient.count, this.opacity);
        }
        if (gradient instanceof PShapeSVG.RadialGradient) {
            final PShapeSVG.RadialGradient radialGradient = (PShapeSVG.RadialGradient)gradient;
            return (Paint)new PShapeSVG.RadialGradientPaint(this, radialGradient.cx, radialGradient.cy, radialGradient.r, radialGradient.offset, radialGradient.color, radialGradient.count, this.opacity);
        }
        return null;
    }
    
    @Override
    protected void styles(final PGraphics pGraphics) {
        super.styles(pGraphics);
        if (pGraphics instanceof PGraphicsJava2D) {
            final PGraphicsJava2D pGraphicsJava2D = (PGraphicsJava2D)pGraphics;
            if (this.strokeGradient != null) {
                pGraphicsJava2D.strokeGradient = true;
                pGraphicsJava2D.strokeGradientObject = this.strokeGradientPaint;
            }
            if (this.fillGradient != null) {
                pGraphicsJava2D.fillGradient = true;
                pGraphicsJava2D.fillGradientObject = this.fillGradientPaint;
            }
        }
    }
    
    @Override
    public PShape getChild(final String s) {
        PShape pShape = super.getChild(s);
        if (pShape == null) {
            pShape = super.getChild(s.replace(' ', '_'));
        }
        if (pShape != null) {
            pShape.width = this.width;
            pShape.height = this.height;
        }
        return pShape;
    }
    
    public void print() {
        PApplet.println(this.element.toString());
    }
}
