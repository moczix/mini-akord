package com.rolnik.akord.excel;

import android.os.Environment;
import android.util.Log;

import com.rolnik.akord.db.Employee;
import com.rolnik.akord.db.EmployeeWithHarvests;
import com.rolnik.akord.db.Harvest;
import com.rolnik.akord.db.converters.DateConverter;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import jxl.CellView;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import static android.content.ContentValues.TAG;

/**
 * Created by moczniak on 01.01.2018.
 */

public class Excel {
    private WritableWorkbook createWorkbook(String fileName){
        //exports must use a temp file while writing to avoid memory hogging
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setUseTemporaryFileDuringWrite(true);

        //get the sdcard's directory
        File sdCard = Environment.getExternalStorageDirectory();
        //add on the your app's path
        File dir = new File(sdCard.getAbsolutePath() );
        //make them in case they're not there
        dir.mkdirs();
        //create a standard java.io.File object for the Workbook to use
        File wbfile = new File(dir,fileName);

        WritableWorkbook wb = null;

        try{
            //create a new WritableWorkbook using the java.io.File and
            //WorkbookSettings from above
            wb = Workbook.createWorkbook(wbfile,wbSettings);
        }catch(IOException ex){
            Log.e(TAG,ex.getStackTrace().toString());
            Log.e(TAG, ex.getMessage());
        }

        return wb;
    }


    public void prepareExcel(List<EmployeeWithHarvests> employeeWithHarvestsList) throws WriteException, IOException {
        WritableWorkbook excelFile = createWorkbook("pierwszy.xls");

        // create an Excel sheet
        WritableSheet excelSheet = excelFile.createSheet("Sheet 1", 0);

        // add something into the Excel sheet
        Label label = new Label(0, 0, "Test Count");
        excelSheet.addCell(label);

        Number number = new Number(0, 1, 1);
        excelSheet.addCell(number);

        label = new Label(1, 0, "Result");
        excelSheet.addCell(label);

        label = new Label(1, 1, "Passed");
        excelSheet.addCell(label);

        number = new Number(0, 2, 2);
        excelSheet.addCell(number);

        label = new Label(1, 2, "Passed 2");
        excelSheet.addCell(label);

        excelFile.write();
        excelFile.close();

    }

