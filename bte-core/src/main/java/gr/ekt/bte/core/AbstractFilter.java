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

/**
 * Abstract class providing the interface for record filtering.
 * <br>
 * <br>
 * The user should extend this class and override the method {@link
 * AbstractFilter#isIncluded} in order to implement the criterion by
 * which the records should be filtered.
 *
 *  @author Panagiotis Koutsourakis
 *  @author Konstantinos Stamatis
 *  @author Nikos Houssos
 */
public abstract class AbstractFilter implements ProcessingStep {
    /**
     * The name of the filter. Can be used for logging purposes.
     */
    private String name;

    /**
     * Sets the filter's name.
     *
     * @param name The name of the filter
     */
    public AbstractFilter(String name) {
        this.name = name;
    }

    /**
     * Executes this {@link ProcessingStep}.
     * <br>
     * <br>
     * For each {@link Record} contained in the {@link RecordSet}
     * given as argument, if {@link AbstractFilter#isIncluded} returns
     * <code>true</code> the Record is included in the return result,
     * otherwise it is ignored.
     *
     * @see ProcessingStep#execute
     *
     * @param recs The initial set of records
     * @return     The resulting set after the filter is applied
     */
    @Override
    public RecordSet execute(RecordSet recs) {
        RecordSet ret = new RecordSet();
        for (Record rec : recs) {
            if (this.isIncluded(rec)) {
                ret.addRecord(rec);
            }
        }

        return ret;
    }

    /**
     * Gets this filter's user defined name.
     *
     * @return The name of the filter
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Abstract method that examines if the specified record should be
     * included in the resulting set. This method is the one that
     * should be overriden by the user that creates her own filter.
     *
     * @param rec The record to be examined
     * @return    <code>true</code> if the record should be included
     */
    public abstract boolean isIncluded(Record rec);
}
