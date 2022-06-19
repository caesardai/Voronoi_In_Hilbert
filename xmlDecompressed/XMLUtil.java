package processing.xml;

import java.io.Reader;
import java.io.IOException;

class XMLUtil
{
    static void skipComment(final StdXMLReader stdXMLReader) throws IOException, XMLParseException {
        if (stdXMLReader.read() != '-') {
            errorExpectedInput(stdXMLReader.getSystemID(), stdXMLReader.getLineNr(), "<!--");
        }
        int n = 0;
    Block_2:
        while (true) {
            switch (stdXMLReader.read()) {
                case '-': {
                    ++n;
                    continue;
                }
                case '>': {
                    if (n == 2) {
                        break Block_2;
                    }
                    n = 0;
                    continue;
                }
                default: {
                    n = 0;
                    continue;
                }
            }
        }
    }
    
    static void skipTag(final StdXMLReader stdXMLReader) throws IOException, XMLParseException {
        int i = 1;
        while (i > 0) {
            switch (stdXMLReader.read()) {
                case '<': {
                    ++i;
                    continue;
                }
                case '>': {
                    --i;
                    continue;
                }
            }
        }
    }
    
    static String scanPublicID(final StringBuffer sb, final StdXMLReader stdXMLReader) throws IOException, XMLParseException {
        if (!checkLiteral(stdXMLReader, "UBLIC")) {
            return null;
        }
        skipWhitespace(stdXMLReader, null);
        sb.append(scanString(stdXMLReader, '\0', null));
        skipWhitespace(stdXMLReader, null);
        return scanString(stdXMLReader, '\0', null);
    }
    
    static String scanSystemID(final StdXMLReader stdXMLReader) throws IOException, XMLParseException {
        if (!checkLiteral(stdXMLReader, "YSTEM")) {
            return null;
        }
        skipWhitespace(stdXMLReader, null);
        return scanString(stdXMLReader, '\0', null);
    }
    
    static String scanIdentifier(final StdXMLReader stdXMLReader) throws IOException, XMLParseException {
        final StringBuffer sb = new StringBuffer();
        char read;
        while (true) {
            read = stdXMLReader.read();
            if (read != '_' && read != ':' && read != '-' && read != '.' && (read < 'a' || read > 'z') && (read < 'A' || read > 'Z') && (read < '0' || read > '9') && read <= '~') {
                break;
            }
            sb.append(read);
        }
        stdXMLReader.unread(read);
        return sb.toString();
    }
    
    static String scanString(final StdXMLReader stdXMLReader, final char c, final XMLEntityResolver xmlEntityResolver) throws IOException, XMLParseException {
        final StringBuffer sb = new StringBuffer();
        final int streamLevel = stdXMLReader.getStreamLevel();
        final char read = stdXMLReader.read();
        if (read != '\'' && read != '\"') {
            errorExpectedInput(stdXMLReader.getSystemID(), stdXMLReader.getLineNr(), "delimited string");
        }
        while (true) {
            final String read2 = read(stdXMLReader, c);
            final char char1 = read2.charAt(0);
            if (char1 == c) {
                if (read2.charAt(1) == '#') {
                    sb.append(processCharLiteral(read2));
                }
                else {
                    processEntity(read2, stdXMLReader, xmlEntityResolver);
                }
            }
            else if (char1 == '&') {
                stdXMLReader.unread(char1);
                final String read3 = read(stdXMLReader, '&');
                if (read3.charAt(1) == '#') {
                    sb.append(processCharLiteral(read3));
                }
                else {
                    sb.append(read3);
                }
            }
            else if (stdXMLReader.getStreamLevel() == streamLevel) {
                if (char1 == read) {
                    break;
                }
                if (char1 == '\t' || char1 == '\n' || char1 == '\r') {
                    sb.append(' ');
                }
                else {
                    sb.append(char1);
                }
            }
            else {
                sb.append(char1);
            }
        }
        return sb.toString();
    }
    
    static void processEntity(String substring, final StdXMLReader stdXMLReader, final XMLEntityResolver xmlEntityResolver) throws IOException, XMLParseException {
        substring = substring.substring(1, substring.length() - 1);
        final Reader entity = xmlEntityResolver.getEntity(stdXMLReader, substring);
        if (entity == null) {
            errorInvalidEntity(stdXMLReader.getSystemID(), stdXMLReader.getLineNr(), substring);
        }
        stdXMLReader.startNewStream(entity, !xmlEntityResolver.isExternalEntity(substring));
    }
    
    static char processCharLiteral(String s) throws IOException, XMLParseException {
        if (s.charAt(2) == 'x') {
            s = s.substring(3, s.length() - 1);
            return (char)Integer.parseInt(s, 16);
        }
        s = s.substring(2, s.length() - 1);
        return (char)Integer.parseInt(s, 10);
    }
    
