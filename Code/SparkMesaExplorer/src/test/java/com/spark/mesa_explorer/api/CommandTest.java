package com.spark.mesa_explorer.api;

import static org.junit.Assert.*;

import org.junit.Test;

public class CommandTest {

	@Test
	public void testGetParamCount() {
		//request command without params
		Command command = Command.createLocationRequest();
		assertEquals(0, command.getParamCount());
		//response command with 3 params
		RobotLocation robotLocation = new RobotLocation(10.1,10.2,90.00);
		Command locationResponse = Command.createLocationResponse(robotLocation);
		assertEquals(3, locationResponse.getParamCount());
	}

	@Test
	public void testToString() {
		//request command without params
		Command command = Command.createLocationRequest();
		assertEquals("commandType=REQUEST,name=location", command.toString());
		//response command with 3 params
		RobotLocation robotLocation = new RobotLocation(10.1,10.2,90.00);
		Command locationResponse = Command.createLocationResponse(robotLocation);
		assertEquals("commandType=RESPONSE,name=location,x=10.1,y=10.2,heading=90.0",locationResponse.toString());
				
	}

	@Test
	public void testParseCommand() {
		String str = "commandType=RESPONSE,name=location,x=10.1,y=10.2,heading=90.0";
		Command command = Command.parseCommand(str);
		assertEquals(CommandType.RESPONSE, command.getCommandType());;
		assertEquals("location",command.getName());
		assertEquals(3,command.getParamCount());
		assertEquals("10.1",command.getParam("x"));
		assertEquals("10.2",command.getParam("y"));
		assertEquals("90.0", command.getParam("heading"));
	}

	@Test
	public void testCreateLocationRequest() {
		Command command = Command.createLocationRequest();
		assertEquals(CommandType.REQUEST, command.getCommandType());
		assertEquals("location",command.getName());
		assertEquals("commandType=REQUEST,name=location", command.toString());
	}

}
