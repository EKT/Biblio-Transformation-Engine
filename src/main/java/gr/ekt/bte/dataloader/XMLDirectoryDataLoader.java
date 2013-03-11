package gr.ekt.bte.dataloader;

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

public class XMLDirectoryDataLoader extends FileDataLoader {
    private Map<String, String> xpath_string_map;
    private static Logger logger = Logger.getLogger(XMLDirectoryDataLoader.class);

    public XMLDirectoryDataLoader(String filename, Map<String, String> xpath_string_map) {
        super(filename);
        xpath_string_map = xpath_string_map;
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
        DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
        doc_factory.setNamespaceAware(true);
        try {
            DocumentBuilder doc_builder = doc_factory.newDocumentBuilder();

            for (File fl : xmls) {
                try {
                    Document doc = doc_builder.parse(fl);
                    XPathRecord rec = new XPathRecord(doc, xpath_string_map);
                    ret.addRecord(rec);
                } catch(SAXException e) {
                    logger.info(e.getStackTrace());
                } catch(IOException e) {
                    logger.info(e.getStackTrace());
                } catch(XPathExpressionException e) {
                    logger.info(e.getStackTrace());
                }
            }
        } catch(ParserConfigurationException e) {
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
