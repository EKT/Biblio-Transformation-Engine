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

import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.core.Value;
import gr.ekt.bte.dataloader.FileDataLoader;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.record.MapRecord;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

public class RISDataLoader extends FileDataLoader {
    private static Logger logger_ = Logger.getLogger(RISDataLoader.class);
    private BufferedReader reader_;
    private Map<String, String> field_map_;

    public RISDataLoader() {
        super();
        reader_ = null;
        field_map_ = null;
    }

    public RISDataLoader(String filename, Map<String, String> field_map) throws EmptySourceException {
        super(filename);
        field_map_ = field_map;
        openReader();
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        RecordSet records = new RecordSet();

        try {
            String line;
            boolean in_record = false;
            int line_cnt = 0;
            MapRecord rec = null;

            while((line = reader_.readLine()) != null) {
                line_cnt++;
                line = line.trim();

                //Ignore empty lines
                if(line.isEmpty()) {
                    continue;
                }
                Pattern ris_pattern = Pattern.compile("^([A-Z][A-Z0-9])  - (.*)$");
                Matcher ris_matcher = ris_pattern.matcher(line);
                if (!ris_matcher.matches()) {
                    logger_.info("Line: " + line_cnt + " in file " + filename + " does not match the RIS format");
                    throw new MalformedSourceException("Line: " + line_cnt + " in file " + filename + " does not match the RIS format");
                }
                String ris_tag = ris_matcher.group(1);
                if (!in_record) {
                    //The first tag of the record should be "TY". If we
                    //encounter it we should create a new record.
                    if (ris_tag.equals("TY")) {
                        in_record = true;
                        rec = new MapRecord();
                    }
                    else {
                        logger_.info("Line: " + line_cnt + " in file " + filename + " should contain tag \"TY\"");
                        throw new MalformedSourceException("Line: " + line_cnt + " in file " + filename + " should contain tag \"TY\"");
                    }
                }

                //If the tag is the end record tag ("ER") we stop
                //being in a record. Add the current record in the
                //record set and skip the rest of the processing.
                if (ris_tag.equals("ER")) {
                    in_record = false;
                    records.addRecord(rec);
                    rec = null;
                    continue;
                }

                //If there is no mapping for the current tag we do not
                //know what to do with it, so we ignore it.
                if (!field_map_.containsKey(ris_tag)) {
                    logger_.warn("Tag \"" + ris_tag + "\" is not in the field map. Ignoring");
                    continue;
                }

                String field = field_map_.get(ris_tag);
                Value val = new StringValue(ris_matcher.group(2));
                rec.addValue(field, val);
            }
        } catch (IOException e) {
            logger_.info("Error while reading from file " + filename);
            throw new MalformedSourceException("Error while reading from file " + filename);
        }

        return records;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        return getRecords();
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
        try {
            openReader();
        } catch (EmptySourceException e) {
            logger_.info("Could not open file " + filename);
            reader_ = null;
        }
    }
    @Override
    protected void finalize() throws Throwable {
        reader_.close();
    }

    private void openReader() throws EmptySourceException {
        try {
            reader_ = new BufferedReader(new FileReader(filename));
        } catch (FileNotFoundException e) {
            throw new EmptySourceException("File " + filename + " not found");
        }
    }
}
