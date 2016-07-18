package com.spark.mesa_explorer.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.spark.mesa_explorer.api.Command;
import com.spark.mesa_explorer.gui.component.MapPanel;

public class AgentRobot implements Robot {
	private float x = MapPanel.MAP_LEFT_MARGIN + MapPanel.BOUNDARY_THICK;
	private float y = MapPanel.MAP_TOP_MARGIN + MapPanel.BOUNDARY_THICK;
	private float heading;
	private Set<LocationChangeListener> locationChangeListeners = new HashSet<LocationChangeListener>();
	
	public AgentRobot() {

			
			try {
				final Socket socket = new Socket("10.0.1.1", 30000);
				System.out.println("socket connected to robot");
				//final Socket socket = new Socket("127.0.0.1", 30000);
				InputStream inputStream = socket.getInputStream();
				final Scanner scanner = new Scanner(inputStream);
				
				OutputStream outputStream = socket.getOutputStream();
				final PrintWriter out = new PrintWriter(outputStream, true /* autoFlush */);
				new Thread(new Runnable() {
					
					public void run() {
						while(true){
							float oldX = x;
							float oldY = y;
							
							Command command = Command.createLocationRequest();
							System.out.println("request:" + command.toString());
							out.println(command.toString());
							if (out.checkError()){
								System.err.println("fail to write to network");
							}
							System.out.println("request:" + command.toString());
							String line = scanner.nextLine();
							System.out.println("line = " + line);
							Command response = Command.parseCommand(line);
							x = Float.parseFloat(response.getParam("x"));
							y = Float.parseFloat(response.getParam("y"));
							if (x < MapPanel.getInnerLeft()){
								x = MapPanel.getInnerLeft();
							}
							
							if (y < MapPanel.getInnerTop()){
								y = MapPanel.getInnerTop();
							}
							
							if (x > MapPanel.getInnerRight()){
								x = MapPanel.getInnerRight();
							}
							
							if (y > MapPanel.getInnerBottom()){
								y = MapPanel.getInnerBottom();
							}	
							System.out.println(response);
							fireLocationChangeEvent(x,y,oldX,oldY);
						}
					}
				}).start();

				
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
//			Timer timer = new Timer(true);
//	        timer.scheduleAtFixedRate(new TimerTask() {
//				
//				@Override
//				public void run() {
//					float oldX = x;
//					float oldY = y;
//					
//					Command command = Command.createLocationRequest();
//					out.println(command.toString());
//					System.out.println("request:" + command.toString());
//					String line = scanner.nextLine();
//					System.out.println("line = " + line);
//					Command response = Command.parseCommand(line);
//					x = Float.parseFloat(response.getParam("x"));
//					y = Float.parseFloat(response.getParam("y"));
//					System.out.println(response);
//					fireLocationChangeEvent(x,y,oldX,oldY);
//				}
//			}, 0, 1*500);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
	
	}
	
	protected void fireLocationChangeEvent(float newX, float newY, float oldX, float oldY) {
		for (LocationChangeListener locationChangeListener : locationChangeListeners) {
			locationChangeListener.onLocationChanged(newX,newY,oldX,oldY);
		}
	}

	public float getXLocation() {
		return x;
	}

	public float getYLocation() {
		return y;
	}

	public float getHeading() {
		return heading;
	}

	public void addLocationChangeListener(LocationChangeListener event) {
		locationChangeListeners.add(event);		
	}

}
