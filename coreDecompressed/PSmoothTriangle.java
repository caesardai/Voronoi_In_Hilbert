package processing.core;

public class PSmoothTriangle implements PConstants
{
    private static final boolean EWJORDAN = false;
    private static final boolean FRY = false;
    static final int X = 0;
    static final int Y = 1;
    static final int Z = 2;
    static final int R = 3;
    static final int G = 4;
    static final int B = 5;
    static final int A = 6;
    static final int U = 7;
    static final int V = 8;
    static final int DEFAULT_SIZE = 64;
    float[][] vertices;
    int vertexCount;
    static final int ZBUFFER_MIN_COVERAGE = 204;
    float[] r;
    float[] dr;
    float[] l;
    float[] dl;
    float[] sp;
    float[] sdp;
    boolean interpX;
    boolean interpZ;
    boolean interpUV;
    boolean interpARGB;
    int rgba;
    int r2;
    int g2;
    int b2;
    int a2;
    int a2orig;
    boolean noDepthTest;
    PGraphics3D parent;
    int[] pixels;
    float[] zbuffer;
    int width;
    int height;
    int width1;
    int height1;
    PImage timage;
    int[] tpixels;
    int theight;
    int twidth;
    int theight1;
    int twidth1;
    int tformat;
    boolean texture_smooth;
    static final int SUBXRES = 8;
    static final int SUBXRES1 = 7;
    static final int SUBYRES = 8;
    static final int SUBYRES1 = 7;
    static final int MAX_COVERAGE = 64;
    boolean smooth;
    int firstModY;
    int lastModY;
    int lastY;
    int[] aaleft;
    int[] aaright;
    int aaleftmin;
    int aarightmin;
    int aaleftmax;
    int aarightmax;
    int aaleftfull;
    int aarightfull;
    private float[] camX;
    private float[] camY;
    private float[] camZ;
    private float ax;
    private float ay;
    private float az;
    private float bx;
    private float by;
    private float bz;
    private float cx;
    private float cy;
    private float cz;
    private float nearPlaneWidth;
    private float nearPlaneHeight;
    private float nearPlaneDepth;
    private float xmult;
    private float ymult;
    
    private final int MODYRES(final int n) {
        return n & 0x7;
    }
    
    public PSmoothTriangle(final PGraphics3D parent) {
        this.vertices = new float[64][36];
        this.r = new float[64];
        this.dr = new float[64];
        this.l = new float[64];
        this.dl = new float[64];
        this.sp = new float[64];
        this.sdp = new float[64];
        this.aaleft = new int[8];
        this.aaright = new int[8];
        this.camX = new float[3];
        this.camY = new float[3];
        this.camZ = new float[3];
        this.parent = parent;
        this.reset(0);
    }
    
    public void reset(final int vertexCount) {
        this.vertexCount = vertexCount;
        this.interpX = true;
        this.interpZ = true;
        this.interpUV = false;
        this.interpARGB = true;
        this.timage = null;
    }
    
    public float[] nextVertex() {
        if (this.vertexCount == this.vertices.length) {
            final float[][] vertices = new float[this.vertexCount << 1][36];
            System.arraycopy(this.vertices, 0, vertices, 0, this.vertexCount);
            this.vertices = vertices;
            this.r = new float[this.vertices.length];
            this.dr = new float[this.vertices.length];
            this.l = new float[this.vertices.length];
            this.dl = new float[this.vertices.length];
            this.sp = new float[this.vertices.length];
            this.sdp = new float[this.vertices.length];
        }
        return this.vertices[this.vertexCount++];
    }
    
    public void texture(final PImage timage) {
        this.timage = timage;
        this.tpixels = timage.pixels;
        this.twidth = timage.width;
        this.theight = timage.height;
        this.tformat = timage.format;
        this.twidth1 = this.twidth - 1;
        this.theight1 = this.theight - 1;
        this.interpUV = true;
    }
    
