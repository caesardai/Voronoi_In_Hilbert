package processing.core;

public class PLine implements PConstants
{
    private int[] m_pixels;
    private float[] m_zbuffer;
    private int m_index;
    static final int R_COLOR = 1;
    static final int R_ALPHA = 2;
    static final int R_SPATIAL = 8;
    static final int R_THICK = 4;
    static final int R_SMOOTH = 16;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int SCREEN_WIDTH1;
    private int SCREEN_HEIGHT1;
    public boolean INTERPOLATE_RGB;
    public boolean INTERPOLATE_ALPHA;
    public boolean INTERPOLATE_Z;
    public boolean INTERPOLATE_THICK;
    private boolean SMOOTH;
    private int m_stroke;
    public int m_drawFlags;
    private float[] x_array;
    private float[] y_array;
    private float[] z_array;
    private float[] r_array;
    private float[] g_array;
    private float[] b_array;
    private float[] a_array;
    private int o0;
    private int o1;
    private float m_r0;
    private float m_g0;
    private float m_b0;
    private float m_a0;
    private float m_z0;
    private float dz;
    private float dr;
    private float dg;
    private float db;
    private float da;
    private PGraphics parent;
    
    public PLine(final PGraphics parent) {
        this.INTERPOLATE_Z = false;
        this.x_array = new float[2];
        this.y_array = new float[2];
        this.z_array = new float[2];
        this.r_array = new float[2];
        this.g_array = new float[2];
        this.b_array = new float[2];
        this.a_array = new float[2];
        this.parent = parent;
    }
    
    public void reset() {
        this.SCREEN_WIDTH = this.parent.width;
        this.SCREEN_HEIGHT = this.parent.height;
        this.SCREEN_WIDTH1 = this.SCREEN_WIDTH - 1;
        this.SCREEN_HEIGHT1 = this.SCREEN_HEIGHT - 1;
        this.m_pixels = this.parent.pixels;
        if (this.parent instanceof PGraphics3D) {
            this.m_zbuffer = ((PGraphics3D)this.parent).zbuffer;
        }
        this.INTERPOLATE_RGB = false;
        this.INTERPOLATE_ALPHA = false;
        this.m_drawFlags = 0;
        this.m_index = 0;
    }
    
    public void setVertices(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        if (n3 != n6 || n3 != 0.0f || n6 != 0.0f || this.INTERPOLATE_Z) {
            this.INTERPOLATE_Z = true;
            this.m_drawFlags |= 0x8;
        }
        else {
            this.INTERPOLATE_Z = false;
            this.m_drawFlags &= 0xFFFFFFF7;
        }
        this.z_array[0] = n3;
        this.z_array[1] = n6;
        this.x_array[0] = n;
        this.x_array[1] = n4;
        this.y_array[0] = n2;
        this.y_array[1] = n5;
    }
    
    public void setIntensities(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8) {
        this.a_array[0] = (n4 * 253.0f + 1.0f) * 65536.0f;
        this.a_array[1] = (n8 * 253.0f + 1.0f) * 65536.0f;
        if (n4 != 1.0f || n8 != 1.0f) {
            this.INTERPOLATE_ALPHA = true;
            this.m_drawFlags |= 0x2;
        }
        else {
            this.INTERPOLATE_ALPHA = false;
            this.m_drawFlags &= 0xFFFFFFFD;
        }
        this.r_array[0] = (n * 253.0f + 1.0f) * 65536.0f;
        this.r_array[1] = (n5 * 253.0f + 1.0f) * 65536.0f;
        this.g_array[0] = (n2 * 253.0f + 1.0f) * 65536.0f;
        this.g_array[1] = (n6 * 253.0f + 1.0f) * 65536.0f;
        this.b_array[0] = (n3 * 253.0f + 1.0f) * 65536.0f;
        this.b_array[1] = (n7 * 253.0f + 1.0f) * 65536.0f;
        if (n != n5) {
            this.INTERPOLATE_RGB = true;
            this.m_drawFlags |= 0x1;
        }
        else if (n2 != n6) {
            this.INTERPOLATE_RGB = true;
            this.m_drawFlags |= 0x1;
        }
        else if (n3 != n7) {
            this.INTERPOLATE_RGB = true;
            this.m_drawFlags |= 0x1;
        }
        else {
            this.m_stroke = (0xFF000000 | (int)(255.0f * n) << 16 | (int)(255.0f * n2) << 8 | (int)(255.0f * n3));
            this.INTERPOLATE_RGB = false;
            this.m_drawFlags &= 0xFFFFFFFE;
        }
    }
    
