package tjgking.importer;

import com.sun.istack.internal.Nullable;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import tjgking.ExcelFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tjg_k on 2017/10/30.
 */
public class Importer {
    final ExcelFile excelfile;

    int IMOPRT_SUCCESSED = 0;
    public static final int IMOPRT_NotDirecotry = -1;
    public static final int IMOPRT_BadTable = -2;
    public static final int IMORRT_IOFailure = -3;

    public Importer(ExcelFile excelfile) {
        this.excelfile = excelfile;
    }

    public int importExcelTable(ExcelFile excelfileDirectory) throws IOException, InvalidFormatException {

        if (excelfileDirectory.isDirectory()) {
            Workbook workbookOut = excelfile.getWorkBook(true);

            //读入表格数据
            ExcelFile[] files = excelfileDirectory.listFiles();

            for (ExcelFile file : files) {
                Map<String, String> map = readExcelFileToMap(file, file.getWorkBook(false).getSheetName(0));
                Sheet sheetOut = workbookOut.getSheetAt(0);

                if (!writeMapToRow(map, sheetOut)) {
                    return IMOPRT_BadTable;
                }

                IMOPRT_SUCCESSED++;
            }

            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
            String date = df.format(new Date());// new Date()为获取当前系统时间
            int No = 1;

            String path = excelfile.getPath();

            while ((new File(path.substring(0, path.lastIndexOf(".")) + date + "" + No + path.substring(path.lastIndexOf(".")))).exists()) {
                No++;
            }

            FileOutputStream out = new FileOutputStream(new File(path.substring(0, path.lastIndexOf(".")) + date + "" + No + path.substring(path.lastIndexOf("."))));
            workbookOut.write(out);
            out.flush();
            out.close();

            return IMOPRT_SUCCESSED;
        }
        return IMOPRT_NotDirecotry;
    }

    static String getStringCellValue(Cell cell) {
        // 获取单元格数据内容为字符串类型的数据
        String str = "#N/A";

        if (cell == null) {
            str = "";
        } else {
            switch (cell.getCellTypeEnum().ordinal()) {
                case 1:
                    str = String.valueOf(cell.getNumericCellValue());
                    break;
                case 2:
                    str = cell.getStringCellValue();
                    break;
                case 3:
                    switch (cell.getCachedFormulaResultTypeEnum().ordinal()) {
                        case 1:
                            str = String.valueOf(cell.getNumericCellValue());
                            break;
                        case 2:
                            str = cell.getStringCellValue();
                            break;
                        case 4:
                            str = "";
                            break;
                        case 5:
                            str = "#ERROR";
                            break;
                        default:
                            str = "";
                            break;
                    }
                    break;
                case 4:
                    str = "";
                    break;
                case 5:
                    str = "#ERROR";
                    break;
                default:
                    str = "";
            }
        }

        return str.trim();
    }

    static Map<String, String> readExcelFileToMap(ExcelFile excelFile, String sheetname) throws IOException, InvalidFormatException {
        Map<String, String> map = new HashMap<>();
        System.out.print("\n" + excelFile.getName());
        Workbook workbookIn = excelFile.getWorkBook(true);
        Sheet sheetIn = workbookIn.getSheet(sheetname);

        if (null != sheetIn) {
            int coloumNum = sheetIn.getRow(0).getPhysicalNumberOfCells();
            int rowNum = sheetIn.getLastRowNum();//获得总行数
            for (int i = 0; i < rowNum + 1; i++) {
                for (int j = 0; j < coloumNum; j++) {
                    Cell cell = sheetIn.getRow(i).getCell(j);
                    Cell cell1 = sheetIn.getRow(i).getCell(j + 1);

                    String key = getStringCellValue(cell);
                    String value = getStringCellValue(cell1);

                    if (!key.equals("")) {
                        map.put(key, value);
                    }
                }

                //System.out.println("rowNum:" + i);
                //System.out.println("rowNum Name:" + getStringCellValue(sheetIn.getRow(i).getCell(0)));
            }
        }
        workbookIn.close();

        return map;
    }

    static boolean writeMapToRow(Map<String, String> dataMap, Sheet sheetOut) {
        if (sheetOut.getRow(0).getPhysicalNumberOfCells() > 0) {
            int coloumNum = sheetOut.getRow(0).getPhysicalNumberOfCells();
            int rowNum = sheetOut.getLastRowNum() + 1;//获得总行数+1
            sheetOut.createRow(rowNum);
            for (int i = 0; i < coloumNum; i++) {
                String key = getStringCellValue(sheetOut.getRow(0).getCell(i));
                String value = dataMap.get(key);
                //System.out.println("(" + rowNum + "," + i + ") key:" + key + ",values:" + value);
                System.out.print("*");
                Cell cell = sheetOut.getRow(rowNum).createCell(i);
                cell.setCellValue(value);
                if (rowNum > 1) {
                    if (sheetOut.getRow(rowNum - 1).getCell(i) != null) {
                        sheetOut.getRow(rowNum).getCell(i).setCellStyle(sheetOut.getRow(rowNum - 1).getCell(i).getCellStyle());

                        CellType cellType = sheetOut.getRow(rowNum - 1).getCell(i).getCellTypeEnum();
                        if (cellType.ordinal() == 1) {
                            if (HSSFDateUtil.isCellDateFormatted(sheetOut.getRow(rowNum - 1).getCell(i))) {
                                try {
                                    cell.setCellValue(HSSFDateUtil.getJavaDate(Double.valueOf(cell.getStringCellValue())));
                                } catch (NumberFormatException e) {
                                    System.out.print("-");
                                }

                            }
                        }

                    }

                }
            }
            System.out.println("\n");
            return true;
        }
        return false;
    }

    static boolean copyAllRowToTable(Sheet sheetIN, Sheet sheetOut, @Nullable Map<String, String> addition) {
        //根据表头把每行数据转换为一个map
        if (sheetIN.getRow(0).getPhysicalNumberOfCells() > 0) {
            int columNum = sheetIN.getRow(0).getPhysicalNumberOfCells();
            int rowNum = sheetIN.getLastRowNum() + 1;
            for (int i = 1; i < rowNum; i++) {
                int count = 0;
                Map<String, String> map = new HashMap<>();
                for (int j = 0; j < columNum; j++) {
                    String key = getStringCellValue(sheetIN.getRow(0).getCell(j));
                    String value = getStringCellValue(sheetIN.getRow(i).getCell(j));
                    map.put(key, value);
                    if (value.equals("")) count++;
                }

                //检测空行的数量
                if (count < columNum) {
                    if (addition == null || addition.isEmpty()) {
                        writeMapToRow(map, sheetOut);
                    } else {
                        map.putAll(addition);
                        writeMapToRow(map, sheetOut);
                    }
                }
            }
            return true;
        }
        return false;
    }

}
