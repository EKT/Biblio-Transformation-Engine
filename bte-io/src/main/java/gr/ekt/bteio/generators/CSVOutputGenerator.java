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
    private List<String> m_fields;
    private CSVWriter m_writer;
    private static Logger m_logger = Logger.getLogger(CSVOutputGenerator.class);

    public CSVOutputGenerator(String filename, List<String> fields) throws FileNotFoundException, IOException {
        m_writer = new CSVWriter(new FileWriter(filename));
        m_fields = fields;
    }

    @Override
    public List<String> generateOutput(RecordSet record_set) {
        Iterator<Record> rec_it = record_set.iterator();
        while (rec_it.hasNext()) {
            Record rec = rec_it.next();
            String [] line = new String[m_fields.size()];
            for (int i = 0; i < m_fields.size(); i++) {
                List<Value> vals = rec.getValues(m_fields.get(i));
                if (vals.size() == 0) {
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
            m_writer.writeNext(line);
        }

        try{
            m_writer.flush();
        } catch(IOException e) {
            m_logger.info(e.getMessage());
        }
        return new ArrayList<String>();
    }

    @Override
    public List<String> generateOutput(RecordSet record_set, DataOutputSpec spec) {
        return generateOutput(record_set);
    }
}
