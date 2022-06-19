package processing.core;

public class PTriangle implements PConstants
{
    static final float PIXEL_CENTER = 0.5f;
    static final int R_GOURAUD = 1;
    static final int R_TEXTURE8 = 2;
    static final int R_TEXTURE24 = 4;
    static final int R_TEXTURE32 = 8;
    static final int R_ALPHA = 16;
    private int[] m_pixels;
    private int[] m_texture;
    private float[] m_zbuffer;
    private int SCREEN_WIDTH;
    private int SCREEN_HEIGHT;
    private int TEX_WIDTH;
    private int TEX_HEIGHT;
    private float F_TEX_WIDTH;
    private float F_TEX_HEIGHT;
    public boolean INTERPOLATE_UV;
    public boolean INTERPOLATE_RGB;
    public boolean INTERPOLATE_ALPHA;
    private static final int DEFAULT_INTERP_POWER = 3;
    private static int TEX_INTERP_POWER;
    private float[] x_array;
    private float[] y_array;
    private float[] z_array;
    private float[] camX;
    private float[] camY;
    private float[] camZ;
    private float[] u_array;
    private float[] v_array;
    private float[] r_array;
    private float[] g_array;
    private float[] b_array;
    private float[] a_array;
    private int o0;
    private int o1;
    private int o2;
    private float r0;
    private float r1;
    private float r2;
    private float g0;
    private float g1;
    private float g2;
    private float b0;
    private float b1;
    private float b2;
    private float a0;
    private float a1;
    private float a2;
    private float u0;
    private float u1;
    private float u2;
    private float v0;
    private float v1;
    private float v2;
    private float dx2;
    private float dy0;
    private float dy1;
    private float dy2;
    private float dz0;
    private float dz2;
    private float du0;
    private float du2;
    private float dv0;
    private float dv2;
    private float dr0;
    private float dr2;
    private float dg0;
    private float dg2;
    private float db0;
    private float db2;
    private float da0;
    private float da2;
    private float uleft;
    private float vleft;
    private float uleftadd;
    private float vleftadd;
    private float xleft;
    private float xrght;
    private float xadd1;
    private float xadd2;
    private float zleft;
    private float zleftadd;
    private float rleft;
    private float gleft;
    private float bleft;
    private float aleft;
    private float rleftadd;
    private float gleftadd;
    private float bleftadd;
    private float aleftadd;
    private float dta;
    private float temp;
    private float width;
    private int iuadd;
    private int ivadd;
    private int iradd;
    private int igadd;
    private int ibadd;
    private int iaadd;
    private float izadd;
    private int m_fill;
    public int m_drawFlags;
    private PGraphics3D parent;
    private boolean noDepthTest;
    private boolean m_culling;
    private boolean m_singleRight;
    private boolean m_bilinear;
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
    private float newax;
    private float newbx;
    private float newcx;
    private boolean firstSegment;
    
    public PTriangle(final PGraphics3D parent) {
        this.m_bilinear = true;
        this.x_array = new float[3];
        this.y_array = new float[3];
        this.z_array = new float[3];
        this.u_array = new float[3];
        this.v_array = new float[3];
        this.r_array = new float[3];
        this.g_array = new float[3];
        this.b_array = new float[3];
        this.a_array = new float[3];
        this.camX = new float[3];
        this.camY = new float[3];
        this.camZ = new float[3];
        this.parent = parent;
        this.reset();
    }
    
    public void reset() {
        this.SCREEN_WIDTH = this.parent.width;
        this.SCREEN_HEIGHT = this.parent.height;
        this.m_pixels = this.parent.pixels;
        this.m_zbuffer = this.parent.zbuffer;
        this.noDepthTest = this.parent.hints[4];
        this.INTERPOLATE_UV = false;
        this.INTERPOLATE_RGB = false;
        this.INTERPOLATE_ALPHA = false;
        this.m_texture = null;
        this.m_drawFlags = 0;
    }
    
    public void setCulling(final boolean culling) {
        this.m_culling = culling;
    }
    