    public void render() {
        if (this.vertexCount < 3) {
            return;
        }
        this.smooth = true;
        this.pixels = this.parent.pixels;
        this.zbuffer = this.parent.zbuffer;
        this.noDepthTest = false;
        this.texture_smooth = true;
        this.width = (this.smooth ? (this.parent.width * 8) : this.parent.width);
        this.height = (this.smooth ? (this.parent.height * 8) : this.parent.height);
        this.width1 = this.width - 1;
        this.height1 = this.height - 1;
        if (!this.interpARGB) {
            this.r2 = (int)(this.vertices[0][3] * 255.0f);
            this.g2 = (int)(this.vertices[0][4] * 255.0f);
            this.b2 = (int)(this.vertices[0][5] * 255.0f);
            this.a2 = (int)(this.vertices[0][6] * 255.0f);
            this.a2orig = this.a2;
            this.rgba = (0xFF000000 | this.r2 << 16 | this.g2 << 8 | this.b2);
        }
        for (int i = 0; i < this.vertexCount; ++i) {
            this.r[i] = 0.0f;
            this.dr[i] = 0.0f;
            this.l[i] = 0.0f;
            this.dl[i] = 0.0f;
        }
        if (this.smooth) {
            for (int j = 0; j < this.vertexCount; ++j) {
                final float[] array = this.vertices[j];
                final int n = 0;
                array[n] *= 8.0f;
                final float[] array2 = this.vertices[j];
                final int n2 = 1;
                array2[n2] *= 8.0f;
            }
            this.firstModY = -1;
        }
        int n3 = 0;
        float n4 = this.vertices[0][1];
        float n5 = this.vertices[0][1];
        for (int k = 1; k < this.vertexCount; ++k) {
            if (this.vertices[k][1] < n4) {
                n4 = this.vertices[k][1];
                n3 = k;
            }
            if (this.vertices[k][1] > n5) {
                n5 = this.vertices[k][1];
            }
        }
        this.lastY = (int)(n5 - 0.5f);
        int n6 = n3;
        int n7 = n3;
        int n8 = (int)(n4 + 0.5f);
        int n9 = n8 - 1;
        int n10 = n8 - 1;
        this.interpX = true;
        int l = this.vertexCount;
        while (l > 0) {
            while (n9 <= n8 && l > 0) {
                --l;
                final int n11 = (n6 != 0) ? (n6 - 1) : (this.vertexCount - 1);
                this.incrementalize_y(this.vertices[n6], this.vertices[n11], this.l, this.dl, n8);
                n9 = (int)(this.vertices[n11][1] + 0.5f);
                n6 = n11;
            }
            while (n10 <= n8 && l > 0) {
                --l;
                final int n12 = (n7 != this.vertexCount - 1) ? (n7 + 1) : 0;
                this.incrementalize_y(this.vertices[n7], this.vertices[n12], this.r, this.dr, n8);
                n10 = (int)(this.vertices[n12][1] + 0.5f);
                n7 = n12;
            }
            while (n8 < n9 && n8 < n10) {
                if (n8 >= 0 && n8 < this.height) {
                    if (this.l[0] <= this.r[0]) {
                        this.scanline(n8, this.l, this.r);
                    }
                    else {
                        this.scanline(n8, this.r, this.l);
                    }
                }
                ++n8;
                this.increment(this.l, this.dl);
                this.increment(this.r, this.dr);
            }
        }
    }
    
    public void unexpand() {
        if (this.smooth) {
            for (int i = 0; i < this.vertexCount; ++i) {
                final float[] array = this.vertices[i];
                final int n = 0;
                array[n] /= 8.0f;
                final float[] array2 = this.vertices[i];
                final int n2 = 1;
                array2[n2] /= 8.0f;
            }
        }
    }
    
