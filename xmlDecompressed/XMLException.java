package processing.xml;

import java.io.PrintStream;
import java.io.PrintWriter;

public class XMLException extends Exception
{
    private String msg;
    private String systemID;
    private int lineNr;
    private Exception encapsulatedException;
    
    public XMLException(final String s) {
        this(null, -1, null, s, false);
    }
    
    public XMLException(final Exception ex) {
        this(null, -1, ex, "Nested Exception", false);
    }
    
    public XMLException(final String s, final int n, final Exception ex) {
        this(s, n, ex, "Nested Exception", true);
    }
    
    public XMLException(final String s, final int n, final String s2) {
        this(s, n, null, s2, true);
    }
    
    public XMLException(final String systemID, final int lineNr, final Exception encapsulatedException, final String s, final boolean b) {
        super(buildMessage(systemID, lineNr, encapsulatedException, s, b));
        this.systemID = systemID;
        this.lineNr = lineNr;
        this.encapsulatedException = encapsulatedException;
        this.msg = buildMessage(systemID, lineNr, encapsulatedException, s, b);
    }
    
    private static String buildMessage(final String str, final int i, final Exception obj, final String s, final boolean b) {
        String str2 = s;
        if (b) {
            if (str != null) {
                str2 = str2 + ", SystemID='" + str + "'";
            }
            if (i >= 0) {
                str2 = str2 + ", Line=" + i;
            }
            if (obj != null) {
                str2 = str2 + ", Exception: " + obj;
            }
        }
        return str2;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.systemID = null;
        this.encapsulatedException = null;
        super.finalize();
    }
    
    public String getSystemID() {
        return this.systemID;
    }
    
    public int getLineNr() {
        return this.lineNr;
    }
    
    public Exception getException() {
        return this.encapsulatedException;
    }
    
    @Override
    public void printStackTrace(final PrintWriter printWriter) {
        super.printStackTrace(printWriter);
        if (this.encapsulatedException != null) {
            printWriter.println("*** Nested Exception:");
            this.encapsulatedException.printStackTrace(printWriter);
        }
    }
    
    @Override
    public void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);
        if (this.encapsulatedException != null) {
            printStream.println("*** Nested Exception:");
            this.encapsulatedException.printStackTrace(printStream);
        }
    }
    
    @Override
    public void printStackTrace() {
        super.printStackTrace();
        if (this.encapsulatedException != null) {
            System.err.println("*** Nested Exception:");
            this.encapsulatedException.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return this.msg;
    }
}