package com.spark.mesa_explorer.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

import Initiall.Test;
import lejos.robotics.Color;


public class CommandExecutor implements Runnable {
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;
	private static CommandExecutor commandExecutor;
	private ServerSocket serverSocket;
	
	private CommandExecutor() {
		super();
		try {
			serverSocket = new ServerSocket(29999);
		} catch (IOException e) {
			System.err.println("Faiil to open server port 29999");
			e.printStackTrace();
		}
	}
	
	public static CommandExecutor getSingleInstance(){
		if (commandExecutor == null){
			commandExecutor = new CommandExecutor();
		}
		return commandExecutor;
	}
	
	public synchronized static void startUp(){
		new Thread(getSingleInstance()).start();
	}



	public void run() {


		while (true) {
			try {
				socket = serverSocket.accept();
				System.out.println("A socket is created");
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Scanner scanner = new Scanner(inputStream);
			while (socket.isConnected() && scanner.hasNextLine()) {
				String line = scanner.nextLine();
				System.out.println("line = " + line);
				// receive request commands from PC
				final Command request = Command.parseCommand(line);
				Command response = Command.createResponse(request);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						if (request.getName().equals(Command.MOVE_FOWARD)){
							float[] angle={0};
							Test.angleProvider.fetchSample(angle, 0);
							switch(Test.direction()){
							case 1:{
								Test.straightF(angle[0],Color.WHITE,Test.locationGlobal[0] + 10,Double.NaN);
								break;
							}
							case 2:{
								Test.straightF(angle[0],Color.WHITE,Double.NaN,Test.locationGlobal[1] + 10);
								break;
							}
							case 3:{
								Test.straightF(angle[0],Color.WHITE,Test.locationGlobal[0] - 10,Double.NaN);
								break;
							}
							case 4:{
								Test.straightF(angle[0],Color.WHITE,Double.NaN,Test.locationGlobal[1] - 10);
								break;
							}
							default : break;
						}
						}else if (request.getName().equals(Command.MOVE_BACKWORD)){
							float[] angle={0};
							Test.angleProvider.fetchSample(angle, 0);
							switch(Test.direction()){
							case 1:{
								Test.straightB(angle[0],Color.RED,Test.locationGlobal[0]-5.2,Double.NaN);
								break;
							}
							case 2:{
								Test.straightB(angle[0],Color.RED,Double.NaN,Test.locationGlobal[1]-5.2);
								break;
							}
							case 3:{
								Test.straightB(angle[0],Color.RED,Test.locationGlobal[0]+5.2,Double.NaN);
								break;
							}
							case 4:{
								Test.straightB(angle[0],Color.RED,Double.NaN,Test.locationGlobal[1]+5.2);
								break;
							}
							default : break;
						}
						}else if (request.getName().equals(Command.TURN_LEFT)){
							float[] angle={0};
							Test.angleProvider.fetchSample(angle, 0);
							Test.l_turn(angle[0]-90);
						}else if (request.getName().equals(Command.TURN_RIGHT)){
							float[] angle={0};
							Test.angleProvider.fetchSample(angle, 0);
							Test.r_turn(angle[0]+90);
						}else if (request.getName().equals(Command.STOP)){
							Test.halt();
						}
					}
				}).start();

				
				
				if (response != null){
					PrintWriter out = new PrintWriter(outputStream, true);
					out.println(response.toString());
					System.out.println("reponse = " + response.toString());
				}

				
			}
		} // endwhile

	
	}

}
