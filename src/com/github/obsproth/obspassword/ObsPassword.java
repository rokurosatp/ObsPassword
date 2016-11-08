package com.github.obsproth.obspassword;

import java.awt.BorderLayout;

import javax.swing.JFrame;

public class ObsPassword extends JFrame {

	public ObsPassword() {
		setSize(600, 800);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//
		setLayout(new BorderLayout());
		//
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new ObsPassword();
	}

}
