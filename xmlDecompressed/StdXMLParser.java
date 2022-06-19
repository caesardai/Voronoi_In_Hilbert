package processing.xml;

import java.util.Enumeration;
import java.util.Vector;
import java.io.Reader;
import java.util.Properties;

public class StdXMLParser
{
    private StdXMLBuilder builder;
    private StdXMLReader reader;
    private XMLEntityResolver entityResolver;
    private XMLValidator validator;
    
    public StdXMLParser() {
        this.builder = null;
        this.validator = null;
        this.reader = null;
        this.entityResolver = new XMLEntityResolver();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.builder = null;
        this.reader = null;
        this.entityResolver = null;
        this.validator = null;
        super.finalize();
    }
    
    public void setBuilder(final StdXMLBuilder builder) {
        this.builder = builder;
    }
    
    public StdXMLBuilder getBuilder() {
        return this.builder;
    }
    
    public void setValidator(final XMLValidator validator) {
        this.validator = validator;
    }
    
    public XMLValidator getValidator() {
        return this.validator;
    }
    
    public void setResolver(final XMLEntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public XMLEntityResolver getResolver() {
        return this.entityResolver;
    }
    
    public void setReader(final StdXMLReader reader) {
        this.reader = reader;
    }
    
    public StdXMLReader getReader() {
        return this.reader;
    }
    
    public Object parse() throws XMLException {
        try {
            this.builder.startBuilding(this.reader.getSystemID(), this.reader.getLineNr());
            this.scanData();
            return this.builder.getResult();
        }
        catch (XMLException ex) {
            throw ex;
        }
        catch (Exception ex2) {
            throw new XMLException(ex2);
        }
    }
    
    protected void scanData() throws Exception {
        while (!this.reader.atEOF() && this.builder.getResult() == null) {
            final String read = XMLUtil.read(this.reader, '&');
            final char char1 = read.charAt(0);
            if (char1 == '&') {
                XMLUtil.processEntity(read, this.reader, this.entityResolver);
            }
            else {
                switch (char1) {
                    case 60: {
                        this.scanSomeTag(false, null, new Properties());
                        continue;
                    }
                    case 9:
                    case 10:
                    case 13:
                    case 32: {
                        continue;
                    }
                    default: {
                        XMLUtil.errorInvalidInput(this.reader.getSystemID(), this.reader.getLineNr(), "`" + char1 + "' (0x" + Integer.toHexString(char1) + ')');
                        continue;
                    }
                }
            }
        }
    }
    
    protected void scanSomeTag(final boolean b, final String s, final Properties properties) throws Exception {
        final String read = XMLUtil.read(this.reader, '&');
        final char char1 = read.charAt(0);
        if (char1 == '&') {
            XMLUtil.errorUnexpectedEntity(this.reader.getSystemID(), this.reader.getLineNr(), read);
        }
        switch (char1) {
            case 63: {
                this.processPI();
                break;
            }
            case 33: {
                this.processSpecialTag(b);
                break;
            }
            default: {
                this.reader.unread(char1);
                this.processElement(s, properties);
                break;
            }
        }
    }
    
    protected void processPI() throws Exception {
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        final String scanIdentifier = XMLUtil.scanIdentifier(this.reader);
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        final PIReader piReader = new PIReader(this.reader);
        if (!scanIdentifier.equalsIgnoreCase("xml")) {
            this.builder.newProcessingInstruction(scanIdentifier, piReader);
        }
        piReader.close();
    }
    
    protected void processSpecialTag(final boolean b) throws Exception {
        final String read = XMLUtil.read(this.reader, '&');
        final char char1 = read.charAt(0);
        if (char1 == '&') {
            XMLUtil.errorUnexpectedEntity(this.reader.getSystemID(), this.reader.getLineNr(), read);
        }
        switch (char1) {
            case 91: {
                if (b) {
                    this.processCDATA();
                }
                else {
                    XMLUtil.errorUnexpectedCDATA(this.reader.getSystemID(), this.reader.getLineNr());
                }
            }
            case 68: {
                this.processDocType();
            }
            case 45: {
                XMLUtil.skipComment(this.reader);
            }
            default: {}
        }
    }
    
    protected void processCDATA() throws Exception {
        if (!XMLUtil.checkLiteral(this.reader, "CDATA[")) {
            XMLUtil.errorExpectedInput(this.reader.getSystemID(), this.reader.getLineNr(), "<![[CDATA[");
        }
        this.validator.PCDataAdded(this.reader.getSystemID(), this.reader.getLineNr());
        final CDATAReader cdataReader = new CDATAReader(this.reader);
        this.builder.addPCData(cdataReader, this.reader.getSystemID(), this.reader.getLineNr());
        cdataReader.close();
    }
    
    protected void processDocType() throws Exception {
        if (!XMLUtil.checkLiteral(this.reader, "OCTYPE")) {
            XMLUtil.errorExpectedInput(this.reader.getSystemID(), this.reader.getLineNr(), "<!DOCTYPE");
            return;
        }
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        final StringBuffer sb = new StringBuffer();
        XMLUtil.scanIdentifier(this.reader);
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        char c = this.reader.read();
        if (c == 'P') {
            XMLUtil.scanPublicID(sb, this.reader);
            XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
            c = this.reader.read();
        }
        else if (c == 'S') {
            XMLUtil.scanSystemID(this.reader);
            XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
            c = this.reader.read();
        }
        if (c == '[') {
            this.validator.parseDTD(sb.toString(), this.reader, this.entityResolver, false);
            XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
            c = this.reader.read();
        }
        if (c != '>') {
            XMLUtil.errorExpectedInput(this.reader.getSystemID(), this.reader.getLineNr(), "`>'");
        }
    }
    
    protected void processElement(final String s, final Properties properties) throws Exception {
        String s2;
        final String anObject = s2 = XMLUtil.scanIdentifier(this.reader);
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        String substring = null;
        final int index = s2.indexOf(58);
        if (index > 0) {
            substring = s2.substring(0, index);
            s2 = s2.substring(index + 1);
        }
        final Vector vector = new Vector<String>();
        final Vector<String> vector2 = new Vector<String>();
        final Vector<String> vector3 = new Vector<String>();
        this.validator.elementStarted(anObject, this.reader.getSystemID(), this.reader.getLineNr());
        char read;
        while (true) {
            read = this.reader.read();
            if (read == '/' || read == '>') {
                break;
            }
            this.reader.unread(read);
            this.processAttribute(vector, vector2, vector3);
            XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        }
        final Properties properties2 = new Properties();
        this.validator.elementAttributesProcessed(anObject, properties2, this.reader.getSystemID(), this.reader.getLineNr());
        final Enumeration<Object> keys = properties2.keys();
        while (keys.hasMoreElements()) {
            final String s3 = keys.nextElement();
            final String property = properties2.getProperty(s3);
            vector.addElement(s3);
            vector2.addElement(property);
            vector3.addElement("CDATA");
        }
        if (substring == null) {
            this.builder.startElement(s2, substring, s, this.reader.getSystemID(), this.reader.getLineNr());
        }
        else {
            this.builder.startElement(s2, substring, properties.getProperty(substring), this.reader.getSystemID(), this.reader.getLineNr());
        }
        for (int i = 0; i < vector.size(); ++i) {
            final String s4 = vector.elementAt(i);
            final String s5 = vector2.elementAt(i);
            final String s6 = vector3.elementAt(i);
            final int index2 = s4.indexOf(58);
            if (index2 > 0) {
                final String substring2 = s4.substring(0, index2);
                this.builder.addAttribute(s4.substring(index2 + 1), substring2, properties.getProperty(substring2), s5, s6);
            }
            else {
                this.builder.addAttribute(s4, null, null, s5, s6);
            }
        }
        if (substring == null) {
            this.builder.elementAttributesProcessed(s2, substring, s);
        }
        else {
            this.builder.elementAttributesProcessed(s2, substring, properties.getProperty(substring));
        }
        if (read == '/') {
            if (this.reader.read() != '>') {
                XMLUtil.errorExpectedInput(this.reader.getSystemID(), this.reader.getLineNr(), "`>'");
            }
            this.validator.elementEnded(s2, this.reader.getSystemID(), this.reader.getLineNr());
            if (substring == null) {
                this.builder.endElement(s2, substring, s);
            }
            else {
                this.builder.endElement(s2, substring, properties.getProperty(substring));
            }
            return;
        }
        final StringBuffer sb = new StringBuffer(16);
        while (true) {
            sb.setLength(0);
            String read2;
            while (true) {
                XMLUtil.skipWhitespace(this.reader, sb);
                read2 = XMLUtil.read(this.reader, '&');
                if (read2.charAt(0) != '&' || read2.charAt(1) == '#') {
                    break;
                }
                XMLUtil.processEntity(read2, this.reader, this.entityResolver);
            }
            if (read2.charAt(0) == '<') {
                final String read3 = XMLUtil.read(this.reader, '\0');
                if (read3.charAt(0) == '/') {
                    break;
                }
                this.reader.unread(read3.charAt(0));
                this.scanSomeTag(true, s, (Properties)properties.clone());
            }
            else {
                if (read2.charAt(0) == '&') {
                    sb.append(XMLUtil.processCharLiteral(read2));
                }
                else {
                    this.reader.unread(read2.charAt(0));
                }
                this.validator.PCDataAdded(this.reader.getSystemID(), this.reader.getLineNr());
                final ContentReader contentReader = new ContentReader(this.reader, this.entityResolver, sb.toString());
                this.builder.addPCData(contentReader, this.reader.getSystemID(), this.reader.getLineNr());
                contentReader.close();
            }
        }
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        final String scanIdentifier = XMLUtil.scanIdentifier(this.reader);
        if (!scanIdentifier.equals(anObject)) {
            XMLUtil.errorWrongClosingTag(this.reader.getSystemID(), this.reader.getLineNr(), s2, scanIdentifier);
        }
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        if (this.reader.read() != '>') {
            XMLUtil.errorClosingTagNotEmpty(this.reader.getSystemID(), this.reader.getLineNr());
        }
        this.validator.elementEnded(anObject, this.reader.getSystemID(), this.reader.getLineNr());
        if (substring == null) {
            this.builder.endElement(s2, substring, s);
        }
        else {
            this.builder.endElement(s2, substring, properties.getProperty(substring));
        }
    }
    
    protected void processAttribute(final Vector<String> vector, final Vector<String> vector2, final Vector<String> vector3) throws Exception {
        final String scanIdentifier = XMLUtil.scanIdentifier(this.reader);
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        if (!XMLUtil.read(this.reader, '&').equals("=")) {
            XMLUtil.errorExpectedInput(this.reader.getSystemID(), this.reader.getLineNr(), "`='");
        }
        XMLUtil.skipWhitespace(this.reader, (StringBuffer)null);
        final String scanString = XMLUtil.scanString(this.reader, '&', this.entityResolver);
        vector.addElement(scanIdentifier);
        vector2.addElement(scanString);
        vector3.addElement("CDATA");
        this.validator.attributeAdded(scanIdentifier, scanString, this.reader.getSystemID(), this.reader.getLineNr());
    }
}