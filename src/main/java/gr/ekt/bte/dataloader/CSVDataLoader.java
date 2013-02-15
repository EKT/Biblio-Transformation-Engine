/**
 * 
 */
package gr.ekt.bte.dataloader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;
import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.Record;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.exceptions.EmptySourceException;
import gr.ekt.bte.record.MapRecord;

/**
 * @author kutsurak
 *
 */
public class CSVDataLoader extends FileDataLoader {
    private RecordSet records_;
    private CSVReader reader_;

    /**
     * @param filename
     */
    public CSVDataLoader(String filename) {
        super(filename);
        records_ = new RecordSet();
        
        try {
            reader_ = new CSVReader(new FileReader(filename_));
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see gr.ekt.bte.core.DataLoader#getRecords()
     */
    @Override
    public RecordSet getRecords() throws EmptySourceException {
        String next_line[];
        String field_names[];
        try {
            //We assume that the first line contains the field names.
            field_names = reader_.readNext();

            if (field_names == null) {
                throw new EmptySourceException("CSVDataLoader::getRecords(): empty file " + filename_);
            }
            
            while((next_line = reader_.readNext()) != null) {
                if (next_line.length != field_names.length) {
                    //TODO throw exception?
                }
                Record rec = new MapRecord(); //TODO The instantiation should use abstract factory (spring framework?)
                for(int i = 0; i < next_line.length; i++) {
                    rec.addValue(field_names[i], new StringValue(next_line[i]));
                }
                
                records_.addRecord(rec);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return records_;
    }

    /* (non-Javadoc)
     * @see gr.ekt.bte.core.DataLoader#getRecords(gr.ekt.bte.core.DataLoadingSpec)
     */
    @Override
    public RecordSet getRecords(DataLoadingSpec spec) {
        // TODO Auto-generated method stub
        return null;
    }

}
