package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.dataloader.FileDataLoader;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.record.MapRecord;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import au.com.bytecode.opencsv.CSVReader;

public class CSVDataLoader extends FileDataLoader {
    private static Logger logger_ = Logger.getLogger(CSVDataLoader.class);
    private CSVReader reader_;
    private List<String> fields_;
    private int skip_lines_ = 0;
    private char separator_ = ',';
    private char quote_char_ = '"';
    private String value_separator_ = "\\|\\|";

    public CSVDataLoader(String filename, List<String> fields) throws EmptySourceException {
        super(filename);
        fields_ = fields;
        openReader();
    }

    public CSVDataLoader(String filename, List<String> fields, char separator, char quote_char, int skip_lines, String value_separator) throws EmptySourceException {
        super(filename);
        fields_ = fields;
        separator_ = separator;
        quote_char_ = quote_char;
        skip_lines_ = skip_lines;
        value_separator_ = value_separator;
        openReader();
    }

    @Override
    public RecordSet getRecords() throws EmptySourceException {
        RecordSet rs = null;
        try {
            String [] next_line;
            rs = new RecordSet();
            while((next_line = reader_.readNext()) != null) {
                MapRecord rec = new MapRecord();
                for(int i = 0; i < next_line.length; i++) {
                    String values[] = next_line[i].split(value_separator_);
                    for(int j = 0; j < values.length; j++) {
                        rec.addValue(fields_.get(i), new StringValue(values[j]));
                    }
                }
                rs.addRecord(rec);
            }
        } catch(IOException e) {
            logger_.info(e.getMessage());
        }

        return rs;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec) throws EmptySourceException {
        //Not using the DataLoadingSpec for the moment
        return getRecords();
    }

    @Override
    protected void finalize() throws Throwable {
        reader_.close();
    }

    private void openReader() throws EmptySourceException {
        try {
            reader_ = new CSVReader(new FileReader(filename), separator_, quote_char_, skip_lines_);
        } catch (FileNotFoundException e) {
            throw new EmptySourceException("File " + filename + " not found");
        }
    }
}
