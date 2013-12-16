package gr.ekt.bteio.generators;

import gr.ekt.bte.core.DataOutputSpec;
import gr.ekt.bte.core.OutputGenerator;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.Value;

import java.util.ArrayList;
import java.util.List;

public class ScreenOutputGenerator implements OutputGenerator {
    @Override
    public List<String> generateOutput(RecordSet recs) {

        System.out.println("=================Records=================");
        for (Record rec : recs) {
            System.out.println("--------------Record--------------");
            for (String str : rec.getFields()) {
                List<Value> vals = rec.getValues(str);
                if (vals != null) {
                    System.out.println(str + ": ");
                    for (Value val : vals) {
                        System.out.println("----: " + val.getAsString());
                    }
                }
            }
        }
        System.out.println("=================Records end=================");
        return new ArrayList<String>();
    }

    @Override
    public List<String> generateOutput(RecordSet recs, DataOutputSpec spec) {
        return generateOutput(recs);
    }
}
