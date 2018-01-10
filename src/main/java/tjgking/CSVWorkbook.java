package tjgking;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.formula.FormulaParseException;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PaneInformation;

import java.io.*;
import java.util.*;

/**
 * 该对象有大量方法未写，请使用时注意
 */
public class CSVWorkbook implements Workbook {
    ExcelFile excelFile;

    public CSVWorkbook(ExcelFile excelFile) {
        this.excelFile = excelFile;
    }

    /**
     * 从CSV文件流中读取一个CSV行。
     *
     * @throws Exception
     */
    private String readLine(BufferedReader br) throws Exception {

        StringBuffer readLine = new StringBuffer();
        boolean bReadNext = true;

        while (bReadNext) {
            //
            if (readLine.length() > 0) {
                readLine.append("\r\n");
            }
            // 一行
            String strReadLine = br.readLine();

            // readLine is Null
            if (strReadLine == null) {
                return null;
            }
            readLine.append(strReadLine);

            // 如果双引号是奇数的时候继续读取。考虑有换行的是情况。
            if (countChar(readLine.toString(), '"', 0) % 2 == 1) {
                bReadNext = true;
            } else {
                bReadNext = false;
            }
        }
        return readLine.toString();
    }

    /**
     * 把CSV文件的一行转换成字符串List。指定List长度，不够长度的部分设置为null。
     */
    private static ArrayList<String> fromCSVLine(String source, int size) {
        ArrayList tmpArray = fromCSVLinetoArray(source);
        if (size < tmpArray.size()) {
            size = tmpArray.size();
        }
        ArrayList<String> rtnArray = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            rtnArray.add((String) tmpArray.get(i));
        }
        return rtnArray;
    }

    /**
     * 把CSV文件的一行转换成字符串List。不指定List长度。
     */
    private static ArrayList<String> fromCSVLinetoArray(String source) {
        if (source == null || source.length() == 0) {
            return new ArrayList<>();
        }
        int currentPosition = 0;
        int maxPosition = source.length();
        int nextComma;
        ArrayList<String> rtnArray = new ArrayList<>();
        while (currentPosition < maxPosition) {
            nextComma = nextComma(source, currentPosition);
            rtnArray.add(nextToken(source, currentPosition, nextComma));
            currentPosition = nextComma + 1;
            if (currentPosition == maxPosition) {
                rtnArray.add("");
            }
        }
        return rtnArray;
    }


    @Override
    public int getActiveSheetIndex() {
        return 0;
    }

    @Override
    public void setActiveSheet(int i) {

    }

    @Override
    public int getFirstVisibleTab() {
        return 0;
    }

    @Override
    public void setFirstVisibleTab(int i) {

    }

    @Override
    public void setSheetOrder(String s, int i) {

    }

    @Override
    public void setSelectedTab(int i) {

    }

    @Override
    public void setSheetName(int i, String s) {

    }

    @Override
    public String getSheetName(int i) {
        return null;
    }

    @Override
    public int getSheetIndex(String s) {
        return 0;
    }

    @Override
    public int getSheetIndex(Sheet sheet) {
        return 0;
    }

    @Override
    public Sheet createSheet() {
        return null;
    }

    @Override
    public Sheet createSheet(String s) {
        return null;
    }

    @Override
    public Sheet cloneSheet(int i) {
        return null;
    }

    @Override
    public Iterator<Sheet> sheetIterator() {
        return null;
    }

    @Override
    public int getNumberOfSheets() {
        return 0;
    }

    @Override
    public Sheet getSheetAt(int i) {
        List<String> firstList;
        List<List<String>> dataList = new ArrayList<>();

        try {
            String ENCODE = "GB2312";
            FileInputStream fis = new FileInputStream(excelFile);
            InputStreamReader isw = new InputStreamReader(fis, ENCODE);
            BufferedReader br = new BufferedReader(isw);
            boolean readnext = true;
            int count = 0;

            while (readnext) {
                String CSVline = readLine(br);
                if (CSVline == null) {
                    readnext = false;
                } else {
                    int coloumNum = 0;
                    if (count == 0) {
                        firstList = fromCSVLinetoArray(CSVline);
                        coloumNum = firstList.size();
                        dataList.add(firstList);
                        count++;
                    } else {
                        List<String> currentList = fromCSVLine(CSVline, coloumNum);
                        dataList.add(currentList);
                        count++;
                    }
                }
            }

            br.close();
            isw.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Sheet sheet = new Sheet() {
            @Override
            public Iterator<Row> iterator() {
                return null;
            }

            @Override
            public Row createRow(int i) {
                return null;
            }

            @Override
            public void removeRow(Row row) {

            }

            @Override
            public Row getRow(int i) {
                List<String> list = dataList.get(i);

                Row row = new Row() {
                    @Override
                    public Cell createCell(int i) {
                        return null;
                    }

                    @Override
                    public Cell createCell(int i, int i1) {
                        return null;
                    }

                    @Override
                    public Cell createCell(int i, CellType cellType) {
                        return null;
                    }

                    @Override
                    public void removeCell(Cell cell) {

                    }

                    @Override
                    public void setRowNum(int i) {

                    }

                    @Override
                    public int getRowNum() {
                        return 0;
                    }

                    @Override
                    public Cell getCell(int i) {
                        String value = list.get(i);
                        Cell cell = new Cell() {
                            @Override
                            public int getColumnIndex() {
                                return 0;
                            }

                            @Override
                            public int getRowIndex() {
                                return 0;
                            }

                            @Override
                            public Sheet getSheet() {
                                return null;
                            }

                            @Override
                            public Row getRow() {
                                return null;
                            }

                            @Override
                            public void setCellType(int i) {

                            }

                            @Override
                            public void setCellType(CellType cellType) {

                            }

                            @Override
                            public int getCellType() {
                                return 0;
                            }

                            @Override
                            public CellType getCellTypeEnum() {
                                return CellType.forInt(1);
                            }

                            @Override
                            public int getCachedFormulaResultType() {
                                return 0;
                            }

                            @Override
                            public CellType getCachedFormulaResultTypeEnum() {
                                return null;
                            }

                            @Override
                            public void setCellValue(double v) {

                            }

                            @Override
                            public void setCellValue(Date date) {

                            }

                            @Override
                            public void setCellValue(Calendar calendar) {

                            }

                            @Override
                            public void setCellValue(RichTextString richTextString) {

                            }

                            @Override
                            public void setCellValue(String s) {

                            }

                            @Override
                            public void setCellFormula(String s) throws FormulaParseException {

                            }

                            @Override
                            public String getCellFormula() {
                                return null;
                            }

                            @Override
                            public double getNumericCellValue() {
                                return 0;
                            }

                            @Override
                            public Date getDateCellValue() {
                                return null;
                            }

                            @Override
                            public RichTextString getRichStringCellValue() {
                                return null;
                            }

                            @Override
                            public String getStringCellValue() {
                                return value;
                            }

                            @Override
                            public void setCellValue(boolean b) {

                            }

                            @Override
                            public void setCellErrorValue(byte b) {

                            }

                            @Override
                            public boolean getBooleanCellValue() {
                                return false;
                            }

                            @Override
                            public byte getErrorCellValue() {
                                return 0;
                            }

                            @Override
                            public void setCellStyle(CellStyle cellStyle) {

                            }

                            @Override
                            public CellStyle getCellStyle() {
                                return null;
                            }

                            @Override
                            public void setAsActiveCell() {

                            }

                            @Override
                            public CellAddress getAddress() {
                                return null;
                            }

                            @Override
                            public void setCellComment(Comment comment) {

                            }

                            @Override
                            public Comment getCellComment() {
                                return null;
                            }

                            @Override
                            public void removeCellComment() {

                            }

                            @Override
                            public Hyperlink getHyperlink() {
                                return null;
                            }

                            @Override
                            public void setHyperlink(Hyperlink hyperlink) {

                            }

                            @Override
                            public void removeHyperlink() {

                            }

                            @Override
                            public CellRangeAddress getArrayFormulaRange() {
                                return null;
                            }

                            @Override
                            public boolean isPartOfArrayFormulaGroup() {
                                return false;
                            }
                        };

                        return cell;
                    }

                    @Override
                    public Cell getCell(int i, MissingCellPolicy missingCellPolicy) {
                        return null;
                    }

                    @Override
                    public short getFirstCellNum() {
                        return 0;
                    }

                    @Override
                    public short getLastCellNum() {
                        return 0;
                    }

                    @Override
                    public int getPhysicalNumberOfCells() {
                        return list.size();
                    }

                    @Override
                    public void setHeight(short i) {

                    }

                    @Override
                    public void setZeroHeight(boolean b) {

                    }

                    @Override
                    public boolean getZeroHeight() {
                        return false;
                    }

                    @Override
                    public void setHeightInPoints(float v) {

                    }

                    @Override
                    public short getHeight() {
                        return 0;
                    }

                    @Override
                    public float getHeightInPoints() {
                        return 0;
                    }

                    @Override
                    public boolean isFormatted() {
                        return false;
                    }

                    @Override
                    public CellStyle getRowStyle() {
                        return null;
                    }

                    @Override
                    public void setRowStyle(CellStyle cellStyle) {

                    }

                    @Override
                    public Iterator<Cell> cellIterator() {
                        return null;
                    }

                    @Override
                    public Sheet getSheet() {
                        return null;
                    }

                    @Override
                    public int getOutlineLevel() {
                        return 0;
                    }

                    @Override
                    public Iterator<Cell> iterator() {
                        return null;
                    }
                };

                return row;
            }

            @Override
            public int getPhysicalNumberOfRows() {
                return 0;
            }

            @Override
            public int getFirstRowNum() {
                return 0;
            }

            @Override
            public int getLastRowNum() {
                return dataList.size() - 1;
            }

            @Override
            public void setColumnHidden(int i, boolean b) {

            }

            @Override
            public boolean isColumnHidden(int i) {
                return false;
            }

            @Override
            public void setRightToLeft(boolean b) {

            }

            @Override
            public boolean isRightToLeft() {
                return false;
            }

            @Override
            public void setColumnWidth(int i, int i1) {

            }

            @Override
            public int getColumnWidth(int i) {
                return 0;
            }

            @Override
            public float getColumnWidthInPixels(int i) {
                return 0;
            }

            @Override
            public void setDefaultColumnWidth(int i) {

            }

            @Override
            public int getDefaultColumnWidth() {
                return 0;
            }

            @Override
            public short getDefaultRowHeight() {
                return 0;
            }

            @Override
            public float getDefaultRowHeightInPoints() {
                return 0;
            }

            @Override
            public void setDefaultRowHeight(short i) {

            }

            @Override
            public void setDefaultRowHeightInPoints(float v) {

            }

            @Override
            public CellStyle getColumnStyle(int i) {
                return null;
            }

            @Override
            public int addMergedRegion(CellRangeAddress cellRangeAddress) {
                return 0;
            }

            @Override
            public int addMergedRegionUnsafe(CellRangeAddress cellRangeAddress) {
                return 0;
            }

            @Override
            public void validateMergedRegions() {

            }

            @Override
            public void setVerticallyCenter(boolean b) {

            }

            @Override
            public void setHorizontallyCenter(boolean b) {

            }

            @Override
            public boolean getHorizontallyCenter() {
                return false;
            }

            @Override
            public boolean getVerticallyCenter() {
                return false;
            }

            @Override
            public void removeMergedRegion(int i) {

            }

            @Override
            public void removeMergedRegions(Collection<Integer> collection) {

            }

            @Override
            public int getNumMergedRegions() {
                return 0;
            }

            @Override
            public CellRangeAddress getMergedRegion(int i) {
                return null;
            }

            @Override
            public List<CellRangeAddress> getMergedRegions() {
                return null;
            }

            @Override
            public Iterator<Row> rowIterator() {
                return null;
            }

            @Override
            public void setForceFormulaRecalculation(boolean b) {

            }

            @Override
            public boolean getForceFormulaRecalculation() {
                return false;
            }

            @Override
            public void setAutobreaks(boolean b) {

            }

            @Override
            public void setDisplayGuts(boolean b) {

            }

            @Override
            public void setDisplayZeros(boolean b) {

            }

            @Override
            public boolean isDisplayZeros() {
                return false;
            }

            @Override
            public void setFitToPage(boolean b) {

            }

            @Override
            public void setRowSumsBelow(boolean b) {

            }

            @Override
            public void setRowSumsRight(boolean b) {

            }

            @Override
            public boolean getAutobreaks() {
                return false;
            }

            @Override
            public boolean getDisplayGuts() {
                return false;
            }

            @Override
            public boolean getFitToPage() {
                return false;
            }

            @Override
            public boolean getRowSumsBelow() {
                return false;
            }

            @Override
            public boolean getRowSumsRight() {
                return false;
            }

            @Override
            public boolean isPrintGridlines() {
                return false;
            }

            @Override
            public void setPrintGridlines(boolean b) {

            }

            @Override
            public boolean isPrintRowAndColumnHeadings() {
                return false;
            }

            @Override
            public void setPrintRowAndColumnHeadings(boolean b) {

            }

            @Override
            public PrintSetup getPrintSetup() {
                return null;
            }

            @Override
            public Header getHeader() {
                return null;
            }

            @Override
            public Footer getFooter() {
                return null;
            }

            @Override
            public void setSelected(boolean b) {

            }

            @Override
            public double getMargin(short i) {
                return 0;
            }

            @Override
            public void setMargin(short i, double v) {

            }

            @Override
            public boolean getProtect() {
                return false;
            }

            @Override
            public void protectSheet(String s) {

            }

            @Override
            public boolean getScenarioProtect() {
                return false;
            }

            @Override
            public void setZoom(int i) {

            }

            @Override
            public short getTopRow() {
                return 0;
            }

            @Override
            public short getLeftCol() {
                return 0;
            }

            @Override
            public void showInPane(int i, int i1) {

            }

            @Override
            public void shiftRows(int i, int i1, int i2) {

            }

            @Override
            public void shiftRows(int i, int i1, int i2, boolean b, boolean b1) {

            }

            @Override
            public void createFreezePane(int i, int i1, int i2, int i3) {

            }

            @Override
            public void createFreezePane(int i, int i1) {

            }

            @Override
            public void createSplitPane(int i, int i1, int i2, int i3, int i4) {

            }

            @Override
            public PaneInformation getPaneInformation() {
                return null;
            }

            @Override
            public void setDisplayGridlines(boolean b) {

            }

            @Override
            public boolean isDisplayGridlines() {
                return false;
            }

            @Override
            public void setDisplayFormulas(boolean b) {

            }

            @Override
            public boolean isDisplayFormulas() {
                return false;
            }

            @Override
            public void setDisplayRowColHeadings(boolean b) {

            }

            @Override
            public boolean isDisplayRowColHeadings() {
                return false;
            }

            @Override
            public void setRowBreak(int i) {

            }

            @Override
            public boolean isRowBroken(int i) {
                return false;
            }

            @Override
            public void removeRowBreak(int i) {

            }

            @Override
            public int[] getRowBreaks() {
                return new int[0];
            }

            @Override
            public int[] getColumnBreaks() {
                return new int[0];
            }

            @Override
            public void setColumnBreak(int i) {

            }

            @Override
            public boolean isColumnBroken(int i) {
                return false;
            }

            @Override
            public void removeColumnBreak(int i) {

            }

            @Override
            public void setColumnGroupCollapsed(int i, boolean b) {

            }

            @Override
            public void groupColumn(int i, int i1) {

            }

            @Override
            public void ungroupColumn(int i, int i1) {

            }

            @Override
            public void groupRow(int i, int i1) {

            }

            @Override
            public void ungroupRow(int i, int i1) {

            }

            @Override
            public void setRowGroupCollapsed(int i, boolean b) {

            }

            @Override
            public void setDefaultColumnStyle(int i, CellStyle cellStyle) {

            }

            @Override
            public void autoSizeColumn(int i) {

            }

            @Override
            public void autoSizeColumn(int i, boolean b) {

            }

            @Override
            public Comment getCellComment(CellAddress cellAddress) {
                return null;
            }

            @Override
            public Map<CellAddress, ? extends Comment> getCellComments() {
                return null;
            }

            @Override
            public Drawing<?> getDrawingPatriarch() {
                return null;
            }

            @Override
            public Drawing<?> createDrawingPatriarch() {
                return null;
            }

            @Override
            public Workbook getWorkbook() {
                return null;
            }

            @Override
            public String getSheetName() {
                return null;
            }

            @Override
            public boolean isSelected() {
                return false;
            }

            @Override
            public CellRange<? extends Cell> setArrayFormula(String s, CellRangeAddress cellRangeAddress) {
                return null;
            }

            @Override
            public CellRange<? extends Cell> removeArrayFormula(Cell cell) {
                return null;
            }

            @Override
            public DataValidationHelper getDataValidationHelper() {
                return null;
            }

            @Override
            public List<? extends DataValidation> getDataValidations() {
                return null;
            }

            @Override
            public void addValidationData(DataValidation dataValidation) {

            }

            @Override
            public AutoFilter setAutoFilter(CellRangeAddress cellRangeAddress) {
                return null;
            }

            @Override
            public SheetConditionalFormatting getSheetConditionalFormatting() {
                return null;
            }

            @Override
            public CellRangeAddress getRepeatingRows() {
                return null;
            }

            @Override
            public CellRangeAddress getRepeatingColumns() {
                return null;
            }

            @Override
            public void setRepeatingRows(CellRangeAddress cellRangeAddress) {

            }

            @Override
            public void setRepeatingColumns(CellRangeAddress cellRangeAddress) {

            }

            @Override
            public int getColumnOutlineLevel(int i) {
                return 0;
            }

            @Override
            public Hyperlink getHyperlink(int i, int i1) {
                return null;
            }

            @Override
            public Hyperlink getHyperlink(CellAddress cellAddress) {
                return null;
            }

            @Override
            public List<? extends Hyperlink> getHyperlinkList() {
                return null;
            }

            @Override
            public CellAddress getActiveCell() {
                return null;
            }

            @Override
            public void setActiveCell(CellAddress cellAddress) {

            }
        };

        return sheet;
    }

    @Override
    public Sheet getSheet(String s) {
        return getSheetAt(0);
    }

    @Override
    public void removeSheetAt(int i) {

    }

    @Override
    public Font createFont() {
        return null;
    }

    @Override
    public Font findFont(boolean b, short i, short i1, String s, boolean b1, boolean b2, short i2, byte b3) {
        return null;
    }

    @Override
    public short getNumberOfFonts() {
        return 0;
    }

    @Override
    public Font getFontAt(short i) {
        return null;
    }

    @Override
    public CellStyle createCellStyle() {
        return null;
    }

    @Override
    public int getNumCellStyles() {
        return 0;
    }

    @Override
    public CellStyle getCellStyleAt(int i) {
        return null;
    }

    @Override
    public void write(OutputStream outputStream) throws IOException {

    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public int getNumberOfNames() {
        return 0;
    }

    @Override
    public Name getName(String s) {
        return null;
    }

    @Override
    public List<? extends Name> getNames(String s) {
        return null;
    }

    @Override
    public List<? extends Name> getAllNames() {
        return null;
    }

    @Override
    public Name getNameAt(int i) {
        return null;
    }

    @Override
    public Name createName() {
        return null;
    }

    @Override
    public int getNameIndex(String s) {
        return 0;
    }

    @Override
    public void removeName(int i) {

    }

    @Override
    public void removeName(String s) {

    }

    @Override
    public void removeName(Name name) {

    }

    @Override
    public int linkExternalWorkbook(String s, Workbook workbook) {
        return 0;
    }

    @Override
    public void setPrintArea(int i, String s) {

    }

    @Override
    public void setPrintArea(int i, int i1, int i2, int i3, int i4) {

    }

    @Override
    public String getPrintArea(int i) {
        return null;
    }

    @Override
    public void removePrintArea(int i) {

    }

    @Override
    public Row.MissingCellPolicy getMissingCellPolicy() {
        return null;
    }

    @Override
    public void setMissingCellPolicy(Row.MissingCellPolicy missingCellPolicy) {

    }

    @Override
    public DataFormat createDataFormat() {
        return null;
    }

    @Override
    public int addPicture(byte[] bytes, int i) {
        return 0;
    }

    @Override
    public List<? extends PictureData> getAllPictures() {
        return null;
    }

    @Override
    public CreationHelper getCreationHelper() {
        return null;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public void setHidden(boolean b) {

    }

    @Override
    public boolean isSheetHidden(int i) {
        return false;
    }

    @Override
    public boolean isSheetVeryHidden(int i) {
        return false;
    }

    @Override
    public void setSheetHidden(int i, boolean b) {

    }

    @Override
    public void setSheetHidden(int i, int i1) {

    }

    @Override
    public SheetVisibility getSheetVisibility(int i) {
        return null;
    }

    @Override
    public void setSheetVisibility(int i, SheetVisibility sheetVisibility) {

    }

    @Override
    public void addToolPack(UDFFinder udfFinder) {

    }

    @Override
    public void setForceFormulaRecalculation(boolean b) {

    }

    @Override
    public boolean getForceFormulaRecalculation() {
        return false;
    }

    @Override
    public SpreadsheetVersion getSpreadsheetVersion() {
        return null;
    }

    @Override
    public int addOlePackage(byte[] bytes, String s, String s1, String s2) throws IOException {
        return 0;
    }

    @Override
    public Iterator<Sheet> iterator() {
        return null;
    }


    /**
     * 计算指定文字的个数。
     *
     * @param str   文字列
     * @param c     文字
     * @param start 开始位置
     * @return 个数
     */
    private int countChar(String str, char c, int start) {
        int i = 0;
        int index = str.indexOf(c, start);
        return index == -1 ? i : countChar(str, c, index + 1) + 1;
    }

    /**
     * 查询下一个逗号的位置。
     *
     * @param source 文字列
     * @param st     检索开始位置
     * @return 下一个逗号的位置。
     */
    private static int nextComma(String source, int st) {
        int maxPosition = source.length();
        boolean inquote = false;
        while (st < maxPosition) {
            char ch = source.charAt(st);
            if (!inquote && ch == ',') {
                break;
            } else if ('"' == ch) {
                inquote = !inquote;
            }
            st++;
        }
        return st;
    }

    /**
     * 取得下一个字符串
     */
    private static String nextToken(String source, int st, int nextComma) {
        StringBuffer strb = new StringBuffer();
        int next = st;
        while (next < nextComma) {
            char ch = source.charAt(next++);
            if (ch == '"') {
                if ((st + 1 < next && next < nextComma) && (source.charAt(next) == '"')) {
                    strb.append(ch);
                    next++;
                }
            } else {
                strb.append(ch);
            }
        }
        return strb.toString();
    }

    /**
     * 在字符串的外侧加双引号。如果该字符串的内部有双引号的话，把"转换成""。
     *
     * @param item 字符串
     * @return 处理过的字符串
     */
    private static String addQuote(String item) {
        if (item == null || item.length() == 0) {
            return "\"\"";
        }
        StringBuffer sb = new StringBuffer();
        sb.append('"');
        for (int idx = 0; idx < item.length(); idx++) {
            char ch = item.charAt(idx);
            if ('"' == ch) {
                sb.append("\"\"");
            } else {
                sb.append(ch);
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
