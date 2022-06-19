package processing.xml;

import java.io.UnsupportedEncodingException;
import java.io.InputStreamReader;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.io.LineNumberReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;

public class StdXMLReader
{
    private Stack<StdXMLReader.StackedReader> readers;
    private StdXMLReader.StackedReader currentReader;
    
    public static StdXMLReader stringReader(final String s) {
        return new StdXMLReader(new StringReader(s));
    }
    
    public static StdXMLReader fileReader(final String s) throws FileNotFoundException, IOException {
        final StdXMLReader stdXMLReader = new StdXMLReader(new FileInputStream(s));
        stdXMLReader.setSystemID(s);
        for (int i = 0; i < stdXMLReader.readers.size(); ++i) {
            ((StdXMLReader.StackedReader)stdXMLReader.readers.elementAt(i)).systemId = stdXMLReader.currentReader.systemId;
        }
        return stdXMLReader;
    }
    
    public StdXMLReader(final String s, String string) throws MalformedURLException, FileNotFoundException, IOException {
        URL url;
        try {
            url = new URL(string);
        }
        catch (MalformedURLException ex) {
            string = "file:" + string;
            try {
                url = new URL(string);
            }
            catch (MalformedURLException ex2) {
                throw ex;
            }
        }
        this.currentReader = new StdXMLReader.StackedReader(this, (StdXMLReader.StdXMLReader$1)null);
        this.readers = new Stack<StdXMLReader.StackedReader>();
        this.currentReader.lineReader = new LineNumberReader(this.openStream(s, url.toString()));
        this.currentReader.pbReader = new PushbackReader(this.currentReader.lineReader, 2);
    }
    
    public StdXMLReader(final Reader in) {
        this.currentReader = new StdXMLReader.StackedReader(this, (StdXMLReader.StdXMLReader$1)null);
        this.readers = new Stack<StdXMLReader.StackedReader>();
        this.currentReader.lineReader = new LineNumberReader(in);
        this.currentReader.pbReader = new PushbackReader(this.currentReader.lineReader, 2);
        this.currentReader.publicId = "";
        try {
            this.currentReader.systemId = new URL("file:.");
        }
        catch (MalformedURLException ex) {}
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.currentReader.lineReader = null;
        this.currentReader.pbReader = null;
        this.currentReader.systemId = null;
        this.currentReader.publicId = null;
        this.currentReader = null;
        this.readers.clear();
        super.finalize();
    }
    
    protected String getEncoding(final String s) {
        if (!s.startsWith("<?xml")) {
            return null;
        }
        int index;
        for (int i = 5; i < s.length(); i = index + 1) {
            final StringBuffer sb = new StringBuffer();
            while (i < s.length() && s.charAt(i) <= ' ') {
                ++i;
            }
            while (i < s.length() && s.charAt(i) >= 'a' && s.charAt(i) <= 'z') {
                sb.append(s.charAt(i));
                ++i;
            }
            while (i < s.length() && s.charAt(i) <= ' ') {
                ++i;
            }
            if (i >= s.length()) {
                break;
            }
            if (s.charAt(i) != '=') {
                break;
            }
            while (i < s.length() && s.charAt(i) != '\'' && s.charAt(i) != '\"') {
                ++i;
            }
            if (i >= s.length()) {
                break;
            }
            final char char1 = s.charAt(i);
            ++i;
            index = s.indexOf(char1, i);
            if (index < 0) {
                break;
            }
            if (sb.toString().equals("encoding")) {
                return s.substring(i, index);
            }
        }
        return null;
    }
    
    protected Reader stream2reader(final InputStream in, final StringBuffer sb) throws IOException {
        final PushbackInputStream pushbackInputStream = new PushbackInputStream(in);
        final int read = pushbackInputStream.read();
        switch (read) {
            case 0:
            case 254:
            case 255: {
                pushbackInputStream.unread(read);
                return new InputStreamReader(pushbackInputStream, "UTF-16");
            }
            case 239: {
                for (int i = 0; i < 2; ++i) {
                    pushbackInputStream.read();
                }
                return new InputStreamReader(pushbackInputStream, "UTF-8");
            }
            case 60: {
                int n = pushbackInputStream.read();
                sb.append('<');
                while (n > 0 && n != 62) {
                    sb.append((char)n);
                    n = pushbackInputStream.read();
                }
                if (n > 0) {
                    sb.append((char)n);
                }
                final String encoding = this.getEncoding(sb.toString());
                if (encoding == null) {
                    return new InputStreamReader(pushbackInputStream, "UTF-8");
                }
                sb.setLength(0);
                try {
                    return new InputStreamReader(pushbackInputStream, encoding);
                }
                catch (UnsupportedEncodingException ex) {
                    return new InputStreamReader(pushbackInputStream, "UTF-8");
                }
                break;
            }
        }
        sb.append((char)read);
        return new InputStreamReader(pushbackInputStream, "UTF-8");
    }
    
