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

public class EndnoteDataLoader extends FileDataLoader {
    private static Logger logger_ = Logger.getLogger(EndnoteDataLoader.class);
    private BufferedReader reader_;
    private Map<String, String> field_map_;

    public EndnoteDataLoader() {
        super();
        reader_ = null;
        field_map_ = null;
    }

    public EndnoteDataLoader(String filename, Map<String, String> field_map) throws EmptySourceException {
        super(filename);
        field_map_ = field_map;
        openReader();
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        if (reader_ == null) {
            throw new EmptySourceException("File " + filename + " could not be opened");
        }
        RecordSet ret = new RecordSet();

        try {
            String line;

            //Read the first two lines. They should contain the tags
            //FN and VR in that order.
            line = reader_.readLine();

            //We have reached the end of file
            if(line == null) {
                return ret;
            }
            if(!line.startsWith("FN")) {
                throw new MalformedSourceException("File " + filename + " is not a valid Endnote file: First line does not contain \"FN\" tag.");
            }
            line = reader_.readLine();
            if(!line.startsWith("VR")) {
                throw new MalformedSourceException("File " + filename + " is not a valid Endnote file: Second line does not contain \"VR\" tag.");
            }

            MapRecord current_record = new MapRecord();
            Pattern endnote_pattern = Pattern.compile("(^[A-Z]{2}) ?(.*)$");
            String current_value = null;
            String current_tag = null;
            String current_field = null;
            int line_no = 2;

            while ((line = reader_.readLine()) != null) {
                line_no++;
                line = line.trim();
                //Ignore empty lines
                if (line.isEmpty() || line.equals("")) {
                    continue;
                }
                Matcher endnote_matcher = endnote_pattern.matcher(line);
                if(endnote_matcher.matches()) {
                    current_tag = endnote_matcher.group(1);
                    //We found the end record tag. Add the record to
                    //the record set, create a new record and continue
                    //with the next iteration.
                    if (current_tag.equals("ER")) {
                        ret.addRecord(current_record);
                        current_record = new MapRecord();
                        current_value = null;
                        current_tag = null;
                        current_field = null;
                        continue;
                    }

                    //End of file reached. Break out of the loop
                    if (current_tag.equals("EF")) {
                        break;
                    }
                    current_field = field_map_.get(current_tag);
                    current_value = endnote_matcher.group(2);
                }
                else {
                    current_value = line;
                }

                if (current_field == null && current_tag == null) {
                    logger_.debug("Parse error on line " + line_no + ": Tag expected\n" + line);
                    throw new MalformedSourceException("Parse error on line " + line_no + ": Tag expected\n" + line);
                }

                if (current_value == null) {
                    logger_.debug("Parse error on line " + line_no + ": Value expected.");
                    throw new MalformedSourceException("Parse error on line " + line_no + ": Value expected.");
                }
                if (current_field != null) {
                    current_record.addValue(current_field, new StringValue(current_value));
                }
            }
        } catch (IOException e) {
            logger_.info("Error while reading from file " + filename);
            throw new MalformedSourceException("Error while reading from file " + filename);
        }
        return ret;
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
