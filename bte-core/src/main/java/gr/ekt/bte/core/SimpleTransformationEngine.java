/**
 *
 */
package gr.ekt.bte.core;

import gr.ekt.bte.exceptions.BadTransformationSpec;
import gr.ekt.bte.exceptions.MalformedSourceException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * @author Panagiotis Koutsourakis <kutsurak@ekt.gr>
 *
 */
public class SimpleTransformationEngine extends TransformationEngine {
    static Logger m_logger = Logger.getLogger(SimpleTransformationEngine.class);

    public SimpleTransformationEngine() {
        m_dataLoader = null;
        m_outputGenerator = null;
        m_workflow = null;
    }

    public SimpleTransformationEngine(DataLoader dataLoader, OutputGenerator outputGenerator, Workflow workflow) {
        m_dataLoader = dataLoader;
        m_outputGenerator = outputGenerator;
        m_workflow = workflow;
    }

    /* (non-Javadoc)
     * @see gr.ekt.bte.core.TransformationEngine#transform(gr.ekt.bte.core.TransformationSpec)
     */
    @Override
    public TransformationResult transform (TransformationSpec spec)
            throws BadTransformationSpec, MalformedSourceException {
        long startTime = Calendar.getInstance().getTimeInMillis();

        TransformationLog log = new TransformationLog();

        RecordSet recs = null;
        try {
            recs = m_dataLoader.getRecords();
        } catch (MalformedSourceException e) {
            m_logger.info(e.getMessage());
            throw e;
        }

        List<String> output = new ArrayList<String>();
        if (recs.size() > 0) {
            m_logger.info("Loaded " + recs.size() + " records. Running workflow.");
            recs = m_workflow.run(recs);
            output.addAll(m_outputGenerator.generateOutput(recs));
        }

        long endTime = Calendar.getInstance().getTimeInMillis();
        long duration = endTime - startTime;
        m_logger.info("Transformation lasted: " + duration + " milliseconds.");

        log.setTransformationTime(duration);
        log.setStartTime(startTime);
        log.setEndTime(endTime);
        log.setProcessingStepList(m_workflow.getProcess());
        log.setEndOfInput(!m_dataLoader.hasMoreRecords());

        TransformationResult res = new TransformationResult(log, output);

        return res;
    }

}
