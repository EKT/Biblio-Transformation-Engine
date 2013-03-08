package gr.ekt.bte.core;

import java.util.Date;

public interface DataLoadingSpec {
    public int getNumberOfRecords();
    public int getOffset();
    public String getDataSetName();
    public Date getFromDate();
    public Date getUntilDate();
    public String getIdentifier();
}

