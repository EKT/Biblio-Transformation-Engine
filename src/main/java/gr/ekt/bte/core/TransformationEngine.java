/**
 * Copyright (c) 2007-2013, National Documentation Centre (EKT, www.ekt.gr)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     Neither the name of the National Documentation Centre nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written
 *     permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package gr.ekt.bte.core;

import org.apache.log4j.Logger;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.exceptions.BadTransformationSpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Iterator;

public class TransformationEngine {
    private DataLoader dataLoader;
    private OutputGenerator outputGenerator;
    private Workflow workflow;
    private static Logger logger = Logger.getLogger(TransformationEngine.class);
    private int current_offset;


    public TransformationEngine() {
        dataLoader = null;
        outputGenerator = null;
        workflow = null;
    }

    public TransformationEngine(DataLoader dataLoader, OutputGenerator outputGenerator, Workflow workflow) {
        this.dataLoader = dataLoader;
        this.outputGenerator = outputGenerator;
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

        current_offset = 0;
        while((output.size() < spec.getNumberOfRecords() && !end_of_input) || (spec.getNumberOfRecords() == 0 && !end_of_input)) {
            RecordSet tmp_recs = null;
            DataLoadingSpec dls = generateNextLoadingSpec(spec);
            loading_spec_list.add(dls);
            try {
                tmp_recs = dataLoader.getRecords(dls);
            } catch (EmptySourceException e) {
                logger.info(e.getStackTrace());
                return null;
            }

            //The dataloader returned fewer records than requested.
            //This means that it has reached the end of input.
            if (tmp_recs.size() < dls.getNumberOfRecords() && dls.getNumberOfRecords() != 0) {
                end_of_input = true;
                //break;
            } else if (tmp_recs.size() > dls.getNumberOfRecords() && dls.getNumberOfRecords() != 0) {
                //Something went really wrong... throw an exception.
            } else if (tmp_recs.size() == 0 && dls.getNumberOfRecords() == 0) {
                end_of_input = true;
            }
            n_records += tmp_recs.size();
            logger.info("Loaded " + tmp_recs.size() + " records. Running workflow.");
            tmp_recs = workflow.run(tmp_recs);
            kept_records = tmp_recs.size();
            logger.info(tmp_recs.size() + " records remain after workflow.");

            //This is the case where the workflow has returned exactly
            //the number of records that we need or more.
            if (spec.getNumberOfRecords() != 0 && output.size() + kept_records >= spec.getNumberOfRecords()) {
                int needed_recs = spec.getNumberOfRecords() - output.size();
                //current_offset should contain the *number* of
                //records that have been examined and either filtered
                //out or remained in the set in order for output to be
                //generated for spec.n_records records.
                current_offset = n_records - (kept_records - needed_recs);


                Iterator<Record> it = tmp_recs.iterator();
                RecordSet recs_to_keep = new RecordSet();
                for(int i = 0; i < needed_recs; i++) {
                    recs_to_keep.addRecord(it.next());
                }
                tmp_recs = recs_to_keep;
            }
            else { //We have less records than we need.
                current_offset = n_records;
            }
            logger.info("Writing result.");
            output.addAll(outputGenerator.generateOutput(tmp_recs));
        }
        long end_time = Calendar.getInstance().getTimeInMillis();
        long duration = end_time - start_time;
        log.setTransformationSpec(spec);
        log.setLoadingSpecList(loading_spec_list);
        log.setFirstUnexaminedRecord(spec.getOffset() + current_offset);
        log.setStartTime(start_time);
        log.setEndTime(end_time);
        log.setTransformationTime(duration);
        log.setProcessingStepList(workflow.getProcess());
        log.setEndOfInput(end_of_input);

        TransformationResult res = new TransformationResult(log, output);
        return res;
    }

    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    public void setOutputGenerator(OutputGenerator outputGenerator) {
        this.outputGenerator = outputGenerator;
    }

    public void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    private DataLoadingSpec generateNextLoadingSpec(TransformationSpec spec) {
        SimpleDataLoadingSpec ret = new SimpleDataLoadingSpec();

        ret.setNumberOfRecords(spec.getNumberOfRecords());
        ret.setOffset(spec.getOffset() + current_offset);
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
