package com.github.obsproth.prismkey;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ServiceTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = { "Name", "Length", "BaseHash", "Version" };

	public List<ServiceElement> list = new ArrayList<>();

	public ServiceTableModel() {
	}

	@Override
	public int getColumnCount() {
		return COLUMN_NAMES.length;
	}

	@Override
	public int getRowCount() {
		return list.size();
	}

	@Override
	public String getColumnName(int column) {
		return COLUMN_NAMES[column];
	}

	public ServiceElement getSelectedElement(int row) {
		return 0 <= row ? list.get(row) : null;
	}

	@Override
	public Object getValueAt(int row, int column) {
		ServiceElement element = list.get(row);
		switch (column) {
		case 0:
			return element.getServiceName();
		case 1:
			return element.getLength();
		case 2:
			return element.getBaseHash();
		case 3:
			return element.getVersion();
		default:
			throw new RuntimeException("column " + column + "is out of bounds.");
		}
	}

	public void addRow(ServiceElement element) {
		list.add(element);
		fireTableRowsInserted(list.size(), list.size());
	}

	public void removeRow(int row){
		list.remove(row);
		fireTableRowsDeleted(row, row);
	}
	
}
