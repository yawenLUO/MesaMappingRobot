package com.spark.mesa_explorer.robot;

/**
 * @author Yun
 *
 */
public interface Robot {

	float getXLocation();

	float getYLocation();
	
	float getHeading();
	
	void addLocationChangeListener(LocationChangeListener event);
		
}
