package gr.ekt.bte.record;

import javax.xml.xpath.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import java.io.IOException;


import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.HashMap;
import java.util.List;
import gr.ekt.bte.core.Value;

public class XPathRecordTest {
    private XPathRecord rec_;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory dom_factory = DocumentBuilderFactory.newInstance();
        dom_factory.setNamespaceAware(true);
        DocumentBuilder builder = dom_factory.newDocumentBuilder();
        Document doc = builder.parse("src/test/resources/document.xml");

        HashMap<String, String> mp = new HashMap<String, String>();
        mp.put("empty", "/root/leaf1/text()");
        mp.put("leaf1_text", "/root/node1/leaf1/text()");
        mp.put("leaf2_text", "/root/node2/descendant-or-self::text()");
        mp.put("leaf1_attribute", "/root/node1/leaf1/@attribute");
        mp.put("all_attributes", "//@attribute");

        rec_ = new XPathRecord(doc, mp);
    }

    @Test
    public void testGetValues() {
        assertNull(rec_.getValues("not_existing"));
        List<Value> res0 = rec_.getValues("empty");
        assertEquals(0, res0.size());

        List<Value> res1 = rec_.getValues("leaf1_text");
        assertEquals(1, res1.size());
        assertEquals("Text of leaf1", res1.get(0).getAsString());

        // List<Value> res2 = rec_.getValues("leaf2_text");
        // assertEquals(1, res2.size());
        // assertEquals("Text of node2 that contains a leaf with text.", res2.get(0));
    }
}
