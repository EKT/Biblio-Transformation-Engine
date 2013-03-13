package gr.ekt.bte.core;

public class FullFilter extends AbstractFilter {
    public FullFilter() {
        super("Full filter");
    }

    @Override
    public boolean isIncluded(Record rec) {
        return false;
    }
}
