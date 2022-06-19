package processing.core;

import java.awt.Color;
import java.util.HashMap;
import java.awt.Image;

public class PGraphics extends PImage implements PConstants
{
    protected int width1;
    protected int height1;
    public int pixelCount;
    public boolean smooth;
    protected boolean settingsInited;
    protected PGraphics raw;
    protected String path;
    protected boolean primarySurface;
    protected boolean[] hints;
    public int colorMode;
    public float colorModeX;
    public float colorModeY;
    public float colorModeZ;
    public float colorModeA;
    boolean colorModeScale;
    boolean colorModeDefault;
    public boolean tint;
    public int tintColor;
    protected boolean tintAlpha;
    protected float tintR;
    protected float tintG;
    protected float tintB;
    protected float tintA;
    protected int tintRi;
    protected int tintGi;
    protected int tintBi;
    protected int tintAi;
    public boolean fill;
    public int fillColor;
    protected boolean fillAlpha;
    protected float fillR;
    protected float fillG;
    protected float fillB;
    protected float fillA;
    protected int fillRi;
    protected int fillGi;
    protected int fillBi;
    protected int fillAi;
    public boolean stroke;
    public int strokeColor;
    protected boolean strokeAlpha;
    protected float strokeR;
    protected float strokeG;
    protected float strokeB;
    protected float strokeA;
    protected int strokeRi;
    protected int strokeGi;
    protected int strokeBi;
    protected int strokeAi;
    protected static final float DEFAULT_STROKE_WEIGHT = 1.0f;
    protected static final int DEFAULT_STROKE_JOIN = 8;
    protected static final int DEFAULT_STROKE_CAP = 2;
    public float strokeWeight;
    public int strokeJoin;
    public int strokeCap;
    public int rectMode;
    public int ellipseMode;
    public int shapeMode;
    public int imageMode;
    public PFont textFont;
    public int textAlign;
    public int textAlignY;
    public int textMode;
    public float textSize;
    public float textLeading;
    public float ambientR;
    public float ambientG;
    public float ambientB;
    public float specularR;
    public float specularG;
    public float specularB;
    public float emissiveR;
    public float emissiveG;
    public float emissiveB;
    public float shininess;
    static final int STYLE_STACK_DEPTH = 64;
    PStyle[] styleStack;
    int styleStackDepth;
    public int backgroundColor;
    protected boolean backgroundAlpha;
    protected float backgroundR;
    protected float backgroundG;
    protected float backgroundB;
    protected float backgroundA;
    protected int backgroundRi;
    protected int backgroundGi;
    protected int backgroundBi;
    protected int backgroundAi;
    static final int MATRIX_STACK_DEPTH = 32;
    public Image image;
    protected float calcR;
    protected float calcG;
    protected float calcB;
    protected float calcA;
    protected int calcRi;
    protected int calcGi;
    protected int calcBi;
    protected int calcAi;
    protected int calcColor;
    protected boolean calcAlpha;
    int cacheHsbKey;
    float[] cacheHsbValue;
    protected int shape;
    public static final int DEFAULT_VERTICES = 512;
    protected float[][] vertices;
    protected int vertexCount;
    protected boolean bezierInited;
    public int bezierDetail;
    protected PMatrix3D bezierBasisMatrix;
    protected PMatrix3D bezierDrawMatrix;
    protected boolean curveInited;
    protected int curveDetail;
    public float curveTightness;
    protected PMatrix3D curveBasisMatrix;
    protected PMatrix3D curveDrawMatrix;
    protected PMatrix3D bezierBasisInverse;
    protected PMatrix3D curveToBezierMatrix;
    protected float[][] curveVertices;
    protected int curveVertexCount;
    protected static final float[] sinLUT;
    protected static final float[] cosLUT;
    protected static final float SINCOS_PRECISION = 0.5f;
    protected static final int SINCOS_LENGTH = 720;
    protected float textX;
    protected float textY;
    protected float textZ;
    protected char[] textBuffer;
    protected char[] textWidthBuffer;
    protected int textBreakCount;
    protected int[] textBreakStart;
    protected int[] textBreakStop;
    public boolean edge;
    protected static final int NORMAL_MODE_AUTO = 0;
    protected static final int NORMAL_MODE_SHAPE = 1;
    protected static final int NORMAL_MODE_VERTEX = 2;
    protected int normalMode;
    protected boolean autoNormal;
    public float normalX;
    public float normalY;
    public float normalZ;
    public int textureMode;
    public float textureU;
    public float textureV;
    public PImage textureImage;
    float[] sphereX;
    float[] sphereY;
    float[] sphereZ;
    public int sphereDetailU;
    public int sphereDetailV;
    static float[] lerpColorHSB1;
    static float[] lerpColorHSB2;
    protected static HashMap<String, Object> warnings;
    
    public PGraphics() {
        this.smooth = false;
        this.hints = new boolean[10];
        this.fillColor = -1;
        this.strokeColor = -16777216;
        this.strokeWeight = 1.0f;
        this.strokeJoin = 8;
        this.strokeCap = 2;
        this.imageMode = 0;
        this.textAlign = 37;
        this.textAlignY = 0;
        this.textMode = 4;
        this.styleStack = new PStyle[64];
        this.backgroundColor = -3355444;
        this.cacheHsbValue = new float[3];
        this.vertices = new float[512][37];
        this.bezierInited = false;
        this.bezierDetail = 20;
        this.bezierBasisMatrix = new PMatrix3D(-1.0f, 3.0f, -3.0f, 1.0f, 3.0f, -6.0f, 3.0f, 0.0f, -3.0f, 3.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f);
        this.curveInited = false;
        this.curveDetail = 20;
        this.curveTightness = 0.0f;
        this.textBuffer = new char[8192];
        this.textWidthBuffer = new char[8192];
        this.edge = true;
        this.sphereDetailU = 0;
        this.sphereDetailV = 0;
    }
    
    public void setParent(final PApplet parent) {
        this.parent = parent;
    }
    
    public void setPrimary(final boolean primarySurface) {
        this.primarySurface = primarySurface;
        if (this.primarySurface) {
            this.format = 1;
        }
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.width1 = this.width - 1;
        this.height1 = this.height - 1;
        this.allocate();
        this.reapplySettings();
    }
    
    protected void allocate() {
    }
    
    public void dispose() {
    }
    
    public boolean canDraw() {
        return true;
    }
    
    public void beginDraw() {
    }
    
    public void endDraw() {
    }
    
    public void flush() {
    }
    
    protected void checkSettings() {
        if (!this.settingsInited) {
            this.defaultSettings();
        }
    }
    
    protected void defaultSettings() {
        this.noSmooth();
        this.colorMode(1, 255.0f);
        this.fill(255);
        this.stroke(0);
        this.strokeWeight(1.0f);
        this.strokeJoin(8);
        this.strokeCap(2);
        this.rectMode(this.shape = 0);
        this.ellipseMode(3);
        this.autoNormal = true;
        this.textFont = null;
        this.textSize = 12.0f;
        this.textLeading = 14.0f;
        this.textAlign = 37;
        this.textMode = 4;
        if (this.primarySurface) {
            this.background(this.backgroundColor);
        }
        this.settingsInited = true;
    }
    
    protected void reapplySettings() {
        if (!this.settingsInited) {
            return;
        }
        this.colorMode(this.colorMode, this.colorModeX, this.colorModeY, this.colorModeZ);
        if (this.fill) {
            this.fill(this.fillColor);
        }
        else {
            this.noFill();
        }
        if (this.stroke) {
            this.stroke(this.strokeColor);
            this.strokeWeight(this.strokeWeight);
            this.strokeCap(this.strokeCap);
            this.strokeJoin(this.strokeJoin);
        }
        else {
            this.noStroke();
        }
        if (this.tint) {
            this.tint(this.tintColor);
        }
        else {
            this.noTint();
        }
        if (this.smooth) {
            this.smooth();
        }
        else {
            this.noSmooth();
        }
        if (this.textFont != null) {
            final float textLeading = this.textLeading;
            this.textFont(this.textFont, this.textSize);
            this.textLeading(textLeading);
        }
        this.textMode(this.textMode);
        this.textAlign(this.textAlign, this.textAlignY);
        this.background(this.backgroundColor);
    }
    
    public void hint(final int n) {
        if (n > 0) {
            this.hints[n] = true;
        }
        else {
            this.hints[-n] = false;
        }
    }
    
    public boolean hintEnabled(final int n) {
        if (n > 0) {
            return this.hints[n];
        }
        return this.hints[-n];
    }
    
    public void beginShape() {
        this.beginShape(20);
    }
    
    public void beginShape(final int shape) {
        this.shape = shape;
    }
    
    public void edge(final boolean edge) {
        this.edge = edge;
    }
    
    public void normal(final float normalX, final float normalY, final float normalZ) {
        this.normalX = normalX;
        this.normalY = normalY;
        this.normalZ = normalZ;
        if (this.shape != 0) {
            if (this.normalMode == 0) {
                this.normalMode = 1;
            }
            else if (this.normalMode == 1) {
                this.normalMode = 2;
            }
        }
    }
    
    public void textureMode(final int textureMode) {
        this.textureMode = textureMode;
    }
    
    public void texture(final PImage textureImage) {
        this.textureImage = textureImage;
    }
    
    public void noTexture() {
        this.textureImage = null;
    }
    
    protected void vertexCheck() {
        if (this.vertexCount == this.vertices.length) {
            final float[][] vertices = new float[this.vertexCount << 1][37];
            System.arraycopy(this.vertices, 0, vertices, 0, this.vertexCount);
            this.vertices = vertices;
        }
    }
    
    public void vertex(final float n, final float n2) {
        this.vertexCheck();
        final float[] array = this.vertices[this.vertexCount];
        array[this.curveVertexCount = 0] = n;
        array[1] = n2;
        array[12] = (this.edge ? 1.0f : 0.0f);
        final boolean b = this.textureImage != null;
        if (this.fill || b) {
            if (this.textureImage == null) {
                array[3] = this.fillR;
                array[4] = this.fillG;
                array[5] = this.fillB;
                array[6] = this.fillA;
            }
            else if (this.tint) {
                array[3] = this.tintR;
                array[4] = this.tintG;
                array[5] = this.tintB;
                array[6] = this.tintA;
            }
            else {
                array[4] = (array[3] = 1.0f);
                array[6] = (array[5] = 1.0f);
            }
        }
        if (this.stroke) {
            array[13] = this.strokeR;
            array[14] = this.strokeG;
            array[15] = this.strokeB;
            array[16] = this.strokeA;
            array[17] = this.strokeWeight;
        }
        if (b) {
            array[7] = this.textureU;
            array[8] = this.textureV;
        }
        if (this.autoNormal) {
            final float n3 = this.normalX * this.normalX + this.normalY * this.normalY + this.normalZ * this.normalZ;
            if (n3 < 1.0E-4f) {
                array[36] = 0.0f;
            }
            else {
                if (Math.abs(n3 - 1.0f) > 1.0E-4f) {
                    final float sqrt = PApplet.sqrt(n3);
                    this.normalX /= sqrt;
                    this.normalY /= sqrt;
                    this.normalZ /= sqrt;
                }
                array[36] = 1.0f;
            }
        }
        else {
            array[36] = 1.0f;
        }
        ++this.vertexCount;
    }
    
    public void vertex(final float n, final float n2, final float n3) {
        this.vertexCheck();
        final float[] array = this.vertices[this.vertexCount];
        if (this.shape == 20 && this.vertexCount > 0) {
            final float[] array2 = this.vertices[this.vertexCount - 1];
            if (Math.abs(array2[0] - n) < 1.0E-4f && Math.abs(array2[1] - n2) < 1.0E-4f && Math.abs(array2[2] - n3) < 1.0E-4f) {
                return;
            }
        }
        array[this.curveVertexCount = 0] = n;
        array[1] = n2;
        array[2] = n3;
        array[12] = (this.edge ? 1.0f : 0.0f);
        final boolean b = this.textureImage != null;
        if (this.fill || b) {
            if (this.textureImage == null) {
                array[3] = this.fillR;
                array[4] = this.fillG;
                array[5] = this.fillB;
                array[6] = this.fillA;
            }
            else if (this.tint) {
                array[3] = this.tintR;
                array[4] = this.tintG;
                array[5] = this.tintB;
                array[6] = this.tintA;
            }
            else {
                array[4] = (array[3] = 1.0f);
                array[6] = (array[5] = 1.0f);
            }
            array[25] = this.ambientR;
            array[26] = this.ambientG;
            array[27] = this.ambientB;
            array[28] = this.specularR;
            array[29] = this.specularG;
            array[30] = this.specularB;
            array[31] = this.shininess;
            array[32] = this.emissiveR;
            array[33] = this.emissiveG;
            array[34] = this.emissiveB;
        }
        if (this.stroke) {
            array[13] = this.strokeR;
            array[14] = this.strokeG;
            array[15] = this.strokeB;
            array[16] = this.strokeA;
            array[17] = this.strokeWeight;
        }
        if (b) {
            array[7] = this.textureU;
            array[8] = this.textureV;
        }
        if (this.autoNormal) {
            final float n4 = this.normalX * this.normalX + this.normalY * this.normalY + this.normalZ * this.normalZ;
            if (n4 < 1.0E-4f) {
                array[36] = 0.0f;
            }
            else {
                if (Math.abs(n4 - 1.0f) > 1.0E-4f) {
                    final float sqrt = PApplet.sqrt(n4);
                    this.normalX /= sqrt;
                    this.normalY /= sqrt;
                    this.normalZ /= sqrt;
                }
                array[36] = 1.0f;
            }
        }
        else {
            array[36] = 1.0f;
        }
        array[9] = this.normalX;
        array[10] = this.normalY;
        array[11] = this.normalZ;
        array[35] = 0.0f;
        ++this.vertexCount;
    }
    
