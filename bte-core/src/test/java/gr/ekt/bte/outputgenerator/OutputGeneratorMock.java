package gr.ekt.bte.outputgenerator;

import gr.ekt.bte.core.DataOutputSpec;
import gr.ekt.bte.core.OutputGenerator;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;

import java.util.ArrayList;
import java.util.List;

public class OutputGeneratorMock implements OutputGenerator {
    public List<String> generateOutput(RecordSet recs) {
        ArrayList<String> ret = new ArrayList<String>();

        for(Record rec : recs) {
            ret.add(rec.getValues("id").get(0).getAsString());
        }

        return ret;
    }

    public List<String> generateOutput(RecordSet recs, DataOutputSpec spec) {
        return generateOutput(recs);
    }
}
