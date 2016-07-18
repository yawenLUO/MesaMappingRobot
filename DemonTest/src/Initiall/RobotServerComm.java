package Initiall;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Scanner;

import com.spark.mesa_explorer.api.Command;
import com.spark.mesa_explorer.api.RobotLocation;

/**
 * The thread used for creating new threads for connections
 * 
 * @author Yun
 *
 */
public class RobotServerComm implements Runnable {
	/**
	 * The test classes
	 */
	private Socket socket;
	private InputStream inputStream;
	private OutputStream outputStream;

	// the queue for the incoming request
	private PriorityQueue<Command> requestQueue = new PriorityQueue<Command>();

	// the queue for the outgoing request
	private PriorityQueue<Command> responseQueue = new PriorityQueue<Command>();

	private Queue<RobotLocation> robotLocationQueue = new LinkedList<RobotLocation>();

	private static RobotServerComm robotServerComm;

	private RobotServerComm() {
	}

	private synchronized Socket getSocket() {
		return socket;
	}

	private synchronized void setSocket(Socket socket) {
		this.socket = socket;
	}

	public static synchronized RobotServerComm getSingleInstance() {
		if (robotServerComm == null) {
			robotServerComm = new RobotServerComm();
		}
		return robotServerComm;
	}

	public void addRobotLocation(RobotLocation robotLocation) {
		synchronized (robotLocationQueue) {
			robotLocationQueue.add(robotLocation);
			System.out.println("robotLocation" + robotLocation.toString());
			robotLocationQueue.notifyAll();
		}
	}

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(30000);
			System.out.println("server started up on port 30000");
			while (true) {
				socket = serverSocket.accept();
				System.out.println("A socket is created");
				inputStream = socket.getInputStream();
				outputStream = socket.getOutputStream();
				// new Thread(new RequestReader(inputStream)).start();
				// each line is a command

				Scanner scanner = new Scanner(inputStream);
				while (socket.isConnected() && scanner.hasNextLine()) {
					String line = scanner.nextLine();
					System.out.println("line = " + line);
					// receive request commands from PC
					Command command = Command.parseCommand(line);

					RobotLocation robotLocation = null;
					// execute command
					synchronized (robotLocationQueue) {
						while (robotLocationQueue.isEmpty()) {
							try {
								robotLocationQueue.wait();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						robotLocation = robotLocationQueue.remove();
						robotLocationQueue.notifyAll();
					}

					if (robotLocation != null) {
						Command response = Command.createLocationResponse(robotLocation);

						PrintWriter out = new PrintWriter(outputStream, true);
						out.println(response.toString());

						System.out.println("reponse = " + response.toString());
					}
				}
			} // endwhile
		} catch (IOException e) {
			System.out.println("Network error");
		}

	}

	private class RequestReader implements Runnable {
		private Scanner scanner;

		public RequestReader(InputStream inputStream) {
			scanner = new Scanner(inputStream);
		}

		public void run() {
			// each line is a command
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				// receive request commands from PC
				Command command = Command.parseCommand(line);
				synchronized (requestQueue) {
					requestQueue.add(command);
					requestQueue.notifyAll();
				}
			}
		}
	}

	private class ResponseWriter implements Runnable {
		private OutputStream outputStream;
		private BufferedWriter bufferedWriter;

		public ResponseWriter(OutputStream outputStream) {
			this.outputStream = outputStream;
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
		}

		public void run() {
			// write the reponse to pc
			synchronized (responseQueue) {
				while (responseQueue.isEmpty()) {
					try {
						responseQueue.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}

				Command response = responseQueue.remove();
				try {
					bufferedWriter.write(response.toString());
					bufferedWriter.flush();
				} catch (IOException e) {
					System.err.println("Fail to write to the network");
					e.printStackTrace();
				}
				// let the robot execute the command
				responseQueue.notifyAll();
			}
		}
	}

	public static void main(String[] args) {
		new Thread(RobotServerComm.getSingleInstance()).start();
	}
}
