/***********************************************************************
 * Module:  MapPanel.java
 * Author:  Yun
 * Purpose: Defines the Class MapPanel
 ***********************************************************************/

package com.spark.mesa_explorer.gui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import com.spark.mesa_explorer.robot.AgentRobot;
import com.spark.mesa_explorer.robot.LocationChangeListener;
import com.spark.mesa_explorer.robot.Robot;

/** @pdOid 027b8486-4960-4134-8332-fdba440fe471 */
public class MapPanel extends JPanel implements LocationChangeListener {

	private static final long serialVersionUID = -9192380842114946276L;
	
	private int radius = 30;
	
	Robot robot = new AgentRobot();

	private float oldX = -1;

	private float oldY = -1;
	
	public static final float MAP_WIDTH = 84.1F;
	public static final float MAP_HEIGHT = 59.4F;
	//The line thickness of the boundary
	public static final float BOUNDARY_THICK = 2F;
	public static final float MAP_TOP_MARGIN = 1.0f;
	public static final float  MAP_BOTTOM_MARGIN = 0.8f;
	public static final float  MAP_LEFT_MARGIN = 1.0f;
	public static final float  MAP_RIGHT_MARGIN = 0.9f;

	public MapPanel() {
		super();
		robot.addLocationChangeListener(this);
		//setLayout(null);
	}

	@Override
	protected void paintComponent(Graphics g){
		//super.paintComponents(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.scale(9.5125, 9.5125);
		//draw lines between deposits and base
//		drawLineDeposits2BaseStation(g2);
//		paintBaseStation(g2);
//		paintDeposits(g2);
		paintOrgin(g2);
		paintBoundary(g2);
		paintRobot(g2);
	}

	private void paintOrgin(Graphics2D g2) {
		final float RADIUS = 2.0F;
		
		float left = MAP_LEFT_MARGIN + BOUNDARY_THICK/2;
		float top = MAP_TOP_MARGIN + BOUNDARY_THICK/2;
		Ellipse2D.Float circle = new Ellipse2D.Float(left, top, 2*RADIUS, 2*RADIUS);
		g2.setStroke(new BasicStroke(0.1f));
		g2.draw(circle);
		g2.fill(circle);
	}

	private void drawLineDeposits2BaseStation(Graphics2D g2) {
		float baseStationCentreX = this.getWidth() / 2;
		float baseStationCentreY = getHeight() / 2;
		
		//TODO: refactor
		//draw line 2 deposit 1
		float deposit1CentreX = 260f + 15.0f;
		float deposit1CentreY = 100f + 15.0f;
		Shape line1 = new Line2D.Float(deposit1CentreX,deposit1CentreY,baseStationCentreX,baseStationCentreY);
		g2.draw(line1);
		
		float deposit2CentreX = 389f + 20.0f;
		float deposit2CentreY = 200f + 20.0f;
		Shape line2 = new Line2D.Float(deposit2CentreX,deposit2CentreY,baseStationCentreX,baseStationCentreY);
		g2.draw(line2);
		
		float deposit3CentreX = 460f + 25.0f;
		float deposit3CentreY = 360f + 25.0f;
		Shape line3 = new Line2D.Float(deposit3CentreX,deposit3CentreY,baseStationCentreX,baseStationCentreY);
		g2.draw(line3);
	}

	private void paintRobot(Graphics2D g2) {
		float x = robot.getXLocation();
		float y = robot.getYLocation();
		
		Color fillColor = Color.BLUE;
		float robotRadius = 1.0f;
		System.out.println("oldX = " + oldX + "oldY = " + oldY);
//		if (oldX != -1 && oldY != -1){
//			fillCircle(g2,oldX,oldY,robotRadius,getBackground());
//		}
		fillCircle(g2,x,y,robotRadius, fillColor);
	}

	public static float getInnerBottom() {
		return MAP_HEIGHT - MAP_BOTTOM_MARGIN - BOUNDARY_THICK / 2;
	}

