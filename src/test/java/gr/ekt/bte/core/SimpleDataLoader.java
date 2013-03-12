package gr.ekt.bte.core;

import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.record.MapRecord;

public class SimpleDataLoader implements DataLoader {
    public RecordSet getRecords() throws EmptySourceException {
        return getRecords(null);
    }

    public RecordSet getRecords(DataLoadingSpec spec) throws EmptySourceException {
        RecordSet ret = new RecordSet();
        int offset = spec.getOffset();

        if (offset >= 500) {
            return ret;
        }

        for (int i = 0; i < spec.getNumberOfRecords(); i++) {
            MapRecord rec = new MapRecord();
            if (i + offset >= 500) {
                break;
            }
            rec.addValue("id", new StringValue(i + offset + ""));
            ret.addRecord(rec);
        }

        return ret;
    }
}
