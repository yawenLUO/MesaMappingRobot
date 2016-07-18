package com.spark.mesa_explorer.control_centre;

import java.awt.Color;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import com.spark.mesa_explorer.api.UserException;

import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMISampleProvider;
import lejos.remote.ev3.RemoteEV3;
import lejos.utility.Delay;

/**
 * Control the robot through the RMI interface
 * @author Yun
 *
 */
public class RobotAgent {
	private static RemoteEV3 remoteEv3;
	private static RMIRegulatedMotor leftMotor;
	private static RMIRegulatedMotor rightMotor;
	private static RMISampleProvider colorSampleProvider;
	private static RMISampleProvider distanceSampleProvider;
	private static RemoteEV3 getSingleRemoteEV3(){
		if (remoteEv3 != null){
			return remoteEv3;
		}else{
			try {
				remoteEv3 = new RemoteEV3("10.0.1.1");
				remoteEv3.setDefault();
				return remoteEv3;
			} catch (RemoteException e) {
				throw new UserException("Fail to connect to the robot.", e);
			} catch (MalformedURLException e) {
				throw new UserException("Fail to connect to the robot.", e);
			} catch (NotBoundException e) {
				throw new UserException("Fail to connect to the robot.", e);
			}
		}
	}
	
	
	public static void moveForward(){
		RMIRegulatedMotor left = getSingleLeftMotor();
		RMIRegulatedMotor right = getSingleRightMotor();
		try {
			left.forward();
			right.forward();	
		} catch (RemoteException e) {
			throw new UserException("Fail to move forward",e);
		}

	}
	
	public static void moveBackward(){
		RMIRegulatedMotor left = getSingleLeftMotor();
		RMIRegulatedMotor right = getSingleRightMotor();
		try {
			left.backward();
			right.backward();	
		} catch (RemoteException e) {
			throw new UserException("Fail to move forward",e);
		}

	}
	
    
	
	public static void turnLeft(){
		RMIRegulatedMotor left = getSingleLeftMotor();
		RMIRegulatedMotor right = getSingleRightMotor();
		try {
			left.setSpeed(360);
			right.setSpeed(360);
			left.backward();
			right.forward();
			Delay.msDelay(1000);
			stop();
		} catch (RemoteException e) {
			throw new UserException("Fail to move forward",e);
		}
	}
	
	public static void turnRight(){
		RMIRegulatedMotor left = getSingleLeftMotor();
		RMIRegulatedMotor right = getSingleRightMotor();
		try {
			left.setSpeed(360);
			right.setSpeed(360);
			left.forward();
			right.backward();
			Delay.msDelay(1000);
			stop();
		} catch (RemoteException e) {
			throw new UserException("Fail to move forward",e);
		}
	}
	
	public static void stop(){
		RMIRegulatedMotor left = getSingleLeftMotor();
		RMIRegulatedMotor right = getSingleRightMotor();
		try {
			left.stop(true);
			right.stop(false);
		} catch (RemoteException e) {
			throw new UserException("Fail to stop", e);
		}
	}

	private static RMIRegulatedMotor getSingleRightMotor() {
		if (rightMotor == null){
			RemoteEV3 remote = getSingleRemoteEV3();
			rightMotor = remote.createRegulatedMotor("C", 'L');
		}
		return rightMotor;
	}

	private static RMIRegulatedMotor getSingleLeftMotor() {
		
		if (leftMotor == null){
			RemoteEV3 remote = getSingleRemoteEV3();
			leftMotor = remote.createRegulatedMotor("B", 'L');
		}
		return leftMotor;
	}
	
	private static RMISampleProvider getSingleColorSampleProvider(){
		if (colorSampleProvider == null){
			RemoteEV3 remote = getSingleRemoteEV3();
			try {
				//modename ColorID
				colorSampleProvider = remote.createSampleProvider("S1", "lejos.hardware.sensor.EV3ColorSensor", "ColorID");
			} catch (Exception e) {
				if (colorSampleProvider != null){
					try {
						colorSampleProvider.close();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}
				colorSampleProvider = remote.createSampleProvider("S1", "lejos.hardware.sensor.EV3ColorSensor", "ColorID");
			}
		}
		return colorSampleProvider;
	}
	
	private static RMISampleProvider getSingleDistanceSampleProvider(){
		if (distanceSampleProvider == null){
			RemoteEV3 remote = getSingleRemoteEV3();
			try {
				distanceSampleProvider = remote.createSampleProvider("S2", "lejos.hardware.sensor.EV3UltrasonicSensor", "Distance");
			} catch (Exception e) {
				if (distanceSampleProvider != null){
					try {
						distanceSampleProvider.close();
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}
				distanceSampleProvider = remote.createSampleProvider("S2", "lejos.hardware.sensor.EV3UltrasonicSensor", "Distance");
			}
		}
		return distanceSampleProvider;
	}
	
	public static float[] senseColor(){
		RMISampleProvider sampleProvider = getSingleColorSampleProvider();
		try {
			try {
				float[] sample = sampleProvider.fetchSample();
				return sample;
			} finally {
			    colorSampleProvider.close();
			    colorSampleProvider = null;
			}
		} catch (RemoteException e) {
			throw new UserException("Fail to sense the color",e);
		}
	}
	
	public static Color colorId2Color(float[] colorId){
		int id = (int)colorId[0];
		switch (id) {
		case 0:
			return Color.red;
		case 1:
			return Color.green;
		case 2:
			return Color.blue;
		case 3:
			return Color.yellow;
		case 4:
			return Color.magenta;
		case 5:
			return Color.ORANGE;
		case 6:
			return Color.WHITE;
		case 7:
			return Color.BLACK;
		case 8:
			return Color.PINK;
		case 9:
			return Color.GRAY;
		case 10:
			return Color.LIGHT_GRAY;
		case 11:
			return Color.DARK_GRAY;
		case 12:
			return Color.CYAN;	
		case 13:
			return new Color(165,42,42);			
		default:
			return null;
		}
	}
	
	public static float senseDistance(){
		RMISampleProvider sampleProvider = getSingleDistanceSampleProvider();
		try {
			try {
				float[] sample = sampleProvider.fetchSample();
				return sample[0];
			} finally {
			    distanceSampleProvider.close();
			    distanceSampleProvider = null;
			}

		} catch (RemoteException e) {
			throw new UserException("Fail to sense the distance",e);
		}
	}	
	
	public static void closeAll(){
		try {
			colorSampleProvider.close();
			leftMotor.close();
			rightMotor.close();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
