package processing.core;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.awt.image.PixelGrabber;
import java.awt.image.ImageObserver;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.util.HashMap;

public class PImage implements PConstants, Cloneable
{
    public int format;
    public int[] pixels;
    public int width;
    public int height;
    public PApplet parent;
    protected HashMap<PGraphics, Object> cacheMap;
    protected HashMap<PGraphics, Object> paramMap;
    protected boolean modified;
    protected int mx1;
    protected int my1;
    protected int mx2;
    protected int my2;
    private int fracU;
    private int ifU;
    private int fracV;
    private int ifV;
    private int u1;
    private int u2;
    private int v1;
    private int v2;
    private int sX;
    private int sY;
    private int iw;
    private int iw1;
    private int ih1;
    private int ul;
    private int ll;
    private int ur;
    private int lr;
    private int cUL;
    private int cLL;
    private int cUR;
    private int cLR;
    private int srcXOffset;
    private int srcYOffset;
    private int r;
    private int g;
    private int b;
    private int a;
    private int[] srcBuffer;
    static final int PRECISIONB = 15;
    static final int PRECISIONF = 32768;
    static final int PREC_MAXVAL = 32767;
    static final int PREC_ALPHA_SHIFT = 9;
    static final int PREC_RED_SHIFT = 1;
    private int blurRadius;
    private int blurKernelSize;
    private int[] blurKernel;
    private int[][] blurMult;
    static byte[] TIFF_HEADER;
    static final String TIFF_ERROR = "Error: Processing can only read its own TIFF files.";
    protected String[] saveImageFormats;
    
    public PImage() {
        this.format = 2;
    }
    
    public PImage(final int n, final int n2) {
        this.init(n, n2, 1);
    }
    
    public PImage(final int n, final int n2, final int n3) {
        this.init(n, n2, n3);
    }
    
    public void init(final int width, final int height, final int format) {
        this.width = width;
        this.height = height;
        this.pixels = new int[width * height];
        this.format = format;
    }
    
    protected void checkAlpha() {
        if (this.pixels == null) {
            return;
        }
        for (int i = 0; i < this.pixels.length; ++i) {
            if ((this.pixels[i] & 0xFF000000) != 0xFF000000) {
                this.format = 2;
                break;
            }
        }
    }
    
    public PImage(final Image img) {
        this.format = 1;
        if (img instanceof BufferedImage) {
            final BufferedImage bufferedImage = (BufferedImage)img;
            this.width = bufferedImage.getWidth();
            this.height = bufferedImage.getHeight();
            this.pixels = new int[this.width * this.height];
            bufferedImage.getRaster().getDataElements(0, 0, this.width, this.height, this.pixels);
            if (bufferedImage.getType() == 2) {
                this.format = 2;
            }
        }
        else {
            this.width = img.getWidth(null);
            this.height = img.getHeight(null);
            this.pixels = new int[this.width * this.height];
            final PixelGrabber pixelGrabber = new PixelGrabber(img, 0, 0, this.width, this.height, this.pixels, 0, this.width);
            try {
                pixelGrabber.grabPixels();
            }
            catch (InterruptedException ex) {}
        }
    }
    
    public Image getImage() {
        this.loadPixels();
        final BufferedImage bufferedImage = new BufferedImage(this.width, this.height, (this.format == 1) ? 1 : 2);
        bufferedImage.getRaster().setDataElements(0, 0, this.width, this.height, this.pixels);
        return bufferedImage;
    }
    
    public void delete() {
        if (this.cacheMap != null) {
            final Set<PGraphics> keySet = this.cacheMap.keySet();
            if (!keySet.isEmpty()) {
                final Object[] array = keySet.toArray();
                for (int i = 0; i < array.length; ++i) {
                    final Object cache = this.getCache((PGraphics)array[i]);
                    Method method = null;
                    try {
                        method = cache.getClass().getMethod("delete", (Class<?>[])new Class[0]);
                    }
                    catch (Exception ex) {}
                    if (method != null) {
                        try {
                            method.invoke(cache, new Object[0]);
                        }
                        catch (Exception ex2) {}
                    }
                }
            }
        }
    }
    
    public void setCache(final PGraphics key, final Object value) {
        if (this.cacheMap == null) {
            this.cacheMap = new HashMap<PGraphics, Object>();
        }
        this.cacheMap.put(key, value);
    }
    
    public Object getCache(final PGraphics key) {
        if (this.cacheMap == null) {
            return null;
        }
        return this.cacheMap.get(key);
    }
    
    public void removeCache(final PGraphics key) {
        if (this.cacheMap != null) {
            this.cacheMap.remove(key);
        }
    }
    
    public void setParams(final PGraphics key, final Object value) {
        if (this.paramMap == null) {
            this.paramMap = new HashMap<PGraphics, Object>();
        }
        this.paramMap.put(key, value);
    }
    
    public Object getParams(final PGraphics key) {
        if (this.paramMap == null) {
            return null;
        }
        return this.paramMap.get(key);
    }
    
    public void removeParams(final PGraphics key) {
        if (this.paramMap != null) {
            this.paramMap.remove(key);
        }
    }
    
    public boolean isModified() {
        return this.modified;
    }
    
    public void setModified() {
        this.modified = true;
    }
    
    public void setModified(final boolean modified) {
        this.modified = modified;
    }
    
    public int getModifiedX1() {
        return this.mx1;
    }
    
    public int getModifiedX2() {
        return this.mx2;
    }
    
    public int getModifiedY1() {
        return this.my1;
    }
    
    public int getModifiedY2() {
        return this.my2;
    }
    
    public void loadPixels() {
    }
    
    public void updatePixels() {
        this.updatePixelsImpl(0, 0, this.width, this.height);
    }
    
    public void updatePixels(final int n, final int n2, final int n3, final int n4) {
        this.updatePixelsImpl(n, n2, n3, n4);
    }
    
    protected void updatePixelsImpl(final int mx2, final int my2, final int n, final int n2) {
        final int mx3 = mx2 + n;
        final int my3 = my2 + n2;
        if (!this.modified) {
            this.mx1 = mx2;
            this.mx2 = mx3;
            this.my1 = my2;
            this.my2 = my3;
            this.modified = true;
        }
        else {
            if (mx2 < this.mx1) {
                this.mx1 = mx2;
            }
            if (mx2 > this.mx2) {
                this.mx2 = mx2;
            }
            if (my2 < this.my1) {
                this.my1 = my2;
            }
            if (my2 > this.my2) {
                this.my2 = my2;
            }
            if (mx3 < this.mx1) {
                this.mx1 = mx3;
            }
            if (mx3 > this.mx2) {
                this.mx2 = mx3;
            }
            if (my3 < this.my1) {
                this.my1 = my3;
            }
            if (my3 > this.my2) {
                this.my2 = my3;
            }
        }
    }
    
    public Object clone() throws CloneNotSupportedException {
        return this.get();
    }
    
    public void resize(int width, int height) {
        this.loadPixels();
        if (width <= 0 && height <= 0) {
            this.width = 0;
            this.height = 0;
            this.pixels = new int[0];
        }
        else {
            if (width == 0) {
                width = (int)(this.width * (height / (float)this.height));
            }
            else if (height == 0) {
                height = (int)(this.height * (width / (float)this.width));
            }
            final PImage pImage = new PImage(width, height, this.format);
            pImage.copy(this, 0, 0, this.width, this.height, 0, 0, width, height);
            this.width = width;
            this.height = height;
            this.pixels = pImage.pixels;
        }
        this.updatePixels();
    }
    
