package gr.ekt.bte.core;

import java.util.Date;

public class TransformationSpec {
    private int n_records;
    private int offset;
    private String data_set_name;
    private Date from_date;
    private Date until_date;
    private String id;

    public TransformationSpec() {
        n_records = 0;
        offset = 0;
        data_set_name = null;
        from_date = null;
        until_date = null;
        id = null;
    }

    public TransformationSpec(int n_records, int offset, String data_set_name,
                              Date from_date, Date until_date) {
        this.n_records = n_records;
        this.offset = offset;
        this.data_set_name = data_set_name;
        this.from_date = from_date;
        this.until_date = until_date;
        this.id = null;
    }

    public TransformationSpec(String id) {
        this.id = id;
        n_records = 1;
        offset = 0;
        data_set_name = null;
        from_date = null;
        until_date = null;
    }

    public int getNumberOfRecords() {
        return n_records;
    }

    public void setNumberOfRecords(int n_records) {
        this.n_records = n_records;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public String getDataSetName() {
        return data_set_name;
    }

    public void setDataSetName(String data_set_name) {
        this.data_set_name = data_set_name;
    }

    public Date getFromDate() {
        return from_date;
    }

    public void setFromDate(Date from_date) {
        this.from_date = from_date;
    }

    public Date getUntilDate() {
        return until_date;
    }

    public void setUntilDate(Date until_date) {
        this.until_date = until_date;
    }

    public String getIdentifier() {
        return id;
    }

    public void setIdentifier(String id) {
        this.id = id;
    }
}
