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
 * Abstract class that provides the interface for record
 * modifications.
 * <br>
 * <br>
 * The user should extend this class and override the method {@link
 * AbstractModifier#modify} in order to implement the desired record
 * modifications.
 *
 *  @author Panagiotis Koutsourakis
 *  @author Konstantinos Stamatis
 *  @author Nikos Houssos
 */
public abstract class AbstractModifier implements ProcessingStep {
    /**
     * The name of the modifier. Can be used for logging.
     */
    private String name;

    /**
     * Sets the modifier's name.
     *
     * @param name The name of the modifier
     */
    public AbstractModifier(String name) {
        this.name = name;
    }

    /**
     * Executes this {@link ProcessingStep}.
     * <br>
     * <br>
     * For each {@link Record} contained in the {@link RecordSet}
     * given as argument, converts it to {@link MutableRecord} calls
     * {@link AbstractModifier#modify}, and adds the modified record
     * in the return result.
     *
     * @see ProcessingStep#execute
     *
     * @param recs The initial set of records
     * @return     A {@link RecordSet} containing the modified records
     */
    @Override
    public RecordSet execute(RecordSet recs) {
        RecordSet ret = new RecordSet();
        Record modified_record;
        for (Record rec : recs) {
            modified_record = this.modify(rec.makeMutable());
            ret.addRecord(modified_record);
        }

        return recs;
    }

    /**
     * Gets this modifier's user defined name.
     *
     * @return The name of the modifier
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Abstract method that modifies the {@link MutableRecord} given
     * as argument. This method should be overriden by the user that
     * creates her own modifier.
     *
     * @param rec The record to be modified
     * @return    The modified record
     */
    public abstract Record modify(MutableRecord rec);
}
