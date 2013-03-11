package gr.ekt.bte.core;

import java.util.ArrayList;
import java.util.List;

public class LinearWorkflow implements Workflow {
    private List<ProcessingStep> process;

    public LinearWorkflow() {
        this.process = new ArrayList<ProcessingStep>();
    }

    @Override
    public void addStep(ProcessingStep step) {
        process.add(step);
    }

    @Override
    public RecordSet run(RecordSet records) {
        RecordSet recs = records;
        for (ProcessingStep step : process) {
            recs = step.execute(recs);
        }

        return recs;
    }

    @Override
    public List<ProcessingStep> getSteps() {
        return process;
    }

    public void setProcess(List<ProcessingStep> process) {
        this.process = process;
    }
}
