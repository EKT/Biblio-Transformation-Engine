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

import java.util.Date;

/**
 * Provides the interface for specifying the parameters to the {@link
 * DataLoader} for reading records from the data source.
 * <br>
 * <br>
 * Probably not all the provided data will be usefull in all
 * situations, (e.g. there can be sources that do not have data set
 * names). It is the responsibility of the {@link
 * TransformationEngine} to create a correct spec and of the {@link
 * DataLoader} implementation to interpret it correctly.
 * <br>
 * <br>
 * The last paragraph shows that there is a tighter than
 * desirable coupling between the data source, the DataLoader and the
 * TransformationEngine (because it is responsible to create
 * DataLoadingSpecs based on the TransformationSpecs). This should
 * probably be redesigned and abstracted away.
 *
 * @author Panagiotis Koutsourakis
 * @author Konstantinos Stamatis
 * @author Nikos Houssos
 */
public interface DataLoadingSpec {
    /**
     * Returns the number of records that should be read.
     *
     * Since the number of records cannot be an integer less than
     * zero, negative values can be used to signal that the user needs
     * all the records form a source.
     *
     * @return The number of records that should be loaded from the
     * source.
     */
    public int getNumberOfRecords();
    /**
     * Returns the offset at which the next record should be read.
     *
     * If the source provides an ordering for the records, the offset
     * can be useful for partial loading.
     *
     * @return The offset at which the next record should be read
     */
    public int getOffset();
    /**
     * Provide the name of the data set from which records should be
     * loaded.
     *
     * If the data source is structured in a way that allows for
     * different data sets, using this method the DataLoader can
     * selectively load data.
     *
     * @return The name of the dataset from which data should be read.
     */
    public String getDataSetName();
    public Date getFromDate();
    public Date getUntilDate();
    public String getIdentifier();
}

