/*
 * #%L
 * Cantharella :: Service
 * $Id: ExcelColumnsReader.java 269 2014-05-07 08:14:00Z echatellier $
 * $HeadURL: https://svn.codelutin.com/cantharella/trunk/cantharella.service/src/test/java/nc/ird/cantharella/service/utils/ExcelColumnsReader.java $
 * %%
 * Copyright (C) 2009 - 2012 IRD (Institut de Recherche pour le Developpement) and by respective authors (see below)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package nc.ird.cantharella.service.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.ird.cantharella.service.exceptions.ExcelImportException;
import nc.ird.cantharella.utils.AssertTools;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;

/**
 * Reader for Excel files which have a header then data. Based on POI Library.
 * 
 * @author Adrien Cheype
 */
public final class ExcelColumnsReader {

    /** True value in string **/
    private final static String TRUE_STRING = "true";

    /** False value in string **/
    private final static String FALSE_STRING = "false";

    /** Excel workbook which contain sheets of information **/
    private HSSFWorkbook workbook;

    /** current structure sheet browsed */
    private ExcelSheetStructure currentSheetStructure;

    /** Index of the current line of the current sheet **/
    private int currentNumLine;

    /** Structure asked for the Excel file **/
    private List<ExcelSheetStructure> excelStructureAsked;

    /**
     * Give indexes of a sheet's columns. Access by the name of the sheet then by the name of the column
     **/
    private Map<String, Map<String, Integer>> columnsIndexes;

    /**
     * Constructor
     * 
     * @param inputStream Stream which contain Excel data
     * @param fileStructureAsked Structure asked for the Excel file
     * @throws IOException -
     * @throws ExcelImportException -
     */
    public ExcelColumnsReader(InputStream inputStream, List<ExcelSheetStructure> fileStructureAsked)
            throws IOException, ExcelImportException {
        POIFSFileSystem file = new POIFSFileSystem(inputStream);
        workbook = new HSSFWorkbook(file);

        if (fileStructureAsked.isEmpty()) {
            throw new ExcelImportException("The structure given to the Excel Reader doesn't contain any sheet");
        }
        this.excelStructureAsked = fileStructureAsked;

        currentSheetStructure = fileStructureAsked.get(0);
        currentNumLine = workbook.getSheet(currentSheetStructure.name).getFirstRowNum();

        columnsIndexes = new HashMap<String, Map<String, Integer>>();

        findIndexesColumns();
    }

    /**
     * Return the names of the autorized sheet (according to the structure given at the reader)
     * 
     * @return this names
     */
    public List<String> getSheetNames() {
        List<String> names = new ArrayList<String>();
        for (ExcelSheetStructure curSheetStruct : excelStructureAsked) {
            names.add(curSheetStruct.name);
        }
        return names;
    }

    /**
     * Find the index for each column name (in the first row) of each sheet of the given structure file
     * 
     * @throws ExcelImportException if a sheet or a column name is not found
     */
    private void findIndexesColumns() throws ExcelImportException {

        for (ExcelSheetStructure structAsked : excelStructureAsked) {
            HSSFSheet curSheet = workbook.getSheet(structAsked.name);
            // verify the existance of the sheet
            if (curSheet == null) {
                throw new ExcelImportException("'" + structAsked.name + "' sheet not found in the specified Excel file");
            }

            columnsIndexes.put(structAsked.name, new HashMap<String, Integer>());
            // verify the existance of the sheet columns
            for (ExcelColumnStructure col : structAsked.columns) {
                // get the header line
                HSSFRow header = curSheet.getRow(curSheet.getFirstRowNum());
                AssertTools.assertNotNull(header);

                int i = header.getFirstCellNum();
                HSSFCell curCell = header.getCell(i);

                boolean columnFinded = false;
                // look for the colName in the header (blank cell terminate the header)
                while (!columnFinded && curCell != null && curCell.getCellType() != Cell.CELL_TYPE_BLANK) {
                    if (curCell.getStringCellValue().trim().toLowerCase().equals(col.name.toLowerCase())) {
                        columnFinded = true;
                    } else {
                        i++;
                        curCell = header.getCell(i);
                    }
                }

                if (!columnFinded) {
                    throw new ExcelImportException("'" + col.name + "' not found in the header of the '"
                            + structAsked.name + "' sheet");
                }

                // update of the index in the map
                columnsIndexes.get(structAsked.name).put(col.name, i);
            }
        }
    }