    public void setIndex(final int index) {
        this.m_index = index;
        if (this.m_index == -1) {
            this.m_index = 0;
        }
    }
    
    public void draw() {
        if (this.parent.smooth) {
            this.SMOOTH = true;
            this.m_drawFlags |= 0x10;
        }
        else {
            this.SMOOTH = false;
            this.m_drawFlags &= 0xFFFFFFEF;
        }
        if (!this.lineClipping()) {
            return;
        }
        boolean b = false;
        if (this.x_array[1] < this.x_array[0]) {
            final float n = this.x_array[1];
            this.x_array[1] = this.x_array[0];
            this.x_array[0] = n;
            final float n2 = this.y_array[1];
            this.y_array[1] = this.y_array[0];
            this.y_array[0] = n2;
            final float n3 = this.z_array[1];
            this.z_array[1] = this.z_array[0];
            this.z_array[0] = n3;
            final float n4 = this.r_array[1];
            this.r_array[1] = this.r_array[0];
            this.r_array[0] = n4;
            final float n5 = this.g_array[1];
            this.g_array[1] = this.g_array[0];
            this.g_array[0] = n5;
            final float n6 = this.b_array[1];
            this.b_array[1] = this.b_array[0];
            this.b_array[0] = n6;
            final float n7 = this.a_array[1];
            this.a_array[1] = this.a_array[0];
            this.a_array[0] = n7;
        }
        int a = (int)this.x_array[1] - (int)this.x_array[0];
        int a2 = (int)this.y_array[1] - (int)this.y_array[0];
        if (Math.abs(a2) > Math.abs(a)) {
            final int n8 = a2;
            a2 = a;
            a = n8;
            b = true;
        }
        int n9;
        int n10;
        int n11;
        if (a < 0) {
            this.o0 = 1;
            this.o1 = 0;
            n9 = (int)this.x_array[1];
            n10 = (int)this.y_array[1];
            n11 = -a;
        }
        else {
            this.o0 = 0;
            this.o1 = 1;
            n9 = (int)this.x_array[0];
            n10 = (int)this.y_array[0];
            n11 = a;
        }
        int n12;
        if (n11 == 0) {
            n12 = 0;
        }
        else {
            n12 = (a2 << 16) / a;
        }
        this.m_r0 = this.r_array[this.o0];
        this.m_g0 = this.g_array[this.o0];
        this.m_b0 = this.b_array[this.o0];
        if (this.INTERPOLATE_RGB) {
            this.dr = (this.r_array[this.o1] - this.r_array[this.o0]) / n11;
            this.dg = (this.g_array[this.o1] - this.g_array[this.o0]) / n11;
            this.db = (this.b_array[this.o1] - this.b_array[this.o0]) / n11;
        }
        else {
            this.dr = 0.0f;
            this.dg = 0.0f;
            this.db = 0.0f;
        }
        this.m_a0 = this.a_array[this.o0];
        if (this.INTERPOLATE_ALPHA) {
            this.da = (this.a_array[this.o1] - this.a_array[this.o0]) / n11;
        }
        else {
            this.da = 0.0f;
        }
        this.m_z0 = this.z_array[this.o0];
        if (this.INTERPOLATE_Z) {
            this.dz = (this.z_array[this.o1] - this.z_array[this.o0]) / n11;
        }
        else {
            this.dz = 0.0f;
        }
        if (n11 == 0) {
            if (this.INTERPOLATE_ALPHA) {
                this.drawPoint_alpha(n9, n10);
            }
            else {
                this.drawPoint(n9, n10);
            }
            return;
        }
        if (this.SMOOTH) {
            this.drawLine_smooth(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 0) {
            this.drawLine_plain(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 2) {
            this.drawLine_plain_alpha(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 1) {
            this.drawLine_color(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 3) {
            this.drawLine_color_alpha(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 8) {
            this.drawLine_plain_spatial(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 10) {
            this.drawLine_plain_alpha_spatial(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 9) {
            this.drawLine_color_spatial(n9, n10, n12, n11, b);
        }
        else if (this.m_drawFlags == 11) {
            this.drawLine_color_alpha_spatial(n9, n10, n12, n11, b);
        }
    }
    
    public boolean lineClipping() {
        final int lineClipCode = this.lineClipCode(this.x_array[0], this.y_array[0]);
        final int lineClipCode2 = this.lineClipCode(this.x_array[1], this.y_array[1]);
        final int n = lineClipCode | lineClipCode2;
        if ((lineClipCode & lineClipCode2) != 0x0) {
            return false;
        }
        if (n != 0) {
            float n2 = 0.0f;
            float n3 = 1.0f;
            for (int i = 0; i < 4; ++i) {
                if ((n >> i) % 2 == 1) {
                    final float lineSlope = this.lineSlope(this.x_array[0], this.y_array[0], this.x_array[1], this.y_array[1], i + 1);
                    if ((lineClipCode >> i) % 2 == 1) {
                        n2 = ((lineSlope > n2) ? lineSlope : n2);
                    }
                    else {
                        n3 = ((lineSlope < n3) ? lineSlope : n3);
                    }
                }
            }
            if (n2 > n3) {
                return false;
            }
            final float n4 = this.x_array[0];
            final float n5 = this.y_array[0];
            this.x_array[0] = n4 + n2 * (this.x_array[1] - n4);
            this.y_array[0] = n5 + n2 * (this.y_array[1] - n5);
            this.x_array[1] = n4 + n3 * (this.x_array[1] - n4);
            this.y_array[1] = n5 + n3 * (this.y_array[1] - n5);
            if (this.INTERPOLATE_RGB) {
                final float n6 = this.r_array[0];
                this.r_array[0] = n6 + n2 * (this.r_array[1] - n6);
                this.r_array[1] = n6 + n3 * (this.r_array[1] - n6);
                final float n7 = this.g_array[0];
                this.g_array[0] = n7 + n2 * (this.g_array[1] - n7);
                this.g_array[1] = n7 + n3 * (this.g_array[1] - n7);
                final float n8 = this.b_array[0];
                this.b_array[0] = n8 + n2 * (this.b_array[1] - n8);
                this.b_array[1] = n8 + n3 * (this.b_array[1] - n8);
            }
            if (this.INTERPOLATE_ALPHA) {
                final float n9 = this.a_array[0];
                this.a_array[0] = n9 + n2 * (this.a_array[1] - n9);
                this.a_array[1] = n9 + n3 * (this.a_array[1] - n9);
            }
        }
        return true;
    }
    
    private int lineClipCode(final float n, final float n2) {
        return ((n2 < 0) ? 8 : 0) | (((int)n2 > this.SCREEN_HEIGHT1) ? 4 : 0) | ((n < 0) ? 2 : 0) | (((int)n > this.SCREEN_WIDTH1) ? 1 : 0);
    }
    
    private float lineSlope(final float n, final float n2, final float n3, final float n4, final int n5) {
        final int n6 = 0;
        final int n7 = 0;
        final int screen_WIDTH1 = this.SCREEN_WIDTH1;
        final int screen_HEIGHT1 = this.SCREEN_HEIGHT1;
        switch (n5) {
            case 4: {
                return (n7 - n2) / (n4 - n2);
            }
            case 3: {
                return (screen_HEIGHT1 - n2) / (n4 - n2);
            }
            case 2: {
                return (n6 - n) / (n3 - n);
            }
            case 1: {
                return (screen_WIDTH1 - n) / (n3 - n);
            }
            default: {
                return -1.0f;
            }
        }
    }
    
    private void drawPoint(final int n, final int n2) {
        final float z0 = this.m_z0;
        final int n3 = n2 * this.SCREEN_WIDTH + n;
        if (this.m_zbuffer == null) {
            this.m_pixels[n3] = this.m_stroke;
        }
        else if (z0 <= this.m_zbuffer[n3]) {
            this.m_pixels[n3] = this.m_stroke;
            this.m_zbuffer[n3] = z0;
        }
    }
    
    private void drawPoint_alpha(final int n, final int n2) {
        final int n3 = (int)this.a_array[0];
        final int n4 = this.m_stroke & 0xFF0000;
        final int n5 = this.m_stroke & 0xFF00;
        final int n6 = this.m_stroke & 0xFF;
        final float z0 = this.m_z0;
        final int n7 = n2 * this.SCREEN_WIDTH + n;
        if (this.m_zbuffer == null || z0 <= this.m_zbuffer[n7]) {
            final int n8 = n3 >> 16;
            final int n9 = this.m_pixels[n7];
            final int n10 = n9 & 0xFF00;
            final int n11 = n9 & 0xFF;
            final int n12 = n9 & 0xFF0000;
            this.m_pixels[n7] = (0xFF000000 | (n12 + ((n4 - n12) * n8 >> 8) & 0xFF0000) | (n10 + ((n5 - n10) * n8 >> 8) & 0xFF00) | (n11 + ((n6 - n11) * n8 >> 8) & 0xFF));
            if (this.m_zbuffer != null) {
                this.m_zbuffer[n7] = z0;
            }
        }
    }
    
    private void drawLine_plain(int i, int j, final int n, int n2, final boolean b) {
        if (b) {
            n2 += j;
            int n3 = 32768 + (i << 16);
            while (j <= n2) {
                final int n4 = j * this.SCREEN_WIDTH + (n3 >> 16);
                this.m_pixels[n4] = this.m_stroke;
                if (this.m_zbuffer != null) {
                    this.m_zbuffer[n4] = this.m_z0;
                }
                n3 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n5 = 32768 + (j << 16);
            while (i <= n2) {
                final int n6 = (n5 >> 16) * this.SCREEN_WIDTH + i;
                this.m_pixels[n6] = this.m_stroke;
                if (this.m_zbuffer != null) {
                    this.m_zbuffer[n6] = this.m_z0;
                }
                n5 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_plain_alpha(int i, int j, final int n, int n2, final boolean b) {
        final int n3 = this.m_stroke & 0xFF0000;
        final int n4 = this.m_stroke & 0xFF00;
        final int n5 = this.m_stroke & 0xFF;
        int n6 = (int)this.m_a0;
        if (b) {
            n2 += j;
            int n7 = 32768 + (i << 16);
            while (j <= n2) {
                final int n8 = j * this.SCREEN_WIDTH + (n7 >> 16);
                final int n9 = n6 >> 16;
                final int n10 = this.m_pixels[n8];
                final int n11 = n10 & 0xFF00;
                final int n12 = n10 & 0xFF;
                final int n13 = n10 & 0xFF0000;
                this.m_pixels[n8] = (0xFF000000 | (n13 + ((n3 - n13) * n9 >> 8) & 0xFF0000) | (n11 + ((n4 - n11) * n9 >> 8) & 0xFF00) | (n12 + ((n5 - n12) * n9 >> 8) & 0xFF));
                n6 += (int)this.da;
                n7 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n14 = 32768 + (j << 16);
            while (i <= n2) {
                final int n15 = (n14 >> 16) * this.SCREEN_WIDTH + i;
                final int n16 = n6 >> 16;
                final int n17 = this.m_pixels[n15];
                final int n18 = n17 & 0xFF00;
                final int n19 = n17 & 0xFF;
                final int n20 = n17 & 0xFF0000;
                this.m_pixels[n15] = (0xFF000000 | (n20 + ((n3 - n20) * n16 >> 8) & 0xFF0000) | (n18 + ((n4 - n18) * n16 >> 8) & 0xFF00) | (n19 + ((n5 - n19) * n16 >> 8) & 0xFF));
                n6 += (int)this.da;
                n14 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_color(int i, int j, final int n, int n2, final boolean b) {
        int n3 = (int)this.m_r0;
        int n4 = (int)this.m_g0;
        int n5 = (int)this.m_b0;
        if (b) {
            n2 += j;
            int n6 = 32768 + (i << 16);
            while (j <= n2) {
                final int n7 = j * this.SCREEN_WIDTH + (n6 >> 16);
                this.m_pixels[n7] = (0xFF000000 | ((n3 & 0xFF0000) | (n4 >> 8 & 0xFF00) | n5 >> 16));
                if (this.m_zbuffer != null) {
                    this.m_zbuffer[n7] = this.m_z0;
                }
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n6 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n8 = 32768 + (j << 16);
            while (i <= n2) {
                final int n9 = (n8 >> 16) * this.SCREEN_WIDTH + i;
                this.m_pixels[n9] = (0xFF000000 | ((n3 & 0xFF0000) | (n4 >> 8 & 0xFF00) | n5 >> 16));
                if (this.m_zbuffer != null) {
                    this.m_zbuffer[n9] = this.m_z0;
                }
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n8 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_color_alpha(int i, int j, final int n, int n2, final boolean b) {
        int n3 = (int)this.m_r0;
        int n4 = (int)this.m_g0;
        int n5 = (int)this.m_b0;
        int n6 = (int)this.m_a0;
        if (b) {
            n2 += j;
            int n7 = 32768 + (i << 16);
            while (j <= n2) {
                final int n8 = j * this.SCREEN_WIDTH + (n7 >> 16);
                final int n9 = n3 & 0xFF0000;
                final int n10 = n4 >> 8 & 0xFF00;
                final int n11 = n5 >> 16;
                final int n12 = this.m_pixels[n8];
                final int n13 = n12 & 0xFF00;
                final int n14 = n12 & 0xFF;
                final int n15 = n12 & 0xFF0000;
                final int n16 = n6 >> 16;
                this.m_pixels[n8] = (0xFF000000 | (n15 + ((n9 - n15) * n16 >> 8) & 0xFF0000) | (n13 + ((n10 - n13) * n16 >> 8) & 0xFF00) | (n14 + ((n11 - n14) * n16 >> 8) & 0xFF));
                if (this.m_zbuffer != null) {
                    this.m_zbuffer[n8] = this.m_z0;
                }
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n6 += (int)this.da;
                n7 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n17 = 32768 + (j << 16);
            while (i <= n2) {
                final int n18 = (n17 >> 16) * this.SCREEN_WIDTH + i;
                final int n19 = n3 & 0xFF0000;
                final int n20 = n4 >> 8 & 0xFF00;
                final int n21 = n5 >> 16;
                final int n22 = this.m_pixels[n18];
                final int n23 = n22 & 0xFF00;
                final int n24 = n22 & 0xFF;
                final int n25 = n22 & 0xFF0000;
                final int n26 = n6 >> 16;
                this.m_pixels[n18] = (0xFF000000 | (n25 + ((n19 - n25) * n26 >> 8) & 0xFF0000) | (n23 + ((n20 - n23) * n26 >> 8) & 0xFF00) | (n24 + ((n21 - n24) * n26 >> 8) & 0xFF));
                if (this.m_zbuffer != null) {
                    this.m_zbuffer[n18] = this.m_z0;
                }
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n6 += (int)this.da;
                n17 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_plain_spatial(int i, int j, final int n, int n2, final boolean b) {
        float z0 = this.m_z0;
        if (b) {
            n2 += j;
            int n3 = 32768 + (i << 16);
            while (j <= n2) {
                final int n4 = j * this.SCREEN_WIDTH + (n3 >> 16);
                if (n4 < this.m_pixels.length && z0 <= this.m_zbuffer[n4]) {
                    this.m_pixels[n4] = this.m_stroke;
                    this.m_zbuffer[n4] = z0;
                }
                z0 += this.dz;
                n3 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n5 = 32768 + (j << 16);
            while (i <= n2) {
                final int n6 = (n5 >> 16) * this.SCREEN_WIDTH + i;
                if (n6 < this.m_pixels.length && z0 <= this.m_zbuffer[n6]) {
                    this.m_pixels[n6] = this.m_stroke;
                    this.m_zbuffer[n6] = z0;
                }
                z0 += this.dz;
                n5 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_plain_alpha_spatial(int i, int j, final int n, int n2, final boolean b) {
        float z0 = this.m_z0;
        final int n3 = this.m_stroke & 0xFF0000;
        final int n4 = this.m_stroke & 0xFF00;
        final int n5 = this.m_stroke & 0xFF;
        int n6 = (int)this.m_a0;
        if (b) {
            n2 += j;
            int n7 = 32768 + (i << 16);
            while (j <= n2) {
                final int n8 = j * this.SCREEN_WIDTH + (n7 >> 16);
                if (n8 < this.m_pixels.length && z0 <= this.m_zbuffer[n8]) {
                    final int n9 = n6 >> 16;
                    final int n10 = this.m_pixels[n8];
                    final int n11 = n10 & 0xFF00;
                    final int n12 = n10 & 0xFF;
                    final int n13 = n10 & 0xFF0000;
                    this.m_pixels[n8] = (0xFF000000 | (n13 + ((n3 - n13) * n9 >> 8) & 0xFF0000) | (n11 + ((n4 - n11) * n9 >> 8) & 0xFF00) | (n12 + ((n5 - n12) * n9 >> 8) & 0xFF));
                    this.m_zbuffer[n8] = z0;
                }
                z0 += this.dz;
                n6 += (int)this.da;
                n7 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n14 = 32768 + (j << 16);
            while (i <= n2) {
                final int n15 = (n14 >> 16) * this.SCREEN_WIDTH + i;
                if (n15 < this.m_pixels.length && z0 <= this.m_zbuffer[n15]) {
                    final int n16 = n6 >> 16;
                    final int n17 = this.m_pixels[n15];
                    final int n18 = n17 & 0xFF00;
                    final int n19 = n17 & 0xFF;
                    final int n20 = n17 & 0xFF0000;
                    this.m_pixels[n15] = (0xFF000000 | (n20 + ((n3 - n20) * n16 >> 8) & 0xFF0000) | (n18 + ((n4 - n18) * n16 >> 8) & 0xFF00) | (n19 + ((n5 - n19) * n16 >> 8) & 0xFF));
                    this.m_zbuffer[n15] = z0;
                }
                z0 += this.dz;
                n6 += (int)this.da;
                n14 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_color_spatial(int i, int j, final int n, int n2, final boolean b) {
        float z0 = this.m_z0;
        int n3 = (int)this.m_r0;
        int n4 = (int)this.m_g0;
        int n5 = (int)this.m_b0;
        if (b) {
            n2 += j;
            int n6 = 32768 + (i << 16);
            while (j <= n2) {
                final int n7 = j * this.SCREEN_WIDTH + (n6 >> 16);
                if (z0 <= this.m_zbuffer[n7]) {
                    this.m_pixels[n7] = (0xFF000000 | ((n3 & 0xFF0000) | (n4 >> 8 & 0xFF00) | n5 >> 16));
                    this.m_zbuffer[n7] = z0;
                }
                z0 += this.dz;
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n6 += n;
                ++j;
            }
            return;
        }
        n2 += i;
        int n8 = 32768 + (j << 16);
        while (i <= n2) {
            final int n9 = (n8 >> 16) * this.SCREEN_WIDTH + i;
            if (z0 <= this.m_zbuffer[n9]) {
                this.m_pixels[n9] = (0xFF000000 | ((n3 & 0xFF0000) | (n4 >> 8 & 0xFF00) | n5 >> 16));
                this.m_zbuffer[n9] = z0;
            }
            z0 += this.dz;
            n3 += (int)this.dr;
            n4 += (int)this.dg;
            n5 += (int)this.db;
            n8 += n;
            ++i;
        }
    }
    
    private void drawLine_color_alpha_spatial(int i, int j, final int n, int n2, final boolean b) {
        float z0 = this.m_z0;
        int n3 = (int)this.m_r0;
        int n4 = (int)this.m_g0;
        int n5 = (int)this.m_b0;
        int n6 = (int)this.m_a0;
        if (b) {
            n2 += j;
            int n7 = 32768 + (i << 16);
            while (j <= n2) {
                final int n8 = j * this.SCREEN_WIDTH + (n7 >> 16);
                if (z0 <= this.m_zbuffer[n8]) {
                    final int n9 = n3 & 0xFF0000;
                    final int n10 = n4 >> 8 & 0xFF00;
                    final int n11 = n5 >> 16;
                    final int n12 = this.m_pixels[n8];
                    final int n13 = n12 & 0xFF00;
                    final int n14 = n12 & 0xFF;
                    final int n15 = n12 & 0xFF0000;
                    final int n16 = n6 >> 16;
                    this.m_pixels[n8] = (0xFF000000 | (n15 + ((n9 - n15) * n16 >> 8) & 0xFF0000) | (n13 + ((n10 - n13) * n16 >> 8) & 0xFF00) | (n14 + ((n11 - n14) * n16 >> 8) & 0xFF));
                    this.m_zbuffer[n8] = z0;
                }
                z0 += this.dz;
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n6 += (int)this.da;
                n7 += n;
                ++j;
            }
        }
        else {
            n2 += i;
            int n17 = 32768 + (j << 16);
            while (i <= n2) {
                final int n18 = (n17 >> 16) * this.SCREEN_WIDTH + i;
                if (z0 <= this.m_zbuffer[n18]) {
                    final int n19 = n3 & 0xFF0000;
                    final int n20 = n4 >> 8 & 0xFF00;
                    final int n21 = n5 >> 16;
                    final int n22 = this.m_pixels[n18];
                    final int n23 = n22 & 0xFF00;
                    final int n24 = n22 & 0xFF;
                    final int n25 = n22 & 0xFF0000;
                    final int n26 = n6 >> 16;
                    this.m_pixels[n18] = (0xFF000000 | (n25 + ((n19 - n25) * n26 >> 8) & 0xFF0000) | (n23 + ((n20 - n23) * n26 >> 8) & 0xFF00) | (n24 + ((n21 - n24) * n26 >> 8) & 0xFF));
                    this.m_zbuffer[n18] = z0;
                }
                z0 += this.dz;
                n3 += (int)this.dr;
                n4 += (int)this.dg;
                n5 += (int)this.db;
                n6 += (int)this.da;
                n17 += n;
                ++i;
            }
        }
    }
    
    private void drawLine_smooth(final int n, final int n2, final int n3, final int n4, final boolean b) {
        float z0 = this.m_z0;
        int n5 = (int)this.m_r0;
        int n6 = (int)this.m_g0;
        int n7 = (int)this.m_b0;
        int n8 = (int)this.m_a0;
        if (b) {
            int n9 = n << 16;
            int n10 = n2 << 16;
            while (n10 >> 16 < n4 + n2) {
                final int n11 = (n10 >> 16) * this.SCREEN_WIDTH + (n9 >> 16);
                final int n12 = n5 & 0xFF0000;
                final int n13 = n6 >> 8 & 0xFF00;
                final int n14 = n7 >> 16;
                if (this.m_zbuffer == null || z0 <= this.m_zbuffer[n11]) {
                    final int n15 = (~n9 >> 8 & 0xFF) * (n8 >> 16) >> 8;
                    final int n16 = this.m_pixels[n11];
                    final int n17 = n16 & 0xFF00;
                    final int n18 = n16 & 0xFF;
                    final int n19 = n16 & 0xFF0000;
                    this.m_pixels[n11] = (0xFF000000 | (n19 + ((n12 - n19) * n15 >> 8) & 0xFF0000) | (n17 + ((n13 - n17) * n15 >> 8) & 0xFF00) | (n18 + ((n14 - n18) * n15 >> 8) & 0xFF));
                    if (this.m_zbuffer != null) {
                        this.m_zbuffer[n11] = z0;
                    }
                }
                final int n20 = (n9 >> 16) + 1;
                if (n20 >= this.SCREEN_WIDTH) {
                    n9 += n3;
                    n10 += 65536;
                }
                else {
                    final int n21 = (n10 >> 16) * this.SCREEN_WIDTH + n20;
                    if (this.m_zbuffer == null || z0 <= this.m_zbuffer[n21]) {
                        final int n22 = (n9 >> 8 & 0xFF) * (n8 >> 16) >> 8;
                        final int n23 = this.m_pixels[n21];
                        final int n24 = n23 & 0xFF00;
                        final int n25 = n23 & 0xFF;
                        final int n26 = n23 & 0xFF0000;
                        this.m_pixels[n21] = (0xFF000000 | (n26 + ((n12 - n26) * n22 >> 8) & 0xFF0000) | (n24 + ((n13 - n24) * n22 >> 8) & 0xFF00) | (n25 + ((n14 - n25) * n22 >> 8) & 0xFF));
                        if (this.m_zbuffer != null) {
                            this.m_zbuffer[n21] = z0;
                        }
                    }
                    n9 += n3;
                    n10 += 65536;
                    z0 += this.dz;
                    n5 += (int)this.dr;
                    n6 += (int)this.dg;
                    n7 += (int)this.db;
                    n8 += (int)this.da;
                }
            }
        }
        else {
            int n27 = n << 16;
            int n28 = n2 << 16;
            while (n27 >> 16 < n4 + n) {
                final int n29 = (n28 >> 16) * this.SCREEN_WIDTH + (n27 >> 16);
                final int n30 = n5 & 0xFF0000;
                final int n31 = n6 >> 8 & 0xFF00;
                final int n32 = n7 >> 16;
                if (this.m_zbuffer == null || z0 <= this.m_zbuffer[n29]) {
                    final int n33 = (~n28 >> 8 & 0xFF) * (n8 >> 16) >> 8;
                    final int n34 = this.m_pixels[n29];
                    final int n35 = n34 & 0xFF00;
                    final int n36 = n34 & 0xFF;
                    final int n37 = n34 & 0xFF0000;
                    this.m_pixels[n29] = (0xFF000000 | (n37 + ((n30 - n37) * n33 >> 8) & 0xFF0000) | (n35 + ((n31 - n35) * n33 >> 8) & 0xFF00) | (n36 + ((n32 - n36) * n33 >> 8) & 0xFF));
                    if (this.m_zbuffer != null) {
                        this.m_zbuffer[n29] = z0;
                    }
                }
                final int n38 = (n28 >> 16) + 1;
                if (n38 >= this.SCREEN_HEIGHT) {
                    n27 += 65536;
                    n28 += n3;
                }
                else {
                    final int n39 = n38 * this.SCREEN_WIDTH + (n27 >> 16);
                    if (this.m_zbuffer == null || z0 <= this.m_zbuffer[n39]) {
                        final int n40 = (n28 >> 8 & 0xFF) * (n8 >> 16) >> 8;
                        final int n41 = this.m_pixels[n39];
                        final int n42 = n41 & 0xFF00;
                        final int n43 = n41 & 0xFF;
                        final int n44 = n41 & 0xFF0000;
                        this.m_pixels[n39] = (0xFF000000 | (n44 + ((n30 - n44) * n40 >> 8) & 0xFF0000) | (n42 + ((n31 - n42) * n40 >> 8) & 0xFF00) | (n43 + ((n32 - n43) * n40 >> 8) & 0xFF));
                        if (this.m_zbuffer != null) {
                            this.m_zbuffer[n39] = z0;
                        }
                    }
                    n27 += 65536;
                    n28 += n3;
                    z0 += this.dz;
                    n5 += (int)this.dr;
                    n6 += (int)this.dg;
                    n7 += (int)this.db;
                    n8 += (int)this.da;
                }
            }
        }
    }
}