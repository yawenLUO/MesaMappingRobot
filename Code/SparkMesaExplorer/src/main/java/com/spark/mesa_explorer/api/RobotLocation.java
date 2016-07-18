package com.spark.mesa_explorer.api;

public class RobotLocation {
	
	public RobotLocation(double x, double y, double heading) {
		super();
		this.x = x;
		this.y = y;
		this.heading = heading;
		inQueuetime = System.currentTimeMillis();
	}
	
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getHeading() {
		return heading;
	}
	
	
	public long getInQueuetime() {
		return inQueuetime;
	}



	private double x;
	private double y;
	private double heading;
	private long inQueuetime;
	
}
