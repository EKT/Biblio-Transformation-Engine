package gr.ekt.bte.core;

public interface OutputGenerator {
    public void generateOutput(RecordSet recs);
    public void writeOutput(RecordSet recs);
    public void writeOutput(RecordSet recs, DataOutputSpec spec);
}
