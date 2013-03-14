package gr.ekt.bte.core;

import java.util.List;

public interface Workflow {
    public RecordSet run(RecordSet records);
    public void addStepAfter(ProcessingStep step);
    public void addStepBefore(ProcessingStep step);
    public List<ProcessingStep> getProcess();
    public void setProcess(List<ProcessingStep> process);
}
