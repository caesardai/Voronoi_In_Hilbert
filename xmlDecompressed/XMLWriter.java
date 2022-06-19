package processing.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.io.PrintWriter;

public class XMLWriter
{
    static final int INDENT = 2;
    static final String HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private PrintWriter writer;
    
    public XMLWriter(final Writer out) {
        if (out instanceof PrintWriter) {
            this.writer = (PrintWriter)out;
        }
        else {
            this.writer = new PrintWriter(out);
        }
    }
    
    public XMLWriter(final OutputStream out) {
        this.writer = new PrintWriter(out);
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.writer = null;
        super.finalize();
    }
    
    public void write(final XMLElement xmlElement) throws IOException {
        this.write(xmlElement, false, 0, 2, true);
    }
    
    public void write(final XMLElement xmlElement, final boolean b) throws IOException {
        this.write(xmlElement, b, 0, 2, true);
    }
    
    public void write(final XMLElement xmlElement, final boolean b, final int n) throws IOException {
        this.write(xmlElement, b, n, 2, true);
    }
    
    public void write(final XMLElement xmlElement, final boolean b, final int n, final int n2, final boolean b2) throws IOException {
        if (b) {
            for (int i = 0; i < n; ++i) {
                this.writer.print(' ');
            }
        }
        if (xmlElement.getLocalName() == null) {
            if (xmlElement.getContent() != null) {
                if (b) {
                    this.writeEncoded(xmlElement.getContent().trim());
                    this.writer.println();
                }
                else {
                    this.writeEncoded(xmlElement.getContent());
                }
            }
        }
        else {
            this.writer.print('<');
            this.writer.print(xmlElement.getName());
            for (final String str : xmlElement.listAttributes()) {
                final String string = xmlElement.getString(str, null);
                this.writer.print(" " + str + "=\"");
                this.writeEncoded(string);
                this.writer.print('\"');
            }
            if (xmlElement.getContent() != null && xmlElement.getContent().length() > 0) {
                this.writer.print('>');
                this.writeEncoded(xmlElement.getContent());
                this.writer.print("</" + xmlElement.getName() + '>');
                if (b) {
                    this.writer.println();
                }
            }
            else if (xmlElement.hasChildren() || !b2) {
                this.writer.print('>');
                if (b) {
                    this.writer.println();
                }
                for (int childCount = xmlElement.getChildCount(), k = 0; k < childCount; ++k) {
                    this.write(xmlElement.getChild(k), b, n + n2, n2, b2);
                }
                if (b) {
                    for (int l = 0; l < n; ++l) {
                        this.writer.print(' ');
                    }
                }
                this.writer.print("</" + xmlElement.getName() + ">");
                if (b) {
                    this.writer.println();
                }
            }
            else {
                this.writer.print("/>");
                if (b) {
                    this.writer.println();
                }
            }
        }
        this.writer.flush();
    }
    
    private void writeEncoded(final String s) {
        for (int i = 0; i < s.length(); ++i) {
            final char char1 = s.charAt(i);
            switch (char1) {
                case 10: {
                    this.writer.print(char1);
                    break;
                }
                case 60: {
                    this.writer.print("&lt;");
                    break;
                }
                case 62: {
                    this.writer.print("&gt;");
                    break;
                }
                case 38: {
                    this.writer.print("&amp;");
                    break;
                }
                case 39: {
                    this.writer.print("&apos;");
                    break;
                }
                case 34: {
                    this.writer.print("&quot;");
                    break;
                }
                default: {
                    if (char1 < ' ' || char1 > '~') {
                        this.writer.print("&#x");
                        this.writer.print(Integer.toString(char1, 16));
                        this.writer.print(';');
                        break;
                    }
                    this.writer.print(char1);
                    break;
                }
            }
        }
    }
}