    private void scanline(final int n, final float[] array, final float[] array2) {
        for (int i = 0; i < this.vertexCount; ++i) {
            this.sp[i] = 0.0f;
            this.sdp[i] = 0.0f;
        }
        int n2 = (int)(array[0] + 0.49999f);
        if (n2 < 0) {
            n2 = 0;
        }
        int n3 = (int)(array2[0] - 0.5f);
        if (n3 > this.width1) {
            n3 = this.width1;
        }
        if (n2 > n3) {
            return;
        }
        if (this.smooth) {
            final int modyres = this.MODYRES(n);
            this.aaleft[modyres] = n2;
            this.aaright[modyres] = n3;
            if (this.firstModY == -1) {
                this.firstModY = modyres;
                this.aaleftmin = n2;
                this.aaleftmax = n2;
                this.aarightmin = n3;
                this.aarightmax = n3;
            }
            else {
                if (this.aaleftmin > this.aaleft[modyres]) {
                    this.aaleftmin = this.aaleft[modyres];
                }
                if (this.aaleftmax < this.aaleft[modyres]) {
                    this.aaleftmax = this.aaleft[modyres];
                }
                if (this.aarightmin > this.aaright[modyres]) {
                    this.aarightmin = this.aaright[modyres];
                }
                if (this.aarightmax < this.aaright[modyres]) {
                    this.aarightmax = this.aaright[modyres];
                }
            }
            this.lastModY = modyres;
            if (modyres != 7 && n != this.lastY) {
                return;
            }
            this.aaleftfull = this.aaleftmax / 8 + 1;
            this.aarightfull = this.aarightmin / 8 - 1;
        }
        this.incrementalize_x(array, array2, this.sp, this.sdp, n2);
        final int n4 = this.smooth ? (this.parent.width * (n / 8)) : (this.parent.width * n);
        int n5 = 0;
        int n6 = 0;
        if (this.smooth) {
            n5 = n2 / 8;
            n6 = (n3 + 7) / 8;
            n2 = this.aaleftmin / 8;
            n3 = (this.aarightmax + 7) / 8;
            if (n2 < 0) {
                n2 = 0;
            }
            if (n3 > this.parent.width1) {
                n3 = this.parent.width1;
            }
        }
        this.interpX = false;
        for (int j = n2; j <= n3; ++j) {
            if (this.noDepthTest || this.sp[2] <= this.zbuffer[n4 + j]) {
                if (this.interpUV) {
                    int twidth1 = (int)this.sp[7];
                    int theight1 = (int)this.sp[8];
                    if (twidth1 > this.twidth1) {
                        twidth1 = this.twidth1;
                    }
                    if (theight1 > this.theight1) {
                        theight1 = this.theight1;
                    }
                    if (twidth1 < 0) {
                        twidth1 = 0;
                    }
                    if (theight1 < 0) {
                        theight1 = 0;
                    }
                    final int n7 = theight1 * this.twidth + twidth1;
                    final float[] array3 = new float[2];
                    this.getTextureIndex((float)j, n * 1.0f / 8.0f, array3);
                    final int n8 = (int)array3[0];
                    final int n9 = (int)array3[1];
                    final int n10 = this.twidth * n9 + n8;
                    int n19;
                    int n20;
                    int n21;
                    int n22;
                    if (this.smooth || this.texture_smooth) {
                        final int n11 = (int)(255.0f * (array3[0] - n8));
                        final int n12 = (int)(255.0f * (array3[1] - n9));
                        final int n13 = 255 - n11;
                        final int n14 = 255 - n12;
                        final int n15 = this.tpixels[n10];
                        final int n16 = (n9 < this.theight1) ? this.tpixels[n10 + this.twidth] : this.tpixels[n10];
                        final int n17 = (n8 < this.twidth1) ? this.tpixels[n10 + 1] : this.tpixels[n10];
                        final int n18 = (n9 < this.theight1 && n8 < this.twidth1) ? this.tpixels[n10 + this.twidth + 1] : this.tpixels[n10];
                        if (this.tformat == 4) {
                            n19 = ((n15 * n13 + n17 * n11 >> 8) * n14 + (n16 * n13 + n18 * n11 >> 8) * n12 >> 8) * (this.interpARGB ? ((int)(this.sp[6] * 255.0f)) : this.a2orig) >> 8;
                        }
                        else if (this.tformat == 2) {
                            n19 = (((n15 >> 24 & 0xFF) * n13 + (n17 >> 24 & 0xFF) * n11 >> 8) * n14 + ((n16 >> 24 & 0xFF) * n13 + (n18 >> 24 & 0xFF) * n11 >> 8) * n12 >> 8) * (this.interpARGB ? ((int)(this.sp[6] * 255.0f)) : this.a2orig) >> 8;
                        }
                        else {
                            n19 = (this.interpARGB ? ((int)(this.sp[6] * 255.0f)) : this.a2orig);
                        }
                        if (this.tformat == 1 || this.tformat == 2) {
                            n20 = (((n15 >> 16 & 0xFF) * n13 + (n17 >> 16 & 0xFF) * n11 >> 8) * n14 + ((n16 >> 16 & 0xFF) * n13 + (n18 >> 16 & 0xFF) * n11 >> 8) * n12 >> 8) * (this.interpARGB ? ((int)(this.sp[3] * 255.0f)) : this.r2) >> 8;
                            n21 = (((n15 >> 8 & 0xFF) * n13 + (n17 >> 8 & 0xFF) * n11 >> 8) * n14 + ((n16 >> 8 & 0xFF) * n13 + (n18 >> 8 & 0xFF) * n11 >> 8) * n12 >> 8) * (this.interpARGB ? ((int)(this.sp[4] * 255.0f)) : this.g2) >> 8;
                            n22 = (((n15 & 0xFF) * n13 + (n17 & 0xFF) * n11 >> 8) * n14 + ((n16 & 0xFF) * n13 + (n18 & 0xFF) * n11 >> 8) * n12 >> 8) * (this.interpARGB ? ((int)(this.sp[5] * 255.0f)) : this.b2) >> 8;
                        }
                        else if (this.interpARGB) {
                            n20 = (int)(this.sp[3] * 255.0f);
                            n21 = (int)(this.sp[4] * 255.0f);
                            n22 = (int)(this.sp[5] * 255.0f);
                        }
                        else {
                            n20 = this.r2;
                            n21 = this.g2;
                            n22 = this.b2;
                        }
                        final int n23 = this.smooth ? this.coverage(j) : 255;
                        if (n23 != 255) {
                            n19 = n19 * n23 >> 8;
                        }
                    }
                    else {
                        final int n24 = this.tpixels[n10];
                        if (this.tformat == 4) {
                            n19 = n24;
                            if (this.interpARGB) {
                                n20 = (int)(this.sp[3] * 255.0f);
                                n21 = (int)(this.sp[4] * 255.0f);
                                n22 = (int)(this.sp[5] * 255.0f);
                                if (this.sp[6] != 1.0f) {
                                    n19 = (int)(this.sp[6] * 255.0f) * n19 >> 8;
                                }
                            }
                            else {
                                n20 = this.r2;
                                n21 = this.g2;
                                n22 = this.b2;
                                n19 = this.a2orig * n19 >> 8;
                            }
                        }
                        else {
                            final int n25 = (this.tformat == 1) ? 255 : (n24 >> 24 & 0xFF);
                            if (this.interpARGB) {
                                n20 = (int)(this.sp[3] * 255.0f) * (n24 >> 16 & 0xFF) >> 8;
                                n21 = (int)(this.sp[4] * 255.0f) * (n24 >> 8 & 0xFF) >> 8;
                                n22 = (int)(this.sp[5] * 255.0f) * (n24 & 0xFF) >> 8;
                                n19 = (int)(this.sp[6] * 255.0f) * n25 >> 8;
                            }
                            else {
                                n20 = this.r2 * (n24 >> 16 & 0xFF) >> 8;
                                n21 = this.g2 * (n24 >> 8 & 0xFF) >> 8;
                                n22 = this.b2 * (n24 & 0xFF) >> 8;
                                n19 = this.a2orig * n25 >> 8;
                            }
                        }
                    }
                    if (n19 == 254 || n19 == 255) {
                        this.pixels[n4 + j] = (0xFF000000 | n20 << 16 | n21 << 8 | n22);
                        this.zbuffer[n4 + j] = this.sp[2];
                    }
                    else {
                        final int n26 = 255 - n19;
                        this.pixels[n4 + j] = (0xFF000000 | n20 * n19 + (this.pixels[n4 + j] >> 16 & 0xFF) * n26 >> 8 << 16 | (n21 * n19 + (this.pixels[n4 + j] >> 8 & 0xFF) * n26 & 0xFF00) | n22 * n19 + (this.pixels[n4 + j] & 0xFF) * n26 >> 8);
                        if (n19 > 204) {
                            this.zbuffer[n4 + j] = this.sp[2];
                        }
                    }
                }
                else {
                    int a2 = this.smooth ? this.coverage(j) : 255;
                    if (this.interpARGB) {
                        this.r2 = (int)(this.sp[3] * 255.0f);
                        this.g2 = (int)(this.sp[4] * 255.0f);
                        this.b2 = (int)(this.sp[5] * 255.0f);
                        if (this.sp[6] != 1.0f) {
                            a2 = a2 * (int)(this.sp[6] * 255.0f) >> 8;
                        }
                        if (a2 == 255) {
                            this.rgba = (0xFF000000 | this.r2 << 16 | this.g2 << 8 | this.b2);
                        }
                    }
                    else if (this.a2orig != 255) {
                        a2 = a2 * this.a2orig >> 8;
                    }
                    if (a2 == 255) {
                        this.pixels[n4 + j] = this.rgba;
                        this.zbuffer[n4 + j] = this.sp[2];
                    }
                    else {
                        final int n27 = this.pixels[n4 + j] >> 16 & 0xFF;
                        final int n28 = this.pixels[n4 + j] >> 8 & 0xFF;
                        final int n29 = this.pixels[n4 + j] & 0xFF;
                        this.a2 = a2;
                        final int n30 = 255 - this.a2;
                        this.pixels[n4 + j] = (0xFF000000 | n27 * n30 + this.r2 * this.a2 >> 8 << 16 | n28 * n30 + this.g2 * this.a2 >> 8 << 8 | n29 * n30 + this.b2 * this.a2 >> 8);
                        if (this.a2 > 204) {
                            this.zbuffer[n4 + j] = this.sp[2];
                        }
                    }
                }
            }
            if (!this.smooth || (j >= n5 && j <= n6)) {
                this.increment(this.sp, this.sdp);
            }
        }
        this.firstModY = -1;
        this.interpX = true;
    }
    
