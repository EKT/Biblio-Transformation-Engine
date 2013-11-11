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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import gr.ekt.bte.dataloader.DataLoaderMock;
import gr.ekt.bte.exceptions.BadTransformationSpec;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.outputgenerator.OutputGeneratorMock;
import gr.ekt.bte.workflow.FullFilter;
import gr.ekt.bte.workflow.HalfFilter;

import org.junit.Before;
import org.junit.Test;

public class StepTransformationEngineTest {
    private DataLoader dl;
    private OutputGenerator og;
    private Workflow empty_workflow;
    private Workflow full_filter_workflow;
    private Workflow half_filter_workflow;

    @Before
    public void setUp() {
        dl = new DataLoaderMock();
        og = new OutputGeneratorMock();
        empty_workflow = new LinearWorkflow();
        full_filter_workflow = new LinearWorkflow();
        full_filter_workflow.addStepAtEnd(new FullFilter());

        half_filter_workflow = new LinearWorkflow();
        half_filter_workflow.addStepAtEnd(new HalfFilter());
    }

    @Test
    public void testTransform() {
        try {
            TransformationEngine te = new StepTransformationEngine(dl, og, empty_workflow);
            TransformationSpec spec = new TransformationSpec();
            spec.setNumberOfRecords(100);

            TransformationResult res = null;
            try {
                // Request 100 records
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals("The output size should be 100.", 100, res.getOutput().size());
            assertEquals("The first unexamined record should be 100.", 100, res.getLastLog().getFirstUnexaminedRecord());
            assertFalse("We should not be at the end of input.", res.getLastLog().getEndOfInput());

            spec.setOffset(res.getLastLog().getFirstUnexaminedRecord());
            try {
                // Request 100 records
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals("The output size should be 100.", 100, res.getOutput().size());
            assertEquals("The first unexamined record should be 200.", 200, res.getLastLog().getFirstUnexaminedRecord());
            assertFalse("We should not be at the end of input.", res.getLastLog().getEndOfInput());

            spec.setOffset(res.getLastLog().getFirstUnexaminedRecord());
            // Request 350 records (we should only have 300 to return)
            spec.setNumberOfRecords(350);
            try {
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals("The output size should be 300 records.", 300, res.getOutput().size());
            assertEquals("The first unexaminded record should be 500", 500, res.getLastLog().getFirstUnexaminedRecord());
            assertTrue("We should be at the end of input", res.getLastLog().getEndOfInput());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTransformWithFullFilter() {
        try {
            TransformationEngine te = new StepTransformationEngine(dl, og, full_filter_workflow);

            TransformationSpec spec = new TransformationSpec();
            spec.setNumberOfRecords(100);

            TransformationResult res = null;
            try {
                // Request 100 records
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            // We should not get any records at all
            assertEquals("The output size should be 0.", 0, res.getOutput().size());
            assertEquals("The first unexamined record should be 500.", 500, res.getLastLog().getFirstUnexaminedRecord());
            assertTrue("We should be at the end of input.", res.getLastLog().getEndOfInput());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testTransformWithHalfFilter() {
        try {
            TransformationEngine te = new StepTransformationEngine(dl, og, half_filter_workflow);

            TransformationSpec spec = new TransformationSpec();
            spec.setNumberOfRecords(100);

            TransformationResult res = null;
            try {
                // Request 100 records
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals("The output size should be 100.", 100, res.getOutput().size());
            assertEquals("The first unexamined record should be 151.", 151, res.getLastLog().getFirstUnexaminedRecord());
            assertFalse("We should not be at the end of input.", res.getLastLog().getEndOfInput());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }
}
