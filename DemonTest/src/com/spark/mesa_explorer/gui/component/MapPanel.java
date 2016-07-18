/***********************************************************************
 * Module:  MapPanel.java
 * Author:  Yun
 * Purpose: Defines the Class MapPanel
 ***********************************************************************/

package com.spark.mesa_explorer.gui.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.spark.mesa_explorer.api.UserException;
import com.spark.mesa_explorer.robot.AgentRobot;
import com.spark.mesa_explorer.robot.LocationChangeListener;
import com.spark.mesa_explorer.robot.Robot;
import com.spark.mesa_explorer.util.XmlUtil;

/** @pdOid 027b8486-4960-4134-8332-fdba440fe471 */
public class MapPanel extends JPanel implements LocationChangeListener {

	private static final long serialVersionUID = -9192380842114946276L;
	private List<Circle> deposits = new ArrayList<Circle>();
	private Circle baseStation = new Circle();
	private final int BASE_STATION_RADIUS = 3;

	private RobotStatus robotStatus = new RobotStatus();
	
	//explored zone
	private ArrayList<Point2D.Float> exploredPoints = new ArrayList<Point2D.Float>();


	float x;
	float y;
	
	Rectangle2D.Float nogoZone = null;
	
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
		this.setBackground(Color.DARK_GRAY);
		AgentRobot.getSingleInstance().addLocationChangeListener(this);
		//setLayout(null);
		//used for test
		nogoZone = new Rectangle2D.Float();
		nogoZone.x = 23.3f;
		nogoZone.y = 24.5f;
		nogoZone.width = 10;
		nogoZone.height = 10;
		
		//base station
		baseStation.setRadius(BASE_STATION_RADIUS);
		baseStation.setX(getBaseStationX());
		baseStation.setY(getBaseStationY());
		
		//test for deposits
//		Circle circle = new Circle();
//		circle.setDrawColor(Color.YELLOW);
//		circle.setX(25);
//		circle.setY(35);
//		circle.setRadius(3);
//		deposits.add(circle);
		
		Robot robot = AgentRobot.getSingleInstance();
		robotStatus.setHeading(robot.getHeading());
		robotStatus.setX(robot.getXLocation());
		robotStatus.setY(robot.getYLocation());
		
