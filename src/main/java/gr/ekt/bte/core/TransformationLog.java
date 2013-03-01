package gr.ekt.bte.core;

import java.util.List;

public class TransformationLog {
    private TransformationSpec transformation_spec;
    private List<DataLoadingSpec> loading_spec_list;
    private long number_of_examined_records;
    private long start_time;
    private long end_time;
    private long transformation_time;
    private List<ProcessingStep> processing_step_list;
    private RecordSet records;

    public TransformationLog() {
        transformation_spec = null;
        loading_spec_list = null;
        number_of_examined_records = 0;
        start_time = 0;
        end_time = 0;
        transformation_time = 0;
        processing_step_list = null;
        records = null;
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

    public void setNumberOfExaminedRecords(long n_records) {
        number_of_examined_records = n_records;
    }

    public long setNumberOfExaminedRecords() {
        return number_of_examined_records;
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

    public void setProcessingStepList(List<ProcessingStep> lst) {
        processing_step_list = lst;
    }

    public List<ProcessingStep> getProcessingStepList() {
        return processing_step_list;
    }

    public void setRecords(RecordSet recs) {
        records = recs;
    }

    public RecordSet getRecords() {
        return records;
    }
}
