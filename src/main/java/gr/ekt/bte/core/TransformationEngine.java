package gr.ekt.bte.core;

import org.apache.log4j.Logger;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.exceptions.BadTransformationSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Iterator;

public class TransformationEngine {
    private DataLoader loader;
    private OutputGenerator generator;
    private Workflow workflow;
    private static Logger logger = Logger.getLogger(TransformationEngine.class);
    private int current_offset;


    public TransformationEngine(DataLoader loader, OutputGenerator generator, Workflow workflow) {
        this.loader = loader;
        this.generator = generator;
        this.workflow = workflow;
    }

    public TransformationResult transform(TransformationSpec spec) throws BadTransformationSpec {
        if (!checkSpec(spec)) {
            throw new BadTransformationSpec("Bad Spec: " + spec.toString());
        }
        TransformationLog log = new TransformationLog();
        List<DataLoadingSpec> loading_spec_list = new ArrayList<DataLoadingSpec>();

        long start_time = Calendar.getInstance().getTimeInMillis();
        logger.info("Loading records");

        int n_records = 0;
        int kept_records = 0;
        List<String> output = new ArrayList<String>();
        boolean end_of_input = false;

        while(output.size() != spec.getNumberOfRecords()) {
            RecordSet tmp_recs = null;
            DataLoadingSpec dls = generateNextLoadingSpec(spec);
            loading_spec_list.add(dls);
            try {
                tmp_recs = loader.getRecords(dls);
            } catch (EmptySourceException e) {
                logger.info(e.getStackTrace());
                return null;
            }

            //no more records from the source.
            if (tmp_recs.size() == 0) {
                end_of_input = true;
                break;
            }
            n_records += tmp_recs.size();
            logger.info("Loaded " + tmp_recs.size() + " records. Running workflow.");
            tmp_recs = workflow.run(tmp_recs);
            kept_records = tmp_recs.size();
            logger.info(tmp_recs.size() + " records remain after workflow.");
            if (output.size() + kept_records >= spec.getNumberOfRecords()) {
                int needed_recs = spec.getNumberOfRecords() - output.size();
                //current_offset should contain the *number* of
                //records that have been examined and either filtered
                //out or remained in the set in order for output to be
                //generated for spec.n_records records.
                current_offset = n_records - (kept_records - needed_recs);  //Is this computation correct, or do we need a +1/-1?


                Iterator<Record> it = tmp_recs.iterator();
                RecordSet recs_to_keep = new RecordSet();
                for(int i = 0; i < needed_recs; i++) {
                    recs_to_keep.addRecord(it.next());
                }
                tmp_recs = recs_to_keep;
            }
            logger.info("Writing result.");
            output.addAll(generator.generateOutput(tmp_recs));
        }
        long end_time = Calendar.getInstance().getTimeInMillis();
        long duration = end_time - start_time;
        log.setTransformationSpec(spec);
        log.setLoadingSpecList(loading_spec_list);
        log.setFirstUnexaminedRecord(current_offset + 1);
        log.setStartTime(start_time);
        log.setEndTime(end_time);
        log.setTransformationTime(duration);
        log.setProcessingStepList(workflow.getSteps());
        log.setEndOfInput(end_of_input);

        TransformationResult res = new TransformationResult(log, output);
        return res;
    }

    public void setDataLoader(DataLoader loader) {
        this.loader = loader;
    }

    public void setOutputGenerator(OutputGenerator generator) {
        this.generator = generator;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    private DataLoadingSpec generateNextLoadingSpec(TransformationSpec spec) {
        SimpleDataLoadingSpec ret = new SimpleDataLoadingSpec();

        ret.setNumberOfRecords(spec.getNumberOfRecords());
        ret.setOffset(spec.getOffset() + current_offset + 1);
        ret.setDataSetName(spec.getDataSetName());
        ret.setFromDate(spec.getFromDate());
        ret.setUntilDate(spec.getUntilDate());
        ret.setIdentifier(spec.getIdentifier());
        return ret;
    }

    private boolean checkSpec(TransformationSpec spec) {
        // The spec must have EITHER an id OR other info.
        if (spec.getIdentifier() != null &&
            (spec.getNumberOfRecords() != 1 ||
             spec.getOffset() != 0 ||
             spec.getDataSetName() != null ||
             spec.getFromDate() != null ||
             spec.getUntilDate() != null)) {
            return false;
        }

        return true;
    }
}