    public void vertexFields(final float[] array) {
        this.vertexCheck();
        System.arraycopy(array, this.curveVertexCount = 0, this.vertices[this.vertexCount], 0, 37);
        ++this.vertexCount;
    }
    
    public void vertex(final float n, final float n2, final float n3, final float n4) {
        this.vertexTexture(n3, n4);
        this.vertex(n, n2);
    }
    
    public void vertex(final float n, final float n2, final float n3, final float n4, final float n5) {
        this.vertexTexture(n4, n5);
        this.vertex(n, n2, n3);
    }
    
    protected void vertexTexture(float textureU, float textureV) {
        if (this.textureImage == null) {
            throw new RuntimeException("You must first call texture() before using u and v coordinates with vertex()");
        }
        if (this.textureMode == 2) {
            textureU /= this.textureImage.width;
            textureV /= this.textureImage.height;
        }
        this.textureU = textureU;
        this.textureV = textureV;
        if (this.textureU < 0.0f) {
            this.textureU = 0.0f;
        }
        else if (this.textureU > 1.0f) {
            this.textureU = 1.0f;
        }
        if (this.textureV < 0.0f) {
            this.textureV = 0.0f;
        }
        else if (this.textureV > 1.0f) {
            this.textureV = 1.0f;
        }
    }
    
    public void breakShape() {
        showWarning("This renderer cannot currently handle concave shapes, or shapes with holes.");
    }
    
    public void endShape() {
        this.endShape(1);
    }
    
    public void endShape(final int n) {
    }
    
    protected void bezierVertexCheck() {
        if (this.shape == 0 || this.shape != 20) {
            throw new RuntimeException("beginShape() or beginShape(POLYGON) must be used before bezierVertex() or quadVertex()");
        }
        if (this.vertexCount == 0) {
            throw new RuntimeException("vertex() must be used at least oncebefore bezierVertex() or quadVertex()");
        }
    }
    
