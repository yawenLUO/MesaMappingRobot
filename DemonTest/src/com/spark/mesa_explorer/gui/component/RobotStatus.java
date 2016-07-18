package com.spark.mesa_explorer.gui.component;

public class RobotStatus {
	private float heading;
	private float x;
	private float y;
	public float getHeading() {
		return heading;
	}
	public void setHeading(float heading) {
		this.heading = heading;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	@Override
	public String toString() {
		return "RobotStatus [heading=" + heading + ", x=" + x + ", y=" + y + "]";
	}
	
	
}
