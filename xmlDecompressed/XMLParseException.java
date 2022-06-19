package processing.xml;

public class XMLParseException extends XMLException
{
    public XMLParseException(final String s) {
        super(s);
    }
    
    public XMLParseException(final String s, final int n, final String s2) {
        super(s, n, null, s2, true);
    }
}