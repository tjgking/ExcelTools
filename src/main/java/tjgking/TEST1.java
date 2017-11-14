package tjgking;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import tjgking.importer.Importer;
import tjgking.importer.WSASMRecordImporter;

import java.io.File;
import java.io.IOException;

/**
 * Created by tjg_k on 2017/10/26.
 */
public class TEST1 {
    public static void main(String[] args) {
        ExcelFile file = new ExcelFile("C:\\Users\\tjg_k\\Desktop\\新建文件夹 (2)\\厂家维保工单审核记录汇总表201711061201711062.xlsx");
        Importer importer = new WSASMRecordImporter(file);
        try {
            importer.importExcelTable(new ExcelFile("C:\\Users\\tjg_k\\Desktop\\新建文件夹 (2)\\新建文件夹"));
        } catch (IOException | InvalidFormatException e) {
            e.printStackTrace();
        }

    }
}