    public StdXMLReader(final InputStream inputStream) throws IOException {
        final StringBuffer sb = new StringBuffer();
        final Reader stream2reader = this.stream2reader(inputStream, sb);
        this.currentReader = new StdXMLReader.StackedReader(this, (StdXMLReader.StdXMLReader$1)null);
        this.readers = new Stack<StdXMLReader.StackedReader>();
        this.currentReader.lineReader = new LineNumberReader(stream2reader);
        this.currentReader.pbReader = new PushbackReader(this.currentReader.lineReader, 2);
        this.currentReader.publicId = "";
        try {
            this.currentReader.systemId = new URL("file:.");
        }
        catch (MalformedURLException ex) {}
        this.startNewStream(new StringReader(sb.toString()));
    }
    
    public char read() throws IOException {
        int i;
        for (i = this.currentReader.pbReader.read(); i < 0; i = this.currentReader.pbReader.read()) {
            if (this.readers.empty()) {
                throw new IOException("Unexpected EOF");
            }
            this.currentReader.pbReader.close();
            this.currentReader = this.readers.pop();
        }
        return (char)i;
    }
    
    public boolean atEOFOfCurrentStream() throws IOException {
        final int read = this.currentReader.pbReader.read();
        if (read < 0) {
            return true;
        }
        this.currentReader.pbReader.unread(read);
        return false;
    }
    
    public boolean atEOF() throws IOException {
        int i;
        for (i = this.currentReader.pbReader.read(); i < 0; i = this.currentReader.pbReader.read()) {
            if (this.readers.empty()) {
                return true;
            }
            this.currentReader.pbReader.close();
            this.currentReader = this.readers.pop();
        }
        this.currentReader.pbReader.unread(i);
        return false;
    }
    
    public void unread(final char c) throws IOException {
        this.currentReader.pbReader.unread(c);
    }
    
    public Reader openStream(final String publicId, final String spec) throws MalformedURLException, FileNotFoundException, IOException {
        URL resource = new URL(this.currentReader.systemId, spec);
        if (resource.getRef() != null) {
            final String ref = resource.getRef();
            if (resource.getFile().length() > 0) {
                resource = new URL("jar:" + new URL(resource.getProtocol(), resource.getHost(), resource.getPort(), resource.getFile()) + '!' + ref);
            }
            else {
                resource = StdXMLReader.class.getResource(ref);
            }
        }
        this.currentReader.publicId = publicId;
        this.currentReader.systemId = resource;
        final StringBuffer sb = new StringBuffer();
        final Reader stream2reader = this.stream2reader(resource.openStream(), sb);
        if (sb.length() == 0) {
            return stream2reader;
        }
        final String string = sb.toString();
        final PushbackReader pushbackReader = new PushbackReader(stream2reader, string.length());
        for (int i = string.length() - 1; i >= 0; --i) {
            pushbackReader.unread(string.charAt(i));
        }
        return pushbackReader;
    }
    
    public void startNewStream(final Reader reader) {
        this.startNewStream(reader, false);
    }
    
    public void startNewStream(final Reader reader, final boolean b) {
        final StdXMLReader.StackedReader currentReader = this.currentReader;
        this.readers.push(this.currentReader);
        this.currentReader = new StdXMLReader.StackedReader(this, (StdXMLReader.StdXMLReader$1)null);
        if (b) {
            this.currentReader.lineReader = null;
            this.currentReader.pbReader = new PushbackReader(reader, 2);
        }
        else {
            this.currentReader.lineReader = new LineNumberReader(reader);
            this.currentReader.pbReader = new PushbackReader(this.currentReader.lineReader, 2);
        }
        this.currentReader.systemId = currentReader.systemId;
        this.currentReader.publicId = currentReader.publicId;
    }
    
    public int getStreamLevel() {
        return this.readers.size();
    }
    
    public int getLineNr() {
        if (this.currentReader.lineReader != null) {
            return this.currentReader.lineReader.getLineNumber() + 1;
        }
        final StdXMLReader.StackedReader stackedReader = this.readers.peek();
        if (stackedReader.lineReader == null) {
            return 0;
        }
        return stackedReader.lineReader.getLineNumber() + 1;
    }
    
    public void setSystemID(final String spec) throws MalformedURLException {
        this.currentReader.systemId = new URL(this.currentReader.systemId, spec);
    }
    
    public void setPublicID(final String publicId) {
        this.currentReader.publicId = publicId;
    }
    
    public String getSystemID() {
        return this.currentReader.systemId.toString();
    }
    
    public String getPublicID() {
        return this.currentReader.publicId;
    }
}