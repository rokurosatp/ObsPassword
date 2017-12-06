// CLI上でのテーブル表示の調整機構 (string.formatを利用)
// 長さを予め計算しておきテーブルっぽくなるように幅を調節する
//
//
package com.github.obsproth.prismkey.cli;

import java.util.ArrayList;
import java.util.List;

class CLITableColumn {
    public String name;
    public String format;
    public int width;
    public boolean autoSized;
    public boolean leftAligned;
    public final String LINE_CHAR = "-";
    public CLITableColumn(String name, String format, int width, boolean autoSized, boolean leftAligned) {
        this.name = name;
        this.format = format;
        this.width = width;
        this.autoSized = autoSized;
        this.leftAligned = leftAligned;
    }

    public String getRowString(Object item) {
        String alignFlag = "";
        if (this.leftAligned) {
            alignFlag = "-";
        }
        return String.format("%"+alignFlag+Integer.toString(this.width)+this.format, item);
    }

    public String getHeaderStr() {
        String alignFlag = "";
        if (this.leftAligned) {
            alignFlag = "-";
        }
        return String.format("%"+alignFlag+Integer.toString(this.width)+"s", this.name);
    }

    public String getHeaderLine() {
        StringBuilder line = new StringBuilder();
        for(int i = 0; i < this.width; i++) {
            line.append("-");
        }
        return line.toString();
    }
    //
    //　calculate column length of item, based on format
    public int calcWidth(Object item) {
        return String.format("%"+this.format, item).length();
    }
}

public class CLITable {
    List<List<Object>> rows;
    List<CLITableColumn> columns;
    public CLITable() {
        this.rows = new ArrayList<List<Object>>();
        this.columns = new ArrayList<CLITableColumn>();
    }

    public void addColumn(String name, String format) {
        this.columns.add(new CLITableColumn(name, format, 0, true, false));
    }
    
    public void addColumn(String name, String format, int length) {
        this.columns.add(new CLITableColumn(name, format, length, false, false));
    }
    
    public void addColumn(String name, String format, int length, boolean rightAligned) {
        this.columns.add(new CLITableColumn(name, format, length, false, rightAligned));
    }
    
    public void addColumn(String name, String format, boolean rightAligned) {
        this.columns.add(new CLITableColumn(name, format, 0, true, rightAligned));
    }

    public void addRow(Object...args) {
        if (args.length != this.columns.size()) {
            throw new IllegalArgumentException(
                String.format("row size (actual:%d) must equal to column size(%d)", args.length, this.columns.size())
            );
        }
        List<Object> row = new ArrayList<Object>();
        for(Object arg : args) {
            row.add(arg);
        }
        this.rows.add(row);
    }

    void calcWidth() {
        for(int i = 0; i < this.columns.size(); i++) {
            CLITableColumn col = this.columns.get(i);
            if(col.autoSized) {
                int maxLength = 1, length = 0;
                length = col.name.length();
                if(length > maxLength) {
                    maxLength = length;
                }
                
                for(Object item : this.rows) {
                    length = col.calcWidth(item);
                    if(length > maxLength) {
                        maxLength = length;
                    }
                }
                col.width = maxLength + 1;
            }
        }
    }

    @Override
    public String toString() {
        this.calcWidth();
        List<String> colstrs = new ArrayList<String>();
        String[] dummy = new String[0];
        StringBuilder sb = new StringBuilder();
        for(CLITableColumn col : this.columns) {
            colstrs.add(col.getHeaderStr());
        }
        sb.append(String.join(" ", colstrs.toArray(dummy)));
        sb.append("\n");
        colstrs.clear();
        for(CLITableColumn col : this.columns) {
            colstrs.add(col.getHeaderLine());
        }
        sb.append(String.join(" ", colstrs.toArray(dummy)));
        sb.append("\n");
        colstrs.clear();
        for(List<Object> row : this.rows) {
            for(int i = 0; i < this.columns.size(); i++){
                colstrs.add(this.columns.get(i).getRowString(row.get(i)));
            }
            sb.append(String.join(" ", colstrs.toArray(dummy)));
            sb.append("\n");
            colstrs.clear();
        }
        return sb.toString();
    }

    public void print() {
        System.out.print(this.toString());
    }
}