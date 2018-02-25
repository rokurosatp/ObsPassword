package com.github.obsproth.prismkey;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.github.obsproth.prismkey.common.generator.AbstractGenerator;
import com.github.obsproth.prismkey.common.generator.GeneratorFactory;
import com.github.obsproth.prismkey.common.generator.GeneratorV2;

public class PrismKey extends JFrame {

	private static final String VERSION = "0.1.2";
	private static final String DATA_FILE = "data.csv";
	public static final int ALGO_VERSION = 2;

	ServiceTableModel tableModel;
	JTable table;
	JPasswordField passwordField;

	public PrismKey(List<ServiceElement> list) {
		setSize(600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("PrismKey");
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
		genButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (isPasswordFieldEmpty()) {
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : The password field is empty.");
					return;
				}
				ServiceElement element = tableModel.getSelectedElement(table.getSelectedRow());
				if (element == null) {
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : NO SELECTED ROW");
					return;
				}
				AbstractGenerator generator = GeneratorFactory.getGenerator(element);
				char[] seedPassword = passwordField.getPassword();
				if (generator.verifySeed(seedPassword, element)) {
					char[] password = generator.generate(seedPassword, element);
					switch (JOptionPane.showConfirmDialog(PrismKey.this, "Do you want to copy the password to the clipboard?", "",
							JOptionPane.YES_NO_CANCEL_OPTION)) {
					case JOptionPane.YES_OPTION:
						Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(new String(password)), null);
						break;
					case JOptionPane.NO_OPTION:
						JOptionPane.showMessageDialog(PrismKey.this, new String(password));
						break;
					}
					Arrays.fill(password, '0');
				}
				else{
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : Password mismatch.");
				}
				Arrays.fill(seedPassword, '0');
			}
		});
		southPanel.add(genButton, BorderLayout.EAST);

		add(southPanel, BorderLayout.SOUTH);

		JPanel northPanel = new JPanel();
		northPanel.setLayout(new GridLayout(1, 2));
		JButton addButton = new JButton("ADD");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (isPasswordFieldEmpty()) {
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : The password field is empty.");
					return;
				}
				String name, lengthStr;
				name = JOptionPane.showInputDialog(PrismKey.this, "Name");
				if (name == null || name.isEmpty()) {
					return;
				}else if(name.contains(",")){
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : The name must not contain ','");
					return;
				}
				lengthStr = JOptionPane.showInputDialog(PrismKey.this, "Length");
				int length;
				try {
					length = Integer.parseInt(lengthStr);
				} catch (NumberFormatException e) {
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : The length is invalid.");
					return;
				}
				if (length <= 0) {
					JOptionPane.showMessageDialog(PrismKey.this, "ERROR : The length must be a positive integer.");
					return;
				}
				List<String> config = new ArrayList<String>();
				config.add(JOptionPane.showConfirmDialog(PrismKey.this, "allowNumbers", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ? "True" : "False");
				config.add(JOptionPane.showConfirmDialog(PrismKey.this, "allowCaps", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ? "True" : "False");
				config.add(JOptionPane.showConfirmDialog(PrismKey.this, "allowSmalls", "", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION ? "True" : "False");
				config.add(JOptionPane.showInputDialog("symbols", GeneratorV2.DEFAULT_SYMBOLS));
				char[] seedPassword = passwordField.getPassword();
				addData(new ServiceElement(name, length, GeneratorFactory.getLatestGenerator(config).getSeedDigestStr(seedPassword), ALGO_VERSION, config));
				writeFile();
			}
		});
		northPanel.add(addButton);
		JButton deleteButton = new JButton("DELETE");
		deleteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int row = table.getSelectedRow();
				if (row >= 0) {
					StringBuilder sb = new StringBuilder();
					sb.append("Are you sure you want to delete ");
					sb.append(tableModel.getSelectedElement(row).getServiceName());
					sb.append(" ?");
					switch (JOptionPane.showConfirmDialog(PrismKey.this, sb.toString(), "Delete", JOptionPane.OK_CANCEL_OPTION)) {
					case JOptionPane.OK_OPTION:
						tableModel.removeRow(row);
						writeFile();
						break;
					}
				}
			}
		});
		northPanel.add(deleteButton);
		add(northPanel, BorderLayout.NORTH);
		//
		for(ServiceElement element : list){
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
		for (ServiceElement element : tableModel.list) {
			pw.print(element.asCSV());
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
		new PrismKey(readFile());
	}

}
