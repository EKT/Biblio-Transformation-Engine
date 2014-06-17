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
package gr.ekt.bteio.generators;

import java.util.List;
import java.util.Iterator;
import java.io.FileWriter;

import au.com.bytecode.opencsv.CSVWriter;

import org.apache.log4j.Logger;

import gr.ekt.bte.core.OutputGenerator;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.DataOutputSpec;
import gr.ekt.bte.core.Value;
import gr.ekt.bte.core.Record;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class CSVOutputGenerator implements OutputGenerator {
    private List<String> fields_;
    private CSVWriter writer_;
    private static Logger logger_ = Logger.getLogger(CSVOutputGenerator.class);

    public CSVOutputGenerator(String filename, List<String> fields) throws FileNotFoundException, IOException {
        writer_ = new CSVWriter(new FileWriter(filename));
        fields_ = fields;
    }

    @Override
    public List<String> generateOutput(RecordSet record_set) {
        Iterator<Record> rec_it = record_set.iterator();
        while (rec_it.hasNext()) {
            Record rec = rec_it.next();
            String [] line = new String[fields_.size()];
            for (int i = 0; i < fields_.size(); i++) {
                List<Value> vals = rec.getValues(fields_.get(i));
                if (vals == null || vals.size() == 0) {
                    continue;
                }

                String val = "";
                for (int j = 0; j < vals.size(); j++) {
                    val += vals.get(j).getAsString();
                    if(j == vals.size() - 1) {
                        break;
                    }
                    val += "||";
                }

                line[i] = val;
            }
            writer_.writeNext(line);
        }

        try{
            writer_.flush();
        } catch(IOException e) {
            logger_.info(e.getMessage());
        }
        return new ArrayList<String>();
    }

    @Override
    public List<String> generateOutput(RecordSet record_set, DataOutputSpec spec) {
        return generateOutput(record_set);
    }
}
