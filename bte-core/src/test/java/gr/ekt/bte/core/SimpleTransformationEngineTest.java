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
