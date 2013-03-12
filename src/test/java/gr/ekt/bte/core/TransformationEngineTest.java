package gr.ekt.bte.core;

import static org.junit.Assert.*;
import gr.ekt.bte.exceptions.BadTransformationSpec;

import org.junit.Before;
import org.junit.Test;


public class TransformationEngineTest {
    TransformationEngine te;

    @Before
    public void setUp() {
        te = new TransformationEngine(new SimpleDataLoader(), new SimpleOutputGenerator(), new LinearWorkflow());
    }

    // @Test(expected = BadTransformationSpec.class)
    // public void testBadSpec() {
    //     TransformationSpec spec = new TransformationSpec("foo");
    //     spec.setOffset(100);
    //     te.transform(spec);
    // }

    @Test
    public void testTransform() {
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
}
