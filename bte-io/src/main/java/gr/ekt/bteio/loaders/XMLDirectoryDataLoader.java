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
package gr.ekt.bteio.loaders;

import java.io.FilenameFilter;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.record.XPathRecord;
import gr.ekt.bte.dataloader.FileDataLoader;

public class XMLDirectoryDataLoader extends FileDataLoader {
    private Map<String, String> xpathStringMap_;
    private static Logger logger = Logger.getLogger(XMLDirectoryDataLoader.class);

    public XMLDirectoryDataLoader(String filename, Map<String, String> xpath_string_map) {
        super(filename);
        this.xpathStringMap_ = xpath_string_map;
    }

    class XmlFilter implements FilenameFilter {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".xml");
        }
    }

    @Override
    public RecordSet getRecords() {
        RecordSet ret = new RecordSet();
        File data_dir = new File(filename);
        if (!data_dir.isDirectory()) {
            return null;
        }
        File xmls[] = data_dir.listFiles(new XmlFilter());
        //TODO factor out the actual reading in order for it to be available for use from getRecords(DataLoadingSpec)
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        doc_factory.setNamespaceAware(true);
        try {
            DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();

            for (File fl : xmls) {
                try {
                    Document doc = doc_builder.parse(fl);
                    XPathRecord rec = new XPathRecord(doc, xpathStringMap_);
                    ret.addRecord(rec);
                    //TODO handle the exceptions
                } catch(SAXException e) {
                    logger.info(e.getStackTrace());
                } catch(IOException e) {
                    logger.info(e.getStackTrace());
                } catch(XPathExpressionException e) {
                    logger.info(e.getStackTrace());
                }
            }
        } catch(ParserConfigurationException e) {
            //TODO throw MalformedSourceException
            logger.info(e.getStackTrace());
            return null;
        }

        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) {
        //TODO implement when DataLoadingSpec gets defined.
        return getRecords();
    }
}
