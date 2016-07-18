package com.spark.mesa_explorer.api;

import lejos.robotics.Color;

public class RobotLocation {
	
	public RobotLocation(double x, double y, double heading) {
		super();
		this.x = x;
		this.y = y;
		this.heading = heading;
		this.type = 0;
		this.color = Color.WHITE;
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



	@Override
	public String toString() {
		String str = String.format("loc [x=%.2f,y=%.2f,heading=%.2f,type=%d,inQueuetime=%d", x,y,heading,type,inQueuetime);
		return str;
	}



	private double x;
	private double y;
	private double heading;
	private long inQueuetime;
	//0 normal 1 deposit 2 NGZ 3 obstacle 4 base
	private int type;
	private int color = -1;
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
	
}
