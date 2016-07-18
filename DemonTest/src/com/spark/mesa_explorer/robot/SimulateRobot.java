package com.spark.mesa_explorer.robot;

import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.spark.mesa_explorer.gui.component.MapPanel;

public class SimulateRobot implements Robot {
	private float x = MapPanel.MAP_LEFT_MARGIN + MapPanel.BOUNDARY_THICK;
	private float y = MapPanel.MAP_TOP_MARGIN + MapPanel.BOUNDARY_THICK;
	private Set<LocationChangeListener> locationChangeListeners = new HashSet<LocationChangeListener>();
	public SimulateRobot(){
		Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				float oldX = x;
				float oldY = y;
				x+=10;
				y+=10;
				fireLocationChangeEvent(x,y,oldX,oldY);
			}
		}, 0, 1*1000);
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
		return 0;
	}

	public void addLocationChangeListener(LocationChangeListener event) {
		locationChangeListeners.add(event);
	}
	public void moveForward() {
		// TODO Auto-generated method stub
		
	}
	public void moveBackward() {
		// TODO Auto-generated method stub
		
	}
	public void turnLeft() {
		// TODO Auto-generated method stub
		
	}
	public void turnRight() {
		// TODO Auto-generated method stub
		
	}
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isAutoExploration() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void setAutoExploration(boolean autoExploration) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setConnected(boolean connected) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isConnected() {
		// TODO Auto-generated method stub
		return false;
	}
}