	public static float getInnerRight() {
		return MAP_WIDTH - MAP_RIGHT_MARGIN - BOUNDARY_THICK / 2;
	}

	public static float getInnerTop() {
		return MAP_TOP_MARGIN + BOUNDARY_THICK / 2;
	}

	public static float getInnerLeft() {
		return MAP_LEFT_MARGIN + BOUNDARY_THICK / 2;
	}

	private void fillCircle(Graphics2D g2d, float x, float y, float radius, Color fillColor) {
		float width = 2 * radius;
		Ellipse2D circle = new Ellipse2D.Float(x, y, width, width);
		g2d.setPaint(fillColor);
		g2d.setStroke(new java.awt.BasicStroke(0.1f));
		g2d.draw(circle);
		g2d.fill(circle);
	}
	
	private void drawCircle(Graphics2D g2d, float x, float y, float radius, Color drawColor) {
		float width = 2 * radius;
		Ellipse2D circle = new Ellipse2D.Float(x, y, width, width);
		//set the thinkness of the boundary
		g2d.setStroke(new java.awt.BasicStroke(4.0f));
		g2d.draw(circle);
		g2d.setPaint(drawColor);
		g2d.draw(circle);
	}

	private void paintBoundary(Graphics2D g2) {
		//top left is an (margin,margin)
		g2.setStroke(new BasicStroke(BOUNDARY_THICK));
		g2.setPaint(Color.black);
		Rectangle2D rect = new Rectangle2D.Float(MAP_LEFT_MARGIN,MAP_TOP_MARGIN,MAP_WIDTH - MAP_LEFT_MARGIN - MAP_RIGHT_MARGIN, MAP_HEIGHT - MAP_TOP_MARGIN - MAP_BOTTOM_MARGIN);
		g2.draw(rect);	
	}

	private void paintDeposits(Graphics2D g2) {
		//
		Color[] colors = new Color[3];
		colors[0] = Color.yellow;
		colors[1] = new Color(240,240,0);
		colors[2] = new Color(220,220,0);
		float radii[] = {15.0f,20.0f,25.0f};
		//System.out.println("color:" + colors[0]);
		drawCircle(g2, 260, 100, radii[0], colors[0]);
		fillCircle(g2, 260, 100, radii[0], getBackground());		
		drawCircle(g2, 389, 200,radii[1], colors[1]);
		fillCircle(g2, 389, 200,radii[1], getBackground());
		drawCircle(g2, 460, 360,radii[2], colors[2]);
		fillCircle(g2, 460, 360,radii[2], getBackground());
		g2.setPaint(Color.BLACK);
	}

	private void paintBaseStation(Graphics2D g2) {
		float x = this.getWidth() / 2 - radius;
		float y = getHeight() / 2 - radius;
		float width = 2 * radius;
		Ellipse2D circle = new Ellipse2D.Float(x, y, width, width);
		//set the thinkness of the boundary
		g2.setStroke(new java.awt.BasicStroke(4.0f));
		g2.draw(circle);
		Paint redtowhite = new GradientPaint(x,y,Color.RED,x+width, y+width,Color.WHITE);
		g2.setPaint(redtowhite);
		g2.fill(circle);
		
		//draw a string in the center of the circle
		String baseStr = "Base";
		FontMetrics metrics = g2.getFontMetrics();
		int strHeight = metrics.getHeight();
		int strWidth = metrics.stringWidth(baseStr);
		float strX = x + radius - strWidth / 2;
		float strY = y + radius + strHeight / 2 - metrics.getDescent();
		
		Paint black = Color.black;
		g2.setPaint(black);
		g2.setStroke(new BasicStroke(10.0f));
		g2.drawString(baseStr, strX, strY);
	}

	public void onLocationChanged(float newX, float newY, float oldX, float oldY) {
		this.oldX = oldX;
		this.oldY = oldY;
		repaint();
	}	
}