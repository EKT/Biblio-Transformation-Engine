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
