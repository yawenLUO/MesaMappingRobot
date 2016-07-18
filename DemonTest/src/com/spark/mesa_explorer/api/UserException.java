package com.spark.mesa_explorer.api;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class UserException extends RuntimeException {

	private static final long serialVersionUID = 8648362410195281957L;

	public UserException() {
	}

	public UserException(String arg0) {
		super(arg0);
	}

	public UserException(Throwable arg0) {
		super(arg0);
	}

	public UserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public UserException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
//		super(arg0, arg1, arg2, arg3);
	}

	public static void handeException(JFrame frame, UserException e) {
		JOptionPane.showMessageDialog(frame, e.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
		e.printStackTrace();
	}

}
