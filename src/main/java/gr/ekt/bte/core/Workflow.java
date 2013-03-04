package gr.ekt.bte.core;

public interface Workflow {
    public RecordSet run(RecordSet records);
    public void addStep(ProcessingStep step);
}
