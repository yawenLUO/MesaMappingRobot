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

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.spark.mesa_explorer.control_centre.RobotAgent;
import com.spark.mesa_explorer.gui.component.CircularButton;
import com.spark.mesa_explorer.gui.component.MapPanel;
import com.spark.mesa_explorer.gui.component.MoveCommandPanel;

public class MainFrame extends JFrame {

	private static final long serialVersionUID = -5896366152699450852L;
	private Container contentPane;
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
		
		MapPanel mapPanel = new MapPanel();
		mapPanel.setPreferredSize(new Dimension(800, 565));
		contentPane.add(mapPanel, BorderLayout.CENTER);
		
		JPanel pnlBottom = new JPanel();
		
		contentPane.add(pnlBottom, BorderLayout.SOUTH);
		pnlBottom.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JPanel pnlMapButtons = new JPanel();
		pnlBottom.add(pnlMapButtons);
		
		JButton btnNgzSet = new JButton("NGZ set");
		btnNgzSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		pnlMapButtons.setLayout(new GridLayout(6, 1, 0, 0));
		pnlMapButtons.add(btnNgzSet);

		
		JButton btnNewButton = new JButton("Map load");
		pnlMapButtons.add(btnNewButton);
		
		JButton btnMapSave = new JButton("Map save");
		pnlMapButtons.add(btnMapSave);
		
		JButton btnReturnBase = new JButton("Return base");
		btnReturnBase.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnReturnBase.setHorizontalAlignment(SwingConstants.LEFT);
		pnlMapButtons.add(btnReturnBase);
				
		JButton btnExit = new JButton("Exit");
		pnlMapButtons.add(btnExit);
		btnExit.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				beforeExist(MainFrame.this);
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
				RobotAgent.stop();
			}
			
		});
		
		pnlBottom.add(circularButton);
		this.pack();
		this.setLocationRelativeTo(null);
		//this.setExtendedState(MAXIMIZED_BOTH);
		//setResizable(false);
	}

	private static void beforeExist(Component parent) {
		int dialogResult = JOptionPane.showConfirmDialog (parent, "Do you want to exit?","Warning",JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			//TODO stop the robot
			//disconnect with the robot
			//save the map
			System.exit(0);					
		}
	}

}
