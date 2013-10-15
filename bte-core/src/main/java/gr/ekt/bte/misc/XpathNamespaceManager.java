package gr.ekt.bte.misc;

import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;


public class XpathNamespaceManager implements NamespaceContext {
    public String getNamespaceURI(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }

        if (prefix.equals("dc")) {
            return "http://purl.org/dc/elements/1.1/";
        }

        if (prefix.equals("oai_dc")) {
            return "http://www.openarchives.org/OAI/2.0/";
        }
        return XMLConstants.NULL_NS_URI;
    }

    public String getPrefix(String namespaceURI) {
        if (namespaceURI.equals("http://www.openarchives.org/OAI/2.0/")) {
            return "oai_dc";
        }

        if (namespaceURI.equals("http://purl.org/dc/elements/1.1/")) {
            return "dc";
        }
        return null;
    }

    public Iterator getPrefixes(String namespaceURI) {
        ArrayList<String> foo = new ArrayList<String>();
        if (namespaceURI.equals("http://www.openarchives.org/OAI/2.0/")) {
            foo.add("oai_dc");
        }

        if (namespaceURI.equals("http://purl.org/dc/elements/1.1/")) {
            foo.add("dc");
        }
        return foo.iterator();
    }
}
