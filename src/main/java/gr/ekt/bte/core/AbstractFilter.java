package gr.ekt.bte.core;

public abstract class AbstractFilter implements ProcessingStep {
    public RecordSet execute(RecordSet recs) {
        RecordSet ret = new RecordSet();
        for (Record rec : recs) {
            if (this.isIncluded(rec)) {
                ret.addRecord(rec);
            }
        }

        return ret;
    }

    public abstract boolean isIncluded(Record rec);
}
