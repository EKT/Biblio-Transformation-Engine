package gr.ekt.bte.core;

import java.util.ArrayList;
import java.util.List;

public class Workflow {
    private List<ProcessingStep> process_;

    public Workflow() {
        process_ = new ArrayList<ProcessingStep>();
    }

    public void addStep(ProcessingStep step) {
        process_.add(step);
    }

    public RecordSet run(RecordSet records) {
        RecordSet recs = records;
        for (ProcessingStep step : process_) {
            recs = step.execute(recs);
        }

        return recs;
    }

    public void setProcess(List<ProcessingStep> process) {
        process_ = process;
    }
}
