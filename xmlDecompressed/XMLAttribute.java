package processing.xml;

class XMLAttribute
{
    private String name;
    private String localName;
    private String namespace;
    private String value;
    private String type;
    
    XMLAttribute(final String name, final String localName, final String namespace, final String value, final String type) {
        this.name = name;
        this.localName = localName;
        this.namespace = namespace;
        this.value = value;
        this.type = type;
    }
    
    String getName() {
        return this.name;
    }
    
    String getLocalName() {
        return this.localName;
    }
    
    String getNamespace() {
        return this.namespace;
    }
    
    String getValue() {
        return this.value;
    }
    
    void setValue(final String value) {
        this.value = value;
    }
    
    String getType() {
        return this.type;
    }
}