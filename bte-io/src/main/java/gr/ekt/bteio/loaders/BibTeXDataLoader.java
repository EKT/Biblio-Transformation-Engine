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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.BibTeXString;
import org.jbibtex.Key;
import org.jbibtex.ParseException;


public class BibTeXDataLoader extends FileDataLoader {
    private static Logger logger_ = Logger.getLogger(BibTeXDataLoader.class);
    private Map<String, String> field_map_;
    private FileReader reader_;

    public BibTeXDataLoader() {
        super();
        field_map_ = null;
        reader_ = null;
    }

    public BibTeXDataLoader(String filename, Map<String, String> fields) throws EmptySourceException {
        super(filename);
        field_map_ = fields;

        try {
            reader_ = new FileReader(new File(filename));
        } catch(IOException e) {
            logger_.info("Problem loading file: " + filename);
            throw new EmptySourceException("Problem loading file: " + filename);
        }
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        BibTeXDatabase bibtex_entries = null;

        try {
            bibtex_entries = loadFile();
        } catch (IOException e) {
            logger_.info("Problem loading file: " + filename);
            throw new MalformedSourceException("Problem loading file: " + filename);
        } catch (ParseException e) {
            logger_.info("Bad input file: " + filename);
            throw new MalformedSourceException("Bad input file: " + filename);
        }

        //This should not happen, but be prepared to handle it anyway
        if (bibtex_entries == null) {
            logger_.debug("Unknown error while reading file: " + filename);
            throw new EmptySourceException("Unknown error while reading file: " + filename);
        }
        RecordSet ret = new RecordSet();

        Collection<BibTeXEntry> entries = bibtex_entries.getEntries().values();
        for (BibTeXEntry entry : entries) {
            MapRecord rec = new MapRecord();
            for (Map.Entry<String, String> en : field_map_.entrySet()) {
                String record_key = en.getValue();
                Key key = new Key(en.getKey());
                org.jbibtex.Value bib_value = entry.getField(key);

                if (bib_value != null) {
                    String latexString = bib_value.toUserString();

                    if (rec.hasField(record_key)) {
                        List<Value> vals = rec.getValues(record_key);
                        vals.add(new StringValue(latexString));
                        rec.updateField(record_key, vals);
                    }
                    else {
                        List<Value> vals = new ArrayList<Value>();
                        vals.add(new StringValue(latexString));
                        rec.addField(record_key, vals);
                    }
                }
            }

            ret.addRecord(rec);
        }

        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        return getRecords();
    }

    @Override
    protected void finalize() throws Throwable {
        reader_.close();
    }

    private BibTeXDatabase loadFile() throws IOException, ParseException, EmptySourceException {
        if (reader_ == null) {
            throw new EmptySourceException("Input file is not open");
        }
        BibTeXParser parser = new BibTeXParser()
            {

                @Override
                public void checkStringResolution(Key key, BibTeXString string){
                    if(string == null){
                        logger_.debug("Unresolved string: \"" + key.getValue() + "\"");
                    }
                }

                @Override
                public void checkCrossReferenceResolution(Key key, BibTeXEntry entry){

                    if(entry == null){
                        logger_.debug("Unresolved cross-reference: \"" + key.getValue() + "\"");
                    }
                }
            };

        return parser.parse(reader_);
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

    @Override
    public void setFilename(String filename) {
        this.filename = filename;

        try {
            reader_ = new FileReader(new File(filename));
        } catch(IOException e) {
            logger_.info("Problem loading file: " + filename);
            reader_ = null;
        }
    }
}
