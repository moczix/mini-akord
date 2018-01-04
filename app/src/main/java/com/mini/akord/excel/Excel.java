package com.mini.akord.excel;

import com.mini.akord.db.Employee;
import com.mini.akord.db.EmployeeWithHarvests;
import com.mini.akord.db.Harvest;
import com.mini.akord.db.converters.DateConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import jxl.CellView;
import jxl.Workbook;
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

/**
 * Created by moczniak on 01.01.2018.
 */

class EmployeeCalc {

    public double avgWeight = 0;
    public double avgPrice = 0;

    public double sumWeight = 0;
    public double sumPrice = 0;

    void calc(List<Harvest> dataSet) {
        for (int z = 0; z < dataSet.size(); ++z) {
            sumWeight += dataSet.get(z).getWeight();
            sumPrice += dataSet.get(z).getCost();
        }
        if (dataSet.size() > 0){
            avgWeight = sumWeight / dataSet.size();
            avgPrice = sumPrice / dataSet.size();
        }
    }
}


public class Excel {

    private WritableCellFormat titleCellFormat;
    private WritableCellFormat centerWithBorderThinCellFormat;
    private WritableCellFormat greenWithBorderThinCellFormat;
    private WritableCellFormat redWithBorderThinCellFormat;
    private WritableCellFormat borderCellFormat;
    private WritableCellFormat borderThinCellFormat;


    private char[] alphabet = new char[26];

    List<EmployeeWithHarvests> mDataSet;

    public Excel() {
        int k = 0;
        for(int i = 0; i < 26; i++){
            alphabet[i] = (char)(65 + (k++));
        }
    }

    public void setData(List<EmployeeWithHarvests> data) {
        mDataSet = data;
    }

    private int  getHarvestSize() {
        int harvestSize = 0;
        for (EmployeeWithHarvests el : mDataSet) {
            if (el.harvests.size() > harvestSize) {
                harvestSize = el.harvests.size();
            }
        }
        harvestSize-=1;
        return harvestSize;
    }

    private void prepareStyles() throws WriteException {
        titleCellFormat = new WritableCellFormat();
        WritableFont font = new WritableFont(WritableFont.ARIAL, 12, WritableFont.BOLD);
        titleCellFormat.setFont(font);

        centerWithBorderThinCellFormat = new WritableCellFormat();
        centerWithBorderThinCellFormat.setAlignment(Alignment.CENTRE);
        centerWithBorderThinCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        greenWithBorderThinCellFormat = new WritableCellFormat();
        greenWithBorderThinCellFormat.setBackground(Colour.LIGHT_TURQUOISE);
        greenWithBorderThinCellFormat.setBorder(Border.ALL, BorderLineStyle.THICK);

        borderCellFormat = new WritableCellFormat();
        borderCellFormat.setBorder(Border.ALL, BorderLineStyle.THICK);

        borderThinCellFormat = new WritableCellFormat();
        borderThinCellFormat.setBorder(Border.ALL, BorderLineStyle.THIN);

        redWithBorderThinCellFormat = new WritableCellFormat();
        redWithBorderThinCellFormat.setBackground(Colour.LIGHT_ORANGE);
        redWithBorderThinCellFormat.setBorder(Border.ALL, BorderLineStyle.THICK);
    }


    String sumFormula(int colStart,int colEnd, int row ) {
        colEnd--;
        return "SUM("+alphabet[colStart]+""+row+":"+alphabet[colEnd]+""+row+")";
    }

    String sumFormulaCol(int col, int rowStart, int rowEnd ) {
        return "SUM("+alphabet[col]+""+rowStart+":"+alphabet[col]+""+rowEnd+")";
    }

    String multiplyFormula(int colStart,int colEnd, int row) {
        colEnd--;
        return alphabet[colStart]+""+row+"*"+alphabet[colEnd]+""+row;
    }

