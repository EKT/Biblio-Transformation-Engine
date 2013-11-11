package gr.ekt.bte.workflow;

import gr.ekt.bte.core.AbstractFilter;
import gr.ekt.bte.core.Record;

public class EvenNumberFilter extends AbstractFilter {
    public EvenNumberFilter() {
        super("Even Number Filter");
    }

    @Override
    public boolean isIncluded(Record rec) {
        if (Integer.parseInt(rec.getValues("id").get(0).getAsString()) % 2 == 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
