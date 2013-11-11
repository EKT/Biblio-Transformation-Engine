package gr.ekt.bte.workflow;

import gr.ekt.bte.core.AbstractFilter;
import gr.ekt.bte.core.Record;

public class FullFilter extends AbstractFilter {
    public FullFilter() {
        super("Full filter");
    }

    @Override
    public boolean isIncluded(Record rec) {
        return false;
    }
}