    /**
     * Move to the beggining of the given sheet and return the structure of its columns
     * 
     * @param sheetName name of the sheet
     * @return the list of the columns of the sheet according to the structure given to the Excel Reader
     * @throws ExcelImportException if the sheet is not autorized according to the structure given to the Excel Reader
     */
    public List<ExcelColumnStructure> selectSheet(String sheetName) throws ExcelImportException {

        Iterator<ExcelSheetStructure> itStructSheet = excelStructureAsked.iterator();

        while (itStructSheet.hasNext()) {
            ExcelSheetStructure structSheet = itStructSheet.next();
            if (sheetName != null && sheetName.equals(structSheet.name)) {
                currentSheetStructure = structSheet;
                // +1 for the second line
                currentNumLine = workbook.getSheet(currentSheetStructure.name).getFirstRowNum() + 1;
                return structSheet.columns;
            }
        }

        throw new ExcelImportException("'" + sheetName
                + "' is not autorized according to the structure given to the Excel Reader");
    }

    /**
     * Read a line of the Excel file current sheet Read the columns specified to the structure given to Excel Reader
     * 
     * @return A map which represent a line with the name of column as key
     * @throws ExcelImportException If error in retrieving a cell value
     */
    public Map<String, Object> readLine() throws ExcelImportException {
        Map<String, Object> line = new HashMap<String, Object>();

        HSSFRow row = workbook.getSheet(currentSheetStructure.name).getRow(currentNumLine);
        if (row != null) {
            for (ExcelColumnStructure curCol : currentSheetStructure.columns) {
                Integer indCol = columnsIndexes.get(currentSheetStructure.name).get(curCol.name);
                Object cellVal = null;
                try {
                    cellVal = checkColumnValue(getCellValue(row.getCell(indCol)), curCol);
                } catch (ExcelImportException e) {
                    throw new ExcelImportException("Error in retrieving the cell value : sheet '"
                            + currentSheetStructure.name + "', line " + Integer.toString(currentNumLine) + ", column '"
                            + curCol.name + "'", e);
                } catch (RuntimeException e) {
                    throw new ExcelImportException("Error in retrieving the cell value : sheet '"
                            + currentSheetStructure.name + "', line " + Integer.toString(currentNumLine) + ", column '"
                            + curCol.name + "'", e);
                }

                line.put(curCol.name, cellVal);
            }
            currentNumLine++;
        }
        return line;
    }

