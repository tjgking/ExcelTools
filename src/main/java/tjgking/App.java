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

        if (args.length != 3 && args.length != 0) {
            System.out.println("参数错误");
            return;
        }

        while (!sw) {
            System.out.println("\n**********************************");
            System.out.println("欢迎使用！");
            System.out.println("本工具用于汇总Excel表格");
            System.out.println("请选择功能项:\n" +
                    "1:记录表汇总\n" +
                    "2:多张表格汇总\n" +
                    "3:无线维保记录表汇总\n" +
                    "4:其他\n" +
                    "q:退出程序\n" +
                    "**********************************\n" +
                    "请输入功能：\n\n");
            String read = "";
            String importExcelDirectoryPath = "";
            String outputExcelFilePath = "";

            if (args != null && args.length == 3) {
                System.out.println("检测到自动化参数");
                read = args[0];
                importExcelDirectoryPath = args[1];
                outputExcelFilePath = args[2];
            } else {
                Scanner scan = new Scanner(System.in);
                read = scan.nextLine();
            }

            switch (read.toLowerCase()) {
                case "1":
                    sw = importOperationHistory("normal", importExcelDirectoryPath, outputExcelFilePath);
                    break;
                case "3":
                    sw = importOperationHistory("WSASMRecord", importExcelDirectoryPath, outputExcelFilePath);
                    break;
                case "q":
                    return;
                default:
                    System.out.println("请重新输入:");
            }

            args = null;
        }
    }


    private static boolean importOperationHistory(String importType,String importExcelDirectoryPath,String outputExcelFilePath) {
        Importer importer;
        System.out.println("请选择汇总表：");
        try {
            ExcelFile file;

            if (importExcelDirectoryPath.equals("")) {
                file = jFileChooser(FileType_ExcelFile);
            } else {
                file = new ExcelFile(importExcelDirectoryPath);
            }

            if (file == null || !file.exists()) {
                System.out.println("没有选中文件");
                return false;
            } else {
                System.out.println("汇总表为：" + file.getPath());
                System.out.println("请选择记录表所在文件夹:");

                ExcelFile file1;
                if (outputExcelFilePath.equals("")) {
                    file1 = jFileChooser(FileType_Directory);
                } else {
                    file1 = new ExcelFile(outputExcelFilePath);
                }

                if (file1.exists()) {
                    System.out.println("记录表文件夹：" + file.getPath());
                } else {
                    System.out.println("记录表文件夹不存在!");
                    return false;
                }


                switch (importType) {
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
                        break;
                    case Importer.IMORRT_IOFailure:
                        System.out.println("导入失败，IO错误，请检查文件目录和权限");
                        break;
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
