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



public class XPathRecord implements Record {
    private Document doc_;
    private Map<String, XPathExpression> xpath_map_;
    private Logger logger = Logger.getLogger(XPathRecord.class);

    public XPathRecord(Document doc, Map<String, String> xpath_string_map) throws XPathExpressionException {
        doc_ = doc;
        xpath_map_ = new TreeMap<String, XPathExpression>();

        Set<Map.Entry<String, String>> entries = xpath_string_map.entrySet();
        XPath xpath = XPathFactory.newInstance().newXPath();
        for(Map.Entry<String, String> entry : entries) {
            XPathExpression expr = xpath.compile(entry.getValue());
            xpath_map_.put(entry.getKey(), expr);
        }
    }

    @Override
    public List<Value> getValues(String field) {
        try {
            XPathExpression expr = xpath_map_.get(field);
            if (expr == null) {
                return null;
            }
            NodeList lst = (NodeList)expr.evaluate(doc_, XPathConstants.NODESET);
            ArrayList<Value> ret = new ArrayList<Value>();
            for (int i = 0; i < lst.getLength(); i++) {
                String str = lst.item(i).getTextContent().trim();
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
    public boolean addField(String field, List<Value> values) {
        return false;
    }

    @Override
    public boolean addValue(String field, Value value) {
        return false;
    }

    @Override
    public boolean removeField(String field) {
        return false;
    }

    @Override
    public boolean removeValue(String field, Value value) {
        return false;
    }

    @Override
    public boolean updateField(String field, List<Value> value) {
        return false;
    }

    @Override
    public boolean updateValue(String field, Value old_value, Value new_value) {
        return false;
    }
}
