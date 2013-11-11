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

import gr.ekt.bte.exceptions.BadTransformationSpec;
import gr.ekt.bte.exceptions.MalformedSourceException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

public class StepTransformationEngine extends TransformationEngine {
    static Logger m_logger = Logger.getLogger(StepTransformationEngine.class);
    int m_currentOffset;


    public StepTransformationEngine() {
        m_dataLoader = null;
        m_outputGenerator = null;
        m_workflow = null;
    }

    public StepTransformationEngine(DataLoader dataLoader, OutputGenerator outputGenerator, Workflow workflow) {
        this.m_dataLoader = dataLoader;
        this.m_outputGenerator = outputGenerator;
        this.m_workflow = workflow;
    }

    @Override
    public TransformationResult transform(TransformationSpec spec) throws BadTransformationSpec, MalformedSourceException {
        if (!checkSpec(spec)) {
            throw new BadTransformationSpec("Bad Spec: " + spec.toString());
        }
        TransformationLog log = new TransformationLog();
        List<DataLoadingSpec> loadingSpecList = new ArrayList<DataLoadingSpec>();

        long startTime = Calendar.getInstance().getTimeInMillis();
        m_logger.info("Loading records");

        int n_records = 0;
        int keptRecords = 0;
        List<String> output = new ArrayList<String>();
        //boolean endOfInput = false;

        m_currentOffset = 0;
        while((output.size() < spec.getNumberOfRecords() || spec.getNumberOfRecords() == 0) && m_dataLoader.hasMoreRecords()) {
            RecordSet tmpRecs = null;
            DataLoadingSpec dlSpec = generateNextLoadingSpec(spec);
            loadingSpecList.add(dlSpec);
            try {
                tmpRecs = m_dataLoader.getRecords(dlSpec);
            } catch (MalformedSourceException e) {
                m_logger.info(e.getMessage());
                throw e;
            }

            //The number of records the data loader returns should be
            //less than or equal to the number requested.
            assert tmpRecs.size() <= dlSpec.getNumberOfRecords();

            //The data loader returned fewer records than requested, or
            //both the requested and the returned number of records is
            //0. Either of these conditions mean that the data loader
            //has reached the end of input.
            // if ((tmpRecs.size() < dlSpec.getNumberOfRecords() &&
            //      dlSpec.getNumberOfRecords() != 0) ||
            //     (tmpRecs.size() == 0 && dlSpec.getNumberOfRecords() == 0)) {
            //     endOfInput = true;
            // }
            n_records += tmpRecs.size();
            if (tmpRecs.size() == 0) {
                m_logger.info("Loaded 0 records. Skipping the rest of the workflow.");
                continue;
            }
            m_logger.info("Loaded " + tmpRecs.size() + " records. Running workflow.");
            tmpRecs = m_workflow.run(tmpRecs);
            keptRecords = tmpRecs.size();
            m_logger.info(tmpRecs.size() + " records remain after workflow.");

            //This is the case where the workflow has returned exactly
            //the number of records that we need or more.
            if (spec.getNumberOfRecords() != 0 && output.size() + keptRecords >= spec.getNumberOfRecords()) {
                int neededRecs = spec.getNumberOfRecords() - output.size();
                //current_offset should contain the *number* of
                //records that have been examined and either filtered
                //out or remained in the set in order for output to be
                //generated for spec.n_records records.
                m_currentOffset = n_records - (keptRecords - neededRecs);

                Iterator<Record> it = tmpRecs.iterator();
                RecordSet recsToKeep = new RecordSet();
                for(int i = 0; i < neededRecs; i++) {
                    recsToKeep.addRecord(it.next());
                }
                tmpRecs = recsToKeep;
            }
            else { //We have less records than we need.
                m_currentOffset = n_records;
            }
            if (tmpRecs.size() == 0) {
                m_logger.info("Empty record set. Skiping writting");
                continue;
            }
            m_logger.info("Writing result.");
            output.addAll(m_outputGenerator.generateOutput(tmpRecs));
        }
        long endTime = Calendar.getInstance().getTimeInMillis();
        long duration = endTime - startTime;
        log.setTransformationSpec(spec);
        log.setLoadingSpecList(loadingSpecList);
        log.setFirstUnexaminedRecord(spec.getOffset() + m_currentOffset);
        log.setStartTime(startTime);
        log.setEndTime(endTime);
        log.setTransformationTime(duration);
        log.setProcessingStepList(m_workflow.getProcess());
        log.setEndOfInput(!m_dataLoader.hasMoreRecords());

        TransformationResult res = new TransformationResult(log, output);
        return res;
    }
    //generates the next data loading spec for the iterative loading
    DataLoadingSpec generateNextLoadingSpec(TransformationSpec spec) {
        SimpleDataLoadingSpec ret = new SimpleDataLoadingSpec();

        ret.setNumberOfRecords(spec.getNumberOfRecords());
        ret.setOffset(spec.getOffset() + m_currentOffset);
        ret.setDataSetName(spec.getDataSetName());
        ret.setFromDate(spec.getFromDate());
        ret.setUntilDate(spec.getUntilDate());
        ret.setIdentifier(spec.getIdentifier());
        return ret;
    }

    //Checks a spec's consistency
    boolean checkSpec(TransformationSpec spec) {
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
