package processing.core;

import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Color;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class PGraphicsJava2D extends PGraphics
{
    public Graphics2D g2;
    protected BufferedImage offscreen;
    GeneralPath gpath;
    boolean breakShape;
    float[] curveCoordX;
    float[] curveCoordY;
    float[] curveDrawX;
    float[] curveDrawY;
    int transformCount;
    AffineTransform[] transformStack;
    double[] transform;
    Line2D.Float line;
    Ellipse2D.Float ellipse;
    Rectangle2D.Float rect;
    Arc2D.Float arc;
    protected Color tintColorObject;
    protected Color fillColorObject;
    public boolean fillGradient;
    public Paint fillGradientObject;
    protected Color strokeColorObject;
    public boolean strokeGradient;
    public Paint strokeGradientObject;
    int[] clearPixels;
    static int[] getset;
    
    public PGraphicsJava2D() {
        this.transformStack = new AffineTransform[32];
        this.transform = new double[6];
        this.line = new Line2D.Float();
        this.ellipse = new Ellipse2D.Float();
        this.rect = new Rectangle2D.Float();
        this.arc = new Arc2D.Float();
    }
    
    @Override
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.width1 = this.width - 1;
        this.height1 = this.height - 1;
        this.allocate();
        this.reapplySettings();
    }
    
    @Override
    protected void allocate() {
        this.image = new BufferedImage(this.width, this.height, 2);
        if (this.primarySurface) {
            this.offscreen = new BufferedImage(this.width, this.height, 2);
            this.g2 = (Graphics2D)this.offscreen.getGraphics();
        }
        else {
            this.g2 = (Graphics2D)this.image.getGraphics();
        }
    }
    
    @Override
    public boolean canDraw() {
        return true;
    }
    
    @Override
    public void beginDraw() {
        this.checkSettings();
        this.resetMatrix();
        this.vertexCount = 0;
    }
    
    @Override
    public void endDraw() {
        if (this.primarySurface) {
            synchronized (this.image) {
                this.image.getGraphics().drawImage(this.offscreen, 0, 0, null);
            }
        }
        else {
            this.loadPixels();
        }
        this.modified = true;
    }
    
    @Override
    public void beginShape(final int shape) {
        this.shape = shape;
        this.vertexCount = 0;
        this.curveVertexCount = 0;
        this.gpath = null;
    }
    
    @Override
    public void texture(final PImage pImage) {
        PGraphics.showMethodWarning("texture");
    }
    
    @Override
    public void vertex(final float n, final float n2) {
        this.curveVertexCount = 0;
        if (this.vertexCount == this.vertices.length) {
            final float[][] vertices = new float[this.vertexCount << 1][37];
            System.arraycopy(this.vertices, 0, vertices, 0, this.vertexCount);
            this.vertices = vertices;
        }
        this.vertices[this.vertexCount][0] = n;
        this.vertices[this.vertexCount][1] = n2;
        ++this.vertexCount;
        switch (this.shape) {
            case 2: {
                this.point(n, n2);
                break;
            }
            case 4: {
                if (this.vertexCount % 2 == 0) {
                    this.line(this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], n, n2);
                    break;
                }
                break;
            }
            case 9: {
                if (this.vertexCount % 3 == 0) {
                    this.triangle(this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], n, n2);
                    break;
                }
                break;
            }
            case 10: {
                if (this.vertexCount >= 3) {
                    this.triangle(this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], this.vertices[this.vertexCount - 1][0], this.vertices[this.vertexCount - 1][1], this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1]);
                    break;
                }
                break;
            }
            case 11: {
                if (this.vertexCount == 3) {
                    this.triangle(this.vertices[0][0], this.vertices[0][1], this.vertices[1][0], this.vertices[1][1], n, n2);
                    break;
                }
                if (this.vertexCount > 3) {
                    (this.gpath = new GeneralPath()).moveTo(this.vertices[0][0], this.vertices[0][1]);
                    this.gpath.lineTo(this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1]);
                    this.gpath.lineTo(n, n2);
                    this.drawShape(this.gpath);
                    break;
                }
                break;
            }
            case 16: {
                if (this.vertexCount % 4 == 0) {
                    this.quad(this.vertices[this.vertexCount - 4][0], this.vertices[this.vertexCount - 4][1], this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], n, n2);
                    break;
                }
                break;
            }
            case 17: {
                if (this.vertexCount >= 4 && this.vertexCount % 2 == 0) {
                    this.quad(this.vertices[this.vertexCount - 4][0], this.vertices[this.vertexCount - 4][1], this.vertices[this.vertexCount - 2][0], this.vertices[this.vertexCount - 2][1], n, n2, this.vertices[this.vertexCount - 3][0], this.vertices[this.vertexCount - 3][1]);
                    break;
                }
                break;
            }
            case 20: {
                if (this.gpath == null) {
                    (this.gpath = new GeneralPath()).moveTo(n, n2);
                    break;
                }
                if (this.breakShape) {
                    this.gpath.moveTo(n, n2);
                    this.breakShape = false;
                    break;
                }
                this.gpath.lineTo(n, n2);
                break;
            }
        }
    }
    
    @Override
    public void vertex(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("vertex");
    }
    
    @Override
    public void vertex(final float n, final float n2, final float n3, final float n4) {
        PGraphics.showVariationWarning("vertex(x, y, u, v)");
    }
    
    @Override
    public void vertex(final float n, final float n2, final float n3, final float n4, final float n5) {
        PGraphics.showDepthWarningXYZ("vertex");
    }
    
    @Override
    public void breakShape() {
        this.breakShape = true;
    }
    
    @Override
    public void endShape(final int n) {
        if (this.gpath != null && this.shape == 20) {
            if (n == 2) {
                this.gpath.closePath();
            }
            this.drawShape(this.gpath);
        }
        this.shape = 0;
    }
    
    @Override
    public void bezierVertex(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) {
        this.bezierVertexCheck();
        this.gpath.curveTo(x1, y1, x2, y2, x3, y3);
    }
    
    @Override
    public void bezierVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        PGraphics.showDepthWarningXYZ("bezierVertex");
    }
    
    @Override
    public void quadVertex(final float n, final float n2, final float n3, final float n4) {
        this.bezierVertexCheck();
        final Point2D currentPoint = this.gpath.getCurrentPoint();
        final float n5 = (float)currentPoint.getX();
        final float n6 = (float)currentPoint.getY();
        this.bezierVertex(n5 + (n - n5) * 2.0f / 3.0f, n6 + (n2 - n6) * 2.0f / 3.0f, n3 + (n - n3) * 2.0f / 3.0f, n4 + (n2 - n4) * 2.0f / 3.0f, n3, n4);
    }
    
    @Override
    public void quadVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        PGraphics.showDepthWarningXYZ("quadVertex");
    }
    
    @Override
    protected void curveVertexCheck() {
        super.curveVertexCheck();
        if (this.curveCoordX == null) {
            this.curveCoordX = new float[4];
            this.curveCoordY = new float[4];
            this.curveDrawX = new float[4];
            this.curveDrawY = new float[4];
        }
    }
    
    @Override
    protected void curveVertexSegment(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.curveCoordX[0] = n;
        this.curveCoordY[0] = n2;
        this.curveCoordX[1] = n3;
        this.curveCoordY[1] = n4;
        this.curveCoordX[2] = n5;
        this.curveCoordY[2] = n6;
        this.curveCoordX[3] = n7;
        this.curveCoordY[3] = n8;
        this.curveToBezierMatrix.mult(this.curveCoordX, this.curveDrawX);
        this.curveToBezierMatrix.mult(this.curveCoordY, this.curveDrawY);
        if (this.gpath == null) {
            (this.gpath = new GeneralPath()).moveTo(this.curveDrawX[0], this.curveDrawY[0]);
        }
        this.gpath.curveTo(this.curveDrawX[1], this.curveDrawY[1], this.curveDrawX[2], this.curveDrawY[2], this.curveDrawX[3], this.curveDrawY[3]);
    }
    
    @Override
    public void curveVertex(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("curveVertex");
    }
    
    @Override
    public void point(final float n, final float n2) {
        if (this.stroke) {
            this.line(n, n2, n + 1.0E-4f, n2 + 1.0E-4f);
        }
    }
    
    @Override
    public void line(final float x1, final float y1, final float x2, final float y2) {
        this.line.setLine(x1, y1, x2, y2);
        this.strokeShape(this.line);
    }
    
    @Override
    public void triangle(final float x, final float y, final float x2, final float y2, final float x3, final float y3) {
        (this.gpath = new GeneralPath()).moveTo(x, y);
        this.gpath.lineTo(x2, y2);
        this.gpath.lineTo(x3, y3);
        this.gpath.closePath();
        this.drawShape(this.gpath);
    }
    
    @Override
    public void quad(final float x, final float y, final float x2, final float y2, final float x3, final float y3, final float x4, final float y4) {
        final GeneralPath generalPath = new GeneralPath();
        generalPath.moveTo(x, y);
        generalPath.lineTo(x2, y2);
        generalPath.lineTo(x3, y3);
        generalPath.lineTo(x4, y4);
        generalPath.closePath();
        this.drawShape(generalPath);
    }
    
    @Override
    protected void rectImpl(final float n, final float n2, final float n3, final float n4) {
        this.rect.setFrame(n, n2, n3 - n, n4 - n2);
        this.drawShape(this.rect);
    }
    
    @Override
    protected void ellipseImpl(final float x, final float y, final float w, final float h) {
        this.ellipse.setFrame(x, y, w, h);
        this.drawShape(this.ellipse);
    }
    
    @Override
    protected void arcImpl(final float n, final float n2, final float n3, final float n4, float n5, float n6) {
        n5 = -n5 * 57.295776f;
        n6 = -n6 * 57.295776f;
        final float n7 = n6 - n5;
        if (this.fill) {
            this.arc.setArc(n, n2, n3, n4, n5, n7, 2);
            this.fillShape(this.arc);
        }
        if (this.stroke) {
            this.arc.setArc(n, n2, n3, n4, n5, n7, 0);
            this.strokeShape(this.arc);
        }
    }
    
    protected void fillShape(final Shape shape) {
        if (this.fillGradient) {
            this.g2.setPaint(this.fillGradientObject);
            this.g2.fill(shape);
        }
        else if (this.fill) {
            this.g2.setColor(this.fillColorObject);
            this.g2.fill(shape);
        }
    }
    
    protected void strokeShape(final Shape shape) {
        if (this.strokeGradient) {
            this.g2.setPaint(this.strokeGradientObject);
            this.g2.draw(shape);
        }
        else if (this.stroke) {
            this.g2.setColor(this.strokeColorObject);
            this.g2.draw(shape);
        }
    }
    
    protected void drawShape(final Shape shape) {
        if (this.fillGradient) {
            this.g2.setPaint(this.fillGradientObject);
            this.g2.fill(shape);
        }
        else if (this.fill) {
            this.g2.setColor(this.fillColorObject);
            this.g2.fill(shape);
        }
        if (this.strokeGradient) {
            this.g2.setPaint(this.strokeGradientObject);
            this.g2.draw(shape);
        }
        else if (this.stroke) {
            this.g2.setColor(this.strokeColorObject);
            this.g2.draw(shape);
        }
    }
    
    @Override
    public void box(final float n, final float n2, final float n3) {
        PGraphics.showMethodWarning("box");
    }
    
    @Override
    public void sphere(final float n) {
        PGraphics.showMethodWarning("sphere");
    }
    
    @Override
    public void bezierDetail(final int n) {
    }
    
    @Override
    public void curveDetail(final int n) {
    }
    
    @Override
    public void smooth() {
        this.smooth = true;
        this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    }
    
    @Override
    public void noSmooth() {
        this.smooth = false;
        this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        this.g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
    }
    
    @Override
    protected void imageImpl(final PImage pImage, final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8) {
        if (pImage.width <= 0 || pImage.height <= 0) {
            return;
        }
        if (pImage.getCache((PGraphics)this) == null) {
            pImage.setCache((PGraphics)this, (Object)new PGraphicsJava2D.ImageCache(this, pImage));
            pImage.updatePixels();
            pImage.modified = true;
        }
        final PGraphicsJava2D.ImageCache imageCache = (PGraphicsJava2D.ImageCache)pImage.getCache((PGraphics)this);
        if ((this.tint && !imageCache.tinted) || (this.tint && imageCache.tintedColor != this.tintColor) || (!this.tint && imageCache.tinted)) {
            pImage.updatePixels();
        }
        if (pImage.modified) {
            imageCache.update(this.tint, this.tintColor);
            pImage.modified = false;
        }
        this.g2.drawImage(((PGraphicsJava2D.ImageCache)pImage.getCache((PGraphics)this)).image, (int)n, (int)n2, (int)n3, (int)n4, n5, n6, n7, n8, null);
    }
    
    @Override
    public float textAscent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textAscent");
        }
        final Font font = this.textFont.getFont();
        if (font != null) {
            return (float)this.parent.getFontMetrics(font).getAscent();
        }
        return super.textAscent();
    }
    
    @Override
    public float textDescent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textAscent");
        }
        final Font font = this.textFont.getFont();
        if (font != null) {
            return (float)this.parent.getFontMetrics(font).getDescent();
        }
        return super.textDescent();
    }
    
    @Override
    protected boolean textModeCheck(final int n) {
        return n == 4 || n == 256;
    }
    
    @Override
    public void textSize(final float size) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textAscent", size);
        }
        final Font font = this.textFont.getFont();
        if (font != null) {
            final Font deriveFont = font.deriveFont(size);
            this.g2.setFont(deriveFont);
            this.textFont.setFont(deriveFont);
        }
        super.textSize(size);
    }
    
    @Override
    protected float textWidthImpl(final char[] data, final int off, final int n) {
        final Font font = this.textFont.getFont();
        if (font != null) {
            return (float)this.g2.getFontMetrics(font).charsWidth(data, off, n - off);
        }
        return super.textWidthImpl(data, off, n);
    }
    
    @Override
    protected void textLineImpl(final char[] data, final int offset, final int n, final float n2, final float textY) {
        if (this.textFont.getFont() != null) {
            Object o = this.g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            if (o == null) {
                o = RenderingHints.VALUE_ANTIALIAS_DEFAULT;
            }
            this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, this.textFont.smooth ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
            this.g2.setColor(this.fillColorObject);
            this.g2.drawChars(data, offset, n - offset, (int)(n2 + 0.5f), (int)(textY + 0.5f));
            this.g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, o);
            this.textX = n2 + this.textWidthImpl(data, offset, n);
            this.textY = textY;
            this.textZ = 0.0f;
        }
        else {
            super.textLineImpl(data, offset, n, n2, textY);
        }
    }
    
    @Override
    public void pushMatrix() {
        if (this.transformCount == this.transformStack.length) {
            throw new RuntimeException("pushMatrix() cannot use push more than " + this.transformStack.length + " times");
        }
        this.transformStack[this.transformCount] = this.g2.getTransform();
        ++this.transformCount;
    }
    
    @Override
    public void popMatrix() {
        if (this.transformCount == 0) {
            throw new RuntimeException("missing a popMatrix() to go with that pushMatrix()");
        }
        --this.transformCount;
        this.g2.setTransform(this.transformStack[this.transformCount]);
    }
    
    @Override
    public void translate(final float n, final float n2) {
        this.g2.translate(n, n2);
    }
    
    @Override
    public void rotate(final float n) {
        this.g2.rotate(n);
    }
    
    @Override
    public void rotateX(final float n) {
        PGraphics.showDepthWarning("rotateX");
    }
    
    @Override
    public void rotateY(final float n) {
        PGraphics.showDepthWarning("rotateY");
    }
    
    @Override
    public void rotateZ(final float n) {
        PGraphics.showDepthWarning("rotateZ");
    }
    
    @Override
    public void rotate(final float n, final float n2, final float n3, final float n4) {
        PGraphics.showVariationWarning("rotate");
    }
    
    @Override
    public void scale(final float n) {
        this.g2.scale(n, n);
    }
    
    @Override
    public void scale(final float n, final float n2) {
        this.g2.scale(n, n2);
    }
    
    @Override
    public void scale(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("scale");
    }
    
    public void skewX(final float n) {
        this.g2.shear(Math.tan(n), 0.0);
    }
    
    public void skewY(final float n) {
        this.g2.shear(0.0, Math.tan(n));
    }
    
    @Override
    public void resetMatrix() {
        this.g2.setTransform(new AffineTransform());
    }
    
    @Override
    public void applyMatrix(final float m00, final float m2, final float m3, final float m4, final float m5, final float m6) {
        this.g2.transform(new AffineTransform(m00, m4, m2, m5, m3, m6));
    }
    
    @Override
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        PGraphics.showVariationWarning("applyMatrix");
    }
    
    @Override
    public PMatrix getMatrix() {
        return (PMatrix)this.getMatrix((PMatrix2D)null);
    }
    
    @Override
    public PMatrix2D getMatrix(PMatrix2D pMatrix2D) {
        if (pMatrix2D == null) {
            pMatrix2D = new PMatrix2D();
        }
        this.g2.getTransform().getMatrix(this.transform);
        pMatrix2D.set((float)this.transform[0], (float)this.transform[2], (float)this.transform[4], (float)this.transform[1], (float)this.transform[3], (float)this.transform[5]);
        return pMatrix2D;
    }
    
    @Override
    public PMatrix3D getMatrix(final PMatrix3D pMatrix3D) {
        PGraphics.showVariationWarning("getMatrix");
        return pMatrix3D;
    }
    
    @Override
    public void setMatrix(final PMatrix2D pMatrix2D) {
        this.g2.setTransform(new AffineTransform(pMatrix2D.m00, pMatrix2D.m10, pMatrix2D.m01, pMatrix2D.m11, pMatrix2D.m02, pMatrix2D.m12));
    }
    
    @Override
    public void setMatrix(final PMatrix3D pMatrix3D) {
        PGraphics.showVariationWarning("setMatrix");
    }
    
    @Override
    public void printMatrix() {
        this.getMatrix((PMatrix2D)null).print();
    }
    
    @Override
    public float screenX(final float n, final float n2) {
        this.g2.getTransform().getMatrix(this.transform);
        return (float)this.transform[0] * n + (float)this.transform[2] * n2 + (float)this.transform[4];
    }
    
    @Override
    public float screenY(final float n, final float n2) {
        this.g2.getTransform().getMatrix(this.transform);
        return (float)this.transform[1] * n + (float)this.transform[3] * n2 + (float)this.transform[5];
    }
    
    @Override
    public float screenX(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("screenX");
        return 0.0f;
    }
    
    @Override
    public float screenY(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("screenY");
        return 0.0f;
    }
    
    @Override
    public float screenZ(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("screenZ");
        return 0.0f;
    }
    
    @Override
    public void strokeCap(final int n) {
        super.strokeCap(n);
        this.strokeImpl();
    }
    
    @Override
    public void strokeJoin(final int n) {
        super.strokeJoin(n);
        this.strokeImpl();
    }
    
    @Override
    public void strokeWeight(final float n) {
        super.strokeWeight(n);
        this.strokeImpl();
    }
    
    protected void strokeImpl() {
        int cap = 0;
        if (this.strokeCap == 2) {
            cap = 1;
        }
        else if (this.strokeCap == 4) {
            cap = 2;
        }
        int join = 2;
        if (this.strokeJoin == 8) {
            join = 0;
        }
        else if (this.strokeJoin == 2) {
            join = 1;
        }
        this.g2.setStroke(new BasicStroke(this.strokeWeight, cap, join));
    }
    
    @Override
    protected void strokeFromCalc() {
        super.strokeFromCalc();
        this.strokeColorObject = new Color(this.strokeColor, true);
        this.strokeGradient = false;
    }
    
    @Override
    protected void tintFromCalc() {
        super.tintFromCalc();
        this.tintColorObject = new Color(this.tintColor, true);
    }
    
    @Override
    protected void fillFromCalc() {
        super.fillFromCalc();
        this.fillColorObject = new Color(this.fillColor, true);
        this.fillGradient = false;
    }
    
    public void backgroundImpl() {
        if (this.backgroundAlpha) {
            final WritableRaster raster = ((BufferedImage)this.image).getRaster();
            if (this.clearPixels == null || this.clearPixels.length < this.width) {
                this.clearPixels = new int[this.width];
            }
            Arrays.fill(this.clearPixels, this.backgroundColor);
            for (int i = 0; i < this.height; ++i) {
                raster.setDataElements(0, i, this.width, 1, this.clearPixels);
            }
        }
        else {
            this.pushMatrix();
            this.resetMatrix();
            this.g2.setColor(new Color(this.backgroundColor));
            this.g2.fillRect(0, 0, this.width, this.height);
            this.popMatrix();
        }
    }
    
    @Override
    public void beginRaw(final PGraphics pGraphics) {
        PGraphics.showMethodWarning("beginRaw");
    }
    
    @Override
    public void endRaw() {
        PGraphics.showMethodWarning("endRaw");
    }
    
    public void loadPixels() {
        if (this.pixels == null || this.pixels.length != this.width * this.height) {
            this.pixels = new int[this.width * this.height];
        }
        ((BufferedImage)(this.primarySurface ? this.offscreen : this.image)).getRaster().getDataElements(0, 0, this.width, this.height, this.pixels);
    }
    
    public void updatePixels() {
        ((BufferedImage)(this.primarySurface ? this.offscreen : this.image)).getRaster().setDataElements(0, 0, this.width, this.height, this.pixels);
    }
    
    public void updatePixels(final int n, final int n2, final int n3, final int n4) {
        if (n != 0 || n2 != 0 || n3 != this.width || n4 != this.height) {
            PGraphics.showVariationWarning("updatePixels(x, y, w, h)");
        }
        this.updatePixels();
    }
    
    public int get(final int x, final int y) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            return 0;
        }
        ((BufferedImage)(this.primarySurface ? this.offscreen : this.image)).getRaster().getDataElements(x, y, PGraphicsJava2D.getset);
        return PGraphicsJava2D.getset[0];
    }
    
    public PImage getImpl(final int x, final int y, final int w, final int h) {
        final PImage pImage = new PImage(w, h);
        pImage.parent = this.parent;
        ((BufferedImage)(this.primarySurface ? this.offscreen : this.image)).getRaster().getDataElements(x, y, w, h, pImage.pixels);
        return pImage;
    }
    
    public PImage get() {
        return this.get(0, 0, this.width, this.height);
    }
    
    public void set(final int x, final int y, final int n) {
        if (x < 0 || y < 0 || x >= this.width || y >= this.height) {
            return;
        }
        PGraphicsJava2D.getset[0] = n;
        ((BufferedImage)(this.primarySurface ? this.offscreen : this.image)).getRaster().setDataElements(x, y, PGraphicsJava2D.getset);
    }
    
    protected void setImpl(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final PImage pImage) {
        final WritableRaster raster = ((BufferedImage)(this.primarySurface ? this.offscreen : this.image)).getRaster();
        if (n3 == 0 && n4 == 0 && n5 == pImage.width && n6 == pImage.height) {
            raster.setDataElements(n, n2, pImage.width, pImage.height, pImage.pixels);
        }
        else {
            final PImage value = pImage.get(n3, n4, n5, n6);
            raster.setDataElements(n, n2, value.width, value.height, value.pixels);
        }
    }
    
    public void mask(final int[] array) {
        PGraphics.showMethodWarning("mask");
    }
    
    public void mask(final PImage pImage) {
        PGraphics.showMethodWarning("mask");
    }
    
    public void copy(final int n, final int n2, final int n3, final int n4, int n5, int n6, final int n7, final int n8) {
        if (n3 != n7 || n4 != n8) {
            this.copy((PImage)this, n, n2, n3, n4, n5, n6, n7, n8);
        }
        else {
            n5 -= n;
            n6 -= n2;
            this.g2.copyArea(n, n2, n3, n4, n5, n6);
        }
    }
    
    static {
        PGraphicsJava2D.getset = new int[1];
    }
}
 