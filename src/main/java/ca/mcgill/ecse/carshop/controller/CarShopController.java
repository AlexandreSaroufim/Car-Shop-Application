package ca.mcgill.ecse.carshop.controller;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.model.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CarShopController {

	
	
	/**
	 * @author simobenkirane
	 * @param carshop1
	 * @param username
	 * @param servicename
	 * @param duration
	 * @param techtype
	 * @throws Exception
	 */
	public static void addService(CarShop carshop1, String username, String servicename, String duration, String techtype) throws Exception {
		
		//CarShop carshop = CarShopApplication.getCarShop();
		CarShop carshop = carshop1;
		//User currentuser = CarShopApplication.getCurrentUser();
		User currentuser = User.getWithUsername(username);
		
		if(servicename.isEmpty() || servicename == null) {
			throw new Exception("servicename can not be null or empty");
		}
		
		//throw exception if user != to owner
				//how can i see who's logged in?? getUser??
		if(!username.equals("owner") && (currentuser instanceof Owner) != true) {
								
			throw new Exception("You are not authorized to perform this operation");
		}
		
		if(Integer.parseInt(duration)<1) {
			throw new Exception("Duration must be positive ");
		}
		
		//throw exception if service already exists
		if(serviceExists(carshop, servicename) == true) {
			throw new Exception("Service " + servicename + " already exists");
		}
		
		if(Service.getWithName(servicename) != null) {
			throw new Exception("Service " + servicename + " already exists");
		}
		
		
		
		
		Garage garage = getGarage(carshop, techtype);
		
		if(garage == null) {
			throw new Exception("Garage of technician type "+ techtype + " does not exist");
		}
		
		
		try {
		Service newServ = new Service(servicename,carshop, Integer.parseInt(duration), garage);
	
		carshop.addBookableService(newServ);
	
		garage.addService(newServ);
			
		
		}
		catch (RuntimeException e){
			String error = e.getMessage();
			throw new Exception(error);
			
		}
		
	}
	
	/**
	 * @author simobenkirane
	 * @param carshop1
	 * @param username
	 * @param servicename
	 * @param updatedServicename
	 * @param duration
	 * @param techtype
	 * @throws Exception
	 */
	public static void updateService(CarShop carshop1, String username, String servicename,String updatedServicename, String duration, String techtype) throws Exception {
		
		
		CarShop carshop = carshop1;
		User currentuser = User.getWithUsername(username);
		Garage garage = getGarage(carshop, techtype);
		
		if(servicename.isEmpty() || servicename == null) {
			throw new Exception("servicename can not be null or empty");
		}
		
		//throw exception if user != to owner
		//how can i see who's logged in?? getUser??
		if(!username.equals("owner") && (currentuser instanceof Owner) != true) {
								
			throw new Exception("You are not authorized to perform this operation");
		}
		
		if(Integer.parseInt(duration)<1) {
			throw new Exception("Duration must be positive ");
		}
		

		Service sToUpdate = getService(carshop,servicename);
		
		if(sToUpdate == null) {
			throw new Exception("Service with name " + servicename + " was not found");
		}
		
		if(getService(carshop, updatedServicename) != null && (getService(carshop, updatedServicename).getGarage()).equals(garage) != true) {
			throw new Exception("Service " + updatedServicename + " already exists");
		}
		
	
		if(garage == null) {
			throw new Exception("Garage of technician type "+ techtype + " does not exist");
		}
		
		try {
						
			sToUpdate.setName(updatedServicename);
			sToUpdate.setDuration(Integer.parseInt(duration));
			sToUpdate.setGarage(garage);

		}
		catch (RuntimeException e) {
			String error = e.getMessage();
			throw new Exception(error);
		}
		
		
	}
	
	/**
	 * @author simobenkirane
	 * @param carshop
	 * @param techtype
	 * @return
	 */
	private static Garage getGarage(CarShop carshop, String techtype) {
		
		List<Technician> technicians = carshop.getTechnicians();
		
		for(Iterator<Technician> i = technicians.iterator(); i.hasNext();) {
					
					Technician t = i.next();
					if(t.getType().toString().equals(techtype)) {
						return t.getGarage();
					}
				}
		
		
		
		return null;
	}
	
	
	/**
	 * @author simobenkirane
	 * @param carshop
	 * @param servicename
	 * @return
	 */
	private static Service getService(CarShop carshop, String servicename) {
		
		
			
			List<BookableService> bookableServices = carshop.getBookableServices();
			
			for(Iterator<BookableService> i = bookableServices.iterator(); i.hasNext();) {
				
				BookableService b = i.next();
				if(b.getName().equals(servicename) && (b instanceof Service)) { 
					return (Service)b;
				}
			}
			
			return null;
			
		}
	
	
	
	/* author Simo Benkirane */
	//may run into isssues with servicecombo if they have the same name
	//checks if service by name servicename exists in a given carShop by iterating through bookable services
	private static boolean serviceExists(CarShop carshop, String servicename) {
		
		List<BookableService> bookableServices = carshop.getBookableServices();
		
		for(Iterator<BookableService> i = bookableServices.iterator(); i.hasNext();) {
			
			BookableService b = i.next();
			if(b.getName().equals(servicename)) { 
				return true;
			}
		}
		
		return false;
		
	}
	
	
		
	

}
