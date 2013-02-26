package gr.ekt.bte.core;

import java.util.List;

public interface Record {
    public List<Value> getValues(String field);
    public MutableRecord makeMutable();
    public boolean isMutable();
}
