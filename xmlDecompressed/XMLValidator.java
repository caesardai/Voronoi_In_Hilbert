package processing.xml;

import java.util.Enumeration;
import java.io.Reader;
import java.io.StringReader;
import java.util.Stack;
import java.util.Properties;
import java.util.Hashtable;

public class XMLValidator
{
    protected XMLEntityResolver parameterEntityResolver;
    protected Hashtable<String, Properties> attributeDefaultValues;
    protected Stack<Properties> currentElements;
    
    public XMLValidator() {
        this.attributeDefaultValues = new Hashtable<String, Properties>();
        this.currentElements = new Stack<Properties>();
        this.parameterEntityResolver = new XMLEntityResolver();
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.parameterEntityResolver = null;
        this.attributeDefaultValues.clear();
        this.attributeDefaultValues = null;
        this.currentElements.clear();
        this.currentElements = null;
        super.finalize();
    }
    
    public void setParameterEntityResolver(final XMLEntityResolver parameterEntityResolver) {
        this.parameterEntityResolver = parameterEntityResolver;
    }
    
    public XMLEntityResolver getParameterEntityResolver() {
        return this.parameterEntityResolver;
    }
    
    public void parseDTD(final String s, final StdXMLReader stdXMLReader, final XMLEntityResolver xmlEntityResolver, final boolean b) throws Exception {
        XMLUtil.skipWhitespace(stdXMLReader, null);
        final int streamLevel = stdXMLReader.getStreamLevel();
        while (true) {
            final String read = XMLUtil.read(stdXMLReader, '%');
            final char char1 = read.charAt(0);
            if (char1 == '%') {
                XMLUtil.processEntity(read, stdXMLReader, this.parameterEntityResolver);
            }
            else {
                if (char1 == '<') {
                    this.processElement(stdXMLReader, xmlEntityResolver);
                }
                else {
                    if (char1 == ']') {
                        return;
                    }
                    XMLUtil.errorInvalidInput(stdXMLReader.getSystemID(), stdXMLReader.getLineNr(), read);
                }
                char read2;
                do {
                    read2 = stdXMLReader.read();
                    if (b && stdXMLReader.getStreamLevel() < streamLevel) {
                        stdXMLReader.unread(read2);
                        return;
                    }
                } while (read2 == ' ' || read2 == '\t' || read2 == '\n' || read2 == '\r');
                stdXMLReader.unread(read2);
            }
        }
    }
    
    protected void processElement(final StdXMLReader stdXMLReader, final XMLEntityResolver xmlEntityResolver) throws Exception {
        if (XMLUtil.read(stdXMLReader, '%').charAt(0) != '!') {
            XMLUtil.skipTag(stdXMLReader);
            return;
        }
        switch (XMLUtil.read(stdXMLReader, '%').charAt(0)) {
            case '-': {
                XMLUtil.skipComment(stdXMLReader);
                break;
            }
            case '[': {
                this.processConditionalSection(stdXMLReader, xmlEntityResolver);
                break;
            }
            case 'E': {
                this.processEntity(stdXMLReader, xmlEntityResolver);
                break;
            }
            case 'A': {
                this.processAttList(stdXMLReader, xmlEntityResolver);
                break;
            }
            default: {
                XMLUtil.skipTag(stdXMLReader);
                break;
            }
        }
    }
    
    protected void processConditionalSection(final StdXMLReader stdXMLReader, final XMLEntityResolver xmlEntityResolver) throws Exception {
        XMLUtil.skipWhitespace(stdXMLReader, null);
        if (XMLUtil.read(stdXMLReader, '%').charAt(0) != 'I') {
            XMLUtil.skipTag(stdXMLReader);
            return;
        }
        switch (XMLUtil.read(stdXMLReader, '%').charAt(0)) {
            case 'G': {
                this.processIgnoreSection(stdXMLReader, xmlEntityResolver);
            }
            case 'N': {
                if (!XMLUtil.checkLiteral(stdXMLReader, "CLUDE")) {
                    XMLUtil.skipTag(stdXMLReader);
                    return;
                }
                XMLUtil.skipWhitespace(stdXMLReader, null);
                if (XMLUtil.read(stdXMLReader, '%').charAt(0) != '[') {
                    XMLUtil.skipTag(stdXMLReader);
                    return;
                }
                final CDATAReader cdataReader = new CDATAReader(stdXMLReader);
                final StringBuffer sb = new StringBuffer(1024);
                while (true) {
                    final int read = cdataReader.read();
                    if (read < 0) {
                        break;
                    }
                    sb.append((char)read);
                }
                cdataReader.close();
                stdXMLReader.startNewStream(new StringReader(sb.toString()));
            }
            default: {
                XMLUtil.skipTag(stdXMLReader);
            }
        }
    }
    
