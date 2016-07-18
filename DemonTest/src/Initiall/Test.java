package Initiall;
import java.util.ArrayList;

import com.spark.mesa_explorer.api.CommandExecutor;
import com.spark.mesa_explorer.api.RobotLocation;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class Test {
	/* @author Jingwen Wei
	 */
	public static float degreeCm = 26.8419581f;
	public static float degreeArm = 0.18f;
	public static float[] sensorData = {0 , 0 , 0};
	public static double[] locationGlobal = {0 , 0};
	public static double[] mapSize = {0, 63, 0, 38};
	public static ArrayList<String> spots = new ArrayList<String>();
	public static ArrayList<double[]> noGZ = new ArrayList<double[]>();
	/* Initial all the motors
	 * L_motor is left motor, R_motor is right motor, both motor are large regulated motor. 
	 * Arm is the arm of the robot the suggested angle of arm limit is ( -500 ~ 0 ), however, ( -700 ~ 0 ) is the mechanical limit.
	 */
	public static EV3LargeRegulatedMotor L_motor = new EV3LargeRegulatedMotor(MotorPort.B);
	public static EV3LargeRegulatedMotor R_motor = new EV3LargeRegulatedMotor(MotorPort.C);
	public static EV3MediumRegulatedMotor Arm = new EV3MediumRegulatedMotor(MotorPort.D);
	/* Initial all the sensors
	 * The color sensor will be set in color ID mode, which will automatically return the color number matched with real color.
	 * The ultrasonic sensor will working on distance detected mode
	 * The gyroscope sensor will be set in angle mode
	 */	
	public static EV3ColorSensor col_S = new EV3ColorSensor(SensorPort.S1);
	public static SampleProvider colID = col_S.getColorIDMode();
	public static EV3UltrasonicSensor Dis_S = new EV3UltrasonicSensor(SensorPort.S2);
	public static SampleProvider US_Sen = Dis_S.getDistanceMode();
	public static EV3GyroSensor towardFB = new EV3GyroSensor(SensorPort.S4);
	public static SampleProvider angleProvider = towardFB.getAngleMode();
	public static void main(String arg[]) throws Exception{
		new Thread(RobotServerComm.getSingleInstance()).start();
		CommandExecutor.startUp();
		towardFB.reset();
		Sound.beepSequenceUp();
		Button.waitForAnyPress(0);
		towardFB.reset();
		locationGlobal[0]=0;
		locationGlobal[1]=0;
		initialMap();
//		Button.waitForAnyPress(0);
		spots.clear();
		for(int i = 0 ; i < 5 ; i++){
			towardFB.reset();
			locationGlobal[0]=0;
			locationGlobal[1]=0;
			while(true)
			{
				scan();
			}
//			Button.waitForAnyPress(0);
		}
		Sound.beep();
		Button.waitForAnyPress(0);
		
	}
	/* 
	 * Scan for a line
	 */
	public static void lineScan(){
		int i = 0;
		do{
			scan();
		}while(i != 0);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		double wide = locationGlobal[1];
		angleProvider.fetchSample(sensorData, 1);
		if(wide>18){
			straightB(sensorData[1],Color.RED,Double.NaN,wide-5);
		}
		else straightF(sensorData[1],Color.WHITE,Double.NaN,wide+5);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		do{
			scan();
		}while(i != 0);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		l_turn(sensorData[1]-180);
		System.out.println(spots.toString());
	 }
	/* 
	 * Scan for color & obstacle
	 */
	public static void scan(){
		do{
			angleProvider.fetchSample(sensorData, 1);
			straightF(sensorData[1],Color.WHITE,Double.NaN,Double.NaN);
			colID.fetchSample(sensorData, 0);
			if(sensorData[0] == Color.BLACK) break;
			if(sensorData[0] != Color.WHITE){
				if(sensorData[0] == Color.BLACK) break;
				else if(sensorData[0] == Color.RED){
					ngzDectected();
				}
				else{
					colID.fetchSample(sensorData, 0);
					Button.LEDPattern(1);
					RobotLocation robotLocation = new RobotLocation(locationGlobal[0],locationGlobal[1],(double)sensorData[1]);
					robotLocation.setType(1);
					robotLocation.setColor((int)sensorData[0]);
					RobotServerComm.getSingleInstance().addRobotLocation(robotLocation);
					spots.add("\tX: "+locationGlobal[0]+";Y: "+locationGlobal[1]+";Col: "+sensorData[0]);
					angleProvider.fetchSample(sensorData, 1);
					straightF(sensorData[1], sensorData[0], Double.NaN,Double.NaN);
					Button.LEDPattern(0);
				}
			}
			if(sensorData[2] < 0.09){
				obstacle();
			}
			else continue;
		}while(true);
		switch(direction()){
			case 1:{
//				locationGlobal[0]=mapSize[1];
//				System.out.println("\t Correct coordinate to the X max");
				straightB(sensorData[1],Color.RED,locationGlobal[0]-5.2,Double.NaN);
				angleProvider.fetchSample(sensorData, 1);
				r_turn(sensorData[1]+90);
				double wide = locationGlobal[1];
				angleProvider.fetchSample(sensorData, 1);
				if(wide>18) straightB(sensorData[1],Color.RED,Double.NaN,wide-5);
				else straightF(sensorData[1],Color.WHITE,Double.NaN,wide+5);
				Sound.beep();
				angleProvider.fetchSample(sensorData, 1);
				r_turn(sensorData[1] + 90);
				break;
			}
			case 2:{
//				locationGlobal[1] = mapSize[3];
//				System.out.println("\t Correct coordinate to the Y max");
				straightB(sensorData[1],Color.RED,Double.NaN,locationGlobal[1]-5.2);
				angleProvider.fetchSample(sensorData, 1);
				l_turn(sensorData[1] - 90);				
				break;
			}
			case 3:{
//				locationGlobal[0] = mapSize[0];
//				System.out.println("\t Correct coordinate to the X min");
				straightB(sensorData[1],Color.RED,locationGlobal[0]+5.2,Double.NaN);
				Sound.beep();
				angleProvider.fetchSample(sensorData, 1);
				l_turn(sensorData[1]-180);
				break;
			}
			case 4:{
//				locationGlobal[1] = mapSize[2];
//				System.out.println("\t Correct coordinate to the Y max");
				straightB(sensorData[1],Color.RED,Double.NaN,locationGlobal[1]+5.2);
				angleProvider.fetchSample(sensorData, 1);
				r_turn(sensorData[1] + 90);
				break;
			}
			default : break;
		}
	}
	/* 
	 * Forward square loop
	 */
	public static void fSquare(){
		straightF(0,Color.WHITE, 5 , Double.NaN);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		straightF(sensorData[1],Color.WHITE,Double.NaN,5);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		straightF(sensorData[1],Color.WHITE, 0, Double.NaN);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		straightF(sensorData[1],Color.WHITE,Double.NaN,0);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		Button.waitForAnyPress(0);
		towardFB.reset();
	}
	/* 
	 * Backward square loop
	 */
	public static void bSquare(){
		straightB(0,Color.RED,-5,Double.NaN);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		straightB(sensorData[1],Color.RED,Double.NaN,-5);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		straightB(sensorData[1],Color.RED,0,Double.NaN);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		angleProvider.fetchSample(sensorData, 1);
		straightB(sensorData[1],Color.RED,Double.NaN,0);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1]+90);
		Sound.beep();
		Button.waitForAnyPress(0);	
		towardFB.reset();
	}
	
	/* 
	 * Scan the map and return all the possible coordinate 
	 */
	public static void initialMap(){
		angleProvider.fetchSample(sensorData, 1);
		alongLine(direction());
//		mapSize[1]=locationGlobal[0];
		angleProvider.fetchSample(sensorData, 1);
		straightB(sensorData[1],Color.RED,locationGlobal[0]-5.5,Double.NaN);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1] + 90);
		angleProvider.fetchSample(sensorData, 1);
		alongLine(direction());
