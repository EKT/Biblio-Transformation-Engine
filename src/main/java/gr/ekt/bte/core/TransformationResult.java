package gr.ekt.bte.core;

import java.util.List;

public class TransformationResult {
    private TransformationLog last_log;
    private List<String> output;

    public TransformationResult(TransformationLog log, List<String> output) {
        last_log = log;
        this.output = output;
    }

    public TransformationLog getLastLog() {
        return last_log;
    }

    public List<String> getOutput() {
        return output;
    }
}