    static void skipWhitespace(final StdXMLReader stdXMLReader, final StringBuffer sb) throws IOException {
        char c;
        if (sb == null) {
            do {
                c = stdXMLReader.read();
            } while (c == ' ' || c == '\t' || c == '\n');
        }
        else {
            while (true) {
                c = stdXMLReader.read();
                if (c != ' ' && c != '\t' && c != '\n') {
                    break;
                }
                if (c == '\n') {
                    sb.append('\n');
                }
                else {
                    sb.append(' ');
                }
            }
        }
        stdXMLReader.unread(c);
    }
    
    static String read(final StdXMLReader stdXMLReader, final char c) throws IOException, XMLParseException {
        char c2 = stdXMLReader.read();
        final StringBuffer sb = new StringBuffer();
        sb.append(c2);
        if (c2 == c) {
            while (c2 != ';') {
                c2 = stdXMLReader.read();
                sb.append(c2);
            }
        }
        return sb.toString();
    }
    
    static char readChar(final StdXMLReader stdXMLReader, final char c) throws IOException, XMLParseException {
        final String read = read(stdXMLReader, c);
        final char char1 = read.charAt(0);
        if (char1 == c) {
            errorUnexpectedEntity(stdXMLReader.getSystemID(), stdXMLReader.getLineNr(), read);
        }
        return char1;
    }
    
    static boolean checkLiteral(final StdXMLReader stdXMLReader, final String s) throws IOException, XMLParseException {
        for (int i = 0; i < s.length(); ++i) {
            if (stdXMLReader.read() != s.charAt(i)) {
                return false;
            }
        }
        return true;
    }
    
    static void errorExpectedInput(final String s, final int n, final String str) throws XMLParseException {
        throw new XMLParseException(s, n, "Expected: " + str);
    }
    
    static void errorInvalidEntity(final String s, final int n, final String str) throws XMLParseException {
        throw new XMLParseException(s, n, "Invalid entity: `&" + str + ";'");
    }
    
    static void errorUnexpectedEntity(final String s, final int n, final String str) throws XMLParseException {
        throw new XMLParseException(s, n, "No entity reference is expected here (" + str + ")");
    }
    
    static void errorUnexpectedCDATA(final String s, final int n) throws XMLParseException {
        throw new XMLParseException(s, n, "No CDATA section is expected here");
    }
    
    static void errorInvalidInput(final String s, final int n, final String str) throws XMLParseException {
        throw new XMLParseException(s, n, "Invalid input: " + str);
    }
    
    static void errorWrongClosingTag(final String s, final int n, final String str, final String str2) throws XMLParseException {
        throw new XMLParseException(s, n, "Closing tag does not match opening tag: `" + str2 + "' != `" + str + "'");
    }
    
    static void errorClosingTagNotEmpty(final String s, final int n) throws XMLParseException {
        throw new XMLParseException(s, n, "Closing tag must be empty");
    }
    
    static void errorMissingElement(final String s, final int n, final String str, final String str2) throws XMLValidationException {
        throw new XMLValidationException(1, s, n, str2, (String)null, (String)null, "Element " + str + " expects to have a " + str2);
    }
    
    static void errorUnexpectedElement(final String s, final int n, final String str, final String str2) throws XMLValidationException {
        throw new XMLValidationException(2, s, n, str2, (String)null, (String)null, "Unexpected " + str2 + " in a " + str);
    }
    
    static void errorMissingAttribute(final String s, final int n, final String str, final String str2) throws XMLValidationException {
        throw new XMLValidationException(3, s, n, str, str2, (String)null, "Element " + str + " expects an attribute named " + str2);
    }
    
    static void errorUnexpectedAttribute(final String s, final int n, final String str, final String str2) throws XMLValidationException {
        throw new XMLValidationException(4, s, n, str, str2, (String)null, "Element " + str + " did not expect an attribute " + "named " + str2);
    }
    
    static void errorInvalidAttributeValue(final String s, final int n, final String s2, final String str, final String s3) throws XMLValidationException {
        throw new XMLValidationException(5, s, n, s2, str, s3, "Invalid value for attribute " + str);
    }
    
    static void errorMissingPCData(final String s, final int n, final String str) throws XMLValidationException {
        throw new XMLValidationException(6, s, n, (String)null, (String)null, (String)null, "Missing #PCDATA in element " + str);
    }
    
    static void errorUnexpectedPCData(final String s, final int n, final String str) throws XMLValidationException {
        throw new XMLValidationException(7, s, n, (String)null, (String)null, (String)null, "Unexpected #PCDATA in element " + str);
    }
    
    static void validationError(final String s, final int n, final String s2, final String s3, final String s4, final String s5) throws XMLValidationException {
        throw new XMLValidationException(0, s, n, s3, s4, s5, s2);
    }
}