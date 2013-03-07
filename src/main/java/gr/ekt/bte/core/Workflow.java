package gr.ekt.bte.core;

import java.util.List;

public interface Workflow {
    public RecordSet run(RecordSet records);
    public void addStep(ProcessingStep step);
    public List<ProcessingStep> getSteps();
}
