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

import gr.ekt.bte.exceptions.MalformedSourceException;

/**
 * Provides the interface for the loader of data from their original
 * form into {@link Record}s.
 *
 * @author Panagiotis Koutsourakis
 * @author Konstantinos Stamatis
 * @author Nikos Houssos
 */
public interface DataLoader {
    /**
     * Loads all the records from the data source and returns them as a
     * {@link RecordSet}.
     *
     * @return A {@link RecordSet} containing all the data in the
     * data source in the form of {@link Record}s.
     *
     * @throws EmptySourceException If the data source does not
     * contain any data
     */
    public RecordSet getRecords() throws MalformedSourceException;

    /**
     * Loads records from the data source based on criteria defined by
     * the {@link DataLoadingSpec} given as argument.
     * <br>
     * <br>
     * The idea is that it might be more efficient to perform some
     * initial filtering of records at the source, than to define and
     * run {@link AbstractFilter}s later, for the whole set of
     * records, that could be big.
     *
     * @param spec The criteria on which the initial filtering is
     * based
     *
     * @return A {@link RecordSet} containing data specified by the
     * argument in the data source in the form of {@link Record}s.
     *
     * @throws EmptySourceException If the source does not contain any
     * data
     */
    public RecordSet getRecords(DataLoadingSpec spec) throws MalformedSourceException;
}