    public void bezierVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.bezierInitCheck();
        this.bezierVertexCheck();
        final PMatrix3D bezierDrawMatrix = this.bezierDrawMatrix;
        final float[] array = this.vertices[this.vertexCount - 1];
        float n7 = array[0];
        float n8 = array[1];
        float n9 = bezierDrawMatrix.m10 * n7 + bezierDrawMatrix.m11 * n + bezierDrawMatrix.m12 * n3 + bezierDrawMatrix.m13 * n5;
        float n10 = bezierDrawMatrix.m20 * n7 + bezierDrawMatrix.m21 * n + bezierDrawMatrix.m22 * n3 + bezierDrawMatrix.m23 * n5;
        final float n11 = bezierDrawMatrix.m30 * n7 + bezierDrawMatrix.m31 * n + bezierDrawMatrix.m32 * n3 + bezierDrawMatrix.m33 * n5;
        float n12 = bezierDrawMatrix.m10 * n8 + bezierDrawMatrix.m11 * n2 + bezierDrawMatrix.m12 * n4 + bezierDrawMatrix.m13 * n6;
        float n13 = bezierDrawMatrix.m20 * n8 + bezierDrawMatrix.m21 * n2 + bezierDrawMatrix.m22 * n4 + bezierDrawMatrix.m23 * n6;
        final float n14 = bezierDrawMatrix.m30 * n8 + bezierDrawMatrix.m31 * n2 + bezierDrawMatrix.m32 * n4 + bezierDrawMatrix.m33 * n6;
        for (int i = 0; i < this.bezierDetail; ++i) {
            n7 += n9;
            n9 += n10;
            n10 += n11;
            n8 += n12;
            n12 += n13;
            n13 += n14;
            this.vertex(n7, n8);
        }
    }
    
    public void bezierVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        this.bezierInitCheck();
        this.bezierVertexCheck();
        final PMatrix3D bezierDrawMatrix = this.bezierDrawMatrix;
        final float[] array = this.vertices[this.vertexCount - 1];
        float n10 = array[0];
        float n11 = array[1];
        float n12 = array[2];
        float n13 = bezierDrawMatrix.m10 * n10 + bezierDrawMatrix.m11 * n + bezierDrawMatrix.m12 * n4 + bezierDrawMatrix.m13 * n7;
        float n14 = bezierDrawMatrix.m20 * n10 + bezierDrawMatrix.m21 * n + bezierDrawMatrix.m22 * n4 + bezierDrawMatrix.m23 * n7;
        final float n15 = bezierDrawMatrix.m30 * n10 + bezierDrawMatrix.m31 * n + bezierDrawMatrix.m32 * n4 + bezierDrawMatrix.m33 * n7;
        float n16 = bezierDrawMatrix.m10 * n11 + bezierDrawMatrix.m11 * n2 + bezierDrawMatrix.m12 * n5 + bezierDrawMatrix.m13 * n8;
        float n17 = bezierDrawMatrix.m20 * n11 + bezierDrawMatrix.m21 * n2 + bezierDrawMatrix.m22 * n5 + bezierDrawMatrix.m23 * n8;
        final float n18 = bezierDrawMatrix.m30 * n11 + bezierDrawMatrix.m31 * n2 + bezierDrawMatrix.m32 * n5 + bezierDrawMatrix.m33 * n8;
        float n19 = bezierDrawMatrix.m10 * n12 + bezierDrawMatrix.m11 * n3 + bezierDrawMatrix.m12 * n6 + bezierDrawMatrix.m13 * n9;
        float n20 = bezierDrawMatrix.m20 * n12 + bezierDrawMatrix.m21 * n3 + bezierDrawMatrix.m22 * n6 + bezierDrawMatrix.m23 * n9;
        final float n21 = bezierDrawMatrix.m30 * n12 + bezierDrawMatrix.m31 * n3 + bezierDrawMatrix.m32 * n6 + bezierDrawMatrix.m33 * n9;
        for (int i = 0; i < this.bezierDetail; ++i) {
            n10 += n13;
            n13 += n14;
            n14 += n15;
            n11 += n16;
            n16 += n17;
            n17 += n18;
            n12 += n19;
            n19 += n20;
            n20 += n21;
            this.vertex(n10, n11, n12);
        }
    }
    
    public void quadVertex(final float n, final float n2, final float n3, final float n4) {
        final float[] array = this.vertices[this.vertexCount - 1];
        final float n5 = array[0];
        final float n6 = array[1];
        this.bezierVertex(n5 + (n - n5) * 2.0f / 3.0f, n6 + (n2 - n6) * 2.0f / 3.0f, n3 + (n - n3) * 2.0f / 3.0f, n4 + (n2 - n4) * 2.0f / 3.0f, n3, n4);
    }
    
    public void quadVertex(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float[] array = this.vertices[this.vertexCount - 1];
        final float n7 = array[0];
        final float n8 = array[1];
        final float n9 = array[2];
        this.bezierVertex(n7 + (n - n7) * 2.0f / 3.0f, n8 + (n2 - n8) * 2.0f / 3.0f, n9 + (n3 - n9) * 2.0f / 3.0f, n4 + (n - n4) * 2.0f / 3.0f, n5 + (n2 - n5) * 2.0f / 3.0f, n6 + (n3 - n6) * 2.0f / 3.0f, n4, n5, n6);
    }
    
    protected void curveVertexCheck() {
        if (this.shape != 20) {
            throw new RuntimeException("You must use beginShape() or beginShape(POLYGON) before curveVertex()");
        }
        if (this.curveVertices == null) {
            this.curveVertices = new float[128][3];
        }
        if (this.curveVertexCount == this.curveVertices.length) {
            final float[][] curveVertices = new float[this.curveVertexCount << 1][3];
            System.arraycopy(this.curveVertices, 0, curveVertices, 0, this.curveVertexCount);
            this.curveVertices = curveVertices;
        }
        this.curveInitCheck();
    }
    
    public void curveVertex(final float n, final float n2) {
        this.curveVertexCheck();
        final float[] array = this.curveVertices[this.curveVertexCount];
        array[0] = n;
        array[1] = n2;
        ++this.curveVertexCount;
        if (this.curveVertexCount > 3) {
            this.curveVertexSegment(this.curveVertices[this.curveVertexCount - 4][0], this.curveVertices[this.curveVertexCount - 4][1], this.curveVertices[this.curveVertexCount - 3][0], this.curveVertices[this.curveVertexCount - 3][1], this.curveVertices[this.curveVertexCount - 2][0], this.curveVertices[this.curveVertexCount - 2][1], this.curveVertices[this.curveVertexCount - 1][0], this.curveVertices[this.curveVertexCount - 1][1]);
        }
    }
    
    public void curveVertex(final float n, final float n2, final float n3) {
        this.curveVertexCheck();
        final float[] array = this.curveVertices[this.curveVertexCount];
        array[0] = n;
        array[1] = n2;
        array[2] = n3;
        ++this.curveVertexCount;
        if (this.curveVertexCount > 3) {
            this.curveVertexSegment(this.curveVertices[this.curveVertexCount - 4][0], this.curveVertices[this.curveVertexCount - 4][1], this.curveVertices[this.curveVertexCount - 4][2], this.curveVertices[this.curveVertexCount - 3][0], this.curveVertices[this.curveVertexCount - 3][1], this.curveVertices[this.curveVertexCount - 3][2], this.curveVertices[this.curveVertexCount - 2][0], this.curveVertices[this.curveVertexCount - 2][1], this.curveVertices[this.curveVertexCount - 2][2], this.curveVertices[this.curveVertexCount - 1][0], this.curveVertices[this.curveVertexCount - 1][1], this.curveVertices[this.curveVertexCount - 1][2]);
        }
    }
    
    protected void curveVertexSegment(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        float n9 = n3;
        float n10 = n4;
        final PMatrix3D curveDrawMatrix = this.curveDrawMatrix;
        float n11 = curveDrawMatrix.m10 * n + curveDrawMatrix.m11 * n3 + curveDrawMatrix.m12 * n5 + curveDrawMatrix.m13 * n7;
        float n12 = curveDrawMatrix.m20 * n + curveDrawMatrix.m21 * n3 + curveDrawMatrix.m22 * n5 + curveDrawMatrix.m23 * n7;
        final float n13 = curveDrawMatrix.m30 * n + curveDrawMatrix.m31 * n3 + curveDrawMatrix.m32 * n5 + curveDrawMatrix.m33 * n7;
        float n14 = curveDrawMatrix.m10 * n2 + curveDrawMatrix.m11 * n4 + curveDrawMatrix.m12 * n6 + curveDrawMatrix.m13 * n8;
        float n15 = curveDrawMatrix.m20 * n2 + curveDrawMatrix.m21 * n4 + curveDrawMatrix.m22 * n6 + curveDrawMatrix.m23 * n8;
        final float n16 = curveDrawMatrix.m30 * n2 + curveDrawMatrix.m31 * n4 + curveDrawMatrix.m32 * n6 + curveDrawMatrix.m33 * n8;
        final int curveVertexCount = this.curveVertexCount;
        this.vertex(n9, n10);
        for (int i = 0; i < this.curveDetail; ++i) {
            n9 += n11;
            n11 += n12;
            n12 += n13;
            n10 += n14;
            n14 += n15;
            n15 += n16;
            this.vertex(n9, n10);
        }
        this.curveVertexCount = curveVertexCount;
    }
    
    protected void curveVertexSegment(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        float n13 = n4;
        float n14 = n5;
        float n15 = n6;
        final PMatrix3D curveDrawMatrix = this.curveDrawMatrix;
        float n16 = curveDrawMatrix.m10 * n + curveDrawMatrix.m11 * n4 + curveDrawMatrix.m12 * n7 + curveDrawMatrix.m13 * n10;
        float n17 = curveDrawMatrix.m20 * n + curveDrawMatrix.m21 * n4 + curveDrawMatrix.m22 * n7 + curveDrawMatrix.m23 * n10;
        final float n18 = curveDrawMatrix.m30 * n + curveDrawMatrix.m31 * n4 + curveDrawMatrix.m32 * n7 + curveDrawMatrix.m33 * n10;
        float n19 = curveDrawMatrix.m10 * n2 + curveDrawMatrix.m11 * n5 + curveDrawMatrix.m12 * n8 + curveDrawMatrix.m13 * n11;
        float n20 = curveDrawMatrix.m20 * n2 + curveDrawMatrix.m21 * n5 + curveDrawMatrix.m22 * n8 + curveDrawMatrix.m23 * n11;
        final float n21 = curveDrawMatrix.m30 * n2 + curveDrawMatrix.m31 * n5 + curveDrawMatrix.m32 * n8 + curveDrawMatrix.m33 * n11;
        final int curveVertexCount = this.curveVertexCount;
        float n22 = curveDrawMatrix.m10 * n3 + curveDrawMatrix.m11 * n6 + curveDrawMatrix.m12 * n9 + curveDrawMatrix.m13 * n12;
        float n23 = curveDrawMatrix.m20 * n3 + curveDrawMatrix.m21 * n6 + curveDrawMatrix.m22 * n9 + curveDrawMatrix.m23 * n12;
        final float n24 = curveDrawMatrix.m30 * n3 + curveDrawMatrix.m31 * n6 + curveDrawMatrix.m32 * n9 + curveDrawMatrix.m33 * n12;
        this.vertex(n13, n14, n15);
        for (int i = 0; i < this.curveDetail; ++i) {
            n13 += n16;
            n16 += n17;
            n17 += n18;
            n14 += n19;
            n19 += n20;
            n20 += n21;
            n15 += n22;
            n22 += n23;
            n23 += n24;
            this.vertex(n13, n14, n15);
        }
        this.curveVertexCount = curveVertexCount;
    }
    
    public void point(final float n, final float n2) {
        this.beginShape(2);
        this.vertex(n, n2);
        this.endShape();
    }
    
    public void point(final float n, final float n2, final float n3) {
        this.beginShape(2);
        this.vertex(n, n2, n3);
        this.endShape();
    }
    
    public void line(final float n, final float n2, final float n3, final float n4) {
        this.beginShape(4);
        this.vertex(n, n2);
        this.vertex(n3, n4);
        this.endShape();
    }
    
    public void line(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.beginShape(4);
        this.vertex(n, n2, n3);
        this.vertex(n4, n5, n6);
        this.endShape();
    }
    
    public void triangle(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.beginShape(9);
        this.vertex(n, n2);
        this.vertex(n3, n4);
        this.vertex(n5, n6);
        this.endShape();
    }
    
    public void quad(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.beginShape(16);
        this.vertex(n, n2);
        this.vertex(n3, n4);
        this.vertex(n5, n6);
        this.vertex(n7, n8);
        this.endShape();
    }
    
    public void rectMode(final int rectMode) {
        this.rectMode = rectMode;
    }
    
    public void rect(float n, float n2, float n3, float n4) {
        switch (this.rectMode) {
            case 0: {
                n3 += n;
                n4 += n2;
                break;
            }
            case 2: {
                final float n5 = n3;
                final float n6 = n4;
                n3 = n + n5;
                n4 = n2 + n6;
                n -= n5;
                n2 -= n6;
                break;
            }
            case 3: {
                final float n7 = n3 / 2.0f;
                final float n8 = n4 / 2.0f;
                n3 = n + n7;
                n4 = n2 + n8;
                n -= n7;
                n2 -= n8;
                break;
            }
        }
        if (n > n3) {
            final float n9 = n;
            n = n3;
            n3 = n9;
        }
        if (n2 > n4) {
            final float n10 = n2;
            n2 = n4;
            n4 = n10;
        }
        this.rectImpl(n, n2, n3, n4);
    }
    
    protected void rectImpl(final float n, final float n2, final float n3, final float n4) {
        this.quad(n, n2, n3, n2, n3, n4, n, n4);
    }
    
    private void quadraticVertex(final float n, final float n2, final float n3, final float n4) {
        final float[] array = this.vertices[this.vertexCount - 1];
        final float n5 = array[0];
        final float n6 = array[1];
        final float n7 = n5 + 0.6666667f * (n - n5);
        final float n8 = n6 + 0.6666667f * (n2 - n6);
        this.bezierVertex(n7, n8, n7 + (n3 - n5) / 3.0f, n8 + (n4 - n6) / 3.0f, n3, n4);
    }
    
    public void rect(float n, float n2, float n3, float n4, final float n5, final float n6) {
        switch (this.rectMode) {
            case 0: {
                n3 += n;
                n4 += n2;
                break;
            }
            case 2: {
                final float n7 = n3;
                final float n8 = n4;
                n3 = n + n7;
                n4 = n2 + n8;
                n -= n7;
                n2 -= n8;
                break;
            }
            case 3: {
                final float n9 = n3 / 2.0f;
                final float n10 = n4 / 2.0f;
                n3 = n + n9;
                n4 = n2 + n10;
                n -= n9;
                n2 -= n10;
                break;
            }
        }
        if (n > n3) {
            final float n11 = n;
            n = n3;
            n3 = n11;
        }
        if (n2 > n4) {
            final float n12 = n2;
            n2 = n4;
            n4 = n12;
        }
        this.rectImpl(n, n2, n3, n4, n5, n6);
    }
    
    protected void rectImpl(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.beginShape();
        this.vertex(n3 - n5, n2);
        this.quadraticVertex(n3, n2, n3, n2 + n6);
        this.vertex(n3, n4 - n6);
        this.quadraticVertex(n3, n4, n3 - n5, n4);
        this.vertex(n + n5, n4);
        this.quadraticVertex(n, n4, n, n4 - n6);
        this.vertex(n, n2 + n6);
        this.quadraticVertex(n, n2, n + n5, n2);
        this.endShape(2);
    }
    
    public void rect(float n, float n2, float n3, float n4, final float n5, final float n6, final float n7, final float n8) {
        switch (this.rectMode) {
            case 0: {
                n3 += n;
                n4 += n2;
                break;
            }
            case 2: {
                final float n9 = n3;
                final float n10 = n4;
                n3 = n + n9;
                n4 = n2 + n10;
                n -= n9;
                n2 -= n10;
                break;
            }
            case 3: {
                final float n11 = n3 / 2.0f;
                final float n12 = n4 / 2.0f;
                n3 = n + n11;
                n4 = n2 + n12;
                n -= n11;
                n2 -= n12;
                break;
            }
        }
        if (n > n3) {
            final float n13 = n;
            n = n3;
            n3 = n13;
        }
        if (n2 > n4) {
            final float n14 = n2;
            n2 = n4;
            n4 = n14;
        }
        this.rectImpl(n, n2, n3, n4, n5, n6, n7, n8);
    }
    
    protected void rectImpl(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.beginShape();
        if (n6 != 0.0f) {
            this.vertex(n3 - n6, n2);
            this.quadraticVertex(n3, n2, n3, n2 + n6);
        }
        else {
            this.vertex(n3, n2);
        }
        if (n8 != 0.0f) {
            this.vertex(n3, n4 - n8);
            this.quadraticVertex(n3, n4, n3 - n8, n4);
        }
        else {
            this.vertex(n3, n4);
        }
        if (n7 != 0.0f) {
            this.vertex(n + n7, n4);
            this.quadraticVertex(n, n4, n, n4 - n7);
        }
        else {
            this.vertex(n, n4);
        }
        if (n5 != 0.0f) {
            this.vertex(n, n2 + n5);
            this.quadraticVertex(n, n2, n + n5, n2);
        }
        else {
            this.vertex(n, n2);
        }
        this.endShape(2);
    }
    
    public void ellipseMode(final int ellipseMode) {
        this.ellipseMode = ellipseMode;
    }
    
    public void ellipse(final float n, final float n2, final float n3, final float n4) {
        float n5 = n;
        float n6 = n2;
        float n7 = n3;
        float n8 = n4;
        if (this.ellipseMode == 1) {
            n7 = n3 - n;
            n8 = n4 - n2;
        }
        else if (this.ellipseMode == 2) {
            n5 = n - n3;
            n6 = n2 - n4;
            n7 = n3 * 2.0f;
            n8 = n4 * 2.0f;
        }
        else if (this.ellipseMode == 3) {
            n5 = n - n3 / 2.0f;
            n6 = n2 - n4 / 2.0f;
        }
        if (n7 < 0.0f) {
            n5 += n7;
            n7 = -n7;
        }
        if (n8 < 0.0f) {
            n6 += n8;
            n8 = -n8;
        }
        this.ellipseImpl(n5, n6, n7, n8);
    }
    
    protected void ellipseImpl(final float n, final float n2, final float n3, final float n4) {
    }
    
    public void arc(final float n, final float n2, final float n3, final float n4, float v, float v2) {
        float n5 = n;
        float n6 = n2;
        float n7 = n3;
        float n8 = n4;
        if (this.ellipseMode == 1) {
            n7 = n3 - n;
            n8 = n4 - n2;
        }
        else if (this.ellipseMode == 2) {
            n5 = n - n3;
            n6 = n2 - n4;
            n7 = n3 * 2.0f;
            n8 = n4 * 2.0f;
        }
        else if (this.ellipseMode == 3) {
            n5 = n - n3 / 2.0f;
            n6 = n2 - n4 / 2.0f;
        }
        if (Float.isInfinite(v) || Float.isInfinite(v2)) {
            return;
        }
        if (v2 < v) {
            return;
        }
        while (v < 0.0f) {
            v += 6.2831855f;
            v2 += 6.2831855f;
        }
        if (v2 - v > 6.2831855f) {
            v = 0.0f;
            v2 = 6.2831855f;
        }
        this.arcImpl(n5, n6, n7, n8, v, v2);
    }
    
    protected void arcImpl(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
    }
    
    public void box(final float n) {
        this.box(n, n, n);
    }
    
    public void box(final float n, final float n2, final float n3) {
        final float n4 = -n / 2.0f;
        final float n5 = n / 2.0f;
        final float n6 = -n2 / 2.0f;
        final float n7 = n2 / 2.0f;
        final float n8 = -n3 / 2.0f;
        final float n9 = n3 / 2.0f;
        this.beginShape(16);
        this.normal(0.0f, 0.0f, 1.0f);
        this.vertex(n4, n6, n8);
        this.vertex(n5, n6, n8);
        this.vertex(n5, n7, n8);
        this.vertex(n4, n7, n8);
        this.normal(1.0f, 0.0f, 0.0f);
        this.vertex(n5, n6, n8);
        this.vertex(n5, n6, n9);
        this.vertex(n5, n7, n9);
        this.vertex(n5, n7, n8);
        this.normal(0.0f, 0.0f, -1.0f);
        this.vertex(n5, n6, n9);
        this.vertex(n4, n6, n9);
        this.vertex(n4, n7, n9);
        this.vertex(n5, n7, n9);
        this.normal(-1.0f, 0.0f, 0.0f);
        this.vertex(n4, n6, n9);
        this.vertex(n4, n6, n8);
        this.vertex(n4, n7, n8);
        this.vertex(n4, n7, n9);
        this.normal(0.0f, 1.0f, 0.0f);
        this.vertex(n4, n6, n9);
        this.vertex(n5, n6, n9);
        this.vertex(n5, n6, n8);
        this.vertex(n4, n6, n8);
        this.normal(0.0f, -1.0f, 0.0f);
        this.vertex(n4, n7, n8);
        this.vertex(n5, n7, n8);
        this.vertex(n5, n7, n9);
        this.vertex(n4, n7, n9);
        this.endShape();
    }
    
    public void sphereDetail(final int n) {
        this.sphereDetail(n, n);
    }
    
    public void sphereDetail(int sphereDetailU, int sphereDetailV) {
        if (sphereDetailU < 3) {
            sphereDetailU = 3;
        }
        if (sphereDetailV < 2) {
            sphereDetailV = 2;
        }
        if (sphereDetailU == this.sphereDetailU && sphereDetailV == this.sphereDetailV) {
            return;
        }
        final float n = 720.0f / sphereDetailU;
        final float[] array = new float[sphereDetailU];
        final float[] array2 = new float[sphereDetailU];
        for (int i = 0; i < sphereDetailU; ++i) {
            array[i] = PGraphics.cosLUT[(int)(i * n) % 720];
            array2[i] = PGraphics.sinLUT[(int)(i * n) % 720];
        }
        final int n2 = sphereDetailU * (sphereDetailV - 1) + 2;
        int n3 = 0;
        this.sphereX = new float[n2];
        this.sphereY = new float[n2];
        this.sphereZ = new float[n2];
        float n5;
        final float n4 = n5 = 360.0f / sphereDetailV;
        for (int j = 1; j < sphereDetailV; ++j) {
            final float n6 = PGraphics.sinLUT[(int)n5 % 720];
            final float n7 = -PGraphics.cosLUT[(int)n5 % 720];
            for (int k = 0; k < sphereDetailU; ++k) {
                this.sphereX[n3] = array[k] * n6;
                this.sphereY[n3] = n7;
                this.sphereZ[n3++] = array2[k] * n6;
            }
            n5 += n4;
        }
        this.sphereDetailU = sphereDetailU;
        this.sphereDetailV = sphereDetailV;
    }
    
    public void sphere(final float n) {
        if (this.sphereDetailU < 3 || this.sphereDetailV < 2) {
            this.sphereDetail(30);
        }
        this.edge(false);
        this.beginShape(10);
        for (int i = 0; i < this.sphereDetailU; ++i) {
            this.normal(0.0f, -1.0f, 0.0f);
            this.vertex(0.0f, -n, 0.0f);
            this.normal(this.sphereX[i], this.sphereY[i], this.sphereZ[i]);
            this.vertex(n * this.sphereX[i], n * this.sphereY[i], n * this.sphereZ[i]);
        }
        this.normal(0.0f, -1.0f, 0.0f);
        this.vertex(0.0f, -1.0f, 0.0f);
        this.normal(this.sphereX[0], this.sphereY[0], this.sphereZ[0]);
        this.vertex(n * this.sphereX[0], n * this.sphereY[0], n * this.sphereZ[0]);
        this.endShape();
        int n2 = 0;
        for (int j = 2; j < this.sphereDetailV; ++j) {
            int n4;
            final int n3 = n4 = n2;
            int n5;
            n2 = (n5 = n2 + this.sphereDetailU);
            this.beginShape(10);
            for (int k = 0; k < this.sphereDetailU; ++k) {
                this.normal(this.sphereX[n4], this.sphereY[n4], this.sphereZ[n4]);
                this.vertex(n * this.sphereX[n4], n * this.sphereY[n4], n * this.sphereZ[n4++]);
                this.normal(this.sphereX[n5], this.sphereY[n5], this.sphereZ[n5]);
                this.vertex(n * this.sphereX[n5], n * this.sphereY[n5], n * this.sphereZ[n5++]);
            }
            final int n6 = n3;
            final int n7 = n2;
            this.normal(this.sphereX[n6], this.sphereY[n6], this.sphereZ[n6]);
            this.vertex(n * this.sphereX[n6], n * this.sphereY[n6], this.sphereZ[n6]);
            this.normal(this.sphereX[n7], this.sphereY[n7], this.sphereZ[n7]);
            this.vertex(n * this.sphereX[n7], n * this.sphereY[n7], n * this.sphereZ[n7]);
            this.endShape();
        }
        this.beginShape(10);
        for (int l = 0; l < this.sphereDetailU; ++l) {
            final int n8 = n2 + l;
            this.normal(this.sphereX[n8], this.sphereY[n8], this.sphereZ[n8]);
            this.vertex(n * this.sphereX[n8], n * this.sphereY[n8], n * this.sphereZ[n8]);
            this.normal(0.0f, 1.0f, 0.0f);
            this.vertex(0.0f, n, 0.0f);
        }
        this.normal(this.sphereX[n2], this.sphereY[n2], this.sphereZ[n2]);
        this.vertex(n * this.sphereX[n2], n * this.sphereY[n2], n * this.sphereZ[n2]);
        this.normal(0.0f, 1.0f, 0.0f);
        this.vertex(0.0f, n, 0.0f);
        this.endShape();
        this.edge(true);
    }
    
    public float bezierPoint(final float n, final float n2, final float n3, final float n4, final float n5) {
        final float n6 = 1.0f - n5;
        return n * n6 * n6 * n6 + 3.0f * n2 * n5 * n6 * n6 + 3.0f * n3 * n5 * n5 * n6 + n4 * n5 * n5 * n5;
    }
    
    public float bezierTangent(final float n, final float n2, final float n3, final float n4, final float n5) {
        return 3.0f * n5 * n5 * (-n + 3.0f * n2 - 3.0f * n3 + n4) + 6.0f * n5 * (n - 2.0f * n2 + n3) + 3.0f * (-n + n2);
    }
    
    protected void bezierInitCheck() {
        if (!this.bezierInited) {
            this.bezierInit();
        }
    }
    
    protected void bezierInit() {
        this.bezierDetail(this.bezierDetail);
        this.bezierInited = true;
    }
    
    public void bezierDetail(final int bezierDetail) {
        this.bezierDetail = bezierDetail;
        if (this.bezierDrawMatrix == null) {
            this.bezierDrawMatrix = new PMatrix3D();
        }
        this.splineForward(bezierDetail, this.bezierDrawMatrix);
        this.bezierDrawMatrix.apply(this.bezierBasisMatrix);
    }
    
    public void bezier(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.beginShape();
        this.vertex(n, n2);
        this.bezierVertex(n3, n4, n5, n6, n7, n8);
        this.endShape();
    }
    
    public void bezier(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.beginShape();
        this.vertex(n, n2, n3);
        this.bezierVertex(n4, n5, n6, n7, n8, n9, n10, n11, n12);
        this.endShape();
    }
    
    public float curvePoint(final float n, final float n2, final float n3, final float n4, final float n5) {
        this.curveInitCheck();
        final float n6 = n5 * n5;
        final float n7 = n5 * n6;
        final PMatrix3D curveBasisMatrix = this.curveBasisMatrix;
        return n * (n7 * curveBasisMatrix.m00 + n6 * curveBasisMatrix.m10 + n5 * curveBasisMatrix.m20 + curveBasisMatrix.m30) + n2 * (n7 * curveBasisMatrix.m01 + n6 * curveBasisMatrix.m11 + n5 * curveBasisMatrix.m21 + curveBasisMatrix.m31) + n3 * (n7 * curveBasisMatrix.m02 + n6 * curveBasisMatrix.m12 + n5 * curveBasisMatrix.m22 + curveBasisMatrix.m32) + n4 * (n7 * curveBasisMatrix.m03 + n6 * curveBasisMatrix.m13 + n5 * curveBasisMatrix.m23 + curveBasisMatrix.m33);
    }
    
    public float curveTangent(final float n, final float n2, final float n3, final float n4, final float n5) {
        this.curveInitCheck();
        final float n6 = n5 * n5 * 3.0f;
        final float n7 = n5 * 2.0f;
        final PMatrix3D curveBasisMatrix = this.curveBasisMatrix;
        return n * (n6 * curveBasisMatrix.m00 + n7 * curveBasisMatrix.m10 + curveBasisMatrix.m20) + n2 * (n6 * curveBasisMatrix.m01 + n7 * curveBasisMatrix.m11 + curveBasisMatrix.m21) + n3 * (n6 * curveBasisMatrix.m02 + n7 * curveBasisMatrix.m12 + curveBasisMatrix.m22) + n4 * (n6 * curveBasisMatrix.m03 + n7 * curveBasisMatrix.m13 + curveBasisMatrix.m23);
    }
    
    public void curveDetail(final int curveDetail) {
        this.curveDetail = curveDetail;
        this.curveInit();
    }
    
    public void curveTightness(final float curveTightness) {
        this.curveTightness = curveTightness;
        this.curveInit();
    }
    
    protected void curveInitCheck() {
        if (!this.curveInited) {
            this.curveInit();
        }
    }
    
    protected void curveInit() {
        if (this.curveDrawMatrix == null) {
            this.curveBasisMatrix = new PMatrix3D();
            this.curveDrawMatrix = new PMatrix3D();
            this.curveInited = true;
        }
        final float curveTightness = this.curveTightness;
        this.curveBasisMatrix.set((curveTightness - 1.0f) / 2.0f, (curveTightness + 3.0f) / 2.0f, (-3.0f - curveTightness) / 2.0f, (1.0f - curveTightness) / 2.0f, 1.0f - curveTightness, (-5.0f - curveTightness) / 2.0f, curveTightness + 2.0f, (curveTightness - 1.0f) / 2.0f, (curveTightness - 1.0f) / 2.0f, 0.0f, (1.0f - curveTightness) / 2.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f);
        this.splineForward(this.curveDetail, this.curveDrawMatrix);
        if (this.bezierBasisInverse == null) {
            (this.bezierBasisInverse = this.bezierBasisMatrix.get()).invert();
            this.curveToBezierMatrix = new PMatrix3D();
        }
        this.curveToBezierMatrix.set((PMatrix)this.curveBasisMatrix);
        this.curveToBezierMatrix.preApply(this.bezierBasisInverse);
        this.curveDrawMatrix.apply(this.curveBasisMatrix);
    }
    
    public void curve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.beginShape();
        this.curveVertex(n, n2);
        this.curveVertex(n3, n4);
        this.curveVertex(n5, n6);
        this.curveVertex(n7, n8);
        this.endShape();
    }
    
    public void curve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.beginShape();
        this.curveVertex(n, n2, n3);
        this.curveVertex(n4, n5, n6);
        this.curveVertex(n7, n8, n9);
        this.curveVertex(n10, n11, n12);
        this.endShape();
    }
    
    protected void splineForward(final int n, final PMatrix3D pMatrix3D) {
        final float n2 = 1.0f / n;
        final float n3 = n2 * n2;
        final float n4 = n3 * n2;
        pMatrix3D.set(0.0f, 0.0f, 0.0f, 1.0f, n4, n3, n2, 0.0f, 6.0f * n4, 2.0f * n3, 0.0f, 0.0f, 6.0f * n4, 0.0f, 0.0f, 0.0f);
    }
    
    public void smooth() {
        this.smooth = true;
    }
    
    public void noSmooth() {
        this.smooth = false;
    }
    
    public void imageMode(final int imageMode) {
        if (imageMode == 0 || imageMode == 1 || imageMode == 3) {
            this.imageMode = imageMode;
            return;
        }
        throw new RuntimeException("imageMode() only works with CORNER, CORNERS, or CENTER");
    }
    
    public void image(final PImage pImage, final float n, final float n2) {
        if (pImage.width == -1 || pImage.height == -1) {
            return;
        }
        if (this.imageMode == 0 || this.imageMode == 1) {
            this.imageImpl(pImage, n, n2, n + pImage.width, n2 + pImage.height, 0, 0, pImage.width, pImage.height);
        }
        else if (this.imageMode == 3) {
            final float n3 = n - pImage.width / 2;
            final float n4 = n2 - pImage.height / 2;
            this.imageImpl(pImage, n3, n4, n3 + pImage.width, n4 + pImage.height, 0, 0, pImage.width, pImage.height);
        }
    }
    
    public void image(final PImage pImage, final float n, final float n2, final float n3, final float n4) {
        this.image(pImage, n, n2, n3, n4, 0, 0, pImage.width, pImage.height);
    }
    
    public void image(final PImage pImage, float n, float n2, float n3, float n4, final int n5, final int n6, final int n7, final int n8) {
        if (pImage.width == -1 || pImage.height == -1) {
            return;
        }
        if (this.imageMode == 0) {
            if (n3 < 0.0f) {
                n += n3;
                n3 = -n3;
            }
            if (n4 < 0.0f) {
                n2 += n4;
                n4 = -n4;
            }
            this.imageImpl(pImage, n, n2, n + n3, n2 + n4, n5, n6, n7, n8);
        }
        else if (this.imageMode == 1) {
            if (n3 < n) {
                final float n9 = n;
                n = n3;
                n3 = n9;
            }
            if (n4 < n2) {
                final float n10 = n2;
                n2 = n4;
                n4 = n10;
            }
            this.imageImpl(pImage, n, n2, n3, n4, n5, n6, n7, n8);
        }
        else if (this.imageMode == 3) {
            if (n3 < 0.0f) {
                n3 = -n3;
            }
            if (n4 < 0.0f) {
                n4 = -n4;
            }
            final float n11 = n - n3 / 2.0f;
            final float n12 = n2 - n4 / 2.0f;
            this.imageImpl(pImage, n11, n12, n11 + n3, n12 + n4, n5, n6, n7, n8);
        }
    }
    
    protected void imageImpl(final PImage pImage, final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8) {
        final boolean stroke = this.stroke;
        final int textureMode = this.textureMode;
        this.stroke = false;
        this.textureMode = 2;
        this.beginShape(16);
        this.texture(pImage);
        this.vertex(n, n2, (float)n5, (float)n6);
        this.vertex(n, n4, (float)n5, (float)n8);
        this.vertex(n3, n4, (float)n7, (float)n8);
        this.vertex(n3, n2, (float)n7, (float)n6);
        this.endShape();
        this.stroke = stroke;
        this.textureMode = textureMode;
    }
    
    public void shapeMode(final int shapeMode) {
        this.shapeMode = shapeMode;
    }
    
    public void shape(final PShape pShape) {
        if (pShape.isVisible()) {
            if (this.shapeMode == 3) {
                this.pushMatrix();
                this.translate(-pShape.getWidth() / 2.0f, -pShape.getHeight() / 2.0f);
            }
            pShape.draw(this);
            if (this.shapeMode == 3) {
                this.popMatrix();
            }
        }
    }
    
    public void shape(final PShape pShape, final float n, final float n2) {
        if (pShape.isVisible()) {
            this.pushMatrix();
            if (this.shapeMode == 3) {
                this.translate(n - pShape.getWidth() / 2.0f, n2 - pShape.getHeight() / 2.0f);
            }
            else if (this.shapeMode == 0 || this.shapeMode == 1) {
                this.translate(n, n2);
            }
            pShape.draw(this);
            this.popMatrix();
        }
    }
    
    public void shape(final PShape pShape, final float n, final float n2, float n3, float n4) {
        if (pShape.isVisible()) {
            this.pushMatrix();
            if (this.shapeMode == 3) {
                this.translate(n - n3 / 2.0f, n2 - n4 / 2.0f);
                this.scale(n3 / pShape.getWidth(), n4 / pShape.getHeight());
            }
            else if (this.shapeMode == 0) {
                this.translate(n, n2);
                this.scale(n3 / pShape.getWidth(), n4 / pShape.getHeight());
            }
            else if (this.shapeMode == 1) {
                n3 -= n;
                n4 -= n2;
                this.translate(n, n2);
                this.scale(n3 / pShape.getWidth(), n4 / pShape.getHeight());
            }
            pShape.draw(this);
            this.popMatrix();
        }
    }
    
    public void textAlign(final int n) {
        this.textAlign(n, 0);
    }
    
    public void textAlign(final int textAlign, final int textAlignY) {
        this.textAlign = textAlign;
        this.textAlignY = textAlignY;
    }
    
    public float textAscent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textAscent");
        }
        return this.textFont.ascent() * ((this.textMode == 256) ? ((float)this.textFont.size) : this.textSize);
    }
    
    public float textDescent() {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textDescent");
        }
        return this.textFont.descent() * ((this.textMode == 256) ? ((float)this.textFont.size) : this.textSize);
    }
    
    public void textFont(final PFont textFont) {
        if (textFont != null) {
            this.textFont = textFont;
            if (this.hints[3]) {
                textFont.findFont();
            }
            this.textSize((float)textFont.size);
            return;
        }
        throw new RuntimeException("A null PFont was passed to textFont()");
    }
    
    public void textFont(final PFont pFont, final float n) {
        this.textFont(pFont);
        this.textSize(n);
    }
    
    public void textLeading(final float textLeading) {
        this.textLeading = textLeading;
    }
    
    public void textMode(final int n) {
        if (n == 37 || n == 39) {
            showWarning("Since Processing beta, textMode() is now textAlign().");
            return;
        }
        if (this.textModeCheck(n)) {
            this.textMode = n;
        }
        else {
            String value = String.valueOf(n);
            switch (n) {
                case 256: {
                    value = "SCREEN";
                    break;
                }
                case 4: {
                    value = "MODEL";
                    break;
                }
                case 5: {
                    value = "SHAPE";
                    break;
                }
            }
            showWarning("textMode(" + value + ") is not supported by this renderer.");
        }
    }
    
    protected boolean textModeCheck(final int n) {
        return true;
    }
    
    public void textSize(final float textSize) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textSize", textSize);
        }
        this.textSize = textSize;
        this.textLeading = (this.textAscent() + this.textDescent()) * 1.275f;
    }
    
    public float textWidth(final char c) {
        this.textWidthBuffer[0] = c;
        return this.textWidthImpl(this.textWidthBuffer, 0, 1);
    }
    
    public float textWidth(final String s) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("textWidth");
        }
        final int length = s.length();
        if (length > this.textWidthBuffer.length) {
            this.textWidthBuffer = new char[length + 10];
        }
        s.getChars(0, length, this.textWidthBuffer, 0);
        float n = 0.0f;
        int i = 0;
        int n2 = 0;
        while (i < length) {
            if (this.textWidthBuffer[i] == '\n') {
                n = Math.max(n, this.textWidthImpl(this.textWidthBuffer, n2, i));
                n2 = i + 1;
            }
            ++i;
        }
        if (n2 < length) {
            n = Math.max(n, this.textWidthImpl(this.textWidthBuffer, n2, i));
        }
        return n;
    }
    
    public float textWidth(final char[] array, final int n, final int n2) {
        return this.textWidthImpl(array, n, n + n2);
    }
    
    protected float textWidthImpl(final char[] array, final int n, final int n2) {
        float n3 = 0.0f;
        for (int i = n; i < n2; ++i) {
            n3 += this.textFont.width(array[i]) * this.textSize;
        }
        return n3;
    }
    
    public void text(final char c) {
        this.text(c, this.textX, this.textY, this.textZ);
    }
    
    public void text(final char c, final float n, float n2) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }
        if (this.textMode == 256) {
            this.loadPixels();
        }
        if (this.textAlignY == 3) {
            n2 += this.textAscent() / 2.0f;
        }
        else if (this.textAlignY == 101) {
            n2 += this.textAscent();
        }
        else if (this.textAlignY == 102) {
            n2 -= this.textDescent();
        }
        this.textBuffer[0] = c;
        this.textLineAlignImpl(this.textBuffer, 0, 1, n, n2);
        if (this.textMode == 256) {
            this.updatePixels();
        }
    }
    
    public void text(final char c, final float n, final float n2, final float textZ) {
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, textZ);
        }
        this.text(c, n, n2);
        this.textZ = textZ;
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, -textZ);
        }
    }
    
    public void text(final String s) {
        this.text(s, this.textX, this.textY, this.textZ);
    }
    
    public void text(final String s, final float n, final float n2) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }
        if (this.textMode == 256) {
            this.loadPixels();
        }
        final int length = s.length();
        if (length > this.textBuffer.length) {
            this.textBuffer = new char[length + 10];
        }
        s.getChars(0, length, this.textBuffer, 0);
        this.text(this.textBuffer, 0, length, n, n2);
    }
    
    public void text(final char[] array, int n, final int n2, final float n3, float n4) {
        float n5 = 0.0f;
        for (int i = n; i < n2; ++i) {
            if (array[i] == '\n') {
                n5 += this.textLeading;
            }
        }
        if (this.textAlignY == 3) {
            n4 += (this.textAscent() - n5) / 2.0f;
        }
        else if (this.textAlignY == 101) {
            n4 += this.textAscent();
        }
        else if (this.textAlignY == 102) {
            n4 -= this.textDescent() + n5;
        }
        int j;
        for (j = 0; j < n2; ++j) {
            if (array[j] == '\n') {
                this.textLineAlignImpl(array, n, j, n3, n4);
                n = j + 1;
                n4 += this.textLeading;
            }
        }
        if (n < n2) {
            this.textLineAlignImpl(array, n, j, n3, n4);
        }
        if (this.textMode == 256) {
            this.updatePixels();
        }
    }
    
    public void text(final String s, final float n, final float n2, final float textZ) {
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, textZ);
        }
        this.text(s, n, n2);
        this.textZ = textZ;
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, -textZ);
        }
    }
    
    public void text(final char[] array, final int n, final int n2, final float n3, final float n4, final float textZ) {
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, textZ);
        }
        this.text(array, n, n2, n3, n4);
        this.textZ = textZ;
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, -textZ);
        }
    }
    
    public void text(final String s, float n, float n2, float n3, float n4) {
        if (this.textFont == null) {
            this.defaultFontOrDeath("text");
        }
        if (this.textMode == 256) {
            this.loadPixels();
        }
        switch (this.rectMode) {
            case 0: {
                n3 += n;
                n4 += n2;
                break;
            }
            case 2: {
                final float n5 = n3;
                final float n6 = n4;
                n3 = n + n5;
                n4 = n2 + n6;
                n -= n5;
                n2 -= n6;
                break;
            }
            case 3: {
                final float n7 = n3 / 2.0f;
                final float n8 = n4 / 2.0f;
                n3 = n + n7;
                n4 = n2 + n8;
                n -= n7;
                n2 -= n8;
                break;
            }
        }
        if (n3 < n) {
            final float n9 = n;
            n = n3;
            n3 = n9;
        }
        if (n4 < n2) {
            final float n10 = n2;
            n2 = n4;
            n4 = n10;
        }
        final float n11 = n3 - n;
        final float textWidth = this.textWidth(' ');
        if (this.textBreakStart == null) {
            this.textBreakStart = new int[20];
            this.textBreakStop = new int[20];
        }
        this.textBreakCount = 0;
        int length = s.length();
        if (length + 1 > this.textBuffer.length) {
            this.textBuffer = new char[length + 1];
        }
        s.getChars(0, length, this.textBuffer, 0);
        this.textBuffer[length++] = '\n';
        int n12 = 0;
        for (int i = 0; i < length; ++i) {
            if (this.textBuffer[i] == '\n') {
                if (!this.textSentence(this.textBuffer, n12, i, n11, textWidth)) {
                    break;
                }
                n12 = i + 1;
            }
        }
        float n13 = n;
        if (this.textAlign == 3) {
            n13 += n11 / 2.0f;
        }
        else if (this.textAlign == 39) {
            n13 = n3;
        }
        final float n14 = n4 - n2;
        final int min = Math.min(this.textBreakCount, 1 + PApplet.floor((n14 - (this.textAscent() + this.textDescent())) / this.textLeading));
        if (this.textAlignY == 3) {
            float n15 = n2 + this.textAscent() + (n14 - (this.textAscent() + this.textLeading * (min - 1))) / 2.0f;
            for (int j = 0; j < min; ++j) {
                this.textLineAlignImpl(this.textBuffer, this.textBreakStart[j], this.textBreakStop[j], n13, n15);
                n15 += this.textLeading;
            }
        }
        else if (this.textAlignY == 102) {
            float n16 = n4 - this.textDescent() - this.textLeading * (min - 1);
            for (int k = 0; k < min; ++k) {
                this.textLineAlignImpl(this.textBuffer, this.textBreakStart[k], this.textBreakStop[k], n13, n16);
                n16 += this.textLeading;
            }
        }
        else {
            float n17 = n2 + this.textAscent();
            for (int l = 0; l < min; ++l) {
                this.textLineAlignImpl(this.textBuffer, this.textBreakStart[l], this.textBreakStop[l], n13, n17);
                n17 += this.textLeading;
            }
        }
        if (this.textMode == 256) {
            this.updatePixels();
        }
    }
    
    protected boolean textSentence(final char[] array, final int n, final int n2, final float n3, final float n4) {
        float n5 = 0.0f;
        int n6 = n;
        int n7 = n;
        int i = n;
        while (i <= n2) {
            if (array[i] == ' ' || i == n2) {
                final float textWidthImpl = this.textWidthImpl(array, n7, i);
                if (n5 + textWidthImpl > n3) {
                    Label_0134: {
                        if (n5 == 0.0f) {
                            while (--i != n7) {
                                if (this.textWidthImpl(array, n7, i) <= n3) {
                                    this.textSentenceBreak(n6, i);
                                    break Label_0134;
                                }
                            }
                            return false;
                        }
                        i = n7;
                        this.textSentenceBreak(n6, i);
                        while (i < n2 && array[i] == ' ') {
                            ++i;
                        }
                    }
                    n6 = i;
                    n7 = i;
                    n5 = 0.0f;
                }
                else if (i == n2) {
                    this.textSentenceBreak(n6, i);
                    ++i;
                }
                else {
                    n5 += textWidthImpl + n4;
                    n7 = i + 1;
                    ++i;
                }
            }
            else {
                ++i;
            }
        }
        return true;
    }
    
    protected void textSentenceBreak(final int n, final int n2) {
        if (this.textBreakCount == this.textBreakStart.length) {
            this.textBreakStart = PApplet.expand(this.textBreakStart);
            this.textBreakStop = PApplet.expand(this.textBreakStop);
        }
        this.textBreakStart[this.textBreakCount] = n;
        this.textBreakStop[this.textBreakCount] = n2;
        ++this.textBreakCount;
    }
    
    public void text(final String s, final float n, final float n2, final float n3, final float n4, final float textZ) {
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, textZ);
        }
        this.text(s, n, n2, n3, n4);
        this.textZ = textZ;
        if (textZ != 0.0f) {
            this.translate(0.0f, 0.0f, -textZ);
        }
    }
    
    public void text(final int i, final float n, final float n2) {
        this.text(String.valueOf(i), n, n2);
    }
    
    public void text(final int i, final float n, final float n2, final float n3) {
        this.text(String.valueOf(i), n, n2, n3);
    }
    
    public void text(final float n, final float n2, final float n3) {
        this.text(PApplet.nfs(n, 0, 3), n2, n3);
    }
    
    public void text(final float n, final float n2, final float n3, final float n4) {
        this.text(PApplet.nfs(n, 0, 3), n2, n3, n4);
    }
    
    protected void textLineAlignImpl(final char[] array, final int n, final int n2, float n3, final float n4) {
        if (this.textAlign == 3) {
            n3 -= this.textWidthImpl(array, n, n2) / 2.0f;
        }
        else if (this.textAlign == 39) {
            n3 -= this.textWidthImpl(array, n, n2);
        }
        this.textLineImpl(array, n, n2, n3, n4);
    }
    
    protected void textLineImpl(final char[] array, final int n, final int n2, float textX, final float textY) {
        for (int i = n; i < n2; ++i) {
            this.textCharImpl(array[i], textX, textY);
            textX += this.textWidth(array[i]);
        }
        this.textX = textX;
        this.textY = textY;
        this.textZ = 0.0f;
    }
    
    protected void textCharImpl(final char c, final float n, final float n2) {
        final PFont.Glyph glyph = this.textFont.getGlyph(c);
        if (glyph != null) {
            if (this.textMode == 4) {
                final float n3 = glyph.height / (float)this.textFont.size;
                final float n4 = glyph.width / (float)this.textFont.size;
                final float n5 = glyph.leftExtent / (float)this.textFont.size;
                final float n6 = glyph.topExtent / (float)this.textFont.size;
                final float n7 = n + n5 * this.textSize;
                final float n8 = n2 - n6 * this.textSize;
                this.textCharModelImpl(glyph.image, n7, n8, n7 + n4 * this.textSize, n8 + n3 * this.textSize, glyph.width, glyph.height);
            }
            else if (this.textMode == 256) {
                this.textCharScreenImpl(glyph.image, (int)n + glyph.leftExtent, (int)n2 - glyph.topExtent, glyph.width, glyph.height);
            }
        }
    }
    
    protected void textCharModelImpl(final PImage pImage, final float n, final float n2, final float n3, final float n4, final int n5, final int n6) {
        final boolean tint = this.tint;
        final int tintColor = this.tintColor;
        final float tintR = this.tintR;
        final float tintG = this.tintG;
        final float tintB = this.tintB;
        final float tintA = this.tintA;
        final boolean tintAlpha = this.tintAlpha;
        this.tint = true;
        this.tintColor = this.fillColor;
        this.tintR = this.fillR;
        this.tintG = this.fillG;
        this.tintB = this.fillB;
        this.tintA = this.fillA;
        this.tintAlpha = this.fillAlpha;
        this.imageImpl(pImage, n, n2, n3, n4, 0, 0, n5, n6);
        this.tint = tint;
        this.tintColor = tintColor;
        this.tintR = tintR;
        this.tintG = tintG;
        this.tintB = tintB;
        this.tintA = tintA;
        this.tintAlpha = tintAlpha;
    }
    
    protected void textCharScreenImpl(final PImage pImage, int n, int n2, int n3, int n4) {
        int n5 = 0;
        int n6 = 0;
        if (n >= this.width || n2 >= this.height || n + n3 < 0 || n2 + n4 < 0) {
            return;
        }
        if (n < 0) {
            n5 -= n;
            n3 += n;
            n = 0;
        }
        if (n2 < 0) {
            n6 -= n2;
            n4 += n2;
            n2 = 0;
        }
        if (n + n3 > this.width) {
            n3 -= n + n3 - this.width;
        }
        if (n2 + n4 > this.height) {
            n4 -= n2 + n4 - this.height;
        }
        final int fillRi = this.fillRi;
        final int fillGi = this.fillGi;
        final int fillBi = this.fillBi;
        final int fillAi = this.fillAi;
        final int[] pixels = pImage.pixels;
        for (int i = n6; i < n6 + n4; ++i) {
            for (int j = n5; j < n5 + n3; ++j) {
                final int n7 = fillAi * pixels[i * pImage.width + j] >> 8;
                final int n8 = n7 ^ 0xFF;
                final int n9 = this.pixels[(n2 + i - n6) * this.width + (n + j - n5)];
                this.pixels[(n2 + i - n6) * this.width + n + j - n5] = (0xFF000000 | (n7 * fillRi + n8 * (n9 >> 16 & 0xFF) & 0xFF00) << 8 | (n7 * fillGi + n8 * (n9 >> 8 & 0xFF) & 0xFF00) | n7 * fillBi + n8 * (n9 & 0xFF) >> 8);
            }
        }
    }
    
    public void pushMatrix() {
        showMethodWarning("pushMatrix");
    }
    
    public void popMatrix() {
        showMethodWarning("popMatrix");
    }
    
    public void translate(final float n, final float n2) {
        showMissingWarning("translate");
    }
    
    public void translate(final float n, final float n2, final float n3) {
        showMissingWarning("translate");
    }
    
    public void rotate(final float n) {
        showMissingWarning("rotate");
    }
    
    public void rotateX(final float n) {
        showMethodWarning("rotateX");
    }
    
    public void rotateY(final float n) {
        showMethodWarning("rotateY");
    }
    
    public void rotateZ(final float n) {
        showMethodWarning("rotateZ");
    }
    
    public void rotate(final float n, final float n2, final float n3, final float n4) {
        showMissingWarning("rotate");
    }
    
    public void scale(final float n) {
        showMissingWarning("scale");
    }
    
    public void scale(final float n, final float n2) {
        showMissingWarning("scale");
    }
    
    public void scale(final float n, final float n2, final float n3) {
        showMissingWarning("scale");
    }
    
    public void shearX(final float n) {
        showMissingWarning("shearX");
    }
    
    public void shearY(final float n) {
        showMissingWarning("shearY");
    }
    
    public void resetMatrix() {
        showMethodWarning("resetMatrix");
    }
    
    public void applyMatrix(final PMatrix pMatrix) {
        if (pMatrix instanceof PMatrix2D) {
            this.applyMatrix((PMatrix2D)pMatrix);
        }
        else if (pMatrix instanceof PMatrix3D) {
            this.applyMatrix((PMatrix3D)pMatrix);
        }
    }
    
    public void applyMatrix(final PMatrix2D pMatrix2D) {
        this.applyMatrix(pMatrix2D.m00, pMatrix2D.m01, pMatrix2D.m02, pMatrix2D.m10, pMatrix2D.m11, pMatrix2D.m12);
    }
    
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        showMissingWarning("applyMatrix");
    }
    
    public void applyMatrix(final PMatrix3D pMatrix3D) {
        this.applyMatrix(pMatrix3D.m00, pMatrix3D.m01, pMatrix3D.m02, pMatrix3D.m03, pMatrix3D.m10, pMatrix3D.m11, pMatrix3D.m12, pMatrix3D.m13, pMatrix3D.m20, pMatrix3D.m21, pMatrix3D.m22, pMatrix3D.m23, pMatrix3D.m30, pMatrix3D.m31, pMatrix3D.m32, pMatrix3D.m33);
    }
    
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        showMissingWarning("applyMatrix");
    }
    
    public PMatrix getMatrix() {
        showMissingWarning("getMatrix");
        return null;
    }
    
    public PMatrix2D getMatrix(final PMatrix2D pMatrix2D) {
        showMissingWarning("getMatrix");
        return null;
    }
    
    public PMatrix3D getMatrix(final PMatrix3D pMatrix3D) {
        showMissingWarning("getMatrix");
        return null;
    }
    
    public void setMatrix(final PMatrix pMatrix) {
        if (pMatrix instanceof PMatrix2D) {
            this.setMatrix((PMatrix2D)pMatrix);
        }
        else if (pMatrix instanceof PMatrix3D) {
            this.setMatrix((PMatrix3D)pMatrix);
        }
    }
    
    public void setMatrix(final PMatrix2D pMatrix2D) {
        showMissingWarning("setMatrix");
    }
    
    public void setMatrix(final PMatrix3D pMatrix3D) {
        showMissingWarning("setMatrix");
    }
    
    public void printMatrix() {
        showMethodWarning("printMatrix");
    }
    
    public void beginCamera() {
        showMethodWarning("beginCamera");
    }
    
    public void endCamera() {
        showMethodWarning("endCamera");
    }
    
    public void camera() {
        showMissingWarning("camera");
    }
    
    public void camera(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        showMissingWarning("camera");
    }
    
    public void printCamera() {
        showMethodWarning("printCamera");
    }
    
    public void ortho() {
        showMissingWarning("ortho");
    }
    
    public void ortho(final float n, final float n2, final float n3, final float n4) {
        showMissingWarning("ortho");
    }
    
    public void ortho(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        showMissingWarning("ortho");
    }
    
    public void perspective() {
        showMissingWarning("perspective");
    }
    
    public void perspective(final float n, final float n2, final float n3, final float n4) {
        showMissingWarning("perspective");
    }
    
    public void frustum(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        showMethodWarning("frustum");
    }
    
    public void printProjection() {
        showMethodWarning("printCamera");
    }
    
    public float screenX(final float n, final float n2) {
        showMissingWarning("screenX");
        return 0.0f;
    }
    
    public float screenY(final float n, final float n2) {
        showMissingWarning("screenY");
        return 0.0f;
    }
    
    public float screenX(final float n, final float n2, final float n3) {
        showMissingWarning("screenX");
        return 0.0f;
    }
    
    public float screenY(final float n, final float n2, final float n3) {
        showMissingWarning("screenY");
        return 0.0f;
    }
    
    public float screenZ(final float n, final float n2, final float n3) {
        showMissingWarning("screenZ");
        return 0.0f;
    }
    
    public float modelX(final float n, final float n2, final float n3) {
        showMissingWarning("modelX");
        return 0.0f;
    }
    
    public float modelY(final float n, final float n2, final float n3) {
        showMissingWarning("modelY");
        return 0.0f;
    }
    
    public float modelZ(final float n, final float n2, final float n3) {
        showMissingWarning("modelZ");
        return 0.0f;
    }
    
    public void pushStyle() {
        if (this.styleStackDepth == this.styleStack.length) {
            this.styleStack = (PStyle[])PApplet.expand(this.styleStack);
        }
        if (this.styleStack[this.styleStackDepth] == null) {
            this.styleStack[this.styleStackDepth] = new PStyle();
        }
        this.getStyle(this.styleStack[this.styleStackDepth++]);
    }
    
    public void popStyle() {
        if (this.styleStackDepth == 0) {
            throw new RuntimeException("Too many popStyle() without enough pushStyle()");
        }
        --this.styleStackDepth;
        this.style(this.styleStack[this.styleStackDepth]);
    }
    
    public void style(final PStyle pStyle) {
        this.imageMode(pStyle.imageMode);
        this.rectMode(pStyle.rectMode);
        this.ellipseMode(pStyle.ellipseMode);
        this.shapeMode(pStyle.shapeMode);
        if (pStyle.tint) {
            this.tint(pStyle.tintColor);
        }
        else {
            this.noTint();
        }
        if (pStyle.fill) {
            this.fill(pStyle.fillColor);
        }
        else {
            this.noFill();
        }
        if (pStyle.stroke) {
            this.stroke(pStyle.strokeColor);
        }
        else {
            this.noStroke();
        }
        this.strokeWeight(pStyle.strokeWeight);
        this.strokeCap(pStyle.strokeCap);
        this.strokeJoin(pStyle.strokeJoin);
        this.colorMode(1, 1.0f);
        this.ambient(pStyle.ambientR, pStyle.ambientG, pStyle.ambientB);
        this.emissive(pStyle.emissiveR, pStyle.emissiveG, pStyle.emissiveB);
        this.specular(pStyle.specularR, pStyle.specularG, pStyle.specularB);
        this.shininess(pStyle.shininess);
        this.colorMode(pStyle.colorMode, pStyle.colorModeX, pStyle.colorModeY, pStyle.colorModeZ, pStyle.colorModeA);
        if (pStyle.textFont != null) {
            this.textFont(pStyle.textFont, pStyle.textSize);
            this.textLeading(pStyle.textLeading);
        }
        this.textAlign(pStyle.textAlign, pStyle.textAlignY);
        this.textMode(pStyle.textMode);
    }
    
    public PStyle getStyle() {
        return this.getStyle(null);
    }
    
    public PStyle getStyle(PStyle pStyle) {
        if (pStyle == null) {
            pStyle = new PStyle();
        }
        pStyle.imageMode = this.imageMode;
        pStyle.rectMode = this.rectMode;
        pStyle.ellipseMode = this.ellipseMode;
        pStyle.shapeMode = this.shapeMode;
        pStyle.colorMode = this.colorMode;
        pStyle.colorModeX = this.colorModeX;
        pStyle.colorModeY = this.colorModeY;
        pStyle.colorModeZ = this.colorModeZ;
        pStyle.colorModeA = this.colorModeA;
        pStyle.tint = this.tint;
        pStyle.tintColor = this.tintColor;
        pStyle.fill = this.fill;
        pStyle.fillColor = this.fillColor;
        pStyle.stroke = this.stroke;
        pStyle.strokeColor = this.strokeColor;
        pStyle.strokeWeight = this.strokeWeight;
        pStyle.strokeCap = this.strokeCap;
        pStyle.strokeJoin = this.strokeJoin;
        pStyle.ambientR = this.ambientR;
        pStyle.ambientG = this.ambientG;
        pStyle.ambientB = this.ambientB;
        pStyle.specularR = this.specularR;
        pStyle.specularG = this.specularG;
        pStyle.specularB = this.specularB;
        pStyle.emissiveR = this.emissiveR;
        pStyle.emissiveG = this.emissiveG;
        pStyle.emissiveB = this.emissiveB;
        pStyle.shininess = this.shininess;
        pStyle.textFont = this.textFont;
        pStyle.textAlign = this.textAlign;
        pStyle.textAlignY = this.textAlignY;
        pStyle.textMode = this.textMode;
        pStyle.textSize = this.textSize;
        pStyle.textLeading = this.textLeading;
        return pStyle;
    }
    
    public void strokeWeight(final float strokeWeight) {
        this.strokeWeight = strokeWeight;
    }
    
    public void strokeJoin(final int strokeJoin) {
        this.strokeJoin = strokeJoin;
    }
    
    public void strokeCap(final int strokeCap) {
        this.strokeCap = strokeCap;
    }
    
    public void noStroke() {
        this.stroke = false;
    }
    
    public void stroke(final int n) {
        this.colorCalc(n);
        this.strokeFromCalc();
    }
    
    public void stroke(final int n, final float n2) {
        this.colorCalc(n, n2);
        this.strokeFromCalc();
    }
    
    public void stroke(final float n) {
        this.colorCalc(n);
        this.strokeFromCalc();
    }
    
    public void stroke(final float n, final float n2) {
        this.colorCalc(n, n2);
        this.strokeFromCalc();
    }
    
    public void stroke(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.strokeFromCalc();
    }
    
    public void stroke(final float n, final float n2, final float n3, final float n4) {
        this.colorCalc(n, n2, n3, n4);
        this.strokeFromCalc();
    }
    
    protected void strokeFromCalc() {
        this.stroke = true;
        this.strokeR = this.calcR;
        this.strokeG = this.calcG;
        this.strokeB = this.calcB;
        this.strokeA = this.calcA;
        this.strokeRi = this.calcRi;
        this.strokeGi = this.calcGi;
        this.strokeBi = this.calcBi;
        this.strokeAi = this.calcAi;
        this.strokeColor = this.calcColor;
        this.strokeAlpha = this.calcAlpha;
    }
    
    public void noTint() {
        this.tint = false;
    }
    
    public void tint(final int n) {
        this.colorCalc(n);
        this.tintFromCalc();
    }
    
    public void tint(final int n, final float n2) {
        this.colorCalc(n, n2);
        this.tintFromCalc();
    }
    
    public void tint(final float n) {
        this.colorCalc(n);
        this.tintFromCalc();
    }
    
    public void tint(final float n, final float n2) {
        this.colorCalc(n, n2);
        this.tintFromCalc();
    }
    
    public void tint(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.tintFromCalc();
    }
    
    public void tint(final float n, final float n2, final float n3, final float n4) {
        this.colorCalc(n, n2, n3, n4);
        this.tintFromCalc();
    }
    
    protected void tintFromCalc() {
        this.tint = true;
        this.tintR = this.calcR;
        this.tintG = this.calcG;
        this.tintB = this.calcB;
        this.tintA = this.calcA;
        this.tintRi = this.calcRi;
        this.tintGi = this.calcGi;
        this.tintBi = this.calcBi;
        this.tintAi = this.calcAi;
        this.tintColor = this.calcColor;
        this.tintAlpha = this.calcAlpha;
    }
    
    public void noFill() {
        this.fill = false;
    }
    
    public void fill(final int n) {
        this.colorCalc(n);
        this.fillFromCalc();
    }
    
    public void fill(final int n, final float n2) {
        this.colorCalc(n, n2);
        this.fillFromCalc();
    }
    
    public void fill(final float n) {
        this.colorCalc(n);
        this.fillFromCalc();
    }
    
    public void fill(final float n, final float n2) {
        this.colorCalc(n, n2);
        this.fillFromCalc();
    }
    
    public void fill(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.fillFromCalc();
    }
    
    public void fill(final float n, final float n2, final float n3, final float n4) {
        this.colorCalc(n, n2, n3, n4);
        this.fillFromCalc();
    }
    
    protected void fillFromCalc() {
        this.fill = true;
        this.fillR = this.calcR;
        this.fillG = this.calcG;
        this.fillB = this.calcB;
        this.fillA = this.calcA;
        this.fillRi = this.calcRi;
        this.fillGi = this.calcGi;
        this.fillBi = this.calcBi;
        this.fillAi = this.calcAi;
        this.fillColor = this.calcColor;
        this.fillAlpha = this.calcAlpha;
    }
    
    public void ambient(final int n) {
        this.colorCalc(n);
        this.ambientFromCalc();
    }
    
    public void ambient(final float n) {
        this.colorCalc(n);
        this.ambientFromCalc();
    }
    
    public void ambient(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.ambientFromCalc();
    }
    
    protected void ambientFromCalc() {
        this.ambientR = this.calcR;
        this.ambientG = this.calcG;
        this.ambientB = this.calcB;
    }
    
    public void specular(final int n) {
        this.colorCalc(n);
        this.specularFromCalc();
    }
    
    public void specular(final float n) {
        this.colorCalc(n);
        this.specularFromCalc();
    }
    
    public void specular(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.specularFromCalc();
    }
    
    protected void specularFromCalc() {
        this.specularR = this.calcR;
        this.specularG = this.calcG;
        this.specularB = this.calcB;
    }
    
    public void shininess(final float shininess) {
        this.shininess = shininess;
    }
    
    public void emissive(final int n) {
        this.colorCalc(n);
        this.emissiveFromCalc();
    }
    
    public void emissive(final float n) {
        this.colorCalc(n);
        this.emissiveFromCalc();
    }
    
    public void emissive(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.emissiveFromCalc();
    }
    
    protected void emissiveFromCalc() {
        this.emissiveR = this.calcR;
        this.emissiveG = this.calcG;
        this.emissiveB = this.calcB;
    }
    
    public void lights() {
        showMethodWarning("lights");
    }
    
    public void noLights() {
        showMethodWarning("noLights");
    }
    
    public void ambientLight(final float n, final float n2, final float n3) {
        showMethodWarning("ambientLight");
    }
    
    public void ambientLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        showMethodWarning("ambientLight");
    }
    
    public void directionalLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        showMethodWarning("directionalLight");
    }
    
    public void pointLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        showMethodWarning("pointLight");
    }
    
    public void spotLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11) {
        showMethodWarning("spotLight");
    }
    
    public void lightFalloff(final float n, final float n2, final float n3) {
        showMethodWarning("lightFalloff");
    }
    
    public void lightSpecular(final float n, final float n2, final float n3) {
        showMethodWarning("lightSpecular");
    }
    
    public void background(final int n) {
        this.colorCalc(n);
        this.backgroundFromCalc();
    }
    
    public void background(final int n, final float n2) {
        this.colorCalc(n, n2);
        this.backgroundFromCalc();
    }
    
    public void background(final float n) {
        this.colorCalc(n);
        this.backgroundFromCalc();
    }
    
    public void background(final float n, final float n2) {
        if (this.format == 1) {
            this.background(n);
        }
        else {
            this.colorCalc(n, n2);
            this.backgroundFromCalc();
        }
    }
    
    public void background(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.backgroundFromCalc();
    }
    
    public void background(final float n, final float n2, final float n3, final float n4) {
        this.colorCalc(n, n2, n3, n4);
        this.backgroundFromCalc();
    }
    
    protected void backgroundFromCalc() {
        this.backgroundR = this.calcR;
        this.backgroundG = this.calcG;
        this.backgroundB = this.calcB;
        this.backgroundA = ((this.format == 1) ? this.colorModeA : this.calcA);
        this.backgroundRi = this.calcRi;
        this.backgroundGi = this.calcGi;
        this.backgroundBi = this.calcBi;
        this.backgroundAi = ((this.format == 1) ? 255 : this.calcAi);
        this.backgroundAlpha = (this.format != 1 && this.calcAlpha);
        this.backgroundColor = this.calcColor;
        this.backgroundImpl();
    }
    
    public void background(final PImage pImage) {
        if (pImage.width != this.width || pImage.height != this.height) {
            throw new RuntimeException("background image must be the same size as your application");
        }
        if (pImage.format != 1 && pImage.format != 2) {
            throw new RuntimeException("background images should be RGB or ARGB");
        }
        this.backgroundColor = 0;
        this.backgroundImpl(pImage);
    }
    
    protected void backgroundImpl(final PImage pImage) {
        this.set(0, 0, pImage);
    }
    
    protected void backgroundImpl() {
        this.pushStyle();
        this.pushMatrix();
        this.resetMatrix();
        this.fill(this.backgroundColor);
        this.rect(0.0f, 0.0f, (float)this.width, (float)this.height);
        this.popMatrix();
        this.popStyle();
    }
    
    public void colorMode(final int n) {
        this.colorMode(n, this.colorModeX, this.colorModeY, this.colorModeZ, this.colorModeA);
    }
    
    public void colorMode(final int n, final float n2) {
        this.colorMode(n, n2, n2, n2, n2);
    }
    
    public void colorMode(final int n, final float n2, final float n3, final float n4) {
        this.colorMode(n, n2, n3, n4, this.colorModeA);
    }
    
    public void colorMode(final int colorMode, final float colorModeX, final float colorModeY, final float colorModeZ, final float colorModeA) {
        this.colorMode = colorMode;
        this.colorModeX = colorModeX;
        this.colorModeY = colorModeY;
        this.colorModeZ = colorModeZ;
        this.colorModeA = colorModeA;
        this.colorModeScale = (colorModeA != 1.0f || colorModeX != colorModeY || colorModeY != colorModeZ || colorModeZ != colorModeA);
        this.colorModeDefault = (this.colorMode == 1 && this.colorModeA == 255.0f && this.colorModeX == 255.0f && this.colorModeY == 255.0f && this.colorModeZ == 255.0f);
    }
    
    protected void colorCalc(final int n) {
        if ((n & 0xFF000000) == 0x0 && n <= this.colorModeX) {
            this.colorCalc((float)n);
        }
        else {
            this.colorCalcARGB(n, this.colorModeA);
        }
    }
    
    protected void colorCalc(final int n, final float n2) {
        if ((n & 0xFF000000) == 0x0 && n <= this.colorModeX) {
            this.colorCalc((float)n, n2);
        }
        else {
            this.colorCalcARGB(n, n2);
        }
    }
    
    protected void colorCalc(final float n) {
        this.colorCalc(n, this.colorModeA);
    }
    
    protected void colorCalc(float colorModeX, float colorModeA) {
        if (colorModeX > this.colorModeX) {
            colorModeX = this.colorModeX;
        }
        if (colorModeA > this.colorModeA) {
            colorModeA = this.colorModeA;
        }
        if (colorModeX < 0.0f) {
            colorModeX = 0.0f;
        }
        if (colorModeA < 0.0f) {
            colorModeA = 0.0f;
        }
        this.calcR = (this.colorModeScale ? (colorModeX / this.colorModeX) : colorModeX);
        this.calcG = this.calcR;
        this.calcB = this.calcR;
        this.calcA = (this.colorModeScale ? (colorModeA / this.colorModeA) : colorModeA);
        this.calcRi = (int)(this.calcR * 255.0f);
        this.calcGi = (int)(this.calcG * 255.0f);
        this.calcBi = (int)(this.calcB * 255.0f);
        this.calcAi = (int)(this.calcA * 255.0f);
        this.calcColor = (this.calcAi << 24 | this.calcRi << 16 | this.calcGi << 8 | this.calcBi);
        this.calcAlpha = (this.calcAi != 255);
    }
    
    protected void colorCalc(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3, this.colorModeA);
    }
    
    protected void colorCalc(float colorModeX, float colorModeY, float colorModeZ, float colorModeA) {
        if (colorModeX > this.colorModeX) {
            colorModeX = this.colorModeX;
        }
        if (colorModeY > this.colorModeY) {
            colorModeY = this.colorModeY;
        }
        if (colorModeZ > this.colorModeZ) {
            colorModeZ = this.colorModeZ;
        }
        if (colorModeA > this.colorModeA) {
            colorModeA = this.colorModeA;
        }
        if (colorModeX < 0.0f) {
            colorModeX = 0.0f;
        }
        if (colorModeY < 0.0f) {
            colorModeY = 0.0f;
        }
        if (colorModeZ < 0.0f) {
            colorModeZ = 0.0f;
        }
        if (colorModeA < 0.0f) {
            colorModeA = 0.0f;
        }
        Label_0473: {
            switch (this.colorMode) {
                case 1: {
                    if (this.colorModeScale) {
                        this.calcR = colorModeX / this.colorModeX;
                        this.calcG = colorModeY / this.colorModeY;
                        this.calcB = colorModeZ / this.colorModeZ;
                        this.calcA = colorModeA / this.colorModeA;
                        break;
                    }
                    this.calcR = colorModeX;
                    this.calcG = colorModeY;
                    this.calcB = colorModeZ;
                    this.calcA = colorModeA;
                    break;
                }
                case 3: {
                    colorModeX /= this.colorModeX;
                    colorModeY /= this.colorModeY;
                    colorModeZ /= this.colorModeZ;
                    this.calcA = (this.colorModeScale ? (colorModeA / this.colorModeA) : colorModeA);
                    if (colorModeY == 0.0f) {
                        final float calcR = colorModeZ;
                        this.calcB = calcR;
                        this.calcG = calcR;
                        this.calcR = calcR;
                        break;
                    }
                    final float n = (colorModeX - (int)colorModeX) * 6.0f;
                    final float n2 = n - (int)n;
                    final float n3 = colorModeZ * (1.0f - colorModeY);
                    final float calcB = colorModeZ * (1.0f - colorModeY * n2);
                    final float calcR2 = colorModeZ * (1.0f - colorModeY * (1.0f - n2));
                    switch ((int)n) {
                        case 0: {
                            this.calcR = colorModeZ;
                            this.calcG = calcR2;
                            this.calcB = n3;
                            break Label_0473;
                        }
                        case 1: {
                            this.calcR = calcB;
                            this.calcG = colorModeZ;
                            this.calcB = n3;
                            break Label_0473;
                        }
                        case 2: {
                            this.calcR = n3;
                            this.calcG = colorModeZ;
                            this.calcB = calcR2;
                            break Label_0473;
                        }
                        case 3: {
                            this.calcR = n3;
                            this.calcG = calcB;
                            this.calcB = colorModeZ;
                            break Label_0473;
                        }
                        case 4: {
                            this.calcR = calcR2;
                            this.calcG = n3;
                            this.calcB = colorModeZ;
                            break Label_0473;
                        }
                        case 5: {
                            this.calcR = colorModeZ;
                            this.calcG = n3;
                            this.calcB = calcB;
                            break Label_0473;
                        }
                    }
                    break;
                }
            }
        }
        this.calcRi = (int)(255.0f * this.calcR);
        this.calcGi = (int)(255.0f * this.calcG);
        this.calcBi = (int)(255.0f * this.calcB);
        this.calcAi = (int)(255.0f * this.calcA);
        this.calcColor = (this.calcAi << 24 | this.calcRi << 16 | this.calcGi << 8 | this.calcBi);
        this.calcAlpha = (this.calcAi != 255);
    }
    
    protected void colorCalcARGB(final int calcColor, final float n) {
        if (n == this.colorModeA) {
            this.calcAi = (calcColor >> 24 & 0xFF);
            this.calcColor = calcColor;
        }
        else {
            this.calcAi = (int)((calcColor >> 24 & 0xFF) * (n / this.colorModeA));
            this.calcColor = (this.calcAi << 24 | (calcColor & 0xFFFFFF));
        }
        this.calcRi = (calcColor >> 16 & 0xFF);
        this.calcGi = (calcColor >> 8 & 0xFF);
        this.calcBi = (calcColor & 0xFF);
        this.calcA = this.calcAi / 255.0f;
        this.calcR = this.calcRi / 255.0f;
        this.calcG = this.calcGi / 255.0f;
        this.calcB = this.calcBi / 255.0f;
        this.calcAlpha = (this.calcAi != 255);
    }
    
    public final int color(final int n) {
        this.colorCalc(n);
        return this.calcColor;
    }
    
    public final int color(final float n) {
        this.colorCalc(n);
        return this.calcColor;
    }
    
    public final int color(final int n, final int n2) {
        this.colorCalc(n, (float)n2);
        return this.calcColor;
    }
    
    public final int color(final int n, final float n2) {
        this.colorCalc(n, n2);
        return this.calcColor;
    }
    
    public final int color(final float n, final float n2) {
        this.colorCalc(n, n2);
        return this.calcColor;
    }
    
    public final int color(final int n, final int n2, final int n3) {
        this.colorCalc((float)n, (float)n2, (float)n3);
        return this.calcColor;
    }
    
    public final int color(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        return this.calcColor;
    }
    
    public final int color(final int n, final int n2, final int n3, final int n4) {
        this.colorCalc((float)n, (float)n2, (float)n3, (float)n4);
        return this.calcColor;
    }
    
    public final int color(final float n, final float n2, final float n3, final float n4) {
        this.colorCalc(n, n2, n3, n4);
        return this.calcColor;
    }
    
    public final float alpha(final int n) {
        final float n2 = (float)(n >> 24 & 0xFF);
        if (this.colorModeA == 255.0f) {
            return n2;
        }
        return n2 / 255.0f * this.colorModeA;
    }
    
    public final float red(final int n) {
        final float n2 = (float)(n >> 16 & 0xFF);
        if (this.colorModeDefault) {
            return n2;
        }
        return n2 / 255.0f * this.colorModeX;
    }
    
    public final float green(final int n) {
        final float n2 = (float)(n >> 8 & 0xFF);
        if (this.colorModeDefault) {
            return n2;
        }
        return n2 / 255.0f * this.colorModeY;
    }
    
    public final float blue(final int n) {
        final float n2 = (float)(n & 0xFF);
        if (this.colorModeDefault) {
            return n2;
        }
        return n2 / 255.0f * this.colorModeZ;
    }
    
    public final float hue(final int cacheHsbKey) {
        if (cacheHsbKey != this.cacheHsbKey) {
            Color.RGBtoHSB(cacheHsbKey >> 16 & 0xFF, cacheHsbKey >> 8 & 0xFF, cacheHsbKey & 0xFF, this.cacheHsbValue);
            this.cacheHsbKey = cacheHsbKey;
        }
        return this.cacheHsbValue[0] * this.colorModeX;
    }
    
    public final float saturation(final int cacheHsbKey) {
        if (cacheHsbKey != this.cacheHsbKey) {
            Color.RGBtoHSB(cacheHsbKey >> 16 & 0xFF, cacheHsbKey >> 8 & 0xFF, cacheHsbKey & 0xFF, this.cacheHsbValue);
            this.cacheHsbKey = cacheHsbKey;
        }
        return this.cacheHsbValue[1] * this.colorModeY;
    }
    
    public final float brightness(final int cacheHsbKey) {
        if (cacheHsbKey != this.cacheHsbKey) {
            Color.RGBtoHSB(cacheHsbKey >> 16 & 0xFF, cacheHsbKey >> 8 & 0xFF, cacheHsbKey & 0xFF, this.cacheHsbValue);
            this.cacheHsbKey = cacheHsbKey;
        }
        return this.cacheHsbValue[2] * this.colorModeZ;
    }
    
    public int lerpColor(final int n, final int n2, final float n3) {
        return lerpColor(n, n2, n3, this.colorMode);
    }
    
    public static int lerpColor(final int n, final int n2, final float n3, final int n4) {
        if (n4 == 1) {
            final float n5 = (float)(n >> 24 & 0xFF);
            final float n6 = (float)(n >> 16 & 0xFF);
            final float n7 = (float)(n >> 8 & 0xFF);
            final float n8 = (float)(n & 0xFF);
            return (int)(n5 + ((n2 >> 24 & 0xFF) - n5) * n3) << 24 | (int)(n6 + ((n2 >> 16 & 0xFF) - n6) * n3) << 16 | (int)(n7 + ((n2 >> 8 & 0xFF) - n7) * n3) << 8 | (int)(n8 + ((n2 & 0xFF) - n8) * n3);
        }
        if (n4 == 3) {
            if (PGraphics.lerpColorHSB1 == null) {
                PGraphics.lerpColorHSB1 = new float[3];
                PGraphics.lerpColorHSB2 = new float[3];
            }
            final float n9 = (float)(n >> 24 & 0xFF);
            final int n10 = (int)(n9 + ((n2 >> 24 & 0xFF) - n9) * n3) << 24;
            Color.RGBtoHSB(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF, PGraphics.lerpColorHSB1);
            Color.RGBtoHSB(n2 >> 16 & 0xFF, n2 >> 8 & 0xFF, n2 & 0xFF, PGraphics.lerpColorHSB2);
            return n10 | (Color.HSBtoRGB(PApplet.lerp(PGraphics.lerpColorHSB1[0], PGraphics.lerpColorHSB2[0], n3), PApplet.lerp(PGraphics.lerpColorHSB1[1], PGraphics.lerpColorHSB2[1], n3), PApplet.lerp(PGraphics.lerpColorHSB1[2], PGraphics.lerpColorHSB2[2], n3)) & 0xFFFFFF);
        }
        return 0;
    }
    
    public void beginRaw(final PGraphics raw) {
        (this.raw = raw).beginDraw();
    }
    
    public void endRaw() {
        if (this.raw != null) {
            this.flush();
            this.raw.endDraw();
            this.raw.dispose();
            this.raw = null;
        }
    }
    
    public static void showWarning(final String key) {
        if (PGraphics.warnings == null) {
            PGraphics.warnings = new HashMap<String, Object>();
        }
        if (!PGraphics.warnings.containsKey(key)) {
            System.err.println(key);
            PGraphics.warnings.put(key, new Object());
        }
    }
    
    public static void showDepthWarning(final String str) {
        showWarning(str + "() can only be used with a renderer that " + "supports 3D, such as P3D or OPENGL.");
    }
    
    public static void showDepthWarningXYZ(final String str) {
        showWarning(str + "() with x, y, and z coordinates " + "can only be used with a renderer that " + "supports 3D, such as P3D or OPENGL. " + "Use a version without a z-coordinate instead.");
    }
    
    public static void showMethodWarning(final String str) {
        showWarning(str + "() is not available with this renderer.");
    }
    
    public static void showVariationWarning(final String str) {
        showWarning(str + " is not available with this renderer.");
    }
    
    public static void showMissingWarning(final String str) {
        showWarning(str + "(), or this particular variation of it, " + "is not available with this renderer.");
    }
    
    public static void showException(final String message) {
        throw new RuntimeException(message);
    }
    
    protected void defaultFontOrDeath(final String s) {
        this.defaultFontOrDeath(s, 12.0f);
    }
    
    protected void defaultFontOrDeath(final String str, final float n) {
        if (this.parent != null) {
            this.textFont = this.parent.createDefaultFont(n);
            return;
        }
        throw new RuntimeException("Use textFont() before " + str + "()");
    }
    
    public boolean displayable() {
        return true;
    }
    
    public boolean is2D() {
        return true;
    }
    
    public boolean is3D() {
        return false;
    }
    
    protected String[] getSupportedShapeFormats() {
        showMissingWarning("getSupportedShapeFormats");
        return null;
    }
    
    protected PShape loadShape(final String s, final Object o) {
        showMissingWarning("loadShape");
        return null;
    }
    
    protected PShape createShape(final int n, final Object o) {
        showMissingWarning("createShape");
        return null;
    }
    
    public void screenBlend(final int n) {
        showMissingWarning("screenBlend");
    }
    
    public void textureBlend(final int n) {
        showMissingWarning("textureBlend");
    }
    
    public PShape beginRecord() {
        showMissingWarning("beginRecord");
        return null;
    }
    
    public void endRecord() {
        showMissingWarning("endRecord");
    }
    
    public boolean isRecording() {
        showMissingWarning("isRecording");
        return false;
    }
    
    public void mergeShapes(final boolean b) {
        showMissingWarning("mergeShapes");
    }
    
    public void shapeName(final String s) {
        showMissingWarning("shapeName");
    }
    
    public void autoNormal(final boolean autoNormal) {
        this.autoNormal = autoNormal;
    }
    
    public void matrixMode(final int n) {
        showMissingWarning("matrixMode");
    }
    
    public void beginText() {
        showMissingWarning("beginText");
    }
    
    public void endText() {
        showMissingWarning("endText");
    }
    
    public void texture(final PImage... array) {
        showMissingWarning("texture");
    }
    
    public void vertex(final float... array) {
        showMissingWarning("vertex");
    }
    
    static {
        sinLUT = new float[720];
        cosLUT = new float[720];
        for (int i = 0; i < 720; ++i) {
            PGraphics.sinLUT[i] = (float)Math.sin(i * 0.017453292f * 0.5f);
            PGraphics.cosLUT[i] = (float)Math.cos(i * 0.017453292f * 0.5f);
        }
    }
}

 
 Upload and Decompile  

 Twitter   Facebook   Stumbleupon   LinkedIn
Select a decompiler 
  Procyon - fast decompiler for modern Java
  CFR - very good and well-supported decompiler for modern Java
  JDCore (very fast)
  Jadx, fast and with Android support
  Fernflower
  JAD (very fast, but outdated)
Privacy Policy
 