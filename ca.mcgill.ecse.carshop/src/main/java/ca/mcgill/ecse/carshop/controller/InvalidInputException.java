package ca.mcgill.ecse.carshop.controller;

public class InvalidInputException extends Exception{

private static final long serialVersionUID = -5633915762703837868L;
	
	public InvalidInputException(String errorMessage) {
		super(errorMessage);
	}
	
}
