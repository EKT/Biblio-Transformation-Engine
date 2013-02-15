package gr.ekt.bte.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This class is essentially a wrapper arround a list with a restricted interface.
 * Not sure if it is really needed or could be replaced with a Collection type.
 *  
 * @author kutsurak
 */
public class RecordSet implements Iterable<Record> {
    private List<Record> records_;

    public RecordSet() {
        records_ = new ArrayList<Record>();
    }
    
    public RecordSet(List<Record> records) {
        records_ = new ArrayList<Record>(records);
    }
    
    @Override
    public Iterator<Record> iterator() {
        return records_.iterator();
    }

    // After a talk with kstamatis we decided that records can be 
    // inserted multiple times, therefore there is no way for this 
    // method to fail.
    public void addRecord(Record rec) {
        records_.add(rec);
    }

    public List<Record> getRecords() {
        return records_;
    }

    public void setRecords_(List<Record> records) {
        records_ = records;
    }
    
    public int size() {
        return records_.size();
    }
}
