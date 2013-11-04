package gr.ekt.bteio.loaders;

import gr.ekt.bte.core.DataLoadingSpec;
import gr.ekt.bte.core.RecordSet;
import gr.ekt.bte.core.StringValue;
import gr.ekt.bte.dataloader.FileDataLoader;
import gr.ekt.bte.exceptions.MalformedSourceException;
import gr.ekt.bte.record.MapRecord;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class ExcelDataLoader extends FileDataLoader {
    private static Logger logger = Logger.getLogger(ExcelDataLoader.class);
    private Workbook wb;
    private Map<Integer, String> fieldMap;
    //Ignore the lines before line number skipLines
    private int skipLines;
    //If the ignoreLines == 0 then we read all the lines (respecting skipLines)
    private int ignoreLinesAfter;
    private boolean isRead;

    public ExcelDataLoader(String filename, Map<Integer, String> fieldMap) {
        super(filename);
        new File(filename);
        this.fieldMap = fieldMap;
        this.skipLines = 0;
        this.ignoreLinesAfter = 0;
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
        catch (InvalidFormatException e) {
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
            logger.info("Opening file: " + filename);
            int nSheets = wb.getNumberOfSheets();
            logger.info("number of sheets: " + nSheets);
            for(int i = 0; i < nSheets; i++) {
                Sheet cSheet = wb.getSheetAt(i);
                String cSheetName = cSheet.getSheetName();
                for(int j = skipLines; j < cSheet.getLastRowNum(); j++) {
                    if (ignoreLinesAfter != 0 && j >= ignoreLinesAfter) {
                        break;
                    }
                    Row row = cSheet.getRow(j);
                    MapRecord rec = new MapRecord();
                    for(int k = 0; k < row.getLastCellNum(); k++) {
                        if (!fieldMap.keySet().contains(k)) {
                            continue;
                        }
                        StringValue val;
                        Cell cCell = row.getCell(k);
                        if (cCell == null) {
                            continue;
                        }
                        int cellType = cCell.getCellType();
                        switch (cellType) {
                            case Cell.CELL_TYPE_STRING:
                                val = new StringValue(
                                        cCell.getStringCellValue());
                                break;
                            case Cell.CELL_TYPE_NUMERIC:
                                if (DateUtil.isCellDateFormatted(cCell)) {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                                    val = new StringValue(sdf.format(cCell.getDateCellValue()));
                                }
                                else {
                                    val = new StringValue(String.valueOf(cCell
                                                                         .getNumericCellValue()));
                                }
                                break;
                            case Cell.CELL_TYPE_BLANK:
                                val = new StringValue("");
                                break;
                            case Cell.CELL_TYPE_BOOLEAN:
                                val = new StringValue(String.valueOf(cCell
                                        .getBooleanCellValue()));
                                break;
                            default:
                                val = new StringValue("Unsupported cell type");
                        }

                        rec.addValue(fieldMap.get(k), val);
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

    private void openReader() throws IOException, InvalidFormatException {
        wb = WorkbookFactory.create(new File(filename));
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

    /**
     * @return the ignoreLinesAfter
     */
    public int getIgnoreLinesAfter () {
        return ignoreLinesAfter;
    }

    /**
     * @param ignoreLinesAfter the ignoreLinesAfter to set
     */
    public void setIgnoreLinesAfter (int ignoreLinesAfter) {
        this.ignoreLinesAfter = ignoreLinesAfter;
    }

}
