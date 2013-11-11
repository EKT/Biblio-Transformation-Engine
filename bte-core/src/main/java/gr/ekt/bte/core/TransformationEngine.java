package gr.ekt.bte.core;

import gr.ekt.bte.exceptions.BadTransformationSpec;
import gr.ekt.bte.exceptions.MalformedSourceException;

public abstract class TransformationEngine {

    protected DataLoader m_dataLoader;
    protected OutputGenerator m_outputGenerator;
    protected Workflow m_workflow;

    public TransformationEngine () {
        super();
    }

    public abstract TransformationResult transform (TransformationSpec spec) throws BadTransformationSpec, MalformedSourceException;

    /**
     * @return the dataLoader
     */
    public DataLoader getDataLoader () {
        return m_dataLoader;
    }

    public void setDataLoader (DataLoader dataLoader) {
        this.m_dataLoader = dataLoader;
    }

    /**
     * @return the outputGenerator
     */
    public OutputGenerator getOutputGenerator () {
        return m_outputGenerator;
    }

    public void setOutputGenerator (OutputGenerator outputGenerator) {
        this.m_outputGenerator = outputGenerator;
    }

    /**
     * @return the workflow
     */
    public Workflow getWorkflow () {
        return m_workflow;
    }

    public void setWorkflow (Workflow workflow) {
        this.m_workflow = workflow;
    }

}
