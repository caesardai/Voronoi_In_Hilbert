package processing.core;

public class PPolygon implements PConstants
{
    static final int DEFAULT_SIZE = 64;
    float[][] vertices;
    int vertexCount;
    float[] r;
    float[] dr;
    float[] l;
    float[] dl;
    float[] sp;
    float[] sdp;
    protected boolean interpX;
    protected boolean interpUV;
    protected boolean interpARGB;
    private int rgba;
    private int r2;
    private int g2;
    private int b2;
    private int a2;
    private int a2orig;
    PGraphics parent;
    int[] pixels;
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
    
    private final int MODYRES(final int n) {
        return n & 0x7;
    }
    
    public PPolygon(final PGraphics parent) {
        this.vertices = new float[64][36];
        this.r = new float[64];
        this.dr = new float[64];
        this.l = new float[64];
        this.dl = new float[64];
        this.sp = new float[64];
        this.sdp = new float[64];
        this.aaleft = new int[8];
        this.aaright = new int[8];
        this.parent = parent;
        this.reset(0);
    }
    
    protected void reset(final int vertexCount) {
        this.vertexCount = vertexCount;
        this.interpX = true;
        this.interpUV = false;
        this.interpARGB = true;
        this.timage = null;
    }
    
    protected float[] nextVertex() {
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
    
    protected void texture(final PImage timage) {
        this.timage = timage;
        if (timage != null) {
            this.tpixels = timage.pixels;
            this.twidth = timage.width;
            this.theight = timage.height;
            this.tformat = timage.format;
            this.twidth1 = this.twidth - 1;
            this.theight1 = this.theight - 1;
            this.interpUV = true;
        }
        else {
            this.interpUV = false;
        }
    }
    
    protected void renderPolygon(final float[][] vertices, final int vertexCount) {
        this.vertices = vertices;
        this.vertexCount = vertexCount;
        if (this.r.length < this.vertexCount) {
            this.r = new float[this.vertexCount];
            this.dr = new float[this.vertexCount];
            this.l = new float[this.vertexCount];
            this.dl = new float[this.vertexCount];
            this.sp = new float[this.vertexCount];
            this.sdp = new float[this.vertexCount];
        }
        this.render();
        this.checkExpand();
    }
    
    protected void renderTriangle(final float[] array, final float[] array2, final float[] array3) {
        this.vertices[0] = array;
        this.vertices[1] = array2;
        this.vertices[2] = array3;
        this.render();
        this.checkExpand();
    }
    
    protected void checkExpand() {
        if (this.smooth) {
            for (int i = 0; i < this.vertexCount; ++i) {
                final float[] array = this.vertices[i];
                final int n = 18;
                array[n] /= 8.0f;
                final float[] array2 = this.vertices[i];
                final int n2 = 19;
                array2[n2] /= 8.0f;
            }
        }
    }
    
    protected void render() {
        if (this.vertexCount < 3) {
            return;
        }
        this.pixels = this.parent.pixels;
        this.smooth = this.parent.smooth;
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
                final int n = 18;
                array[n] *= 8.0f;
                final float[] array2 = this.vertices[j];
                final int n2 = 19;
                array2[n2] *= 8.0f;
            }
            this.firstModY = -1;
        }
        int n3 = 0;
        float n4 = this.vertices[0][19];
        float n5 = this.vertices[0][19];
        for (int k = 1; k < this.vertexCount; ++k) {
            if (this.vertices[k][19] < n4) {
                n4 = this.vertices[k][19];
                n3 = k;
            }
            if (this.vertices[k][19] > n5) {
                n5 = this.vertices[k][19];
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
                this.incrementalizeY(this.vertices[n6], this.vertices[n11], this.l, this.dl, n8);
                n9 = (int)(this.vertices[n11][19] + 0.5f);
                n6 = n11;
            }
            while (n10 <= n8 && l > 0) {
                --l;
                final int n12 = (n7 != this.vertexCount - 1) ? (n7 + 1) : 0;
                this.incrementalizeY(this.vertices[n7], this.vertices[n12], this.r, this.dr, n8);
                n10 = (int)(this.vertices[n12][19] + 0.5f);
                n7 = n12;
            }
            while (n8 < n9 && n8 < n10) {
                if (n8 >= 0 && n8 < this.height) {
                    if (this.l[18] <= this.r[18]) {
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
    
    private void scanline(final int n, final float[] array, final float[] array2) {
        for (int i = 0; i < this.vertexCount; ++i) {
            this.sp[i] = 0.0f;
            this.sdp[i] = 0.0f;
        }
        int n2 = (int)(array[18] + 0.49999f);
        if (n2 < 0) {
            n2 = 0;
        }
        int n3 = (int)(array2[18] - 0.5f);
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
        this.incrementalizeX(array, array2, this.sp, this.sdp, n2);
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
            if (this.interpUV) {
                int twidth1 = (int)(this.sp[7] * this.twidth);
                int theight1 = (int)(this.sp[8] * this.theight);
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
                final int n8 = (int)(255.0f * (this.sp[7] * this.twidth - twidth1));
                final int n9 = (int)(255.0f * (this.sp[8] * this.theight - theight1));
                final int n10 = 255 - n8;
                final int n11 = 255 - n9;
                final int n12 = this.tpixels[n7];
                final int n13 = (theight1 < this.theight1) ? this.tpixels[n7 + this.twidth] : this.tpixels[n7];
                final int n14 = (twidth1 < this.twidth1) ? this.tpixels[n7 + 1] : this.tpixels[n7];
                final int n15 = (theight1 < this.theight1 && twidth1 < this.twidth1) ? this.tpixels[n7 + this.twidth + 1] : this.tpixels[n7];
                int n16;
                if (this.tformat == 4) {
                    n16 = ((n12 * n10 + n14 * n8 >> 8) * n11 + (n13 * n10 + n15 * n8 >> 8) * n9 >> 8) * (this.interpARGB ? ((int)(this.sp[6] * 255.0f)) : this.a2orig) >> 8;
                }
                else if (this.tformat == 2) {
                    n16 = (((n12 >> 24 & 0xFF) * n10 + (n14 >> 24 & 0xFF) * n8 >> 8) * n11 + ((n13 >> 24 & 0xFF) * n10 + (n15 >> 24 & 0xFF) * n8 >> 8) * n9 >> 8) * (this.interpARGB ? ((int)(this.sp[6] * 255.0f)) : this.a2orig) >> 8;
                }
                else {
                    n16 = (this.interpARGB ? ((int)(this.sp[6] * 255.0f)) : this.a2orig);
                }
                int r2;
                int g2;
                int b2;
                if (this.tformat == 1 || this.tformat == 2) {
                    r2 = (((n12 >> 16 & 0xFF) * n10 + (n14 >> 16 & 0xFF) * n8 >> 8) * n11 + ((n13 >> 16 & 0xFF) * n10 + (n15 >> 16 & 0xFF) * n8 >> 8) * n9 >> 8) * (this.interpARGB ? ((int)(this.sp[3] * 255.0f)) : this.r2) >> 8;
                    g2 = (((n12 >> 8 & 0xFF) * n10 + (n14 >> 8 & 0xFF) * n8 >> 8) * n11 + ((n13 >> 8 & 0xFF) * n10 + (n15 >> 8 & 0xFF) * n8 >> 8) * n9 >> 8) * (this.interpARGB ? ((int)(this.sp[4] * 255.0f)) : this.g2) >> 8;
                    b2 = (((n12 & 0xFF) * n10 + (n14 & 0xFF) * n8 >> 8) * n11 + ((n13 & 0xFF) * n10 + (n15 & 0xFF) * n8 >> 8) * n9 >> 8) * (this.interpARGB ? ((int)(this.sp[5] * 255.0f)) : this.b2) >> 8;
                }
                else if (this.interpARGB) {
                    r2 = (int)(this.sp[3] * 255.0f);
                    g2 = (int)(this.sp[4] * 255.0f);
                    b2 = (int)(this.sp[5] * 255.0f);
                }
                else {
                    r2 = this.r2;
                    g2 = this.g2;
                    b2 = this.b2;
                }
                final int n17 = this.smooth ? this.coverage(j) : 255;
                if (n17 != 255) {
                    n16 = n16 * n17 >> 8;
                }
                if (n16 == 254 || n16 == 255) {
                    this.pixels[n4 + j] = (0xFF000000 | r2 << 16 | g2 << 8 | b2);
                }
                else {
                    final int n18 = 255 - n16;
                    this.pixels[n4 + j] = (0xFF000000 | r2 * n16 + (this.pixels[n4 + j] >> 16 & 0xFF) * n18 >> 8 << 16 | (g2 * n16 + (this.pixels[n4 + j] >> 8 & 0xFF) * n18 & 0xFF00) | b2 * n16 + (this.pixels[n4 + j] & 0xFF) * n18 >> 8);
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
                }
                else {
                    final int n19 = this.pixels[n4 + j] >> 16 & 0xFF;
                    final int n20 = this.pixels[n4 + j] >> 8 & 0xFF;
                    final int n21 = this.pixels[n4 + j] & 0xFF;
                    this.a2 = a2;
                    final int n22 = 255 - this.a2;
                    this.pixels[n4 + j] = (0xFF000000 | n19 * n22 + this.r2 * this.a2 >> 8 << 16 | n20 * n22 + this.g2 * this.a2 >> 8 << 8 | n21 * n22 + this.b2 * this.a2 >> 8);
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
    
    private void incrementalizeY(final float[] array, final float[] array2, final float[] array3, final float[] array4, final int n) {
        float n2 = array2[19] - array[19];
        if (n2 == 0.0f) {
            n2 = 1.0f;
        }
        final float n3 = n + 0.5f - array[19];
        if (this.interpX) {
            array4[18] = (array2[18] - array[18]) / n2;
            array3[18] = array[18] + array4[18] * n3;
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
    
    private void incrementalizeX(final float[] array, final float[] array2, final float[] array3, final float[] array4, final int n) {
        float n2 = array2[18] - array[18];
        if (n2 == 0.0f) {
            n2 = 1.0f;
        }
        float n3 = n + 0.5f - array[18];
        if (this.smooth) {
            n2 /= 8.0f;
            n3 /= 8.0f;
        }
        if (this.interpX) {
            array4[18] = (array2[18] - array[18]) / n2;
            array3[18] = array[18] + array4[18] * n3;
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
            final int n = 18;
            array[n] += array2[18];
        }
        if (this.interpARGB) {
            final int n2 = 3;
            array[n2] += array2[3];
            final int n3 = 4;
            array[n3] += array2[4];
            final int n4 = 5;
            array[n4] += array2[5];
            final int n5 = 6;
            array[n5] += array2[6];
        }
        if (this.interpUV) {
            final int n6 = 7;
            array[n6] += array2[7];
            final int n7 = 8;
            array[n7] += array2[8];
        }
    }
}

 