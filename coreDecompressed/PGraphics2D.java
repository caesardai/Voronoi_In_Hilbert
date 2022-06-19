package processing.core;

import java.util.Arrays;
import java.awt.image.ColorModel;
import java.awt.image.ImageProducer;
import java.awt.Toolkit;
import java.awt.image.MemoryImageSource;
import java.awt.image.DirectColorModel;

public class PGraphics2D extends PGraphics
{
    PMatrix2D ctm;
    PPolygon fpolygon;
    PPolygon spolygon;
    float[][] svertices;
    PPolygon tpolygon;
    int[] vertexOrder;
    PLine line;
    float[][] matrixStack;
    int matrixStackDepth;
    DirectColorModel cm;
    MemoryImageSource mis;
    
    public PGraphics2D() {
        this.ctm = new PMatrix2D();
        this.matrixStack = new float[32][6];
    }
    
    @Override
    protected void allocate() {
        this.pixelCount = this.width * this.height;
        this.pixels = new int[this.pixelCount];
        if (this.primarySurface) {
            this.cm = new DirectColorModel(32, 16711680, 65280, 255);
            (this.mis = new MemoryImageSource(this.width, this.height, this.pixels, 0, this.width)).setFullBufferUpdates(true);
            this.mis.setAnimated(true);
            this.image = Toolkit.getDefaultToolkit().createImage(this.mis);
        }
    }
    
    @Override
    public boolean canDraw() {
        return true;
    }
    
    @Override
    public void beginDraw() {
        if (!this.settingsInited) {
            this.defaultSettings();
            this.fpolygon = new PPolygon((PGraphics)this);
            this.spolygon = new PPolygon((PGraphics)this);
            this.spolygon.vertexCount = 4;
            this.svertices = new float[2][];
        }
        this.resetMatrix();
        this.vertexCount = 0;
    }
    
    @Override
    public void endDraw() {
        if (this.mis != null) {
            this.mis.newPixels(this.pixels, this.cm, 0, this.width);
        }
        this.updatePixels();
    }
    
    @Override
    public void beginShape(final int shape) {
        this.shape = shape;
        this.vertexCount = 0;
        this.curveVertexCount = 0;
        this.fpolygon.reset(4);
        this.spolygon.reset(4);
        this.textureImage = null;
    }
    
    @Override
    public void vertex(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("vertex");
    }
    
    @Override
    public void vertex(final float n, final float n2, final float n3, final float n4, final float n5) {
        PGraphics.showDepthWarningXYZ("vertex");
    }
    
    @Override
    public void breakShape() {
        PGraphics.showWarning("This renderer cannot handle concave shapes or shapes with holes.");
    }
    