    public int get(final int n, final int n2) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            return 0;
        }
        switch (this.format) {
            case 1: {
                return this.pixels[n2 * this.width + n] | 0xFF000000;
            }
            case 2: {
                return this.pixels[n2 * this.width + n];
            }
            case 4: {
                return this.pixels[n2 * this.width + n] << 24 | 0xFFFFFF;
            }
            default: {
                return 0;
            }
        }
    }
    
    public PImage get(int n, int n2, int n3, int n4) {
        if (n < 0) {
            n3 += n;
            n = 0;
        }
        if (n2 < 0) {
            n4 += n2;
            n2 = 0;
        }
        if (n + n3 > this.width) {
            n3 = this.width - n;
        }
        if (n2 + n4 > this.height) {
            n4 = this.height - n2;
        }
        return this.getImpl(n, n2, n3, n4);
    }
    
    protected PImage getImpl(final int n, final int n2, final int n3, final int n4) {
        final PImage pImage = new PImage(n3, n4, this.format);
        pImage.parent = this.parent;
        int n5 = n2 * this.width + n;
        int n6 = 0;
        for (int i = n2; i < n2 + n4; ++i) {
            System.arraycopy(this.pixels, n5, pImage.pixels, n6, n3);
            n5 += this.width;
            n6 += n3;
        }
        return pImage;
    }
    
    public PImage get() {
        return this.get(0, 0, this.width, this.height);
    }
    
    public void set(final int n, final int n2, final int n3) {
        if (n < 0 || n2 < 0 || n >= this.width || n2 >= this.height) {
            return;
        }
        this.pixels[n2 * this.width + n] = n3;
        this.updatePixelsImpl(n, n2, n + 1, n2 + 1);
    }
    
    public void set(int n, int n2, final PImage pImage) {
        int n3 = 0;
        int n4 = 0;
        int width = pImage.width;
        int height = pImage.height;
        if (n < 0) {
            n3 -= n;
            width += n;
            n = 0;
        }
        if (n2 < 0) {
            n4 -= n2;
            height += n2;
            n2 = 0;
        }
        if (n + width > this.width) {
            width = this.width - n;
        }
        if (n2 + height > this.height) {
            height = this.height - n2;
        }
        if (width <= 0 || height <= 0) {
            return;
        }
        this.setImpl(n, n2, n3, n4, width, height, pImage);
    }
    
    protected void setImpl(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final PImage pImage) {
        int n7 = n4 * pImage.width + n3;
        int n8 = n2 * this.width + n;
        for (int i = n4; i < n4 + n6; ++i) {
            System.arraycopy(pImage.pixels, n7, this.pixels, n8, n5);
            n7 += pImage.width;
            n8 += this.width;
        }
        this.updatePixelsImpl(n3, n4, n3 + n5, n4 + n6);
    }
    
    public void mask(final int[] array) {
        this.loadPixels();
        if (array.length != this.pixels.length) {
            throw new RuntimeException("The PImage used with mask() must be the same size as the applet.");
        }
        for (int i = 0; i < this.pixels.length; ++i) {
            this.pixels[i] = ((array[i] & 0xFF) << 24 | (this.pixels[i] & 0xFFFFFF));
        }
        this.format = 2;
        this.updatePixels();
    }
    
    public void mask(final PImage pImage) {
        pImage.loadPixels();
        this.mask(pImage.pixels);
    }
    
    public void filter(final int n) {
        this.loadPixels();
        switch (n) {
            case 11: {
                this.filter(11, 1.0f);
                break;
            }
            case 12: {
                if (this.format == 4) {
                    for (int i = 0; i < this.pixels.length; ++i) {
                        final int n2 = 255 - this.pixels[i];
                        this.pixels[i] = (0xFF000000 | n2 << 16 | n2 << 8 | n2);
                    }
                    this.format = 1;
                    break;
                }
                for (int j = 0; j < this.pixels.length; ++j) {
                    final int n3 = this.pixels[j];
                    final int n4 = 77 * (n3 >> 16 & 0xFF) + 151 * (n3 >> 8 & 0xFF) + 28 * (n3 & 0xFF) >> 8;
                    this.pixels[j] = ((n3 & 0xFF000000) | n4 << 16 | n4 << 8 | n4);
                }
                break;
            }
            case 13: {
                for (int k = 0; k < this.pixels.length; ++k) {
                    final int[] pixels = this.pixels;
                    final int n5 = k;
                    pixels[n5] ^= 0xFFFFFF;
                }
                break;
            }
            case 15: {
                throw new RuntimeException("Use filter(POSTERIZE, int levels) instead of filter(POSTERIZE)");
            }
            case 14: {
                for (int l = 0; l < this.pixels.length; ++l) {
                    final int[] pixels2 = this.pixels;
                    final int n6 = l;
                    pixels2[n6] |= 0xFF000000;
                }
                this.format = 1;
                break;
            }
            case 16: {
                this.filter(16, 0.5f);
                break;
            }
            case 17: {
                this.dilate(true);
                break;
            }
            case 18: {
                this.dilate(false);
                break;
            }
        }
        this.updatePixels();
    }
    
    public void filter(final int n, final float n2) {
        this.loadPixels();
        switch (n) {
            case 11: {
                if (this.format == 4) {
                    this.blurAlpha(n2);
                    break;
                }
                if (this.format == 2) {
                    this.blurARGB(n2);
                    break;
                }
                this.blurRGB(n2);
                break;
            }
            case 12: {
                throw new RuntimeException("Use filter(GRAY) instead of filter(GRAY, param)");
            }
            case 13: {
                throw new RuntimeException("Use filter(INVERT) instead of filter(INVERT, param)");
            }
            case 14: {
                throw new RuntimeException("Use filter(OPAQUE) instead of filter(OPAQUE, param)");
            }
            case 15: {
                final int n3 = (int)n2;
                if (n3 < 2 || n3 > 255) {
                    throw new RuntimeException("Levels must be between 2 and 255 for filter(POSTERIZE, levels)");
                }
                final int n4 = n3 - 1;
                for (int i = 0; i < this.pixels.length; ++i) {
                    this.pixels[i] = ((0xFF000000 & this.pixels[i]) | ((this.pixels[i] >> 16 & 0xFF) * n3 >> 8) * 255 / n4 << 16 | ((this.pixels[i] >> 8 & 0xFF) * n3 >> 8) * 255 / n4 << 8 | ((this.pixels[i] & 0xFF) * n3 >> 8) * 255 / n4);
                }
                break;
            }
            case 16: {
                final int n5 = (int)(n2 * 255.0f);
                for (int j = 0; j < this.pixels.length; ++j) {
                    this.pixels[j] = ((this.pixels[j] & 0xFF000000) | ((Math.max((this.pixels[j] & 0xFF0000) >> 16, Math.max((this.pixels[j] & 0xFF00) >> 8, this.pixels[j] & 0xFF)) < n5) ? 0 : 16777215));
                }
                break;
            }
            case 17: {
                throw new RuntimeException("Use filter(ERODE) instead of filter(ERODE, param)");
            }
            case 18: {
                throw new RuntimeException("Use filter(DILATE) instead of filter(DILATE, param)");
            }
        }
        this.updatePixels();
    }
    
    protected void buildBlurKernel(final float n) {
        final int n2 = (int)(n * 3.5f);
        final int blurRadius = (n2 < 1) ? 1 : ((n2 < 248) ? n2 : 248);
        if (this.blurRadius != blurRadius) {
            this.blurRadius = blurRadius;
            this.blurKernelSize = 1 + this.blurRadius << 1;
            this.blurKernel = new int[this.blurKernelSize];
            this.blurMult = new int[this.blurKernelSize][256];
            int i = 1;
            int n3 = blurRadius - 1;
            while (i < blurRadius) {
                final int n4;
                this.blurKernel[blurRadius + i] = (this.blurKernel[n3] = (n4 = n3 * n3));
                final int[] array = this.blurMult[blurRadius + i];
                final int[] array2 = this.blurMult[n3--];
                for (int j = 0; j < 256; ++j) {
                    array[j] = (array2[j] = n4 * j);
                }
                ++i;
            }
            final int[] blurKernel = this.blurKernel;
            final int n5 = blurRadius;
            final int n6 = blurRadius * blurRadius;
            blurKernel[n5] = n6;
            final int n7 = n6;
            final int[] array3 = this.blurMult[blurRadius];
            for (int k = 0; k < 256; ++k) {
                array3[k] = n7 * k;
            }
        }
    }
    
    protected void blurAlpha(final float n) {
        final int[] array = new int[this.pixels.length];
        int n2 = 0;
        this.buildBlurKernel(n);
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                int n4;
                int n3 = n4 = 0;
                int n5 = j - this.blurRadius;
                int n6;
                if (n5 < 0) {
                    n6 = -n5;
                    n5 = 0;
                }
                else {
                    if (n5 >= this.width) {
                        break;
                    }
                    n6 = 0;
                }
                for (int n7 = n6; n7 < this.blurKernelSize && n5 < this.width; ++n5, ++n7) {
                    n4 += this.blurMult[n7][this.pixels[n5 + n2] & 0xFF];
                    n3 += this.blurKernel[n7];
                }
                array[n2 + j] = n4 / n3;
            }
            n2 += this.width;
        }
        int n8 = 0;
        int n9 = -this.blurRadius;
        int n10 = n9 * this.width;
        for (int k = 0; k < this.height; ++k) {
            for (int l = 0; l < this.width; ++l) {
                int n12;
                int n11 = n12 = 0;
                int n14;
                int n13;
                int n15;
                if (n9 < 0) {
                    n13 = (n14 = -n9);
                    n15 = l;
                }
                else {
                    if (n9 >= this.height) {
                        break;
                    }
                    n14 = 0;
                    n13 = n9;
                    n15 = l + n10;
                }
                for (int n16 = n14; n16 < this.blurKernelSize && n13 < this.height; ++n13, n15 += this.width, ++n16) {
                    n12 += this.blurMult[n16][array[n15]];
                    n11 += this.blurKernel[n16];
                }
                this.pixels[l + n8] = n12 / n11;
            }
            n8 += this.width;
            n10 += this.width;
            ++n9;
        }
    }
    
    protected void blurRGB(final float n) {
        final int[] array = new int[this.pixels.length];
        final int[] array2 = new int[this.pixels.length];
        final int[] array3 = new int[this.pixels.length];
        int n2 = 0;
        this.buildBlurKernel(n);
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                int n6;
                int n5;
                int n4;
                int n3 = n4 = (n5 = (n6 = 0));
                int n7 = j - this.blurRadius;
                int n8;
                if (n7 < 0) {
                    n8 = -n7;
                    n7 = 0;
                }
                else {
                    if (n7 >= this.width) {
                        break;
                    }
                    n8 = 0;
                }
                for (int n9 = n8; n9 < this.blurKernelSize && n7 < this.width; ++n7, ++n9) {
                    final int n10 = this.pixels[n7 + n2];
                    final int[] array4 = this.blurMult[n9];
                    n5 += array4[(n10 & 0xFF0000) >> 16];
                    n3 += array4[(n10 & 0xFF00) >> 8];
                    n4 += array4[n10 & 0xFF];
                    n6 += this.blurKernel[n9];
                }
                final int n11 = n2 + j;
                array[n11] = n5 / n6;
                array2[n11] = n3 / n6;
                array3[n11] = n4 / n6;
            }
            n2 += this.width;
        }
        int n12 = 0;
        int n13 = -this.blurRadius;
        int n14 = n13 * this.width;
        for (int k = 0; k < this.height; ++k) {
            for (int l = 0; l < this.width; ++l) {
                int n18;
                int n17;
                int n16;
                int n15 = n16 = (n17 = (n18 = 0));
                int n20;
                int n19;
                int n21;
                if (n13 < 0) {
                    n19 = (n20 = -n13);
                    n21 = l;
                }
                else {
                    if (n13 >= this.height) {
                        break;
                    }
                    n20 = 0;
                    n19 = n13;
                    n21 = l + n14;
                }
                for (int n22 = n20; n22 < this.blurKernelSize && n19 < this.height; ++n19, n21 += this.width, ++n22) {
                    final int[] array5 = this.blurMult[n22];
                    n17 += array5[array[n21]];
                    n15 += array5[array2[n21]];
                    n16 += array5[array3[n21]];
                    n18 += this.blurKernel[n22];
                }
                this.pixels[l + n12] = (0xFF000000 | n17 / n18 << 16 | n15 / n18 << 8 | n16 / n18);
            }
            n12 += this.width;
            n14 += this.width;
            ++n13;
        }
    }
    
    protected void blurARGB(final float n) {
        final int length = this.pixels.length;
        final int[] array = new int[length];
        final int[] array2 = new int[length];
        final int[] array3 = new int[length];
        final int[] array4 = new int[length];
        int n2 = 0;
        this.buildBlurKernel(n);
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                int n7;
                int n6;
                int n5;
                int n4;
                int n3 = n4 = (n5 = (n6 = (n7 = 0)));
                int n8 = j - this.blurRadius;
                int n9;
                if (n8 < 0) {
                    n9 = -n8;
                    n8 = 0;
                }
                else {
                    if (n8 >= this.width) {
                        break;
                    }
                    n9 = 0;
                }
                for (int n10 = n9; n10 < this.blurKernelSize && n8 < this.width; ++n8, ++n10) {
                    final int n11 = this.pixels[n8 + n2];
                    final int[] array5 = this.blurMult[n10];
                    n6 += array5[(n11 & 0xFF000000) >>> 24];
                    n5 += array5[(n11 & 0xFF0000) >> 16];
                    n3 += array5[(n11 & 0xFF00) >> 8];
                    n4 += array5[n11 & 0xFF];
                    n7 += this.blurKernel[n10];
                }
                final int n12 = n2 + j;
                array4[n12] = n6 / n7;
                array[n12] = n5 / n7;
                array2[n12] = n3 / n7;
                array3[n12] = n4 / n7;
            }
            n2 += this.width;
        }
        int n13 = 0;
        int n14 = -this.blurRadius;
        int n15 = n14 * this.width;
        for (int k = 0; k < this.height; ++k) {
            for (int l = 0; l < this.width; ++l) {
                int n20;
                int n19;
                int n18;
                int n17;
                int n16 = n17 = (n18 = (n19 = (n20 = 0)));
                int n22;
                int n21;
                int n23;
                if (n14 < 0) {
                    n21 = (n22 = -n14);
                    n23 = l;
                }
                else {
                    if (n14 >= this.height) {
                        break;
                    }
                    n22 = 0;
                    n21 = n14;
                    n23 = l + n15;
                }
                for (int n24 = n22; n24 < this.blurKernelSize && n21 < this.height; ++n21, n23 += this.width, ++n24) {
                    final int[] array6 = this.blurMult[n24];
                    n19 += array6[array4[n23]];
                    n18 += array6[array[n23]];
                    n16 += array6[array2[n23]];
                    n17 += array6[array3[n23]];
                    n20 += this.blurKernel[n24];
                }
                this.pixels[l + n13] = (n19 / n20 << 24 | n18 / n20 << 16 | n16 / n20 << 8 | n17 / n20);
            }
            n13 += this.width;
            n15 += this.width;
            ++n14;
        }
    }
    
    protected void dilate(final boolean b) {
        int i = 0;
        final int length = this.pixels.length;
        final int[] array = new int[length];
        if (!b) {
            while (i < length) {
                final int n = i;
                int n3;
                for (int n2 = i + this.width; i < n2; array[i++] = n3) {
                    final int n4;
                    n3 = (n4 = this.pixels[i]);
                    int n5 = i - 1;
                    int n6 = i + 1;
                    int n7 = i - this.width;
                    int n8 = i + this.width;
                    if (n5 < n) {
                        n5 = i;
                    }
                    if (n6 >= n2) {
                        n6 = i;
                    }
                    if (n7 < 0) {
                        n7 = i;
                    }
                    if (n8 >= length) {
                        n8 = i;
                    }
                    final int n9 = this.pixels[n7];
                    final int n10 = this.pixels[n5];
                    final int n11 = this.pixels[n8];
                    final int n12 = this.pixels[n6];
                    int n13 = 77 * (n4 >> 16 & 0xFF) + 151 * (n4 >> 8 & 0xFF) + 28 * (n4 & 0xFF);
                    final int n14 = 77 * (n10 >> 16 & 0xFF) + 151 * (n10 >> 8 & 0xFF) + 28 * (n10 & 0xFF);
                    final int n15 = 77 * (n12 >> 16 & 0xFF) + 151 * (n12 >> 8 & 0xFF) + 28 * (n12 & 0xFF);
                    final int n16 = 77 * (n9 >> 16 & 0xFF) + 151 * (n9 >> 8 & 0xFF) + 28 * (n9 & 0xFF);
                    final int n17 = 77 * (n11 >> 16 & 0xFF) + 151 * (n11 >> 8 & 0xFF) + 28 * (n11 & 0xFF);
                    if (n14 > n13) {
                        n3 = n10;
                        n13 = n14;
                    }
                    if (n15 > n13) {
                        n3 = n12;
                        n13 = n15;
                    }
                    if (n16 > n13) {
                        n3 = n9;
                        n13 = n16;
                    }
                    if (n17 > n13) {
                        n3 = n11;
                    }
                }
            }
        }
        else {
            while (i < length) {
                final int n18 = i;
                int n20;
                for (int n19 = i + this.width; i < n19; array[i++] = n20) {
                    final int n21;
                    n20 = (n21 = this.pixels[i]);
                    int n22 = i - 1;
                    int n23 = i + 1;
                    int n24 = i - this.width;
                    int n25 = i + this.width;
                    if (n22 < n18) {
                        n22 = i;
                    }
                    if (n23 >= n19) {
                        n23 = i;
                    }
                    if (n24 < 0) {
                        n24 = i;
                    }
                    if (n25 >= length) {
                        n25 = i;
                    }
                    final int n26 = this.pixels[n24];
                    final int n27 = this.pixels[n22];
                    final int n28 = this.pixels[n25];
                    final int n29 = this.pixels[n23];
                    int n30 = 77 * (n21 >> 16 & 0xFF) + 151 * (n21 >> 8 & 0xFF) + 28 * (n21 & 0xFF);
                    final int n31 = 77 * (n27 >> 16 & 0xFF) + 151 * (n27 >> 8 & 0xFF) + 28 * (n27 & 0xFF);
                    final int n32 = 77 * (n29 >> 16 & 0xFF) + 151 * (n29 >> 8 & 0xFF) + 28 * (n29 & 0xFF);
                    final int n33 = 77 * (n26 >> 16 & 0xFF) + 151 * (n26 >> 8 & 0xFF) + 28 * (n26 & 0xFF);
                    final int n34 = 77 * (n28 >> 16 & 0xFF) + 151 * (n28 >> 8 & 0xFF) + 28 * (n28 & 0xFF);
                    if (n31 < n30) {
                        n20 = n27;
                        n30 = n31;
                    }
                    if (n32 < n30) {
                        n20 = n29;
                        n30 = n32;
                    }
                    if (n33 < n30) {
                        n20 = n26;
                        n30 = n33;
                    }
                    if (n34 < n30) {
                        n20 = n28;
                    }
                }
            }
        }
        System.arraycopy(array, 0, this.pixels, 0, length);
    }
    
    public void copy(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.blend(this, n, n2, n3, n4, n5, n6, n7, n8, 0);
    }
    
    public void copy(final PImage pImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        this.blend(pImage, n, n2, n3, n4, n5, n6, n7, n8, 0);
    }
    
    public static int blendColor(final int n, final int n2, final int n3) {
        switch (n3) {
            case 0: {
                return n2;
            }
            case 1: {
                return blend_blend(n, n2);
            }
            case 2: {
                return blend_add_pin(n, n2);
            }
            case 4: {
                return blend_sub_pin(n, n2);
            }
            case 8: {
                return blend_lightest(n, n2);
            }
            case 16: {
                return blend_darkest(n, n2);
            }
            case 32: {
                return blend_difference(n, n2);
            }
            case 64: {
                return blend_exclusion(n, n2);
            }
            case 128: {
                return blend_multiply(n, n2);
            }
            case 256: {
                return blend_screen(n, n2);
            }
            case 1024: {
                return blend_hard_light(n, n2);
            }
            case 2048: {
                return blend_soft_light(n, n2);
            }
            case 512: {
                return blend_overlay(n, n2);
            }
            case 4096: {
                return blend_dodge(n, n2);
            }
            case 8192: {
                return blend_burn(n, n2);
            }
            default: {
                return 0;
            }
        }
    }
    
    public void blend(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9) {
        this.blend(this, n, n2, n3, n4, n5, n6, n7, n8, n9);
    }
    
    public void blend(final PImage pImage, final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8, final int n9) {
        final int n10 = n + n3;
        final int n11 = n2 + n4;
        final int n12 = n5 + n7;
        final int n13 = n6 + n8;
        this.loadPixels();
        if (pImage == this) {
            if (this.intersect(n, n2, n10, n11, n5, n6, n12, n13)) {
                this.blit_resize(this.get(n, n2, n10 - n, n11 - n2), 0, 0, n10 - n - 1, n11 - n2 - 1, this.pixels, this.width, this.height, n5, n6, n12, n13, n9);
            }
            else {
                this.blit_resize(pImage, n, n2, n10, n11, this.pixels, this.width, this.height, n5, n6, n12, n13, n9);
            }
        }
        else {
            pImage.loadPixels();
            this.blit_resize(pImage, n, n2, n10, n11, this.pixels, this.width, this.height, n5, n6, n12, n13, n9);
        }
        this.updatePixels();
    }
    
    private boolean intersect(final int n, final int n2, final int n3, final int n4, final int n5, final int n6, final int n7, final int n8) {
        final int n9 = n3 - n + 1;
        final int n10 = n4 - n2 + 1;
        int n11 = n7 - n5 + 1;
        int n12 = n8 - n6 + 1;
        if (n5 < n) {
            n11 += n5 - n;
            if (n11 > n9) {
                n11 = n9;
            }
        }
        else {
            final int n13 = n9 + n - n5;
            if (n11 > n13) {
                n11 = n13;
            }
        }
        if (n6 < n2) {
            n12 += n6 - n2;
            if (n12 > n10) {
                n12 = n10;
            }
        }
        else {
            final int n14 = n10 + n2 - n6;
            if (n12 > n14) {
                n12 = n14;
            }
        }
        return n11 > 0 && n12 > 0;
    }
    
    private void blit_resize(final PImage pImage, int n, int n2, int width, int height, final int[] array, final int n3, final int n4, int n5, int n6, final int n7, final int n8, final int n9) {
        if (n < 0) {
            n = 0;
        }
        if (n2 < 0) {
            n2 = 0;
        }
        if (width > pImage.width) {
            width = pImage.width;
        }
        if (height > pImage.height) {
            height = pImage.height;
        }
        int n10 = width - n;
        int n11 = height - n2;
        int n12 = n7 - n5;
        int n13 = n8 - n6;
        final boolean b = true;
        if (!b) {
            ++n10;
            ++n11;
        }
        if (n12 <= 0 || n13 <= 0 || n10 <= 0 || n11 <= 0 || n5 >= n3 || n6 >= n4 || n >= pImage.width || n2 >= pImage.height) {
            return;
        }
        final int n14 = (int)(n10 / (float)n12 * 32768.0f);
        final int n15 = (int)(n11 / (float)n13 * 32768.0f);
        this.srcXOffset = ((n5 < 0) ? (-n5 * n14) : (n * 32768));
        this.srcYOffset = ((n6 < 0) ? (-n6 * n15) : (n2 * 32768));
        if (n5 < 0) {
            n12 += n5;
            n5 = 0;
        }
        if (n6 < 0) {
            n13 += n6;
            n6 = 0;
        }
        final int low = low(n12, n3 - n5);
        final int low2 = low(n13, n4 - n6);
        int n16 = n6 * n3 + n5;
        this.srcBuffer = pImage.pixels;
        if (b) {
            this.iw = pImage.width;
            this.iw1 = pImage.width - 1;
            this.ih1 = pImage.height - 1;
            switch (n9) {
                case 1: {
                    for (int i = 0; i < low2; ++i) {
                        this.filter_new_scanline();
                        for (int j = 0; j < low; ++j) {
                            array[n16 + j] = blend_blend(array[n16 + j], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 2: {
                    for (int k = 0; k < low2; ++k) {
                        this.filter_new_scanline();
                        for (int l = 0; l < low; ++l) {
                            array[n16 + l] = blend_add_pin(array[n16 + l], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 4: {
                    for (int n17 = 0; n17 < low2; ++n17) {
                        this.filter_new_scanline();
                        for (int n18 = 0; n18 < low; ++n18) {
                            array[n16 + n18] = blend_sub_pin(array[n16 + n18], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 8: {
                    for (int n19 = 0; n19 < low2; ++n19) {
                        this.filter_new_scanline();
                        for (int n20 = 0; n20 < low; ++n20) {
                            array[n16 + n20] = blend_lightest(array[n16 + n20], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 16: {
                    for (int n21 = 0; n21 < low2; ++n21) {
                        this.filter_new_scanline();
                        for (int n22 = 0; n22 < low; ++n22) {
                            array[n16 + n22] = blend_darkest(array[n16 + n22], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 0: {
                    for (int n23 = 0; n23 < low2; ++n23) {
                        this.filter_new_scanline();
                        for (int n24 = 0; n24 < low; ++n24) {
                            array[n16 + n24] = this.filter_bilinear();
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 32: {
                    for (int n25 = 0; n25 < low2; ++n25) {
                        this.filter_new_scanline();
                        for (int n26 = 0; n26 < low; ++n26) {
                            array[n16 + n26] = blend_difference(array[n16 + n26], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 64: {
                    for (int n27 = 0; n27 < low2; ++n27) {
                        this.filter_new_scanline();
                        for (int n28 = 0; n28 < low; ++n28) {
                            array[n16 + n28] = blend_exclusion(array[n16 + n28], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 128: {
                    for (int n29 = 0; n29 < low2; ++n29) {
                        this.filter_new_scanline();
                        for (int n30 = 0; n30 < low; ++n30) {
                            array[n16 + n30] = blend_multiply(array[n16 + n30], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 256: {
                    for (int n31 = 0; n31 < low2; ++n31) {
                        this.filter_new_scanline();
                        for (int n32 = 0; n32 < low; ++n32) {
                            array[n16 + n32] = blend_screen(array[n16 + n32], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 512: {
                    for (int n33 = 0; n33 < low2; ++n33) {
                        this.filter_new_scanline();
                        for (int n34 = 0; n34 < low; ++n34) {
                            array[n16 + n34] = blend_overlay(array[n16 + n34], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 1024: {
                    for (int n35 = 0; n35 < low2; ++n35) {
                        this.filter_new_scanline();
                        for (int n36 = 0; n36 < low; ++n36) {
                            array[n16 + n36] = blend_hard_light(array[n16 + n36], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 2048: {
                    for (int n37 = 0; n37 < low2; ++n37) {
                        this.filter_new_scanline();
                        for (int n38 = 0; n38 < low; ++n38) {
                            array[n16 + n38] = blend_soft_light(array[n16 + n38], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 4096: {
                    for (int n39 = 0; n39 < low2; ++n39) {
                        this.filter_new_scanline();
                        for (int n40 = 0; n40 < low; ++n40) {
                            array[n16 + n40] = blend_dodge(array[n16 + n40], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 8192: {
                    for (int n41 = 0; n41 < low2; ++n41) {
                        this.filter_new_scanline();
                        for (int n42 = 0; n42 < low; ++n42) {
                            array[n16 + n42] = blend_burn(array[n16 + n42], this.filter_bilinear());
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
            }
        }
        else {
            switch (n9) {
                case 1: {
                    for (int n43 = 0; n43 < low2; ++n43) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n44 = 0; n44 < low; ++n44) {
                            array[n16 + n44] = blend_blend(array[n16 + n44], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 2: {
                    for (int n45 = 0; n45 < low2; ++n45) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n46 = 0; n46 < low; ++n46) {
                            array[n16 + n46] = blend_add_pin(array[n16 + n46], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 4: {
                    for (int n47 = 0; n47 < low2; ++n47) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n48 = 0; n48 < low; ++n48) {
                            array[n16 + n48] = blend_sub_pin(array[n16 + n48], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 8: {
                    for (int n49 = 0; n49 < low2; ++n49) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n50 = 0; n50 < low; ++n50) {
                            array[n16 + n50] = blend_lightest(array[n16 + n50], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 16: {
                    for (int n51 = 0; n51 < low2; ++n51) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n52 = 0; n52 < low; ++n52) {
                            array[n16 + n52] = blend_darkest(array[n16 + n52], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 0: {
                    for (int n53 = 0; n53 < low2; ++n53) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n54 = 0; n54 < low; ++n54) {
                            array[n16 + n54] = this.srcBuffer[this.sY + (this.sX >> 15)];
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 32: {
                    for (int n55 = 0; n55 < low2; ++n55) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n56 = 0; n56 < low; ++n56) {
                            array[n16 + n56] = blend_difference(array[n16 + n56], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 64: {
                    for (int n57 = 0; n57 < low2; ++n57) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n58 = 0; n58 < low; ++n58) {
                            array[n16 + n58] = blend_exclusion(array[n16 + n58], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 128: {
                    for (int n59 = 0; n59 < low2; ++n59) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n60 = 0; n60 < low; ++n60) {
                            array[n16 + n60] = blend_multiply(array[n16 + n60], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 256: {
                    for (int n61 = 0; n61 < low2; ++n61) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n62 = 0; n62 < low; ++n62) {
                            array[n16 + n62] = blend_screen(array[n16 + n62], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 512: {
                    for (int n63 = 0; n63 < low2; ++n63) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n64 = 0; n64 < low; ++n64) {
                            array[n16 + n64] = blend_overlay(array[n16 + n64], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 1024: {
                    for (int n65 = 0; n65 < low2; ++n65) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n66 = 0; n66 < low; ++n66) {
                            array[n16 + n66] = blend_hard_light(array[n16 + n66], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 2048: {
                    for (int n67 = 0; n67 < low2; ++n67) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n68 = 0; n68 < low; ++n68) {
                            array[n16 + n68] = blend_soft_light(array[n16 + n68], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 4096: {
                    for (int n69 = 0; n69 < low2; ++n69) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n70 = 0; n70 < low; ++n70) {
                            array[n16 + n70] = blend_dodge(array[n16 + n70], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
                case 8192: {
                    for (int n71 = 0; n71 < low2; ++n71) {
                        this.sX = this.srcXOffset;
                        this.sY = (this.srcYOffset >> 15) * pImage.width;
                        for (int n72 = 0; n72 < low; ++n72) {
                            array[n16 + n72] = blend_burn(array[n16 + n72], this.srcBuffer[this.sY + (this.sX >> 15)]);
                            this.sX += n14;
                        }
                        n16 += n3;
                        this.srcYOffset += n15;
                    }
                    break;
                }
            }
        }
    }
    
    private void filter_new_scanline() {
        this.sX = this.srcXOffset;
        this.fracV = (this.srcYOffset & 0x7FFF);
        this.ifV = 32767 - this.fracV;
        this.v1 = (this.srcYOffset >> 15) * this.iw;
        this.v2 = low((this.srcYOffset >> 15) + 1, this.ih1) * this.iw;
    }
    
    private int filter_bilinear() {
        this.fracU = (this.sX & 0x7FFF);
        this.ifU = 32767 - this.fracU;
        this.ul = this.ifU * this.ifV >> 15;
        this.ll = this.ifU * this.fracV >> 15;
        this.ur = this.fracU * this.ifV >> 15;
        this.lr = this.fracU * this.fracV >> 15;
        this.u1 = this.sX >> 15;
        this.u2 = low(this.u1 + 1, this.iw1);
        this.cUL = this.srcBuffer[this.v1 + this.u1];
        this.cUR = this.srcBuffer[this.v1 + this.u2];
        this.cLL = this.srcBuffer[this.v2 + this.u1];
        this.cLR = this.srcBuffer[this.v2 + this.u2];
        this.r = (this.ul * ((this.cUL & 0xFF0000) >> 16) + this.ll * ((this.cLL & 0xFF0000) >> 16) + this.ur * ((this.cUR & 0xFF0000) >> 16) + this.lr * ((this.cLR & 0xFF0000) >> 16) << 1 & 0xFF0000);
        this.g = (this.ul * (this.cUL & 0xFF00) + this.ll * (this.cLL & 0xFF00) + this.ur * (this.cUR & 0xFF00) + this.lr * (this.cLR & 0xFF00) >>> 15 & 0xFF00);
        this.b = this.ul * (this.cUL & 0xFF) + this.ll * (this.cLL & 0xFF) + this.ur * (this.cUR & 0xFF) + this.lr * (this.cLR & 0xFF) >>> 15;
        this.a = (this.ul * ((this.cUL & 0xFF000000) >>> 24) + this.ll * ((this.cLL & 0xFF000000) >>> 24) + this.ur * ((this.cUR & 0xFF000000) >>> 24) + this.lr * ((this.cLR & 0xFF000000) >>> 24) << 9 & 0xFF000000);
        return this.a | this.r | this.g | this.b;
    }
    
    private static int low(final int n, final int n2) {
        return (n < n2) ? n : n2;
    }
    
    private static int high(final int n, final int n2) {
        return (n > n2) ? n : n2;
    }
    
    private static int peg(final int n) {
        return (n < 0) ? 0 : ((n > 255) ? 255 : n);
    }
    
    private static int mix(final int n, final int n2, final int n3) {
        return n + ((n2 - n) * n3 >> 8);
    }
    
    private static int blend_blend(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | (mix(n & 0xFF0000, n2 & 0xFF0000, n3) & 0xFF0000) | (mix(n & 0xFF00, n2 & 0xFF00, n3) & 0xFF00) | mix(n & 0xFF, n2 & 0xFF, n3);
    }
    
    private static int blend_add_pin(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | (low((n & 0xFF0000) + ((n2 & 0xFF0000) >> 8) * n3, 16711680) & 0xFF0000) | (low((n & 0xFF00) + ((n2 & 0xFF00) >> 8) * n3, 65280) & 0xFF00) | low((n & 0xFF) + ((n2 & 0xFF) * n3 >> 8), 255);
    }
    
    private static int blend_sub_pin(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | (high((n & 0xFF0000) - ((n2 & 0xFF0000) >> 8) * n3, 65280) & 0xFF0000) | (high((n & 0xFF00) - ((n2 & 0xFF00) >> 8) * n3, 255) & 0xFF00) | high((n & 0xFF) - ((n2 & 0xFF) * n3 >> 8), 0);
    }
    
    private static int blend_lightest(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | (high(n & 0xFF0000, ((n2 & 0xFF0000) >> 8) * n3) & 0xFF0000) | (high(n & 0xFF00, ((n2 & 0xFF00) >> 8) * n3) & 0xFF00) | high(n & 0xFF, (n2 & 0xFF) * n3 >> 8);
    }
    
    private static int blend_darkest(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | (mix(n & 0xFF0000, low(n & 0xFF0000, ((n2 & 0xFF0000) >> 8) * n3), n3) & 0xFF0000) | (mix(n & 0xFF00, low(n & 0xFF00, ((n2 & 0xFF00) >> 8) * n3), n3) & 0xFF00) | mix(n & 0xFF, low(n & 0xFF, (n2 & 0xFF) * n3 >> 8), n3);
    }
    
    private static int blend_difference(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((((n4 > n7) ? (n4 - n7) : (n7 - n4)) - n4) * n3 >> 8)) << 16 | peg(n5 + ((((n5 > n8) ? (n5 - n8) : (n8 - n5)) - n5) * n3 >> 8)) << 8 | peg(n6 + ((((n6 > n9) ? (n6 - n9) : (n9 - n6)) - n6) * n3 >> 8));
    }
    
    private static int blend_exclusion(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((n4 + n7 - (n4 * n7 >> 7) - n4) * n3 >> 8)) << 16 | peg(n5 + ((n5 + n8 - (n5 * n8 >> 7) - n5) * n3 >> 8)) << 8 | peg(n6 + ((n6 + n9 - (n6 * n9 >> 7) - n6) * n3 >> 8));
    }
    
    private static int blend_multiply(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + (((n4 * ((n2 & 0xFF0000) >> 16) >> 8) - n4) * n3 >> 8)) << 16 | peg(n5 + (((n5 * ((n2 & 0xFF00) >> 8) >> 8) - n5) * n3 >> 8)) << 8 | peg(n6 + (((n6 * (n2 & 0xFF) >> 8) - n6) * n3 >> 8));
    }
    
    private static int blend_screen(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((255 - ((255 - n4) * (255 - ((n2 & 0xFF0000) >> 16)) >> 8) - n4) * n3 >> 8)) << 16 | peg(n5 + ((255 - ((255 - n5) * (255 - ((n2 & 0xFF00) >> 8)) >> 8) - n5) * n3 >> 8)) << 8 | peg(n6 + ((255 - ((255 - n6) * (255 - (n2 & 0xFF)) >> 8) - n6) * n3 >> 8));
    }
    
    private static int blend_overlay(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((((n4 < 128) ? (n4 * n7 >> 7) : (255 - ((255 - n4) * (255 - n7) >> 7))) - n4) * n3 >> 8)) << 16 | peg(n5 + ((((n5 < 128) ? (n5 * n8 >> 7) : (255 - ((255 - n5) * (255 - n8) >> 7))) - n5) * n3 >> 8)) << 8 | peg(n6 + ((((n6 < 128) ? (n6 * n9 >> 7) : (255 - ((255 - n6) * (255 - n9) >> 7))) - n6) * n3 >> 8));
    }
    
    private static int blend_hard_light(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((((n7 < 128) ? (n4 * n7 >> 7) : (255 - ((255 - n4) * (255 - n7) >> 7))) - n4) * n3 >> 8)) << 16 | peg(n5 + ((((n8 < 128) ? (n5 * n8 >> 7) : (255 - ((255 - n5) * (255 - n8) >> 7))) - n5) * n3 >> 8)) << 8 | peg(n6 + ((((n9 < 128) ? (n6 * n9 >> 7) : (255 - ((255 - n6) * (255 - n9) >> 7))) - n6) * n3 >> 8));
    }
    
    private static int blend_soft_light(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + (((n4 * n7 >> 7) + (n4 * n4 >> 8) - (n4 * n4 * n7 >> 15) - n4) * n3 >> 8)) << 16 | peg(n5 + (((n5 * n8 >> 7) + (n5 * n5 >> 8) - (n5 * n5 * n8 >> 15) - n5) * n3 >> 8)) << 8 | peg(n6 + (((n6 * n9 >> 7) + (n6 * n6 >> 8) - (n6 * n6 * n9 >> 15) - n6) * n3 >> 8));
    }
    
    private static int blend_dodge(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((((n7 == 255) ? 255 : peg((n4 << 8) / (255 - n7))) - n4) * n3 >> 8)) << 16 | peg(n5 + ((((n8 == 255) ? 255 : peg((n5 << 8) / (255 - n8))) - n5) * n3 >> 8)) << 8 | peg(n6 + ((((n9 == 255) ? 255 : peg((n6 << 8) / (255 - n9))) - n6) * n3 >> 8));
    }
    
    private static int blend_burn(final int n, final int n2) {
        final int n3 = (n2 & 0xFF000000) >>> 24;
        final int n4 = (n & 0xFF0000) >> 16;
        final int n5 = (n & 0xFF00) >> 8;
        final int n6 = n & 0xFF;
        final int n7 = (n2 & 0xFF0000) >> 16;
        final int n8 = (n2 & 0xFF00) >> 8;
        final int n9 = n2 & 0xFF;
        return low(((n & 0xFF000000) >>> 24) + n3, 255) << 24 | peg(n4 + ((((n7 == 0) ? 0 : (255 - peg((255 - n4 << 8) / n7))) - n4) * n3 >> 8)) << 16 | peg(n5 + ((((n8 == 0) ? 0 : (255 - peg((255 - n5 << 8) / n8))) - n5) * n3 >> 8)) << 8 | peg(n6 + ((((n9 == 0) ? 0 : (255 - peg((255 - n6 << 8) / n9))) - n6) * n3 >> 8));
    }
    
    protected static PImage loadTIFF(final byte[] array) {
        if (array[42] != array[102] || array[43] != array[103]) {
            System.err.println("Error: Processing can only read its own TIFF files.");
            return null;
        }
        final int i = (array[30] & 0xFF) << 8 | (array[31] & 0xFF);
        final int j = (array[42] & 0xFF) << 8 | (array[43] & 0xFF);
        final int n = (array[114] & 0xFF) << 24 | (array[115] & 0xFF) << 16 | (array[116] & 0xFF) << 8 | (array[117] & 0xFF);
        if (n != i * j * 3) {
            System.err.println("Error: Processing can only read its own TIFF files. (" + i + ", " + j + ")");
            return null;
        }
        for (int k = 0; k < PImage.TIFF_HEADER.length; ++k) {
            if (k != 30 && k != 31 && k != 42 && k != 43 && k != 102 && k != 103 && k != 114 && k != 115 && k != 116) {
                if (k != 117) {
                    if (array[k] != PImage.TIFF_HEADER[k]) {
                        System.err.println("Error: Processing can only read its own TIFF files. (" + k + ")");
                        return null;
                    }
                }
            }
        }
        final PImage pImage = new PImage(i, j, 1);
        int n2 = 768;
        for (int n3 = n / 3, l = 0; l < n3; ++l) {
            pImage.pixels[l] = (0xFF000000 | (array[n2++] & 0xFF) << 16 | (array[n2++] & 0xFF) << 8 | (array[n2++] & 0xFF));
        }
        return pImage;
    }
    
    protected boolean saveTIFF(final OutputStream outputStream) {
        try {
            final byte[] b = new byte[768];
            System.arraycopy(PImage.TIFF_HEADER, 0, b, 0, PImage.TIFF_HEADER.length);
            b[30] = (byte)(this.width >> 8 & 0xFF);
            b[31] = (byte)(this.width & 0xFF);
            b[42] = (b[102] = (byte)(this.height >> 8 & 0xFF));
            b[43] = (b[103] = (byte)(this.height & 0xFF));
            final int n = this.width * this.height * 3;
            b[114] = (byte)(n >> 24 & 0xFF);
            b[115] = (byte)(n >> 16 & 0xFF);
            b[116] = (byte)(n >> 8 & 0xFF);
            b[117] = (byte)(n & 0xFF);
            outputStream.write(b);
            for (int i = 0; i < this.pixels.length; ++i) {
                outputStream.write(this.pixels[i] >> 16 & 0xFF);
                outputStream.write(this.pixels[i] >> 8 & 0xFF);
                outputStream.write(this.pixels[i] & 0xFF);
            }
            outputStream.flush();
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    protected boolean saveTGA(final OutputStream outputStream) {
        final byte[] b = new byte[18];
        if (this.format == 4) {
            b[2] = 11;
            b[16] = 8;
            b[17] = 40;
        }
        else if (this.format == 1) {
            b[2] = 10;
            b[16] = 24;
            b[17] = 32;
        }
        else {
            if (this.format != 2) {
                throw new RuntimeException("Image format not recognized inside save()");
            }
            b[2] = 10;
            b[16] = 32;
            b[17] = 40;
        }
        b[12] = (byte)(this.width & 0xFF);
        b[13] = (byte)(this.width >> 8);
        b[14] = (byte)(this.height & 0xFF);
        b[15] = (byte)(this.height >> 8);
        try {
            outputStream.write(b);
            final int n = this.height * this.width;
            int i = 0;
            final int[] array = new int[128];
            if (this.format == 4) {
                while (i < n) {
                    int n2 = 0;
                    int n3 = 1;
                    int n4 = array[0] = (this.pixels[i] & 0xFF);
                    while (i + n3 < n) {
                        if (n4 != (this.pixels[i + n3] & 0xFF) || n3 == 128) {
                            n2 = ((n3 > 1) ? 1 : 0);
                            break;
                        }
                        ++n3;
                    }
                    if (n2 != 0) {
                        outputStream.write(0x80 | n3 - 1);
                        outputStream.write(n4);
                    }
                    else {
                        n3 = 1;
                        while (i + n3 < n) {
                            final int n5 = this.pixels[i + n3] & 0xFF;
                            if ((n4 != n5 && n3 < 128) || n3 < 3) {
                                n4 = (array[n3] = n5);
                                ++n3;
                            }
                            else {
                                if (n4 == n5) {
                                    n3 -= 2;
                                    break;
                                }
                                break;
                            }
                        }
                        outputStream.write(n3 - 1);
                        for (int j = 0; j < n3; ++j) {
                            outputStream.write(array[j]);
                        }
                    }
                    i += n3;
                }
            }
            else {
                while (i < n) {
                    int n6 = 0;
                    int n7 = array[0] = this.pixels[i];
                    int n8;
                    for (n8 = 1; i + n8 < n; ++n8) {
                        if (n7 != this.pixels[i + n8] || n8 == 128) {
                            n6 = ((n8 > 1) ? 1 : 0);
                            break;
                        }
                    }
                    if (n6 != 0) {
                        outputStream.write(0x80 | n8 - 1);
                        outputStream.write(n7 & 0xFF);
                        outputStream.write(n7 >> 8 & 0xFF);
                        outputStream.write(n7 >> 16 & 0xFF);
                        if (this.format == 2) {
                            outputStream.write(n7 >>> 24 & 0xFF);
                        }
                    }
                    else {
                        n8 = 1;
                        while (i + n8 < n) {
                            if ((n7 != this.pixels[i + n8] && n8 < 128) || n8 < 3) {
                                n7 = (array[n8] = this.pixels[i + n8]);
                                ++n8;
                            }
                            else {
                                if (n7 == this.pixels[i + n8]) {
                                    n8 -= 2;
                                    break;
                                }
                                break;
                            }
                        }
                        outputStream.write(n8 - 1);
                        if (this.format == 2) {
                            for (final int n9 : array) {
                                outputStream.write(n9 & 0xFF);
                                outputStream.write(n9 >> 8 & 0xFF);
                                outputStream.write(n9 >> 16 & 0xFF);
                                outputStream.write(n9 >>> 24 & 0xFF);
                            }
                        }
                        else {
                            for (final int n10 : array) {
                                outputStream.write(n10 & 0xFF);
                                outputStream.write(n10 >> 8 & 0xFF);
                                outputStream.write(n10 >> 16 & 0xFF);
                            }
                        }
                    }
                    i += n8;
                }
            }
            outputStream.flush();
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    protected boolean saveImageIO(final String pathname) throws IOException {
        try {
            int imageType = (this.format == 2) ? 2 : 1;
            final String lowerCase = pathname.toLowerCase();
            if (lowerCase.endsWith("bmp") || lowerCase.endsWith("jpg") || lowerCase.endsWith("jpeg")) {
                imageType = 1;
            }
            final BufferedImage im = new BufferedImage(this.width, this.height, imageType);
            im.setRGB(0, 0, this.width, this.height, this.pixels, 0, this.width);
            return ImageIO.write(im, pathname.substring(pathname.lastIndexOf(46) + 1), new File(pathname));
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("image save failed.");
        }
    }
    
    public void save(String name) {
        if (this.parent != null) {
            name = this.parent.savePath(name);
        }
        else {
            PGraphics.showException("PImage.save() requires an absolute path. Use createImage(), or pass savePath() to save().");
        }
        this.loadPixels();
        boolean b;
        try {
            if (this.saveImageFormats == null) {
                this.saveImageFormats = ImageIO.getWriterFormatNames();
            }
            if (this.saveImageFormats != null) {
                int i = 0;
                while (i < this.saveImageFormats.length) {
                    if (name.endsWith("." + this.saveImageFormats[i])) {
                        if (!this.saveImageIO(name)) {
                            throw new RuntimeException("Error while saving image.");
                        }
                        return;
                    }
                    else {
                        ++i;
                    }
                }
            }
            BufferedOutputStream bufferedOutputStream;
            if (name.toLowerCase().endsWith(".tga")) {
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(name), 32768);
                b = this.saveTGA(bufferedOutputStream);
            }
            else {
                if (!name.toLowerCase().endsWith(".tif") && !name.toLowerCase().endsWith(".tiff")) {
                    name += ".tif";
                }
                bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(name), 32768);
                b = this.saveTIFF(bufferedOutputStream);
            }
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
            b = false;
        }
        if (!b) {
            throw new RuntimeException("Error while saving image.");
        }
    }
    
    static {
        PImage.TIFF_HEADER = new byte[] { 77, 77, 0, 42, 0, 0, 0, 8, 0, 9, 0, -2, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 2, 0, 3, 0, 0, 0, 3, 0, 0, 0, 122, 1, 6, 0, 3, 0, 0, 0, 1, 0, 2, 0, 0, 1, 17, 0, 4, 0, 0, 0, 1, 0, 0, 3, 0, 1, 21, 0, 3, 0, 0, 0, 1, 0, 3, 0, 0, 1, 22, 0, 3, 0, 0, 0, 1, 0, 0, 0, 0, 1, 23, 0, 4, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 8, 0, 8, 0, 8 };
    }
}
