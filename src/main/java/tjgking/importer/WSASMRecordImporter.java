package tjgking.importer;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
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
 * Created by tjg_k on 2017/11/2.
 */
public class WSASMRecordImporter extends Importer {

    public WSASMRecordImporter(ExcelFile excelfile) {
        super(excelfile);
    }

    @Override
    public int importExcelTable(ExcelFile excelfileDirectory) throws IOException, InvalidFormatException {
        if (excelfileDirectory.isDirectory()) {
            Workbook workbookOut = excelfile.getWorkBook();

            //读入表格数据
            ExcelFile[] files = excelfileDirectory.listFiles();

            assert files != null;
            for (ExcelFile file : files) {
                Map<String, String> map = readExcelFileToMap(file, "操作记录");
                String recordType = map.get("工单类型");
                if (recordType == null || recordType.equals("现场服务")) {
                    Sheet sheetOut = workbookOut.getSheet("现场服务工单详情");
                    if (!writeMapToRow(map, sheetOut)) {
                        return IMOPRT_BadTable;
                    }
                } else if (recordType.equals("硬件维修") || recordType.equals("硬件返修")) {
                    Sheet sheetOut1 = workbookOut.getSheet("硬件维修工单详情");
                    Sheet sheetOut2 = workbookOut.getSheet("硬件维修硬件详情");
                    Sheet sheetIn = file.getWorkBook().getSheet("硬件维修明细表");

                    Map<String, String> addtion = new HashMap<>();
                    addtion.put("工单号", map.get("工单号"));
                    if (!writeMapToRow(map, sheetOut1) & !copyAllRowToTable(sheetIn, sheetOut2, addtion)) {
                        return IMOPRT_BadTable;
                    }
                } else if (recordType.equals("室分及直放站返修")) {
                    Sheet sheetOut = workbookOut.getSheet("室分及直放站返修工单详情");
                    if (!writeMapToRow(map, sheetOut)) {
                        return IMOPRT_BadTable;
                    }
                }

                File outputDirectory = new File(excelfile.getParent() + "\\已导入\\");
                if (!outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        return IMORRT_IOFailure;
                    }
                    System.out.println("创建“已导入文件夹”");
                }

                if (file.renameTo(new File(outputDirectory.getPath() + "\\" + file.getName()))) {
                    return IMORRT_IOFailure;
                }


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
}