    @Override
    public void endShape(final int n) {
        if (this.ctm.isIdentity()) {
            for (int i = 0; i < this.vertexCount; ++i) {
                this.vertices[i][18] = this.vertices[i][0];
                this.vertices[i][19] = this.vertices[i][1];
            }
        }
        else {
            for (int j = 0; j < this.vertexCount; ++j) {
                this.vertices[j][18] = this.ctm.multX(this.vertices[j][0], this.vertices[j][1]);
                this.vertices[j][19] = this.ctm.multY(this.vertices[j][0], this.vertices[j][1]);
            }
        }
        this.fpolygon.texture(this.textureImage);
        this.spolygon.interpARGB = true;
        this.fpolygon.interpARGB = true;
        switch (this.shape) {
            case 2: {
                if (!this.stroke) {
                    break;
                }
                if (this.ctm.m00 == this.ctm.m11 && this.strokeWeight == 1.0f) {
                    for (int k = 0; k < this.vertexCount; ++k) {
                        this.thin_point(this.vertices[k][18], this.vertices[k][19], this.strokeColor);
                    }
                    break;
                }
                for (int l = 0; l < this.vertexCount; ++l) {
                    final float[] array = this.vertices[l];
                    this.thick_point(array[18], array[19], array[20], array[13], array[14], array[15], array[16]);
                }
                break;
            }
            case 4: {
                if (this.stroke) {
                    this.draw_lines(this.vertices, this.vertexCount - 1, 1, (this.shape == 4) ? 2 : 1, 0);
                    break;
                }
                break;
            }
            case 11: {
                if (this.fill || this.textureImage != null) {
                    this.fpolygon.vertexCount = 3;
                    for (int n2 = 1; n2 < this.vertexCount - 1; ++n2) {
                        this.fpolygon.vertices[2][3] = this.vertices[0][3];
                        this.fpolygon.vertices[2][4] = this.vertices[0][4];
                        this.fpolygon.vertices[2][5] = this.vertices[0][5];
                        this.fpolygon.vertices[2][6] = this.vertices[0][6];
                        this.fpolygon.vertices[2][18] = this.vertices[0][18];
                        this.fpolygon.vertices[2][19] = this.vertices[0][19];
                        if (this.textureImage != null) {
                            this.fpolygon.vertices[2][7] = this.vertices[0][7];
                            this.fpolygon.vertices[2][8] = this.vertices[0][8];
                        }
                        for (int n3 = 0; n3 < 2; ++n3) {
                            this.fpolygon.vertices[n3][3] = this.vertices[n2 + n3][3];
                            this.fpolygon.vertices[n3][4] = this.vertices[n2 + n3][4];
                            this.fpolygon.vertices[n3][5] = this.vertices[n2 + n3][5];
                            this.fpolygon.vertices[n3][6] = this.vertices[n2 + n3][6];
                            this.fpolygon.vertices[n3][18] = this.vertices[n2 + n3][18];
                            this.fpolygon.vertices[n3][19] = this.vertices[n2 + n3][19];
                            if (this.textureImage != null) {
                                this.fpolygon.vertices[n3][7] = this.vertices[n2 + n3][7];
                                this.fpolygon.vertices[n3][8] = this.vertices[n2 + n3][8];
                            }
                        }
                        this.fpolygon.render();
                    }
                }
                if (this.stroke) {
                    for (int n4 = 1; n4 < this.vertexCount; ++n4) {
                        this.draw_line(this.vertices[0], this.vertices[n4]);
                    }
                    for (int n5 = 1; n5 < this.vertexCount - 1; ++n5) {
                        this.draw_line(this.vertices[n5], this.vertices[n5 + 1]);
                    }
                    this.draw_line(this.vertices[this.vertexCount - 1], this.vertices[1]);
                    break;
                }
                break;
            }
            case 9:
            case 10: {
                final int n6 = (this.shape == 9) ? 3 : 1;
                if (this.fill || this.textureImage != null) {
                    this.fpolygon.vertexCount = 3;
                    for (int n7 = 0; n7 < this.vertexCount - 2; n7 += n6) {
                        for (int n8 = 0; n8 < 3; ++n8) {
                            this.fpolygon.vertices[n8][3] = this.vertices[n7 + n8][3];
                            this.fpolygon.vertices[n8][4] = this.vertices[n7 + n8][4];
                            this.fpolygon.vertices[n8][5] = this.vertices[n7 + n8][5];
                            this.fpolygon.vertices[n8][6] = this.vertices[n7 + n8][6];
                            this.fpolygon.vertices[n8][18] = this.vertices[n7 + n8][18];
                            this.fpolygon.vertices[n8][19] = this.vertices[n7 + n8][19];
                            this.fpolygon.vertices[n8][20] = this.vertices[n7 + n8][20];
                            if (this.textureImage != null) {
                                this.fpolygon.vertices[n8][7] = this.vertices[n7 + n8][7];
                                this.fpolygon.vertices[n8][8] = this.vertices[n7 + n8][8];
                            }
                        }
                        this.fpolygon.render();
                    }
                }
                if (this.stroke) {
                    if (this.shape == 10) {
                        this.draw_lines(this.vertices, this.vertexCount - 1, 1, 1, 0);
                    }
                    else {
                        this.draw_lines(this.vertices, this.vertexCount - 1, 1, 1, 3);
                    }
                    this.draw_lines(this.vertices, this.vertexCount - 2, 2, n6, 0);
                    break;
                }
                break;
            }
            case 16: {
                if (this.fill || this.textureImage != null) {
                    this.fpolygon.vertexCount = 4;
                    for (int n9 = 0; n9 < this.vertexCount - 3; n9 += 4) {
                        for (int n10 = 0; n10 < 4; ++n10) {
                            final int n11 = n9 + n10;
                            this.fpolygon.vertices[n10][3] = this.vertices[n11][3];
                            this.fpolygon.vertices[n10][4] = this.vertices[n11][4];
                            this.fpolygon.vertices[n10][5] = this.vertices[n11][5];
                            this.fpolygon.vertices[n10][6] = this.vertices[n11][6];
                            this.fpolygon.vertices[n10][18] = this.vertices[n11][18];
                            this.fpolygon.vertices[n10][19] = this.vertices[n11][19];
                            this.fpolygon.vertices[n10][20] = this.vertices[n11][20];
                            if (this.textureImage != null) {
                                this.fpolygon.vertices[n10][7] = this.vertices[n11][7];
                                this.fpolygon.vertices[n10][8] = this.vertices[n11][8];
                            }
                        }
                        this.fpolygon.render();
                    }
                }
                if (this.stroke) {
                    for (int n12 = 0; n12 < this.vertexCount - 3; n12 += 4) {
                        this.draw_line(this.vertices[n12 + 0], this.vertices[n12 + 1]);
                        this.draw_line(this.vertices[n12 + 1], this.vertices[n12 + 2]);
                        this.draw_line(this.vertices[n12 + 2], this.vertices[n12 + 3]);
                        this.draw_line(this.vertices[n12 + 3], this.vertices[n12 + 0]);
                    }
                    break;
                }
                break;
            }
            case 17: {
                if (this.fill || this.textureImage != null) {
                    this.fpolygon.vertexCount = 4;
                    for (int n13 = 0; n13 < this.vertexCount - 3; n13 += 2) {
                        for (int n14 = 0; n14 < 4; ++n14) {
                            int n15 = n13 + n14;
                            if (n14 == 2) {
                                n15 = n13 + 3;
                            }
                            if (n14 == 3) {
                                n15 = n13 + 2;
                            }
                            this.fpolygon.vertices[n14][3] = this.vertices[n15][3];
                            this.fpolygon.vertices[n14][4] = this.vertices[n15][4];
                            this.fpolygon.vertices[n14][5] = this.vertices[n15][5];
                            this.fpolygon.vertices[n14][6] = this.vertices[n15][6];
                            this.fpolygon.vertices[n14][18] = this.vertices[n15][18];
                            this.fpolygon.vertices[n14][19] = this.vertices[n15][19];
                            this.fpolygon.vertices[n14][20] = this.vertices[n15][20];
                            if (this.textureImage != null) {
                                this.fpolygon.vertices[n14][7] = this.vertices[n15][7];
                                this.fpolygon.vertices[n14][8] = this.vertices[n15][8];
                            }
                        }
                        this.fpolygon.render();
                    }
                }
                if (this.stroke) {
                    this.draw_lines(this.vertices, this.vertexCount - 1, 1, 2, 0);
                    this.draw_lines(this.vertices, this.vertexCount - 2, 2, 1, 0);
                    break;
                }
                break;
            }
            case 20: {
                if (this.isConvex()) {
                    if (this.fill || this.textureImage != null) {
                        this.fpolygon.renderPolygon(this.vertices, this.vertexCount);
                    }
                    if (!this.stroke) {
                        break;
                    }
                    this.draw_lines(this.vertices, this.vertexCount - 1, 1, 1, 0);
                    if (n == 2) {
                        this.draw_line(this.vertices[this.vertexCount - 1], this.vertices[0]);
                        break;
                    }
                    break;
                }
                else {
                    if (this.fill || this.textureImage != null) {
                        final boolean smooth = this.smooth;
                        if (this.stroke) {
                            this.smooth = false;
                        }
                        this.concaveRender();
                        if (this.stroke) {
                            this.smooth = smooth;
                        }
                    }
                    if (!this.stroke) {
                        break;
                    }
                    this.draw_lines(this.vertices, this.vertexCount - 1, 1, 1, 0);
                    if (n == 2) {
                        this.draw_line(this.vertices[this.vertexCount - 1], this.vertices[0]);
                        break;
                    }
                    break;
                }
                break;
            }
        }
        this.shape = 0;
    }
    
