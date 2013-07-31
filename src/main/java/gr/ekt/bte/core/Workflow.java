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
