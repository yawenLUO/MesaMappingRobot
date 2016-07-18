package com.spark.mesa_explorer.robot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import com.spark.mesa_explorer.api.Command;
import com.spark.mesa_explorer.api.UserException;
import com.spark.mesa_explorer.gui.component.MapPanel;

public class AgentRobot implements Robot {
	private static AgentRobot agentRobot;
	private float x = MapPanel.MAP_LEFT_MARGIN + MapPanel.BOUNDARY_THICK;
	private float y = MapPanel.MAP_TOP_MARGIN + MapPanel.BOUNDARY_THICK;
	private float heading;
	private float xOffset = 0f;
	private float yOffset = 0f;
	private Set<LocationChangeListener> locationChangeListeners = new HashSet<LocationChangeListener>();
	private Socket socket;
	private Socket commandSocket;
	private boolean autoExploration = false;
	private boolean connected = false;
	//0 normal 1 deposit 2 NGZ 3 obstacle 4 base
	private int type;
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	private int color = -1;

	public boolean isConnected() {
		return connected;
	}

	public void setConnected(boolean connected) {
		if (connected){
			//connect to sockets
			SocketAddress socketAddress = new InetSocketAddress("10.0.1.1", 30000);
			//timeout 10 secs
			try {
				socket.connect(socketAddress, 10*1000);
				InputStream inputStream = socket.getInputStream();
				final Scanner scanner = new Scanner(inputStream);

				OutputStream outputStream = socket.getOutputStream();
				out = new PrintWriter(outputStream, true /* autoFlush */);
				robotExector = new RobotExector(scanner);
				robotExecutorThread = new Thread(robotExector);
				robotExecutorThread.start();
			} catch (IOException e) {
				throw new UserException("fail to connect to " + socketAddress.toString(),e);
			}
			
			//connect to sockets
			SocketAddress commandAddress = new InetSocketAddress("10.0.1.1", 29999);
			//timeout 10 secs
			try {
				commandSocket.connect(commandAddress, 10*1000);
			} catch (IOException e) {
				throw new UserException("fail to connect to " + commandAddress.toString(),e);
			}			
			
		}
		
		if (!connected){
			try {
				socket.close();
			} catch (IOException e) {
				throw new UserException("Fail to close the socket " + socket);
			}
			try {
				commandSocket.close();
			} catch (IOException e) {
				throw new UserException("Fail to close the socket "+ commandSocket);
			}
		}
		this.connected = connected;
	}

	public boolean isAutoExploration() {
		return autoExploration;
	}

	public void setAutoExploration(boolean autoExploration) {
		this.autoExploration = autoExploration;
	}

	PrintWriter out;
	private RobotExector robotExector;
	private Thread robotExecutorThread;

	private class RobotExector implements Runnable {
		private Scanner scanner;

		public RobotExector(Scanner scanner) {
			this.scanner = scanner;
		}

		public void run() {

			while (true) {
				float oldX = x;
				float oldY = y;

				Command command = Command.createLocationRequest();
				System.out.println("request:" + command.toString());
				out.println(command.toString());
				if (out.checkError()) {
					System.err.println("fail to write to network");
				}
				System.out.println("request:" + command.toString());
				String line = scanner.nextLine();
				System.out.println("line = " + line);
				Command response = Command.parseCommand(line);
				
				x = Float.parseFloat(response.getParam("x"));
				y = Float.parseFloat(response.getParam("y"));
				int kind = Integer.parseInt(response.getParam("type"));
				if (kind == 1){
					color = Integer.parseInt(response.getParam("color"));
				}

				float lHeading = Float.parseFloat(response.getParam("heading"));
				if (lHeading < 0 && Math.abs(lHeading) % 90 == 0) {
					heading = lHeading;
				}

				if (heading == -90) {
					xOffset = MapPanel.getInnerRight() - x;
					yOffset = 0;
				} else if (heading == -180) {
					xOffset = 0;
					yOffset = MapPanel.getInnerBottom() - y;
				} else if (heading == -270) {
					xOffset = MapPanel.getInnerLeft() - x;
					yOffset = 0;
				}
				x += xOffset;
				y += yOffset;

				if (x < MapPanel.getInnerLeft()) {
					x = MapPanel.getInnerLeft();
				}

				if (y < MapPanel.getInnerTop()) {
					y = MapPanel.getInnerTop();
				}

				if (x > MapPanel.getInnerRight()) {
					x = MapPanel.getInnerRight();
				}

				if (y > MapPanel.getInnerBottom()) {
					y = MapPanel.getInnerBottom();
				}
				System.out.println(response);
				fireLocationChangeEvent(x, y, oldX, oldY);
			}

		}

	}

	private AgentRobot() {

		socket = new Socket();
		commandSocket = new Socket();

	}

	protected void fireLocationChangeEvent(float newX, float newY, float oldX, float oldY) {
		for (LocationChangeListener locationChangeListener : locationChangeListeners) {
			locationChangeListener.onLocationChanged(newX, newY, oldX, oldY);
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

	public Command executeCommand(Command command) {
		try {
			OutputStream outputStream = commandSocket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream, true /* autoFlush */);
			printWriter.println(command.toString());

			InputStream inputStream = commandSocket.getInputStream();
			final Scanner scanner = new Scanner(inputStream);
			Command response = Command.parseCommand(scanner.nextLine());
			return response;
		} catch (IOException e) {
			throw new UserException("network IO error", e);
		}
	}

	public void moveForward() {
		Command request = Command.createRequestCommand(Command.MOVE_FOWARD);
		try {
		    this.executeCommand(request);	
		} catch (UserException e) {
			UserException.handeException(null, e);
		}
	}

	public void moveBackward() {
		Command request = Command.createRequestCommand(Command.MOVE_BACKWORD);
		try {
		    this.executeCommand(request);	
		} catch (UserException e) {
			UserException.handeException(null, e);
		}
	}

	public void turnLeft() {
		Command request = Command.createRequestCommand(Command.TURN_LEFT);
		try {
		    this.executeCommand(request);	
		} catch (UserException e) {
			UserException.handeException(null, e);
		}
	}

	public void turnRight() {
		Command request = Command.createRequestCommand(Command.TURN_RIGHT);
		try {
		    this.executeCommand(request);	
		} catch (UserException e) {
			UserException.handeException(null, e);
		}
	}

	public void stop() {
		Command request = Command.createRequestCommand(Command.STOP);
		try {
		    this.executeCommand(request);	
		} catch (UserException e) {
			UserException.handeException(null, e);
		}
	}

	// get a single instance
	public static Robot getSingleInstance() {
		if (agentRobot == null) {
			agentRobot = new AgentRobot();
		}
		return agentRobot;
	}

	@Override
	public String toString() {
		return "AgentRobot [x=" + x + ", y=" + y + ", heading=" + heading + ", xOffset=" + xOffset + ", yOffset="
				+ yOffset + ", connected=" + connected + ", type=" + type + ", color=" + color + "]";
	}
	
	


}
