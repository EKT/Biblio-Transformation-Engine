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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import gr.ekt.bte.dataloader.DataLoaderMock;
import gr.ekt.bte.exceptions.BadTransformationSpec;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.outputgenerator.OutputGeneratorMock;
import gr.ekt.bte.workflow.EvenNumberFilter;
import gr.ekt.bte.workflow.FullFilter;

import org.junit.Before;
import org.junit.Test;

public class SimpleTransformationEngineTest {
    private DataLoader m_loader;
    private OutputGenerator m_generator;
    private Workflow m_emptyWorkflow;
    private Workflow m_fullFilterWorkflow;
    private Workflow m_halfFilterWorkflow;

    @Before
    public void setUp() {
        m_loader = new DataLoaderMock();
        m_generator = new OutputGeneratorMock();

        m_emptyWorkflow = new LinearWorkflow();

        m_fullFilterWorkflow = new LinearWorkflow();
        m_fullFilterWorkflow.addStepAtEnd(new FullFilter());

        m_halfFilterWorkflow = new LinearWorkflow();
        m_halfFilterWorkflow.addStepAtEnd(new EvenNumberFilter());
    }

    @Test
    public void testEmptyTransform() {
        try {
            TransformationEngine te = new SimpleTransformationEngine(m_loader, m_generator, m_emptyWorkflow);

            TransformationResult res = te.transform(null);

            assertEquals("The output size should be 500.", 500, res.getOutput().size());
            assertTrue("We should be at the end of input.", res.getLastLog().getEndOfInput());
        } catch (BadTransformationSpec e) {
            fail(e.getMessage());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testHalfTransform() {
        try {
            TransformationEngine te = new SimpleTransformationEngine(m_loader, m_generator, m_halfFilterWorkflow);
            TransformationResult res = te.transform(null);

            assertEquals("The output size should be 250.", 250, res.getOutput().size());
            assertTrue("We should be at the end of input.", res.getLastLog().getEndOfInput());
        } catch (BadTransformationSpec e) {
            fail(e.getMessage());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testFullFilter() {
        try {
            TransformationEngine te = new SimpleTransformationEngine(m_loader, m_generator, m_fullFilterWorkflow);
            TransformationResult res = te.transform(null);

            assertEquals("The output size should be 0.", 0, res.getOutput().size());
            assertTrue("We should be at the end of input.", res.getLastLog().getEndOfInput());
        } catch (BadTransformationSpec e) {
            fail(e.getMessage());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }
}
