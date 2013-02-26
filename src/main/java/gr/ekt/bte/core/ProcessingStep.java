package gr.ekt.bte.core;

public interface ProcessingStep {
    public RecordSet execute(RecordSet recs);
}