//		mapSize[3]=locationGlobal[1];
		angleProvider.fetchSample(sensorData, 1);
		straightB(sensorData[1],Color.RED,Double.NaN,locationGlobal[1]-5.5);
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1] + 90);
		angleProvider.fetchSample(sensorData, 1);
		alongLine(direction());
		angleProvider.fetchSample(sensorData, 1);
		straightB(sensorData[1],Color.RED, locationGlobal[0]+5.5,Double.NaN);
//		mapSize[0]=locationGlobal[0];
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1] + 90);
		angleProvider.fetchSample(sensorData, 1);
		alongLine(direction());
//		angleProvider.fetchSample(sensorData, 1);
//		straightB(sensorData[1],Color.RED,Double.NaN,locationGlobal[1]+5.6);
//		mapSize[2]=locationGlobal[1];
		angleProvider.fetchSample(sensorData, 1);
		r_turn(sensorData[1] + 90);
	}
	/*
	 * Used to recognize the size that if robot go cross a line, it will identify the side of the line that it stands
	 */
	public static void recongniseSide(){
		L_motor.setSpeed(30);
		R_motor.setSpeed(30);
		L_motor.backward();
		R_motor.backward();
		Delay.msDelay(600);
		L_motor.stop(true);
		R_motor.stop(false);
		Arm.rotateTo(-500,true);
		do{
			colID.fetchSample(sensorData, 0);
		}while(sensorData[0]!=7);
		if(sensorData[0]==7)Arm.rotateTo(0);
	}
	/* 
	 * Make robot go straight forward, using the Gyroscope Sensor to do adjustment while forward.
	 * @param the currentAngle - float that robot shall follow, and the legalColor -float, targetLength - float stop distance
	 * @return distance (cm) - float
	 */
	public static void straightF(float currentAngle, float legalColor, double targetX, double targetY){
		do{
			US_Sen.fetchSample(sensorData, 2);
		}while(sensorData[2] < 0.105);
		int axis = 2, direction=0;
		double[] targetPt = {0,0};
		if(Double.isNaN(targetX)) {
			axis = 1;
			targetPt[axis]=targetY;
			if(locationGlobal[axis] < targetPt[axis]) direction = 1 ;
			else direction = 2;
		}
		if(Double.isNaN(targetY)){
			axis = 0;
			targetPt[axis]=targetX;
			if(locationGlobal[axis] < targetPt[axis]) direction = 1 ;
			else direction = 2;
		}
		if((Double.isNaN(targetX)) && (Double.isNaN(targetY))) axis=-1;
		L_motor.setSpeed(180);
		R_motor.setSpeed(180);
		L_motor.forward();
		R_motor.forward();
		do{
			coordinate(0);
			colID.fetchSample(sensorData, 0);
			if(sensorData[0] != legalColor)break;
			US_Sen.fetchSample(sensorData, 2);
			if(sensorData[2] < 0.09) break;
			angleProvider.fetchSample(sensorData, 1);
			if(sensorData[1]<currentAngle){
				L_motor.setSpeed(180);
				L_motor.forward();
				R_motor.setSpeed(120);
				R_motor.forward();
			}
			else if(sensorData[1]>currentAngle){
				R_motor.setSpeed(180);
				R_motor.forward();
				L_motor.setSpeed(120);
				L_motor.forward();
			}
			else{
				L_motor.setSpeed(180);
				L_motor.forward();
				R_motor.setSpeed(180);
				R_motor.forward();
			}
			if(axis!=-1){
				if(direction==1){
					//positive direction
					if(axis==2){
						if(locationGlobal[0] > targetPt[0])break;
						if(locationGlobal[1] > targetPt[1])break;
					}
					if(locationGlobal[axis] > targetPt[axis])break;
				}
				else{
					//negative direction
					if(axis==2){
						if(locationGlobal[0] < targetPt[0])break;
						if(locationGlobal[1] < targetPt[1])break;
					}
					if(locationGlobal[axis] < targetPt[axis])break;
				}
			}
		}while(true);
		halt();
		System.out.println("\tX: "+locationGlobal[0]+";Y="+locationGlobal[1]);
	}
	/* 
	 * Make robot go straight backward, using the Gyroscope Sensor to do adjustment while forward.
	 * @param the currentAngle - float that robot shall follow, and the targetColor -float, targetLength - float stop distance
	 * @return distance (cm) - float
	 */
	public static void straightB(float currentAngle, float targetColor, double targetX, double targetY){
		int axis = 2, direction=0;
		double[] targetPt = {0.0,0.0};
		if(Double.isNaN(targetX)) {
			axis = 1;
			targetPt[1]=targetY;
			if(locationGlobal[axis] < targetPt[axis]) direction = 1 ;
			else direction = 2;
		}
		if(Double.isNaN(targetY)){
			axis = 0;
			targetPt[axis]=targetX;
			if(locationGlobal[axis] < targetPt[axis]) direction = 1 ;
			else direction = 2;
		}
		if((Double.isNaN(targetX)) && (Double.isNaN(targetY))) axis=-1;
		L_motor.setSpeed(180);
		R_motor.setSpeed(180);
		L_motor.backward();
		R_motor.backward();
		do{
			coordinate(0);
			colID.fetchSample(sensorData, 0);
			if(sensorData[0] == targetColor)break;
			angleProvider.fetchSample(sensorData, 1);
			if(sensorData[1]>currentAngle){
				L_motor.setSpeed(180);
				L_motor.backward();
				R_motor.setSpeed(120);
				R_motor.backward();
			}
			else if(sensorData[1]<currentAngle){
				R_motor.setSpeed(180);
				R_motor.backward();
				L_motor.setSpeed(120);
				L_motor.backward();
			}
			else{
				L_motor.setSpeed(180);
				L_motor.backward();
				R_motor.setSpeed(180);
				R_motor.backward();
			}
			if(sensorData[0] == targetColor)break;
			if(axis!=-1){
				if(direction==2){
					//positive direction
					if(axis==2){
						if(locationGlobal[0] < targetPt[0])break;
						if(locationGlobal[1] < targetPt[1])break;
					}
					if(locationGlobal[axis] < targetPt[axis])break;
				}
				else{
					//negative direction
					if(axis==2){
						if(locationGlobal[0] > targetPt[0])break;
						if(locationGlobal[1] > targetPt[1])break;
					}
					if(locationGlobal[axis] > targetPt[axis])break;
				}
			}
		}while(true);
		halt();
		System.out.println("\tX: "+locationGlobal[0]+";Y="+locationGlobal[1]);
	}
	
	/* 
	 * Let robot go along the black boundary and stop at the end of the map
	 * @param bonN - integer the indicator that shows if 
	 * @return void
	 */
	public static void alongLine(int direction){
		float length = 0;
		if( direction == 0) length = 50;
		else length = 24;
		Button.LEDPattern(0);
		Arm.rotateTo(-250);
		while(true){
			blackWT();
			double curX = locationGlobal[0];
			double curY = locationGlobal[1];
			switch(direction){
			case 1:{
				do{
					Button.LEDPattern(4);
					angleProvider.fetchSample(sensorData, 1);
					straightF(sensorData[1],Color.BLACK,curX + length,Double.NaN);
					if(locationGlobal[0] < curX + length){
						halt();
						angleProvider.fetchSample(sensorData, 1);
						searchBon(sensorData[1]);
						blackWT();						
						if(direction==0){
							towardFB.reset();
						}
							
					}
					else break;
					}while(true);
					break;
				}
				case 2:{
					do{
						Button.LEDPattern(4);
						angleProvider.fetchSample(sensorData, 1);
						straightF(sensorData[1],Color.BLACK,Double.NaN,curY + length);
						if(locationGlobal[1] < curY + length){
							halt();
							angleProvider.fetchSample(sensorData, 1);
							searchBon(sensorData[1]);
							blackWT();
						}
						else break;
					}while(true);
					break;
				}
				case 3:{
					do{
						Button.LEDPattern(4);
						angleProvider.fetchSample(sensorData, 1);
						straightF(sensorData[1],Color.BLACK,curX - length,Double.NaN);
						if(locationGlobal[0] > curX - length){
							halt();
							angleProvider.fetchSample(sensorData, 1);
							searchBon(sensorData[1]);
							blackWT();
						}
						else break;
					}while(true);
					break;
				}
				case 4:{
					do{
						Button.LEDPattern(4);
						angleProvider.fetchSample(sensorData, 1);
						straightF(sensorData[1],Color.BLACK,Double.NaN,curY - length);
						if(locationGlobal[1] > curY - length){
							halt();
							angleProvider.fetchSample(sensorData, 1);
							searchBon(sensorData[1]);
							blackWT();
						}
						else break;
					}while(true);
					break;
				}
				default : break;
			}
			if(direction==0) towardFB.reset();
				halt();
				Arm.rotateTo(0);
				angleProvider.fetchSample(sensorData, 1);
				straightF(sensorData[1],Color.WHITE,Double.NaN,Double.NaN);
				angleProvider.fetchSample(sensorData, 1);
				Button.LEDPattern(1);
				break;
				
			}
		Button.LEDPattern(0);
	}
	/* 
	 * Make the robot wait for the right color
	 */
	
	private static void blackWT(){
		colID.fetchSample(sensorData, 0);
		angleProvider.fetchSample(sensorData, 1);
		if(sensorData[0] == Color.BLACK){
			Button.LEDPattern(1);
			int timer = 100000;
			while(timer!=0){
				do{
					colID.fetchSample(sensorData, 0);
				}while(sensorData[0] != Color.BLACK);
				if(sensorData[0] == Color.BLACK) timer--;
			}
		}
		else{
			colID.fetchSample(sensorData, 0);
		}
	}
	/* 
	 * Let robot turning parallel to bondary
	 * @param float formerA - float : the original Arm angle , targetColor - float 
	 * @return void
	 */
	public static void searchBon(float original){
		int flag=0;
		do{
			colID.fetchSample(sensorData, 0);
			if(sensorData[0] == Color.BLACK){
				Button.LEDPattern(1);
				break;
			}
			angleProvider.fetchSample(sensorData, 1);
			if(flag == 0) r_turn(sensorData[1] + 3);
			else l_turn(sensorData[1] - 3);
			colID.fetchSample(sensorData, 0);
			if(sensorData[0] == Color.BLACK){
				Button.LEDPattern(1);
				break;
			}
			angleProvider.fetchSample(sensorData, 1);
			if(sensorData[1] > original + 20) flag =1;
			else if(sensorData[1] < original - 20) {
				flag = 3;
				Button.LEDPattern(2);
				break;
			}
		}while(true);
		switch(flag){
		//fixing angle
		case 0:{
			angleProvider.fetchSample(sensorData, 1);
			r_turn(sensorData[1] + 3);
			break;
		}
		case 1:{
			angleProvider.fetchSample(sensorData, 1);
			l_turn(sensorData[1] - 3);
			break;
		}
		case 3:{
			Button.waitForAnyPress(0);
			Button.LEDPattern(0);
			break;
		}
		default :break;
		}
	}
	/* 
	 * Turn toward right side function
	 * @param original - float, targetAngle - float
	 * @return	void
	 */
	public static void r_turn(float targetAngle){
		L_motor.setSpeed(60);
		R_motor.setSpeed(60);
		L_motor.forward();
		R_motor.backward();
		do{
			angleProvider.fetchSample(sensorData, 1);
		}
		while(sensorData[1]!=targetAngle-1);
		halt();
		angleProvider.fetchSample(sensorData, 1);
		if(sensorData[1]>targetAngle+1){
			L_motor.setSpeed(20);
			R_motor.setSpeed(20);
			R_motor.forward();
			L_motor.backward();
			do{
				angleProvider.fetchSample(sensorData, 1);
			}
			while(sensorData[1]!=targetAngle);
		}
		halt();
		L_motor.resetTachoCount();
		R_motor.resetTachoCount();
		angleProvider.fetchSample(sensorData, 1);
		System.out.println(" angle: " + sensorData[1]);
	}
	/* 
	 * Turn toward left side function
	 * @param original - float, targetAngle - float
	 * @return	void
	 */
	public static void l_turn(float targetAngle){
		L_motor.setSpeed(60);
		R_motor.setSpeed(60);
		L_motor.backward();
		R_motor.forward();
		do{
			angleProvider.fetchSample(sensorData, 1);
		}
		while(sensorData[1]>targetAngle+1);
		halt();
		angleProvider.fetchSample(sensorData, 1);
		if(sensorData[1]<targetAngle-1){
			L_motor.setSpeed(20);
			R_motor.setSpeed(20);
			L_motor.forward();
			R_motor.backward();
			do{
				angleProvider.fetchSample(sensorData, 1);
			}
			while(sensorData[1]!=targetAngle);
		}
		halt();
		L_motor.resetTachoCount();
		R_motor.resetTachoCount();
		angleProvider.fetchSample(sensorData, 1);
		System.out.println(" angle: " + sensorData[1]);
	}
	/* 
	 * Proper define the direction of the robot in 4 sections, when the angle is out of range, adjust to initial angle
	 * @param none
	 * @return (Integer): 1: robot towards the positive X; 2: robot towards the positive Y; 3: robot towards negative X; 4: robot towards negative Y.
	 */
	public static int direction(){
		angleProvider.fetchSample(sensorData, 1);
		if(sensorData[1] < -45){
			r_turn(0);
			towardFB.reset();
			return 1;
		}
		if((sensorData[1] > -45)&&(sensorData[1] <= 45)) return 1;
		if((sensorData[1] > 45)&&(sensorData[1] <= 135)) return 2;
		if((sensorData[1] > 135)&&(sensorData[1] <= 225)) return 3;
		if((sensorData[1] > 225)&&(sensorData[1] <= 405)) return 4;
		else{
			l_turn(360);
			towardFB.reset();
			return 1;
		}
	}
	
	/* 
	 * Calculate the current coordinate of the robot and sent it to GUI
	 * @param 
	 * @return void
	 */
	public static void coordinate(int saveSig){
		angleProvider.fetchSample(sensorData, 1);
		double angle = Math.toRadians(sensorData[1]);
		double xDimention = 0;
		double yDimention = 0;
		float distance = L_motor.getPosition();
		distance = distance + R_motor.getPosition();
		R_motor.resetTachoCount();
		L_motor.resetTachoCount();
		distance = distance/2;
		distance = distance/degreeCm;
		if(distance >= 1.5) distance = 0;
		if(saveSig ==0){
			xDimention = locationGlobal[0] + distance * Math.cos(angle);
			yDimention = locationGlobal[1] + distance * Math.sin(angle);
			locationGlobal[0]=xDimention;
			locationGlobal[1]=yDimention;
		}
		if(saveSig == 1){
			if((sensorData[1]>-20)&&(sensorData[1]<20))sensorData[1]=-90;
			else if((sensorData[1]>70)&&(sensorData[1]<110))sensorData[1]=-180;
			else if((sensorData[1]>160)&&(sensorData[1]<200))sensorData[1]=-270;
		}
		RobotLocation robotLocation = new RobotLocation(xDimention,yDimention,(double)sensorData[1]);
		RobotServerComm.getSingleInstance().addRobotLocation(robotLocation);
	}
	/* 
	 * Stop robot immediately
	 * @param void
	 * @return void
	 */
	public static void halt(){
		L_motor.stop(true);
		R_motor.stop(false);
		while((R_motor.isMoving())||(L_motor.isMoving()));
		Arm.stop();
		while(Arm.isMoving());
	}
	/*
	 * Obstacle detected and the turn left
	 */
	public static int obstacle(){
		System.out.println("\tObstacle detected: "+sensorData[2]);
		Button.LEDPattern(2);
		switch(direction()){
			case 1:{
				straightB(sensorData[1],Color.RED,locationGlobal[0]-5.3,Double.NaN);
				break;
			}
			case 2:{
				straightB(sensorData[1],Color.RED,Double.NaN,locationGlobal[1]-5.3);
				break;
			}
			case 3:{
				straightB(sensorData[1],Color.RED,locationGlobal[0]+5.3,Double.NaN);
				break;
			}
			case 4:{
				straightB(sensorData[1],Color.RED,Double.NaN,locationGlobal[1]+5.3);
				break;
			}
			default : break;
		}
		angleProvider.fetchSample(sensorData, 1);
		l_turn(sensorData[1] - 90);
		Button.LEDPattern(0);
		return direction();
	}
	/* 
	 * Detected reaching NGZ
	 */
	public static int ngzDectected(){
		System.out.println("\tNo go zone detected at X: "+locationGlobal[0]+"\tY: "+locationGlobal[1]);
		noGZ.add(locationGlobal);
		Button.LEDPattern(3);
		switch(direction()){
			case 1:{
				straightB(sensorData[1],Color.BLACK,locationGlobal[0] - 5.3,Double.NaN);
				break;
			}
			case 2:{
				straightB(sensorData[1],Color.BLACK,Double.NaN,locationGlobal[1] - 5.3);
				break;
			}
			case 3:{
				straightB(sensorData[1],Color.BLACK,locationGlobal[0] + 5.3,Double.NaN);
				break;
			}
			case 4:{
				straightB(sensorData[1],Color.BLACK,Double.NaN,locationGlobal[1] + 5.3);
				break;
			}
			default : break;
		}
		angleProvider.fetchSample(sensorData, 1);
		l_turn(sensorData[1] - 90);
		Button.LEDPattern(0);
		return direction();
	}
}
