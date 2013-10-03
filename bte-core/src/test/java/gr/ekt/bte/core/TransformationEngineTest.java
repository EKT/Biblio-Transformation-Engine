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

import static org.junit.Assert.*;
import gr.ekt.bte.exceptions.BadTransformationSpec;

import gr.ekt.bte.exceptions.MalformedSourceException;

import org.junit.Before;
import org.junit.Test;

public class TransformationEngineTest {
    DataLoader dl;
    OutputGenerator og;
    Workflow empty_workflow;
    Workflow full_filter_workflow;
    Workflow half_filter_workflow;

    @Before
    public void setUp() {
        dl = new SimpleDataLoader();
        og = new SimpleOutputGenerator();
        empty_workflow = new LinearWorkflow();
        full_filter_workflow = new LinearWorkflow();
        full_filter_workflow.addStepAtEnd(new FullFilter());

        half_filter_workflow = new LinearWorkflow();
        half_filter_workflow.addStepAtEnd(new HalfFilter());
    }

    // @Test(expected = BadTransformationSpec.class)
    // public void testBadSpec() {
    //     TransformationSpec spec = new TransformationSpec("foo");
    //     spec.setOffset(100);
    //     te.transform(spec);
    // }

    @Test
    public void testTransform() {
        try {
            TransformationEngine te = new TransformationEngine(dl, og, empty_workflow);
            TransformationSpec spec = new TransformationSpec();
            spec.setNumberOfRecords(100);

            TransformationResult res = null;
            try {
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals(100, res.getOutput().size());
            assertEquals(100, res.getLastLog().getFirstUnexaminedRecord());
            assertFalse(res.getLastLog().getEndOfInput());

            spec.setOffset(res.getLastLog().getFirstUnexaminedRecord());
            try {
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals(100, res.getOutput().size());
            assertEquals(200, res.getLastLog().getFirstUnexaminedRecord());
            assertFalse(res.getLastLog().getEndOfInput());

            spec.setOffset(res.getLastLog().getFirstUnexaminedRecord());
            spec.setNumberOfRecords(350);
            try {
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals(300, res.getOutput().size());
            assertEquals(500, res.getLastLog().getFirstUnexaminedRecord());
            assertTrue(res.getLastLog().getEndOfInput());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testTransformWithFullFilter() {
        try {
            TransformationEngine te = new TransformationEngine(dl, og, full_filter_workflow);

            TransformationSpec spec = new TransformationSpec();
            spec.setNumberOfRecords(100);

            TransformationResult res = null;
            try {
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals(0, res.getOutput().size());
            assertEquals(500, res.getLastLog().getFirstUnexaminedRecord());
            assertTrue(res.getLastLog().getEndOfInput());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testTransformWithHalfFilter() {
        try {
            TransformationEngine te = new TransformationEngine(dl, og, half_filter_workflow);

            TransformationSpec spec = new TransformationSpec();
            spec.setNumberOfRecords(100);

            TransformationResult res = null;
            try {
                res = te.transform(spec);
            } catch(BadTransformationSpec e) {
                fail(e.getMessage());
            }

            assertEquals(100, res.getOutput().size());
            assertEquals(151, res.getLastLog().getFirstUnexaminedRecord());
            assertFalse(res.getLastLog().getEndOfInput());
        } catch (MalformedSourceException e) {
            fail(e.getMessage());
        }
    }
}
