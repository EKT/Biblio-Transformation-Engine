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