		//test for explore points
//		exploredPoints.add(new Point2D.Float(10, 20));
//		exploredPoints.add(new Point2D.Float(30, 40));
	}

	@Override
	protected void paintComponent(Graphics g){
		//super.paintComponents(g);
		Graphics2D g2 = (Graphics2D)g;
		g2.scale(9.5125, 9.5125);
		//draw lines between deposits and base
		drawLineDeposits2BaseStation(g2);
		paintBaseStation(g2);
		paintDeposits(g2);
		paintOrgin(g2);
		paintBoundary(g2);
		paintNogoZon(g2);
		paintExploredZone(g2);
		paintRobot(g2);
	}

	private void paintNogoZon(Graphics2D g2) {
		Color oldColor = g2.getColor();
		Stroke oldStroke = g2.getStroke();
	    try {
	    	g2.setStroke(new BasicStroke(1.0f));
	    	g2.setColor(Color.RED);
		    g2.draw(nogoZone);
		} finally {
			g2.setColor(oldColor);
			g2.setStroke(oldStroke);
		}
	}

	private void paintExploredZone(Graphics2D g2) {
		Color oldColor = g2.getColor();
		g2.setColor(Color.WHITE);
		try {
			for (Point2D.Float point : exploredPoints) {
				Ellipse2D.Float circle = new Ellipse2D.Float(point.x, point.y, 2, 2);
				g2.fill(circle);
			}			
		} finally {
			g2.setColor(oldColor);
		}

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

	@SuppressWarnings("unused")
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
		x = AgentRobot.getSingleInstance().getXLocation();
		y = AgentRobot.getSingleInstance().getYLocation();
		
		Color fillColor = Color.BLUE;
		float robotRadius = 1.0f;
		System.out.println("oldX = " + oldX + "oldY = " + oldY);
		if (oldX != -1 && oldY != -1){
			fillCircle(g2,oldX,oldY,robotRadius,g2.getBackground());
		}
		fillCircle(g2,x,y,robotRadius, fillColor);
	}

	public static float getInnerBottom() {
		return MAP_HEIGHT - MAP_BOTTOM_MARGIN - BOUNDARY_THICK / 2;
	}

	public static float getInnerRight() {
		return MAP_WIDTH - MAP_RIGHT_MARGIN - BOUNDARY_THICK / 2;
	}

	public static float getInnerTop() {
		return MAP_TOP_MARGIN - BOUNDARY_THICK / 2;
	}

	public static float getInnerLeft() {
		return MAP_LEFT_MARGIN - BOUNDARY_THICK / 2;
	}

	private void fillCircle(Graphics2D g2d, float x, float y, float radius, Color fillColor) {
		float width = 2 * radius;
		Ellipse2D circle = new Ellipse2D.Float(x, y, width, width);
		g2d.setColor(fillColor);
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

	@SuppressWarnings("unused")
	private void paintDeposits(Graphics2D g2) {
		for ( Circle deposit : deposits) {
			drawCircle(g2, deposit.getX(), deposit.getY(), deposit.getRadius(), deposit.getDrawColor());
			fillCircle(g2, deposit.getX(), deposit.getY(), deposit.getRadius(), deposit.getDrawColor());
		}
		g2.setPaint(Color.BLACK);
	}

	@SuppressWarnings("unused")
	private void paintBaseStation(Graphics2D g2) {
		System.out.println("baseStation: " + baseStation);
		if (baseStation != null){
			float x = baseStation.getX();
			float y = baseStation.getY();
			float width = 2 * baseStation.getRadius();
			Ellipse2D circle = new Ellipse2D.Float(x, y, width, width);
			//set the thinkness of the boundary
			g2.setStroke(new java.awt.BasicStroke(0.1f));
			g2.draw(circle);
			Paint redtowhite = new GradientPaint(x,y,Color.RED,x+width, y+width,Color.WHITE);
			g2.setPaint(redtowhite);
			g2.fill(circle);
			
//			//draw a string in the center of the circle
//			String baseStr = "Base";
//			FontMetrics metrics = g2.getFontMetrics();
//			int strHeight = metrics.getHeight();
//			int strWidth = metrics.stringWidth(baseStr);
//			float strX = x + baseStation.getRadius() - strWidth / 2;
//			float strY = y + baseStation.getRadius() + strHeight / 2 - metrics.getDescent();
//			
//			Paint black = Color.black;
//			g2.setPaint(black);
//			g2.setStroke(new BasicStroke(10.0f));
//			g2.drawString(baseStr, strX, strY);
		}

	}

	private float getBaseStationY() {
		return MAP_HEIGHT / 2 - BASE_STATION_RADIUS;
	}

	private float getBaseStationX() {
		return MAP_WIDTH / 2 - BASE_STATION_RADIUS;
	}

	public void onLocationChanged(float newX, float newY, float oldX, float oldY) {
		this.oldX = oldX;
		this.oldY = oldY;
		AgentRobot robot = (AgentRobot)AgentRobot.getSingleInstance();
		if (robot.getType() == 1){
			//deposit
			if (robot.getColor() == lejos.robotics.Color.BLUE){
				createBlueDeposit(newX,newY);
			}else if(robot.getColor() == lejos.robotics.Color.GREEN){
				createGreenDeposit(newX,newY);
			}else{
				createYellowDeposit(newX,newY);
			}
			System.out.println(robot);
		}
		repaint();
	}

	private void createYellowDeposit(float x, float y) {
		Circle circle = new Circle();
		circle.setX(x);
		circle.setY(y);
		circle.setDrawColor(Color.YELLOW);
		deposits.add(circle);		
	}

	private void createGreenDeposit(float x, float y) {
		Circle circle = new Circle();
		circle.setX(x);
		circle.setY(y);
		circle.setDrawColor(Color.GREEN);
		deposits.add(circle);
	}

	private void createBlueDeposit(float x, float y) {
		Circle circle = new Circle();
		circle.setX(x);
		circle.setY(y);
		circle.setDrawColor(Color.BLUE);
		deposits.add(circle);
	}

	public void saveMap(File file) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Element rootElement = document.createElement("mesamap");
			document.appendChild(rootElement);
			
			
			rootElement.setAttribute("units", "cm");
			
			//create attributes of the map
			createMapAttribute(document,rootElement);
			
			//create boundary children
			createBoundaryNode(document,rootElement);
						
			//robot-status
			Element robotStatus = document.createElement("robot-status");
			rootElement.appendChild(robotStatus);
			createAttribute(document,robotStatus,"heading","0");
			Element point = createChildElement(document,robotStatus,"point");			
			point.setAttribute("x", Float.toString(x));
			point.setAttribute("y", Float.toString(y));
			
			//base-station elements
			createBaseStationNode(document,rootElement);
			
			createBaseStationNode(exploredPoints,document,rootElement);
			
			//nogo zone
			createNogoZoneNode(nogoZone,document,rootElement);
			
			//deposit

			createDepositNode(document,rootElement);
			
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(file);
			transformer.transform(source,result);
//			Element attribute = ;
		} catch (ParserConfigurationException | TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
		
	}

	private void createDepositNode(Document document, Element rootElement) {
		/*
		 * <deposits>
			<deposit radius="30.0" x="370.0" y="252.0" color="0000FF"/>
			</deposits>
		 * **/	
		Element depositsElement = createChildElement(document, rootElement, "deposits");
		for (Circle circle : deposits) {
			Element depositElement = createChildElement(document, depositsElement, "deposit");
			depositElement.setAttribute("radius", Float.toString(circle.getRadius()));
			depositElement.setAttribute("x", Float.toString(circle.getX()));
			depositElement.setAttribute("y", Float.toString(circle.getY()));
			depositElement.setAttribute("color", Integer.toHexString(circle.getDrawColor().getRGB()));
		}
	}

	private void createNogoZoneNode(java.awt.geom.Rectangle2D.Float aNogoZone, Document document, Element rootElement) {
		/**
		  <nogo>
		      <x></x>
			  <y></y>
			  <height></height>
			  <width></width>
		  </nogo>
		 * 
		 * */
		if (aNogoZone != null){
			Element nogo = createChildElement(document, rootElement, "nogo");
			Element x = createChildElement(document, nogo, "x");
			x.setTextContent(Double.toString(nogoZone.getX()));
			Element y = createChildElement(document, nogo, "y");
			y.setTextContent(Double.toString(nogoZone.getY()));
			Element height = createChildElement(document, nogo, "height");
			height.setTextContent(Double.toString(nogoZone.getHeight()));
			Element width = createChildElement(document, nogo, "width");
			width.setTextContent(Double.toString(nogoZone.getWidth()));
		}
	}

	private void createBaseStationNode(ArrayList<java.awt.geom.Point2D.Float> points, Document document,
			Element rootElement) {
		Element zone = createChildElement(document, rootElement, "zone");
		zone.setAttribute("state", "explored");
		Element area = createChildElement(document, zone, "area");
		for (Point2D.Float point : points) {
			Element pointElement = createChildElement(document, area, "point");
			pointElement.setAttribute("x", Float.toString(point.x));
			pointElement.setAttribute("y", Float.toString(point.y));
		}
		
	}

	private void createBaseStationNode(Document document, Element rootElement) {
		Element baseStation = createChildElement(document, rootElement, "base-station");
		Element circle = createChildElement(document,baseStation,"circle");
		circle.setAttribute("x", Float.toString(getBaseStationX()));
		circle.setAttribute("y", Float.toString(getBaseStationY()));
		circle.setAttribute("radius",Float.toString(this.baseStation.getRadius()));
	}

	private void createBoundaryNode(Document document, Element rootElement) {
		Element boundary = createChildElement(document, rootElement, "boundary");
		Element area = createChildElement(document, boundary, "area");
		//top left point
		Element topLeft = createChildElement(document, area, "point");
		topLeft.setAttribute("x","0");
		topLeft.setAttribute("y", "0");
		
		//top right point
		Element topRight = createChildElement(document, area, "point");
		topRight.setAttribute("x", Float.toString(MAP_WIDTH));
		topRight.setAttribute("y", "0");
		
		//bottom left point
		Element bottomLeft = createChildElement(document, area, "point");
		bottomLeft.setAttribute("x", "0");
		bottomLeft.setAttribute("y", Float.toString(MAP_HEIGHT));
		
		//bottom right point
		Element bottomRight = createChildElement(document, area, "point");
		bottomRight.setAttribute("x", Float.toString(MAP_WIDTH));
		bottomRight.setAttribute("y", Float.toString(MAP_HEIGHT));
	}

	private void createMapAttribute(Document document, Element root) {
		//
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		createAttribute(document, root, "Survey Date", simpleDateFormat.format(new Date()));
		createAttribute(document, root, "Robot Model", "Lego Mindstorm EV3");
	}

	private Element createChildElement(Document document, Element parent, String name) {
		Element element = document.createElement(name);
		parent.appendChild(element);
		return element;
	}

	private void createAttribute(Document document, Element parent, String name, String value) {
		Element attribute = document.createElement("attribute");
		parent.appendChild(attribute);
		Element key = document.createElement("key");
		attribute.appendChild(key);
		key.setTextContent(name);
		Element valElment = document.createElement("value");
		attribute.appendChild(valElment);
		valElment.setTextContent(value);
	}

	public void loadMap(File file) {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			Document doc = documentBuilder.parse(file);
			Element root = doc.getDocumentElement();
						
			NodeList childNodes = root.getChildNodes();
			//scan children of root nodes
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node node = childNodes.item(i);
				if (node.getNodeType() == Node.ELEMENT_NODE){
					Element element = (Element)node;
					if (node.getNodeName().trim().equals("robot-status")){
						parseRobotStatus(element);
					}else if (node.getNodeName().trim().equals("base-station")){
						parseBaseStation(element);
					}else if (node.getNodeName().trim().equals("zone")){
						parseExploredZone(element);
					}else if (node.getNodeName().trim().equals("nogo")){
						parseNogoZone(element);
					}else if(node.getNodeName().trim().equals("deposits")){
						parseDeposits(element);
					}
					
					
				}
			}
						
		} catch (SAXException e) {
			throw new UserException("Fail to parse file: " + file.getAbsolutePath() , e);
		} catch (IOException e) {
			throw new UserException("Fail to open file: " + file.getAbsolutePath() , e);
		} catch (ParserConfigurationException e) {
			throw new UserException("Fail to init DocumentBuilder for the file: " + file.getAbsolutePath() , e);
		}
		
	}

	private void parseDeposits(Element parent) {
	/*
	   <deposits>
	     <deposit color="ffffff00" radius="40.0" x="80.0" y="100.0"/>
	   </deposits>
	 * */
		deposits.clear();
		
		NodeList nodelist = parent.getElementsByTagName("deposit");
		for (int i = 0; i < nodelist.getLength(); i++) {
			Node depositElement = nodelist.item(i);
			Circle deposit = new Circle();
			deposits.add(deposit);
			deposit.setX(XmlUtil.getAttrValueAsFloat(depositElement, "x"));
			deposit.setY(XmlUtil.getAttrValueAsFloat(depositElement, "y"));
			deposit.setRadius(XmlUtil.getAttrValueAsFloat(depositElement, "radius"));
			String color = XmlUtil.getAttrValueAsString(depositElement, "color");
			int temp = (int)Long.parseLong(color, 16);
			deposit.setDrawColor(new Color(temp));
		}
		System.out.println("deposits: " + deposits);		
	}

	private void parseNogoZone(Element parent) {
		nogoZone.x = XmlUtil.getElementAsFloat(parent,"x");
		nogoZone.y = XmlUtil.getElementAsFloat(parent,"y");
		nogoZone.height = XmlUtil.getElementAsFloat(parent, "height");
		nogoZone.width = XmlUtil.getElementAsFloat(parent, "width");
		System.out.println(nogoZone);
	}

	/*
	 * <area>
		<point x="1.0" y="2.0"/>
		<point x="3.0" y="4.0"/>
		</area>
	 * */
	private void parseExploredZone(Element element) {
		Element area = (Element)element.getElementsByTagName("area").item(0);
		
		NodeList nodeList = area.getElementsByTagName("point");
		exploredPoints.clear();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node pointElement = nodeList.item(i);
			Point2D.Float point2D = new Point2D.Float();
			exploredPoints.add(point2D);
			float x = XmlUtil.getAttrValueAsFloat(pointElement, "x");
			float y = XmlUtil.getAttrValueAsFloat(pointElement, "y");
			point2D.setLocation(x, y);
		}
		System.out.println("exploredPoints: " + exploredPoints);
	}

	private void parseBaseStation(Element element) {
    /*
     * <base-station>
		<circle radius="30.0" x="370.0" y="252.0"/>
		</base-station>
     * */
		Node circle = element.getElementsByTagName("circle").item(0);
		String radius = circle.getAttributes().getNamedItem("radius").getNodeValue().trim();
		baseStation = new Circle();
		baseStation.setRadius(Float.parseFloat(radius));
		float x = XmlUtil.getAttrValueAsFloat(circle,"x");
		float y = XmlUtil.getAttrValueAsFloat(circle, "y");
		baseStation.setX(x);
		baseStation.setY(y);
		System.out.println(baseStation);
	}

	private void parseRobotStatus(Element element) {
		Element attribute = (Element)element.getElementsByTagName("attribute").item(0);
		Node value = attribute.getElementsByTagName("value").item(0);
		float heading = Float.parseFloat((value.getTextContent().trim()));
		robotStatus.setHeading(heading);
		Node point = element.getElementsByTagName("point").item(0);
		String x = point.getAttributes().getNamedItem("x").getNodeValue().trim();
		robotStatus.setX(Float.parseFloat(x));
		String y = point.getAttributes().getNamedItem("y").getNodeValue().trim();
		robotStatus.setY(Float.parseFloat(y));
		System.out.println(robotStatus);
	}	
}