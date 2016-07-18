package Initiall;

import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.robotics.navigation.DifferentialPilot;

public class LocalRobot {
	private boolean autoNavigation = true;
	private DifferentialPilot pilot = new DifferentialPilot(28f, 140f, Motor.B, Motor.C);
	public static void main(String arg[]) throws Exception {
		Sound.beepSequenceUp();
		//
		LocalRobot localRobot = new LocalRobot();
		localRobot.navigate();
		//if ()
	}
	public void navigate() {
		pilot.setTravelSpeed(15);
		pilot.travel(535);
	}
}