    private boolean isConvex() {
        if (this.vertexCount < 3) {
            return true;
        }
        int n = 0;
        for (int i = 0; i < this.vertexCount; ++i) {
            final float[] array = this.vertices[i];
            final float[] array2 = this.vertices[(i + 1) % this.vertexCount];
            final float[] array3 = this.vertices[(i + 2) % this.vertexCount];
            final float n2 = (array2[18] - array[18]) * (array3[19] - array2[19]) - (array2[19] - array[19]) * (array3[18] - array2[18]);
            if (n2 < 0.0f) {
                n |= 0x1;
            }
            else if (n2 > 0.0f) {
                n |= 0x2;
            }
            if (n == 3) {
                return false;
            }
        }
        return n == 0 || true;
    }
    
    protected void concaveRender() {
        if (this.vertexOrder == null || this.vertexOrder.length != this.vertices.length) {
            this.vertexOrder = new int[this.vertices.length];
        }
        if (this.tpolygon == null) {
            this.tpolygon = new PPolygon((PGraphics)this);
        }
        this.tpolygon.reset(3);
        float n = 0.0f;
        int n2 = this.vertexCount - 1;
        for (int i = 0; i < this.vertexCount; n2 = i++) {
            n += this.vertices[i][0] * this.vertices[n2][1] - this.vertices[n2][0] * this.vertices[i][1];
        }
        if (n == 0.0f) {
            return;
        }
        final float[] array = this.vertices[0];
        final float[] array2 = this.vertices[this.vertexCount - 1];
        if (Math.abs(array[0] - array2[0]) < 1.0E-4f && Math.abs(array[1] - array2[1]) < 1.0E-4f && Math.abs(array[2] - array2[2]) < 1.0E-4f) {
            --this.vertexCount;
        }
        for (int j = 0; j < this.vertexCount; ++j) {
            this.vertexOrder[j] = ((n > 0.0f) ? j : (this.vertexCount - 1 - j));
        }
        int k = this.vertexCount;
        int n3 = 2 * k;
        int n4 = 0;
        int n5 = k - 1;
        while (k > 2) {
            boolean b = true;
            if (0 >= n3--) {
                break;
            }
            int n6 = n5;
            if (k <= n6) {
                n6 = 0;
            }
            n5 = n6 + 1;
            if (k <= n5) {
                n5 = 0;
            }
            int n7 = n5 + 1;
            if (k <= n7) {
                n7 = 0;
            }
            final double n8 = -10.0f * this.vertices[this.vertexOrder[n6]][0];
            final double n9 = 10.0f * this.vertices[this.vertexOrder[n6]][1];
            final double n10 = -10.0f * this.vertices[this.vertexOrder[n5]][0];
            final double n11 = 10.0f * this.vertices[this.vertexOrder[n5]][1];
            final double n12 = -10.0f * this.vertices[this.vertexOrder[n7]][0];
            final double n13 = 10.0f * this.vertices[this.vertexOrder[n7]][1];
            if (9.999999747378752E-5 > (n10 - n8) * (n13 - n9) - (n11 - n9) * (n12 - n8)) {
                continue;
            }
            for (int l = 0; l < k; ++l) {
                if (l != n6 && l != n5) {
                    if (l != n7) {
                        final double n14 = -10.0f * this.vertices[this.vertexOrder[l]][0];
                        final double n15 = 10.0f * this.vertices[this.vertexOrder[l]][1];
                        final double n16 = n12 - n10;
                        final double n17 = n13 - n11;
                        final double n18 = n8 - n12;
                        final double n19 = n9 - n13;
                        final double n20 = n10 - n8;
                        final double n21 = n11 - n9;
                        final double n22 = n14 - n8;
                        final double n23 = n15 - n9;
                        final double n24 = n14 - n10;
                        final double n25 = n15 - n11;
                        final double n26 = n14 - n12;
                        final double n27 = n15 - n13;
                        final double n28 = n16 * n25 - n17 * n24;
                        final double n29 = n20 * n23 - n21 * n22;
                        final double n30 = n18 * n27 - n19 * n26;
                        if (n28 >= 0.0 && n30 >= 0.0 && n29 >= 0.0) {
                            b = false;
                        }
                    }
                }
            }
            if (!b) {
                continue;
            }
            this.tpolygon.renderTriangle(this.vertices[this.vertexOrder[n6]], this.vertices[this.vertexOrder[n5]], this.vertices[this.vertexOrder[n7]]);
            ++n4;
            int n31 = n5;
            for (int n32 = n5 + 1; n32 < k; ++n32) {
                this.vertexOrder[n31] = this.vertexOrder[n32];
                ++n31;
            }
            --k;
            n3 = 2 * k;
        }
    }
    
    @Override
    public void point(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("point");
    }
    
