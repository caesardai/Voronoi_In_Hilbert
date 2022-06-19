package processing.xml;

import java.net.URL;
import java.io.LineNumberReader;
import java.io.PushbackReader;

private class StackedReader
{
    PushbackReader pbReader;
    LineNumberReader lineReader;
    URL systemId;
    String publicId;
}