    public void setVertices(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9) {
        this.x_array[0] = n;
        this.x_array[1] = n4;
        this.x_array[2] = n7;
        this.y_array[0] = n2;
        this.y_array[1] = n5;
        this.y_array[2] = n8;
        this.z_array[0] = n3;
        this.z_array[1] = n6;
        this.z_array[2] = n9;
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
    
    public void setUV(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.u_array[0] = (n * this.F_TEX_WIDTH + 0.5f) * 65536.0f;
        this.u_array[1] = (n3 * this.F_TEX_WIDTH + 0.5f) * 65536.0f;
        this.u_array[2] = (n5 * this.F_TEX_WIDTH + 0.5f) * 65536.0f;
        this.v_array[0] = (n2 * this.F_TEX_HEIGHT + 0.5f) * 65536.0f;
        this.v_array[1] = (n4 * this.F_TEX_HEIGHT + 0.5f) * 65536.0f;
        this.v_array[2] = (n6 * this.F_TEX_HEIGHT + 0.5f) * 65536.0f;
    }
    
    public void setIntensities(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        if (n4 != 1.0f || n8 != 1.0f || n12 != 1.0f) {
            this.INTERPOLATE_ALPHA = true;
            this.a_array[0] = (n4 * 253.0f + 1.0f) * 65536.0f;
            this.a_array[1] = (n8 * 253.0f + 1.0f) * 65536.0f;
            this.a_array[2] = (n12 * 253.0f + 1.0f) * 65536.0f;
            this.m_drawFlags |= 0x10;
        }
        else {
            this.INTERPOLATE_ALPHA = false;
            this.m_drawFlags &= 0xFFFFFFEF;
        }
        if (n != n5 || n5 != n9) {
            this.INTERPOLATE_RGB = true;
            this.m_drawFlags |= 0x1;
        }
        else if (n2 != n6 || n6 != n10) {
            this.INTERPOLATE_RGB = true;
            this.m_drawFlags |= 0x1;
        }
        else if (n3 != n7 || n7 != n11) {
            this.INTERPOLATE_RGB = true;
            this.m_drawFlags |= 0x1;
        }
        else {
            this.m_drawFlags &= 0xFFFFFFFE;
        }
        this.r_array[0] = (n * 253.0f + 1.0f) * 65536.0f;
        this.r_array[1] = (n5 * 253.0f + 1.0f) * 65536.0f;
        this.r_array[2] = (n9 * 253.0f + 1.0f) * 65536.0f;
        this.g_array[0] = (n2 * 253.0f + 1.0f) * 65536.0f;
        this.g_array[1] = (n6 * 253.0f + 1.0f) * 65536.0f;
        this.g_array[2] = (n10 * 253.0f + 1.0f) * 65536.0f;
        this.b_array[0] = (n3 * 253.0f + 1.0f) * 65536.0f;
        this.b_array[1] = (n7 * 253.0f + 1.0f) * 65536.0f;
        this.b_array[2] = (n11 * 253.0f + 1.0f) * 65536.0f;
        this.m_fill = (0xFF000000 | (int)(255.0f * n) << 16 | (int)(255.0f * n2) << 8 | (int)(255.0f * n3));
    }
    
    public void setTexture(final PImage pImage) {
        this.m_texture = pImage.pixels;
        this.TEX_WIDTH = pImage.width;
        this.TEX_HEIGHT = pImage.height;
        this.F_TEX_WIDTH = (float)(this.TEX_WIDTH - 1);
        this.F_TEX_HEIGHT = (float)(this.TEX_HEIGHT - 1);
        this.INTERPOLATE_UV = true;
        if (pImage.format == 2) {
            this.m_drawFlags |= 0x8;
        }
        else if (pImage.format == 1) {
            this.m_drawFlags |= 0x4;
        }
        else if (pImage.format == 4) {
            this.m_drawFlags |= 0x2;
        }
    }
    
    public void setUV(final float[] array, final float[] array2) {
        if (this.m_bilinear) {
            this.u_array[0] = array[0] * this.F_TEX_WIDTH * 65500.0f;
            this.u_array[1] = array[1] * this.F_TEX_WIDTH * 65500.0f;
            this.u_array[2] = array[2] * this.F_TEX_WIDTH * 65500.0f;
            this.v_array[0] = array2[0] * this.F_TEX_HEIGHT * 65500.0f;
            this.v_array[1] = array2[1] * this.F_TEX_HEIGHT * 65500.0f;
            this.v_array[2] = array2[2] * this.F_TEX_HEIGHT * 65500.0f;
        }
        else {
            this.u_array[0] = array[0] * this.TEX_WIDTH * 65500.0f;
            this.u_array[1] = array[1] * this.TEX_WIDTH * 65500.0f;
            this.u_array[2] = array[2] * this.TEX_WIDTH * 65500.0f;
            this.v_array[0] = array2[0] * this.TEX_HEIGHT * 65500.0f;
            this.v_array[1] = array2[1] * this.TEX_HEIGHT * 65500.0f;
            this.v_array[2] = array2[2] * this.TEX_HEIGHT * 65500.0f;
        }
    }
    
    public void render() {
        final float n = this.y_array[0];
        final float n2 = this.y_array[1];
        final float n3 = this.y_array[2];
        this.firstSegment = true;
        if (this.m_culling) {
            final float n4 = this.x_array[0];
            if ((this.x_array[2] - n4) * (n2 - n) < (this.x_array[1] - n4) * (n3 - n)) {
                return;
            }
        }
        if (n < n2) {
            if (n3 < n2) {
                if (n3 < n) {
                    this.o0 = 2;
                    this.o1 = 0;
                    this.o2 = 1;
                }
                else {
                    this.o0 = 0;
                    this.o1 = 2;
                    this.o2 = 1;
                }
            }
            else {
                this.o0 = 0;
                this.o1 = 1;
                this.o2 = 2;
            }
        }
        else if (n3 > n2) {
            if (n3 < n) {
                this.o0 = 1;
                this.o1 = 2;
                this.o2 = 0;
            }
            else {
                this.o0 = 1;
                this.o1 = 0;
                this.o2 = 2;
            }
        }
        else {
            this.o0 = 2;
            this.o1 = 1;
            this.o2 = 0;
        }
        final float n5 = this.y_array[this.o0];
        int n6 = (int)(n5 + 0.5f);
        if (n6 > this.SCREEN_HEIGHT) {
            return;
        }
        if (n6 < 0) {
            n6 = 0;
        }
        final float n7 = this.y_array[this.o2];
        int screen_HEIGHT = (int)(n7 + 0.5f);
        if (screen_HEIGHT < 0) {
            return;
        }
        if (screen_HEIGHT > this.SCREEN_HEIGHT) {
            screen_HEIGHT = this.SCREEN_HEIGHT;
        }
        if (screen_HEIGHT > n6) {
            final float n8 = this.x_array[this.o0];
            final float n9 = this.x_array[this.o1];
            final float n10 = this.x_array[this.o2];
            final float n11 = this.y_array[this.o1];
            int screen_HEIGHT2 = (int)(n11 + 0.5f);
            if (screen_HEIGHT2 < 0) {
                screen_HEIGHT2 = 0;
            }
            if (screen_HEIGHT2 > this.SCREEN_HEIGHT) {
                screen_HEIGHT2 = this.SCREEN_HEIGHT;
            }
            this.dx2 = n10 - n8;
            this.dy0 = n11 - n5;
            this.dy2 = n7 - n5;
            this.xadd2 = this.dx2 / this.dy2;
            this.temp = this.dy0 / this.dy2;
            this.width = this.temp * this.dx2 + n8 - n9;
            if (this.INTERPOLATE_ALPHA) {
                this.a0 = this.a_array[this.o0];
                this.a1 = this.a_array[this.o1];
                this.a2 = this.a_array[this.o2];
                this.da0 = this.a1 - this.a0;
                this.da2 = this.a2 - this.a0;
                this.iaadd = (int)((this.temp * this.da2 - this.da0) / this.width);
            }
            if (this.INTERPOLATE_RGB) {
                this.r0 = this.r_array[this.o0];
                this.r1 = this.r_array[this.o1];
                this.r2 = this.r_array[this.o2];
                this.g0 = this.g_array[this.o0];
                this.g1 = this.g_array[this.o1];
                this.g2 = this.g_array[this.o2];
                this.b0 = this.b_array[this.o0];
                this.b1 = this.b_array[this.o1];
                this.b2 = this.b_array[this.o2];
                this.dr0 = this.r1 - this.r0;
                this.dg0 = this.g1 - this.g0;
                this.db0 = this.b1 - this.b0;
                this.dr2 = this.r2 - this.r0;
                this.dg2 = this.g2 - this.g0;
                this.db2 = this.b2 - this.b0;
                this.iradd = (int)((this.temp * this.dr2 - this.dr0) / this.width);
                this.igadd = (int)((this.temp * this.dg2 - this.dg0) / this.width);
                this.ibadd = (int)((this.temp * this.db2 - this.db0) / this.width);
            }
            if (this.INTERPOLATE_UV) {
                this.u0 = this.u_array[this.o0];
                this.u1 = this.u_array[this.o1];
                this.u2 = this.u_array[this.o2];
                this.v0 = this.v_array[this.o0];
                this.v1 = this.v_array[this.o1];
                this.v2 = this.v_array[this.o2];
                this.du0 = this.u1 - this.u0;
                this.dv0 = this.v1 - this.v0;
                this.du2 = this.u2 - this.u0;
                this.dv2 = this.v2 - this.v0;
                this.iuadd = (int)((this.temp * this.du2 - this.du0) / this.width);
                this.ivadd = (int)((this.temp * this.dv2 - this.dv0) / this.width);
            }
            final float n12 = this.z_array[this.o0];
            final float n13 = this.z_array[this.o1];
            final float n14 = this.z_array[this.o2];
            this.dz0 = n13 - n12;
            this.dz2 = n14 - n12;
            this.izadd = (this.temp * this.dz2 - this.dz0) / this.width;
            if (screen_HEIGHT2 > n6) {
                this.dta = n6 + 0.5f - n5;
                this.xadd1 = (n9 - n8) / this.dy0;
                if (this.xadd2 > this.xadd1) {
                    this.xleft = n8 + this.dta * this.xadd1;
                    this.xrght = n8 + this.dta * this.xadd2;
                    this.zleftadd = this.dz0 / this.dy0;
                    this.zleft = this.dta * this.zleftadd + n12;
                    if (this.INTERPOLATE_UV) {
                        this.uleftadd = this.du0 / this.dy0;
                        this.vleftadd = this.dv0 / this.dy0;
                        this.uleft = this.dta * this.uleftadd + this.u0;
                        this.vleft = this.dta * this.vleftadd + this.v0;
                    }
                    if (this.INTERPOLATE_RGB) {
                        this.rleftadd = this.dr0 / this.dy0;
                        this.gleftadd = this.dg0 / this.dy0;
                        this.bleftadd = this.db0 / this.dy0;
                        this.rleft = this.dta * this.rleftadd + this.r0;
                        this.gleft = this.dta * this.gleftadd + this.g0;
                        this.bleft = this.dta * this.bleftadd + this.b0;
                    }
                    if (this.INTERPOLATE_ALPHA) {
                        this.aleftadd = this.da0 / this.dy0;
                        this.aleft = this.dta * this.aleftadd + this.a0;
                        if (this.m_drawFlags == 16) {
                            this.drawsegment_plain_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 17) {
                            this.drawsegment_gouraud_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 18) {
                            this.drawsegment_texture8_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 20) {
                            this.drawsegment_texture24_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 24) {
                            this.drawsegment_texture32_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 19) {
                            this.drawsegment_gouraud_texture8_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 21) {
                            this.drawsegment_gouraud_texture24_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 25) {
                            this.drawsegment_gouraud_texture32_alpha(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                        }
                    }
                    else if (this.m_drawFlags == 0) {
                        this.drawsegment_plain(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 1) {
                        this.drawsegment_gouraud(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 2) {
                        this.drawsegment_texture8(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 4) {
                        this.drawsegment_texture24(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 8) {
                        this.drawsegment_texture32(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 3) {
                        this.drawsegment_gouraud_texture8(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 5) {
                        this.drawsegment_gouraud_texture24(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 9) {
                        this.drawsegment_gouraud_texture32(this.xadd1, this.xadd2, n6, screen_HEIGHT2);
                    }
                    this.m_singleRight = true;
                }
                else {
                    this.xleft = n8 + this.dta * this.xadd2;
                    this.xrght = n8 + this.dta * this.xadd1;
                    this.zleftadd = this.dz2 / this.dy2;
                    this.zleft = this.dta * this.zleftadd + n12;
                    if (this.INTERPOLATE_UV) {
                        this.uleftadd = this.du2 / this.dy2;
                        this.vleftadd = this.dv2 / this.dy2;
                        this.uleft = this.dta * this.uleftadd + this.u0;
                        this.vleft = this.dta * this.vleftadd + this.v0;
                    }
                    if (this.INTERPOLATE_RGB) {
                        this.rleftadd = this.dr2 / this.dy2;
                        this.gleftadd = this.dg2 / this.dy2;
                        this.bleftadd = this.db2 / this.dy2;
                        this.rleft = this.dta * this.rleftadd + this.r0;
                        this.gleft = this.dta * this.gleftadd + this.g0;
                        this.bleft = this.dta * this.bleftadd + this.b0;
                    }
                    if (this.INTERPOLATE_ALPHA) {
                        this.aleftadd = this.da2 / this.dy2;
                        this.aleft = this.dta * this.aleftadd + this.a0;
                        if (this.m_drawFlags == 16) {
                            this.drawsegment_plain_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 17) {
                            this.drawsegment_gouraud_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 18) {
                            this.drawsegment_texture8_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 20) {
                            this.drawsegment_texture24_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 24) {
                            this.drawsegment_texture32_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 19) {
                            this.drawsegment_gouraud_texture8_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 21) {
                            this.drawsegment_gouraud_texture24_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                        else if (this.m_drawFlags == 25) {
                            this.drawsegment_gouraud_texture32_alpha(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                        }
                    }
                    else if (this.m_drawFlags == 0) {
                        this.drawsegment_plain(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 1) {
                        this.drawsegment_gouraud(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 2) {
                        this.drawsegment_texture8(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 4) {
                        this.drawsegment_texture24(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 8) {
                        this.drawsegment_texture32(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 3) {
                        this.drawsegment_gouraud_texture8(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 5) {
                        this.drawsegment_gouraud_texture24(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    else if (this.m_drawFlags == 9) {
                        this.drawsegment_gouraud_texture32(this.xadd2, this.xadd1, n6, screen_HEIGHT2);
                    }
                    this.m_singleRight = false;
                }
                if (screen_HEIGHT == screen_HEIGHT2) {
                    return;
                }
                this.dy1 = n7 - n11;
                this.xadd1 = (n10 - n9) / this.dy1;
            }
            else {
                this.dy1 = n7 - n11;
                this.xadd1 = (n10 - n9) / this.dy1;
                if (this.xadd2 < this.xadd1) {
                    this.xrght = (screen_HEIGHT2 + 0.5f - n5) * this.xadd2 + n8;
                    this.m_singleRight = true;
                }
                else {
                    this.dta = screen_HEIGHT2 + 0.5f - n5;
                    this.xleft = this.dta * this.xadd2 + n8;
                    this.zleftadd = this.dz2 / this.dy2;
                    this.zleft = this.dta * this.zleftadd + n12;
                    if (this.INTERPOLATE_UV) {
                        this.uleftadd = this.du2 / this.dy2;
                        this.vleftadd = this.dv2 / this.dy2;
                        this.uleft = this.dta * this.uleftadd + this.u0;
                        this.vleft = this.dta * this.vleftadd + this.v0;
                    }
                    if (this.INTERPOLATE_RGB) {
                        this.rleftadd = this.dr2 / this.dy2;
                        this.gleftadd = this.dg2 / this.dy2;
                        this.bleftadd = this.db2 / this.dy2;
                        this.rleft = this.dta * this.rleftadd + this.r0;
                        this.gleft = this.dta * this.gleftadd + this.g0;
                        this.bleft = this.dta * this.bleftadd + this.b0;
                    }
                    if (this.INTERPOLATE_ALPHA) {
                        this.aleftadd = this.da2 / this.dy2;
                        this.aleft = this.dta * this.aleftadd + this.a0;
                    }
                    this.m_singleRight = false;
                }
            }
            if (this.m_singleRight) {
                this.dta = screen_HEIGHT2 + 0.5f - n11;
                this.xleft = this.dta * this.xadd1 + n9;
                this.zleftadd = (n14 - n13) / this.dy1;
                this.zleft = this.dta * this.zleftadd + n13;
                if (this.INTERPOLATE_UV) {
                    this.uleftadd = (this.u2 - this.u1) / this.dy1;
                    this.vleftadd = (this.v2 - this.v1) / this.dy1;
                    this.uleft = this.dta * this.uleftadd + this.u1;
                    this.vleft = this.dta * this.vleftadd + this.v1;
                }
                if (this.INTERPOLATE_RGB) {
                    this.rleftadd = (this.r2 - this.r1) / this.dy1;
                    this.gleftadd = (this.g2 - this.g1) / this.dy1;
                    this.bleftadd = (this.b2 - this.b1) / this.dy1;
                    this.rleft = this.dta * this.rleftadd + this.r1;
                    this.gleft = this.dta * this.gleftadd + this.g1;
                    this.bleft = this.dta * this.bleftadd + this.b1;
                }
                if (this.INTERPOLATE_ALPHA) {
                    this.aleftadd = (this.a2 - this.a1) / this.dy1;
                    this.aleft = this.dta * this.aleftadd + this.a1;
                    if (this.m_drawFlags == 16) {
                        this.drawsegment_plain_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 17) {
                        this.drawsegment_gouraud_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 18) {
                        this.drawsegment_texture8_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 20) {
                        this.drawsegment_texture24_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 24) {
                        this.drawsegment_texture32_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 19) {
                        this.drawsegment_gouraud_texture8_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 21) {
                        this.drawsegment_gouraud_texture24_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 25) {
                        this.drawsegment_gouraud_texture32_alpha(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                    }
                }
                else if (this.m_drawFlags == 0) {
                    this.drawsegment_plain(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 1) {
                    this.drawsegment_gouraud(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 2) {
                    this.drawsegment_texture8(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 4) {
                    this.drawsegment_texture24(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 8) {
                    this.drawsegment_texture32(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 3) {
                    this.drawsegment_gouraud_texture8(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 5) {
                    this.drawsegment_gouraud_texture24(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 9) {
                    this.drawsegment_gouraud_texture32(this.xadd1, this.xadd2, screen_HEIGHT2, screen_HEIGHT);
                }
            }
            else {
                this.xrght = (screen_HEIGHT2 + 0.5f - n11) * this.xadd1 + n9;
                if (this.INTERPOLATE_ALPHA) {
                    if (this.m_drawFlags == 16) {
                        this.drawsegment_plain_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 17) {
                        this.drawsegment_gouraud_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 18) {
                        this.drawsegment_texture8_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 20) {
                        this.drawsegment_texture24_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 24) {
                        this.drawsegment_texture32_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 19) {
                        this.drawsegment_gouraud_texture8_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 21) {
                        this.drawsegment_gouraud_texture24_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                    else if (this.m_drawFlags == 25) {
                        this.drawsegment_gouraud_texture32_alpha(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                    }
                }
                else if (this.m_drawFlags == 0) {
                    this.drawsegment_plain(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 1) {
                    this.drawsegment_gouraud(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 2) {
                    this.drawsegment_texture8(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 4) {
                    this.drawsegment_texture24(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 8) {
                    this.drawsegment_texture32(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 3) {
                    this.drawsegment_gouraud_texture8(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 5) {
                    this.drawsegment_gouraud_texture24(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
                else if (this.m_drawFlags == 9) {
                    this.drawsegment_gouraud_texture32(this.xadd2, this.xadd1, screen_HEIGHT2, screen_HEIGHT);
                }
            }
        }
    }
    
    private boolean precomputeAccurateTexturing() {
        final float n = 65500.0f;
        final float n2 = 65500.0f;
        if (this.firstSegment) {
            final PMatrix3D pMatrix3D = new PMatrix3D(this.u_array[this.o0] / n, this.v_array[this.o0] / n2, 1.0f, 0.0f, this.u_array[this.o1] / n, this.v_array[this.o1] / n2, 1.0f, 0.0f, this.u_array[this.o2] / n, this.v_array[this.o2] / n2, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f);
            if (!pMatrix3D.invert()) {
                return false;
            }
            final float n3 = pMatrix3D.m00 * this.camX[this.o0] + pMatrix3D.m01 * this.camX[this.o1] + pMatrix3D.m02 * this.camX[this.o2];
            final float n4 = pMatrix3D.m10 * this.camX[this.o0] + pMatrix3D.m11 * this.camX[this.o1] + pMatrix3D.m12 * this.camX[this.o2];
            final float n5 = pMatrix3D.m20 * this.camX[this.o0] + pMatrix3D.m21 * this.camX[this.o1] + pMatrix3D.m22 * this.camX[this.o2];
            final float n6 = pMatrix3D.m00 * this.camY[this.o0] + pMatrix3D.m01 * this.camY[this.o1] + pMatrix3D.m02 * this.camY[this.o2];
            final float n7 = pMatrix3D.m10 * this.camY[this.o0] + pMatrix3D.m11 * this.camY[this.o1] + pMatrix3D.m12 * this.camY[this.o2];
            final float n8 = pMatrix3D.m20 * this.camY[this.o0] + pMatrix3D.m21 * this.camY[this.o1] + pMatrix3D.m22 * this.camY[this.o2];
            final float n9 = -(pMatrix3D.m00 * this.camZ[this.o0] + pMatrix3D.m01 * this.camZ[this.o1] + pMatrix3D.m02 * this.camZ[this.o2]);
            final float n10 = -(pMatrix3D.m10 * this.camZ[this.o0] + pMatrix3D.m11 * this.camZ[this.o1] + pMatrix3D.m12 * this.camZ[this.o2]);
            final float n11 = -(pMatrix3D.m20 * this.camZ[this.o0] + pMatrix3D.m21 * this.camZ[this.o1] + pMatrix3D.m22 * this.camZ[this.o2]);
            final float n12 = n5;
            final float n13 = n8;
            final float n14 = n11;
            final float n15 = n3 * this.TEX_WIDTH + n5;
            final float n16 = n6 * this.TEX_WIDTH + n8;
            final float n17 = n9 * this.TEX_WIDTH + n11;
            final float n18 = n4 * this.TEX_HEIGHT + n5;
            final float n19 = n7 * this.TEX_HEIGHT + n8;
            final float n20 = n10 * this.TEX_HEIGHT + n11;
            final float n21 = n15 - n5;
            final float n22 = n16 - n8;
            final float n23 = n17 - n11;
            final float n24 = n18 - n5;
            final float n25 = n19 - n8;
            final float n26 = n20 - n11;
            this.ax = (n13 * n26 - n14 * n25) * this.TEX_WIDTH;
            this.ay = (n14 * n24 - n12 * n26) * this.TEX_WIDTH;
            this.az = (n12 * n25 - n13 * n24) * this.TEX_WIDTH;
            this.bx = (n22 * n14 - n23 * n13) * this.TEX_HEIGHT;
            this.by = (n23 * n12 - n21 * n14) * this.TEX_HEIGHT;
            this.bz = (n21 * n13 - n22 * n12) * this.TEX_HEIGHT;
            this.cx = n25 * n23 - n26 * n22;
            this.cy = n26 * n21 - n24 * n23;
            this.cz = n24 * n22 - n25 * n21;
        }
        this.nearPlaneWidth = this.parent.rightScreen - this.parent.leftScreen;
        this.nearPlaneHeight = this.parent.topScreen - this.parent.bottomScreen;
        this.nearPlaneDepth = this.parent.nearPlane;
        this.xmult = this.nearPlaneWidth / this.SCREEN_WIDTH;
        this.ymult = this.nearPlaneHeight / this.SCREEN_HEIGHT;
        this.newax = this.ax * this.xmult;
        this.newbx = this.bx * this.xmult;
        this.newcx = this.cx * this.xmult;
        return true;
    }
    
    public static void setInterpPower(final int tex_INTERP_POWER) {
        PTriangle.TEX_INTERP_POWER = tex_INTERP_POWER;
    }
    
    private void drawsegment_plain(final float n, final float n2, int i, int n3) {
        int n4;
        int screen_WIDTH;
        float n5;
        int j;
        for (i *= this.SCREEN_WIDTH, n3 *= this.SCREEN_WIDTH; i < n3; i += this.SCREEN_WIDTH, this.xleft += n, this.xrght += n2, this.zleft += this.zleftadd) {
            n4 = (int)(this.xleft + 0.5f);
            if (n4 < 0) {
                n4 = 0;
            }
            screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            n5 = this.izadd * (n4 + 0.5f - this.xleft) + this.zleft;
            for (j = n4 + i; j < screen_WIDTH + i; ++j) {
                if (this.noDepthTest || n5 <= this.m_zbuffer[j]) {
                    this.m_zbuffer[j] = n5;
                    this.m_pixels[j] = this.m_fill;
                }
                n5 += this.izadd;
            }
        }
    }
    
    private void drawsegment_plain_alpha(final float n, final float n2, int i, int n3) {
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final int n4 = this.m_fill & 0xFF0000;
        final int n5 = this.m_fill & 0xFF00;
        final int n6 = this.m_fill & 0xFF;
        final float n7 = (float)this.iaadd;
        while (i < n3) {
            int n8 = (int)(this.xleft + 0.5f);
            if (n8 < 0) {
                n8 = 0;
            }
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n9 = n8 + 0.5f - this.xleft;
            float n10 = this.izadd * n9 + this.zleft;
            int n11 = (int)(n7 * n9 + this.aleft);
            for (int j = n8 + i; j < screen_WIDTH + i; ++j) {
                if (this.noDepthTest || n10 <= this.m_zbuffer[j]) {
                    final int n12 = n11 >> 16;
                    final int n13 = this.m_pixels[j];
                    final int n14 = n13 & 0xFF00;
                    final int n15 = n13 & 0xFF;
                    final int n16 = n13 & 0xFF0000;
                    this.m_pixels[j] = (0xFF000000 | (n16 + ((n4 - n16) * n12 >> 8) & 0xFF0000) | (n14 + ((n5 - n14) * n12 >> 8) & 0xFF00) | (n15 + ((n6 - n15) * n12 >> 8) & 0xFF));
                }
                n10 += this.izadd;
                n11 += this.iaadd;
            }
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_gouraud(final float n, final float n2, int i, int n3) {
        final float n4 = (float)this.iradd;
        final float n5 = (float)this.igadd;
        final float n6 = (float)this.ibadd;
        int n7;
        int screen_WIDTH;
        float n8;
        int n9;
        int n10;
        int n11;
        float n12;
        int j;
        for (i *= this.SCREEN_WIDTH, n3 *= this.SCREEN_WIDTH; i < n3; i += this.SCREEN_WIDTH, this.xleft += n, this.xrght += n2, this.rleft += this.rleftadd, this.gleft += this.gleftadd, this.bleft += this.bleftadd, this.zleft += this.zleftadd) {
            n7 = (int)(this.xleft + 0.5f);
            if (n7 < 0) {
                n7 = 0;
            }
            screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            n8 = n7 + 0.5f - this.xleft;
            n9 = (int)(n4 * n8 + this.rleft);
            n10 = (int)(n5 * n8 + this.gleft);
            n11 = (int)(n6 * n8 + this.bleft);
            n12 = this.izadd * n8 + this.zleft;
            for (j = n7 + i; j < screen_WIDTH + i; ++j) {
                if (this.noDepthTest || n12 <= this.m_zbuffer[j]) {
                    this.m_zbuffer[j] = n12;
                    this.m_pixels[j] = (0xFF000000 | ((n9 & 0xFF0000) | (n10 >> 8 & 0xFF00) | n11 >> 16));
                }
                n9 += this.iradd;
                n10 += this.igadd;
                n11 += this.ibadd;
                n12 += this.izadd;
            }
        }
    }
    
    private void drawsegment_gouraud_alpha(final float n, final float n2, int i, int n3) {
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n4 = (float)this.iradd;
        final float n5 = (float)this.igadd;
        final float n6 = (float)this.ibadd;
        final float n7 = (float)this.iaadd;
        while (i < n3) {
            int n8 = (int)(this.xleft + 0.5f);
            if (n8 < 0) {
                n8 = 0;
            }
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n9 = n8 + 0.5f - this.xleft;
            int n10 = (int)(n4 * n9 + this.rleft);
            int n11 = (int)(n5 * n9 + this.gleft);
            int n12 = (int)(n6 * n9 + this.bleft);
            int n13 = (int)(n7 * n9 + this.aleft);
            float n14 = this.izadd * n9 + this.zleft;
            for (int j = n8 + i; j < screen_WIDTH + i; ++j) {
                if (this.noDepthTest || n14 <= this.m_zbuffer[j]) {
                    final int n15 = n10 & 0xFF0000;
                    final int n16 = n11 >> 8 & 0xFF00;
                    final int n17 = n12 >> 16;
                    final int n18 = this.m_pixels[j];
                    final int n19 = n18 & 0xFF0000;
                    final int n20 = n18 & 0xFF00;
                    final int n21 = n18 & 0xFF;
                    final int n22 = n13 >> 16;
                    this.m_pixels[j] = (0xFF000000 | (n19 + ((n15 - n19) * n22 >> 8) & 0xFF0000) | (n20 + ((n16 - n20) * n22 >> 8) & 0xFF00) | (n21 + ((n17 - n21) * n22 >> 8) & 0xFF));
                }
                n10 += this.iradd;
                n11 += this.igadd;
                n12 += this.ibadd;
                n13 += this.iaadd;
                n14 += this.izadd;
            }
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.aleft += this.aleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_texture8(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final int n13 = this.m_fill & 0xFF0000;
        final int n14 = this.m_fill & 0xFF00;
        final int n15 = this.m_fill & 0xFF;
        while (i < n3) {
            int n16 = (int)(this.xleft + 0.5f);
            if (n16 < 0) {
                n16 = 0;
            }
            int n17 = n16;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n18 = n16 + 0.5f - this.xleft;
            int n19 = (int)(n11 * n18 + this.uleft);
            int n20 = (int)(n12 * n18 + this.vleft);
            float n21 = this.izadd * n18 + this.zleft;
            int j = n16 + i;
            final int n22 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n23 = this.xmult * (n17 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n24 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n23 * this.ax + n24 * this.ay + nearPlaneDepth * this.az;
                n8 = n23 * this.bx + n24 * this.by + nearPlaneDepth * this.bz;
                n9 = n23 * this.cx + n24 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n25 = 0;
            int n26 = 0;
            int n27 = 0;
            float n38;
            float n39;
            if (n6 != 0 && b) {
                final int n28 = (n22 - j - 1) % n10;
                final int n29 = n10 - n28;
                final float n30 = n28 / (float)n10;
                final float n31 = n29 / (float)n10;
                n25 = n29;
                final float n32 = n7 - n31 * this.newax;
                final float n33 = n8 - n31 * this.newbx;
                final float n34 = 65536.0f / (n9 - n31 * this.newcx);
                final float n35 = n32 * n34;
                final float n36 = n33 * n34;
                n7 += n30 * this.newax;
                n8 += n30 * this.newbx;
                n9 += n30 * this.newcx;
                final float n37 = 65536.0f / n9;
                n38 = n7 * n37;
                n39 = n8 * n37;
                n26 = (int)(n38 - n35) >> tex_INTERP_POWER;
                n27 = (int)(n39 - n36) >> tex_INTERP_POWER;
                n19 = (int)n35 + (n29 - 1) * n26;
                n20 = (int)n36 + (n29 - 1) * n27;
            }
            else {
                final float n40 = 65536.0f / n9;
                n38 = n7 * n40;
                n39 = n8 * n40;
            }
            while (j < n22) {
                if (n6 != 0) {
                    if (n25 == n10) {
                        n25 = 0;
                    }
                    if (n25 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n41 = 65536.0f / n9;
                        final float n42 = n38;
                        final float n43 = n39;
                        n38 = n7 * n41;
                        n39 = n8 * n41;
                        n19 = (int)n42;
                        n20 = (int)n43;
                        n26 = (int)(n38 - n42) >> tex_INTERP_POWER;
                        n27 = (int)(n39 - n43) >> tex_INTERP_POWER;
                    }
                    else {
                        n19 += n26;
                        n20 += n27;
                    }
                    ++n25;
                }
                try {
                    if (this.noDepthTest || n21 <= this.m_zbuffer[j]) {
                        int n51;
                        if (this.m_bilinear) {
                            int n44 = (n20 >> 16) * this.TEX_WIDTH + (n19 >> 16);
                            final int n45 = n19 & 0xFFFF;
                            final int n46 = this.m_texture[n44] & 0xFF;
                            final int n47 = this.m_texture[n44 + 1] & 0xFF;
                            if (n44 < n5) {
                                n44 += this.TEX_WIDTH;
                            }
                            final int n48 = this.m_texture[n44] & 0xFF;
                            final int n49 = this.m_texture[n44 + 1] & 0xFF;
                            final int n50 = n46 + ((n47 - n46) * n45 >> 16);
                            n51 = n50 + ((n48 + ((n49 - n48) * n45 >> 16) - n50) * (n20 & 0xFFFF) >> 16);
                        }
                        else {
                            n51 = (this.m_texture[(n20 >> 16) * this.TEX_WIDTH + (n19 >> 16)] & 0xFF);
                        }
                        final int n52 = this.m_pixels[j];
                        final int n53 = n52 & 0xFF00;
                        final int n54 = n52 & 0xFF;
                        final int n55 = n52 & 0xFF0000;
                        this.m_pixels[j] = (0xFF000000 | (n55 + ((n13 - n55) * n51 >> 8) & 0xFF0000) | (n53 + ((n14 - n53) * n51 >> 8) & 0xFF00) | (n54 + ((n15 - n54) * n51 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n17;
                if (n6 == 0) {
                    n19 += this.iuadd;
                    n20 += this.ivadd;
                }
                n21 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_texture8_alpha(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iaadd;
        final int n14 = this.m_fill & 0xFF0000;
        final int n15 = this.m_fill & 0xFF00;
        final int n16 = this.m_fill & 0xFF;
        while (i < n3) {
            int n17 = (int)(this.xleft + 0.5f);
            if (n17 < 0) {
                n17 = 0;
            }
            int n18 = n17;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n19 = n17 + 0.5f - this.xleft;
            int n20 = (int)(n11 * n19 + this.uleft);
            int n21 = (int)(n12 * n19 + this.vleft);
            int n22 = (int)(n13 * n19 + this.aleft);
            float n23 = this.izadd * n19 + this.zleft;
            int j = n17 + i;
            final int n24 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n25 = this.xmult * (n18 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n26 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n25 * this.ax + n26 * this.ay + nearPlaneDepth * this.az;
                n8 = n25 * this.bx + n26 * this.by + nearPlaneDepth * this.bz;
                n9 = n25 * this.cx + n26 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n27 = 0;
            int n28 = 0;
            int n29 = 0;
            float n40;
            float n41;
            if (n6 != 0 && b) {
                final int n30 = (n24 - j - 1) % n10;
                final int n31 = n10 - n30;
                final float n32 = n30 / (float)n10;
                final float n33 = n31 / (float)n10;
                n27 = n31;
                final float n34 = n7 - n33 * this.newax;
                final float n35 = n8 - n33 * this.newbx;
                final float n36 = 65536.0f / (n9 - n33 * this.newcx);
                final float n37 = n34 * n36;
                final float n38 = n35 * n36;
                n7 += n32 * this.newax;
                n8 += n32 * this.newbx;
                n9 += n32 * this.newcx;
                final float n39 = 65536.0f / n9;
                n40 = n7 * n39;
                n41 = n8 * n39;
                n28 = (int)(n40 - n37) >> tex_INTERP_POWER;
                n29 = (int)(n41 - n38) >> tex_INTERP_POWER;
                n20 = (int)n37 + (n31 - 1) * n28;
                n21 = (int)n38 + (n31 - 1) * n29;
            }
            else {
                final float n42 = 65536.0f / n9;
                n40 = n7 * n42;
                n41 = n8 * n42;
            }
            while (j < n24) {
                if (n6 != 0) {
                    if (n27 == n10) {
                        n27 = 0;
                    }
                    if (n27 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n43 = 65536.0f / n9;
                        final float n44 = n40;
                        final float n45 = n41;
                        n40 = n7 * n43;
                        n41 = n8 * n43;
                        n20 = (int)n44;
                        n21 = (int)n45;
                        n28 = (int)(n40 - n44) >> tex_INTERP_POWER;
                        n29 = (int)(n41 - n45) >> tex_INTERP_POWER;
                    }
                    else {
                        n20 += n28;
                        n21 += n29;
                    }
                    ++n27;
                }
                try {
                    if (this.noDepthTest || n23 <= this.m_zbuffer[j]) {
                        int n53;
                        if (this.m_bilinear) {
                            int n46 = (n21 >> 16) * this.TEX_WIDTH + (n20 >> 16);
                            final int n47 = n20 & 0xFFFF;
                            final int n48 = this.m_texture[n46] & 0xFF;
                            final int n49 = this.m_texture[n46 + 1] & 0xFF;
                            if (n46 < n5) {
                                n46 += this.TEX_WIDTH;
                            }
                            final int n50 = this.m_texture[n46] & 0xFF;
                            final int n51 = this.m_texture[n46 + 1] & 0xFF;
                            final int n52 = n48 + ((n49 - n48) * n47 >> 16);
                            n53 = n52 + ((n50 + ((n51 - n50) * n47 >> 16) - n52) * (n21 & 0xFFFF) >> 16);
                        }
                        else {
                            n53 = (this.m_texture[(n21 >> 16) * this.TEX_WIDTH + (n20 >> 16)] & 0xFF);
                        }
                        final int n54 = n53 * (n22 >> 16) >> 8;
                        final int n55 = this.m_pixels[j];
                        final int n56 = n55 & 0xFF00;
                        final int n57 = n55 & 0xFF;
                        final int n58 = n55 & 0xFF0000;
                        this.m_pixels[j] = (0xFF000000 | (n58 + ((n14 - n58) * n54 >> 8) & 0xFF0000) | (n56 + ((n15 - n56) * n54 >> 8) & 0xFF00) | (n57 + ((n16 - n57) * n54 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n18;
                if (n6 == 0) {
                    n20 += this.iuadd;
                    n21 += this.ivadd;
                }
                n23 += this.izadd;
                n22 += this.iaadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.zleft += this.zleftadd;
            this.aleft += this.aleftadd;
        }
    }
    
    private void drawsegment_texture24(final float n, final float n2, int i, int n3) {
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n4 = (float)this.iuadd;
        final float n5 = (float)this.ivadd;
        final boolean b = (this.m_fill & 0xFFFFFF) != 0xFFFFFF;
        final int n6 = this.m_fill >> 16 & 0xFF;
        final int n7 = this.m_fill >> 8 & 0xFF;
        final int n8 = this.m_fill & 0xFF;
        int n9 = i / this.SCREEN_WIDTH;
        final int n10 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n11 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n12 = 0.0f;
        float n13 = 0.0f;
        float n14 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n15 = 1 << tex_INTERP_POWER;
        if (n11 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n15;
                this.newbx *= n15;
                this.newcx *= n15;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n11 = 0;
            }
        }
        while (i < n3) {
            int n16 = (int)(this.xleft + 0.5f);
            if (n16 < 0) {
                n16 = 0;
            }
            int n17 = n16;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n18 = n16 + 0.5f - this.xleft;
            int n19 = (int)(n4 * n18 + this.uleft);
            int n20 = (int)(n5 * n18 + this.vleft);
            float n21 = this.izadd * n18 + this.zleft;
            int j = n16 + i;
            final int n22 = screen_WIDTH + i;
            if (n11 != 0) {
                final float n23 = this.xmult * (n17 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n24 = this.ymult * (n9 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n12 = n23 * this.ax + n24 * this.ay + nearPlaneDepth * this.az;
                n13 = n23 * this.bx + n24 * this.by + nearPlaneDepth * this.bz;
                n14 = n23 * this.cx + n24 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b2 = this.newcx > 0.0f != n14 > 0.0f;
            int n25 = 0;
            int n26 = 0;
            int n27 = 0;
            float n38;
            float n39;
            if (n11 != 0 && b2) {
                final int n28 = (n22 - j - 1) % n15;
                final int n29 = n15 - n28;
                final float n30 = n28 / (float)n15;
                final float n31 = n29 / (float)n15;
                n25 = n29;
                final float n32 = n12 - n31 * this.newax;
                final float n33 = n13 - n31 * this.newbx;
                final float n34 = 65536.0f / (n14 - n31 * this.newcx);
                final float n35 = n32 * n34;
                final float n36 = n33 * n34;
                n12 += n30 * this.newax;
                n13 += n30 * this.newbx;
                n14 += n30 * this.newcx;
                final float n37 = 65536.0f / n14;
                n38 = n12 * n37;
                n39 = n13 * n37;
                n26 = (int)(n38 - n35) >> tex_INTERP_POWER;
                n27 = (int)(n39 - n36) >> tex_INTERP_POWER;
                n19 = (int)n35 + (n29 - 1) * n26;
                n20 = (int)n36 + (n29 - 1) * n27;
            }
            else {
                final float n40 = 65536.0f / n14;
                n38 = n12 * n40;
                n39 = n13 * n40;
            }
            while (j < n22) {
                if (n11 != 0) {
                    if (n25 == n15) {
                        n25 = 0;
                    }
                    if (n25 == 0) {
                        n12 += this.newax;
                        n13 += this.newbx;
                        n14 += this.newcx;
                        final float n41 = 65536.0f / n14;
                        final float n42 = n38;
                        final float n43 = n39;
                        n38 = n12 * n41;
                        n39 = n13 * n41;
                        n19 = (int)n42;
                        n20 = (int)n43;
                        n26 = (int)(n38 - n42) >> tex_INTERP_POWER;
                        n27 = (int)(n39 - n43) >> tex_INTERP_POWER;
                    }
                    else {
                        n19 += n26;
                        n20 += n27;
                    }
                    ++n25;
                }
                try {
                    if (this.noDepthTest || n21 <= this.m_zbuffer[j]) {
                        this.m_zbuffer[j] = n21;
                        if (this.m_bilinear) {
                            int n44 = (n20 >> 16) * this.TEX_WIDTH + (n19 >> 16);
                            final int n45 = (n19 & 0xFFFF) >> 9;
                            final int n46 = (n20 & 0xFFFF) >> 9;
                            final int n47 = this.m_texture[n44];
                            final int n48 = this.m_texture[n44 + 1];
                            if (n44 < n10) {
                                n44 += this.TEX_WIDTH;
                            }
                            final int n49 = this.m_texture[n44];
                            final int n50 = this.m_texture[n44 + 1];
                            final int n51 = n47 & 0xFF0000;
                            final int n52 = n49 & 0xFF0000;
                            final int n53 = n51 + (((n48 & 0xFF0000) - n51) * n45 >> 7);
                            int n54 = n53 + ((n52 + (((n50 & 0xFF0000) - n52) * n45 >> 7) - n53) * n46 >> 7);
                            if (b) {
                                n54 = (n54 * n6 >> 8 & 0xFF0000);
                            }
                            final int n55 = n47 & 0xFF00;
                            final int n56 = n49 & 0xFF00;
                            final int n57 = n55 + (((n48 & 0xFF00) - n55) * n45 >> 7);
                            int n58 = n57 + ((n56 + (((n50 & 0xFF00) - n56) * n45 >> 7) - n57) * n46 >> 7);
                            if (b) {
                                n58 = (n58 * n7 >> 8 & 0xFF00);
                            }
                            final int n59 = n47 & 0xFF;
                            final int n60 = n49 & 0xFF;
                            final int n61 = n59 + (((n48 & 0xFF) - n59) * n45 >> 7);
                            int n62 = n61 + ((n60 + (((n50 & 0xFF) - n60) * n45 >> 7) - n61) * n46 >> 7);
                            if (b) {
                                n62 = (n62 * n8 >> 8 & 0xFF);
                            }
                            this.m_pixels[j] = (0xFF000000 | (n54 & 0xFF0000) | (n58 & 0xFF00) | (n62 & 0xFF));
                        }
                        else {
                            this.m_pixels[j] = this.m_texture[(n20 >> 16) * this.TEX_WIDTH + (n19 >> 16)];
                        }
                    }
                }
                catch (Exception ex) {}
                n21 += this.izadd;
                ++n17;
                if (n11 == 0) {
                    n19 += this.iuadd;
                    n20 += this.ivadd;
                }
                ++j;
            }
            ++n9;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.zleft += this.zleftadd;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
        }
    }
    
    private void drawsegment_texture24_alpha(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        final boolean b = (this.m_fill & 0xFFFFFF) != 0xFFFFFF;
        final int n11 = this.m_fill >> 16 & 0xFF;
        final int n12 = this.m_fill >> 8 & 0xFF;
        final int n13 = this.m_fill & 0xFF;
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n14 = (float)this.iuadd;
        final float n15 = (float)this.ivadd;
        final float n16 = (float)this.iaadd;
        while (i < n3) {
            int n17 = (int)(this.xleft + 0.5f);
            if (n17 < 0) {
                n17 = 0;
            }
            int n18 = n17;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n19 = n17 + 0.5f - this.xleft;
            int n20 = (int)(n14 * n19 + this.uleft);
            int n21 = (int)(n15 * n19 + this.vleft);
            int n22 = (int)(n16 * n19 + this.aleft);
            float n23 = this.izadd * n19 + this.zleft;
            int j = n17 + i;
            final int n24 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n25 = this.xmult * (n18 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n26 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n25 * this.ax + n26 * this.ay + nearPlaneDepth * this.az;
                n8 = n25 * this.bx + n26 * this.by + nearPlaneDepth * this.bz;
                n9 = n25 * this.cx + n26 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b2 = this.newcx > 0.0f != n9 > 0.0f;
            int n27 = 0;
            int n28 = 0;
            int n29 = 0;
            float n40;
            float n41;
            if (n6 != 0 && b2) {
                final int n30 = (n24 - j - 1) % n10;
                final int n31 = n10 - n30;
                final float n32 = n30 / (float)n10;
                final float n33 = n31 / (float)n10;
                n27 = n31;
                final float n34 = n7 - n33 * this.newax;
                final float n35 = n8 - n33 * this.newbx;
                final float n36 = 65536.0f / (n9 - n33 * this.newcx);
                final float n37 = n34 * n36;
                final float n38 = n35 * n36;
                n7 += n32 * this.newax;
                n8 += n32 * this.newbx;
                n9 += n32 * this.newcx;
                final float n39 = 65536.0f / n9;
                n40 = n7 * n39;
                n41 = n8 * n39;
                n28 = (int)(n40 - n37) >> tex_INTERP_POWER;
                n29 = (int)(n41 - n38) >> tex_INTERP_POWER;
                n20 = (int)n37 + (n31 - 1) * n28;
                n21 = (int)n38 + (n31 - 1) * n29;
            }
            else {
                final float n42 = 65536.0f / n9;
                n40 = n7 * n42;
                n41 = n8 * n42;
            }
            while (j < n24) {
                if (n6 != 0) {
                    if (n27 == n10) {
                        n27 = 0;
                    }
                    if (n27 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n43 = 65536.0f / n9;
                        final float n44 = n40;
                        final float n45 = n41;
                        n40 = n7 * n43;
                        n41 = n8 * n43;
                        n20 = (int)n44;
                        n21 = (int)n45;
                        n28 = (int)(n40 - n44) >> tex_INTERP_POWER;
                        n29 = (int)(n41 - n45) >> tex_INTERP_POWER;
                    }
                    else {
                        n20 += n28;
                        n21 += n29;
                    }
                    ++n27;
                }
                try {
                    if (this.noDepthTest || n23 <= this.m_zbuffer[j]) {
                        final int n46 = n22 >> 16;
                        if (this.m_bilinear) {
                            int n47 = (n21 >> 16) * this.TEX_WIDTH + (n20 >> 16);
                            final int n48 = (n20 & 0xFFFF) >> 9;
                            final int n49 = (n21 & 0xFFFF) >> 9;
                            final int n50 = this.m_texture[n47];
                            final int n51 = this.m_texture[n47 + 1];
                            if (n47 < n5) {
                                n47 += this.TEX_WIDTH;
                            }
                            final int n52 = this.m_texture[n47];
                            final int n53 = this.m_texture[n47 + 1];
                            final int n54 = n50 & 0xFF0000;
                            final int n55 = n52 & 0xFF0000;
                            final int n56 = n54 + (((n51 & 0xFF0000) - n54) * n48 >> 7);
                            int n57 = n56 + ((n55 + (((n53 & 0xFF0000) - n55) * n48 >> 7) - n56) * n49 >> 7);
                            if (b) {
                                n57 = (n57 * n11 >> 8 & 0xFF0000);
                            }
                            final int n58 = n50 & 0xFF00;
                            final int n59 = n52 & 0xFF00;
                            final int n60 = n58 + (((n51 & 0xFF00) - n58) * n48 >> 7);
                            int n61 = n60 + ((n59 + (((n53 & 0xFF00) - n59) * n48 >> 7) - n60) * n49 >> 7);
                            if (b) {
                                n61 = (n61 * n12 >> 8 & 0xFF00);
                            }
                            final int n62 = n50 & 0xFF;
                            final int n63 = n52 & 0xFF;
                            final int n64 = n62 + (((n51 & 0xFF) - n62) * n48 >> 7);
                            int n65 = n64 + ((n63 + (((n53 & 0xFF) - n63) * n48 >> 7) - n64) * n49 >> 7);
                            if (b) {
                                n65 = (n65 * n13 >> 8 & 0xFF);
                            }
                            final int n66 = this.m_pixels[j];
                            final int n67 = n66 & 0xFF0000;
                            final int n68 = n66 & 0xFF00;
                            final int n69 = n66 & 0xFF;
                            this.m_pixels[j] = (0xFF000000 | (n67 + ((n57 - n67) * n46 >> 8) & 0xFF0000) | (n68 + ((n61 - n68) * n46 >> 8) & 0xFF00) | (n69 + ((n65 - n69) * n46 >> 8) & 0xFF));
                        }
                        else {
                            final int n70 = this.m_texture[(n21 >> 16) * this.TEX_WIDTH + (n20 >> 16)];
                            final int n71 = n70 & 0xFF00;
                            final int n72 = n70 & 0xFF;
                            final int n73 = n70 & 0xFF0000;
                            final int n74 = this.m_pixels[j];
                            final int n75 = n74 & 0xFF0000;
                            final int n76 = n74 & 0xFF00;
                            final int n77 = n74 & 0xFF;
                            this.m_pixels[j] = (0xFF000000 | (n75 + ((n73 - n75) * n46 >> 8) & 0xFF0000) | (n76 + ((n71 - n76) * n46 >> 8) & 0xFF00) | (n77 + ((n72 - n77) * n46 >> 8) & 0xFF));
                        }
                    }
                }
                catch (Exception ex) {}
                ++n18;
                if (n6 == 0) {
                    n20 += this.iuadd;
                    n21 += this.ivadd;
                }
                n22 += this.iaadd;
                n23 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.zleft += this.zleftadd;
            this.aleft += this.aleftadd;
        }
    }
    
    private void drawsegment_texture32(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final boolean b = this.m_fill != -1;
        final int n11 = this.m_fill >> 16 & 0xFF;
        final int n12 = this.m_fill >> 8 & 0xFF;
        final int n13 = this.m_fill & 0xFF;
        final float n14 = (float)this.iuadd;
        final float n15 = (float)this.ivadd;
        while (i < n3) {
            int n16 = (int)(this.xleft + 0.5f);
            if (n16 < 0) {
                n16 = 0;
            }
            int n17 = n16;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n18 = n16 + 0.5f - this.xleft;
            int n19 = (int)(n14 * n18 + this.uleft);
            int n20 = (int)(n15 * n18 + this.vleft);
            float n21 = this.izadd * n18 + this.zleft;
            int j = n16 + i;
            final int n22 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n23 = this.xmult * (n17 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n24 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n23 * this.ax + n24 * this.ay + nearPlaneDepth * this.az;
                n8 = n23 * this.bx + n24 * this.by + nearPlaneDepth * this.bz;
                n9 = n23 * this.cx + n24 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b2 = this.newcx > 0.0f != n9 > 0.0f;
            int n25 = 0;
            int n26 = 0;
            int n27 = 0;
            float n38;
            float n39;
            if (n6 != 0 && b2) {
                final int n28 = (n22 - j - 1) % n10;
                final int n29 = n10 - n28;
                final float n30 = n28 / (float)n10;
                final float n31 = n29 / (float)n10;
                n25 = n29;
                final float n32 = n7 - n31 * this.newax;
                final float n33 = n8 - n31 * this.newbx;
                final float n34 = 65536.0f / (n9 - n31 * this.newcx);
                final float n35 = n32 * n34;
                final float n36 = n33 * n34;
                n7 += n30 * this.newax;
                n8 += n30 * this.newbx;
                n9 += n30 * this.newcx;
                final float n37 = 65536.0f / n9;
                n38 = n7 * n37;
                n39 = n8 * n37;
                n26 = (int)(n38 - n35) >> tex_INTERP_POWER;
                n27 = (int)(n39 - n36) >> tex_INTERP_POWER;
                n19 = (int)n35 + (n29 - 1) * n26;
                n20 = (int)n36 + (n29 - 1) * n27;
            }
            else {
                final float n40 = 65536.0f / n9;
                n38 = n7 * n40;
                n39 = n8 * n40;
            }
            while (j < n22) {
                if (n6 != 0) {
                    if (n25 == n10) {
                        n25 = 0;
                    }
                    if (n25 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n41 = 65536.0f / n9;
                        final float n42 = n38;
                        final float n43 = n39;
                        n38 = n7 * n41;
                        n39 = n8 * n41;
                        n19 = (int)n42;
                        n20 = (int)n43;
                        n26 = (int)(n38 - n42) >> tex_INTERP_POWER;
                        n27 = (int)(n39 - n43) >> tex_INTERP_POWER;
                    }
                    else {
                        n19 += n26;
                        n20 += n27;
                    }
                    ++n25;
                }
                try {
                    if (this.noDepthTest || n21 <= this.m_zbuffer[j]) {
                        if (this.m_bilinear) {
                            int n44 = (n20 >> 16) * this.TEX_WIDTH + (n19 >> 16);
                            final int n45 = (n19 & 0xFFFF) >> 9;
                            final int n46 = (n20 & 0xFFFF) >> 9;
                            final int n47 = this.m_texture[n44];
                            final int n48 = this.m_texture[n44 + 1];
                            if (n44 < n5) {
                                n44 += this.TEX_WIDTH;
                            }
                            final int n49 = this.m_texture[n44];
                            final int n50 = this.m_texture[n44 + 1];
                            final int n51 = n47 & 0xFF0000;
                            final int n52 = n49 & 0xFF0000;
                            final int n53 = n51 + (((n48 & 0xFF0000) - n51) * n45 >> 7);
                            int n54 = n53 + ((n52 + (((n50 & 0xFF0000) - n52) * n45 >> 7) - n53) * n46 >> 7);
                            if (b) {
                                n54 = (n54 * n11 >> 8 & 0xFF0000);
                            }
                            final int n55 = n47 & 0xFF00;
                            final int n56 = n49 & 0xFF00;
                            final int n57 = n55 + (((n48 & 0xFF00) - n55) * n45 >> 7);
                            int n58 = n57 + ((n56 + (((n50 & 0xFF00) - n56) * n45 >> 7) - n57) * n46 >> 7);
                            if (b) {
                                n58 = (n58 * n12 >> 8 & 0xFF00);
                            }
                            final int n59 = n47 & 0xFF;
                            final int n60 = n49 & 0xFF;
                            final int n61 = n59 + (((n48 & 0xFF) - n59) * n45 >> 7);
                            int n62 = n61 + ((n60 + (((n50 & 0xFF) - n60) * n45 >> 7) - n61) * n46 >> 7);
                            if (b) {
                                n62 = (n62 * n13 >> 8 & 0xFF);
                            }
                            final int n63 = n47 >>> 24;
                            final int n64 = n49 >>> 24;
                            final int n65 = n63 + (((n48 >>> 24) - n63) * n45 >> 7);
                            final int n66 = n65 + ((n64 + (((n50 >>> 24) - n64) * n45 >> 7) - n65) * n46 >> 7);
                            final int n67 = this.m_pixels[j];
                            final int n68 = n67 & 0xFF0000;
                            final int n69 = n67 & 0xFF00;
                            final int n70 = n67 & 0xFF;
                            this.m_pixels[j] = (0xFF000000 | (n68 + ((n54 - n68) * n66 >> 8) & 0xFF0000) | (n69 + ((n58 - n69) * n66 >> 8) & 0xFF00) | (n70 + ((n62 - n70) * n66 >> 8) & 0xFF));
                        }
                        else {
                            final int n71 = this.m_texture[(n20 >> 16) * this.TEX_WIDTH + (n19 >> 16)];
                            final int n72 = n71 >>> 24;
                            final int n73 = n71 & 0xFF00;
                            final int n74 = n71 & 0xFF;
                            final int n75 = n71 & 0xFF0000;
                            final int n76 = this.m_pixels[j];
                            final int n77 = n76 & 0xFF0000;
                            final int n78 = n76 & 0xFF00;
                            final int n79 = n76 & 0xFF;
                            this.m_pixels[j] = (0xFF000000 | (n77 + ((n75 - n77) * n72 >> 8) & 0xFF0000) | (n78 + ((n73 - n78) * n72 >> 8) & 0xFF00) | (n79 + ((n74 - n79) * n72 >> 8) & 0xFF));
                        }
                    }
                }
                catch (Exception ex) {}
                ++n17;
                if (n6 == 0) {
                    n19 += this.iuadd;
                    n20 += this.ivadd;
                }
                n21 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.zleft += this.zleftadd;
            this.aleft += this.aleftadd;
        }
    }
    
    private void drawsegment_texture32_alpha(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final boolean b = (this.m_fill & 0xFFFFFF) != 0xFFFFFF;
        final int n11 = this.m_fill >> 16 & 0xFF;
        final int n12 = this.m_fill >> 8 & 0xFF;
        final int n13 = this.m_fill & 0xFF;
        final float n14 = (float)this.iuadd;
        final float n15 = (float)this.ivadd;
        final float n16 = (float)this.iaadd;
        while (i < n3) {
            int n17 = (int)(this.xleft + 0.5f);
            if (n17 < 0) {
                n17 = 0;
            }
            int n18 = n17;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n19 = n17 + 0.5f - this.xleft;
            int n20 = (int)(n14 * n19 + this.uleft);
            int n21 = (int)(n15 * n19 + this.vleft);
            int n22 = (int)(n16 * n19 + this.aleft);
            float n23 = this.izadd * n19 + this.zleft;
            int j = n17 + i;
            final int n24 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n25 = this.xmult * (n18 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n26 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n25 * this.ax + n26 * this.ay + nearPlaneDepth * this.az;
                n8 = n25 * this.bx + n26 * this.by + nearPlaneDepth * this.bz;
                n9 = n25 * this.cx + n26 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b2 = this.newcx > 0.0f != n9 > 0.0f;
            int n27 = 0;
            int n28 = 0;
            int n29 = 0;
            float n40;
            float n41;
            if (n6 != 0 && b2) {
                final int n30 = (n24 - j - 1) % n10;
                final int n31 = n10 - n30;
                final float n32 = n30 / (float)n10;
                final float n33 = n31 / (float)n10;
                n27 = n31;
                final float n34 = n7 - n33 * this.newax;
                final float n35 = n8 - n33 * this.newbx;
                final float n36 = 65536.0f / (n9 - n33 * this.newcx);
                final float n37 = n34 * n36;
                final float n38 = n35 * n36;
                n7 += n32 * this.newax;
                n8 += n32 * this.newbx;
                n9 += n32 * this.newcx;
                final float n39 = 65536.0f / n9;
                n40 = n7 * n39;
                n41 = n8 * n39;
                n28 = (int)(n40 - n37) >> tex_INTERP_POWER;
                n29 = (int)(n41 - n38) >> tex_INTERP_POWER;
                n20 = (int)n37 + (n31 - 1) * n28;
                n21 = (int)n38 + (n31 - 1) * n29;
            }
            else {
                final float n42 = 65536.0f / n9;
                n40 = n7 * n42;
                n41 = n8 * n42;
            }
            while (j < n24) {
                if (n6 != 0) {
                    if (n27 == n10) {
                        n27 = 0;
                    }
                    if (n27 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n43 = 65536.0f / n9;
                        final float n44 = n40;
                        final float n45 = n41;
                        n40 = n7 * n43;
                        n41 = n8 * n43;
                        n20 = (int)n44;
                        n21 = (int)n45;
                        n28 = (int)(n40 - n44) >> tex_INTERP_POWER;
                        n29 = (int)(n41 - n45) >> tex_INTERP_POWER;
                    }
                    else {
                        n20 += n28;
                        n21 += n29;
                    }
                    ++n27;
                }
                try {
                    if (this.noDepthTest || n23 <= this.m_zbuffer[j]) {
                        final int n46 = n22 >> 16;
                        if (this.m_bilinear) {
                            int n47 = (n21 >> 16) * this.TEX_WIDTH + (n20 >> 16);
                            final int n48 = (n20 & 0xFFFF) >> 9;
                            final int n49 = (n21 & 0xFFFF) >> 9;
                            final int n50 = this.m_texture[n47];
                            final int n51 = this.m_texture[n47 + 1];
                            if (n47 < n5) {
                                n47 += this.TEX_WIDTH;
                            }
                            final int n52 = this.m_texture[n47];
                            final int n53 = this.m_texture[n47 + 1];
                            final int n54 = n50 & 0xFF0000;
                            final int n55 = n52 & 0xFF0000;
                            final int n56 = n54 + (((n51 & 0xFF0000) - n54) * n48 >> 7);
                            int n57 = n56 + ((n55 + (((n53 & 0xFF0000) - n55) * n48 >> 7) - n56) * n49 >> 7);
                            if (b) {
                                n57 = (n57 * n11 >> 8 & 0xFF0000);
                            }
                            final int n58 = n50 & 0xFF00;
                            final int n59 = n52 & 0xFF00;
                            final int n60 = n58 + (((n51 & 0xFF00) - n58) * n48 >> 7);
                            int n61 = n60 + ((n59 + (((n53 & 0xFF00) - n59) * n48 >> 7) - n60) * n49 >> 7);
                            if (b) {
                                n61 = (n61 * n12 >> 8 & 0xFF00);
                            }
                            final int n62 = n50 & 0xFF;
                            final int n63 = n52 & 0xFF;
                            final int n64 = n62 + (((n51 & 0xFF) - n62) * n48 >> 7);
                            int n65 = n64 + ((n63 + (((n53 & 0xFF) - n63) * n48 >> 7) - n64) * n49 >> 7);
                            if (b) {
                                n65 = (n65 * n13 >> 8 & 0xFF);
                            }
                            final int n66 = n50 >>> 24;
                            final int n67 = n52 >>> 24;
                            final int n68 = n66 + (((n51 >>> 24) - n66) * n48 >> 7);
                            final int n69 = n46 * (n68 + ((n67 + (((n53 >>> 24) - n67) * n48 >> 7) - n68) * n49 >> 7)) >> 8;
                            final int n70 = this.m_pixels[j];
                            final int n71 = n70 & 0xFF0000;
                            final int n72 = n70 & 0xFF00;
                            final int n73 = n70 & 0xFF;
                            this.m_pixels[j] = (0xFF000000 | (n71 + ((n57 - n71) * n69 >> 8) & 0xFF0000) | (n72 + ((n61 - n72) * n69 >> 8) & 0xFF00) | (n73 + ((n65 - n73) * n69 >> 8) & 0xFF));
                        }
                        else {
                            final int n74 = this.m_texture[(n21 >> 16) * this.TEX_WIDTH + (n20 >> 16)];
                            final int n75 = n46 * (n74 >>> 24) >> 8;
                            final int n76 = n74 & 0xFF00;
                            final int n77 = n74 & 0xFF;
                            final int n78 = n74 & 0xFF0000;
                            final int n79 = this.m_pixels[j];
                            final int n80 = n79 & 0xFF0000;
                            final int n81 = n79 & 0xFF00;
                            final int n82 = n79 & 0xFF;
                            this.m_pixels[j] = (0xFF000000 | (n80 + ((n78 - n80) * n75 >> 8) & 0xFF0000) | (n81 + ((n76 - n81) * n75 >> 8) & 0xFF00) | (n82 + ((n77 - n82) * n75 >> 8) & 0xFF));
                        }
                    }
                }
                catch (Exception ex) {}
                ++n18;
                if (n6 == 0) {
                    n20 += this.iuadd;
                    n21 += this.ivadd;
                }
                n22 += this.iaadd;
                n23 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.zleft += this.zleftadd;
            this.aleft += this.aleftadd;
        }
    }
    
    private void drawsegment_gouraud_texture8(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iradd;
        final float n14 = (float)this.igadd;
        final float n15 = (float)this.ibadd;
        while (i < n3) {
            int n16 = (int)(this.xleft + 0.5f);
            if (n16 < 0) {
                n16 = 0;
            }
            int n17 = n16;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n18 = n16 + 0.5f - this.xleft;
            int n19 = (int)(n11 * n18 + this.uleft);
            int n20 = (int)(n12 * n18 + this.vleft);
            int n21 = (int)(n13 * n18 + this.rleft);
            int n22 = (int)(n14 * n18 + this.gleft);
            int n23 = (int)(n15 * n18 + this.bleft);
            float n24 = this.izadd * n18 + this.zleft;
            int j = n16 + i;
            final int n25 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n26 = this.xmult * (n17 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n27 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n26 * this.ax + n27 * this.ay + nearPlaneDepth * this.az;
                n8 = n26 * this.bx + n27 * this.by + nearPlaneDepth * this.bz;
                n9 = n26 * this.cx + n27 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n28 = 0;
            int n29 = 0;
            int n30 = 0;
            float n41;
            float n42;
            if (n6 != 0 && b) {
                final int n31 = (n25 - j - 1) % n10;
                final int n32 = n10 - n31;
                final float n33 = n31 / (float)n10;
                final float n34 = n32 / (float)n10;
                n28 = n32;
                final float n35 = n7 - n34 * this.newax;
                final float n36 = n8 - n34 * this.newbx;
                final float n37 = 65536.0f / (n9 - n34 * this.newcx);
                final float n38 = n35 * n37;
                final float n39 = n36 * n37;
                n7 += n33 * this.newax;
                n8 += n33 * this.newbx;
                n9 += n33 * this.newcx;
                final float n40 = 65536.0f / n9;
                n41 = n7 * n40;
                n42 = n8 * n40;
                n29 = (int)(n41 - n38) >> tex_INTERP_POWER;
                n30 = (int)(n42 - n39) >> tex_INTERP_POWER;
                n19 = (int)n38 + (n32 - 1) * n29;
                n20 = (int)n39 + (n32 - 1) * n30;
            }
            else {
                final float n43 = 65536.0f / n9;
                n41 = n7 * n43;
                n42 = n8 * n43;
            }
            while (j < n25) {
                if (n6 != 0) {
                    if (n28 == n10) {
                        n28 = 0;
                    }
                    if (n28 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n44 = 65536.0f / n9;
                        final float n45 = n41;
                        final float n46 = n42;
                        n41 = n7 * n44;
                        n42 = n8 * n44;
                        n19 = (int)n45;
                        n20 = (int)n46;
                        n29 = (int)(n41 - n45) >> tex_INTERP_POWER;
                        n30 = (int)(n42 - n46) >> tex_INTERP_POWER;
                    }
                    else {
                        n19 += n29;
                        n20 += n30;
                    }
                    ++n28;
                }
                try {
                    if (this.noDepthTest || n24 <= this.m_zbuffer[j]) {
                        int n54;
                        if (this.m_bilinear) {
                            int n47 = (n20 >> 16) * this.TEX_WIDTH + (n19 >> 16);
                            final int n48 = n19 & 0xFFFF;
                            final int n49 = this.m_texture[n47] & 0xFF;
                            final int n50 = this.m_texture[n47 + 1] & 0xFF;
                            if (n47 < n5) {
                                n47 += this.TEX_WIDTH;
                            }
                            final int n51 = this.m_texture[n47] & 0xFF;
                            final int n52 = this.m_texture[n47 + 1] & 0xFF;
                            final int n53 = n49 + ((n50 - n49) * n48 >> 16);
                            n54 = n53 + ((n51 + ((n52 - n51) * n48 >> 16) - n53) * (n20 & 0xFFFF) >> 16);
                        }
                        else {
                            n54 = (this.m_texture[(n20 >> 16) * this.TEX_WIDTH + (n19 >> 16)] & 0xFF);
                        }
                        final int n55 = n21 & 0xFF0000;
                        final int n56 = n22 >> 8 & 0xFF00;
                        final int n57 = n23 >> 16;
                        final int n58 = this.m_pixels[j];
                        final int n59 = n58 & 0xFF0000;
                        final int n60 = n58 & 0xFF00;
                        final int n61 = n58 & 0xFF;
                        this.m_pixels[j] = (0xFF000000 | (n59 + ((n55 - n59) * n54 >> 8) & 0xFF0000) | (n60 + ((n56 - n60) * n54 >> 8) & 0xFF00) | (n61 + ((n57 - n61) * n54 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n17;
                if (n6 == 0) {
                    n19 += this.iuadd;
                    n20 += this.ivadd;
                }
                n21 += this.iradd;
                n22 += this.igadd;
                n23 += this.ibadd;
                n24 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_gouraud_texture8_alpha(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iradd;
        final float n14 = (float)this.igadd;
        final float n15 = (float)this.ibadd;
        final float n16 = (float)this.iaadd;
        while (i < n3) {
            int n17 = (int)(this.xleft + 0.5f);
            if (n17 < 0) {
                n17 = 0;
            }
            int n18 = n17;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n19 = n17 + 0.5f - this.xleft;
            int n20 = (int)(n11 * n19 + this.uleft);
            int n21 = (int)(n12 * n19 + this.vleft);
            int n22 = (int)(n13 * n19 + this.rleft);
            int n23 = (int)(n14 * n19 + this.gleft);
            int n24 = (int)(n15 * n19 + this.bleft);
            int n25 = (int)(n16 * n19 + this.aleft);
            float n26 = this.izadd * n19 + this.zleft;
            int j = n17 + i;
            final int n27 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n28 = this.xmult * (n18 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n29 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n28 * this.ax + n29 * this.ay + nearPlaneDepth * this.az;
                n8 = n28 * this.bx + n29 * this.by + nearPlaneDepth * this.bz;
                n9 = n28 * this.cx + n29 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n30 = 0;
            int n31 = 0;
            int n32 = 0;
            float n43;
            float n44;
            if (n6 != 0 && b) {
                final int n33 = (n27 - j - 1) % n10;
                final int n34 = n10 - n33;
                final float n35 = n33 / (float)n10;
                final float n36 = n34 / (float)n10;
                n30 = n34;
                final float n37 = n7 - n36 * this.newax;
                final float n38 = n8 - n36 * this.newbx;
                final float n39 = 65536.0f / (n9 - n36 * this.newcx);
                final float n40 = n37 * n39;
                final float n41 = n38 * n39;
                n7 += n35 * this.newax;
                n8 += n35 * this.newbx;
                n9 += n35 * this.newcx;
                final float n42 = 65536.0f / n9;
                n43 = n7 * n42;
                n44 = n8 * n42;
                n31 = (int)(n43 - n40) >> tex_INTERP_POWER;
                n32 = (int)(n44 - n41) >> tex_INTERP_POWER;
                n20 = (int)n40 + (n34 - 1) * n31;
                n21 = (int)n41 + (n34 - 1) * n32;
            }
            else {
                final float n45 = 65536.0f / n9;
                n43 = n7 * n45;
                n44 = n8 * n45;
            }
            while (j < n27) {
                if (n6 != 0) {
                    if (n30 == n10) {
                        n30 = 0;
                    }
                    if (n30 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n46 = 65536.0f / n9;
                        final float n47 = n43;
                        final float n48 = n44;
                        n43 = n7 * n46;
                        n44 = n8 * n46;
                        n20 = (int)n47;
                        n21 = (int)n48;
                        n31 = (int)(n43 - n47) >> tex_INTERP_POWER;
                        n32 = (int)(n44 - n48) >> tex_INTERP_POWER;
                    }
                    else {
                        n20 += n31;
                        n21 += n32;
                    }
                    ++n30;
                }
                try {
                    if (this.noDepthTest || n26 <= this.m_zbuffer[j]) {
                        int n56;
                        if (this.m_bilinear) {
                            int n49 = (n21 >> 16) * this.TEX_WIDTH + (n20 >> 16);
                            final int n50 = n20 & 0xFFFF;
                            final int n51 = this.m_texture[n49] & 0xFF;
                            final int n52 = this.m_texture[n49 + 1] & 0xFF;
                            if (n49 < n5) {
                                n49 += this.TEX_WIDTH;
                            }
                            final int n53 = this.m_texture[n49] & 0xFF;
                            final int n54 = this.m_texture[n49 + 1] & 0xFF;
                            final int n55 = n51 + ((n52 - n51) * n50 >> 16);
                            n56 = n55 + ((n53 + ((n54 - n53) * n50 >> 16) - n55) * (n21 & 0xFFFF) >> 16);
                        }
                        else {
                            n56 = (this.m_texture[(n21 >> 16) * this.TEX_WIDTH + (n20 >> 16)] & 0xFF);
                        }
                        final int n57 = n56 * (n25 >> 16) >> 8;
                        final int n58 = n22 & 0xFF0000;
                        final int n59 = n23 >> 8 & 0xFF00;
                        final int n60 = n24 >> 16;
                        final int n61 = this.m_pixels[j];
                        final int n62 = n61 & 0xFF0000;
                        final int n63 = n61 & 0xFF00;
                        final int n64 = n61 & 0xFF;
                        this.m_pixels[j] = (0xFF000000 | (n62 + ((n58 - n62) * n57 >> 8) & 0xFF0000) | (n63 + ((n59 - n63) * n57 >> 8) & 0xFF00) | (n64 + ((n60 - n64) * n57 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n18;
                if (n6 == 0) {
                    n20 += this.iuadd;
                    n21 += this.ivadd;
                }
                n22 += this.iradd;
                n23 += this.igadd;
                n24 += this.ibadd;
                n25 += this.iaadd;
                n26 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.aleft += this.aleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_gouraud_texture24(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iradd;
        final float n14 = (float)this.igadd;
        final float n15 = (float)this.ibadd;
        while (i < n3) {
            int n16 = (int)(this.xleft + 0.5f);
            if (n16 < 0) {
                n16 = 0;
            }
            int n17 = n16;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n18 = n16 + 0.5f - this.xleft;
            int n19 = (int)(n11 * n18 + this.uleft);
            int n20 = (int)(n12 * n18 + this.vleft);
            int n21 = (int)(n13 * n18 + this.rleft);
            int n22 = (int)(n14 * n18 + this.gleft);
            int n23 = (int)(n15 * n18 + this.bleft);
            float n24 = this.izadd * n18 + this.zleft;
            int j = n16 + i;
            final int n25 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n26 = this.xmult * (n17 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n27 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n26 * this.ax + n27 * this.ay + nearPlaneDepth * this.az;
                n8 = n26 * this.bx + n27 * this.by + nearPlaneDepth * this.bz;
                n9 = n26 * this.cx + n27 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n28 = 0;
            int n29 = 0;
            int n30 = 0;
            float n41;
            float n42;
            if (n6 != 0 && b) {
                final int n31 = (n25 - j - 1) % n10;
                final int n32 = n10 - n31;
                final float n33 = n31 / (float)n10;
                final float n34 = n32 / (float)n10;
                n28 = n32;
                final float n35 = n7 - n34 * this.newax;
                final float n36 = n8 - n34 * this.newbx;
                final float n37 = 65536.0f / (n9 - n34 * this.newcx);
                final float n38 = n35 * n37;
                final float n39 = n36 * n37;
                n7 += n33 * this.newax;
                n8 += n33 * this.newbx;
                n9 += n33 * this.newcx;
                final float n40 = 65536.0f / n9;
                n41 = n7 * n40;
                n42 = n8 * n40;
                n29 = (int)(n41 - n38) >> tex_INTERP_POWER;
                n30 = (int)(n42 - n39) >> tex_INTERP_POWER;
                n19 = (int)n38 + (n32 - 1) * n29;
                n20 = (int)n39 + (n32 - 1) * n30;
            }
            else {
                final float n43 = 65536.0f / n9;
                n41 = n7 * n43;
                n42 = n8 * n43;
            }
            while (j < n25) {
                if (n6 != 0) {
                    if (n28 == n10) {
                        n28 = 0;
                    }
                    if (n28 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n44 = 65536.0f / n9;
                        final float n45 = n41;
                        final float n46 = n42;
                        n41 = n7 * n44;
                        n42 = n8 * n44;
                        n19 = (int)n45;
                        n20 = (int)n46;
                        n29 = (int)(n41 - n45) >> tex_INTERP_POWER;
                        n30 = (int)(n42 - n46) >> tex_INTERP_POWER;
                    }
                    else {
                        n19 += n29;
                        n20 += n30;
                    }
                    ++n28;
                }
                try {
                    if (this.noDepthTest || n24 <= this.m_zbuffer[j]) {
                        this.m_zbuffer[j] = n24;
                        int n57;
                        int n61;
                        int n65;
                        if (this.m_bilinear) {
                            int n47 = (n20 >> 16) * this.TEX_WIDTH + (n19 >> 16);
                            final int n48 = (n19 & 0xFFFF) >> 9;
                            final int n49 = (n20 & 0xFFFF) >> 9;
                            final int n50 = this.m_texture[n47];
                            final int n51 = this.m_texture[n47 + 1];
                            if (n47 < n5) {
                                n47 += this.TEX_WIDTH;
                            }
                            final int n52 = this.m_texture[n47];
                            final int n53 = this.m_texture[n47 + 1];
                            final int n54 = n50 & 0xFF0000;
                            final int n55 = n52 & 0xFF0000;
                            final int n56 = n54 + (((n51 & 0xFF0000) - n54) * n48 >> 7);
                            n57 = n56 + ((n55 + (((n53 & 0xFF0000) - n55) * n48 >> 7) - n56) * n49 >> 7);
                            final int n58 = n50 & 0xFF00;
                            final int n59 = n52 & 0xFF00;
                            final int n60 = n58 + (((n51 & 0xFF00) - n58) * n48 >> 7);
                            n61 = n60 + ((n59 + (((n53 & 0xFF00) - n59) * n48 >> 7) - n60) * n49 >> 7);
                            final int n62 = n50 & 0xFF;
                            final int n63 = n52 & 0xFF;
                            final int n64 = n62 + (((n51 & 0xFF) - n62) * n48 >> 7);
                            n65 = n64 + ((n63 + (((n53 & 0xFF) - n63) * n48 >> 7) - n64) * n49 >> 7);
                        }
                        else {
                            final int n66 = this.m_texture[(n20 >> 16) * this.TEX_WIDTH + (n19 >> 16)];
                            n57 = (n66 & 0xFF0000);
                            n61 = (n66 & 0xFF00);
                            n65 = (n66 & 0xFF);
                        }
                        this.m_pixels[j] = (0xFF000000 | ((n57 * (n21 >> 16) & 0xFF000000) | (n61 * (n22 >> 16) & 0xFF0000) | n65 * (n23 >> 16)) >> 8);
                    }
                }
                catch (Exception ex) {}
                ++n17;
                if (n6 == 0) {
                    n19 += this.iuadd;
                    n20 += this.ivadd;
                }
                n21 += this.iradd;
                n22 += this.igadd;
                n23 += this.ibadd;
                n24 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_gouraud_texture24_alpha(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iradd;
        final float n14 = (float)this.igadd;
        final float n15 = (float)this.ibadd;
        final float n16 = (float)this.iaadd;
        while (i < n3) {
            int n17 = (int)(this.xleft + 0.5f);
            if (n17 < 0) {
                n17 = 0;
            }
            int n18 = n17;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n19 = n17 + 0.5f - this.xleft;
            int n20 = (int)(n11 * n19 + this.uleft);
            int n21 = (int)(n12 * n19 + this.vleft);
            int n22 = (int)(n13 * n19 + this.rleft);
            int n23 = (int)(n14 * n19 + this.gleft);
            int n24 = (int)(n15 * n19 + this.bleft);
            int n25 = (int)(n16 * n19 + this.aleft);
            float n26 = this.izadd * n19 + this.zleft;
            int j = n17 + i;
            final int n27 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n28 = this.xmult * (n18 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n29 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n28 * this.ax + n29 * this.ay + nearPlaneDepth * this.az;
                n8 = n28 * this.bx + n29 * this.by + nearPlaneDepth * this.bz;
                n9 = n28 * this.cx + n29 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n30 = 0;
            int n31 = 0;
            int n32 = 0;
            float n43;
            float n44;
            if (n6 != 0 && b) {
                final int n33 = (n27 - j - 1) % n10;
                final int n34 = n10 - n33;
                final float n35 = n33 / (float)n10;
                final float n36 = n34 / (float)n10;
                n30 = n34;
                final float n37 = n7 - n36 * this.newax;
                final float n38 = n8 - n36 * this.newbx;
                final float n39 = 65536.0f / (n9 - n36 * this.newcx);
                final float n40 = n37 * n39;
                final float n41 = n38 * n39;
                n7 += n35 * this.newax;
                n8 += n35 * this.newbx;
                n9 += n35 * this.newcx;
                final float n42 = 65536.0f / n9;
                n43 = n7 * n42;
                n44 = n8 * n42;
                n31 = (int)(n43 - n40) >> tex_INTERP_POWER;
                n32 = (int)(n44 - n41) >> tex_INTERP_POWER;
                n20 = (int)n40 + (n34 - 1) * n31;
                n21 = (int)n41 + (n34 - 1) * n32;
            }
            else {
                final float n45 = 65536.0f / n9;
                n43 = n7 * n45;
                n44 = n8 * n45;
            }
            while (j < n27) {
                if (n6 != 0) {
                    if (n30 == n10) {
                        n30 = 0;
                    }
                    if (n30 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n46 = 65536.0f / n9;
                        final float n47 = n43;
                        final float n48 = n44;
                        n43 = n7 * n46;
                        n44 = n8 * n46;
                        n20 = (int)n47;
                        n21 = (int)n48;
                        n31 = (int)(n43 - n47) >> tex_INTERP_POWER;
                        n32 = (int)(n44 - n48) >> tex_INTERP_POWER;
                    }
                    else {
                        n20 += n31;
                        n21 += n32;
                    }
                    ++n30;
                }
                try {
                    if (this.noDepthTest || n26 <= this.m_zbuffer[j]) {
                        final int n49 = n25 >> 16;
                        int n60;
                        int n64;
                        int n68;
                        if (this.m_bilinear) {
                            int n50 = (n21 >> 16) * this.TEX_WIDTH + (n20 >> 16);
                            final int n51 = (n20 & 0xFFFF) >> 9;
                            final int n52 = (n21 & 0xFFFF) >> 9;
                            final int n53 = this.m_texture[n50];
                            final int n54 = this.m_texture[n50 + 1];
                            if (n50 < n5) {
                                n50 += this.TEX_WIDTH;
                            }
                            final int n55 = this.m_texture[n50];
                            final int n56 = this.m_texture[n50 + 1];
                            final int n57 = n53 & 0xFF0000;
                            final int n58 = n55 & 0xFF0000;
                            final int n59 = n57 + (((n54 & 0xFF0000) - n57) * n51 >> 7);
                            n60 = n59 + ((n58 + (((n56 & 0xFF0000) - n58) * n51 >> 7) - n59) * n52 >> 7) >> 16;
                            final int n61 = n53 & 0xFF00;
                            final int n62 = n55 & 0xFF00;
                            final int n63 = n61 + (((n54 & 0xFF00) - n61) * n51 >> 7);
                            n64 = n63 + ((n62 + (((n56 & 0xFF00) - n62) * n51 >> 7) - n63) * n52 >> 7) >> 8;
                            final int n65 = n53 & 0xFF;
                            final int n66 = n55 & 0xFF;
                            final int n67 = n65 + (((n54 & 0xFF) - n65) * n51 >> 7);
                            n68 = n67 + ((n66 + (((n56 & 0xFF) - n66) * n51 >> 7) - n67) * n52 >> 7);
                        }
                        else {
                            final int n69 = this.m_texture[(n21 >> 16) * this.TEX_WIDTH + (n20 >> 16)];
                            n60 = (n69 & 0xFF0000) >> 16;
                            n64 = (n69 & 0xFF00) >> 8;
                            n68 = (n69 & 0xFF);
                        }
                        final int n70 = n60 * n22 >>> 8;
                        final int n71 = n64 * n23 >>> 16;
                        final int n72 = n68 * n24 >>> 24;
                        final int n73 = this.m_pixels[j];
                        final int n74 = n73 & 0xFF0000;
                        final int n75 = n73 & 0xFF00;
                        final int n76 = n73 & 0xFF;
                        this.m_pixels[j] = (0xFF000000 | (n74 + ((n70 - n74) * n49 >> 8) & 0xFF0000) | (n75 + ((n71 - n75) * n49 >> 8) & 0xFF00) | (n76 + ((n72 - n76) * n49 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n18;
                if (n6 == 0) {
                    n20 += this.iuadd;
                    n21 += this.ivadd;
                }
                n22 += this.iradd;
                n23 += this.igadd;
                n24 += this.ibadd;
                n25 += this.iaadd;
                n26 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.aleft += this.aleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_gouraud_texture32(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iradd;
        final float n14 = (float)this.igadd;
        final float n15 = (float)this.ibadd;
        while (i < n3) {
            int n16 = (int)(this.xleft + 0.5f);
            if (n16 < 0) {
                n16 = 0;
            }
            int n17 = n16;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n18 = n16 + 0.5f - this.xleft;
            int n19 = (int)(n11 * n18 + this.uleft);
            int n20 = (int)(n12 * n18 + this.vleft);
            int n21 = (int)(n13 * n18 + this.rleft);
            int n22 = (int)(n14 * n18 + this.gleft);
            int n23 = (int)(n15 * n18 + this.bleft);
            float n24 = this.izadd * n18 + this.zleft;
            int j = n16 + i;
            final int n25 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n26 = this.xmult * (n17 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n27 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n26 * this.ax + n27 * this.ay + nearPlaneDepth * this.az;
                n8 = n26 * this.bx + n27 * this.by + nearPlaneDepth * this.bz;
                n9 = n26 * this.cx + n27 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n28 = 0;
            int n29 = 0;
            int n30 = 0;
            float n41;
            float n42;
            if (n6 != 0 && b) {
                final int n31 = (n25 - j - 1) % n10;
                final int n32 = n10 - n31;
                final float n33 = n31 / (float)n10;
                final float n34 = n32 / (float)n10;
                n28 = n32;
                final float n35 = n7 - n34 * this.newax;
                final float n36 = n8 - n34 * this.newbx;
                final float n37 = 65536.0f / (n9 - n34 * this.newcx);
                final float n38 = n35 * n37;
                final float n39 = n36 * n37;
                n7 += n33 * this.newax;
                n8 += n33 * this.newbx;
                n9 += n33 * this.newcx;
                final float n40 = 65536.0f / n9;
                n41 = n7 * n40;
                n42 = n8 * n40;
                n29 = (int)(n41 - n38) >> tex_INTERP_POWER;
                n30 = (int)(n42 - n39) >> tex_INTERP_POWER;
                n19 = (int)n38 + (n32 - 1) * n29;
                n20 = (int)n39 + (n32 - 1) * n30;
            }
            else {
                final float n43 = 65536.0f / n9;
                n41 = n7 * n43;
                n42 = n8 * n43;
            }
            while (j < n25) {
                if (n6 != 0) {
                    if (n28 == n10) {
                        n28 = 0;
                    }
                    if (n28 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n44 = 65536.0f / n9;
                        final float n45 = n41;
                        final float n46 = n42;
                        n41 = n7 * n44;
                        n42 = n8 * n44;
                        n19 = (int)n45;
                        n20 = (int)n46;
                        n29 = (int)(n41 - n45) >> tex_INTERP_POWER;
                        n30 = (int)(n42 - n46) >> tex_INTERP_POWER;
                    }
                    else {
                        n19 += n29;
                        n20 += n30;
                    }
                    ++n28;
                }
                try {
                    if (this.noDepthTest || n24 <= this.m_zbuffer[j]) {
                        int n57;
                        int n61;
                        int n65;
                        int n69;
                        if (this.m_bilinear) {
                            int n47 = (n20 >> 16) * this.TEX_WIDTH + (n19 >> 16);
                            final int n48 = (n19 & 0xFFFF) >> 9;
                            final int n49 = (n20 & 0xFFFF) >> 9;
                            final int n50 = this.m_texture[n47];
                            final int n51 = this.m_texture[n47 + 1];
                            if (n47 < n5) {
                                n47 += this.TEX_WIDTH;
                            }
                            final int n52 = this.m_texture[n47];
                            final int n53 = this.m_texture[n47 + 1];
                            final int n54 = n50 & 0xFF0000;
                            final int n55 = n52 & 0xFF0000;
                            final int n56 = n54 + (((n51 & 0xFF0000) - n54) * n48 >> 7);
                            n57 = n56 + ((n55 + (((n53 & 0xFF0000) - n55) * n48 >> 7) - n56) * n49 >> 7) >> 16;
                            final int n58 = n50 & 0xFF00;
                            final int n59 = n52 & 0xFF00;
                            final int n60 = n58 + (((n51 & 0xFF00) - n58) * n48 >> 7);
                            n61 = n60 + ((n59 + (((n53 & 0xFF00) - n59) * n48 >> 7) - n60) * n49 >> 7) >> 8;
                            final int n62 = n50 & 0xFF;
                            final int n63 = n52 & 0xFF;
                            final int n64 = n62 + (((n51 & 0xFF) - n62) * n48 >> 7);
                            n65 = n64 + ((n63 + (((n53 & 0xFF) - n63) * n48 >> 7) - n64) * n49 >> 7);
                            final int n66 = n50 >>> 24;
                            final int n67 = n52 >>> 24;
                            final int n68 = n66 + (((n51 >>> 24) - n66) * n48 >> 7);
                            n69 = n68 + ((n67 + (((n53 >>> 24) - n67) * n48 >> 7) - n68) * n49 >> 7);
                        }
                        else {
                            final int n70 = this.m_texture[(n20 >> 16) * this.TEX_WIDTH + (n19 >> 16)];
                            n69 = n70 >>> 24;
                            n57 = (n70 & 0xFF0000) >> 16;
                            n61 = (n70 & 0xFF00) >> 8;
                            n65 = (n70 & 0xFF);
                        }
                        final int n71 = n57 * n21 >>> 8;
                        final int n72 = n61 * n22 >>> 16;
                        final int n73 = n65 * n23 >>> 24;
                        final int n74 = this.m_pixels[j];
                        final int n75 = n74 & 0xFF0000;
                        final int n76 = n74 & 0xFF00;
                        final int n77 = n74 & 0xFF;
                        this.m_pixels[j] = (0xFF000000 | (n75 + ((n71 - n75) * n69 >> 8) & 0xFF0000) | (n76 + ((n72 - n76) * n69 >> 8) & 0xFF00) | (n77 + ((n73 - n77) * n69 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n17;
                if (n6 == 0) {
                    n19 += this.iuadd;
                    n20 += this.ivadd;
                }
                n21 += this.iradd;
                n22 += this.igadd;
                n23 += this.ibadd;
                n24 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    private void drawsegment_gouraud_texture32_alpha(final float n, final float n2, int i, int n3) {
        int n4 = i;
        final int n5 = this.m_texture.length - this.TEX_WIDTH - 2;
        int n6 = this.parent.hints[7] ? 1 : 0;
        float nearPlaneDepth = 0.0f;
        float n7 = 0.0f;
        float n8 = 0.0f;
        float n9 = 0.0f;
        final int tex_INTERP_POWER = PTriangle.TEX_INTERP_POWER;
        final int n10 = 1 << tex_INTERP_POWER;
        if (n6 != 0) {
            if (this.precomputeAccurateTexturing()) {
                this.newax *= n10;
                this.newbx *= n10;
                this.newcx *= n10;
                nearPlaneDepth = this.nearPlaneDepth;
                this.firstSegment = false;
            }
            else {
                n6 = 0;
            }
        }
        i *= this.SCREEN_WIDTH;
        n3 *= this.SCREEN_WIDTH;
        final float n11 = (float)this.iuadd;
        final float n12 = (float)this.ivadd;
        final float n13 = (float)this.iradd;
        final float n14 = (float)this.igadd;
        final float n15 = (float)this.ibadd;
        final float n16 = (float)this.iaadd;
        while (i < n3) {
            int n17 = (int)(this.xleft + 0.5f);
            if (n17 < 0) {
                n17 = 0;
            }
            int n18 = n17;
            int screen_WIDTH = (int)(this.xrght + 0.5f);
            if (screen_WIDTH > this.SCREEN_WIDTH) {
                screen_WIDTH = this.SCREEN_WIDTH;
            }
            final float n19 = n17 + 0.5f - this.xleft;
            int n20 = (int)(n11 * n19 + this.uleft);
            int n21 = (int)(n12 * n19 + this.vleft);
            int n22 = (int)(n13 * n19 + this.rleft);
            int n23 = (int)(n14 * n19 + this.gleft);
            int n24 = (int)(n15 * n19 + this.bleft);
            int n25 = (int)(n16 * n19 + this.aleft);
            float n26 = this.izadd * n19 + this.zleft;
            int j = n17 + i;
            final int n27 = screen_WIDTH + i;
            if (n6 != 0) {
                final float n28 = this.xmult * (n18 + 0.5f - this.SCREEN_WIDTH / 2.0f);
                final float n29 = this.ymult * (n4 + 0.5f - this.SCREEN_HEIGHT / 2.0f);
                n7 = n28 * this.ax + n29 * this.ay + nearPlaneDepth * this.az;
                n8 = n28 * this.bx + n29 * this.by + nearPlaneDepth * this.bz;
                n9 = n28 * this.cx + n29 * this.cy + nearPlaneDepth * this.cz;
            }
            final boolean b = this.newcx > 0.0f != n9 > 0.0f;
            int n30 = 0;
            int n31 = 0;
            int n32 = 0;
            float n43;
            float n44;
            if (n6 != 0 && b) {
                final int n33 = (n27 - j - 1) % n10;
                final int n34 = n10 - n33;
                final float n35 = n33 / (float)n10;
                final float n36 = n34 / (float)n10;
                n30 = n34;
                final float n37 = n7 - n36 * this.newax;
                final float n38 = n8 - n36 * this.newbx;
                final float n39 = 65536.0f / (n9 - n36 * this.newcx);
                final float n40 = n37 * n39;
                final float n41 = n38 * n39;
                n7 += n35 * this.newax;
                n8 += n35 * this.newbx;
                n9 += n35 * this.newcx;
                final float n42 = 65536.0f / n9;
                n43 = n7 * n42;
                n44 = n8 * n42;
                n31 = (int)(n43 - n40) >> tex_INTERP_POWER;
                n32 = (int)(n44 - n41) >> tex_INTERP_POWER;
                n20 = (int)n40 + (n34 - 1) * n31;
                n21 = (int)n41 + (n34 - 1) * n32;
            }
            else {
                final float n45 = 65536.0f / n9;
                n43 = n7 * n45;
                n44 = n8 * n45;
            }
            while (j < n27) {
                if (n6 != 0) {
                    if (n30 == n10) {
                        n30 = 0;
                    }
                    if (n30 == 0) {
                        n7 += this.newax;
                        n8 += this.newbx;
                        n9 += this.newcx;
                        final float n46 = 65536.0f / n9;
                        final float n47 = n43;
                        final float n48 = n44;
                        n43 = n7 * n46;
                        n44 = n8 * n46;
                        n20 = (int)n47;
                        n21 = (int)n48;
                        n31 = (int)(n43 - n47) >> tex_INTERP_POWER;
                        n32 = (int)(n44 - n48) >> tex_INTERP_POWER;
                    }
                    else {
                        n20 += n31;
                        n21 += n32;
                    }
                    ++n30;
                }
                try {
                    if (this.noDepthTest || n26 <= this.m_zbuffer[j]) {
                        final int n49 = n25 >> 16;
                        int n60;
                        int n64;
                        int n68;
                        int n72;
                        if (this.m_bilinear) {
                            int n50 = (n21 >> 16) * this.TEX_WIDTH + (n20 >> 16);
                            final int n51 = (n20 & 0xFFFF) >> 9;
                            final int n52 = (n21 & 0xFFFF) >> 9;
                            final int n53 = this.m_texture[n50];
                            final int n54 = this.m_texture[n50 + 1];
                            if (n50 < n5) {
                                n50 += this.TEX_WIDTH;
                            }
                            final int n55 = this.m_texture[n50];
                            final int n56 = this.m_texture[n50 + 1];
                            final int n57 = n53 & 0xFF0000;
                            final int n58 = n55 & 0xFF0000;
                            final int n59 = n57 + (((n54 & 0xFF0000) - n57) * n51 >> 7);
                            n60 = n59 + ((n58 + (((n56 & 0xFF0000) - n58) * n51 >> 7) - n59) * n52 >> 7) >> 16;
                            final int n61 = n53 & 0xFF00;
                            final int n62 = n55 & 0xFF00;
                            final int n63 = n61 + (((n54 & 0xFF00) - n61) * n51 >> 7);
                            n64 = n63 + ((n62 + (((n56 & 0xFF00) - n62) * n51 >> 7) - n63) * n52 >> 7) >> 8;
                            final int n65 = n53 & 0xFF;
                            final int n66 = n55 & 0xFF;
                            final int n67 = n65 + (((n54 & 0xFF) - n65) * n51 >> 7);
                            n68 = n67 + ((n66 + (((n56 & 0xFF) - n66) * n51 >> 7) - n67) * n52 >> 7);
                            final int n69 = n53 >>> 24;
                            final int n70 = n55 >>> 24;
                            final int n71 = n69 + (((n54 >>> 24) - n69) * n51 >> 7);
                            n72 = n49 * (n71 + ((n70 + (((n56 >>> 24) - n70) * n51 >> 7) - n71) * n52 >> 7)) >> 8;
                        }
                        else {
                            final int n73 = this.m_texture[(n21 >> 16) * this.TEX_WIDTH + (n20 >> 16)];
                            n72 = n49 * (n73 >>> 24) >> 8;
                            n60 = (n73 & 0xFF0000) >> 16;
                            n64 = (n73 & 0xFF00) >> 8;
                            n68 = (n73 & 0xFF);
                        }
                        final int n74 = n60 * n22 >>> 8;
                        final int n75 = n64 * n23 >>> 16;
                        final int n76 = n68 * n24 >>> 24;
                        final int n77 = this.m_pixels[j];
                        final int n78 = n77 & 0xFF0000;
                        final int n79 = n77 & 0xFF00;
                        final int n80 = n77 & 0xFF;
                        this.m_pixels[j] = (0xFF000000 | (n78 + ((n74 - n78) * n72 >> 8) & 0xFF0000) | (n79 + ((n75 - n79) * n72 >> 8) & 0xFF00) | (n80 + ((n76 - n80) * n72 >> 8) & 0xFF));
                    }
                }
                catch (Exception ex) {}
                ++n18;
                if (n6 == 0) {
                    n20 += this.iuadd;
                    n21 += this.ivadd;
                }
                n22 += this.iradd;
                n23 += this.igadd;
                n24 += this.ibadd;
                n25 += this.iaadd;
                n26 += this.izadd;
                ++j;
            }
            ++n4;
            i += this.SCREEN_WIDTH;
            this.xleft += n;
            this.xrght += n2;
            this.uleft += this.uleftadd;
            this.vleft += this.vleftadd;
            this.rleft += this.rleftadd;
            this.gleft += this.gleftadd;
            this.bleft += this.bleftadd;
            this.aleft += this.aleftadd;
            this.zleft += this.zleftadd;
        }
    }
    
    static {
        PTriangle.TEX_INTERP_POWER = 3;
    }
}
