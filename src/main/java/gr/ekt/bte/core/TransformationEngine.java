package gr.ekt.bte.core;

public class TransformationEngine {
    private DataLoader loader_;
    private OutputGenerator generator_;
    private Workflow workflow_;
    private RecordSet records_;


    public TransformationEngine() {
        records_ = new RecordSet();
    }

    public RecordSet transform() {
        return records_;
    }

    public void setDataLoader(DataLoader loader) {
        loader_ = loader;
    }

    public void setOutputGenerator(OutputGenerator generator) {
        generator_ = generator;
    }

    public void setWorkflow(Workflow workflow) {
        workflow_ = workflow;
    }
}