    private int coverage(final int n) {
        if (n >= this.aaleftfull && n <= this.aarightfull && this.firstModY == 0 && this.lastModY == 7) {
            return 255;
        }
        final int n2 = n * 8;
        final int n3 = n2 + 8;
        int n4 = 0;
        for (int i = this.firstModY; i <= this.lastModY; ++i) {
            if (this.aaleft[i] <= n3) {
                if (this.aaright[i] >= n2) {
                    n4 += ((this.aaright[i] < n3) ? this.aaright[i] : n3) - ((this.aaleft[i] > n2) ? this.aaleft[i] : n2);
                }
            }
        }
        final int n5 = n4 << 2;
        return (n5 == 256) ? 255 : n5;
    }
    
    private void incrementalize_y(final float[] array, final float[] array2, final float[] array3, final float[] array4, final int n) {
        float n2 = array2[1] - array[1];
        if (n2 == 0.0f) {
            n2 = 1.0f;
        }
        final float n3 = n + 0.5f - array[1];
        if (this.interpX) {
            array4[0] = (array2[0] - array[0]) / n2;
            array3[0] = array[0] + array4[0] * n3;
        }
        if (this.interpZ) {
            array4[2] = (array2[2] - array[2]) / n2;
            array3[2] = array[2] + array4[2] * n3;
        }
        if (this.interpARGB) {
            array4[3] = (array2[3] - array[3]) / n2;
            array4[4] = (array2[4] - array[4]) / n2;
            array4[5] = (array2[5] - array[5]) / n2;
            array4[6] = (array2[6] - array[6]) / n2;
            array3[3] = array[3] + array4[3] * n3;
            array3[4] = array[4] + array4[4] * n3;
            array3[5] = array[5] + array4[5] * n3;
            array3[6] = array[6] + array4[6] * n3;
        }
        if (this.interpUV) {
            array4[7] = (array2[7] - array[7]) / n2;
            array4[8] = (array2[8] - array[8]) / n2;
            array3[7] = array[7] + array4[7] * n3;
            array3[8] = array[8] + array4[8] * n3;
        }
    }
    
