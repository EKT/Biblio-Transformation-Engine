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
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.BibTeXParser;
import org.jbibtex.BibTeXString;
import org.jbibtex.Key;
import org.jbibtex.LaTeXObject;
import org.jbibtex.LaTeXParser;
import org.jbibtex.LaTeXPrinter;
import org.jbibtex.ParseException;


public class BibTeXDataLoader extends FileDataLoader {
    private static Logger logger_ = Logger.getLogger(BibTeXDataLoader.class);
    private BibTeXDatabase bibtex_entries_;

    public BibTeXDataLoader(String filename) {
        super(filename);
        try {
            loadFile();
        } catch(IOException e) {
            logger_.info("Problem loading file: " + filename);
            bibtex_entries_ = null;
        } catch(ParseException e) {
            logger_.info("File: " + filename + " is not a valid BibTeX file");
            bibtex_entries_ = null;
        }
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        if (bibtex_entries_ == null) {
            throw new EmptySourceException("Could not read from file: " + filename);
        }
        RecordSet ret = new RecordSet();

        Collection<BibTeXEntry> entries = bibtex_entries_.getEntries().values();
        for (BibTeXEntry entry : entries) {
            MapRecord rec = new MapRecord();
            Key key = entry.getKey();
            org.jbibtex.Value bib_value = entry.getField(key);
            String plainTextString = null;

            if (bib_value != null) {
                String latexString = null;
                try {
                    latexString = bib_value.toUserString();
                    List<LaTeXObject> objects = parseLaTeX(latexString);
                    plainTextString = printLaTeX(objects);

                } catch (IOException e) {
                    logger_.info("Invalid string: " + latexString);
                    throw new MalformedSourceException("Invalid string: " + latexString);
                } catch (ParseException e) {
                    logger_.info("Invalid string: " + latexString);
                    throw new MalformedSourceException("Invalid string: " + latexString);
                }
            }

            if (!rec.hasField(key.getValue())) {
                List<Value> vals = rec.getValues(key.getValue());
                vals.add(new StringValue(plainTextString));
                rec.updateField(key.getValue(), vals);
            }
            else {
                List<Value> vals = new ArrayList<Value>();
                vals.add(new StringValue(plainTextString));
                rec.addField(key.getValue(), vals);
            }

            ret.addRecord(rec);
        }

        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        return getRecords();
    }

    static
    public List<LaTeXObject> parseLaTeX(String string) throws IOException, ParseException {
        Reader reader = new StringReader(string);

        try {
            LaTeXParser parser = new LaTeXParser();

            return parser.parse(reader);
        } finally {
            reader.close();
        }
    }

    static
    public String printLaTeX(List<LaTeXObject> objects){
        LaTeXPrinter printer = new LaTeXPrinter();

        return printer.print(objects);
    }

    private void loadFile() throws IOException, ParseException {
        Reader reader = new FileReader(new File(filename));

        BibTeXParser parser = new BibTeXParser()
            {
                @Override
                public void checkStringResolution(Key key, BibTeXString string) {
                    if(string == null) {
                        logger_.info("Unresolved string: \"" + key.getValue() + "\"");
                    }
                }

                @Override
                public void checkCrossReferenceResolution(Key key, BibTeXEntry entry){
                    if(entry == null){
                        logger_.info("Unresolved cross-reference: \"" + key.getValue() + "\"");
                    }
                }

            };

        bibtex_entries_ = parser.parse(reader);

        reader.close();
    }
}
