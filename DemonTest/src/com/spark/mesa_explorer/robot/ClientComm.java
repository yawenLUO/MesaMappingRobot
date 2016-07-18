package com.spark.mesa_explorer.robot;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;

//used for test
public class ClientComm {

	public static void main(String[] args) {
		try {
			Socket socket = new Socket("10.0.1.1", 30000);
			try {
				OutputStream outputStream = socket.getOutputStream();
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
				bufferedWriter.write("hello,robot");
				bufferedWriter.flush();
			} finally {
				socket.close();
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
