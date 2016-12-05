package com.github.obsproth.obspassword;

public interface UserInterface {

	public boolean isPasswordFieldEmpty();

	public char[] getPassword();

	public void addData(ServiceElement element);
	
	public void showMessage(String message);
}
