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
import java.util.ArrayList;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.core.MutableRecord;

public class XPathRecordTest {
    private XPathRecord rec;
    private List<Value> vals;
    HashMap<String, String> mp;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
        DocumentBuilderFactory dom_factory = DocumentBuilderFactory.newInstance();
        dom_factory.setNamespaceAware(true);
        DocumentBuilder builder = dom_factory.newDocumentBuilder();
        Document doc = builder.parse("src/test/resources/document.xml");

        mp = new HashMap<String, String>();
        mp.put("empty", "/root/leaf1/text()");
        mp.put("leaf1_text", "/root/node1/leaf1/text()");
        mp.put("node2_text", "/root/node2/descendant-or-self::text()");
        mp.put("leaf1_attribute", "/root/node1/leaf1/@attribute");
        mp.put("all_attributes", "//@attribute");
        mp.put("leaf3", "/root/node3/leaf3");

        rec = new XPathRecord(doc, mp);

        vals = new ArrayList<Value>();

        vals.add(new StringValue("val1"));
        vals.add(new StringValue("val2"));
    }

    @Test
    public void testGetValues_NotExisting() {
        assertNull(rec.getValues("not_existing"));
    }

    @Test
    public void testGetValues_Empty() {
        List<Value> res0 = rec.getValues("empty");
        assertEquals(0, res0.size());
    }

    @Test
    public void testGetValues_Text() {
        List<Value> res1 = rec.getValues("leaf1_text");
        assertEquals(1, res1.size());
        assertEquals("Text of leaf1", res1.get(0).getAsString());

        List<Value> res2 = rec.getValues("node2_text");
        assertEquals(3, res2.size());
        assertEquals("Text of node2 that contains", res2.get(0).getAsString());
        assertEquals("a leaf with text", res2.get(1).getAsString());
        assertEquals("and has some more text at the end.", res2.get(2).getAsString());

    }
    @Test
    public void testGetValues_Attributes() {
        List<Value> res3 = rec.getValues("leaf1_attribute");
        assertEquals(1, res3.size());
        assertEquals("leaf1 attribute", res3.get(0).getAsString());

        List<Value> res4 = rec.getValues("all_attributes");
        assertEquals(2, res4.size());
        assertEquals("leaf1 attribute", res4.get(0).getAsString());
        assertEquals("leaf2 attribute", res4.get(1).getAsString());
    }

    @Test
    public void testIsMutable() {
        assertFalse(rec.isMutable());
    }

    @Test
    public void testMakeMutable() {
        MutableRecord mut_rec = rec.makeMutable();
        assertTrue(mut_rec.isMutable());

        for(String field : mp.keySet()) {
            List<Value> im_rec_val = rec.getValues(field);
            List<Value> mu_rec_val = mut_rec.getValues(field);
            assertEquals(im_rec_val.size(), mu_rec_val.size());
            for (int i = 0; i < im_rec_val.size(); i++) {
                assertEquals(im_rec_val.get(i), mu_rec_val.get(i));
            }
        }
    }
}
