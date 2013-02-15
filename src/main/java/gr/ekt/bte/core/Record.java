package gr.ekt.bte.core;

import java.util.List;

public interface Record {
    public List<Value> getValues(String field);
    public boolean addField(String field, List<Value> values);
    public boolean addValue(String field, Value value);
    public boolean removeField(String field);
    public boolean removeValue(String field, Value value);
    public boolean updateField(String field, List<Value> value);
    public boolean updateValue(String field, Value old_value, Value new_value);
}
