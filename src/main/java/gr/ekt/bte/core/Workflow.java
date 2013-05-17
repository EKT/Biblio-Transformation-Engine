package gr.ekt.bte.core;

import java.util.List;

/**
   An interface representing the process that filters and modifies the
   records. The process is considered to be a list of {@link
   ProcessingStep}s.

   @author Panagiotis Koutsourakis
   @author Konstantinos Stamatis
   @author Nikos Houssos
*/
public interface Workflow {
    /**
       Executes all the steps of the Workflow.

       @param records the current {@link RecordSet}
       @return        the modified {@link RecordSet}
    */
    public RecordSet run(RecordSet records);

    /**
       Adds a {@link ProcessingStep} at the end of the current
       Workflow.

       @param step the step to add
    */
    public void addStepAtEnd(ProcessingStep step);
    /**
       Adds a {@link ProcessingStep} at the beginning of the current
       Workflow.

       @param step the step to add
    */
    public void addStepAtBeggining(ProcessingStep step);
    /**
       Returns the current process.
       @return the current process.
    */
    public List<ProcessingStep> getProcess();
    /**
       Sets the current process to the given list of {@link
       ProcessingStep}s.

       @param process the new process
     */
    public void setProcess(List<ProcessingStep> process);
}