    @Override
    protected void rectImpl(final float n, final float n2, final float n3, final float n4) {
        if (this.smooth || this.strokeAlpha || this.ctm.isWarped()) {
            super.rectImpl(n, n2, n3, n4);
        }
        else {
            final int n5 = (int)(n + this.ctm.m02);
            final int n6 = (int)(n2 + this.ctm.m12);
            final int n7 = (int)(n3 + this.ctm.m02);
            final int n8 = (int)(n4 + this.ctm.m12);
            if (this.fill) {
                this.simple_rect_fill(n5, n6, n7, n8);
            }
            if (this.stroke) {
                if (this.strokeWeight == 1.0f) {
                    this.thin_flat_line(n5, n6, n7, n6);
                    this.thin_flat_line(n7, n6, n7, n8);
                    this.thin_flat_line(n7, n8, n5, n8);
                    this.thin_flat_line(n5, n8, n5, n6);
                }
                else {
                    this.thick_flat_line((float)n5, (float)n6, this.strokeR, this.strokeG, this.strokeB, this.strokeA, (float)n7, (float)n6, this.strokeR, this.strokeG, this.strokeB, this.strokeA);
                    this.thick_flat_line((float)n7, (float)n6, this.strokeR, this.strokeG, this.strokeB, this.strokeA, (float)n7, (float)n8, this.strokeR, this.strokeG, this.strokeB, this.strokeA);
                    this.thick_flat_line((float)n7, (float)n8, this.strokeR, this.strokeG, this.strokeB, this.strokeA, (float)n5, (float)n8, this.strokeR, this.strokeG, this.strokeB, this.strokeA);
                    this.thick_flat_line((float)n5, (float)n8, this.strokeR, this.strokeG, this.strokeB, this.strokeA, (float)n5, (float)n6, this.strokeR, this.strokeG, this.strokeB, this.strokeA);
                }
            }
        }
    }
    
