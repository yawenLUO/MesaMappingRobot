package org.lejos.ev3.sample.helloworld;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.Sound;
import lejos.remote.ev3.RemoteEV3;

public class RemoteTest {

	public static void main(String[] args) {
		RemoteEV3 ev3;
		try {
			ev3 = new RemoteEV3("10.0.1.1");
			ev3.setDefault();
			System.out.println("hello");
			Sound.beep();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
