package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;

import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.dataloader.FileDataLoader;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.record.MapRecord;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

import org.apache.log4j.Logger;

public class ExcelDataLoader extends FileDataLoader {
    private static Logger logger = Logger.getLogger(ExcelDataLoader.class);
    private Workbook wb;
    private Map<Integer, String> fieldMap;
    private int skipLines;
    private boolean isRead;

    public ExcelDataLoader(String filename, Map<Integer, String> fieldMap, int skipLines) {
        super(filename);
        new File(filename);
        this.fieldMap = fieldMap;
        this.skipLines = skipLines;
        wb = null;
        isRead = false;
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        try {
            openReader();
        }
        catch (IOException e) {
            logger.info("Problem loading file: " + filename
                        + " (" + e.getMessage() + ")");
            throw new MalformedSourceException("Problem loading file: "
                                               + filename + " ("
                                               + e.getMessage() + ")");
        }
        catch (BiffException e) {
            logger.info("Problem loading file: " + filename
                        + " (" + e.getMessage() + ")");
            throw new MalformedSourceException("Problem loading file: "
                                               + filename + " ("
                                               + e.getMessage() + ")");
        }

        RecordSet ret = new RecordSet();

        //Currently we need this flag in order for
        //TransformationEngine not to go into an infinite loop.
        if (!isRead) {
            int nSheets = wb.getNumberOfSheets();
            for(int i = 0; i < nSheets; i++) {
                Sheet cSheet = wb.getSheet(i);
                String cSheetName = cSheet.getName();
                for(int j = skipLines; j < cSheet.getRows(); j++) {
                    Cell[] row = cSheet.getRow(j);
                    MapRecord rec = new MapRecord();
                    for(int k = 0; k < row.length; k++) {
                        if (!fieldMap.keySet().contains(k)) {
                            continue;
                        }

                        rec.addValue(fieldMap.get(k), new StringValue(row[k].getContents()));
                    }
                    //TODO remove the hardcoded value
                    rec.addValue("ExcelSheetName", new StringValue(cSheetName));
                    ret.addRecord(rec);
                }
            }
            isRead = true;
        }
        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec)
            throws MalformedSourceException {
        return getRecords();
    }

    private void openReader() throws BiffException, IOException {
        wb = Workbook.getWorkbook(new File(filename));
    }

    @Override
    protected void finalize() {
        wb.close();
    }

    /**
     * @return the fieldMap
     */
    public Map<Integer, String> getFieldMap() {
        return fieldMap;
    }

    /**
     * @param fieldMap the fieldMap to set
     */
    public void setFieldMap(Map<Integer, String> fieldMap) {
        this.fieldMap = fieldMap;
    }

    /**
     * @return the skipLines
     */
    public int getSkipLines() {
        return skipLines;
    }

    /**
     * @param skipLines the skipLines to set
     */
    public void setSkipLines(int skipLines) {
        this.skipLines = skipLines;
    }
}