    private void simple_rect_fill(int n, int n2, int width, int height) {
        if (height < n2) {
            final int n3 = n2;
            n2 = height;
            height = n3;
        }
        if (width < n) {
            final int n4 = n;
            n = width;
            width = n4;
        }
        if (n > this.width1 || width < 0 || n2 > this.height1 || height < 0) {
            return;
        }
        if (n < 0) {
            n = 0;
        }
        if (width > this.width) {
            width = this.width;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        if (height > this.height) {
            height = this.height;
        }
        final int n5 = width - n;
        if (this.fillAlpha) {
            for (int i = n2; i < height; ++i) {
                int n6 = i * this.width + n;
                for (int j = 0; j < n5; ++j) {
                    this.pixels[n6] = this.blend_fill(this.pixels[n6]);
                    ++n6;
                }
            }
        }
        else {
            final int n7 = height - n2;
            final int n9;
            int n8 = n9 = n2 * this.width + n;
            for (int k = 0; k < n5; ++k) {
                this.pixels[n8 + k] = this.fillColor;
            }
            for (int l = 0; l < n7; ++l) {
                System.arraycopy(this.pixels, n9, this.pixels, n8, n5);
                n8 += this.width;
            }
        }
    }
    
    @Override
    protected void ellipseImpl(final float n, final float n2, final float n3, final float n4) {
        if (this.smooth || this.strokeWeight != 1.0f || this.fillAlpha || this.strokeAlpha || this.ctm.isWarped()) {
            final float n5 = n3 / 2.0f;
            final float n6 = n4 / 2.0f;
            final float n7 = n + n5;
            final float n8 = n2 + n6;
            final int n9 = (int)(6.2831855f * PApplet.dist(this.screenX(n, n2), this.screenY(n, n2), this.screenX(n + n3, n2 + n4), this.screenY(n + n3, n2 + n4)) / 8.0f);
            if (n9 < 4) {
                return;
            }
            final float n10 = 720.0f / n9;
            float n11 = 0.0f;
            if (this.fill) {
                final boolean stroke = this.stroke;
                this.stroke = false;
                this.beginShape();
                for (int i = 0; i < n9; ++i) {
                    this.vertex(n7 + PGraphics2D.cosLUT[(int)n11] * n5, n8 + PGraphics2D.sinLUT[(int)n11] * n6);
                    n11 += n10;
                }
                this.endShape(2);
                this.stroke = stroke;
            }
            if (this.stroke) {
                final boolean fill = this.fill;
                this.fill = false;
                float n12 = 0.0f;
                this.beginShape();
                for (int j = 0; j < n9; ++j) {
                    this.vertex(n7 + PGraphics2D.cosLUT[(int)n12] * n5, n8 + PGraphics2D.sinLUT[(int)n12] * n6);
                    n12 += n10;
                }
                this.endShape(2);
                this.fill = fill;
            }
        }
        else {
            final float n13 = n3 / 2.0f;
            final float n14 = n4 / 2.0f;
            final int n15 = (int)(n + n13 + this.ctm.m02);
            final int n16 = (int)(n2 + n14 + this.ctm.m12);
            final int n17 = (int)n13;
            final int n18 = (int)n14;
            if (n17 == n18) {
                if (this.fill) {
                    this.flat_circle_fill(n15, n16, n17);
                }
                if (this.stroke) {
                    this.flat_circle_stroke(n15, n16, n17);
                }
            }
            else {
                if (this.fill) {
                    this.flat_ellipse_internal(n15, n16, n17, n18, true);
                }
                if (this.stroke) {
                    this.flat_ellipse_internal(n15, n16, n17, n18, false);
                }
            }
        }
    }
    
    private void flat_circle_stroke(final int n, final int n2, final int n3) {
        int i = 0;
        int n4 = n3;
        int n5 = 1;
        int n6 = 2 * n3 - 1;
        int n7 = 0;
        while (i < n4) {
            this.thin_point((float)(n + i), (float)(n2 + n4), this.strokeColor);
            this.thin_point((float)(n + n4), (float)(n2 - i), this.strokeColor);
            this.thin_point((float)(n - i), (float)(n2 - n4), this.strokeColor);
            this.thin_point((float)(n - n4), (float)(n2 + i), this.strokeColor);
            ++i;
            n7 += n5;
            n5 += 2;
            if (n6 < 2 * n7) {
                --n4;
                n7 -= n6;
                n6 -= 2;
            }
            if (i > n4) {
                break;
            }
            this.thin_point((float)(n + n4), (float)(n2 + i), this.strokeColor);
            this.thin_point((float)(n + i), (float)(n2 - n4), this.strokeColor);
            this.thin_point((float)(n - n4), (float)(n2 - i), this.strokeColor);
            this.thin_point((float)(n - i), (float)(n2 + n4), this.strokeColor);
        }
    }
    
    private void flat_circle_fill(final int n, final int n2, final int n3) {
        int i = 0;
        int n4 = n3;
        int n5 = 1;
        int n6 = 2 * n3 - 1;
        int n7 = 0;
        while (i < n4) {
            for (int j = n; j < n + i; ++j) {
                this.thin_point((float)j, (float)(n2 + n4), this.fillColor);
            }
            for (int k = n; k < n + n4; ++k) {
                this.thin_point((float)k, (float)(n2 - i), this.fillColor);
            }
            for (int l = n - i; l < n; ++l) {
                this.thin_point((float)l, (float)(n2 - n4), this.fillColor);
            }
            for (int n8 = n - n4; n8 < n; ++n8) {
                this.thin_point((float)n8, (float)(n2 + i), this.fillColor);
            }
            ++i;
            n7 += n5;
            n5 += 2;
            if (n6 < 2 * n7) {
                --n4;
                n7 -= n6;
                n6 -= 2;
            }
            if (i > n4) {
                break;
            }
            for (int n9 = n; n9 < n + n4; ++n9) {
                this.thin_point((float)n9, (float)(n2 + i), this.fillColor);
            }
            for (int n10 = n; n10 < n + i; ++n10) {
                this.thin_point((float)n10, (float)(n2 - n4), this.fillColor);
            }
            for (int n11 = n - n4; n11 < n; ++n11) {
                this.thin_point((float)n11, (float)(n2 - i), this.fillColor);
            }
            for (int n12 = n - i; n12 < n; ++n12) {
                this.thin_point((float)n12, (float)(n2 + n4), this.fillColor);
            }
        }
    }
    
    private final void flat_ellipse_symmetry(final int n, final int n2, final int n3, final int n4, final boolean b) {
        if (b) {
            for (int i = n - n3 + 1; i < n + n3; ++i) {
                this.thin_point((float)i, (float)(n2 - n4), this.fillColor);
                this.thin_point((float)i, (float)(n2 + n4), this.fillColor);
            }
        }
        else {
            this.thin_point((float)(n - n3), (float)(n2 + n4), this.strokeColor);
            this.thin_point((float)(n + n3), (float)(n2 + n4), this.strokeColor);
            this.thin_point((float)(n - n3), (float)(n2 - n4), this.strokeColor);
            this.thin_point((float)(n + n3), (float)(n2 - n4), this.strokeColor);
        }
    }
    
    private void flat_ellipse_internal(final int n, final int n2, final int n3, final int n4, final boolean b) {
        final int n5 = n3 * n3;
        final int n6 = n4 * n4;
        int n7 = 0;
        int i = n4;
        int n8 = n5 * (1 - 2 * n4) + 2 * n6;
        int n9 = n6 - 2 * n5 * (2 * n4 - 1);
        this.flat_ellipse_symmetry(n, n2, n7, i, b);
        do {
            if (n8 < 0) {
                n8 += 2 * n6 * (2 * n7 + 3);
                n9 += 4 * n6 * (n7 + 1);
                ++n7;
            }
            else if (n9 < 0) {
                n8 += 2 * n6 * (2 * n7 + 3) - 4 * n5 * (i - 1);
                n9 += 4 * n6 * (n7 + 1) - 2 * n5 * (2 * i - 3);
                ++n7;
                --i;
            }
            else {
                n8 -= 4 * n5 * (i - 1);
                n9 -= 2 * n5 * (2 * i - 3);
                --i;
            }
            this.flat_ellipse_symmetry(n, n2, n7, i, b);
        } while (i > 0);
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
            final int n11 = (int)(-0.5f + n5 / 6.2831855f * 720.0f);
            final int n12 = (int)(0.5f + n6 / 6.2831855f * 720.0f);
            this.beginShape();
            this.vertex(n9, n10);
            for (int i = n11; i < n12; ++i) {
                int n13 = i % 720;
                if (n13 < 0) {
                    n13 += 720;
                }
                this.vertex(n9 + PGraphics2D.cosLUT[n13] * n7, n10 + PGraphics2D.sinLUT[n13] * n8);
            }
            this.endShape(2);
            this.stroke = stroke;
        }
        if (this.stroke) {
            final boolean fill = this.fill;
            this.fill = false;
            final int n14 = (int)(0.5f + n5 / 6.2831855f * 720.0f);
            final int n15 = (int)(0.5f + n6 / 6.2831855f * 720.0f);
            this.beginShape();
            for (int n16 = 1, j = n14; j < n15; j += n16) {
                int n17 = j % 720;
                if (n17 < 0) {
                    n17 += 720;
                }
                this.vertex(n9 + PGraphics2D.cosLUT[n17] * n7, n10 + PGraphics2D.sinLUT[n17] * n8);
            }
            this.vertex(n9 + PGraphics2D.cosLUT[n15 % 720] * n7, n10 + PGraphics2D.sinLUT[n15 % 720] * n8);
            this.endShape();
            this.fill = fill;
        }
    }
    
