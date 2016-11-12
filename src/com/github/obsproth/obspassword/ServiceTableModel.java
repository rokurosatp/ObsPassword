package com.github.obsproth.obspassword;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public class ServiceTableModel extends AbstractTableModel {

	private static final String[] COLUMN_NAMES = { "Name", "Length", "BaseHash", "Version" };

	private List<ServiceElement> serviceList = new ArrayList<>();

	public ServiceTableModel() {
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRowCount() {
		return serviceList.size();
	}

	@Override
	public Object getValueAt(int row, int column) {
		ServiceElement element = serviceList.get(row);
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
		// TODO Auto-generated method stub
	}


}
