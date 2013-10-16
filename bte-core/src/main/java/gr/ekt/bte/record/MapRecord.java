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
package gr.ekt.bte.record;

import gr.ekt.bte.core.MutableRecord;
import gr.ekt.bte.core.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;


public class MapRecord implements MutableRecord {
    private Map<String, List<Value>> records;

    public MapRecord() {
        records = new TreeMap<String, List<Value>>();
    }

    @Override
    public List<Value> getValues(String field) {
        return records.get(field);
    }

    @Override
    public MutableRecord makeMutable() {
        return this;
    }

    @Override
    public boolean isMutable() {
        return true;
    }

    @Override
    public boolean addField(String field, List<Value> values) {
        if (records.containsKey(field)) {
            return false;
        }

        records.put(field, values);
        return true;
    }

    @Override
    public boolean addValue(String field, Value value) {
        if (!records.containsKey(field)) {
            ArrayList<Value> vals = new ArrayList<Value>();
            vals.add(value);
            return this.addField(field, vals);
        }

        if (records.get(field).contains(value)) {
            return false;
        }

        records.get(field).add(value);

        return true;
    }

    @Override
    public boolean removeField(String field) {
        if (!records.containsKey(field)) {
            return false;
        }
        records.remove(field);
        return true;
    }

    @Override
    public boolean removeValue(String field, Value value) {
        if (!records.containsKey(field) || !records.get(field).contains(value)) {
            return false;
        }

        records.get(field).remove(value);

        return true;
    }

    @Override
    public boolean updateField(String field, List<Value> value) {
        if (!records.containsKey(field)) {
            return false;
        }

        records.put(field, value);

        return true;
    }

    @Override
    public boolean updateValue(String field, Value old_value, Value new_value) {
        if (!records.containsKey(field) || !records.get(field).contains(old_value)) {
            return false;
        }

        int idx = records.get(field).indexOf(old_value);
        records.get(field).set(idx, new_value);

        return true;
    }

    @Override
    public boolean hasField(String field) {
        return getValues(field) != null;
    }

    @Override
    public Set<String> getFields() {
        return records.keySet();
    }
}
