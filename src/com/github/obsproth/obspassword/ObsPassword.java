package com.github.obsproth.obspassword;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class ObsPassword extends JFrame {

	private static final String VERSION = "0.1.1";
	private static final String DATA_FILE = "data.csv";
	public static final int ALGO_VERSION = 1;

	ServiceTableModel tableModel;
	JTable table;
	JPasswordField passwordField;

	public ObsPassword(List<ServiceElement> list) {
		setSize(600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		setLayout(new BorderLayout());
		tableModel = new ServiceTableModel();
		table = new JTable(tableModel);
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		JLabel versionLabel = new JLabel("v" + VERSION);
		southPanel.add(versionLabel, BorderLayout.WEST);
		passwordField = new JPasswordField();
		southPanel.add(passwordField, BorderLayout.CENTER);
		JButton genButton = new JButton("GEN");
		genButton.addActionListener(event -> {
			if (isPasswordFieldEmpty()) {
				JOptionPane.showMessageDialog(this, "ERROR : The password field is empty.");
				return;
			}
			ServiceElement element = tableModel.getSelectedElement(table.getSelectedRow());
			if (element == null) {
				JOptionPane.showMessageDialog(this, "ERROR : NO SELECTED ROW");
				return;
			}
			if (!element.getBaseHash().equals(HashUtil.getBaseHashStr(passwordField.getPassword()))) {
				JOptionPane.showMessageDialog(this, "ERROR : Password mismatch.");
				return;
			}
			byte[] hash = HashUtil.calcHash(passwordField.getPassword(), element.getServiceName(), element.getLength());
			String passwordStr = Base64.getEncoder().encodeToString(hash).substring(0, element.getLength());
			switch (JOptionPane.showConfirmDialog(this, "Do you want to copy the password to the clipboard?", "",
					JOptionPane.YES_NO_CANCEL_OPTION)) {
			case JOptionPane.YES_OPTION:
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(passwordStr), null);
				break;
			case JOptionPane.NO_OPTION:
				JOptionPane.showMessageDialog(this, passwordStr);
			}
		});
		southPanel.add(genButton, BorderLayout.EAST);

		add(southPanel, BorderLayout.SOUTH);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 2));
		JButton addButton = new JButton("ADD");
		addButton.addActionListener(event -> {
			if (isPasswordFieldEmpty()) {
				JOptionPane.showMessageDialog(this, "ERROR : The password field is empty.");
				return;
			}
			String name, lengthStr;
			name = JOptionPane.showInputDialog(this, "Name");
			if (name == null) {
				return;
			}
			lengthStr = JOptionPane.showInputDialog(this, "Length");
			int length;
			try {
				length = Integer.parseInt(lengthStr);
			} catch (NumberFormatException e) {
				return;
			}
			if (length <= 0) {
				return;
			}
			addData(new ServiceElement(name, length, HashUtil.getBaseHashStr(passwordField.getPassword())));
			writeFile();
		});
		northPanel.add(addButton);
		JButton deleteButton = new JButton("DELETE");
		deleteButton.addActionListener(event -> {
			int row = table.getSelectedRow();
			if (row >= 0) {
				StringBuilder sb = new StringBuilder();
				sb.append("Are you sure you want to delete ");
				sb.append(tableModel.getSelectedElement(row).getServiceName());
				sb.append(" ?");
				switch (JOptionPane.showConfirmDialog(this, sb.toString(), "Delete", JOptionPane.OK_CANCEL_OPTION)) {
				case JOptionPane.OK_OPTION:
					tableModel.removeRow(row);
					writeFile();
					break;
				}
			}
		});
		northPanel.add(deleteButton);
		add(northPanel, BorderLayout.NORTH);
		//
		for (ServiceElement element : list) {
			addData(element);
		}
		//
		setVisible(true);
	}

	public boolean isPasswordFieldEmpty() {
		char[] password = passwordField.getPassword();
		Arrays.fill(password, (char) 0);
		return password.length == 0;
	}

	public void addData(ServiceElement element) {
		tableModel.addRow(element);
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

	public static List<ServiceElement> readFile() throws IOException {
		List<ServiceElement> list = new ArrayList<>();
		File file = new File(DATA_FILE);
		if (file.exists() && file.isFile()) {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String str;
			while ((str = br.readLine()) != null) {
				if (!str.isEmpty()) {
					list.add(ServiceElement.buildFromCSV(str));
				}
			}
			br.close();
		}
		return list;
	}

	public static void main(String[] args) throws IOException {
		new ObsPassword(readFile());
	}

}
