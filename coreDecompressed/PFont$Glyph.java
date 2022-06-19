package processing.core;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.DataInputStream;

public class Glyph
{
    public PImage image;
    public int value;
    public int height;
    public int width;
    public int index;
    public int setWidth;
    public int topExtent;
    public int leftExtent;
    
    public Glyph() {
        this.index = -1;
    }
    
    public Glyph(final DataInputStream dataInputStream) throws IOException {
        this.index = -1;
        this.readHeader(dataInputStream);
    }
    
    protected void readHeader(final DataInputStream dataInputStream) throws IOException {
        this.value = dataInputStream.readInt();
        this.height = dataInputStream.readInt();
        this.width = dataInputStream.readInt();
        this.setWidth = dataInputStream.readInt();
        this.topExtent = dataInputStream.readInt();
        this.leftExtent = dataInputStream.readInt();
        dataInputStream.readInt();
        if (this.value == 100 && PFont.this.ascent == 0) {
            PFont.this.ascent = this.topExtent;
        }
        if (this.value == 112 && PFont.this.descent == 0) {
            PFont.this.descent = -this.topExtent + this.height;
        }
    }
    
    protected void writeHeader(final DataOutputStream dataOutputStream) throws IOException {
        dataOutputStream.writeInt(this.value);
        dataOutputStream.writeInt(this.height);
        dataOutputStream.writeInt(this.width);
        dataOutputStream.writeInt(this.setWidth);
        dataOutputStream.writeInt(this.topExtent);
        dataOutputStream.writeInt(this.leftExtent);
        dataOutputStream.writeInt(0);
    }
    
    protected void readBitmap(final DataInputStream dataInputStream) throws IOException {
        this.image = new PImage(this.width, this.height, 4);
        final byte[] b = new byte[this.width * this.height];
        dataInputStream.readFully(b);
        final int width = this.width;
        final int height = this.height;
        final int[] pixels = this.image.pixels;
        for (int i = 0; i < height; ++i) {
            for (int j = 0; j < width; ++j) {
                pixels[i * this.width + j] = (b[i * width + j] & 0xFF);
            }
        }
    }
    
    protected void writeBitmap(final DataOutputStream dataOutputStream) throws IOException {
        final int[] pixels = this.image.pixels;
        for (int i = 0; i < this.height; ++i) {
            for (int j = 0; j < this.width; ++j) {
                dataOutputStream.write(pixels[i * this.width + j] & 0xFF);
            }
        }
    }
    
    protected Glyph(final char ch) {
        final int n = PFont.this.size * 3;
        PFont.this.lazyGraphics.setColor(Color.white);
        PFont.this.lazyGraphics.fillRect(0, 0, n, n);
        PFont.this.lazyGraphics.setColor(Color.black);
        PFont.this.lazyGraphics.drawString(String.valueOf(ch), PFont.this.size, PFont.this.size * 2);
        PFont.this.lazyImage.getRaster().getDataElements(0, 0, n, n, PFont.this.lazySamples);
        int n2 = 1000;
        int n3 = 0;
        int n4 = 1000;
        int n5 = 0;
        boolean b = false;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < n; ++j) {
                if ((PFont.this.lazySamples[i * n + j] & 0xFF) != 0xFF) {
                    if (j < n2) {
                        n2 = j;
                    }
                    if (i < n4) {
                        n4 = i;
                    }
                    if (j > n3) {
                        n3 = j;
                    }
                    if (i > n5) {
                        n5 = i;
                    }
                    b = true;
                }
            }
        }
        if (!b) {
            n4 = (n2 = 0);
            n5 = (n3 = 0);
        }
        this.value = ch;
        this.height = n5 - n4 + 1;
        this.width = n3 - n2 + 1;
        this.setWidth = PFont.this.lazyMetrics.charWidth(ch);
        this.topExtent = PFont.this.size * 2 - n4;
        this.leftExtent = n2 - PFont.this.size;
        this.image = new PImage(this.width, this.height, 4);
        final int[] pixels = this.image.pixels;
        for (int k = n4; k <= n5; ++k) {
            for (int l = n2; l <= n3; ++l) {
                pixels[(k - n4) * this.width + (l - n2)] = 255 - (PFont.this.lazySamples[k * n + l] & 0xFF);
            }
        }
        if (this.value == 100 && PFont.this.ascent == 0) {
            PFont.this.ascent = this.topExtent;
        }
        if (this.value == 112 && PFont.this.descent == 0) {
            PFont.this.descent = -this.topExtent + this.height;
        }
    }
}