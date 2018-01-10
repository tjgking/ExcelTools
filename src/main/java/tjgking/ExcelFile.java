package tjgking;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.net.URI;

/**
 * Created by tjg_k on 2017/10/31.
 */
public class ExcelFile extends File {

    public ExcelFile(String pathname) {
        super(pathname);
    }

    public ExcelFile(String parent, String child) {
        super(parent, child);
    }

    public ExcelFile(File parent, String child) {
        super(parent, child);
    }

    public ExcelFile(URI uri) {
        super(uri);
    }


    public Workbook getWorkBook(boolean readonly) throws IOException, InvalidFormatException {
        Workbook workbook;
        try {
            if (readonly) {
                if (getName().endsWith("xls")) {
                    POIFSFileSystem fs = new POIFSFileSystem(this, true);
                    workbook = new HSSFWorkbook(fs);
                } else if (getName().endsWith("xlsx")) {
                    OPCPackage opcPackage = OPCPackage.open(this, PackageAccess.READ);
                    workbook = new XSSFWorkbook(opcPackage);
                } else if (getName().endsWith("csv")) {
                    workbook = new CSVWorkbook(this);
                } else {
                    workbook = null;
                }
            } else {
                if (getName().endsWith("xls")) {
                    POIFSFileSystem fs = new POIFSFileSystem(this);
                    workbook = new HSSFWorkbook(fs);
                } else if (getName().endsWith("xlsx")) {
                    OPCPackage opcPackage = OPCPackage.open(this);
                    workbook = new XSSFWorkbook(opcPackage);
                } else if (getName().endsWith("csv")) {
                    workbook = new CSVWorkbook(this);
                } else {
                    workbook = null;
                }
            }
        } catch (InvalidOperationException e) {
            workbook = null;
            System.out.println("读入异常");
        }

        return workbook;
    }

    @Override
    public ExcelFile[] listFiles() {
        String[] ss = list();
        if (ss == null) return null;
        int n = ss.length;
        ExcelFile[] fs = new ExcelFile[n];
        for (int i = 0; i < n; i++) {
            fs[i] = new ExcelFile(this.getPath(), ss[i]);
        }
        return fs;
    }

    public static boolean MoveFileTo(ExcelFile excelFile1, ExcelFile excelFile2) {
        InputStream inStream = null;
        OutputStream outStream = null;

        try {

            inStream = new FileInputStream(excelFile1);
            outStream = new FileOutputStream(excelFile2);

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }

            inStream.close();
            outStream.close();

            //delete the original file
            excelFile1.delete();

            //System.out.println("File is copied successful!");

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