    public void prepareExcelInMemory(List<EmployeeWithHarvests> employeeWithHarvestsList, OutputStream out) throws WriteException, IOException {
            WritableWorkbook excelFile = Workbook.createWorkbook(out);
        char[] alphabet = new char[26];
        int k = 0;
        for(int i = 0; i < 26; i++){
            alphabet[i] = (char)(65 + (k++));
        }

        int harvestSize = 0;
        for (EmployeeWithHarvests employeeWithHarvests : employeeWithHarvestsList) {
            if (employeeWithHarvests.harvests.size() > harvestSize) {
                harvestSize = employeeWithHarvests.harvests.size();
            }
        }
        harvestSize-=1;

        WritableSheet excelSheet = excelFile.createSheet("Sheet 1", 0);

        WritableCellFormat cFormat = new WritableCellFormat();
        WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        cFormat.setFont(font);

        WritableCellFormat centerformat = new WritableCellFormat();
        centerformat.setAlignment(Alignment.CENTRE);
        centerformat.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableCellFormat lightGreeenColourFormat = new WritableCellFormat();

        lightGreeenColourFormat.setBackground(Colour.LIGHT_TURQUOISE);
        lightGreeenColourFormat.setBorder(Border.ALL, BorderLineStyle.THICK);

        WritableCellFormat justBorder = new WritableCellFormat();
        justBorder.setBorder(Border.ALL, BorderLineStyle.THICK);

        WritableCellFormat justBorderThin = new WritableCellFormat();
        justBorderThin.setBorder(Border.ALL, BorderLineStyle.THIN);

        WritableCellFormat redColourFormat = new WritableCellFormat();
        redColourFormat.setBackground(Colour.LIGHT_ORANGE);
        redColourFormat.setBorder(Border.ALL, BorderLineStyle.THICK);

        //COLUMN, ROW
        excelSheet.addCell(new Label(1, 1, "DATA", cFormat));

        String today = DateConverter.dfPattern.format(new Date());

        excelSheet.addCell(new Label(4, 1, today, cFormat));
        excelSheet.mergeCells(4, 1, 5, 1);

        excelSheet.addCell(new Label(1,3, "nr rwacza", justBorder));
        excelSheet.addCell(new Label(2,3, "waga kg/szt", justBorder));
        excelSheet.addCell(new Label(3,3, "stawka zł/kg", justBorder));

        autoSize(1, excelSheet);
        autoSize(2, excelSheet);
        autoSize(3, excelSheet);



        excelSheet.addCell(new Label(4,3, "zebrane szt.", centerformat));

        //column, row, column, row
        excelSheet.mergeCells(4,3, 4+harvestSize, 3);
        excelSheet.addCell(new Label(5+harvestSize, 3, "razem szt.", justBorder) );
        excelSheet.addCell(new Label(6+harvestSize, 3, "razem kg.", justBorder) );
        excelSheet.addCell(new Label(7+harvestSize, 3, "razem zł.", justBorder) );

        autoSize(5+harvestSize, excelSheet);
        autoSize(6+harvestSize, excelSheet);
        autoSize(7+harvestSize, excelSheet);



        for (int i =0; i < employeeWithHarvestsList.size(); ++i) {
            Employee employee = employeeWithHarvestsList.get(i).employee;
            List<Harvest> harvestList = employeeWithHarvestsList.get(i).harvests;

            int row = 3+i+1;
            excelSheet.addCell(new Label(1,row, employee.getName(), lightGreeenColourFormat));

            double avgWeight = 0;
            double avgPrice = 0;

            double sumWeight = 0;
            double sumPrice = 0;

            int amountColStart = 4;
            for (int z = 0; z < harvestList.size(); ++z) {
                excelSheet.addCell(new Number(amountColStart+z, row, harvestList.get(z).getAmount(), justBorderThin));
                sumWeight += harvestList.get(z).getWeight();
                sumPrice += harvestList.get(z).getCost();
            }
            if (harvestList.size() > 0){
                avgWeight = sumWeight / harvestList.size();
                avgPrice = sumPrice / harvestList.size();
            }
            excelSheet.addCell(new Number(2, row, avgWeight, lightGreeenColourFormat));
            excelSheet.addCell(new Number(3, row, avgPrice, lightGreeenColourFormat));

            int formulaRow = row+1;
            String formula = "SUM("+alphabet[4]+""+formulaRow+":"+alphabet[(4+harvestSize)]+""+formulaRow+")";
            Log.i("test", "formula: " + formula);
            excelSheet.addCell(new Formula(5+harvestSize, row, formula, redColourFormat));
            excelSheet.addCell(new Formula(6+harvestSize, row, "C"+formulaRow+"*"+alphabet[5+harvestSize]+""+formulaRow, redColourFormat));
            excelSheet.addCell(new Formula(7+harvestSize, row, "D"+formulaRow+"*"+alphabet[6+harvestSize]+""+formulaRow, justBorder));
        }

        int rowMax = 3+employeeWithHarvestsList.size()+1;

        excelSheet.addCell(new Label(1, rowMax+1, "razem", lightGreeenColourFormat));
        excelSheet.addCell(new Label(5+harvestSize, rowMax, "szt"));
        excelSheet.addCell(new Label(6+harvestSize, rowMax, "kg"));
        excelSheet.addCell(new Label(7+harvestSize, rowMax, "zł"));

        excelSheet.addCell(new Formula(5+harvestSize, rowMax+1, "SUM("+alphabet[5+harvestSize]+"5:"+alphabet[5+harvestSize]+""+(rowMax)+")", redColourFormat));
        excelSheet.addCell(new Formula(6+harvestSize, rowMax+1, "SUM("+alphabet[6+harvestSize]+"5:"+alphabet[6+harvestSize]+""+(rowMax)+")", redColourFormat));
        excelSheet.addCell(new Formula(7+harvestSize, rowMax+1, "SUM("+alphabet[7+harvestSize]+"5:"+alphabet[7+harvestSize]+""+(rowMax)+")"));
        excelFile.write();
        excelFile.close();
    }


    private void autoSize(int column, WritableSheet sheet) {
        CellView cell = sheet.getColumnView(column);
        cell.setAutosize(true);
        sheet.setColumnView(column, cell);
    }

}
