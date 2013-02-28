package gr.ekt.bte.core;

public abstract class AbstractModifier implements ProcessingStep {
    private String name;

    public AbstractModifier(String name) {
        this.name = name;
    }

    @Override
    public RecordSet execute(RecordSet recs) {
        RecordSet ret = new RecordSet();
        Record modified_record;
        for (Record rec : recs) {
            modified_record = this.modify(rec.makeMutable());
            ret.addRecord(modified_record);
        }

        return recs;
    }

    @Override
    public String getName() {
        return name;
    }

    public abstract Record modify(MutableRecord rec);
}
