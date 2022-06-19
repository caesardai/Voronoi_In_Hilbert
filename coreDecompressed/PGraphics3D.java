package processing.core;

import java.awt.image.ColorModel;
import java.util.Arrays;
import java.awt.image.ImageProducer;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DirectColorModel;

public class PGraphics3D extends PGraphics
{
    public float[] zbuffer;
    public PMatrix3D modelview;
    public PMatrix3D modelviewInv;
    protected boolean sizeChanged;
    public PMatrix3D camera;
    protected PMatrix3D cameraInv;
    public float cameraFOV;
    public float cameraX;
    public float cameraY;
    public float cameraZ;
    public float cameraNear;
    public float cameraFar;
    public float cameraAspect;
    public PMatrix3D projection;
    public static final int MAX_LIGHTS = 8;
    public int lightCount;
    public int[] lightType;
    public PVector[] lightPosition;
    public PVector[] lightNormal;
    public float[] lightFalloffConstant;
    public float[] lightFalloffLinear;
    public float[] lightFalloffQuadratic;
    public float[] lightSpotAngle;
    public float[] lightSpotAngleCos;
    public float[] lightSpotConcentration;
    public float[][] lightDiffuse;
    public float[][] lightSpecular;
    public float[] currentLightSpecular;
    public float currentLightFalloffConstant;
    public float currentLightFalloffLinear;
    public float currentLightFalloffQuadratic;
    public static final int TRI_DIFFUSE_R = 0;
    public static final int TRI_DIFFUSE_G = 1;
    public static final int TRI_DIFFUSE_B = 2;
    public static final int TRI_DIFFUSE_A = 3;
    public static final int TRI_SPECULAR_R = 4;
    public static final int TRI_SPECULAR_G = 5;
    public static final int TRI_SPECULAR_B = 6;
    public static final int TRI_COLOR_COUNT = 7;
    private boolean lightingDependsOnVertexPosition;
    static final int LIGHT_AMBIENT_R = 0;
    static final int LIGHT_AMBIENT_G = 1;
    static final int LIGHT_AMBIENT_B = 2;
    static final int LIGHT_DIFFUSE_R = 3;
    static final int LIGHT_DIFFUSE_G = 4;
    static final int LIGHT_DIFFUSE_B = 5;
    static final int LIGHT_SPECULAR_R = 6;
    static final int LIGHT_SPECULAR_G = 7;
    static final int LIGHT_SPECULAR_B = 8;
    static final int LIGHT_COLOR_COUNT = 9;
    protected float[] tempLightingContribution;
    protected PVector lightTriangleNorm;
    protected boolean manipulatingCamera;
    float[][] matrixStack;
    float[][] matrixInvStack;
    int matrixStackDepth;
    protected int matrixMode;
    float[][] pmatrixStack;
    int pmatrixStackDepth;
    protected PMatrix3D forwardTransform;
    protected PMatrix3D reverseTransform;
    protected float leftScreen;
    protected float rightScreen;
    protected float topScreen;
    protected float bottomScreen;
    protected float nearPlane;
    private boolean frustumMode;
    protected static boolean s_enableAccurateTextures;
    public PSmoothTriangle smoothTriangle;
    protected int shapeFirst;
    protected int shapeLast;
    protected int shapeLastPlusClipped;
    protected int[] vertexOrder;
    protected int pathCount;
    protected int[] pathOffset;
    protected int[] pathLength;
    protected static final int VERTEX1 = 0;
    protected static final int VERTEX2 = 1;
    protected static final int VERTEX3 = 2;
    protected static final int STROKE_COLOR = 1;
    protected static final int TEXTURE_INDEX = 3;
    protected static final int POINT_FIELD_COUNT = 2;
    protected static final int LINE_FIELD_COUNT = 2;
    protected static final int TRIANGLE_FIELD_COUNT = 4;
    static final int DEFAULT_POINTS = 512;
    protected int[][] points;
    protected int pointCount;
    static final int DEFAULT_LINES = 512;
    public PLine line;
    protected int[][] lines;
    protected int lineCount;
    static final int DEFAULT_TRIANGLES = 256;
    public PTriangle triangle;
    protected int[][] triangles;
    protected float[][][] triangleColors;
    protected int triangleCount;
    static final int DEFAULT_TEXTURES = 3;
    protected PImage[] textures;
    int textureIndex;
    DirectColorModel cm;
    MemoryImageSource mis;
    float[] worldNormal;
    PVector lightPositionVec;
    PVector lightDirectionVec;
    
    public PGraphics3D() {
        this.lightCount = 0;
        this.tempLightingContribution = new float[9];
        this.lightTriangleNorm = new PVector();
        this.matrixStack = new float[32][16];
        this.matrixInvStack = new float[32][16];
        this.matrixMode = 1;
        this.pmatrixStack = new float[32][16];
        this.frustumMode = false;
        this.vertexOrder = new int[512];
        this.pathOffset = new int[64];
        this.pathLength = new int[64];
        this.points = new int[512][2];
        this.lines = new int[512][2];
        this.triangles = new int[256][4];
        this.triangleColors = new float[256][3][7];
        this.textures = new PImage[3];
        this.worldNormal = new float[4];
        this.lightPositionVec = new PVector();
        this.lightDirectionVec = new PVector();
    }
    
    @Override
    public void setSize(final int width, final int height) {
        this.width = width;
        this.height = height;
        this.width1 = this.width - 1;
        this.height1 = this.height - 1;
        this.allocate();
        this.reapplySettings();
        this.lightType = new int[8];
        this.lightPosition = new PVector[8];
        this.lightNormal = new PVector[8];
        for (int i = 0; i < 8; ++i) {
            this.lightPosition[i] = new PVector();
            this.lightNormal[i] = new PVector();
        }
        this.lightDiffuse = new float[8][3];
        this.lightSpecular = new float[8][3];
        this.lightFalloffConstant = new float[8];
        this.lightFalloffLinear = new float[8];
        this.lightFalloffQuadratic = new float[8];
        this.lightSpotAngle = new float[8];
        this.lightSpotAngleCos = new float[8];
        this.lightSpotConcentration = new float[8];
        this.currentLightSpecular = new float[3];
        this.projection = new PMatrix3D();
        this.modelview = new PMatrix3D();
        this.modelviewInv = new PMatrix3D();
        this.forwardTransform = this.modelview;
        this.reverseTransform = this.modelviewInv;
        this.cameraFOV = 1.0471976f;
        this.cameraX = this.width / 2.0f;
        this.cameraY = this.height / 2.0f;
        this.cameraZ = this.cameraY / (float)Math.tan(this.cameraFOV / 2.0f);
        this.cameraNear = this.cameraZ / 10.0f;
        this.cameraFar = this.cameraZ * 10.0f;
        this.cameraAspect = this.width / (float)this.height;
        this.camera = new PMatrix3D();
        this.cameraInv = new PMatrix3D();
        this.sizeChanged = true;
    }
    
    @Override
    protected void allocate() {
        this.pixelCount = this.width * this.height;
        this.pixels = new int[this.pixelCount];
        this.zbuffer = new float[this.pixelCount];
        if (this.primarySurface) {
            this.cm = new DirectColorModel(32, 16711680, 65280, 255);
            (this.mis = new MemoryImageSource(this.width, this.height, this.pixels, 0, this.width)).setFullBufferUpdates(true);
            this.mis.setAnimated(true);
            this.image = Toolkit.getDefaultToolkit().createImage(this.mis);
        }
        else {
            Arrays.fill(this.zbuffer, Float.MAX_VALUE);
        }
        this.line = new PLine((PGraphics)this);
        this.triangle = new PTriangle(this);
        this.smoothTriangle = new PSmoothTriangle(this);
    }
    
    @Override
    public void beginDraw() {
        if (!this.settingsInited) {
            this.defaultSettings();
        }
        if (this.sizeChanged) {
            this.camera();
            this.perspective();
            this.sizeChanged = false;
        }
        this.resetMatrix();
        this.vertexCount = 0;
        this.modelview.set((PMatrix)this.camera);
        this.modelviewInv.set((PMatrix)this.cameraInv);
        this.lightCount = 0;
        this.lightingDependsOnVertexPosition = false;
        this.lightFalloff(1.0f, 0.0f, 0.0f);
        this.lightSpecular(0.0f, 0.0f, 0.0f);
        this.shapeFirst = 0;
        Arrays.fill(this.textures, null);
        this.textureIndex = 0;
        this.normal(0.0f, 0.0f, 1.0f);
    }
    
    @Override
    public void endDraw() {
        if (this.hints[5]) {
            this.flush();
        }
        if (this.mis != null) {
            this.mis.newPixels(this.pixels, this.cm, 0, this.width);
        }
        this.updatePixels();
    }
    
    @Override
    protected void defaultSettings() {
        super.defaultSettings();
        this.manipulatingCamera = false;
        this.forwardTransform = this.modelview;
        this.reverseTransform = this.modelviewInv;
        this.camera();
        this.perspective();
        this.textureMode(2);
        this.emissive(0.0f);
        this.specular(0.5f);
        this.shininess(1.0f);
    }
    
    @Override
    public void hint(final int n) {
        if (n == -5) {
            this.flush();
        }
        else if (n == 4 && this.zbuffer != null) {
            Arrays.fill(this.zbuffer, Float.MAX_VALUE);
        }
        super.hint(n);
    }
    
    @Override
    public void beginShape(final int shape) {
        this.shape = shape;
        if (this.hints[5]) {
            this.shapeFirst = this.vertexCount;
            this.shapeLast = 0;
        }
        else {
            this.vertexCount = 0;
            if (this.line != null) {
                this.line.reset();
            }
            this.lineCount = 0;
            if (this.triangle != null) {
                this.triangle.reset();
            }
            this.triangleCount = 0;
        }
        this.textureImage = null;
        this.curveVertexCount = 0;
        this.normalMode = 0;
    }
    
    @Override
    public void texture(final PImage textureImage) {
        this.textureImage = textureImage;
        if (this.textureIndex == this.textures.length - 1) {
            this.textures = (PImage[])PApplet.expand(this.textures);
        }
        if (this.textures[this.textureIndex] != null) {
            ++this.textureIndex;
        }
        this.textures[this.textureIndex] = textureImage;
    }
    
    @Override
    public void vertex(final float n, final float n2) {
        this.vertex(n, n2, 0.0f);
    }
    
    @Override
    public void vertex(final float n, final float n2, final float n3, final float n4) {
        this.vertex(n, n2, 0.0f, n3, n4);
    }
    
    @Override
    public void endShape(final int n) {
        this.shapeLast = this.vertexCount;
        this.shapeLastPlusClipped = this.shapeLast;
        if (this.vertexCount == 0) {
            this.shape = 0;
            return;
        }
        this.endShapeModelToCamera(this.shapeFirst, this.shapeLast);
        if (this.stroke) {
            this.endShapeStroke(n);
        }
        if (this.fill || this.textureImage != null) {
            this.endShapeFill();
        }
        this.endShapeLighting(this.lightCount > 0 && this.fill);
        this.endShapeCameraToScreen(this.shapeFirst, this.shapeLastPlusClipped);
        if (!this.hints[5]) {
            if ((this.fill || this.textureImage != null) && this.triangleCount > 0) {
                this.renderTriangles(0, this.triangleCount);
                if (this.raw != null) {
                    this.rawTriangles(0, this.triangleCount);
                }
                this.triangleCount = 0;
            }
            if (this.stroke) {
                if (this.pointCount > 0) {
                    this.renderPoints(0, this.pointCount);
                    if (this.raw != null) {
                        this.rawPoints(0, this.pointCount);
                    }
                    this.pointCount = 0;
                }
                if (this.lineCount > 0) {
                    this.renderLines(0, this.lineCount);
                    if (this.raw != null) {
                        this.rawLines(0, this.lineCount);
                    }
                    this.lineCount = 0;
                }
            }
            this.pathCount = 0;
        }
        this.shape = 0;
    }
    
