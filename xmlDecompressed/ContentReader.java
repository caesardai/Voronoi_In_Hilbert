package processing.xml;

import java.io.IOException;
import java.io.Reader;

class ContentReader extends Reader
{
    private StdXMLReader reader;
    private String buffer;
    private int bufferIndex;
    private XMLEntityResolver resolver;
    
    ContentReader(final StdXMLReader reader, final XMLEntityResolver resolver, final String buffer) {
        this.reader = reader;
        this.resolver = resolver;
        this.buffer = buffer;
        this.bufferIndex = 0;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.reader = null;
        this.resolver = null;
        this.buffer = null;
        super.finalize();
    }
    
    @Override
    public int read(final char[] array, final int n, int n2) throws IOException {
        try {
            int i = 0;
            final int length = this.buffer.length();
            if (n + n2 > array.length) {
                n2 = array.length - n;
            }
            while (i < n2) {
                if (this.bufferIndex >= length) {
                    final String read = XMLUtil.read(this.reader, '&');
                    char c = read.charAt(0);
                    if (c == '<') {
                        this.reader.unread(c);
                        break;
                    }
                    if (c == '&' && read.length() > 1) {
                        if (read.charAt(1) != '#') {
                            XMLUtil.processEntity(read, this.reader, this.resolver);
                            continue;
                        }
                        c = XMLUtil.processCharLiteral(read);
                    }
                    array[i] = c;
                    ++i;
                }
                else {
                    final char char1 = this.buffer.charAt(this.bufferIndex);
                    ++this.bufferIndex;
                    array[i] = char1;
                    ++i;
                }
            }
            if (i == 0) {
                i = -1;
            }
            return i;
        }
        catch (XMLParseException ex) {
            throw new IOException(ex.getMessage());
        }
    }
    
    @Override
    public void close() throws IOException {
        try {
            final int length = this.buffer.length();
            char char1;
            while (true) {
                if (this.bufferIndex >= length) {
                    final String read = XMLUtil.read(this.reader, '&');
                    char1 = read.charAt(0);
                    if (char1 == '<') {
                        break;
                    }
                    if (char1 != '&' || read.length() <= 1 || read.charAt(1) == '#') {
                        continue;
                    }
                    XMLUtil.processEntity(read, this.reader, this.resolver);
                }
                else {
                    this.buffer.charAt(this.bufferIndex);
                    ++this.bufferIndex;
                }
            }
            this.reader.unread(char1);
        }
        catch (XMLParseException ex) {
            throw new IOException(ex.getMessage());
        }
    }
}