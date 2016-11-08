package com.github.obsproth.obspassword;

import java.awt.BorderLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ObsPassword extends JFrame {

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

	public static void main(String[] args) {
		new ObsPassword();
	}

}
