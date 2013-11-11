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

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.record.MapRecord;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;

import se.kb.oai.OAIException;
import se.kb.oai.pmh.OaiPmhServer;
import se.kb.oai.pmh.RecordsList;
import se.kb.oai.pmh.ResumptionToken;

public class OAIPMHDataLoader implements DataLoader {
    private static  Logger logger_;
    private OaiPmhServer server_;
    private String server_address_;
    private ResumptionToken token_;
    private String prefix_;
    private boolean has_more_records_;
    private Map<String, String> field_map_;
    //private

    public OAIPMHDataLoader() {
        server_ = null;
        server_address_ = null;
        token_ = null;
        prefix_ = null;
        has_more_records_ = true;
        field_map_ = null;
    }
    public OAIPMHDataLoader(String server_address, String prefix, Map<String, String> field_map) {
        server_address_ = server_address;
        server_ = new OaiPmhServer(server_address_);
        has_more_records_ = true;
        prefix_ = prefix;
        field_map_ = field_map;
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

            for (int i = 0; i < oai_record_list.size(); i++) {
                Record rec = oai2bte(oai_record_list.get(i));
                ret.addRecord(rec);
            }
        } catch (OAIException e) {
            logger_.info("Caught OAIException " + e.getMessage());
            throw new MalformedSourceException(e.getMessage());
        }
        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        //See if the data loading spec requests a specific record.
        if (spec.getIdentifier() != null) {
            if(server_ == null) {
                throw new MalformedSourceException("Connection with server " + server_address_ + " has not been established");
            }
            RecordSet ret = new RecordSet();
            try {
                se.kb.oai.pmh.Record oai_record = server_.getRecord(spec.getIdentifier(), prefix_);
                Record rec = oai2bte(oai_record);
                ret.addRecord(rec);

            } catch (OAIException e) {
                logger_.info("Caught OAIException " + e.getMessage());
                throw new MalformedSourceException(e.getMessage());
            }

            return ret;
        }
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
        server_ = new OaiPmhServer(server_address_);
        has_more_records_ = true;
    }

    /**
     * @return the prefix_
     */
    public String getPrefix() {
        return prefix_;
    }

    /**
     * @param prefix_ the prefix_ to set
     */
    public void setPrefix(String prefix_) {
        this.prefix_ = prefix_;
    }

    private Record oai2bte(se.kb.oai.pmh.Record oai_record) {
        MapRecord rec = new MapRecord();
        Element metadata_element = oai_record.getMetadata();
        for (String field : field_map_.keySet()) {
            String record_key = field_map_.get(field);
            for (Object elem : metadata_element.elements(field)) {
                String field_value = ((Element)elem).getText();
                rec.addValue(record_key, new StringValue(field_value));
            }
        }
        return rec;
    }

    /**
     * @return the field_map_
     */
    public Map<String, String> getFieldMap() {
        return field_map_;
    }

    /**
     * @param field_map_ the field_map_ to set
     */
    public void setFieldMap(Map<String, String> field_map_) {
        this.field_map_ = field_map_;
    }
}
