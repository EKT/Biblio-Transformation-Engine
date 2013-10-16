/**
 * Copyright (c) 2007-2013, National Documentation Centre (EKT, www.ekt.gr)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     Neither the name of the National Documentation Centre nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written
 *     permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gr.ekt.bte.record;

import gr.ekt.bte.core.MutableRecord;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.core.Value;
import gr.ekt.bte.misc.XpathNamespaceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


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
        xpath.setNamespaceContext(new XpathNamespaceManager());
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

    @Override
    public boolean hasField(String field) {
        //TODO Probably not the most efficient way to check
        return getValues(field) != null;
    }

    @Override
    public Set<String> getFields() {
        return xpath_map.keySet();
    }
}
