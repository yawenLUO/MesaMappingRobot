package com.spark.mesa_explorer.gui.component;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.spark.mesa_explorer.control_centre.RobotAgent;
import com.spark.mesa_explorer.robot.AgentRobot;

public class MoveCommandPanel extends JPanel {
//	public static EV3LargeRegulatedMotor L_motor = new EV3LargeRegulatedMotor(MotorPort.B);
//	public static EV3LargeRegulatedMotor R_motor = new EV3LargeRegulatedMotor(MotorPort.C);
		/**
	 * Create the panel.
	 */
	public MoveCommandPanel() {
		setLayout(new GridLayout(3, 3, 0, 0));
		JPanel panels[][] = new JPanel[3][3];
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				panels[i][j] = new JPanel();
				this.add(panels[i][j]);
			}
		}

		JButton btnForward = new JButton("Forward");
		panels[0][1].add(btnForward);
		btnForward.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				AgentRobot.getSingleInstance().moveForward();
			}
		});

		JButton btnLeftTurn = new JButton("Left");
		btnLeftTurn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				AgentRobot.getSingleInstance().turnLeft();
			}
		});
		panels[1][0].add(btnLeftTurn);

		JButton btnRightTurn = new JButton("Right");
		panels[1][2].add(btnRightTurn);
		btnRightTurn.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				AgentRobot.getSingleInstance().turnRight();
			}
		});

		JButton btnBackward = new JButton("Backward");
		panels[2][1].add(btnBackward);
		btnBackward.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AgentRobot.getSingleInstance().moveBackward();
			}
		});
	}

}
