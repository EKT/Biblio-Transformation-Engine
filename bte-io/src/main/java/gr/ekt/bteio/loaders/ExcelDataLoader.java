/**
 * Copyright (c) 2007-2013, National Documentation Centre (EKT, www.ekt.gr)
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 *     Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 *     Neither the name of the National Documentation Centre nor the
 *     names of its contributors may be used to endorse or promote
 *     products derived from this software without specific prior written
 *     permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
    private static Logger logger_ = Logger.getLogger(ExcelDataLoader.class);
    private Workbook wb_;
    private Map<Integer, String> fieldMap_;
    //Ignore the lines before line number skipLines
    private int skipLines_;
    //If the ignoreLines == 0 then we read all the lines (respecting skipLines_)
    private int ignoreLinesAfter_;
    private boolean isRead_;

    public ExcelDataLoader(String filename, Map<Integer, String> fieldMap) {
        super(filename);
        new File(filename);
        this.fieldMap_ = fieldMap;
        this.skipLines_ = 0;
        this.ignoreLinesAfter_ = 0;
        wb_ = null;
        isRead_ = false;
    }

    @Override
    public RecordSet getRecords() throws MalformedSourceException {
        try {
            openReader();
        }
        catch (IOException e) {
            logger_.info("Problem loading file: " + filename
                    + " (" + e.getMessage() + ")");
            throw new MalformedSourceException("Problem loading file: "
                                               + filename + " ("
                                               + e.getMessage() + ")");
        }
        catch (InvalidFormatException e) {
            logger_.info("Problem loading file: " + filename
                    + " (" + e.getMessage() + ")");
            throw new MalformedSourceException("Problem loading file: "
                                               + filename + " ("
                                               + e.getMessage() + ")");
        }

        RecordSet ret = new RecordSet();

        //Currently we need this flag in order for
        //TransformationEngine not to go into an infinite loop.
        if (!isRead_) {
            logger_.info("Opening file: " + filename);
            int nSheets = wb_.getNumberOfSheets();
            logger_.info("number of sheets: " + nSheets);
            for(int i = 0; i < nSheets; i++) {
                Sheet cSheet = wb_.getSheetAt(i);
                String cSheetName = cSheet.getSheetName();
                for(int j = skipLines_; j <= cSheet.getLastRowNum(); j++) {
                    if (ignoreLinesAfter_ != 0 && j >= ignoreLinesAfter_) {
                        break;
                    }
                    Row row = cSheet.getRow(j);
                    MapRecord rec = new MapRecord();
                    for(int k = 0; k < row.getLastCellNum(); k++) {
                        if (!fieldMap_.keySet().contains(k)) {
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

                        rec.addValue(fieldMap_.get(k), val);
                    }
                    //TODO remove the hardcoded value
                    rec.addValue("ExcelSheetName", new StringValue(cSheetName));
                    ret.addRecord(rec);
                }
            }
            isRead_ = true;
        }
        return ret;
    }

    @Override
    public RecordSet getRecords(DataLoadingSpec spec)
            throws MalformedSourceException {
        return getRecords();
    }

    private void openReader() throws IOException, InvalidFormatException {
        wb_ = WorkbookFactory.create(new File(filename));
    }

    /**
     * @return the fieldMap_
     */
    public Map<Integer, String> getFieldMap() {
        return fieldMap_;
    }

    /**
     * @param fieldMap the fieldMap_ to set
     */
    public void setFieldMap(Map<Integer, String> fieldMap) {
        this.fieldMap_ = fieldMap;
    }

    /**
     * @return the skipLines_
     */
    public int getSkipLines() {
        return skipLines_;
    }

    /**
     * @param skipLines the skipLines_ to set
     */
    public void setSkipLines(int skipLines) {
        this.skipLines_ = skipLines;
    }

    /**
     * @return the ignoreLinesAfter_
     */
    public int getIgnoreLinesAfter () {
        return ignoreLinesAfter_;
    }

    /**
     * @param ignoreLinesAfter the ignoreLinesAfter_ to set
     */
    public void setIgnoreLinesAfter (int ignoreLinesAfter) {
        this.ignoreLinesAfter_ = ignoreLinesAfter;
    }

}