    private void incrementalize_x(final float[] array, final float[] array2, final float[] array3, final float[] array4, final int n) {
        float n2 = array2[0] - array[0];
        if (n2 == 0.0f) {
            n2 = 1.0f;
        }
        float n3 = n + 0.5f - array[0];
        if (this.smooth) {
            n2 /= 8.0f;
            n3 /= 8.0f;
        }
        if (this.interpX) {
            array4[0] = (array2[0] - array[0]) / n2;
            array3[0] = array[0] + array4[0] * n3;
        }
        if (this.interpZ) {
            array4[2] = (array2[2] - array[2]) / n2;
            array3[2] = array[2] + array4[2] * n3;
        }
        if (this.interpARGB) {
            array4[3] = (array2[3] - array[3]) / n2;
            array4[4] = (array2[4] - array[4]) / n2;
            array4[5] = (array2[5] - array[5]) / n2;
            array4[6] = (array2[6] - array[6]) / n2;
            array3[3] = array[3] + array4[3] * n3;
            array3[4] = array[4] + array4[4] * n3;
            array3[5] = array[5] + array4[5] * n3;
            array3[6] = array[6] + array4[6] * n3;
        }
        if (this.interpUV) {
            array4[7] = (array2[7] - array[7]) / n2;
            array4[8] = (array2[8] - array[8]) / n2;
            array3[7] = array[7] + array4[7] * n3;
            array3[8] = array[8] + array4[8] * n3;
        }
    }
    
