package gr.ekt.bte.core;

public abstract class AbstractFilter implements ProcessingStep {
    private String name;

    public AbstractFilter(String name) {
        this.name = name;
    }

    @Override
    public RecordSet execute(RecordSet recs) {
        RecordSet ret = new RecordSet();
        for (Record rec : recs) {
            if (this.isIncluded(rec)) {
                ret.addRecord(rec);
            }
        }

        return ret;
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract boolean isIncluded(Record rec);
}
