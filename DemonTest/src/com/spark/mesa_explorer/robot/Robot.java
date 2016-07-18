package com.spark.mesa_explorer.robot;

/**
 * @author Yun
 *
 */
public interface Robot {

	float getXLocation();

	float getYLocation();
	
	float getHeading();
	
	void moveForward();
	void moveBackward();
	void turnLeft();
	void turnRight();
	void stop();
	
	void addLocationChangeListener(LocationChangeListener event);
	boolean isAutoExploration();
	void setAutoExploration(boolean autoExploration);
	
	boolean isConnected();
	void setConnected(boolean connected);
}