    private void increment(final float[] array, final float[] array2) {
        if (this.interpX) {
            final int n = 0;
            array[n] += array2[0];
        }
        if (this.interpZ) {
            final int n2 = 2;
            array[n2] += array2[2];
        }
        if (this.interpARGB) {
            final int n3 = 3;
            array[n3] += array2[3];
            final int n4 = 4;
            array[n4] += array2[4];
            final int n5 = 5;
            array[n5] += array2[5];
            final int n6 = 6;
            array[n6] += array2[6];
        }
        if (this.interpUV) {
            final int n7 = 7;
            array[n7] += array2[7];
            final int n8 = 8;
            array[n8] += array2[8];
        }
    }
    
    public void setCamVertices(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        this.camX[0] = n;
        this.camX[1] = n4;
        this.camX[2] = n7;
        this.camY[0] = n2;
        this.camY[1] = n5;
        this.camY[2] = n8;
        this.camZ[0] = n3;
        this.camZ[1] = n6;
        this.camZ[2] = n9;
    }
    
    public void setVertices(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        this.vertices[0][0] = n;
        this.vertices[1][0] = n4;
        this.vertices[2][0] = n7;
        this.vertices[0][1] = n2;
        this.vertices[1][1] = n5;
        this.vertices[2][1] = n8;
        this.vertices[0][2] = n3;
        this.vertices[1][2] = n6;
        this.vertices[2][2] = n9;
    }
    
