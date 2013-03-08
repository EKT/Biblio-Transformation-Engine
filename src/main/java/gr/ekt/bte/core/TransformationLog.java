package gr.ekt.bte.core;

import java.util.List;

public class TransformationLog {
    private TransformationSpec transformation_spec;
    private List<DataLoadingSpec> loading_spec_list;
    private long first_unexamined_record;
    private long start_time;
    private long end_time;
    private long transformation_time;
    private List<ProcessingStep> processing_step_list;
    private boolean end_of_input

    public TransformationLog() {
        transformation_spec = null;
        loading_spec_list = null;
        first_unexamined_record = 0;
        start_time = 0;
        end_time = 0;
        transformation_time = 0;
        processing_step_list = null;
        end_of_input = false;
    }

    public void setTransformationSpec(TransformationSpec spec) {
        transformation_spec = spec;
    }

    public TransformationSpec getTransformationSpec() {
        return transformation_spec;
    }

    public void setLoadingSpecList(List<DataLoadingSpec> lst) {
        loading_spec_list = lst;
    }

    public List<DataLoadingSpec> getLoadingSpecList() {
        return loading_spec_list;
    }

    public void setFirstUnexaminedRecord(long n_records) {
        first_unexamined_record = n_records;
    }

    public long getFirstUnexaminedRecord() {
        return first_unexamined_record;
    }

    public void setStartTime(long st_time) {
        start_time = st_time;
    }

    public long getStartTime() {
        return start_time;
    }

    public void setEndTime(long e_time) {
        end_time = e_time;
    }

    public long getEndTime() {
        return end_time;
    }

    public void setTransformationTime(long t_time) {
        transformation_time = t_time;
    }

    public long getTransformationTime() {
        return transformation_time;
    }

    public void setProcessingStepList(List<ProcessingStep> lst) {
        processing_step_list = lst;
    }

    public List<ProcessingStep> getProcessingStepList() {
        return processing_step_list;
    }

    public void setEndOfInput(boolean end_of_input) {
        this.end_of_input = end_of_input;
    }

    public boolean getEndOfInput() {
        return end_of_input;
    }
}
