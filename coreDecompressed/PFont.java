// 
// Decompiled by Procyon v0.5.36
// 

package processing.core;

import java.awt.GraphicsEnvironment;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Set;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.awt.Font;

public class PFont implements PConstants
{
    protected int glyphCount;
    protected PFont.Glyph[] glyphs;
    protected String name;
    protected String psname;
    protected int size;
    protected boolean smooth;
    protected int ascent;
    protected int descent;
    protected int[] ascii;
    protected boolean lazy;
    protected Font font;
    protected boolean stream;
    protected boolean subsetting;
    protected boolean fontSearched;
    protected static Font[] fonts;
    protected static HashMap<String, Font> fontDifferent;
    protected BufferedImage lazyImage;
    protected Graphics2D lazyGraphics;
    protected FontMetrics lazyMetrics;
    protected int[] lazySamples;
    protected HashMap<PGraphics, Object> cacheMap;
    static final char[] EXTRA_CHARS;
    public static char[] CHARSET;
    
    public PFont() {
    }
    
    public PFont(final Font font, final boolean b) {
        this(font, b, null);
    }
    
    public PFont(final Font font, final boolean smooth, final char[] a) {
        this.font = font;
        this.smooth = smooth;
        this.name = font.getName();
        this.psname = font.getPSName();
        this.size = font.getSize();
        this.glyphs = new PFont.Glyph[10];
        Arrays.fill(this.ascii = new int[128], -1);
        final int n = this.size * 3;
        this.lazyImage = new BufferedImage(n, n, 1);
        (this.lazyGraphics = (Graphics2D)this.lazyImage.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING, smooth ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
        this.lazyGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, smooth ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        this.lazyGraphics.setFont(font);
        this.lazyMetrics = this.lazyGraphics.getFontMetrics();
        this.lazySamples = new int[n * n];
        if (a == null) {
            this.lazy = true;
        }
        else {
            Arrays.sort(a);
            this.glyphs = new PFont.Glyph[a.length];
            this.glyphCount = 0;
            for (final char c : a) {
                if (font.canDisplay(c)) {
                    final PFont.Glyph glyph = new PFont.Glyph(this, c);
                    if (glyph.value < 128) {
                        this.ascii[glyph.value] = this.glyphCount;
                    }
                    glyph.index = this.glyphCount;
                    this.glyphs[this.glyphCount++] = glyph;
                }
            }
            if (this.glyphCount != a.length) {
                this.glyphs = (PFont.Glyph[])PApplet.subset(this.glyphs, 0, this.glyphCount);
            }
        }
        if (this.ascent == 0) {
            if (font.canDisplay('d')) {
                new PFont.Glyph(this, 'd');
            }
            else {
                this.ascent = this.lazyMetrics.getAscent();
            }
        }
        if (this.descent == 0) {
            if (font.canDisplay('p')) {
                new PFont.Glyph(this, 'p');
            }
            else {
                this.descent = this.lazyMetrics.getDescent();
            }
        }
    }
    
    public PFont(final Font font, final boolean b, final char[] array, final boolean stream) {
        this(font, b, array);
        this.stream = stream;
    }
    
    public PFont(final InputStream in) throws IOException {
        final DataInputStream dataInputStream = new DataInputStream(in);
        this.glyphCount = dataInputStream.readInt();
        final int int1 = dataInputStream.readInt();
        this.size = dataInputStream.readInt();
        dataInputStream.readInt();
        this.ascent = dataInputStream.readInt();
        this.descent = dataInputStream.readInt();
        this.glyphs = new PFont.Glyph[this.glyphCount];
        Arrays.fill(this.ascii = new int[128], -1);
        for (int i = 0; i < this.glyphCount; ++i) {
            final PFont.Glyph glyph = new PFont.Glyph(this, dataInputStream);
            if (glyph.value < 128) {
                this.ascii[glyph.value] = i;
            }
            glyph.index = i;
            this.glyphs[i] = glyph;
        }
        if (this.ascent == 0 && this.descent == 0) {
            throw new RuntimeException("Please use \"Create Font\" to re-create this font.");
        }
        final PFont.Glyph[] glyphs = this.glyphs;
        for (int length = glyphs.length, j = 0; j < length; ++j) {
            glyphs[j].readBitmap(dataInputStream);
        }
        if (int1 >= 10) {
            this.name = dataInputStream.readUTF();
            this.psname = dataInputStream.readUTF();
        }
        if (int1 == 11) {
            this.smooth = dataInputStream.readBoolean();
        }
        this.findFont();
    }
    
    void delete() {
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
    
    public void save(final OutputStream out) throws IOException {
        final DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeInt(this.glyphCount);
        if (this.name == null || this.psname == null) {
            this.name = "";
            this.psname = "";
        }
        dataOutputStream.writeInt(11);
        dataOutputStream.writeInt(this.size);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(this.ascent);
        dataOutputStream.writeInt(this.descent);
        for (int i = 0; i < this.glyphCount; ++i) {
            this.glyphs[i].writeHeader(dataOutputStream);
        }
        for (int j = 0; j < this.glyphCount; ++j) {
            this.glyphs[j].writeBitmap(dataOutputStream);
        }
        dataOutputStream.writeUTF(this.name);
        dataOutputStream.writeUTF(this.psname);
        dataOutputStream.writeBoolean(this.smooth);
        dataOutputStream.flush();
    }
    
    protected void addGlyph(final char c) {
        final PFont.Glyph glyph = new PFont.Glyph(this, c);
        if (this.glyphCount == this.glyphs.length) {
            this.glyphs = (PFont.Glyph[])PApplet.expand(this.glyphs);
        }
        if (this.glyphCount == 0) {
            glyph.index = 0;
            this.glyphs[this.glyphCount] = glyph;
            if (glyph.value < 128) {
                this.ascii[glyph.value] = 0;
            }
        }
        else if (this.glyphs[this.glyphCount - 1].value < glyph.value) {
            this.glyphs[this.glyphCount] = glyph;
            if (glyph.value < 128) {
                this.ascii[glyph.value] = this.glyphCount;
            }
        }
        else {
            int i = 0;
            while (i < this.glyphCount) {
                if (this.glyphs[i].value > c) {
                    for (int j = this.glyphCount; j > i; --j) {
                        this.glyphs[j] = this.glyphs[j - 1];
                        if (this.glyphs[j].value < 128) {
                            this.ascii[this.glyphs[j].value] = j;
                        }
                    }
                    glyph.index = i;
                    this.glyphs[i] = glyph;
                    if (c < '\u0080') {
                        this.ascii[c] = i;
                        break;
                    }
                    break;
                }
                else {
                    ++i;
                }
            }
        }
        ++this.glyphCount;
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getPostScriptName() {
        return this.psname;
    }
    
    public void setFont(final Font font) {
        this.font = font;
    }
    
    public Font getFont() {
        if (this.subsetting) {
            return null;
        }
        return this.font;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public boolean isStream() {
        return this.stream;
    }
    
    public void setSubsetting() {
        this.subsetting = true;
    }
    
    public Font findFont() {
        if (this.font == null && !this.fontSearched) {
            this.font = new Font(this.name, 0, this.size);
            if (!this.font.getPSName().equals(this.psname)) {
                this.font = new Font(this.psname, 0, this.size);
            }
            if (!this.font.getPSName().equals(this.psname)) {
                this.font = null;
            }
            this.fontSearched = true;
        }
        return this.font;
    }
    
    public PFont.Glyph getGlyph(final char c) {
        final int index = this.index(c);
        return (index == -1) ? null : this.glyphs[index];
    }
    
    protected int index(final char c) {
        if (!this.lazy) {
            return this.indexActual(c);
        }
        final int indexActual = this.indexActual(c);
        if (indexActual != -1) {
            return indexActual;
        }
        if (this.font != null && this.font.canDisplay(c)) {
            this.addGlyph(c);
            return this.indexActual(c);
        }
        return -1;
    }
    
    protected int indexActual(final char c) {
        if (this.glyphCount == 0) {
            return -1;
        }
        if (c < '\u0080') {
            return this.ascii[c];
        }
        return this.indexHunt(c, 0, this.glyphCount - 1);
    }
    
    protected int indexHunt(final int n, final int n2, final int n3) {
        final int n4 = (n2 + n3) / 2;
        if (n == this.glyphs[n4].value) {
            return n4;
        }
        if (n2 >= n3) {
            return -1;
        }
        if (n < this.glyphs[n4].value) {
            return this.indexHunt(n, n2, n4 - 1);
        }
        return this.indexHunt(n, n4 + 1, n3);
    }
    
    public float kern(final char c, final char c2) {
        return 0.0f;
    }
    
    public float ascent() {
        return this.ascent / (float)this.size;
    }
    
    public float descent() {
        return this.descent / (float)this.size;
    }
    
    public float width(final char c) {
        if (c == ' ') {
            return this.width('i');
        }
        final int index = this.index(c);
        if (index == -1) {
            return 0.0f;
        }
        return this.glyphs[index].setWidth / (float)this.size;
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
    
    public int getGlyphCount() {
        return this.glyphCount;
    }
    
    public PFont.Glyph getGlyph(final int n) {
        return this.glyphs[n];
    }
    
    public static String[] list() {
        loadFonts();
        final String[] array = new String[PFont.fonts.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = PFont.fonts[i].getName();
        }
        return array;
    }
    
    public static void loadFonts() {
        if (PFont.fonts == null) {
            PFont.fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts();
            if (PApplet.platform == 2) {
                PFont.fontDifferent = new HashMap<String, Font>();
                for (final Font value : PFont.fonts) {
                    PFont.fontDifferent.put(value.getName(), value);
                }
            }
        }
    }
    
    public static Font findFont(final String s) {
        loadFonts();
        if (PApplet.platform == 2) {
            final Font font = PFont.fontDifferent.get(s);
            if (font != null) {
                return font;
            }
        }
        return new Font(s, 0, 1);
    }
    
    static {
        EXTRA_CHARS = new char[] { '\u0080', '\u0081', '\u0082', '\u0083', '\u0084', '\u0085', '\u0086', '\u0087', '\u0088', '\u0089', '\u008a', '\u008b', '\u008c', '\u008d', '\u008e', '\u008f', '\u0090', '\u0091', '\u0092', '\u0093', '\u0094', '\u0095', '\u0096', '\u0097', '\u0098', '\u0099', '\u009a', '\u009b', '\u009c', '\u009d', '\u009e', '\u009f', ' ', '¡', '¢', '£', '¤', '¥', '¦', '§', '¨', '©', 'ª', '«', '¬', '\u00ad', '®', '¯', '°', '±', '´', 'µ', '¶', '·', '¸', 'º', '»', '¿', '\u00c0', '\u00c1', '\u00c2', '\u00c3', '\u00c4', '\u00c5', '\u00c6', '\u00c7', '\u00c8', '\u00c9', '\u00ca', '\u00cb', '\u00cc', '\u00cd', '\u00ce', '\u00cf', '\u00d1', '\u00d2', '\u00d3', '\u00d4', '\u00d5', '\u00d6', '\u00d7', '\u00d8', '\u00d9', '\u00da', '\u00db', '\u00dc', '\u00dd', '\u00df', '\u00e0', '\u00e1', '\u00e2', '\u00e3', '\u00e4', '\u00e5', '\u00e6', '\u00e7', '\u00e8', '\u00e9', '\u00ea', '\u00eb', '\u00ec', '\u00ed', '\u00ee', '\u00ef', '\u00f1', '\u00f2', '\u00f3', '\u00f4', '\u00f5', '\u00f6', '\u00f7', '\u00f8', '\u00f9', '\u00fa', '\u00fb', '\u00fc', '\u00fd', '\u00ff', '\u0102', '\u0103', '\u0104', '\u0105', '\u0106', '\u0107', '\u010c', '\u010d', '\u010e', '\u010f', '\u0110', '\u0111', '\u0118', '\u0119', '\u011a', '\u011b', '\u0131', '\u0139', '\u013a', '\u013d', '\u013e', '\u0141', '\u0142', '\u0143', '\u0144', '\u0147', '\u0148', '\u0150', '\u0151', '\u0152', '\u0153', '\u0154', '\u0155', '\u0158', '\u0159', '\u015a', '\u015b', '\u015e', '\u015f', '\u0160', '\u0161', '\u0162', '\u0163', '\u0164', '\u0165', '\u016e', '\u016f', '\u0170', '\u0171', '\u0178', '\u0179', '\u017a', '\u017b', '\u017c', '\u017d', '\u017e', '\u0192', '\u02c6', '\u02c7', '\u02d8', '\u02d9', '\u02da', '\u02db', '\u02dc', '\u02dd', '\u03a9', '\u03c0', '\u2013', '\u2014', '\u2018', '\u2019', '\u201a', '\u201c', '\u201d', '\u201e', '\u2020', '\u2021', '\u2022', '\u2026', '\u2030', '\u2039', '\u203a', '\u2044', '\u20ac', '\u2122', '\u2202', '\u2206', '\u220f', '\u2211', '\u221a', '\u221e', '\u222b', '\u2248', '\u2260', '\u2264', '\u2265', '\u25ca', '\uf8ff', '\ufb01', '\ufb02' };
        PFont.CHARSET = new char[94 + PFont.EXTRA_CHARS.length];
        int n = 0;
        for (int i = 33; i <= 126; ++i) {
            PFont.CHARSET[n++] = (char)i;
        }
        for (int j = 0; j < PFont.EXTRA_CHARS.length; ++j) {
            PFont.CHARSET[n++] = PFont.EXTRA_CHARS[j];
        }
    }
}
