package gr.ekt.bte.record;

import gr.ekt.bte.core.MutableRecord;
import gr.ekt.bte.core.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class MapRecord implements MutableRecord {

    private Map<String, List<Value>> records_;

    public MapRecord() {
        records_ = new TreeMap<String, List<Value>>();
    }

    @Override
    public List<Value> getValues(String field) {
        return records_.get(field);
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
        if (records_.containsKey(field)) {
            return false;
        }

        records_.put(field, values);
        return true;
    }

    @Override
    public boolean addValue(String field, Value value) {
        if (!records_.containsKey(field)) {
            ArrayList<Value> vals = new ArrayList<Value>();
            vals.add(value);
            return this.addField(field, vals);
        }

        if (records_.get(field).contains(value)) {
            return false;
        }

        records_.get(field).add(value);

        return true;
    }

    @Override
    public boolean removeField(String field) {
        if (!records_.containsKey(field)) {
            return false;
        }
        records_.remove(field);
        return true;
    }

    @Override
    public boolean removeValue(String field, Value value) {
        if (!records_.containsKey(field) || !records_.get(field).contains(value)) {
            return false;
        }

        records_.get(field).remove(value);

        return true;
    }

    @Override
    public boolean updateField(String field, List<Value> value) {
        if (!records_.containsKey(field)) {
            return false;
        }

        records_.put(field, value);

        return true;
    }

    @Override
    public boolean updateValue(String field, Value old_value, Value new_value) {
        if (!records_.containsKey(field) || !records_.get(field).contains(old_value)) {
            return false;
        }

        int idx = records_.get(field).indexOf(old_value);
        records_.get(field).set(idx, new_value);

        return true;
    }

}
