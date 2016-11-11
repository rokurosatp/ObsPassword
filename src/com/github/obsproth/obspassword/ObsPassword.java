package com.github.obsproth.obspassword;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ObsPassword extends JFrame {

	private static final String DATA_FILE = "data.csv";
	
	private static final String[] COLUMN_NAMES = { "Name", "Length", "BaseHash" };

	DefaultTableModel tableModel;
	JTable table;

	public ObsPassword() {
		setSize(600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		setLayout(new BorderLayout());
		tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
		table = new JTable(tableModel);
		add(new JScrollPane(table), BorderLayout.CENTER);

		JButton button = new JButton("add row");
		button.addActionListener(event -> tableModel.insertRow(0, new Object[COLUMN_NAMES.length]));
		add(button, BorderLayout.SOUTH);
		//
		setVisible(true);
	}

	public void writeFile() {
		PrintWriter pw;
		try {
			pw = new PrintWriter(DATA_FILE);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			pw.print(table.getValueAt(i, 0));
			for (int j = 1; j < tableModel.getColumnCount(); j++) {
				pw.print(',');
				pw.print(table.getValueAt(i, j));
			}
			pw.println();
		}
		pw.close();
	}


	public static List<String[]> readFile() throws IOException {
		List<String[]> list = new ArrayList<>();
		File file = new File(DATA_FILE);
		if (file.exists() && file.isFile()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				if (!str.isEmpty()) {
					list.add(str.split(","));
				}
			}
			br.close();
		}
		return list;
	}


	public static void main(String[] args) throws IOException {
		new ObsPassword();
	}

}
