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
package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.exceptions.MalformedSourceException;

import java.util.ArrayList;
import java.util.List;

public class MultiSourceDataLoader implements DataLoader {
    private List<DataLoader> dataLoaders_;

    public MultiSourceDataLoader() {
        dataLoaders_ = new ArrayList<DataLoader>();
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        RecordSet ret = new RecordSet();

        for (DataLoader loader : dataLoaders_) {
            RecordSet cSet = loader.getRecords();
            ret.addAll(cSet);
        }

        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException {
        return getRecords();
    }

    public void addDataLoader(DataLoader loader) {
        dataLoaders_.add(loader);
    }

    /**
     * @return the dataLoaders_
     */
    public List<DataLoader> getDataLoaders() {
        return dataLoaders_;
    }

    /**
     * @param dataLoaders the dataLoaders_ to set
     */
    public void setDataLoaders(List<DataLoader> dataLoaders) {
        this.dataLoaders_ = dataLoaders;
    }
}
