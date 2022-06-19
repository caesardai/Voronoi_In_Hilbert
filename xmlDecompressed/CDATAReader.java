package processing.xml;

import java.io.IOException;
import java.io.Reader;

class CDATAReader extends Reader
{
    private StdXMLReader reader;
    private char savedChar;
    private boolean atEndOfData;
    
    CDATAReader(final StdXMLReader reader) {
        this.reader = reader;
        this.savedChar = '\0';
        this.atEndOfData = false;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.reader = null;
        super.finalize();
    }
    
    @Override
    public int read(final char[] array, final int n, int n2) throws IOException {
        int i = 0;
        if (this.atEndOfData) {
            return -1;
        }
        if (n + n2 > array.length) {
            n2 = array.length - n;
        }
        while (i < n2) {
            char c = this.savedChar;
            if (c == '\0') {
                c = this.reader.read();
            }
            else {
                this.savedChar = '\0';
            }
            if (c == ']') {
                final char read = this.reader.read();
                if (read == ']') {
                    final char read2 = this.reader.read();
                    if (read2 == '>') {
                        this.atEndOfData = true;
                        break;
                    }
                    this.savedChar = read;
                    this.reader.unread(read2);
                }
                else {
                    this.reader.unread(read);
                }
            }
            array[i] = c;
            ++i;
        }
        if (i == 0) {
            i = -1;
        }
        return i;
    }
    
    @Override
    public void close() throws IOException {
        while (!this.atEndOfData) {
            char c = this.savedChar;
            if (c == '\0') {
                c = this.reader.read();
            }
            else {
                this.savedChar = '\0';
            }
            if (c == ']') {
                final char read = this.reader.read();
                if (read == ']') {
                    final char read2 = this.reader.read();
                    if (read2 == '>') {
                        break;
                    }
                    this.savedChar = read;
                    this.reader.unread(read2);
                }
                else {
                    this.reader.unread(read);
                }
            }
        }
        this.atEndOfData = true;
    }
}