    protected void endShapeModelToCamera(final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final float[] array = this.vertices[i];
            array[21] = this.modelview.m00 * array[0] + this.modelview.m01 * array[1] + this.modelview.m02 * array[2] + this.modelview.m03;
            array[22] = this.modelview.m10 * array[0] + this.modelview.m11 * array[1] + this.modelview.m12 * array[2] + this.modelview.m13;
            array[23] = this.modelview.m20 * array[0] + this.modelview.m21 * array[1] + this.modelview.m22 * array[2] + this.modelview.m23;
            array[24] = this.modelview.m30 * array[0] + this.modelview.m31 * array[1] + this.modelview.m32 * array[2] + this.modelview.m33;
            if (array[24] != 0.0f && array[24] != 1.0f) {
                final float[] array2 = array;
                final int n3 = 21;
                array2[n3] /= array[24];
                final float[] array3 = array;
                final int n4 = 22;
                array3[n4] /= array[24];
                final float[] array4 = array;
                final int n5 = 23;
                array4[n5] /= array[24];
            }
            array[24] = 1.0f;
        }
    }
    
    protected void endShapeStroke(final int n) {
        switch (this.shape) {
            case 2: {
                for (int shapeLast = this.shapeLast, i = this.shapeFirst; i < shapeLast; ++i) {
                    this.addPoint(i);
                }
                break;
            }
            case 4: {
                final int lineCount = this.lineCount;
                final int n2 = this.shapeLast - 1;
                if (this.shape != 4) {
                    this.addLineBreak();
                }
                for (int j = this.shapeFirst; j < n2; j += 2) {
                    if (this.shape == 4) {
                        this.addLineBreak();
                    }
                    this.addLine(j, j + 1);
                }
                if (n == 2) {
                    this.addLine(n2, this.lines[lineCount][0]);
                }
                break;
            }
            case 9: {
                for (int k = this.shapeFirst; k < this.shapeLast - 2; k += 3) {
                    this.addLineBreak();
                    this.addLine(k + 0, k + 1);
                    this.addLine(k + 1, k + 2);
                    this.addLine(k + 2, k + 0);
                }
                break;
            }
            case 10: {
                final int n3 = this.shapeLast - 1;
                this.addLineBreak();
                for (int l = this.shapeFirst; l < n3; ++l) {
                    this.addLine(l, l + 1);
                }
                for (int n4 = this.shapeLast - 2, shapeFirst = this.shapeFirst; shapeFirst < n4; ++shapeFirst) {
                    this.addLineBreak();
                    this.addLine(shapeFirst, shapeFirst + 2);
                }
                break;
            }
            case 11: {
                for (int n5 = this.shapeFirst + 1; n5 < this.shapeLast; ++n5) {
                    this.addLineBreak();
                    this.addLine(this.shapeFirst, n5);
                }
                this.addLineBreak();
                for (int n6 = this.shapeFirst + 1; n6 < this.shapeLast - 1; ++n6) {
                    this.addLine(n6, n6 + 1);
                }
                this.addLine(this.shapeLast - 1, this.shapeFirst + 1);
                break;
            }
            case 16: {
                for (int shapeFirst2 = this.shapeFirst; shapeFirst2 < this.shapeLast; shapeFirst2 += 4) {
                    this.addLineBreak();
                    this.addLine(shapeFirst2 + 0, shapeFirst2 + 1);
                    this.addLine(shapeFirst2 + 1, shapeFirst2 + 2);
                    this.addLine(shapeFirst2 + 2, shapeFirst2 + 3);
                    this.addLine(shapeFirst2 + 3, shapeFirst2 + 0);
                }
                break;
            }
            case 17: {
                for (int shapeFirst3 = this.shapeFirst; shapeFirst3 < this.shapeLast - 3; shapeFirst3 += 2) {
                    this.addLineBreak();
                    this.addLine(shapeFirst3 + 0, shapeFirst3 + 2);
                    this.addLine(shapeFirst3 + 2, shapeFirst3 + 3);
                    this.addLine(shapeFirst3 + 3, shapeFirst3 + 1);
                    this.addLine(shapeFirst3 + 1, shapeFirst3 + 0);
                }
                break;
            }
            case 20: {
                final int n7 = this.shapeLast - 1;
                this.addLineBreak();
                for (int shapeFirst4 = this.shapeFirst; shapeFirst4 < n7; ++shapeFirst4) {
                    this.addLine(shapeFirst4, shapeFirst4 + 1);
                }
                if (n == 2) {
                    this.addLine(n7, this.shapeFirst);
                    break;
                }
                break;
            }
        }
    }
    
    protected void endShapeFill() {
        switch (this.shape) {
            case 11: {
                for (int n = this.shapeLast - 1, i = this.shapeFirst + 1; i < n; ++i) {
                    this.addTriangle(this.shapeFirst, i, i + 1);
                }
                break;
            }
            case 9: {
                for (int n2 = this.shapeLast - 2, j = this.shapeFirst; j < n2; j += 3) {
                    if (j % 2 == 0) {
                        this.addTriangle(j, j + 2, j + 1);
                    }
                    else {
                        this.addTriangle(j, j + 1, j + 2);
                    }
                }
                break;
            }
            case 10: {
                for (int n3 = this.shapeLast - 2, k = this.shapeFirst; k < n3; ++k) {
                    if (k % 2 == 0) {
                        this.addTriangle(k, k + 2, k + 1);
                    }
                    else {
                        this.addTriangle(k, k + 1, k + 2);
                    }
                }
                break;
            }
            case 16: {
                for (int n4 = this.vertexCount - 3, l = this.shapeFirst; l < n4; l += 4) {
                    this.addTriangle(l, l + 1, l + 2);
                    this.addTriangle(l, l + 2, l + 3);
                }
                break;
            }
            case 17: {
                for (int n5 = this.vertexCount - 3, shapeFirst = this.shapeFirst; shapeFirst < n5; shapeFirst += 2) {
                    this.addTriangle(shapeFirst + 0, shapeFirst + 2, shapeFirst + 1);
                    this.addTriangle(shapeFirst + 2, shapeFirst + 3, shapeFirst + 1);
                }
                break;
            }
            case 20: {
                this.addPolygonTriangles();
                break;
            }
        }
    }
    
    protected void endShapeLighting(final boolean b) {
        if (b) {
            if (!this.lightingDependsOnVertexPosition && this.normalMode == 1) {
                this.calcLightingContribution(this.shapeFirst, this.tempLightingContribution);
                for (int i = 0; i < this.triangleCount; ++i) {
                    this.lightTriangle(i, this.tempLightingContribution);
                }
            }
            else {
                for (int j = 0; j < this.triangleCount; ++j) {
                    this.lightTriangle(j);
                }
            }
        }
        else {
            for (int k = 0; k < this.triangleCount; ++k) {
                this.copyPrelitVertexColor(k, this.triangles[k][0], 0);
                this.copyPrelitVertexColor(k, this.triangles[k][1], 1);
                this.copyPrelitVertexColor(k, this.triangles[k][2], 2);
            }
        }
    }
    
    protected void endShapeCameraToScreen(final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final float[] array = this.vertices[i];
            float n3 = this.projection.m00 * array[21] + this.projection.m01 * array[22] + this.projection.m02 * array[23] + this.projection.m03 * array[24];
            float n4 = this.projection.m10 * array[21] + this.projection.m11 * array[22] + this.projection.m12 * array[23] + this.projection.m13 * array[24];
            float n5 = this.projection.m20 * array[21] + this.projection.m21 * array[22] + this.projection.m22 * array[23] + this.projection.m23 * array[24];
            final float n6 = this.projection.m30 * array[21] + this.projection.m31 * array[22] + this.projection.m32 * array[23] + this.projection.m33 * array[24];
            if (n6 != 0.0f && n6 != 1.0f) {
                n3 /= n6;
                n4 /= n6;
                n5 /= n6;
            }
            array[18] = this.width * (1.0f + n3) / 2.0f;
            array[19] = this.height * (1.0f + n4) / 2.0f;
            array[20] = (n5 + 1.0f) / 2.0f;
        }
    }
    
    protected void addPoint(final int n) {
        if (this.pointCount == this.points.length) {
            final int[][] points = new int[this.pointCount << 1][2];
            System.arraycopy(this.points, 0, points, 0, this.pointCount);
            this.points = points;
        }
        this.points[this.pointCount][0] = n;
        this.points[this.pointCount][1] = this.strokeColor;
        ++this.pointCount;
    }
    
    protected void renderPoints(final int n, final int n2) {
        if (this.strokeWeight != 1.0f) {
            for (int i = n; i < n2; ++i) {
                final float[] array = this.vertices[this.points[i][0]];
                this.renderLineVertices(array, array);
            }
        }
        else {
            for (int j = n; j < n2; ++j) {
                final float[] array2 = this.vertices[this.points[j][0]];
                final int n3 = (int)(array2[18] + 0.4999f);
                final int n4 = (int)(array2[19] + 0.4999f);
                if (n3 >= 0 && n3 < this.width && n4 >= 0 && n4 < this.height) {
                    final int n5 = n4 * this.width + n3;
                    this.pixels[n5] = this.points[j][1];
                    this.zbuffer[n5] = array2[20];
                }
            }
        }
    }
    
    protected void rawPoints(final int n, final int n2) {
        this.raw.colorMode(1, 1.0f);
        this.raw.noFill();
        this.raw.strokeWeight(this.vertices[this.lines[n][0]][17]);
        this.raw.beginShape(2);
        for (int i = n; i < n2; ++i) {
            final float[] array = this.vertices[this.lines[i][0]];
            if (this.raw.is3D()) {
                if (array[24] != 0.0f) {
                    this.raw.stroke(array[13], array[14], array[15], array[16]);
                    this.raw.vertex(array[21] / array[24], array[22] / array[24], array[23] / array[24]);
                }
            }
            else {
                this.raw.stroke(array[13], array[14], array[15], array[16]);
                this.raw.vertex(array[18], array[19]);
            }
        }
        this.raw.endShape();
    }
    
    protected final void addLineBreak() {
        if (this.pathCount == this.pathOffset.length) {
            this.pathOffset = PApplet.expand(this.pathOffset);
            this.pathLength = PApplet.expand(this.pathLength);
        }
        this.pathOffset[this.pathCount] = this.lineCount;
        this.pathLength[this.pathCount] = 0;
        ++this.pathCount;
    }
    
    protected void addLine(final int n, final int n2) {
        this.addLineWithClip(n, n2);
    }
    
    protected final void addLineWithClip(final int n, final int n2) {
        final float n3 = this.vertices[n][23];
        final float n4 = this.vertices[n2][23];
        if (n3 > this.cameraNear) {
            if (n4 > this.cameraNear) {
                return;
            }
            this.addLineWithoutClip(this.interpolateClipVertex(n, n2), n2);
        }
        else {
            if (n4 <= this.cameraNear) {
                this.addLineWithoutClip(n, n2);
                return;
            }
            this.addLineWithoutClip(n, this.interpolateClipVertex(n, n2));
        }
    }
    
    protected final void addLineWithoutClip(final int n, final int n2) {
        if (this.lineCount == this.lines.length) {
            final int[][] lines = new int[this.lineCount << 1][2];
            System.arraycopy(this.lines, 0, lines, 0, this.lineCount);
            this.lines = lines;
        }
        this.lines[this.lineCount][0] = n;
        this.lines[this.lineCount][1] = n2;
        ++this.lineCount;
        final int[] pathLength = this.pathLength;
        final int n3 = this.pathCount - 1;
        ++pathLength[n3];
    }
    
    protected void renderLines(final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            this.renderLineVertices(this.vertices[this.lines[i][0]], this.vertices[this.lines[i][1]]);
        }
    }
    
    protected void renderLineVertices(final float[] array, final float[] array2) {
        if (array[17] > 1.25f || array[17] < 0.75f) {
            final float n = array[18];
            float n2 = array[19];
            final float n3 = array2[18];
            float n4 = array2[19];
            final float n5 = array[17] / 2.0f;
            if (n == n3 && n2 == n4) {
                n2 -= n5;
                n4 += n5;
            }
            final float n6 = n3 - n + 1.0E-4f;
            final float n7 = n4 - n2 + 1.0E-4f;
            final float n8 = n5 / (float)Math.sqrt(n6 * n6 + n7 * n7);
            final float n9 = n8 * n7;
            final float n10 = n8 * n6;
            final float n11 = n8 * n7;
            final float n12 = n8 * n6;
            final float n13 = n + n9;
            final float n14 = n2 - n10;
            final float n15 = n - n9;
            final float n16 = n2 + n10;
            final float n17 = n3 + n11;
            final float n18 = n4 - n12;
            final float n19 = n3 - n11;
            final float n20 = n4 + n12;
            if (this.smooth) {
                this.smoothTriangle.reset(3);
                this.smoothTriangle.smooth = true;
                this.smoothTriangle.interpARGB = true;
                this.smoothTriangle.setVertices(n13, n14, array[20], n19, n20, array2[20], n15, n16, array[20]);
                this.smoothTriangle.setIntensities(array[13], array[14], array[15], array[16], array2[13], array2[14], array2[15], array2[16], array[13], array[14], array[15], array[16]);
                this.smoothTriangle.render();
                this.smoothTriangle.setVertices(n13, n14, array[20], n19, n20, array2[20], n17, n18, array2[20]);
                this.smoothTriangle.setIntensities(array[13], array[14], array[15], array[16], array2[13], array2[14], array2[15], array2[16], array2[13], array2[14], array2[15], array2[16]);
                this.smoothTriangle.render();
            }
            else {
                this.triangle.reset();
                this.triangle.setVertices(n13, n14, array[20], n19, n20, array2[20], n15, n16, array[20]);
                this.triangle.setIntensities(array[13], array[14], array[15], array[16], array2[13], array2[14], array2[15], array2[16], array[13], array[14], array[15], array[16]);
                this.triangle.render();
                this.triangle.setVertices(n13, n14, array[20], n19, n20, array2[20], n17, n18, array2[20]);
                this.triangle.setIntensities(array[13], array[14], array[15], array[16], array2[13], array2[14], array2[15], array2[16], array2[13], array2[14], array2[15], array2[16]);
                this.triangle.render();
            }
        }
        else {
            this.line.reset();
            this.line.setIntensities(array[13], array[14], array[15], array[16], array2[13], array2[14], array2[15], array2[16]);
            this.line.setVertices(array[18], array[19], array[20], array2[18], array2[19], array2[20]);
            this.line.draw();
        }
    }
    
    protected void rawLines(final int n, final int n2) {
        this.raw.colorMode(1, 1.0f);
        this.raw.noFill();
        this.raw.beginShape(4);
        for (int i = n; i < n2; ++i) {
            final float[] array = this.vertices[this.lines[i][0]];
            final float[] array2 = this.vertices[this.lines[i][1]];
            this.raw.strokeWeight(this.vertices[this.lines[i][1]][17]);
            if (this.raw.is3D()) {
                if (array[24] != 0.0f && array2[24] != 0.0f) {
                    this.raw.stroke(array[13], array[14], array[15], array[16]);
                    this.raw.vertex(array[21] / array[24], array[22] / array[24], array[23] / array[24]);
                    this.raw.stroke(array2[13], array2[14], array2[15], array2[16]);
                    this.raw.vertex(array2[21] / array2[24], array2[22] / array2[24], array2[23] / array2[24]);
                }
            }
            else if (this.raw.is2D()) {
                this.raw.stroke(array[13], array[14], array[15], array[16]);
                this.raw.vertex(array[18], array[19]);
                this.raw.stroke(array2[13], array2[14], array2[15], array2[16]);
                this.raw.vertex(array2[18], array2[19]);
            }
        }
        this.raw.endShape();
    }
    
    protected void addTriangle(final int n, final int n2, final int n3) {
        this.addTriangleWithClip(n, n2, n3);
    }
    
    protected final void addTriangleWithClip(final int n, final int n2, final int n3) {
        boolean b = false;
        boolean b2 = false;
        int n4 = 0;
        this.cameraNear = -8.0f;
        if (this.vertices[n][23] > this.cameraNear) {
            b = true;
            ++n4;
        }
        if (this.vertices[n2][23] > this.cameraNear) {
            b2 = true;
            ++n4;
        }
        if (this.vertices[n3][23] > this.cameraNear) {
            ++n4;
        }
        if (n4 == 0) {
            this.addTriangleWithoutClip(n, n2, n3);
        }
        else if (n4 != 3) {
            if (n4 == 2) {
                int n5;
                int n6;
                int n7;
                if (!b) {
                    n5 = n;
                    n6 = n2;
                    n7 = n3;
                }
                else if (!b2) {
                    n5 = n2;
                    n6 = n;
                    n7 = n3;
                }
                else {
                    n5 = n3;
                    n6 = n2;
                    n7 = n;
                }
                this.addTriangleWithoutClip(n5, this.interpolateClipVertex(n5, n6), this.interpolateClipVertex(n5, n7));
            }
            else {
                int n8;
                int n9;
                int n10;
                if (b) {
                    n8 = n3;
                    n9 = n2;
                    n10 = n;
                }
                else if (b2) {
                    n8 = n;
                    n9 = n3;
                    n10 = n2;
                }
                else {
                    n8 = n;
                    n9 = n2;
                    n10 = n3;
                }
                final int interpolateClipVertex = this.interpolateClipVertex(n8, n10);
                final int interpolateClipVertex2 = this.interpolateClipVertex(n9, n10);
                this.addTriangleWithoutClip(n8, interpolateClipVertex, n9);
                this.addTriangleWithoutClip(n9, interpolateClipVertex, interpolateClipVertex2);
            }
        }
    }
    
    protected final int interpolateClipVertex(final int n, final int n2) {
        float[] array;
        float[] array2;
        if (this.vertices[n][23] < this.vertices[n2][23]) {
            array = this.vertices[n2];
            array2 = this.vertices[n];
        }
        else {
            array = this.vertices[n];
            array2 = this.vertices[n2];
        }
        final float n3 = array[23];
        final float n4 = array2[23];
        final float n5 = n3 - n4;
        if (n5 == 0.0f) {
            return n;
        }
        final float n6 = (this.cameraNear - n4) / n5;
        final float n7 = 1.0f - n6;
        this.vertex(n6 * array[0] + n7 * array2[0], n6 * array[1] + n7 * array2[1], n6 * array[2] + n7 * array2[2]);
        final int n8 = this.vertexCount - 1;
        ++this.shapeLastPlusClipped;
        final float[] array3 = this.vertices[n8];
        array3[18] = n6 * array[18] + n7 * array2[18];
        array3[19] = n6 * array[19] + n7 * array2[19];
        array3[20] = n6 * array[20] + n7 * array2[20];
        array3[21] = n6 * array[21] + n7 * array2[21];
        array3[22] = n6 * array[22] + n7 * array2[22];
        array3[23] = n6 * array[23] + n7 * array2[23];
        array3[24] = n6 * array[24] + n7 * array2[24];
        array3[3] = n6 * array[3] + n7 * array2[3];
        array3[4] = n6 * array[4] + n7 * array2[4];
        array3[5] = n6 * array[5] + n7 * array2[5];
        array3[6] = n6 * array[6] + n7 * array2[6];
        array3[7] = n6 * array[7] + n7 * array2[7];
        array3[8] = n6 * array[8] + n7 * array2[8];
        array3[13] = n6 * array[13] + n7 * array2[13];
        array3[14] = n6 * array[14] + n7 * array2[14];
        array3[15] = n6 * array[15] + n7 * array2[15];
        array3[16] = n6 * array[16] + n7 * array2[16];
        array3[9] = n6 * array[9] + n7 * array2[9];
        array3[10] = n6 * array[10] + n7 * array2[10];
        array3[11] = n6 * array[11] + n7 * array2[11];
        array3[25] = n6 * array[25] + n7 * array2[25];
        array3[26] = n6 * array[26] + n7 * array2[26];
        array3[27] = n6 * array[27] + n7 * array2[27];
        array3[28] = n6 * array[28] + n7 * array2[28];
        array3[29] = n6 * array[29] + n7 * array2[29];
        array3[30] = n6 * array[30] + n7 * array2[30];
        array3[32] = n6 * array[32] + n7 * array2[32];
        array3[33] = n6 * array[33] + n7 * array2[33];
        array3[34] = n6 * array[34] + n7 * array2[34];
        array3[31] = n6 * array[31] + n7 * array2[31];
        array3[35] = 0.0f;
        return n8;
    }
    
    protected final void addTriangleWithoutClip(final int n, final int n2, final int n3) {
        if (this.triangleCount == this.triangles.length) {
            final int[][] triangles = new int[this.triangleCount << 1][4];
            System.arraycopy(this.triangles, 0, triangles, 0, this.triangleCount);
            this.triangles = triangles;
            final float[][][] triangleColors = new float[this.triangleCount << 1][3][7];
            System.arraycopy(this.triangleColors, 0, triangleColors, 0, this.triangleCount);
            this.triangleColors = triangleColors;
        }
        this.triangles[this.triangleCount][0] = n;
        this.triangles[this.triangleCount][1] = n2;
        this.triangles[this.triangleCount][2] = n3;
        if (this.textureImage == null) {
            this.triangles[this.triangleCount][3] = -1;
        }
        else {
            this.triangles[this.triangleCount][3] = this.textureIndex;
        }
        ++this.triangleCount;
    }
    
    protected void addPolygonTriangles() {
        if (this.vertexOrder.length != this.vertices.length) {
            final int[] vertexOrder = new int[this.vertices.length];
            PApplet.arrayCopy(this.vertexOrder, vertexOrder, this.vertexOrder.length);
            this.vertexOrder = vertexOrder;
        }
        int n = 0;
        int n2 = 1;
        float n3 = 0.0f;
        int n4 = this.shapeLast - 1;
        for (int i = this.shapeFirst; i < this.shapeLast; n4 = i++) {
            n3 += this.vertices[i][n] * this.vertices[n4][n2] - this.vertices[n4][n] * this.vertices[i][n2];
        }
        if (n3 == 0.0f) {
            boolean b = false;
            boolean b2 = false;
            for (int j = this.shapeFirst; j < this.shapeLast; ++j) {
                for (int k = j; k < this.shapeLast; ++k) {
                    if (this.vertices[j][0] != this.vertices[k][0]) {
                        b = true;
                    }
                    if (this.vertices[j][1] != this.vertices[k][1]) {
                        b2 = true;
                    }
                }
            }
            if (b) {
                n2 = 2;
            }
            else {
                if (!b2) {
                    return;
                }
                n = 1;
                n2 = 2;
            }
            int n5 = this.shapeLast - 1;
            for (int l = this.shapeFirst; l < this.shapeLast; n5 = l++) {
                n3 += this.vertices[l][n] * this.vertices[n5][n2] - this.vertices[n5][n] * this.vertices[l][n2];
            }
        }
        final float[] array = this.vertices[this.shapeFirst];
        final float[] array2 = this.vertices[this.shapeLast - 1];
        if (this.abs(array[0] - array2[0]) < 1.0E-4f && this.abs(array[1] - array2[1]) < 1.0E-4f && this.abs(array[2] - array2[2]) < 1.0E-4f) {
            --this.shapeLast;
        }
        if (n3 > 0.0f) {
            for (int shapeFirst = this.shapeFirst; shapeFirst < this.shapeLast; ++shapeFirst) {
                this.vertexOrder[shapeFirst - this.shapeFirst] = shapeFirst;
            }
        }
        else {
            for (int shapeFirst2 = this.shapeFirst; shapeFirst2 < this.shapeLast; ++shapeFirst2) {
                final int n6 = shapeFirst2 - this.shapeFirst;
                this.vertexOrder[n6] = this.shapeLast - 1 - n6;
            }
        }
        int n7 = this.shapeLast - this.shapeFirst;
        int n8 = 2 * n7;
        int n9 = 0;
        int n10 = n7 - 1;
        while (n7 > 2) {
            boolean b3 = true;
            if (0 >= n8--) {
                break;
            }
            int n11 = n10;
            if (n7 <= n11) {
                n11 = 0;
            }
            n10 = n11 + 1;
            if (n7 <= n10) {
                n10 = 0;
            }
            int n12 = n10 + 1;
            if (n7 <= n12) {
                n12 = 0;
            }
            final double n13 = -10.0f * this.vertices[this.vertexOrder[n11]][n];
            final double n14 = 10.0f * this.vertices[this.vertexOrder[n11]][n2];
            final double n15 = -10.0f * this.vertices[this.vertexOrder[n10]][n];
            final double n16 = 10.0f * this.vertices[this.vertexOrder[n10]][n2];
            final double n17 = -10.0f * this.vertices[this.vertexOrder[n12]][n];
            final double n18 = 10.0f * this.vertices[this.vertexOrder[n12]][n2];
            if (9.999999747378752E-5 > (n15 - n13) * (n18 - n14) - (n16 - n14) * (n17 - n13)) {
                continue;
            }
            for (int n19 = 0; n19 < n7; ++n19) {
                if (n19 != n11 && n19 != n10) {
                    if (n19 != n12) {
                        final double n20 = -10.0f * this.vertices[this.vertexOrder[n19]][n];
                        final double n21 = 10.0f * this.vertices[this.vertexOrder[n19]][n2];
                        final double n22 = n17 - n15;
                        final double n23 = n18 - n16;
                        final double n24 = n13 - n17;
                        final double n25 = n14 - n18;
                        final double n26 = n15 - n13;
                        final double n27 = n16 - n14;
                        final double n28 = n20 - n13;
                        final double n29 = n21 - n14;
                        final double n30 = n20 - n15;
                        final double n31 = n21 - n16;
                        final double n32 = n20 - n17;
                        final double n33 = n21 - n18;
                        final double n34 = n22 * n31 - n23 * n30;
                        final double n35 = n26 * n29 - n27 * n28;
                        final double n36 = n24 * n33 - n25 * n32;
                        if (n34 >= 0.0 && n36 >= 0.0 && n35 >= 0.0) {
                            b3 = false;
                        }
                    }
                }
            }
            if (!b3) {
                continue;
            }
            this.addTriangle(this.vertexOrder[n11], this.vertexOrder[n10], this.vertexOrder[n12]);
            ++n9;
            int n37 = n10;
            for (int n38 = n10 + 1; n38 < n7; ++n38) {
                this.vertexOrder[n37] = this.vertexOrder[n38];
                ++n37;
            }
            --n7;
            n8 = 2 * n7;
        }
    }
    
    private void toWorldNormal(final float n, final float n2, final float n3, final float[] array) {
        array[0] = this.modelviewInv.m00 * n + this.modelviewInv.m10 * n2 + this.modelviewInv.m20 * n3 + this.modelviewInv.m30;
        array[1] = this.modelviewInv.m01 * n + this.modelviewInv.m11 * n2 + this.modelviewInv.m21 * n3 + this.modelviewInv.m31;
        array[2] = this.modelviewInv.m02 * n + this.modelviewInv.m12 * n2 + this.modelviewInv.m22 * n3 + this.modelviewInv.m32;
        array[3] = this.modelviewInv.m03 * n + this.modelviewInv.m13 * n2 + this.modelviewInv.m23 * n3 + this.modelviewInv.m33;
        if (array[3] != 0.0f && array[3] != 1.0f) {
            final int n4 = 0;
            array[n4] /= array[3];
            final int n5 = 1;
            array[n5] /= array[3];
            final int n6 = 2;
            array[n6] /= array[3];
        }
        array[3] = 1.0f;
        final float mag = this.mag(array[0], array[1], array[2]);
        if (mag != 0.0f && mag != 1.0f) {
            final int n7 = 0;
            array[n7] /= mag;
            final int n8 = 1;
            array[n8] /= mag;
            final int n9 = 2;
            array[n9] /= mag;
        }
    }
    
    private void calcLightingContribution(final int n, final float[] array) {
        this.calcLightingContribution(n, array, false);
    }
    
    private void calcLightingContribution(final int n, final float[] array, final boolean b) {
        final float[] array2 = this.vertices[n];
        final float n2 = array2[28];
        final float n3 = array2[29];
        final float n4 = array2[30];
        float n5 = array2[21];
        float n6 = array2[22];
        float n7 = array2[23];
        final float n8 = array2[31];
        final float n9 = array2[9];
        final float n10 = array2[10];
        final float n11 = array2[11];
        float n12;
        float n13;
        float n14;
        if (!b) {
            this.toWorldNormal(array2[9], array2[10], array2[11], this.worldNormal);
            n12 = this.worldNormal[0];
            n13 = this.worldNormal[1];
            n14 = this.worldNormal[2];
        }
        else {
            n12 = array2[9];
            n13 = array2[10];
            n14 = array2[11];
        }
        if (this.dot(n12, n13, n14, -n5, -n6, -n7) < 0.0f) {
            n12 = -n12;
            n13 = -n13;
            n14 = -n14;
        }
        array[0] = 0.0f;
        array[2] = (array[1] = 0.0f);
        array[4] = (array[3] = 0.0f);
        array[6] = (array[5] = 0.0f);
        array[8] = (array[7] = 0.0f);
        for (int i = 0; i < this.lightCount; ++i) {
            float n15 = this.lightFalloffConstant[i];
            float n16 = 1.0f;
            if (this.lightType[i] == 0) {
                if (this.lightFalloffQuadratic[i] != 0.0f || this.lightFalloffLinear[i] != 0.0f) {
                    final float mag = this.mag(this.lightPosition[i].x - n5, this.lightPosition[i].y - n6, this.lightPosition[i].z - n7);
                    n15 += this.lightFalloffQuadratic[i] * mag + this.lightFalloffLinear[i] * this.sqrt(mag);
                }
                if (n15 == 0.0f) {
                    n15 = 1.0f;
                }
                final int n17 = 0;
                array[n17] += this.lightDiffuse[i][0] / n15;
                final int n18 = 1;
                array[n18] += this.lightDiffuse[i][1] / n15;
                final int n19 = 2;
                array[n19] += this.lightDiffuse[i][2] / n15;
            }
            else {
                float n20;
                float n21;
                float n22;
                float n23;
                if (this.lightType[i] == 1) {
                    n20 = -this.lightNormal[i].x;
                    n21 = -this.lightNormal[i].y;
                    n22 = -this.lightNormal[i].z;
                    n15 = 1.0f;
                    n23 = n12 * n20 + n13 * n21 + n14 * n22;
                    if (n23 <= 0.0f) {
                        continue;
                    }
                }
                else {
                    n20 = this.lightPosition[i].x - n5;
                    n21 = this.lightPosition[i].y - n6;
                    n22 = this.lightPosition[i].z - n7;
                    final float mag2 = this.mag(n20, n21, n22);
                    if (mag2 != 0.0f) {
                        n20 /= mag2;
                        n21 /= mag2;
                        n22 /= mag2;
                    }
                    n23 = n12 * n20 + n13 * n21 + n14 * n22;
                    if (n23 <= 0.0f) {
                        continue;
                    }
                    if (this.lightType[i] == 3) {
                        final float n24 = -(this.lightNormal[i].x * n20 + this.lightNormal[i].y * n21 + this.lightNormal[i].z * n22);
                        if (n24 <= this.lightSpotAngleCos[i]) {
                            continue;
                        }
                        n16 = (float)Math.pow(n24, this.lightSpotConcentration[i]);
                    }
                    if (this.lightFalloffQuadratic[i] != 0.0f || this.lightFalloffLinear[i] != 0.0f) {
                        n15 += this.lightFalloffQuadratic[i] * mag2 + this.lightFalloffLinear[i] * this.sqrt(mag2);
                    }
                }
                if (n15 == 0.0f) {
                    n15 = 1.0f;
                }
                final float n25 = n23 * n16 / n15;
                final int n26 = 3;
                array[n26] += this.lightDiffuse[i][0] * n25;
                final int n27 = 4;
                array[n27] += this.lightDiffuse[i][1] * n25;
                final int n28 = 5;
                array[n28] += this.lightDiffuse[i][2] * n25;
                if ((n2 > 0.0f || n3 > 0.0f || n4 > 0.0f) && (this.lightSpecular[i][0] > 0.0f || this.lightSpecular[i][1] > 0.0f || this.lightSpecular[i][2] > 0.0f)) {
                    final float mag3 = this.mag(n5, n6, n7);
                    if (mag3 != 0.0f) {
                        n5 /= mag3;
                        n6 /= mag3;
                        n7 /= mag3;
                    }
                    float n29 = n20 - n5;
                    float n30 = n21 - n6;
                    float n31 = n22 - n7;
                    final float mag4 = this.mag(n29, n30, n31);
                    if (mag4 != 0.0f) {
                        n29 /= mag4;
                        n30 /= mag4;
                        n31 /= mag4;
                    }
                    final float n32 = n29 * n12 + n30 * n13 + n31 * n14;
                    if (n32 > 0.0f) {
                        final float n33 = (float)Math.pow(n32, n8) * n16 / n15;
                        final int n34 = 6;
                        array[n34] += this.lightSpecular[i][0] * n33;
                        final int n35 = 7;
                        array[n35] += this.lightSpecular[i][1] * n33;
                        final int n36 = 8;
                        array[n36] += this.lightSpecular[i][2] * n33;
                    }
                }
            }
        }
    }
    
    private void applyLightingContribution(final int n, final float[] array) {
        final float[] array2 = this.vertices[n];
        array2[3] = this.clamp(array2[32] + array2[25] * array[0] + array2[3] * array[3]);
        array2[4] = this.clamp(array2[33] + array2[26] * array[1] + array2[4] * array[4]);
        array2[5] = this.clamp(array2[34] + array2[27] * array[2] + array2[5] * array[5]);
        array2[6] = this.clamp(array2[6]);
        array2[28] = this.clamp(array2[28] * array[6]);
        array2[29] = this.clamp(array2[29] * array[7]);
        array2[30] = this.clamp(array2[30] * array[8]);
        array2[35] = 1.0f;
    }
    
    private void lightVertex(final int n, final float[] array) {
        this.calcLightingContribution(n, array);
        this.applyLightingContribution(n, array);
    }
    
    private void lightUnlitVertex(final int n, final float[] array) {
        if (this.vertices[n][35] == 0.0f) {
            this.lightVertex(n, array);
        }
    }
    
    private void copyPrelitVertexColor(final int n, final int n2, final int n3) {
        final float[] array = this.triangleColors[n][n3];
        final float[] array2 = this.vertices[n2];
        array[0] = array2[3];
        array[1] = array2[4];
        array[2] = array2[5];
        array[3] = array2[6];
        array[4] = array2[28];
        array[5] = array2[29];
        array[6] = array2[30];
    }
    
    private void copyVertexColor(final int n, final int n2, final int n3, final float[] array) {
        final float[] array2 = this.triangleColors[n][n3];
        final float[] array3 = this.vertices[n2];
        array2[0] = this.clamp(array3[32] + array3[25] * array[0] + array3[3] * array[3]);
        array2[1] = this.clamp(array3[33] + array3[26] * array[1] + array3[4] * array[4]);
        array2[2] = this.clamp(array3[34] + array3[27] * array[2] + array3[5] * array[5]);
        array2[3] = this.clamp(array3[6]);
        array2[4] = this.clamp(array3[28] * array[6]);
        array2[5] = this.clamp(array3[29] * array[7]);
        array2[6] = this.clamp(array3[30] * array[8]);
    }
    
    private void lightTriangle(final int n, final float[] array) {
        this.copyVertexColor(n, this.triangles[n][0], 0, array);
        this.copyVertexColor(n, this.triangles[n][1], 1, array);
        this.copyVertexColor(n, this.triangles[n][2], 2, array);
    }
    
    private void lightTriangle(final int n) {
        if (this.normalMode == 2) {
            final int n2 = this.triangles[n][0];
            this.lightUnlitVertex(n2, this.tempLightingContribution);
            this.copyPrelitVertexColor(n, n2, 0);
            final int n3 = this.triangles[n][1];
            this.lightUnlitVertex(n3, this.tempLightingContribution);
            this.copyPrelitVertexColor(n, n3, 1);
            final int n4 = this.triangles[n][2];
            this.lightUnlitVertex(n4, this.tempLightingContribution);
            this.copyPrelitVertexColor(n, n4, 2);
        }
        else if (!this.lightingDependsOnVertexPosition) {
            final int n5 = this.triangles[n][0];
            final int n6 = this.triangles[n][1];
            final int n7 = this.triangles[n][2];
            this.cross(this.vertices[n6][21] - this.vertices[n5][21], this.vertices[n6][22] - this.vertices[n5][22], this.vertices[n6][23] - this.vertices[n5][23], this.vertices[n7][21] - this.vertices[n5][21], this.vertices[n7][22] - this.vertices[n5][22], this.vertices[n7][23] - this.vertices[n5][23], this.lightTriangleNorm);
            this.lightTriangleNorm.normalize();
            this.vertices[n5][9] = this.lightTriangleNorm.x;
            this.vertices[n5][10] = this.lightTriangleNorm.y;
            this.vertices[n5][11] = this.lightTriangleNorm.z;
            this.calcLightingContribution(n5, this.tempLightingContribution, true);
            this.copyVertexColor(n, n5, 0, this.tempLightingContribution);
            this.copyVertexColor(n, n6, 1, this.tempLightingContribution);
            this.copyVertexColor(n, n7, 2, this.tempLightingContribution);
        }
        else if (this.normalMode == 1) {
            final int n8 = this.triangles[n][0];
            this.vertices[n8][9] = this.vertices[this.shapeFirst][9];
            this.vertices[n8][10] = this.vertices[this.shapeFirst][10];
            this.vertices[n8][11] = this.vertices[this.shapeFirst][11];
            this.calcLightingContribution(n8, this.tempLightingContribution);
            this.copyVertexColor(n, n8, 0, this.tempLightingContribution);
            final int n9 = this.triangles[n][1];
            this.vertices[n9][9] = this.vertices[this.shapeFirst][9];
            this.vertices[n9][10] = this.vertices[this.shapeFirst][10];
            this.vertices[n9][11] = this.vertices[this.shapeFirst][11];
            this.calcLightingContribution(n9, this.tempLightingContribution);
            this.copyVertexColor(n, n9, 1, this.tempLightingContribution);
            final int n10 = this.triangles[n][2];
            this.vertices[n10][9] = this.vertices[this.shapeFirst][9];
            this.vertices[n10][10] = this.vertices[this.shapeFirst][10];
            this.vertices[n10][11] = this.vertices[this.shapeFirst][11];
            this.calcLightingContribution(n10, this.tempLightingContribution);
            this.copyVertexColor(n, n10, 2, this.tempLightingContribution);
        }
        else {
            final int n11 = this.triangles[n][0];
            final int n12 = this.triangles[n][1];
            final int n13 = this.triangles[n][2];
            this.cross(this.vertices[n12][21] - this.vertices[n11][21], this.vertices[n12][22] - this.vertices[n11][22], this.vertices[n12][23] - this.vertices[n11][23], this.vertices[n13][21] - this.vertices[n11][21], this.vertices[n13][22] - this.vertices[n11][22], this.vertices[n13][23] - this.vertices[n11][23], this.lightTriangleNorm);
            this.lightTriangleNorm.normalize();
            this.vertices[n11][9] = this.lightTriangleNorm.x;
            this.vertices[n11][10] = this.lightTriangleNorm.y;
            this.vertices[n11][11] = this.lightTriangleNorm.z;
            this.calcLightingContribution(n11, this.tempLightingContribution, true);
            this.copyVertexColor(n, n11, 0, this.tempLightingContribution);
            this.vertices[n12][9] = this.lightTriangleNorm.x;
            this.vertices[n12][10] = this.lightTriangleNorm.y;
            this.vertices[n12][11] = this.lightTriangleNorm.z;
            this.calcLightingContribution(n12, this.tempLightingContribution, true);
            this.copyVertexColor(n, n12, 1, this.tempLightingContribution);
            this.vertices[n13][9] = this.lightTriangleNorm.x;
            this.vertices[n13][10] = this.lightTriangleNorm.y;
            this.vertices[n13][11] = this.lightTriangleNorm.z;
            this.calcLightingContribution(n13, this.tempLightingContribution, true);
            this.copyVertexColor(n, n13, 2, this.tempLightingContribution);
        }
    }
    
    protected void renderTriangles(final int n, final int n2) {
        for (int i = n; i < n2; ++i) {
            final float[] array = this.vertices[this.triangles[i][0]];
            final float[] array2 = this.vertices[this.triangles[i][1]];
            final float[] array3 = this.vertices[this.triangles[i][2]];
            final int n3 = this.triangles[i][3];
            this.triangle.reset();
            final float clamp = this.clamp(this.triangleColors[i][0][0] + this.triangleColors[i][0][4]);
            final float clamp2 = this.clamp(this.triangleColors[i][0][1] + this.triangleColors[i][0][5]);
            final float clamp3 = this.clamp(this.triangleColors[i][0][2] + this.triangleColors[i][0][6]);
            final float clamp4 = this.clamp(this.triangleColors[i][1][0] + this.triangleColors[i][1][4]);
            final float clamp5 = this.clamp(this.triangleColors[i][1][1] + this.triangleColors[i][1][5]);
            final float clamp6 = this.clamp(this.triangleColors[i][1][2] + this.triangleColors[i][1][6]);
            final float clamp7 = this.clamp(this.triangleColors[i][2][0] + this.triangleColors[i][2][4]);
            final float clamp8 = this.clamp(this.triangleColors[i][2][1] + this.triangleColors[i][2][5]);
            final float clamp9 = this.clamp(this.triangleColors[i][2][2] + this.triangleColors[i][2][6]);
            boolean b = false;
            if (PGraphics3D.s_enableAccurateTextures && this.frustumMode) {
                boolean b2 = true;
                this.smoothTriangle.reset(3);
                this.smoothTriangle.smooth = true;
                this.smoothTriangle.interpARGB = true;
                this.smoothTriangle.setIntensities(clamp, clamp2, clamp3, array[6], clamp4, clamp5, clamp6, array2[6], clamp7, clamp8, clamp9, array3[6]);
                if (n3 > -1 && this.textures[n3] != null) {
                    this.smoothTriangle.setCamVertices(array[21], array[22], array[23], array2[21], array2[22], array2[23], array3[21], array3[22], array3[23]);
                    this.smoothTriangle.interpUV = true;
                    this.smoothTriangle.texture(this.textures[n3]);
                    final float n4 = (float)this.textures[n3].width;
                    final float n5 = (float)this.textures[n3].height;
                    this.smoothTriangle.vertices[0][7] = array[7] * n4;
                    this.smoothTriangle.vertices[0][8] = array[8] * n5;
                    this.smoothTriangle.vertices[1][7] = array2[7] * n4;
                    this.smoothTriangle.vertices[1][8] = array2[8] * n5;
                    this.smoothTriangle.vertices[2][7] = array3[7] * n4;
                    this.smoothTriangle.vertices[2][8] = array3[8] * n5;
                }
                else {
                    this.smoothTriangle.interpUV = false;
                    b2 = false;
                }
                this.smoothTriangle.setVertices(array[18], array[19], array[20], array2[18], array2[19], array2[20], array3[18], array3[19], array3[20]);
                if (!b2 || this.smoothTriangle.precomputeAccurateTexturing()) {
                    this.smoothTriangle.render();
                }
                else {
                    b = true;
                }
            }
            if (!PGraphics3D.s_enableAccurateTextures || b || !this.frustumMode) {
                if (n3 > -1 && this.textures[n3] != null) {
                    this.triangle.setTexture(this.textures[n3]);
                    this.triangle.setUV(array[7], array[8], array2[7], array2[8], array3[7], array3[8]);
                }
                this.triangle.setIntensities(clamp, clamp2, clamp3, array[6], clamp4, clamp5, clamp6, array2[6], clamp7, clamp8, clamp9, array3[6]);
                this.triangle.setVertices(array[18], array[19], array[20], array2[18], array2[19], array2[20], array3[18], array3[19], array3[20]);
                this.triangle.render();
            }
        }
    }
    
    protected void rawTriangles(final int n, final int n2) {
        this.raw.colorMode(1, 1.0f);
        this.raw.noStroke();
        this.raw.beginShape(9);
        for (int i = n; i < n2; ++i) {
            final float[] array = this.vertices[this.triangles[i][0]];
            final float[] array2 = this.vertices[this.triangles[i][1]];
            final float[] array3 = this.vertices[this.triangles[i][2]];
            final float clamp = this.clamp(this.triangleColors[i][0][0] + this.triangleColors[i][0][4]);
            final float clamp2 = this.clamp(this.triangleColors[i][0][1] + this.triangleColors[i][0][5]);
            final float clamp3 = this.clamp(this.triangleColors[i][0][2] + this.triangleColors[i][0][6]);
            final float clamp4 = this.clamp(this.triangleColors[i][1][0] + this.triangleColors[i][1][4]);
            final float clamp5 = this.clamp(this.triangleColors[i][1][1] + this.triangleColors[i][1][5]);
            final float clamp6 = this.clamp(this.triangleColors[i][1][2] + this.triangleColors[i][1][6]);
            final float clamp7 = this.clamp(this.triangleColors[i][2][0] + this.triangleColors[i][2][4]);
            final float clamp8 = this.clamp(this.triangleColors[i][2][1] + this.triangleColors[i][2][5]);
            final float clamp9 = this.clamp(this.triangleColors[i][2][2] + this.triangleColors[i][2][6]);
            final int n3 = this.triangles[i][3];
            if (((n3 > -1) ? this.textures[n3] : null) != null) {
                if (this.raw.is3D()) {
                    if (array[24] != 0.0f && array2[24] != 0.0f && array3[24] != 0.0f) {
                        this.raw.fill(clamp, clamp2, clamp3, array[6]);
                        this.raw.vertex(array[21] / array[24], array[22] / array[24], array[23] / array[24], array[7], array[8]);
                        this.raw.fill(clamp4, clamp5, clamp6, array2[6]);
                        this.raw.vertex(array2[21] / array2[24], array2[22] / array2[24], array2[23] / array2[24], array2[7], array2[8]);
                        this.raw.fill(clamp7, clamp8, clamp9, array3[6]);
                        this.raw.vertex(array3[21] / array3[24], array3[22] / array3[24], array3[23] / array3[24], array3[7], array3[8]);
                    }
                }
                else if (this.raw.is2D()) {
                    this.raw.fill(clamp, clamp2, clamp3, array[6]);
                    this.raw.vertex(array[18], array[19], array[7], array[8]);
                    this.raw.fill(clamp4, clamp5, clamp6, array2[6]);
                    this.raw.vertex(array2[18], array2[19], array2[7], array2[8]);
                    this.raw.fill(clamp7, clamp8, clamp9, array3[6]);
                    this.raw.vertex(array3[18], array3[19], array3[7], array3[8]);
                }
            }
            else if (this.raw.is3D()) {
                if (array[24] != 0.0f && array2[24] != 0.0f && array3[24] != 0.0f) {
                    this.raw.fill(clamp, clamp2, clamp3, array[6]);
                    this.raw.vertex(array[21] / array[24], array[22] / array[24], array[23] / array[24]);
                    this.raw.fill(clamp4, clamp5, clamp6, array2[6]);
                    this.raw.vertex(array2[21] / array2[24], array2[22] / array2[24], array2[23] / array2[24]);
                    this.raw.fill(clamp7, clamp8, clamp9, array3[6]);
                    this.raw.vertex(array3[21] / array3[24], array3[22] / array3[24], array3[23] / array3[24]);
                }
            }
            else if (this.raw.is2D()) {
                this.raw.fill(clamp, clamp2, clamp3, array[6]);
                this.raw.vertex(array[18], array[19]);
                this.raw.fill(clamp4, clamp5, clamp6, array2[6]);
                this.raw.vertex(array2[18], array2[19]);
                this.raw.fill(clamp7, clamp8, clamp9, array3[6]);
                this.raw.vertex(array3[18], array3[19]);
            }
        }
        this.raw.endShape();
    }
    
    @Override
    public void flush() {
        if (this.hints[5]) {
            this.sort();
        }
        this.render();
    }
    
    protected void render() {
        if (this.pointCount > 0) {
            this.renderPoints(0, this.pointCount);
            if (this.raw != null) {
                this.rawPoints(0, this.pointCount);
            }
            this.pointCount = 0;
        }
        if (this.lineCount > 0) {
            this.renderLines(0, this.lineCount);
            if (this.raw != null) {
                this.rawLines(0, this.lineCount);
            }
            this.lineCount = 0;
            this.pathCount = 0;
        }
        if (this.triangleCount > 0) {
            this.renderTriangles(0, this.triangleCount);
            if (this.raw != null) {
                this.rawTriangles(0, this.triangleCount);
            }
            this.triangleCount = 0;
        }
    }
    
    protected void sort() {
        if (this.triangleCount > 0) {
            this.sortTrianglesInternal(0, this.triangleCount - 1);
        }
    }
    
    private void sortTrianglesInternal(final int n, final int n2) {
        this.sortTrianglesSwap((n + n2) / 2, n2);
        final int sortTrianglesPartition = this.sortTrianglesPartition(n - 1, n2);
        this.sortTrianglesSwap(sortTrianglesPartition, n2);
        if (sortTrianglesPartition - n > 1) {
            this.sortTrianglesInternal(n, sortTrianglesPartition - 1);
        }
        if (n2 - sortTrianglesPartition > 1) {
            this.sortTrianglesInternal(sortTrianglesPartition + 1, n2);
        }
    }
    
    private int sortTrianglesPartition(int n, int n2) {
        final int n3 = n2;
        while (true) {
            if (this.sortTrianglesCompare(++n, n3) < 0.0f) {
                continue;
            }
            while (n2 != 0 && this.sortTrianglesCompare(--n2, n3) > 0.0f) {}
            this.sortTrianglesSwap(n, n2);
            if (n >= n2) {
                break;
            }
        }
        this.sortTrianglesSwap(n, n2);
        return n;
    }
    
    private void sortTrianglesSwap(final int n, final int n2) {
        final int[] array = this.triangles[n];
        this.triangles[n] = this.triangles[n2];
        this.triangles[n2] = array;
        final float[][] array2 = this.triangleColors[n];
        this.triangleColors[n] = this.triangleColors[n2];
        this.triangleColors[n2] = array2;
    }
    
    private float sortTrianglesCompare(final int n, final int n2) {
        return this.vertices[this.triangles[n2][0]][20] + this.vertices[this.triangles[n2][1]][20] + this.vertices[this.triangles[n2][2]][20] - (this.vertices[this.triangles[n][0]][20] + this.vertices[this.triangles[n][1]][20] + this.vertices[this.triangles[n][2]][20]);
    }
    
    @Override
    protected void ellipseImpl(final float n, final float n2, final float n3, final float n4) {
        final float n5 = n3 / 2.0f;
        final float n6 = n4 / 2.0f;
        final float n7 = n + n5;
        final float n8 = n2 + n6;
        final int constrain = PApplet.constrain((int)(4.0 + Math.sqrt(n3 + n4) * 3.0), 6, 100);
        if (this.fill) {
            final float n9 = 720.0f / constrain;
            float n10 = 0.0f;
            final boolean stroke = this.stroke;
            this.stroke = false;
            final boolean smooth = this.smooth;
            if (this.smooth && this.stroke) {
                this.smooth = false;
            }
            this.beginShape(11);
            this.normal(0.0f, 0.0f, 1.0f);
            this.vertex(n7, n8);
            for (int i = 0; i < constrain; ++i) {
                this.vertex(n7 + PGraphics3D.cosLUT[(int)n10] * n5, n8 + PGraphics3D.sinLUT[(int)n10] * n6);
                n10 = (n10 + n9) % 720.0f;
            }
            this.vertex(n7 + PGraphics3D.cosLUT[0] * n5, n8 + PGraphics3D.sinLUT[0] * n6);
            this.endShape();
            this.stroke = stroke;
            this.smooth = smooth;
        }
        if (this.stroke) {
            final float n11 = 720.0f / constrain;
            final boolean fill = this.fill;
            this.fill = false;
            float n12 = 0.0f;
            this.beginShape();
            for (int j = 0; j < constrain; ++j) {
                this.vertex(n7 + PGraphics3D.cosLUT[(int)n12] * n5, n8 + PGraphics3D.sinLUT[(int)n12] * n6);
                n12 = (n12 + n11) % 720.0f;
            }
            this.endShape(2);
            this.fill = fill;
        }
    }
    
    @Override
    protected void arcImpl(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        final float n7 = n3 / 2.0f;
        final float n8 = n4 / 2.0f;
        final float n9 = n + n7;
        final float n10 = n2 + n8;
        if (this.fill) {
            final boolean stroke = this.stroke;
            this.stroke = false;
            final int n11 = (int)(0.5f + n5 / 6.2831855f * 720.0f);
            final int n12 = (int)(0.5f + n6 / 6.2831855f * 720.0f);
            this.beginShape(11);
            this.vertex(n9, n10);
            for (int n13 = 1, i = n11; i < n12; i += n13) {
                int n14 = i % 720;
                if (n14 < 0) {
                    n14 += 720;
                }
                this.vertex(n9 + PGraphics3D.cosLUT[n14] * n7, n10 + PGraphics3D.sinLUT[n14] * n8);
            }
            this.vertex(n9 + PGraphics3D.cosLUT[n12 % 720] * n7, n10 + PGraphics3D.sinLUT[n12 % 720] * n8);
            this.endShape();
            this.stroke = stroke;
        }
        if (this.stroke) {
            final boolean fill = this.fill;
            this.fill = false;
            final int n15 = (int)(0.5f + n5 / 6.2831855f * 720.0f);
            final int n16 = (int)(0.5f + n6 / 6.2831855f * 720.0f);
            this.beginShape();
            for (int n17 = 1, j = n15; j < n16; j += n17) {
                int n18 = j % 720;
                if (n18 < 0) {
                    n18 += 720;
                }
                this.vertex(n9 + PGraphics3D.cosLUT[n18] * n7, n10 + PGraphics3D.sinLUT[n18] * n8);
            }
            this.vertex(n9 + PGraphics3D.cosLUT[n16 % 720] * n7, n10 + PGraphics3D.sinLUT[n16 % 720] * n8);
            this.endShape();
            this.fill = fill;
        }
    }
    
    @Override
    public void box(final float n, final float n2, final float n3) {
        if (this.triangle != null) {
            this.triangle.setCulling(true);
        }
        super.box(n, n2, n3);
        if (this.triangle != null) {
            this.triangle.setCulling(false);
        }
    }
    
    @Override
    public void sphere(final float n) {
        if (this.triangle != null) {
            this.triangle.setCulling(true);
        }
        super.sphere(n);
        if (this.triangle != null) {
            this.triangle.setCulling(false);
        }
    }
    
    @Override
    public void smooth() {
        PGraphics3D.s_enableAccurateTextures = true;
        this.smooth = true;
    }
    
    @Override
    public void noSmooth() {
        PGraphics3D.s_enableAccurateTextures = false;
        this.smooth = false;
    }
    
    @Override
    protected boolean textModeCheck(final int n) {
        return this.textMode == 4 || this.textMode == 256;
    }
    
    @Override
    public void pushMatrix() {
        if (this.matrixMode == 0) {
            if (this.pmatrixStackDepth == 32) {
                throw new RuntimeException("Too many calls to pushMatrix().");
            }
            this.projection.get(this.pmatrixStack[this.pmatrixStackDepth]);
            ++this.pmatrixStackDepth;
        }
        else {
            if (this.matrixStackDepth == 32) {
                throw new RuntimeException("Too many calls to pushMatrix().");
            }
            this.modelview.get(this.matrixStack[this.matrixStackDepth]);
            this.modelviewInv.get(this.matrixInvStack[this.matrixStackDepth]);
            ++this.matrixStackDepth;
        }
    }
    
    @Override
    public void popMatrix() {
        if (this.matrixMode == 0) {
            if (this.pmatrixStackDepth == 0) {
                throw new RuntimeException("Too many calls to popMatrix(), and not enough to pushMatrix().");
            }
            --this.pmatrixStackDepth;
            this.projection.set(this.pmatrixStack[this.pmatrixStackDepth]);
        }
        else {
            if (this.matrixStackDepth == 0) {
                throw new RuntimeException("Too many calls to popMatrix(), and not enough to pushMatrix().");
            }
            --this.matrixStackDepth;
            this.modelview.set(this.matrixStack[this.matrixStackDepth]);
            this.modelviewInv.set(this.matrixInvStack[this.matrixStackDepth]);
        }
    }
    
    @Override
    public void translate(final float n, final float n2) {
        this.translate(n, n2, 0.0f);
    }
    
    @Override
    public void translate(final float n, final float n2, final float n3) {
        if (this.matrixMode == 0) {
            this.projection.translate(n, n2, n3);
        }
        else {
            this.forwardTransform.translate(n, n2, n3);
            this.reverseTransform.invTranslate(n, n2, n3);
        }
    }
    
    @Override
    public void rotate(final float n) {
        this.rotateZ(n);
    }
    
    @Override
    public void rotateX(final float n) {
        if (this.matrixMode == 0) {
            this.projection.rotateX(n);
        }
        else {
            this.forwardTransform.rotateX(n);
            this.reverseTransform.invRotateX(n);
        }
    }
    
    @Override
    public void rotateY(final float n) {
        if (this.matrixMode == 0) {
            this.projection.rotateY(n);
        }
        else {
            this.forwardTransform.rotateY(n);
            this.reverseTransform.invRotateY(n);
        }
    }
    
    @Override
    public void rotateZ(final float n) {
        if (this.matrixMode == 0) {
            this.projection.rotateZ(n);
        }
        else {
            this.forwardTransform.rotateZ(n);
            this.reverseTransform.invRotateZ(n);
        }
    }
    
    @Override
    public void rotate(final float n, final float n2, final float n3, final float n4) {
        if (this.matrixMode == 0) {
            this.projection.rotate(n, n2, n3, n4);
        }
        else {
            this.forwardTransform.rotate(n, n2, n3, n4);
            this.reverseTransform.invRotate(n, n2, n3, n4);
        }
    }
    
    @Override
    public void scale(final float n) {
        this.scale(n, n, n);
    }
    
    @Override
    public void scale(final float n, final float n2) {
        this.scale(n, n2, 1.0f);
    }
    
    @Override
    public void scale(final float n, final float n2, final float n3) {
        if (this.matrixMode == 0) {
            this.projection.scale(n, n2, n3);
        }
        else {
            this.forwardTransform.scale(n, n2, n3);
            this.reverseTransform.invScale(n, n2, n3);
        }
    }
    
    @Override
    public void shearX(final float n) {
        this.applyMatrix(1.0f, (float)Math.tan(n), 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    @Override
    public void shearY(final float n) {
        this.applyMatrix(1.0f, 0.0f, 0.0f, 0.0f, (float)Math.tan(n), 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    @Override
    public void resetMatrix() {
        if (this.matrixMode == 0) {
            this.projection.reset();
        }
        else {
            this.forwardTransform.reset();
            this.reverseTransform.reset();
        }
    }
    
    @Override
    public void applyMatrix(final PMatrix2D pMatrix2D) {
        this.applyMatrix(pMatrix2D.m00, pMatrix2D.m01, pMatrix2D.m02, pMatrix2D.m10, pMatrix2D.m11, pMatrix2D.m12);
    }
    
    @Override
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.applyMatrix(n, n2, n3, 0.0f, n4, n5, n6, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
    }
    
    @Override
    public void applyMatrix(final PMatrix3D pMatrix3D) {
        this.applyMatrix(pMatrix3D.m00, pMatrix3D.m01, pMatrix3D.m02, pMatrix3D.m03, pMatrix3D.m10, pMatrix3D.m11, pMatrix3D.m12, pMatrix3D.m13, pMatrix3D.m20, pMatrix3D.m21, pMatrix3D.m22, pMatrix3D.m23, pMatrix3D.m30, pMatrix3D.m31, pMatrix3D.m32, pMatrix3D.m33);
    }
    
    @Override
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        if (this.matrixMode == 0) {
            this.projection.apply(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
        }
        else {
            this.forwardTransform.apply(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
            this.reverseTransform.invApply(n, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11, n12, n13, n14, n15, n16);
        }
    }
    
    @Override
    public PMatrix getMatrix() {
        if (this.matrixMode == 0) {
            return (PMatrix)this.projection.get();
        }
        return (PMatrix)this.modelview.get();
    }
    
    @Override
    public PMatrix3D getMatrix(PMatrix3D pMatrix3D) {
        if (pMatrix3D == null) {
            pMatrix3D = new PMatrix3D();
        }
        if (this.matrixMode == 0) {
            pMatrix3D.set((PMatrix)this.projection);
        }
        else {
            pMatrix3D.set((PMatrix)this.modelview);
        }
        return pMatrix3D;
    }
    
    @Override
    public void setMatrix(final PMatrix2D pMatrix2D) {
        this.resetMatrix();
        this.applyMatrix(pMatrix2D);
    }
    
    @Override
    public void setMatrix(final PMatrix3D pMatrix3D) {
        this.resetMatrix();
        this.applyMatrix(pMatrix3D);
    }
    
    @Override
    public void printMatrix() {
        if (this.matrixMode == 0) {
            this.projection.print();
        }
        else {
            this.modelview.print();
        }
    }
    
    @Override
    public void beginCamera() {
        if (this.manipulatingCamera) {
            throw new RuntimeException("beginCamera() cannot be called again before endCamera()");
        }
        this.manipulatingCamera = true;
        this.forwardTransform = this.cameraInv;
        this.reverseTransform = this.camera;
    }
    
    @Override
    public void endCamera() {
        if (!this.manipulatingCamera) {
            throw new RuntimeException("Cannot call endCamera() without first calling beginCamera()");
        }
        this.modelview.set((PMatrix)this.camera);
        this.modelviewInv.set((PMatrix)this.cameraInv);
        this.forwardTransform = this.modelview;
        this.reverseTransform = this.modelviewInv;
        this.manipulatingCamera = false;
    }
    
    @Override
    public void camera() {
        this.camera(this.cameraX, this.cameraY, this.cameraZ, this.cameraX, this.cameraY, 0.0f, 0.0f, 1.0f, 0.0f);
    }
    
    @Override
    public void camera(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        float n10 = n - n4;
        float n11 = n2 - n5;
        float n12 = n3 - n6;
        final float sqrt = this.sqrt(n10 * n10 + n11 * n11 + n12 * n12);
        if (sqrt != 0.0f) {
            n10 /= sqrt;
            n11 /= sqrt;
            n12 /= sqrt;
        }
        float n13 = n8 * n12 - n9 * n11;
        float n14 = -n7 * n12 + n9 * n10;
        float n15 = n7 * n11 - n8 * n10;
        float n16 = n11 * n15 - n12 * n14;
        float n17 = -n10 * n15 + n12 * n13;
        float n18 = n10 * n14 - n11 * n13;
        final float sqrt2 = this.sqrt(n13 * n13 + n14 * n14 + n15 * n15);
        if (sqrt2 != 0.0f) {
            n13 /= sqrt2;
            n14 /= sqrt2;
            n15 /= sqrt2;
        }
        final float sqrt3 = this.sqrt(n16 * n16 + n17 * n17 + n18 * n18);
        if (sqrt3 != 0.0f) {
            n16 /= sqrt3;
            n17 /= sqrt3;
            n18 /= sqrt3;
        }
        this.camera.set(n13, n14, n15, 0.0f, n16, n17, n18, 0.0f, n10, n11, n12, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        this.camera.translate(-n, -n2, -n3);
        this.cameraInv.reset();
        this.cameraInv.invApply(n13, n14, n15, 0.0f, n16, n17, n18, 0.0f, n10, n11, n12, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        this.cameraInv.translate(n, n2, n3);
        this.modelview.set((PMatrix)this.camera);
        this.modelviewInv.set((PMatrix)this.cameraInv);
    }
    
    @Override
    public void printCamera() {
        this.camera.print();
    }
    
    @Override
    public void ortho() {
        this.ortho(0.0f, (float)this.width, 0.0f, (float)this.height, -10.0f, 10.0f);
    }
    
    @Override
    public void ortho(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.projection.set(2.0f / (n2 - n), 0.0f, 0.0f, -(n2 + n) / (n2 - n), 0.0f, 2.0f / (n4 - n3), 0.0f, -(n4 + n3) / (n4 - n3), 0.0f, 0.0f, -2.0f / (n6 - n5), -(n6 + n5) / (n6 - n5), 0.0f, 0.0f, 0.0f, 1.0f);
        this.updateProjection();
        this.frustumMode = false;
    }
    
    @Override
    public void perspective() {
        this.perspective(this.cameraFOV, this.cameraAspect, this.cameraNear, this.cameraFar);
    }
    
    @Override
    public void perspective(final float n, final float n2, final float n3, final float n4) {
        final float n5 = n3 * (float)Math.tan(n / 2.0f);
        final float n6 = -n5;
        this.frustum(n6 * n2, n5 * n2, n6, n5, n3, n4);
    }
    
    @Override
    public void frustum(final float leftScreen, final float rightScreen, final float bottomScreen, final float topScreen, final float nearPlane, final float n) {
        this.leftScreen = leftScreen;
        this.rightScreen = rightScreen;
        this.bottomScreen = bottomScreen;
        this.topScreen = topScreen;
        this.nearPlane = nearPlane;
        this.frustumMode = true;
        this.projection.set(2.0f * nearPlane / (rightScreen - leftScreen), 0.0f, (rightScreen + leftScreen) / (rightScreen - leftScreen), 0.0f, 0.0f, 2.0f * nearPlane / (topScreen - bottomScreen), (topScreen + bottomScreen) / (topScreen - bottomScreen), 0.0f, 0.0f, 0.0f, -(n + nearPlane) / (n - nearPlane), -(2.0f * n * nearPlane) / (n - nearPlane), 0.0f, 0.0f, -1.0f, 0.0f);
        this.updateProjection();
    }
    
    protected void updateProjection() {
    }
    
    @Override
    public void printProjection() {
        this.projection.print();
    }
    
    public PMatrix getProjection() {
        return (PMatrix)this.projection.get();
    }
    
    @Override
    public void matrixMode(final int n) {
        if (n == 0) {
            this.matrixMode = 0;
        }
        else if (n == 1) {
            this.matrixMode = 1;
        }
        else {
            PGraphics.showWarning("Invalid matrix mode. Use PROJECTION or MODELVIEW");
        }
    }
    
    @Override
    public float screenX(final float n, final float n2) {
        return this.screenX(n, n2, 0.0f);
    }
    
    @Override
    public float screenY(final float n, final float n2) {
        return this.screenY(n, n2, 0.0f);
    }
    
    @Override
    public float screenX(final float n, final float n2, final float n3) {
        final float n4 = this.modelview.m00 * n + this.modelview.m01 * n2 + this.modelview.m02 * n3 + this.modelview.m03;
        final float n5 = this.modelview.m10 * n + this.modelview.m11 * n2 + this.modelview.m12 * n3 + this.modelview.m13;
        final float n6 = this.modelview.m20 * n + this.modelview.m21 * n2 + this.modelview.m22 * n3 + this.modelview.m23;
        final float n7 = this.modelview.m30 * n + this.modelview.m31 * n2 + this.modelview.m32 * n3 + this.modelview.m33;
        float n8 = this.projection.m00 * n4 + this.projection.m01 * n5 + this.projection.m02 * n6 + this.projection.m03 * n7;
        final float n9 = this.projection.m30 * n4 + this.projection.m31 * n5 + this.projection.m32 * n6 + this.projection.m33 * n7;
        if (n9 != 0.0f) {
            n8 /= n9;
        }
        return this.width * (1.0f + n8) / 2.0f;
    }
    
    @Override
    public float screenY(final float n, final float n2, final float n3) {
        final float n4 = this.modelview.m00 * n + this.modelview.m01 * n2 + this.modelview.m02 * n3 + this.modelview.m03;
        final float n5 = this.modelview.m10 * n + this.modelview.m11 * n2 + this.modelview.m12 * n3 + this.modelview.m13;
        final float n6 = this.modelview.m20 * n + this.modelview.m21 * n2 + this.modelview.m22 * n3 + this.modelview.m23;
        final float n7 = this.modelview.m30 * n + this.modelview.m31 * n2 + this.modelview.m32 * n3 + this.modelview.m33;
        float n8 = this.projection.m10 * n4 + this.projection.m11 * n5 + this.projection.m12 * n6 + this.projection.m13 * n7;
        final float n9 = this.projection.m30 * n4 + this.projection.m31 * n5 + this.projection.m32 * n6 + this.projection.m33 * n7;
        if (n9 != 0.0f) {
            n8 /= n9;
        }
        return this.height * (1.0f + n8) / 2.0f;
    }
    
    @Override
    public float screenZ(final float n, final float n2, final float n3) {
        final float n4 = this.modelview.m00 * n + this.modelview.m01 * n2 + this.modelview.m02 * n3 + this.modelview.m03;
        final float n5 = this.modelview.m10 * n + this.modelview.m11 * n2 + this.modelview.m12 * n3 + this.modelview.m13;
        final float n6 = this.modelview.m20 * n + this.modelview.m21 * n2 + this.modelview.m22 * n3 + this.modelview.m23;
        final float n7 = this.modelview.m30 * n + this.modelview.m31 * n2 + this.modelview.m32 * n3 + this.modelview.m33;
        float n8 = this.projection.m20 * n4 + this.projection.m21 * n5 + this.projection.m22 * n6 + this.projection.m23 * n7;
        final float n9 = this.projection.m30 * n4 + this.projection.m31 * n5 + this.projection.m32 * n6 + this.projection.m33 * n7;
        if (n9 != 0.0f) {
            n8 /= n9;
        }
        return (n8 + 1.0f) / 2.0f;
    }
    
    @Override
    public float modelX(final float n, final float n2, final float n3) {
        final float n4 = this.modelview.m00 * n + this.modelview.m01 * n2 + this.modelview.m02 * n3 + this.modelview.m03;
        final float n5 = this.modelview.m10 * n + this.modelview.m11 * n2 + this.modelview.m12 * n3 + this.modelview.m13;
        final float n6 = this.modelview.m20 * n + this.modelview.m21 * n2 + this.modelview.m22 * n3 + this.modelview.m23;
        final float n7 = this.modelview.m30 * n + this.modelview.m31 * n2 + this.modelview.m32 * n3 + this.modelview.m33;
        final float n8 = this.cameraInv.m00 * n4 + this.cameraInv.m01 * n5 + this.cameraInv.m02 * n6 + this.cameraInv.m03 * n7;
        final float n9 = this.cameraInv.m30 * n4 + this.cameraInv.m31 * n5 + this.cameraInv.m32 * n6 + this.cameraInv.m33 * n7;
        return (n9 != 0.0f) ? (n8 / n9) : n8;
    }
    
    @Override
    public float modelY(final float n, final float n2, final float n3) {
        final float n4 = this.modelview.m00 * n + this.modelview.m01 * n2 + this.modelview.m02 * n3 + this.modelview.m03;
        final float n5 = this.modelview.m10 * n + this.modelview.m11 * n2 + this.modelview.m12 * n3 + this.modelview.m13;
        final float n6 = this.modelview.m20 * n + this.modelview.m21 * n2 + this.modelview.m22 * n3 + this.modelview.m23;
        final float n7 = this.modelview.m30 * n + this.modelview.m31 * n2 + this.modelview.m32 * n3 + this.modelview.m33;
        final float n8 = this.cameraInv.m10 * n4 + this.cameraInv.m11 * n5 + this.cameraInv.m12 * n6 + this.cameraInv.m13 * n7;
        final float n9 = this.cameraInv.m30 * n4 + this.cameraInv.m31 * n5 + this.cameraInv.m32 * n6 + this.cameraInv.m33 * n7;
        return (n9 != 0.0f) ? (n8 / n9) : n8;
    }
    
    @Override
    public float modelZ(final float n, final float n2, final float n3) {
        final float n4 = this.modelview.m00 * n + this.modelview.m01 * n2 + this.modelview.m02 * n3 + this.modelview.m03;
        final float n5 = this.modelview.m10 * n + this.modelview.m11 * n2 + this.modelview.m12 * n3 + this.modelview.m13;
        final float n6 = this.modelview.m20 * n + this.modelview.m21 * n2 + this.modelview.m22 * n3 + this.modelview.m23;
        final float n7 = this.modelview.m30 * n + this.modelview.m31 * n2 + this.modelview.m32 * n3 + this.modelview.m33;
        final float n8 = this.cameraInv.m20 * n4 + this.cameraInv.m21 * n5 + this.cameraInv.m22 * n6 + this.cameraInv.m23 * n7;
        final float n9 = this.cameraInv.m30 * n4 + this.cameraInv.m31 * n5 + this.cameraInv.m32 * n6 + this.cameraInv.m33 * n7;
        return (n9 != 0.0f) ? (n8 / n9) : n8;
    }
    
    @Override
    public void strokeJoin(final int n) {
        if (n != 8) {
            PGraphics.showMethodWarning("strokeJoin");
        }
    }
    
    @Override
    public void strokeCap(final int n) {
        if (n != 2) {
            PGraphics.showMethodWarning("strokeCap");
        }
    }
    
    @Override
    protected void fillFromCalc() {
        super.fillFromCalc();
        this.ambientFromCalc();
    }
    
    @Override
    public void lights() {
        final int colorMode = this.colorMode;
        this.colorMode = 1;
        this.lightFalloff(1.0f, 0.0f, 0.0f);
        this.lightSpecular(0.0f, 0.0f, 0.0f);
        this.ambientLight(this.colorModeX * 0.5f, this.colorModeY * 0.5f, this.colorModeZ * 0.5f);
        this.directionalLight(this.colorModeX * 0.5f, this.colorModeY * 0.5f, this.colorModeZ * 0.5f, 0.0f, 0.0f, -1.0f);
        this.colorMode = colorMode;
        this.lightingDependsOnVertexPosition = false;
    }
    
    @Override
    public void noLights() {
        this.flush();
        this.lightCount = 0;
    }
    
    @Override
    public void ambientLight(final float n, final float n2, final float n3) {
        this.ambientLight(n, n2, n3, 0.0f, 0.0f, 0.0f);
    }
    
    @Override
    public void ambientLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.lightCount == 8) {
            throw new RuntimeException("can only create 8 lights");
        }
        this.colorCalc(n, n2, n3);
        this.lightDiffuse[this.lightCount][0] = this.calcR;
        this.lightDiffuse[this.lightCount][1] = this.calcG;
        this.lightDiffuse[this.lightCount][2] = this.calcB;
        this.lightType[this.lightCount] = 0;
        this.lightFalloffConstant[this.lightCount] = this.currentLightFalloffConstant;
        this.lightFalloffLinear[this.lightCount] = this.currentLightFalloffLinear;
        this.lightFalloffQuadratic[this.lightCount] = this.currentLightFalloffQuadratic;
        this.lightPosition(this.lightCount, n4, n5, n6);
        ++this.lightCount;
    }
    
    @Override
    public void directionalLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.lightCount == 8) {
            throw new RuntimeException("can only create 8 lights");
        }
        this.colorCalc(n, n2, n3);
        this.lightDiffuse[this.lightCount][0] = this.calcR;
        this.lightDiffuse[this.lightCount][1] = this.calcG;
        this.lightDiffuse[this.lightCount][2] = this.calcB;
        this.lightType[this.lightCount] = 1;
        this.lightFalloffConstant[this.lightCount] = this.currentLightFalloffConstant;
        this.lightFalloffLinear[this.lightCount] = this.currentLightFalloffLinear;
        this.lightFalloffQuadratic[this.lightCount] = this.currentLightFalloffQuadratic;
        this.lightSpecular[this.lightCount][0] = this.currentLightSpecular[0];
        this.lightSpecular[this.lightCount][1] = this.currentLightSpecular[1];
        this.lightSpecular[this.lightCount][2] = this.currentLightSpecular[2];
        this.lightDirection(this.lightCount, n4, n5, n6);
        ++this.lightCount;
    }
    
    @Override
    public void pointLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (this.lightCount == 8) {
            throw new RuntimeException("can only create 8 lights");
        }
        this.colorCalc(n, n2, n3);
        this.lightDiffuse[this.lightCount][0] = this.calcR;
        this.lightDiffuse[this.lightCount][1] = this.calcG;
        this.lightDiffuse[this.lightCount][2] = this.calcB;
        this.lightType[this.lightCount] = 2;
        this.lightFalloffConstant[this.lightCount] = this.currentLightFalloffConstant;
        this.lightFalloffLinear[this.lightCount] = this.currentLightFalloffLinear;
        this.lightFalloffQuadratic[this.lightCount] = this.currentLightFalloffQuadratic;
        this.lightSpecular[this.lightCount][0] = this.currentLightSpecular[0];
        this.lightSpecular[this.lightCount][1] = this.currentLightSpecular[1];
        this.lightSpecular[this.lightCount][2] = this.currentLightSpecular[2];
        this.lightPosition(this.lightCount, n4, n5, n6);
        ++this.lightCount;
        this.lightingDependsOnVertexPosition = true;
    }
    
    @Override
    public void spotLight(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11) {
        if (this.lightCount == 8) {
            throw new RuntimeException("can only create 8 lights");
        }
        this.colorCalc(n, n2, n3);
        this.lightDiffuse[this.lightCount][0] = this.calcR;
        this.lightDiffuse[this.lightCount][1] = this.calcG;
        this.lightDiffuse[this.lightCount][2] = this.calcB;
        this.lightType[this.lightCount] = 3;
        this.lightFalloffConstant[this.lightCount] = this.currentLightFalloffConstant;
        this.lightFalloffLinear[this.lightCount] = this.currentLightFalloffLinear;
        this.lightFalloffQuadratic[this.lightCount] = this.currentLightFalloffQuadratic;
        this.lightSpecular[this.lightCount][0] = this.currentLightSpecular[0];
        this.lightSpecular[this.lightCount][1] = this.currentLightSpecular[1];
        this.lightSpecular[this.lightCount][2] = this.currentLightSpecular[2];
        this.lightPosition(this.lightCount, n4, n5, n6);
        this.lightDirection(this.lightCount, n7, n8, n9);
        this.lightSpotAngle[this.lightCount] = n10;
        this.lightSpotAngleCos[this.lightCount] = Math.max(0.0f, (float)Math.cos(n10));
        this.lightSpotConcentration[this.lightCount] = n11;
        ++this.lightCount;
        this.lightingDependsOnVertexPosition = true;
    }
    
    @Override
    public void lightFalloff(final float currentLightFalloffConstant, final float currentLightFalloffLinear, final float currentLightFalloffQuadratic) {
        this.currentLightFalloffConstant = currentLightFalloffConstant;
        this.currentLightFalloffLinear = currentLightFalloffLinear;
        this.currentLightFalloffQuadratic = currentLightFalloffQuadratic;
        this.lightingDependsOnVertexPosition = true;
    }
    
    @Override
    public void lightSpecular(final float n, final float n2, final float n3) {
        this.colorCalc(n, n2, n3);
        this.currentLightSpecular[0] = this.calcR;
        this.currentLightSpecular[1] = this.calcG;
        this.currentLightSpecular[2] = this.calcB;
        this.lightingDependsOnVertexPosition = true;
    }
    
    protected void lightPosition(final int n, final float n2, final float n3, final float n4) {
        this.lightPositionVec.set(n2, n3, n4);
        this.modelview.mult(this.lightPositionVec, this.lightPosition[n]);
    }
    
    protected void lightDirection(final int n, final float n2, final float n3, final float n4) {
        this.lightNormal[n].set(this.modelviewInv.m00 * n2 + this.modelviewInv.m10 * n3 + this.modelviewInv.m20 * n4 + this.modelviewInv.m30, this.modelviewInv.m01 * n2 + this.modelviewInv.m11 * n3 + this.modelviewInv.m21 * n4 + this.modelviewInv.m31, this.modelviewInv.m02 * n2 + this.modelviewInv.m12 * n3 + this.modelviewInv.m22 * n4 + this.modelviewInv.m32);
        this.lightNormal[n].normalize();
    }
    
    @Override
    protected void backgroundImpl(final PImage pImage) {
        System.arraycopy(pImage.pixels, 0, this.pixels, 0, this.pixels.length);
        Arrays.fill(this.zbuffer, Float.MAX_VALUE);
    }
    
    @Override
    protected void backgroundImpl() {
        Arrays.fill(this.pixels, this.backgroundColor);
        Arrays.fill(this.zbuffer, Float.MAX_VALUE);
    }
    
    @Override
    public boolean is2D() {
        return false;
    }
    
    @Override
    public boolean is3D() {
        return true;
    }
    
    private final float sqrt(final float n) {
        return (float)Math.sqrt(n);
    }
    
    private final float mag(final float n, final float n2, final float n3) {
        return (float)Math.sqrt(n * n + n2 * n2 + n3 * n3);
    }
    
    private final float clamp(final float n) {
        return (n < 1.0f) ? n : 1.0f;
    }
    
    private final float abs(final float n) {
        return (n < 0.0f) ? (-n) : n;
    }
    
    private float dot(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        return n * n4 + n2 * n5 + n3 * n6;
    }
    
    private final void cross(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final PVector pVector) {
        pVector.x = n2 * n6 - n3 * n5;
        pVector.y = n3 * n4 - n * n6;
        pVector.z = n * n5 - n2 * n4;
    }
    
    static {
        PGraphics3D.s_enableAccurateTextures = false;
    }
}