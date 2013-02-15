package gr.ekt.bte.core;

import gr.ekt.bte.exceptions.EmptySourceException;

public interface DataLoader {
    public RecordSet getRecords() throws EmptySourceException;
    public RecordSet getRecords(DataLoadingSpec spec);
}