    @Override
    public void box(final float n) {
        PGraphics.showDepthWarning("box");
    }
    
    @Override
    public void box(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarning("box");
    }
    
    @Override
    public void sphereDetail(final int n) {
        PGraphics.showDepthWarning("sphereDetail");
    }
    
    @Override
    public void sphereDetail(final int n, final int n2) {
        PGraphics.showDepthWarning("sphereDetail");
    }
    
    @Override
    public void sphere(final float n) {
        PGraphics.showDepthWarning("sphere");
    }
    
    @Override
    public void bezier(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        PGraphics.showDepthWarningXYZ("bezier");
    }
    
    @Override
    public void curve(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        PGraphics.showDepthWarningXYZ("curve");
    }
    
    @Override
    protected void imageImpl(final PImage pImage, final float n, final float n2, final float n3, final float n4, final int n5, final int n6, final int n7, final int n8) {
        if (n3 - n == pImage.width && n4 - n2 == pImage.height && !this.tint && !this.ctm.isWarped()) {
            this.simple_image(pImage, (int)(n + this.ctm.m02), (int)(n2 + this.ctm.m12), n5, n6, n7, n8);
        }
        else {
            super.imageImpl(pImage, n, n2, n3, n4, n5, n6, n7, n8);
        }
    }
    
    private void simple_image(final PImage pImage, int n, int n2, int n3, int n4, int n5, int n6) {
        int width = n + pImage.width;
        int height = n2 + pImage.height;
        if (n > this.width1 || width < 0 || n2 > this.height1 || height < 0) {
            return;
        }
        if (n < 0) {
            n3 -= n;
            n = 0;
        }
        if (n2 < 0) {
            n4 -= n2;
            n2 = 0;
        }
        if (width > this.width) {
            n5 -= width - this.width;
            width = this.width;
        }
        if (height > this.height) {
            n6 -= height - this.height;
            height = this.height;
        }
        int n7 = n4 * pImage.width + n3;
        int n8 = n2 * this.width;
        if (pImage.format == 2) {
            for (int i = n2; i < height; ++i) {
                int n9 = 0;
                for (int j = n; j < width; ++j) {
                    this.pixels[n8 + j] = this.blend_color(this.pixels[n8 + j], pImage.pixels[n7 + n9++]);
                }
                n7 += pImage.width;
                n8 += this.width;
            }
        }
        else if (pImage.format == 4) {
            for (int k = n2; k < height; ++k) {
                int n10 = 0;
                for (int l = n; l < width; ++l) {
                    this.pixels[n8 + l] = this.blend_color_alpha(this.pixels[n8 + l], this.fillColor, pImage.pixels[n7 + n10++]);
                }
                n7 += pImage.width;
                n8 += this.width;
            }
        }
        else if (pImage.format == 1) {
            int n11 = n8 + n;
            final int n12 = width - n;
            for (int n13 = n2; n13 < height; ++n13) {
                System.arraycopy(pImage.pixels, n7, this.pixels, n11, n12);
                n7 += pImage.width;
                n11 += this.width;
            }
        }
    }
    
    private void thin_point_at(final int n, final int n2, final float n3, final int n4) {
        this.pixels[n2 * this.width + n] = n4;
    }
    
    private void thin_point_at_index(final int n, final float n2, final int n3) {
        this.pixels[n] = n3;
    }
    
    private void thick_point(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7) {
        this.spolygon.reset(4);
        this.spolygon.interpARGB = false;
        final float n8 = this.strokeWeight / 2.0f;
        final float[] array = this.spolygon.vertices[0];
        array[18] = n - n8;
        array[19] = n2 - n8;
        array[20] = n3;
        array[3] = n4;
        array[4] = n5;
        array[5] = n6;
        array[6] = n7;
        final float[] array2 = this.spolygon.vertices[1];
        array2[18] = n + n8;
        array2[19] = n2 - n8;
        array2[20] = n3;
        final float[] array3 = this.spolygon.vertices[2];
        array3[18] = n + n8;
        array3[19] = n2 + n8;
        array3[20] = n3;
        final float[] array4 = this.spolygon.vertices[3];
        array4[18] = n - n8;
        array4[19] = n2 + n8;
        array4[20] = n3;
        this.spolygon.render();
    }
    
