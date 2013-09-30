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

public class TransformationLog {
    private TransformationSpec transformation_spec;
    private List<DataLoadingSpec> loading_spec_list;
    private int first_unexamined_record;
    private long start_time; //milliseconds
    private long end_time;
    private long transformation_time;
    private List<ProcessingStep> processing_step_list;
    private boolean end_of_input;

    public TransformationLog() {
        transformation_spec = null;
        loading_spec_list = null;
        first_unexamined_record = 0;
        start_time = 0;
        end_time = 0;
        transformation_time = 0;
        processing_step_list = null;
        end_of_input = false;
    }

    public void setTransformationSpec(TransformationSpec spec) {
        transformation_spec = spec;
    }

    public TransformationSpec getTransformationSpec() {
        return transformation_spec;
    }

    public void setLoadingSpecList(List<DataLoadingSpec> lst) {
        loading_spec_list = lst;
    }

    public List<DataLoadingSpec> getLoadingSpecList() {
        return loading_spec_list;
    }

    //Assumes that records are ordered in the source
    public void setFirstUnexaminedRecord(int n_records) {
        first_unexamined_record = n_records;
    }

    public int getFirstUnexaminedRecord() {
        return first_unexamined_record;
    }

    public void setStartTime(long st_time) {
        start_time = st_time;
    }

    public long getStartTime() {
        return start_time;
    }

    public void setEndTime(long e_time) {
        end_time = e_time;
    }

    public long getEndTime() {
        return end_time;
    }

    public void setTransformationTime(long t_time) {
        transformation_time = t_time;
    }

    public long getTransformationTime() {
        return transformation_time;
    }

    public void setProcessingStepList(List<ProcessingStep> lst) {
        processing_step_list = lst;
    }

    public List<ProcessingStep> getProcessingStepList() {
        return processing_step_list;
    }

    public void setEndOfInput(boolean end_of_input) {
        this.end_of_input = end_of_input;
    }

    public boolean getEndOfInput() {
        return end_of_input;
    }
}