    protected void processIgnoreSection(final StdXMLReader stdXMLReader, final XMLEntityResolver xmlEntityResolver) throws Exception {
        if (!XMLUtil.checkLiteral(stdXMLReader, "NORE")) {
            XMLUtil.skipTag(stdXMLReader);
            return;
        }
        XMLUtil.skipWhitespace(stdXMLReader, null);
        if (XMLUtil.read(stdXMLReader, '%').charAt(0) != '[') {
            XMLUtil.skipTag(stdXMLReader);
            return;
        }
        new CDATAReader(stdXMLReader).close();
    }
    
    protected void processAttList(final StdXMLReader stdXMLReader, final XMLEntityResolver xmlEntityResolver) throws Exception {
        if (!XMLUtil.checkLiteral(stdXMLReader, "TTLIST")) {
            XMLUtil.skipTag(stdXMLReader);
            return;
        }
        XMLUtil.skipWhitespace(stdXMLReader, null);
        String s;
        char c;
        for (s = XMLUtil.read(stdXMLReader, '%'), c = s.charAt(0); c == '%'; c = s.charAt(0)) {
            XMLUtil.processEntity(s, stdXMLReader, this.parameterEntityResolver);
            s = XMLUtil.read(stdXMLReader, '%');
        }
        stdXMLReader.unread(c);
        final String scanIdentifier = XMLUtil.scanIdentifier(stdXMLReader);
        XMLUtil.skipWhitespace(stdXMLReader, null);
        String s2;
        char c2;
        for (s2 = XMLUtil.read(stdXMLReader, '%'), c2 = s2.charAt(0); c2 == '%'; c2 = s2.charAt(0)) {
            XMLUtil.processEntity(s2, stdXMLReader, this.parameterEntityResolver);
            s2 = XMLUtil.read(stdXMLReader, '%');
        }
        final Properties value = new Properties();
        while (c2 != '>') {
            stdXMLReader.unread(c2);
            final String scanIdentifier2 = XMLUtil.scanIdentifier(stdXMLReader);
            XMLUtil.skipWhitespace(stdXMLReader, null);
            String s3;
            char c3;
            for (s3 = XMLUtil.read(stdXMLReader, '%'), c3 = s3.charAt(0); c3 == '%'; c3 = s3.charAt(0)) {
                XMLUtil.processEntity(s3, stdXMLReader, this.parameterEntityResolver);
                s3 = XMLUtil.read(stdXMLReader, '%');
            }
            if (c3 == '(') {
                while (c3 != ')') {
                    String s4;
                    for (s4 = XMLUtil.read(stdXMLReader, '%'), c3 = s4.charAt(0); c3 == '%'; c3 = s4.charAt(0)) {
                        XMLUtil.processEntity(s4, stdXMLReader, this.parameterEntityResolver);
                        s4 = XMLUtil.read(stdXMLReader, '%');
                    }
                }
            }
            else {
                stdXMLReader.unread(c3);
                XMLUtil.scanIdentifier(stdXMLReader);
            }
            XMLUtil.skipWhitespace(stdXMLReader, null);
            String s5;
            char c4;
            for (s5 = XMLUtil.read(stdXMLReader, '%'), c4 = s5.charAt(0); c4 == '%'; c4 = s5.charAt(0)) {
                XMLUtil.processEntity(s5, stdXMLReader, this.parameterEntityResolver);
                s5 = XMLUtil.read(stdXMLReader, '%');
            }
            if (c4 == '#') {
                final String scanIdentifier3 = XMLUtil.scanIdentifier(stdXMLReader);
                XMLUtil.skipWhitespace(stdXMLReader, null);
                if (!scanIdentifier3.equals("FIXED")) {
                    XMLUtil.skipWhitespace(stdXMLReader, null);
                    String s6;
                    for (s6 = XMLUtil.read(stdXMLReader, '%'), c2 = s6.charAt(0); c2 == '%'; c2 = s6.charAt(0)) {
                        XMLUtil.processEntity(s6, stdXMLReader, this.parameterEntityResolver);
                        s6 = XMLUtil.read(stdXMLReader, '%');
                    }
                    continue;
                }
            }
            else {
                stdXMLReader.unread(c4);
            }
            value.put(scanIdentifier2, XMLUtil.scanString(stdXMLReader, '%', this.parameterEntityResolver));
            XMLUtil.skipWhitespace(stdXMLReader, null);
            String s7;
            for (s7 = XMLUtil.read(stdXMLReader, '%'), c2 = s7.charAt(0); c2 == '%'; c2 = s7.charAt(0)) {
                XMLUtil.processEntity(s7, stdXMLReader, this.parameterEntityResolver);
                s7 = XMLUtil.read(stdXMLReader, '%');
            }
        }
        if (!value.isEmpty()) {
            this.attributeDefaultValues.put(scanIdentifier, value);
        }
    }
    