    private void thin_flat_line(final int n, final int n2, final int n3, final int n4) {
        final int thin_flat_line_clip_code = this.thin_flat_line_clip_code((float)n, (float)n2);
        final int thin_flat_line_clip_code2 = this.thin_flat_line_clip_code((float)n3, (float)n4);
        if ((thin_flat_line_clip_code & thin_flat_line_clip_code2) != 0x0) {
            return;
        }
        final int n5 = thin_flat_line_clip_code | thin_flat_line_clip_code2;
        int j;
        int k;
        int n6;
        int n7;
        if (n5 != 0) {
            float max = 0.0f;
            float min = 1.0f;
            for (int i = 0; i < 4; ++i) {
                if ((n5 >> i) % 2 == 1) {
                    final float thin_flat_line_slope = this.thin_flat_line_slope((float)n, (float)n2, (float)n3, (float)n4, i + 1);
                    if ((thin_flat_line_clip_code >> i) % 2 == 1) {
                        max = Math.max(thin_flat_line_slope, max);
                    }
                    else {
                        min = Math.min(thin_flat_line_slope, min);
                    }
                }
            }
            if (max > min) {
                return;
            }
            j = (int)(n + max * (n3 - n));
            k = (int)(n2 + max * (n4 - n2));
            n6 = (int)(n + min * (n3 - n));
            n7 = (int)(n2 + min * (n4 - n2));
        }
        else {
            j = n;
            n6 = n3;
            k = n2;
            n7 = n4;
        }
        boolean b = false;
        int a = n7 - k;
        int a2 = n6 - j;
        if (Math.abs(a) > Math.abs(a2)) {
            final int n8 = a;
            a = a2;
            a2 = n8;
            b = true;
        }
        int n9;
        if (a2 == 0) {
            n9 = 0;
        }
        else {
            n9 = (a << 16) / a2;
        }
        if (j == n6) {
            if (k > n7) {
                final int n10 = k;
                k = n7;
                n7 = n10;
            }
            int n11 = k * this.width + j;
            for (int l = k; l <= n7; ++l) {
                this.thin_point_at_index(n11, 0.0f, this.strokeColor);
                n11 += this.width;
            }
            return;
        }
        if (k == n7) {
            if (j > n6) {
                final int n12 = j;
                j = n6;
                n6 = n12;
            }
            int n13 = k * this.width + j;
            for (int n14 = j; n14 <= n6; ++n14) {
                this.thin_point_at_index(n13++, 0.0f, this.strokeColor);
            }
            return;
        }
        if (b) {
            if (a2 > 0) {
                final int n15 = a2 + k;
                int n16 = 32768 + (j << 16);
                while (k <= n15) {
                    this.thin_point_at(n16 >> 16, k, 0.0f, this.strokeColor);
                    n16 += n9;
                    ++k;
                }
                return;
            }
            final int n17 = a2 + k;
            int n18 = 32768 + (j << 16);
            while (k >= n17) {
                this.thin_point_at(n18 >> 16, k, 0.0f, this.strokeColor);
                n18 -= n9;
                --k;
            }
        }
        else {
            if (a2 > 0) {
                final int n19 = a2 + j;
                int n20 = 32768 + (k << 16);
                while (j <= n19) {
                    this.thin_point_at(j, n20 >> 16, 0.0f, this.strokeColor);
                    n20 += n9;
                    ++j;
                }
                return;
            }
            final int n21 = a2 + j;
            int n22 = 32768 + (k << 16);
            while (j >= n21) {
                this.thin_point_at(j, n22 >> 16, 0.0f, this.strokeColor);
                n22 -= n9;
                --j;
            }
        }
    }
    
    private int thin_flat_line_clip_code(final float n, final float n2) {
        return ((n2 < 0.0f) ? 8 : 0) | ((n2 > this.height1) ? 4 : 0) | ((n < 0.0f) ? 2 : 0) | ((n > this.width1) ? 1 : 0);
    }
    
    private float thin_flat_line_slope(final float n, final float n2, final float n3, final float n4, final int n5) {
        switch (n5) {
            case 4: {
                return -n2 / (n4 - n2);
            }
            case 3: {
                return (this.height1 - n2) / (n4 - n2);
            }
            case 2: {
                return -n / (n3 - n);
            }
            case 1: {
                return (this.width1 - n) / (n3 - n);
            }
            default: {
                return -1.0f;
            }
        }
    }
    
    private void thick_flat_line(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12) {
        this.spolygon.interpARGB = (n3 != n9 || n4 != n10 || n5 != n11 || n6 != n12);
        final float n13 = n7 - n + 1.0E-4f;
        final float n14 = n8 - n2 + 1.0E-4f;
        final float n15 = this.strokeWeight / (float)Math.sqrt(n13 * n13 + n14 * n14) / 2.0f;
        final float n16 = n15 * n14;
        final float n17 = n15 * n13;
        final float n18 = n15 * n14;
        final float n19 = n15 * n13;
        this.spolygon.reset(4);
        final float[] array = this.spolygon.vertices[0];
        array[18] = n + n16;
        array[19] = n2 - n17;
        array[3] = n3;
        array[4] = n4;
        array[5] = n5;
        array[6] = n6;
        final float[] array2 = this.spolygon.vertices[1];
        array2[18] = n - n16;
        array2[19] = n2 + n17;
        array2[3] = n3;
        array2[4] = n4;
        array2[5] = n5;
        array2[6] = n6;
        final float[] array3 = this.spolygon.vertices[2];
        array3[18] = n7 - n18;
        array3[19] = n8 + n19;
        array3[3] = n9;
        array3[4] = n10;
        array3[5] = n11;
        array3[6] = n12;
        final float[] array4 = this.spolygon.vertices[3];
        array4[18] = n7 + n18;
        array4[19] = n8 - n19;
        array4[3] = n9;
        array4[4] = n10;
        array4[5] = n11;
        array4[6] = n12;
        this.spolygon.render();
    }
    
    private void draw_line(final float[] array, final float[] array2) {
        if (this.strokeWeight == 1.0f) {
            if (this.line == null) {
                this.line = new PLine((PGraphics)this);
            }
            this.line.reset();
            this.line.setIntensities(array[13], array[14], array[15], array[16], array2[13], array2[14], array2[15], array2[16]);
            this.line.setVertices(array[18], array[19], array[20], array2[18], array2[19], array2[20]);
            this.line.draw();
        }
        else {
            this.thick_flat_line(array[18], array[19], array[13], array[14], array[15], array[16], array2[18], array2[19], array2[13], array2[14], array2[15], array2[16]);
        }
    }
    
    private void draw_lines(final float[][] array, final int n, final int n2, final int n3, final int n4) {
        if (this.strokeWeight == 1.0f) {
            for (int i = 0; i < n; i += n3) {
                if (n4 == 0 || (i + n2) % n4 != 0) {
                    final float[] array2 = array[i];
                    final float[] array3 = array[i + n2];
                    if (this.line == null) {
                        this.line = new PLine((PGraphics)this);
                    }
                    this.line.reset();
                    this.line.setIntensities(array2[13], array2[14], array2[15], array2[16], array3[13], array3[14], array3[15], array3[16]);
                    this.line.setVertices(array2[18], array2[19], array2[20], array3[18], array3[19], array3[20]);
                    this.line.draw();
                }
            }
        }
        else {
            for (int j = 0; j < n; j += n3) {
                if (n4 == 0 || (j + n2) % n4 != 0) {
                    final float[] array4 = array[j];
                    final float[] array5 = array[j + n2];
                    this.thick_flat_line(array4[18], array4[19], array4[13], array4[14], array4[15], array4[16], array5[18], array5[19], array5[13], array5[14], array5[15], array5[16]);
                }
            }
        }
    }
    
