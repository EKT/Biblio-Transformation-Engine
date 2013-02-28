package gr.ekt.bte.core;

import org.apache.log4j.Logger;
import gr.ekt.bte.exceptions.EmptySourceException;

public class TransformationEngine {
    private DataLoader loader_;
    private OutputGenerator generator_;
    private Workflow workflow_;
    private RecordSet records_;
    private static Logger logger = Logger.getLogger(TransformationEngine.class);


    public TransformationEngine(DataLoader loader, OutputGenerator generator, Workflow workflow) {
        records_ = null;
        loader_ = loader;
        generator_ = generator;
        workflow_ = workflow;
    }

    public RecordSet transform() {
        logger.info("Loading records");
        try {
            records_ = loader_.getRecords();
        } catch (EmptySourceException e) {
            logger.info(e.getStackTrace());
            return null;
        }
        logger.info("Loaded " + records_.size() + " records. Running workflow.");
        records_ = workflow_.run(records_);
        logger.info(records_.size() + " records remain after workflow. Writing result.");
        generator_.writeOutput(records_);

        return records_;
    }
}
