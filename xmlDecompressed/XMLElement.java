package processing.xml;

import java.io.PrintWriter;
import java.io.IOException;
import java.io.Writer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.ByteArrayOutputStream;
import java.util.Enumeration;
import java.io.StringReader;
import java.io.Reader;
import java.util.Vector;
import processing.core.PApplet;
import java.io.Serializable;

public class XMLElement implements Serializable
{
    public static final int NO_LINE = -1;
    private PApplet sketch;
    private XMLElement parent;
    private Vector<XMLAttribute> attributes;
    private Vector<XMLElement> children;
    private String name;
    private String fullName;
    private String namespace;
    private String content;
    private String systemID;
    private int line;
    
    public XMLElement() {
        this(null, null, null, -1);
    }
    
    public XMLElement(final String s) {
        this(s, null, null, -1);
    }
    
    public XMLElement(final String name, final String namespace, final String systemID, final int line) {
        this.attributes = new Vector<XMLAttribute>();
        this.children = new Vector<XMLElement>(8);
        this.fullName = name;
        if (namespace == null) {
            this.name = name;
        }
        else {
            final int index = name.indexOf(58);
            if (index >= 0) {
                this.name = name.substring(index + 1);
            }
            else {
                this.name = name;
            }
        }
        this.namespace = namespace;
        this.content = null;
        this.line = line;
        this.systemID = systemID;
        this.parent = null;
    }
    
    public XMLElement(final PApplet sketch, final String s) {
        this();
        this.sketch = sketch;
        this.init(sketch.createReader(s));
    }
    
    public XMLElement(final Reader reader) {
        this();
        this.init(reader);
    }
    
    public static XMLElement parse(final String s) {
        return parse(new StringReader(s));
    }
    
