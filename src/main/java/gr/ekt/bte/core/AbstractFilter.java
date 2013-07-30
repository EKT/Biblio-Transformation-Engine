package gr.ekt.bte.core;

/**
 * Abstract class providing the interface for record filtering.
 * <br/>
 * <br/>
 * The user should extend this class and override the method {@link
 * AbstractFilter#isIncluded} in order to implement the criterion by
 * which the records should be filtered.
 *
 *  @author Panagiotis Koutsourakis
 *  @author Konstantinos Stamatis
 *  @author Nikos Houssos
 */
public abstract class AbstractFilter implements ProcessingStep {
    /**
     * The name of the filter. Can be used for bookeping purposes.
     */
    private String name;

    /**
     * Sets the filter's name.
     *
     * @param name The name of the filter
     */
    public AbstractFilter(String name) {
        this.name = name;
    }

    /**
     * Executes this {@link ProcessingStep}.
     * <br/>
     * <br/>
     * For each {@link Record} in the argument, if {@link AbstractFilter#isIncluded} returns
     * <code>true</code> the Record is included in the return result,
     * otherwise it is ignored.
     *
     * @see ProcessingStep#execute
     *
     * @param recs The initial set of records
     * @return     The resulting set after the filter is applied
     */
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

    /**
     * Gets this filter's used defined name.
     *
     * @return The name of the filter
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * Abstract method that examines if the specified record should be
     * included in the resulting set. This method is the one that
     * should be overriden by the user that creates her own filter.
     *
     * @param rec The record to be examined
     * @return    <code>true</code> if the record should be included
     */
    public abstract boolean isIncluded(Record rec);
}
