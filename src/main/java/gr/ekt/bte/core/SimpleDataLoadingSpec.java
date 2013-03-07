package gr.ekt.bte.core;

import java.util.Date;

public class SimpleDataLoadingSpec implements DataLoadingSpec {
    private int n_records;
    private int offset;
    private String data_set_name;
    private Date from_date;
    private Date until_date;

    public SimpleDataLoadingSpec() {
        n_records = 0;
        offset = 0;
        data_set_name = null;
        from_date = null;
        until_date = null;
    }

    @Override
    public int getNumberOfRecords() {
        return n_records;
    }

    public void setNumberOfRecords(int n_records) {
        this.n_records = n_records;
    }

    @Override
    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    public String getDataSetName() {
        return data_set_name;
    }

    public void setDataSetName(String set_name) {
        this.data_set_name = data_set_name;
    }

    @Override
    public Date getFromDate() {
        return from_date;
    }

    public void setFromDate(Date from_date) {
        this.from_date = from_date;
    }

    @Override
    public Date getUntilDate() {
        return until_date;
    }

    public void setUntilDate(Date until_date) {
        this.until_date = until_date;
    }
}
