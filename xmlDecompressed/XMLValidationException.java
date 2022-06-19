package processing.xml;

public class XMLValidationException extends XMLException
{
    public static final int MISSING_ELEMENT = 1;
    public static final int UNEXPECTED_ELEMENT = 2;
    public static final int MISSING_ATTRIBUTE = 3;
    public static final int UNEXPECTED_ATTRIBUTE = 4;
    public static final int ATTRIBUTE_WITH_INVALID_VALUE = 5;
    public static final int MISSING_PCDATA = 6;
    public static final int UNEXPECTED_PCDATA = 7;
    public static final int MISC_ERROR = 0;
    private String elementName;
    private String attributeName;
    private String attributeValue;
    
    public XMLValidationException(final int n, final String s, final int n2, final String s2, final String s3, final String s4, final String str) {
        super(s, n2, null, str + ((s2 == null) ? "" : (", element=" + s2)) + ((s3 == null) ? "" : (", attribute=" + s3)) + ((s4 == null) ? "" : (", value='" + s4 + "'")), false);
        this.elementName = s2;
        this.attributeName = s3;
        this.attributeValue = s4;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.elementName = null;
        this.attributeName = null;
        this.attributeValue = null;
        super.finalize();
    }
    
    public String getElementName() {
        return this.elementName;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public String getAttributeValue() {
        return this.attributeValue;
    }
}