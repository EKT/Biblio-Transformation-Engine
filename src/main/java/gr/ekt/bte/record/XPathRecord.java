package gr.ekt.bte.record;

import java.util.Map;
import java.util.List;
import java.util.TreeMap;
import java.util.Set;
import java.util.ArrayList;

import javax.xml.xpath.*;
import org.w3c.dom.*;

import org.apache.log4j.Logger;

import gr.ekt.bte.core.Value;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.core.MutableRecord;


public class XPathRecord implements Record {
    private Document doc;
    private Map<String, XPathExpression> xpath_map;
    private Logger logger = Logger.getLogger(XPathRecord.class);

    public XPathRecord(Document doc, Map<String, String> xpath_string_map)
            throws XPathExpressionException {
        this.doc = doc;
        xpath_map = new TreeMap<String, XPathExpression>();

        Set<Map.Entry<String, String>> entries = xpath_string_map.entrySet();
        XPath xpath = XPathFactory.newInstance().newXPath();
        for(Map.Entry<String, String> entry : entries) {
            XPathExpression expr = xpath.compile(entry.getValue());
            xpath_map.put(entry.getKey(), expr);
        }
    }

    @Override
    public List<Value> getValues(String field) {
        try {
            XPathExpression expr = xpath_map.get(field);
            if (expr == null) {
                return null;
            }
            NodeList lst = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);
            ArrayList<Value> ret = new ArrayList<Value>();
            for (int i = 0; i < lst.getLength(); i++) {
                String str = lst.item(i).getTextContent().trim();
                //Ignore empty lines
                if (!"".equals(str)) {
                    ret.add(new StringValue(str));
                }
            }
            return ret;
        } catch(XPathExpressionException e) {
            logger.info(e.getStackTrace());
        }

        return null;
    }

    @Override
    public MutableRecord makeMutable() {
        MapRecord mr = new MapRecord();

        for (String field : xpath_map.keySet()) {
            mr.addField(field, this.getValues(field));
        }

        return mr;
    }

    @Override
    public boolean isMutable() {
        return false;
    }
}
