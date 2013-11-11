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

import gr.ekt.bte.exceptions.BadTransformationSpec;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.record.MapRecord;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TransformationEngineTest {
    private DataLoader dl;
    private OutputGenerator og;
    private Workflow empty_workflow;
    private Workflow full_filter_workflow;
    private Workflow half_filter_workflow;

    private class SimpleOutputGenerator implements OutputGenerator {
        public List<String> generateOutput(RecordSet recs) {
            ArrayList<String> ret = new ArrayList<String>();

            for(Record rec : recs) {
                ret.add(rec.getValues("id").get(0).getAsString());
            }

            return ret;
        }

        public List<String> generateOutput(RecordSet recs, DataOutputSpec spec) {
            return generateOutput(recs);
        }
    }

    private class SimpleDataLoader implements DataLoader {
        // A simple data loader that has only 500 records to return
        private int id = 0;

        public RecordSet getRecords() throws EmptySourceException {
            return getRecords(null);
        }

        public RecordSet getRecords(DataLoadingSpec spec) throws EmptySourceException {
            RecordSet ret = new RecordSet();

            for (int i = 0; i < spec.getNumberOfRecords(); i++) {
                MapRecord rec = new MapRecord();
                if (id >= 500) {
                    break;
                }
                rec.addValue("id", new StringValue(Integer.toString(id++)));
                ret.addRecord(rec);
            }

            return ret;
        }

        @Override
        public boolean hasMoreRecords() {
            if (id >= 500) {
                return false;
            }
            return true;
        }
    }

    private class FullFilter extends AbstractFilter {
        public FullFilter() {
            super("Full filter");
        }

        @Override
        public boolean isIncluded(Record rec) {
            return false;
        }
    }

    private class HalfFilter extends AbstractFilter {
        public HalfFilter() {
            super("Half filter");
        }

        @Override
        public boolean isIncluded(Record rec) {
            /* Get only odd numbered records from the first 100, and all
             * of them for the next 100.
             */
            if (Integer.parseInt(rec.getValues("id").get(0).getAsString()) > 100)
                return true;
            if ((Integer.parseInt(rec.getValues("id").get(0).getAsString()) % 2) == 0) {
                return false;
            }

            return true;
        }
    }

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
