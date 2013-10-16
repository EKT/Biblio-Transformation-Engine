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
import java.util.Set;

/**
   The basic immutable record.
   <br/>
   <br/>
   A <code>Record</code> is in its most abstract form a mapping from
   <code>String</code>s to <code>List</code>s of {@link Value}s. The
   most useful operation is the getValues that returns a list of
   values for a given field.

   @author Panagiotis Koutsourakis
   @author Konstantinos Stamatis
   @author Nikos Houssos
*/
public interface Record {
    /**
       Given the name of a field returns the list of values.

       @param field The name of the field
       @return      The list of values of the specified field
    */
    public List<Value> getValues(String field);

    /**
       Converts this record into a {@link MutableRecord}.

       @return a {@link MutableRecord} containing the exact same
       values
     */
    public MutableRecord makeMutable();

    /**
       Tests whether the record is mutable or not.

       @return <code>true</code> if the record is mutable
     */
    public boolean isMutable();

    public boolean hasField(String field);

    public Set<String> getFields();
}