    public static XMLElement parse(final Reader reader) {
        try {
            final StdXMLParser stdXMLParser = new StdXMLParser();
            stdXMLParser.setBuilder(new StdXMLBuilder());
            stdXMLParser.setValidator(new XMLValidator());
            stdXMLParser.setReader(new StdXMLReader(reader));
            return (XMLElement)stdXMLParser.parse();
        }
        catch (XMLException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    protected void init(final String name, final String namespace, final String systemID, final int line) {
        this.fullName = name;
        if (namespace == null) {
            this.name = name;
        }
        else {
            final int index = name.indexOf(58);
            if (index >= 0) {
                this.name = name.substring(index + 1);
            }
            else {
                this.name = name;
            }
        }
        this.namespace = namespace;
        this.line = line;
        this.systemID = systemID;
    }
    
    protected void init(final Reader reader) {
        try {
            final StdXMLParser stdXMLParser = new StdXMLParser();
            stdXMLParser.setBuilder(new StdXMLBuilder(this));
            stdXMLParser.setValidator(new XMLValidator());
            stdXMLParser.setReader(new StdXMLReader(reader));
            stdXMLParser.parse();
        }
        catch (XMLException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.attributes.clear();
        this.attributes = null;
        this.children = null;
        this.fullName = null;
        this.name = null;
        this.namespace = null;
        this.content = null;
        this.systemID = null;
        this.parent = null;
        super.finalize();
    }
    
    public XMLElement getParent() {
        return this.parent;
    }
    
    public String getName() {
        return this.fullName;
    }
    
    public String getLocalName() {
        return this.name;
    }
    
    public String getNamespace() {
        return this.namespace;
    }
    
    public void setName(final String s) {
        this.name = s;
        this.fullName = s;
        this.namespace = null;
    }
    
    public void setName(final String s, final String namespace) {
        final int index = s.indexOf(58);
        if (namespace == null || index < 0) {
            this.name = s;
        }
        else {
            this.name = s.substring(index + 1);
        }
        this.fullName = s;
        this.namespace = namespace;
    }
    
    public void addChild(final XMLElement obj) {
        if (obj == null) {
            throw new IllegalArgumentException("child must not be null");
        }
        if (obj.getLocalName() == null && !this.children.isEmpty()) {
            final XMLElement xmlElement = this.children.lastElement();
            if (xmlElement.getLocalName() == null) {
                xmlElement.setContent(xmlElement.getContent() + obj.getContent());
                return;
            }
        }
        obj.parent = this;
        this.children.addElement(obj);
    }
    
    public void insertChild(final XMLElement obj, final int index) {
        if (obj == null) {
            throw new IllegalArgumentException("child must not be null");
        }
        if (obj.getLocalName() == null && !this.children.isEmpty()) {
            final XMLElement xmlElement = this.children.lastElement();
            if (xmlElement.getLocalName() == null) {
                xmlElement.setContent(xmlElement.getContent() + obj.getContent());
                return;
            }
        }
        obj.parent = this;
        this.children.insertElementAt(obj, index);
    }
    
    public void removeChild(final XMLElement obj) {
        if (obj == null) {
            throw new IllegalArgumentException("child must not be null");
        }
        this.children.removeElement(obj);
    }
    
    public void removeChild(final int index) {
        this.children.removeElementAt(index);
    }
    
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
    
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }
    
    public int getChildCount() {
        return this.children.size();
    }
    
    public String[] listChildren() {
        final int childCount = this.getChildCount();
        final String[] array = new String[childCount];
        for (int i = 0; i < childCount; ++i) {
            array[i] = this.getChild(i).getName();
        }
        return array;
    }
    
    public XMLElement[] getChildren() {
        final XMLElement[] anArray = new XMLElement[this.getChildCount()];
        this.children.copyInto(anArray);
        return anArray;
    }
    
    public XMLElement getChild(final int index) {
        return this.children.elementAt(index);
    }
    
    public XMLElement getChild(final String anObject) {
        if (anObject.indexOf(47) != -1) {
            return this.getChildRecursive(PApplet.split(anObject, '/'), 0);
        }
        for (int childCount = this.getChildCount(), i = 0; i < childCount; ++i) {
            final XMLElement child = this.getChild(i);
            final String name = child.getName();
            if (name != null && name.equals(anObject)) {
                return child;
            }
        }
        return null;
    }
    
    protected XMLElement getChildRecursive(final String[] array, final int n) {
        if (!Character.isDigit(array[n].charAt(0))) {
            final int childCount = this.getChildCount();
            int i = 0;
            while (i < childCount) {
                final XMLElement child = this.getChild(i);
                final String name = child.getName();
                if (name != null && name.equals(array[n])) {
                    if (n == array.length - 1) {
                        return child;
                    }
                    return child.getChildRecursive(array, n + 1);
                }
                else {
                    ++i;
                }
            }
            return null;
        }
        final XMLElement child2 = this.getChild(Integer.parseInt(array[n]));
        if (n == array.length - 1) {
            return child2;
        }
        return child2.getChildRecursive(array, n + 1);
    }
    
    public XMLElement[] getChildren(final String s) {
        if (s.indexOf(47) != -1) {
            return this.getChildrenRecursive(PApplet.split(s, '/'), 0);
        }
        if (Character.isDigit(s.charAt(0))) {
            return new XMLElement[] { this.getChild(Integer.parseInt(s)) };
        }
        final int childCount = this.getChildCount();
        final XMLElement[] array = new XMLElement[childCount];
        int n = 0;
        for (int i = 0; i < childCount; ++i) {
            final XMLElement child = this.getChild(i);
            final String name = child.getName();
            if (name != null && name.equals(s)) {
                array[n++] = child;
            }
        }
        return (XMLElement[])PApplet.subset((Object)array, 0, n);
    }
    
    protected XMLElement[] getChildrenRecursive(final String[] array, final int n) {
        if (n == array.length - 1) {
            return this.getChildren(array[n]);
        }
        final XMLElement[] children = this.getChildren(array[n]);
        XMLElement[] array2 = new XMLElement[0];
        for (int i = 0; i < children.length; ++i) {
            array2 = (XMLElement[])PApplet.concat((Object)array2, (Object)children[i].getChildrenRecursive(array, n + 1));
        }
        return array2;
    }
    
    private XMLAttribute findAttribute(final String anObject) {
        final Enumeration<XMLAttribute> elements = this.attributes.elements();
        while (elements.hasMoreElements()) {
            final XMLAttribute xmlAttribute = elements.nextElement();
            if (xmlAttribute.getName().equals(anObject)) {
                return xmlAttribute;
            }
        }
        return null;
    }
    
    public int getAttributeCount() {
        return this.attributes.size();
    }
    
    public String[] listAttributes() {
        final String[] array = new String[this.attributes.size()];
        for (int i = 0; i < this.attributes.size(); ++i) {
            array[i] = this.attributes.get(i).getName();
        }
        return array;
    }
    
    @Deprecated
    public String getStringAttribute(final String s) {
        return this.getString(s);
    }
    
    @Deprecated
    public String getStringAttribute(final String s, final String s2) {
        return this.getString(s, s2);
    }
    
    public String getString(final String s) {
        return this.getString(s, null);
    }
    
    public String getString(final String s, final String s2) {
        final XMLAttribute attribute = this.findAttribute(s);
        if (attribute == null) {
            return s2;
        }
        return attribute.getValue();
    }
    
    public boolean getBoolean(final String s) {
        return this.getBoolean(s, false);
    }
    
    public boolean getBoolean(final String s, final boolean b) {
        final String string = this.getString(s);
        if (string == null) {
            return b;
        }
        return string.equals("1") || string.toLowerCase().equals("true");
    }
    
    @Deprecated
    public int getIntAttribute(final String s) {
        return this.getInt(s, 0);
    }
    
    @Deprecated
    public int getIntAttribute(final String s, final int n) {
        return this.getInt(s, n);
    }
    
    public int getInt(final String s) {
        return this.getInt(s, 0);
    }
    
    public int getInt(final String s, final int n) {
        final String string = this.getString(s);
        return (string == null) ? n : PApplet.parseInt(string, n);
    }
    
    @Deprecated
    public float getFloatAttribute(final String s) {
        return this.getFloat(s, 0.0f);
    }
    
    @Deprecated
    public float getFloatAttribute(final String s, final float n) {
        return this.getFloat(s, 0.0f);
    }
    
    public float getFloat(final String s) {
        return this.getFloat(s, 0.0f);
    }
    
    public float getFloat(final String s, final float n) {
        final String string = this.getString(s);
        if (string == null) {
            return n;
        }
        return PApplet.parseFloat(string, n);
    }
    
    public double getDouble(final String s) {
        return this.getDouble(s, 0.0);
    }
    
    public double getDouble(final String s, final double n) {
        final String string = this.getString(s);
        return (string == null) ? n : Double.parseDouble(string);
    }
    
    public void setString(final String s, final String value) {
        final XMLAttribute attribute = this.findAttribute(s);
        if (attribute == null) {
            this.attributes.addElement(new XMLAttribute(s, s, null, value, "CDATA"));
        }
        else {
            attribute.setValue(value);
        }
    }
    
    public void setBoolean(final String s, final boolean b) {
        this.setString(s, String.valueOf(b));
    }
    
    public void setInt(final String s, final int i) {
        this.setString(s, String.valueOf(i));
    }
    
    public void setFloat(final String s, final float f) {
        this.setString(s, String.valueOf(f));
    }
    
    public void setDouble(final String s, final double d) {
        this.setString(s, String.valueOf(d));
    }
    
    public void remove(final String anObject) {
        for (int i = 0; i < this.attributes.size(); ++i) {
            if (this.attributes.elementAt(i).getName().equals(anObject)) {
                this.attributes.removeElementAt(i);
                return;
            }
        }
    }
    
    public boolean hasAttribute(final String s) {
        return this.findAttribute(s) != null;
    }
    
    public String getSystemID() {
        return this.systemID;
    }
    
    public int getLine() {
        return this.line;
    }
    
    public String getContent() {
        return this.content;
    }
    
    public void setContent(final String content) {
        this.content = content;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof XMLElement)) {
            return false;
        }
        final XMLElement xmlElement = (XMLElement)o;
        if (!this.name.equals(xmlElement.getLocalName())) {
            return false;
        }
        if (this.attributes.size() != xmlElement.getAttributeCount()) {
            return false;
        }
        final Enumeration<XMLAttribute> elements = this.attributes.elements();
        while (elements.hasMoreElements()) {
            final XMLAttribute xmlAttribute = elements.nextElement();
            if (!xmlElement.hasAttribute(xmlAttribute.getName())) {
                return false;
            }
            if (!xmlAttribute.getValue().equals(xmlElement.getString(xmlAttribute.getName(), null))) {
                return false;
            }
        }
        if (this.children.size() != xmlElement.getChildCount()) {
            return false;
        }
        for (int i = 0; i < this.children.size(); ++i) {
            if (!this.getChild(i).equals(xmlElement.getChild(i))) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        return this.toString(true);
    }
    
    public String toString(final boolean b) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final XMLWriter xmlWriter = new XMLWriter((Writer)new OutputStreamWriter(out));
        try {
            xmlWriter.write(this, b);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return out.toString();
    }
    
    private PApplet findSketch() {
        if (this.sketch != null) {
            return this.sketch;
        }
        if (this.parent != null) {
            return this.parent.findSketch();
        }
        return null;
    }
    
    public boolean save(final String s) {
        if (this.sketch == null) {
            this.sketch = this.findSketch();
        }
        if (this.sketch == null) {
            System.err.println("save() can only be used on elements loaded by a sketch");
            throw new RuntimeException("no sketch found, use write(PrintWriter) instead.");
        }
        return this.write(this.sketch.createWriter(s));
    }
    
    public boolean write(final PrintWriter printWriter) {
        printWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        final XMLWriter xmlWriter = new XMLWriter((Writer)printWriter);
        try {
            xmlWriter.write(this, true);
            printWriter.flush();
            return true;
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}