    private void thin_point(final float n, final float n2, final int n3) {
        final int n4 = (int)(n + 0.4999f);
        final int n5 = (int)(n2 + 0.4999f);
        if (n4 < 0 || n4 > this.width1 || n5 < 0 || n5 > this.height1) {
            return;
        }
        final int n6 = n5 * this.width + n4;
        if ((n3 & 0xFF000000) == 0xFF000000) {
            this.pixels[n6] = n3;
        }
        else {
            final int n7 = n3 >> 24 & 0xFF;
            final int n8 = n7 ^ 0xFF;
            final int strokeColor = this.strokeColor;
            final int n9 = this.pixels[n6];
            this.pixels[n6] = (0xFF000000 | (n8 * (n9 >> 16 & 0xFF) + n7 * (strokeColor >> 16 & 0xFF) & 0xFF00) << 8 | (n8 * (n9 >> 8 & 0xFF) + n7 * (strokeColor >> 8 & 0xFF) & 0xFF00) | n8 * (n9 & 0xFF) + n7 * (strokeColor & 0xFF) >> 8);
        }
    }
    
    @Override
    public void translate(final float n, final float n2) {
        this.ctm.translate(n, n2);
    }
    
    @Override
    public void translate(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("translate");
    }
    
    @Override
    public void rotate(final float n) {
        this.ctm.rotate(n);
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
        PGraphics.showVariationWarning("rotate(angle, x, y, z)");
    }
    
    @Override
    public void scale(final float n) {
        this.ctm.scale(n);
    }
    
    @Override
    public void scale(final float n, final float n2) {
        this.ctm.scale(n, n2);
    }
    
    @Override
    public void scale(final float n, final float n2, final float n3) {
        PGraphics.showDepthWarningXYZ("scale");
    }
    
    public void skewX(final float n) {
        this.ctm.shearX(n);
    }
    
    public void skewY(final float n) {
        this.ctm.shearY(n);
    }
    
    @Override
    public void pushMatrix() {
        if (this.matrixStackDepth == 32) {
            throw new RuntimeException("Too many calls to pushMatrix().");
        }
        this.ctm.get(this.matrixStack[this.matrixStackDepth]);
        ++this.matrixStackDepth;
    }
    
    @Override
    public void popMatrix() {
        if (this.matrixStackDepth == 0) {
            throw new RuntimeException("Too many calls to popMatrix(), and not enough to pushMatrix().");
        }
        --this.matrixStackDepth;
        this.ctm.set(this.matrixStack[this.matrixStackDepth]);
    }
    
    @Override
    public void resetMatrix() {
        this.ctm.reset();
    }
    
    @Override
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6) {
        this.ctm.apply(n, n2, n3, n4, n5, n6);
    }
    
    @Override
    public void applyMatrix(final float n, final float n2, final float n3, final float n4, final float n5, final float n6, final float n7, final float n8, final float n9, final float n10, final float n11, final float n12, final float n13, final float n14, final float n15, final float n16) {
        PGraphics.showDepthWarningXYZ("applyMatrix");
    }
    
    @Override
    public void printMatrix() {
        this.ctm.print();
    }
    
    @Override
    public float screenX(final float n, final float n2) {
        return this.ctm.m00 * n + this.ctm.m01 * n2 + this.ctm.m02;
    }
    
    @Override
    public float screenY(final float n, final float n2) {
        return this.ctm.m10 * n + this.ctm.m11 * n2 + this.ctm.m12;
    }
    
    @Override
    protected void backgroundImpl() {
        Arrays.fill(this.pixels, this.backgroundColor);
    }
    
    private final int blend_fill(final int n) {
        final int fillAi = this.fillAi;
        final int n2 = fillAi ^ 0xFF;
        return 0xFF000000 | (n2 * (n >> 16 & 0xFF) + fillAi * this.fillRi & 0xFF00) << 8 | (n2 * (n >> 8 & 0xFF) + fillAi * this.fillGi & 0xFF00) | (n2 * (n & 0xFF) + fillAi * this.fillBi & 0xFF00) >> 8;
    }
    
    private final int blend_color(final int n, final int n2) {
        final int n3 = n2 >>> 24;
        if (n3 == 255) {
            return n2;
        }
        final int n4 = n3 ^ 0xFF;
        return 0xFF000000 | (n4 * (n >> 16 & 0xFF) + n3 * (n2 >> 16 & 0xFF) & 0xFF00) << 8 | (n4 * (n >> 8 & 0xFF) + n3 * (n2 >> 8 & 0xFF) & 0xFF00) | n4 * (n & 0xFF) + n3 * (n2 & 0xFF) >> 8;
    }
    
    private final int blend_color_alpha(final int n, final int n2, int n3) {
        n3 = n3 * (n2 >>> 24) >> 8;
        final int n4 = n3 ^ 0xFF;
        return 0xFF000000 | (n4 * (n >> 16 & 0xFF) + n3 * (n2 >> 16 & 0xFF) & 0xFF00) << 8 | (n4 * (n >> 8 & 0xFF) + n3 * (n2 >> 8 & 0xFF) & 0xFF00) | n4 * (n & 0xFF) + n3 * (n2 & 0xFF) >> 8;
    }
}