package gr.ekt.bte.core;

import static org.junit.Assert.*;
import gr.ekt.bte.exceptions.BadTransformationSpec;

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
    }

    @Test
    public void testTransformWithFullFilter() {
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
    }

    @Test
    public void testTransformWithHalfFilter() {
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
    }
}