    protected void processEntity(final StdXMLReader stdXMLReader, XMLEntityResolver parameterEntityResolver) throws Exception {
        if (!XMLUtil.checkLiteral(stdXMLReader, "NTITY")) {
            XMLUtil.skipTag(stdXMLReader);
            return;
        }
        XMLUtil.skipWhitespace(stdXMLReader, null);
        final char char1 = XMLUtil.readChar(stdXMLReader, '\0');
        if (char1 == '%') {
            XMLUtil.skipWhitespace(stdXMLReader, null);
            parameterEntityResolver = this.parameterEntityResolver;
        }
        else {
            stdXMLReader.unread(char1);
        }
        final String scanIdentifier = XMLUtil.scanIdentifier(stdXMLReader);
        XMLUtil.skipWhitespace(stdXMLReader, null);
        final char char2 = XMLUtil.readChar(stdXMLReader, '%');
        String s = null;
        String scanString = null;
        switch (char2) {
            case 80: {
                if (!XMLUtil.checkLiteral(stdXMLReader, "UBLIC")) {
                    XMLUtil.skipTag(stdXMLReader);
                    return;
                }
                XMLUtil.skipWhitespace(stdXMLReader, null);
                scanString = XMLUtil.scanString(stdXMLReader, '%', this.parameterEntityResolver);
                XMLUtil.skipWhitespace(stdXMLReader, null);
                s = XMLUtil.scanString(stdXMLReader, '%', this.parameterEntityResolver);
                XMLUtil.skipWhitespace(stdXMLReader, null);
                XMLUtil.readChar(stdXMLReader, '%');
                break;
            }
            case 83: {
                if (!XMLUtil.checkLiteral(stdXMLReader, "YSTEM")) {
                    XMLUtil.skipTag(stdXMLReader);
                    return;
                }
                XMLUtil.skipWhitespace(stdXMLReader, null);
                s = XMLUtil.scanString(stdXMLReader, '%', this.parameterEntityResolver);
                XMLUtil.skipWhitespace(stdXMLReader, null);
                XMLUtil.readChar(stdXMLReader, '%');
                break;
            }
            case 34:
            case 39: {
                stdXMLReader.unread(char2);
                parameterEntityResolver.addInternalEntity(scanIdentifier, XMLUtil.scanString(stdXMLReader, '%', this.parameterEntityResolver));
                XMLUtil.skipWhitespace(stdXMLReader, null);
                XMLUtil.readChar(stdXMLReader, '%');
                break;
            }
            default: {
                XMLUtil.skipTag(stdXMLReader);
                break;
            }
        }
        if (s != null) {
            parameterEntityResolver.addExternalEntity(scanIdentifier, scanString, s);
        }
    }
    
    public void elementStarted(final String key, final String s, final int n) {
        final Properties properties = this.attributeDefaultValues.get(key);
        Properties item;
        if (properties == null) {
            item = new Properties();
        }
        else {
            item = (Properties)properties.clone();
        }
        this.currentElements.push(item);
    }
    
    public void elementEnded(final String s, final String s2, final int n) {
    }
    
    public void elementAttributesProcessed(final String s, final Properties properties, final String s2, final int n) {
        final Properties properties2 = this.currentElements.pop();
        final Enumeration<Object> keys = properties2.keys();
        while (keys.hasMoreElements()) {
            final String s3 = keys.nextElement();
            properties.put(s3, properties2.get(s3));
        }
    }
    
    public void attributeAdded(final String s, final String s2, final String s3, final int n) {
        final Properties properties = this.currentElements.peek();
        if (properties.containsKey(s)) {
            properties.remove(s);
        }
    }
    
    public void PCDataAdded(final String s, final int n) {
    }
}