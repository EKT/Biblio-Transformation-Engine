package gr.ekt.bte.core;

import java.util.ArrayList;
import java.util.List;

public class LinearWorkflow implements Workflow {
    private List<ProcessingStep> process_;

    public LinearWorkflow() {
        process_ = new ArrayList<ProcessingStep>();
    }

    @Override
    public void addStep(ProcessingStep step) {
        process_.add(step);
    }

    @Override
    public RecordSet run(RecordSet records) {
        RecordSet recs = records;
        for (ProcessingStep step : process_) {
            recs = step.execute(recs);
        }

        return recs;
    }

    @Override
    public List<ProcessingStep> getSteps() {
        return process_;
    }

    public void setProcess(List<ProcessingStep> process) {
        process_ = process;
    }
}
