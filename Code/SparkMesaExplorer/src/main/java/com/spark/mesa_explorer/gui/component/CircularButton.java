package com.spark.mesa_explorer.gui.component;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;




public class CircularButton extends JPanel {

	private static final long serialVersionUID = -4166631003538476164L;
		
	private BufferedImage image;
	
	private int margin = 15;
	
	/**
	 * Create the panel.
	 */
	public CircularButton() {
		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(40, 40));
		
		try {
			InputStream inputStream = CircularButton.class.getClassLoader().getResourceAsStream("images/stop_round_button.png");
			image = ImageIO.read(inputStream);
			inputStream.close();
		} catch (IOException e) {
			System.err.println(e);
		}		
	}

	@Override
	protected void paintComponent(Graphics g) {

		super.paintComponent(g);
		g.drawImage(image, margin , margin , getWidth() - 2 * margin, getHeight() - 2*margin, null, null);
	}
	
	

}
