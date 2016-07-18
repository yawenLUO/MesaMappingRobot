package com.spark.mesa_explorer.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.spark.mesa_explorer.api.UserException;
import com.spark.mesa_explorer.gui.component.CircularButton;
import com.spark.mesa_explorer.gui.component.MapPanel;
import com.spark.mesa_explorer.gui.component.MoveCommandPanel;
import com.spark.mesa_explorer.robot.AgentRobot;
import com.spark.mesa_explorer.robot.Robot;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -5896366152699450852L;
	private Container contentPane;
	
	//for open and save the map
	private JFileChooser chooser = new JFileChooser();
	
	private MapPanel mapPanel = new MapPanel();
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					final MainFrame frame = new MainFrame();
					frame.addWindowListener(new WindowAdapter(){

						@Override
						public void windowClosing(WindowEvent windowEvent) {
							
							beforeExist(frame);
							
						}
						
					});
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainFrame() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		contentPane = this.getContentPane();
		
		mapPanel.setPreferredSize(new Dimension(800, 565));
		contentPane.add(mapPanel, BorderLayout.CENTER);
		
		JPanel pnlBottom = new JPanel();
		
		contentPane.add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel pnlMapButtons = new JPanel();
		pnlBottom.add(pnlMapButtons);
		
		final JButton btnConnect = new JButton("Connect");
		pnlMapButtons.add(btnConnect);
		btnConnect.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				//beforeExist(MainFrame.this);
				Robot robot = AgentRobot.getSingleInstance();
				
				try {
					robot.setConnected(!robot.isConnected());
				} catch (UserException e1) {
					UserException.handeException(MainFrame.this,e1);
				}
				//when connected shows disconnect
				String label = robot.isConnected() ? "Disconnect" : "Connect";
				btnConnect.setText(label);
			}
		});
		
		final JButton btnMode = new JButton("Auto");
		btnMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//switch between the auto and manual mode
				Robot robot = AgentRobot.getSingleInstance();
				robot.setAutoExploration(!robot.isAutoExploration());
				//if in auto mode, display manual, otherwise, show auto
				String label = robot.isAutoExploration()?"Manual":"Auto";
				btnMode.setText(label);
			}
		});
		pnlMapButtons.setLayout(new GridLayout(6, 1, 0, 0));
		pnlMapButtons.add(btnMode);

		
		JButton btnMapLoad = new JButton("Map load");
		pnlMapButtons.add(btnMapLoad);
		btnMapLoad.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (chooser.showOpenDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
					File file = chooser.getSelectedFile();
					mapPanel.loadMap(file);
					mapPanel.repaint();
				}
			}
		});
		
		
		JButton btnMapSave = new JButton("Map save");
		pnlMapButtons.add(btnMapSave);
		
		btnMapSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//save the map
				if (chooser.showSaveDialog(MainFrame.this) == JFileChooser.APPROVE_OPTION){
					File file = chooser.getSelectedFile();
					mapPanel.saveMap(file);
					System.out.println("file url: " + file.getAbsolutePath());
				}
				
			}
		});
							
		MoveCommandPanel moveCommandPanel = new MoveCommandPanel();
		pnlBottom.add(moveCommandPanel);
		
		CircularButton circularButton = new CircularButton();
		circularButton.setPreferredSize(new Dimension(160, 160));
		circularButton.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);
				//RobotAgent.stop();
				AgentRobot.getSingleInstance().stop();
			}
			
		});
		
		pnlBottom.add(circularButton);
		this.pack();
		this.setLocationRelativeTo(null);
	}

	private static void beforeExist(Component parent) {
		int dialogResult = JOptionPane.showConfirmDialog (parent, "Do you want to exit?","Warning",JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			//save the map
			System.exit(0);					
		}
	}

}
