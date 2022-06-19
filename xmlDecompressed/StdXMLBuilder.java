package processing.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class StdXMLBuilder
{
    private Stack<XMLElement> stack;
    private XMLElement root;
    private XMLElement parent;
    
    public StdXMLBuilder() {
        this(new XMLElement());
        this.stack = null;
        this.root = null;
    }
    
    public StdXMLBuilder(final XMLElement parent) {
        this.parent = parent;
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.root = null;
        this.stack.clear();
        this.stack = null;
        super.finalize();
    }
    
    public void startBuilding(final String s, final int n) {
        this.stack = new Stack<XMLElement>();
        this.root = null;
    }
    
    public void newProcessingInstruction(final String s, final Reader reader) {
    }
    
    public void startElement(final String str, final String str2, final String s, final String s2, final int n) {
        String string = str;
        if (str2 != null) {
            string = str2 + ':' + str;
        }
        if (this.stack.empty()) {
            this.parent.init(string, s, s2, n);
            this.stack.push(this.parent);
            this.root = this.parent;
        }
        else {
            final XMLElement xmlElement = this.stack.peek();
            final XMLElement item = new XMLElement(string, s, s2, n);
            xmlElement.addChild(item);
            this.stack.push(item);
        }
    }
    
    public void elementAttributesProcessed(final String s, final String s2, final String s3) {
    }
    
    public void endElement(final String s, final String s2, final String s3) {
        final XMLElement xmlElement = this.stack.pop();
        if (xmlElement.getChildCount() == 1) {
            final XMLElement child = xmlElement.getChild(0);
            if (child.getLocalName() == null) {
                xmlElement.setContent(child.getContent());
                xmlElement.removeChild(0);
            }
        }
    }
    
    public void addAttribute(final String s, final String str, final String s2, final String s3, final String s4) throws Exception {
        String string = s;
        if (str != null) {
            string = str + ':' + s;
        }
        final XMLElement xmlElement = this.stack.peek();
        if (xmlElement.hasAttribute(string)) {
            throw new XMLParseException(xmlElement.getSystemID(), xmlElement.getLine(), "Duplicate attribute: " + s);
        }
        xmlElement.setString(string, s3);
    }
    
    public void addPCData(final Reader reader, final String s, final int n) {
        int n2 = 2048;
        int n3 = 0;
        final StringBuffer sb = new StringBuffer(n2);
        final char[] array = new char[n2];
        while (true) {
            if (n3 >= n2) {
                n2 *= 2;
                sb.ensureCapacity(n2);
            }
            int read;
            try {
                read = reader.read(array);
            }
            catch (IOException ex) {
                break;
            }
            if (read < 0) {
                break;
            }
            sb.append(array, 0, read);
            n3 += read;
        }
        final XMLElement xmlElement = new XMLElement((String)null, (String)null, s, n);
        xmlElement.setContent(sb.toString());
        if (!this.stack.empty()) {
            this.stack.peek().addChild(xmlElement);
        }
    }
    
    public Object getResult() {
        return this.root;
    }
}