    public void prepareExcelInMemory(OutputStream out) throws WriteException, IOException {
        WritableWorkbook excelFile = Workbook.createWorkbook(out);
        String today = DateConverter.dfPattern.format(new Date());
        WritableSheet excelSheet = excelFile.createSheet("Arkusz 1", 0);

        prepareStyles();
        int harvestSize = getHarvestSize();

        int startColumn = 1;
        int startRow = 1;



        //COLUMN, ROW
        excelSheet.addCell(new Label(startColumn, startRow, "DATA", titleCellFormat));
        int dateValColumn = startColumn+3;
        excelSheet.addCell(new Label(dateValColumn, startRow, today, titleCellFormat));
        excelSheet.mergeCells(dateValColumn, startRow, dateValColumn+1, startRow);//dateVal equal 2col

        int dataTitleRow = startRow+2;//3

        int employeeCol = startColumn;//1
        int weightCol = startColumn+1;//2
        int priceCol = startColumn+2;//3

        int harvestCol = startColumn+3;//4

        int dataRowNo = dataTitleRow+1;//4

        int quantitySumCol = harvestCol+harvestSize+1;//5 razem szt
        int weightSumCol = harvestCol+harvestSize+2;//6 razem kg
        int moneySumCol = harvestCol+harvestSize+3;//7 razem zl


        excelSheet.addCell(new Label(employeeCol,dataTitleRow, "nr rwacza", borderCellFormat));
        excelSheet.addCell(new Label(weightCol,dataTitleRow, "waga kg/szt", borderCellFormat));
        excelSheet.addCell(new Label(priceCol,dataTitleRow, "stawka zł/kg", borderCellFormat));

        autoSize(employeeCol, excelSheet);
        autoSize(weightCol, excelSheet);
        autoSize(priceCol, excelSheet);



        excelSheet.addCell(new Label(harvestCol,dataTitleRow, "zebrane szt.", centerWithBorderThinCellFormat));

        //column, row, column, row
        excelSheet.mergeCells(harvestCol,dataTitleRow, harvestCol+harvestSize, dataTitleRow);



        excelSheet.addCell(new Label(quantitySumCol, dataTitleRow, "razem szt.", borderCellFormat) ); //razem szt
        excelSheet.addCell(new Label(weightSumCol, dataTitleRow, "razem kg.", borderCellFormat) ); //razem kg
        excelSheet.addCell(new Label(moneySumCol, dataTitleRow, "razem zł.", borderCellFormat) ); //razem zl

        autoSize(quantitySumCol, excelSheet);
        autoSize(weightSumCol, excelSheet);
        autoSize(moneySumCol, excelSheet);



        for (int i =0; i < mDataSet.size(); ++i) {
            Employee employee = mDataSet.get(i).employee;
            List<Harvest> harvestList = mDataSet.get(i).harvests;

            int row = dataRowNo+i;
            excelSheet.addCell(new Label(1,row, employee.getName(), greenWithBorderThinCellFormat));//employee Name

            EmployeeCalc employeeCalc = new EmployeeCalc();
            employeeCalc.calc(harvestList);

            for (int z = 0; z < harvestList.size(); ++z) {
                excelSheet.addCell(new Number(harvestCol+z, row, harvestList.get(z).getAmount(), borderThinCellFormat));// harvest amount
            }
            excelSheet.addCell(new Number(weightCol, row, employeeCalc.avgWeight, greenWithBorderThinCellFormat));//avg weight
            excelSheet.addCell(new Number(priceCol, row, employeeCalc.avgPrice, greenWithBorderThinCellFormat));//avg price

            int formulaRow = row+1;

            excelSheet.addCell(new Formula(quantitySumCol, row, sumFormula(harvestCol,quantitySumCol, formulaRow ), redWithBorderThinCellFormat)); //razem szt
            excelSheet.addCell(new Formula(weightSumCol, row, multiplyFormula(weightCol, weightSumCol, formulaRow), redWithBorderThinCellFormat)); //razem kg
            excelSheet.addCell(new Formula(moneySumCol, row, multiplyFormula(priceCol, moneySumCol, formulaRow), borderCellFormat)); //razem zl
        }

        int rowMax = 3+mDataSet.size()+1;

        excelSheet.addCell(new Label(1, rowMax+1, "razem", greenWithBorderThinCellFormat));
        excelSheet.addCell(new Label(quantitySumCol, rowMax, "szt")); //razem szt
        excelSheet.addCell(new Label(weightSumCol, rowMax, "kg")); //razem kg
        excelSheet.addCell(new Label(moneySumCol, rowMax, "zł")); //razem zl



        excelSheet.addCell(new Formula(quantitySumCol, rowMax+1, sumFormulaCol(quantitySumCol, dataRowNo+1, rowMax), redWithBorderThinCellFormat)); //razem szt
        excelSheet.addCell(new Formula(weightSumCol, rowMax+1, sumFormulaCol(weightSumCol, dataRowNo+1, rowMax), redWithBorderThinCellFormat)); //razem kg
        excelSheet.addCell(new Formula(moneySumCol, rowMax+1, sumFormulaCol(moneySumCol, dataRowNo+1, rowMax)));//razem zl
        excelFile.write();
        excelFile.close();
    }


    private void autoSize(int column, WritableSheet sheet) {
        CellView cell = sheet.getColumnView(column);
        cell.setAutosize(true);
        sheet.setColumnView(column, cell);
    }

}
