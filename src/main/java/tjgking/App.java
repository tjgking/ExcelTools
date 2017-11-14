package tjgking;

import com.sun.istack.internal.NotNull;
import tjgking.importer.Importer;
import tjgking.importer.WSASMRecordImporter;

import javax.swing.*;
import java.io.File;
import java.util.Scanner;

/**
 * Hello world!
 */
public class App {
    public static final int FileType_Directory = 0;
    public static final int FileType_ExcelFile = 1;

    public static void main(String[] args) {
        boolean sw = false;
        while (!sw) {
            System.out.println("欢迎使用！");
            System.out.println("本工具用于汇总Excel表格");
            System.out.println("请选择功能项:\n" +
                    "1:记录表汇总\n" +
                    "2:多张表格汇总\n" +
                    "3:无线维保记录表汇总\n" +
                    "4:其他\n" +
                    "q:退出程序\n" +
                    "**********************************");

            Scanner scan = new Scanner(System.in);
            String read = scan.nextLine();
            switch (read.toLowerCase()) {
                case "1":
                    sw = importOperationHistory("normal");
                    break;
                case "3":
                    sw = importOperationHistory("WSASMRecord");
                    break;
                case "q":
                    return;
                default:
                    System.out.println("请重新输入:");
            }

        }
    }


    private static boolean importOperationHistory(String importType) {
        Importer importer;
        System.out.println("请选择汇总表：");
        try {
            ExcelFile file = jFileChooser(FileType_ExcelFile);
            if (file == null) {
                System.out.println("没有选中文件");
                return false;
            } else {
                System.out.println("汇总表为：" + file.getPath());
                System.out.println("请选择记录表所在文件夹:");
                ExcelFile file1 = jFileChooser(FileType_Directory);
                System.out.println("记录表文件夹：" + file.getPath());

                switch (importType){
                    case "normal":
                        importer = new Importer(file);
                        break;
                    case "WSASMRecord":
                        importer = new WSASMRecordImporter(file);
                        break;
                    default:
                        importer = new Importer(file);
                }
                switch (importer.importExcelTable(file1)) {
                    case Importer.IMOPRT_SUCCESSED:
                        System.out.println("\n导入成功");
                }
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    private static ExcelFile jFileChooser(@NotNull int type) throws Exception {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setAcceptAllFileFilterUsed(false);

        if (type == FileType_Directory) {
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        } else if (type == FileType_ExcelFile) {
            jFileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                public boolean accept(File f) { //设定可用的文件的后缀名
                    if (f.getName().endsWith(".xls") || f.getName().endsWith(".xlsx") || f.getName().endsWith(".xlsm") || f.isDirectory()) {
                        return true;
                    }
                    return false;
                }

                public String getDescription() {
                    return "Excel文件(*.xls *.xlsx *.xlsm)";
                }
            });
        } else {
            throw new Exception("需要文件格式");
        }

        int i = jFileChooser.showOpenDialog(null);
        if (i == jFileChooser.APPROVE_OPTION) { //打开文件
            ExcelFile file = new ExcelFile(jFileChooser.getSelectedFile().getPath());
            return file;
        } else {
            return null;
        }
    }
}
