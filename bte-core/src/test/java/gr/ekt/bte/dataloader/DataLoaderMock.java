package gr.ekt.bte.dataloader;

import gr.ekt.bte.core.DataLoader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.record.MapRecord;

public class DataLoaderMock implements DataLoader {
    // A simple data loader that has only 500 records to return
    private int id = 0;

    public RecordSet getRecords() throws EmptySourceException {
        return getRecords(null);
    }

    public RecordSet getRecords(DataLoadingSpec spec) throws EmptySourceException {
        RecordSet ret = new RecordSet();

        for (int i = 0; i < spec.getNumberOfRecords(); i++) {
            MapRecord rec = new MapRecord();
            if (id >= 500) {
                break;
            }
            rec.addValue("id", new StringValue(Integer.toString(id++)));
            ret.addRecord(rec);
        }

        return ret;
    }

    @Override
    public boolean hasMoreRecords() {
        if (id >= 500) {
            return false;
        }
        return true;
    }
}
