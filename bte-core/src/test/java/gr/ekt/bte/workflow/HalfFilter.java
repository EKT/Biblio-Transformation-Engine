package gr.ekt.bte.workflow;

import gr.ekt.bte.core.AbstractFilter;
import gr.ekt.bte.core.Record;

public class HalfFilter extends AbstractFilter {
    public HalfFilter() {
        super("Half filter");
    }

    @Override
    public boolean isIncluded(Record rec) {
        /* Get only odd numbered records from the first 100, and all
         * of them for the next 100.
         */
        if (Integer.parseInt(rec.getValues("id").get(0).getAsString()) > 100)
            return true;
        if ((Integer.parseInt(rec.getValues("id").get(0).getAsString()) % 2) == 0) {
            return false;
        }

        return true;
    }
}
