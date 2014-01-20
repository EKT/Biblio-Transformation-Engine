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

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

public class CSVDataLoader extends FileDataLoader {
    private static Logger logger_ = Logger.getLogger(CSVDataLoader.class);
    private CSVReader reader_;
    private Map<Integer, String> field_map_;
    private int skip_lines_ = 0;
    private char separator_ = ',';
    private char quote_char_ = '"';
    private String value_separator_ = null;

    public CSVDataLoader() {
        super();
        reader_ = null;
        field_map_ = null;
    }

    public CSVDataLoader(String filename, Map<Integer, String> fields) {
        super(filename);
        field_map_ = fields;
    }

    public CSVDataLoader(String filename, Map<Integer, String> fields, char separator, char quote_char, int skip_lines, String value_separator) {
        super(filename);
        field_map_ = fields;
        separator_ = separator;
        quote_char_ = quote_char;
        skip_lines_ = skip_lines;
        value_separator_ = value_separator;
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException, EmptySourceException {
        openReader();
        if (reader_ == null) {
            throw new EmptySourceException("Input file is not open");
        }
        RecordSet rs = null;
        try {
            String [] next_line;
            rs = new RecordSet();
            while((next_line = reader_.readNext()) != null) {
                MapRecord rec = new MapRecord();
                for(Map.Entry<Integer,String> en : field_map_.entrySet()) {
                    int i = en.getKey();
                    if (next_line.length <= i) {
                        throw new MalformedSourceException("The requested column " + i + " does not exist");
                    }
                    if (value_separator_ != null) {
                        String values[] = next_line[i].split(value_separator_);
                        for(int j = 0; j < values.length; j++) {
                            rec.addValue(field_map_.get(i), new StringValue(values[j]));
                        }
                    }
                    else {
                        rec.addValue(field_map_.get(i), new StringValue(next_line[i]));
                    }
                }
                rs.addRecord(rec);
            }
        } catch(IOException e) {
            logger_.info(e.getMessage());
            throw new MalformedSourceException("");
        }

        return rs;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        //Not using the DataLoadingSpec for the moment
        if (spec.getOffset() > 0) {
            return new RecordSet();
        }
        return getRecords();
    }

    @Override
    protected void finalize() throws Throwable {
        reader_.close();
    }

    private void openReader() throws EmptySourceException {
        try {
            reader_ = new CSVReader(new FileReader(filename), separator_, quote_char_, skip_lines_);
        } catch (FileNotFoundException e) {
            throw new EmptySourceException("File " + filename + " not found");
        }
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

    /**
     * @return the fields_
     */
    public Map<Integer, String> getFieldMap() {
        return field_map_;
    }

    /**
     * @param fields_ the fields_ to set
     */
    public void setFieldMap(Map<Integer, String> fields_) {
        this.field_map_ = fields_;
    }

    /**
     * @return the skip_lines_
     */
    public int getSkipLines() {
        return skip_lines_;
    }

    /**
     * @param skip_lines_ the skip_lines_ to set
     */
    public void setSkipLines(int skip_lines_) {
        this.skip_lines_ = skip_lines_;
    }

    /**
     * @return the separator_
     */
    public char getSeparator() {
        return separator_;
    }

    /**
     * @param separator_ the separator_ to set
     */
    public void setSeparator(char separator_) {
        this.separator_ = separator_;
    }

    /**
     * @return the quote_char_
     */
    public char getQuoteChar() {
        return quote_char_;
    }

    /**
     * @param quote_char_ the quote_char_ to set
     */
    public void setQuoteChar(char quote_char_) {
        this.quote_char_ = quote_char_;
    }

    /**
     * @return the value_separator_
     */
    public String getValueSeparator() {
        return value_separator_;
    }

    /**
     * @param value_separator_ the value_separator_ to set
     */
    public void setValueSeparator(String value_separator_) {
        this.value_separator_ = value_separator_;
    }
}
