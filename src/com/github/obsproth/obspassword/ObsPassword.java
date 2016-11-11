package com.github.obsproth.obspassword;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ObsPassword extends JFrame {

	private static final String DATA_FILE = "data.csv";
	private static final String[] COLUMN_NAMES = { "Name", "Length", "BaseHash", "Version" };
	private static final int VERSION = 1;

	private static final String HASH_ALGO = "SHA-512";

	private static final String BASE_SALT = "ObsPassword";
	private static final int STRETCHING = 123456;

	DefaultTableModel tableModel;
	JTable table;
	JPasswordField passwordField;

	public ObsPassword(List<String[]> list) {
		setSize(600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		setLayout(new BorderLayout());
		tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
		table = new JTable(tableModel);
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BorderLayout());
		passwordField = new JPasswordField();
		southPanel.add(passwordField, BorderLayout.CENTER);
		JButton genButton = new JButton("GEN");
		genButton.addActionListener(event -> {
			int row = table.getSelectedRow();
			if (row < 0) {
				JOptionPane.showMessageDialog(this, "NO SELECTED ROW");
				return;
			}
			if (!table.getValueAt(row, 2).equals(getBaseHashStr())) {
				JOptionPane.showMessageDialog(this, "ERROR");
				return;
			}
			//
		});
		southPanel.add(genButton, BorderLayout.EAST);
		add(southPanel, BorderLayout.SOUTH);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());
		JButton saveButton = new JButton("SAVE");
		saveButton.addActionListener(event -> writeFile());
		northPanel.add(saveButton, BorderLayout.EAST);
		JButton addButton = new JButton("ADD");
		addButton.addActionListener(event -> {
			String[] s = new String[COLUMN_NAMES.length];
			s[0] = JOptionPane.showInputDialog(this, COLUMN_NAMES[0]);
			if (s[0] == null) {
				return;
			}
			s[1] = JOptionPane.showInputDialog(this, COLUMN_NAMES[1]);
			int len;
			try {
				len = Integer.parseInt(s[1]);
			} catch (NumberFormatException e) {
				return;
			}
			if (len <= 0) {
				return;
			}
			s[2] = getBaseHashStr();
			s[3] = String.valueOf(VERSION);
			addData(s);
		});
		northPanel.add(addButton, BorderLayout.WEST);
		add(northPanel, BorderLayout.NORTH);
		//
		for (String[] data : list) {
			addData(data);
		}
		//
		setVisible(true);
	}

	public void addData(String[] data) {
		tableModel.addRow(data);
	}

	private String getBaseHashStr() {
		byte[] passByte = calcHash(BASE_SALT, 0);
		//
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < passByte.length; i++) {
			sb.append(String.format("%02x", passByte[i]));
		}
		return sb.toString();
	}

	private byte[] calcHash(String name, int l) {
		char[] password = passwordField.getPassword();
		byte[] passByte = new byte[password.length + name.length() + 1];
		Arrays.fill(passByte, (byte) 0);
		int i;
		for (i = 0; i < password.length; i++) {
			passByte[i] = (byte) password[i];
		}
		int offset = i;
		for (i = 0; i < name.length(); i++) {
			passByte[i + offset] = (byte) name.charAt(i);
		}
		passByte[passByte.length - 1] = (byte) l;
		//
		MessageDigest md;
		try {
			md = MessageDigest.getInstance(HASH_ALGO);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
		byte[] passByte_s;
		for (i = 0; i < STRETCHING; i++) {
			passByte_s = md.digest(passByte);
			Arrays.fill(passByte, (byte) 0);
			passByte = passByte_s;
		}
		return passByte;
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
		new ObsPassword(readFile());
	}

}
