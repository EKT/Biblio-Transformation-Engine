package gr.ekt.bte.core;

import java.util.List;
import java.util.ArrayList;

public class SimpleOutputGenerator implements OutputGenerator {
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
