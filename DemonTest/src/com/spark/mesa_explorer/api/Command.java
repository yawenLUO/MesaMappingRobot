package com.spark.mesa_explorer.api;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The command request or command response
 * @author Yun
 *
 */
public class Command {

	private CommandType commandType = CommandType.REQUEST;
	
	private String name;
	
	private int statusCode = 0;
	
	private Map<String,String> params = new LinkedHashMap<String,String>();
	
	public Command(String name) {
		super();
		this.name = name;
	}
	
	public Command(String name, CommandType commandType) {
		this.name = name;
		this.commandType = commandType;
	}

	public int getParamCount(){
		return params.size();
	}
	
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("commandType=").append(commandType)
		.append(",name=").append(name);
		for (String currentKey : params.keySet()) {
			String value = params.get(currentKey);
			sb.append(",").append(currentKey).append("=").append(value);
		}
		return sb.toString();
	}
	
	public static Command parseCommand(String str){
		if (str == null || str.trim().length() == 0){
			throw new UserException("command str cannot be null or empty");
		}
		
		String components[] = str.split(",");
		Map<String,String> values = new LinkedHashMap<String,String>();
		for (String component : components) {
			String[] tokens = component.split("=");
			values.put(tokens[0].trim(), tokens[1].trim());
		}
		
		
		if (!values.containsKey("name")){
			throw new UserException("The command string does not contain name");
		}
		
		if (!values.containsKey("commandType")){
			throw new UserException("The command string does not contain command type");
		}
		
		//Strings in values do not have surrounding whitespace
		Command command = new Command(values.get("name"));
		if (!values.get("commandType").equals(CommandType.REQUEST)){
			command.setCommandType(CommandType.RESPONSE);
		}
		
		//remove the values whose are not params
		values.remove("name");
		values.remove("commandType");
		
		//iterate over other values to add params
		for (String key : values.keySet()) {
			command.addParam(key, values.get(key));
		}
		
		return command;
	}

	public final static String LOCATION = "location";
	
	public final static String MOVE_FOWARD = "MOVE_FOWARD";
	public final static String MOVE_BACKWORD = "MOVE_BACKWORD";
	public final static String TURN_LEFT = "TURN_LEFT"; 
	public final static String TURN_RIGHT = "TURN_RIGHT";
	public final static String STOP = "STOP"; 
	
	public static Command createRequestCommand(String name){
		Command command = new Command(name);
		return command;
	}
	
	public static Command createLocationRequest(){
		Command command = new Command(LOCATION);
		return command;
	}

	public static Command createLocationResponse(RobotLocation robotLocation) {
		Command command = new Command(LOCATION,CommandType.RESPONSE);	
		command.addParam("x",robotLocation.getX());
		command.addParam("y",robotLocation.getY());
		command.addParam("heading",robotLocation.getHeading());
		command.addParam("type", robotLocation.getType());
		command.addParam("color", robotLocation.getColor());
		return command;
	}

	private void addParam(String name, Object value) {
		params.put(name, value.toString());
	}
	
	public String getParam(String paramName) {
		return params.get(paramName);
	}

	public CommandType getCommandType() {
		return commandType;
	}
	
	private void setCommandType(CommandType commandType) {
		this.commandType = commandType;
	}

	public String getName() {
		return name;
	}
	
	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public static Command createResponse(Command request) {
		Command response = new Command(request.getName());
		response.setStatusCode(0);
		response.setCommandType(CommandType.RESPONSE);
		return response;
	}
}
