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
 * Created by tjg_k on 2018/1/8.
 */
public class MultiCollectImporter extends Importer {
    public MultiCollectImporter(ExcelFile excelFile) {
        super(excelFile);
    }

    @Override
    public int importExcelTable(ExcelFile excelfileDirectory) throws IOException, InvalidFormatException {
        if (excelfileDirectory.isDirectory()) {
            Workbook workbookOut = excelfile.getWorkBook(false);

            //读入表格数据
            ExcelFile[] files = excelfileDirectory.listFiles();
            assert files != null;

            for (ExcelFile file : files) {
                System.out.println(file.getName());
                Sheet sheetOut = workbookOut.getSheetAt(0);
                Workbook workbookIn = file.getWorkBook(false);
                Sheet sheetIn = workbookIn.getSheetAt(0);

                Map<String, String> addtion = new HashMap<>();
                addtion.put("文件名", file.getName());

                if (!copyAllRowToTable(sheetIn, sheetOut, addtion)) {
                    return IMORRT_IOFailure;
                }

                workbookIn.close();

                File outputDirectory = new File(excelfile.getParent() + "\\已导入\\");
                if (!outputDirectory.exists()) {
                    if (!outputDirectory.mkdirs()) {
                        return IMORRT_IOFailure;
                    }
                    System.out.println("创建“已导入文件夹”");
                }

                System.out.println(outputDirectory.getPath() + "\\" + file.getName());
                if (!file.renameTo(new File(outputDirectory.getPath() + "\\" + file.getName()))) {
                    return IMORRT_IOFailure;
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

            workbookOut.close();
            out.flush();
            out.close();
            return IMOPRT_SUCCESSED;
        }
        return IMOPRT_NotDirecotry;
    }
}
