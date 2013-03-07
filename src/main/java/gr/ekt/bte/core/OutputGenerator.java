package gr.ekt.bte.core;

import java.util.List;

public interface OutputGenerator {
    public List<String> generateOutput(RecordSet recs);
    public List<String> generateOutput(RecordSet recs, DataOutputSpec spec);
}