    boolean precomputeAccurateTexturing() {
        final int n = 0;
        final int n2 = 1;
        final int n3 = 2;
        final PMatrix3D pMatrix3D = new PMatrix3D(this.vertices[n][7], this.vertices[n][8], 1.0f, 0.0f, this.vertices[n2][7], this.vertices[n2][8], 1.0f, 0.0f, this.vertices[n3][7], this.vertices[n3][8], 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
        if (!pMatrix3D.invert()) {
            return false;
        }
        final float n4 = pMatrix3D.m00 * this.camX[n] + pMatrix3D.m01 * this.camX[n2] + pMatrix3D.m02 * this.camX[n3];
        final float n5 = pMatrix3D.m10 * this.camX[n] + pMatrix3D.m11 * this.camX[n2] + pMatrix3D.m12 * this.camX[n3];
        final float n6 = pMatrix3D.m20 * this.camX[n] + pMatrix3D.m21 * this.camX[n2] + pMatrix3D.m22 * this.camX[n3];
        final float n7 = pMatrix3D.m00 * this.camY[n] + pMatrix3D.m01 * this.camY[n2] + pMatrix3D.m02 * this.camY[n3];
        final float n8 = pMatrix3D.m10 * this.camY[n] + pMatrix3D.m11 * this.camY[n2] + pMatrix3D.m12 * this.camY[n3];
        final float n9 = pMatrix3D.m20 * this.camY[n] + pMatrix3D.m21 * this.camY[n2] + pMatrix3D.m22 * this.camY[n3];
        final float n10 = -(pMatrix3D.m00 * this.camZ[n] + pMatrix3D.m01 * this.camZ[n2] + pMatrix3D.m02 * this.camZ[n3]);
        final float n11 = -(pMatrix3D.m10 * this.camZ[n] + pMatrix3D.m11 * this.camZ[n2] + pMatrix3D.m12 * this.camZ[n3]);
        final float n12 = -(pMatrix3D.m20 * this.camZ[n] + pMatrix3D.m21 * this.camZ[n2] + pMatrix3D.m22 * this.camZ[n3]);
        final float n13 = n6;
        final float n14 = n9;
        final float n15 = n12;
        final float n16 = (float)this.twidth;
        final float n17 = (float)this.theight;
        final float n18 = n4 * n16 + n6;
        final float n19 = n7 * n16 + n9;
        final float n20 = n10 * n16 + n12;
        final float n21 = n5 * n17 + n6;
        final float n22 = n8 * n17 + n9;
        final float n23 = n11 * n17 + n12;
        final float n24 = n18 - n6;
        final float n25 = n19 - n9;
        final float n26 = n20 - n12;
        final float n27 = n21 - n6;
        final float n28 = n22 - n9;
        final float n29 = n23 - n12;
        this.ax = (n14 * n29 - n15 * n28) * n16;
        this.ay = (n15 * n27 - n13 * n29) * n16;
        this.az = (n13 * n28 - n14 * n27) * n16;
        this.bx = (n25 * n15 - n26 * n14) * n17;
        this.by = (n26 * n13 - n24 * n15) * n17;
        this.bz = (n24 * n14 - n25 * n13) * n17;
        this.cx = n28 * n26 - n29 * n25;
        this.cy = n29 * n24 - n27 * n26;
        this.cz = n27 * n25 - n28 * n24;
        this.nearPlaneWidth = this.parent.rightScreen - this.parent.leftScreen;
        this.nearPlaneHeight = this.parent.topScreen - this.parent.bottomScreen;
        this.nearPlaneDepth = this.parent.nearPlane;
        this.xmult = this.nearPlaneWidth / this.parent.width;
        this.ymult = this.nearPlaneHeight / this.parent.height;
        return true;
    }
    
    private int getTextureIndex(float n, float n2, final float[] array) {
        n = this.xmult * (n - this.parent.width / 2.0f + 0.5f);
        n2 = this.ymult * (n2 - this.parent.height / 2.0f + 0.5f);
        final float nearPlaneDepth = this.nearPlaneDepth;
        final float n3 = n * this.ax + n2 * this.ay + nearPlaneDepth * this.az;
        final float n4 = n * this.bx + n2 * this.by + nearPlaneDepth * this.bz;
        final float n5 = n * this.cx + n2 * this.cy + nearPlaneDepth * this.cz;
        int n6 = (int)(n3 / n5);
        int n7 = (int)(n4 / n5);
        array[0] = n3 / n5;
        array[1] = n4 / n5;
        if (array[0] < 0.0f) {
            array[0] = (float)(n6 = 0);
        }
        if (array[1] < 0.0f) {
            array[1] = (float)(n7 = 0);
        }
        if (array[0] >= this.twidth) {
            array[0] = (float)(this.twidth - 1);
            n6 = this.twidth - 1;
        }
        if (array[1] >= this.theight) {
            array[1] = (float)(this.theight - 1);
            n7 = this.theight - 1;
        }
        return n7 * this.twidth + n6;
    }
    
    public void setIntensities(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.vertices[0][3] = n;
        this.vertices[0][4] = n2;
        this.vertices[0][5] = n3;
        this.vertices[0][6] = n4;
        this.vertices[1][3] = n5;
        this.vertices[1][4] = n6;
        this.vertices[1][5] = n7;
        this.vertices[1][6] = n8;
        this.vertices[2][3] = n9;
        this.vertices[2][4] = n10;
        this.vertices[2][5] = n11;
        this.vertices[2][6] = n12;
    }
}