    /**
     * Check the cell value type according its column definition from the structure given at the Excel Reader If it's
     * possible, convert the cell value in the right type
     * 
     * @param cellValue The source value
     * @param curCol The column definition
     * @return The value in the right type
     * @throws ExcelImportException If the cellValue doesn't respect the expected type
     */
    private Object checkColumnValue(Object cellValue, ExcelColumnStructure curCol) throws ExcelImportException {
        // check is it required and no value
        if (cellValue == null && curCol.required) {
            throw new ExcelImportException("Value required for the cell value : sheet '" + currentSheetStructure.name
                    + "', line " + Integer.toString(currentNumLine) + ", column '" + curCol.name + "'");
        }
        // check the good type of value
        switch (curCol.type) {
        case STRING:
            if (cellValue instanceof Double) {
                Double doubleVal = (Double) cellValue;
                if (new Double(Math.round(doubleVal)) == doubleVal) {
                    // if no float part
                    return doubleVal.toString();
                }
                // with float part
                String strVal = doubleVal.toString();
                return strVal.substring(0, strVal.length() - 2);
            }
            // if not a numeric
            if (cellValue == null) {
                return "";
            }
            return cellValue.toString();
        case REEL:
            if (cellValue instanceof Double) {
                return cellValue;
            }
            // if not a numeric, exception
            throw new ExcelImportException("The cell value must be a reel : sheet '" + currentSheetStructure.name
                    + "', line " + Integer.toString(currentNumLine) + ", column '" + curCol.name + "'");
        case INTEGER:
            if (cellValue instanceof Double) {
                Double doubleVal = (Double) cellValue;
                if (new Double(Math.round(doubleVal)) == doubleVal) {
                    // if it's an integer, ok
                    return doubleVal.intValue();
                }
                // if it's a float
                throw new ExcelImportException("The cell value must be a integer : sheet '"
                        + currentSheetStructure.name + "', line " + Integer.toString(currentNumLine) + ", column '"
                        + curCol.name + "'");
            }
            throw new ExcelImportException("The cell value must be a integer : sheet '" + currentSheetStructure.name
                    + "', line " + Integer.toString(currentNumLine) + ", column '" + curCol.name + "'");
        case DATE:
            if (cellValue instanceof Date) {
                return cellValue;
            }
            // is it's not a date
            throw new ExcelImportException("The cell value must be a date : sheet '" + currentSheetStructure.name
                    + "', line " + Integer.toString(currentNumLine) + ", column '" + curCol.name + "'");
        case BOOLEAN:
            if (cellValue instanceof Boolean) {
                return cellValue;
            } else if (cellValue instanceof String
                    && (StringUtils.lowerCase(StringUtils.trim(TRUE_STRING)).equals(
                            StringUtils.lowerCase(StringUtils.trim((String) cellValue))) || StringUtils.lowerCase(
                            StringUtils.trim(FALSE_STRING)).equals(
                            StringUtils.lowerCase(StringUtils.trim((String) cellValue))))) {
                // if "true" or "false" are written in string
                if (StringUtils.lowerCase(StringUtils.trim(TRUE_STRING)).equals(
                        StringUtils.lowerCase(StringUtils.trim((String) cellValue)))) {
                    return true;
                }
                return false;
            }
            // is it's not boolean or a string
            throw new ExcelImportException("The cell value must be a date : sheet '" + currentSheetStructure.name
                    + "', line " + Integer.toString(currentNumLine) + ", column '" + curCol.name + "'");
        }
        // for compiler
        return null;
    }

    /**
     * Get the value of the specified cell
     * 
     * @param cell the cell
     * @return this value according to the type of the cell<br/>
     *         (BLANK -> null, STRING -> String, NUMERIC -> Double, BOOLEAN -> Boolean, FORMULA -> Double, ERROR ->
     *         exception)
     * @throws ExcelImportException Exception if error cell or formula don't get a numeric value
     */
    private Object getCellValue(HSSFCell cell) throws ExcelImportException {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
        case Cell.CELL_TYPE_BLANK:
            return null;
        case Cell.CELL_TYPE_STRING:
            return cell.getStringCellValue();
        case Cell.CELL_TYPE_NUMERIC:
            if (DateUtil.isCellDateFormatted(cell)) {
                // if it's a date
                return DateUtil.getJavaDate(cell.getNumericCellValue());
            }
            return cell.getNumericCellValue();
        case Cell.CELL_TYPE_BOOLEAN:
            return cell.getBooleanCellValue();
        case Cell.CELL_TYPE_FORMULA:
            Double numeric = cell.getNumericCellValue();

            if (numeric.isNaN()) {
                // if it's "Not a Number", exception
                throw new ExcelImportException("Impossible to get a numeric value from the formula");
            }
            return numeric;
        case Cell.CELL_TYPE_ERROR:
            throw new ExcelImportException("Error cell detected");
        }
        // no case for this return
        return null;
    }

    /**
     * fileStructureAsked getter
     * 
     * @return fileStructureAsked
     */
    public List<ExcelSheetStructure> getFileStructureAsked() {
        return excelStructureAsked;
    }

    /**
     * fileStructureAsked setter
     * 
     * @param fileStructureAsked fileStructureAsked
     */
    public void setFileStructureAsked(List<ExcelSheetStructure> fileStructureAsked) {
        this.excelStructureAsked = fileStructureAsked;
    }

    /**
     * currentNumLine getter
     * 
     * @return currentNumLine
     */
    public int getCurrentNumLine() {
        return currentNumLine;
    }
}
