package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.exceptions.MalformedSourceException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.pmh.ResumptionToken;

public class OAIPMHDataLoader implements DataLoader {
    private static  Logger logger_ = Logger.getLogger(OAIPMHDataLoader.class);
    private OaiPmhServer server_ = null;
    private String server_address_ = null;
    private ResumptionToken token_ = null;
    private String prefix_ = null;
    private boolean has_more_records_ = true;
    //private

    public OAIPMHDataLoader(String server_address, String prefix) {
        server_address_ = server_address;
        server_ = new OaiPmhServer(server_address_);
        prefix_ = prefix;
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        if(server_ == null) {
            throw new MalformedSourceException("Connection with server " + server_address_ + " has not been established");
        }
        RecordSet ret = new RecordSet();

        //We should either have a prefix, or a token. If both are null
        //it means that we have read all the records from the ser
        if (!has_more_records_) {
            return ret;
        }

        try {
            RecordsList records = null;
            if(token_ == null) {
                records = server_.listRecords(prefix_);
            }
            else {
                records = server_.listRecords(token_);
            }
            List<se.kb.oai.pmh.Record> oai_record_list = records.asList();
            //Keep the token for the next call
            token_ = records.getResumptionToken();

            //If there is a resumtion token returned, that means that
            //the server has more records.
            has_more_records_ = token_ != null;
            DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
            doc_factory.setNamespaceAware(true);
            for (int i = 0; i < oai_record_list.size(); i++) {
                String metadata = oai_record_list.get(i).getMetadataAsString();

                Document doc = doc_factory.newDocumentBuilder().parse(new ByteArrayInputStream(metadata.getBytes("UTF-8")));

                //TODO create xpaths based on input mappings and use
                //them to initialize XPathRecords
            }
        } catch (OAIException e) {
            logger_.info("Caught OAIException " + e.getMessage());
            throw new MalformedSourceException(e.getMessage());
        } catch (IOException e) {
            logger_.info("Caught IOException " + e.getMessage());
            throw new MalformedSourceException(e.getMessage());
        } catch (ParserConfigurationException e) {
            logger_.info("Caught ParserConfigurationException " + e.getMessage());
            throw new MalformedSourceException(e.getMessage());
        } catch (SAXException e) {
            logger_.info("Caught SAXException " + e.getMessage());
            throw new MalformedSourceException(e.getMessage());
        }
        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        return getRecords();
    }

    /**
     * @return the server_address_
     */
    public String getServerAddress() {
        return server_address_;
    }

    /**
     * @param server_address_ the server_address_ to set
     */
    public void setServerAddress(String server_address_) {
        this.server_address_ = server_address_;
    }

    /**
     * @return the prefix_
     */
    public String getPrefix_() {
        return prefix_;
    }

    /**
     * @param prefix_ the prefix_ to set
     */
    public void setPrefix_(String prefix_) {
        this.prefix_ = prefix_;
    }
}
