package ca.mcgill.ecse.carshop.controller;

import java.sql.Time;
import java.util.*;

import java.sql.Time;
import java.sql.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.model.Appointment;
import ca.mcgill.ecse.carshop.model.BookableService;
import ca.mcgill.ecse.carshop.model.Business;
import ca.mcgill.ecse.carshop.model.BusinessHour;
import ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.carshop.model.CarShop;
import ca.mcgill.ecse.carshop.model.ComboItem;
import ca.mcgill.ecse.carshop.model.Customer;
import ca.mcgill.ecse.carshop.model.Garage;
import ca.mcgill.ecse.carshop.model.Owner;
import ca.mcgill.ecse.carshop.model.Service;
import ca.mcgill.ecse.carshop.model.ServiceBooking;
import ca.mcgill.ecse.carshop.model.ServiceCombo;
import ca.mcgill.ecse.carshop.model.Technician;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.model.TimeSlot;
import ca.mcgill.ecse.carshop.model.User;
import ca.mcgill.ecse.carshop.persistence.CarshopPersistence;

public class CarShopController {

//	public static void main(String[] args) throws Exception {
//		CarShopApplication.setCurrentUser(CarShopApplication.getCarShop().getOwner());
//		
//		for ( int k =0 ; k<CarShopApplication.getCarShop().getAppointments().size();k++) {
//			System.out.println("appointment : "+k);
//			System.out.println(CarShopApplication.getCarShop().getAppointments().get(k).getServiceBooking(0).getTimeSlot().getStartDate().toString());
//		}
//		System.out.println("appointments in the system: "  + CarShopApplication.getCarShop().getAppointments().toString());
//		System.out.println("business hours are : "+CarShopApplication.getCarShop().getHours().toString());
//	
////		AddBusinessHours(BusinessHour.DayOfWeek.valueOf("Tuesday"), StringToTime("8:00"), StringToTime("20:00"));
////		AddBusinessHours(BusinessHour.DayOfWeek.valueOf("Wednesday"), StringToTime("8:00"), StringToTime("20:00"));
//		CarShopApplication.setSystemDate("2021-04-17+9:30");
//		makeAppointment("aly", "2021-04-21", "tire-check", "transmission-check", "18:00,19:00");
//		
//		//	AddBusinessHours(BusinessHour.DayOfWeek.valueOf("Monday"), StringToTime("8:00"), StringToTime("17:00"));
//		
//
//		
//	}

	/**
	 * 
	 * 
	 * Part SignUp and Update account
	 * 
	 * @author Alexandre Saroufim
	 * 
	 * 
	 * 
	 */

	/**
	 * Signs Up a Customer
	 * 
	 * @author Alexandre Saroufim
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 */
	// Check again I think I should add more
	public static void signUpCustomer(String username, String password) throws InvalidInputException {
		boolean done = false;
		CarShop carshop = CarShopApplication.getCarShop();
		String error = "";
		// Cases where SignUp won't work
		if (username.length() == 0 && password.length() == 0) {
			error = "The user name and the password cannot be empty";
		} else if (username.length() == 0) {
			error = "The user name cannot be empty";
		} else if (password.length() == 0) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}

		if (CarShopApplication.getCurrentUser() != null) {
			if (carshop.getOwner() != null && CarShopApplication.getCurrentUser() == carshop.getOwner()) {
				error = "You must log out of the owner account before creating a customer account";
				throw new InvalidInputException(error.trim());
			}

			if (carshop.getTechnicians().size() > 0
					&& findTechnician(CarShopApplication.getCurrentUser().getUsername()) != null) {
				error = "You must log out of the technician account before creating a customer account";
				throw new InvalidInputException(error.trim());
			}
		}

		if (findCustomer(username) != null) {
			error = "The username already exists";
			throw new InvalidInputException(error.trim());
		}
		try {

			// If it passes all cases signup the customer
			Customer c = carshop.addCustomer(username, password);
			CarshopPersistence.save(carshop);
			done = true;
			if (done)
				System.out.println("succesfuly done");

		} catch (RuntimeException e) {

			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Finds a specific Customer given his username
	 * 
	 * @author Alexandre Saroufim
	 * @param username
	 * @return
	 */
	private static Customer findCustomer(String username) {
		Customer foundCustomer = null;
		// loop through customers
		for (Customer customer : CarShopApplication.getCarShop().getCustomers()) {
			if (customer.getUsername().equals(username)) {
				foundCustomer = customer;
				break;
			}
		}

		return foundCustomer;

	}

	/**
	 * Deletes a specific customer given his username
	 * 
	 * @author Alexandre Saroufim
	 * @param username
	 */
	public static void deleteCustomer(String username, String currentuser) throws InvalidInputException {
		try {
			if (username.equals(CarShopApplication.getCurrentUser().getUsername())
					|| currentuser.equals(CarShopApplication.getCarShop().getOwner().getUsername())) {
				// try deleting customer
				CarShop carshop = CarShopApplication.getCarShop();
				Customer customer = findCustomer(username);
				if (customer != null) {
					carshop.removeCustomer(customer);
					customer.delete();
					CarshopPersistence.save(carshop);
				}
			}
		}
		// if fails
		catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * Update the customer's account
	 * 
	 * @author Alexandre Saroufim
	 * @param oldUsername
	 * @param newUsername
	 * @param newPassword
	 * @throws InvalidInputException
	 */
	private static void updateCustomer(String oldUsername, String newUsername, String newPassword)
			throws InvalidInputException {
		Customer customer = findCustomer(oldUsername);
		String error = "";
		// cases where updateCustomer won't work
		if (findCustomer(newUsername) != null) {
			error = "Username not available";
			// throw new InvalidInputException(error.trim());
		}
		if (newUsername.length() == 0 && newPassword.length() == 0) {
			error = "The user name and the password cannot be empty";
		} else if (newUsername.length() == 0) {
			error = "The user name cannot be empty";
		} else if (newPassword.length() == 0) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		// else update customer
		customer.setUsername(newUsername);
		customer.setPassword(newPassword);

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * Update the Owner's account
	 * 
	 * @author Alexandre Saroufim
	 * @param oldUsername
	 * @param newUsername
	 * @param newPassword
	 * @throws InvalidInputException
	 */
	private static void updateOwner(String oldUsername, String newUsername, String newPassword)
			throws InvalidInputException {
		CarShop carshop = CarShopApplication.getCarShop();
		String error = "";
		// cases where update owner won't work
		if (!oldUsername.equals(newUsername)) {
			error = "Changing username of owner is not allowed";
			throw new InvalidInputException(error.trim());
		}
		if (newUsername.length() == 0 && newPassword.length() == 0) {
			error = "The user name and the password cannot be empty";
		} else if (newUsername.length() == 0) {
			error = "The user name cannot be empty";
		} else if (newPassword.length() == 0) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}
		// if all pass
		// update owner
		carshop.getOwner().setPassword(newPassword);

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Finds a specific Technician
	 * 
	 * @author Alexandre Saroufim
	 * @param username
	 * @return
	 */
	private static Technician findTechnician(String username) {

		Technician foundTechnician = null;
		// loop through technicians
		for (Technician technician : CarShopApplication.getCarShop().getTechnicians()) {
			if (technician.getUsername().equals(username)) {
				foundTechnician = technician;
				break;
			}
		}

		return foundTechnician;

	}

	/**
	 * Updates a technician 's account
	 * 
	 * @author Alexandre Saroufim
	 * @param oldUsername
	 * @param newUsername
	 * @param newPassword
	 * @throws InvalidInputException
	 */
	private static void updateTechnician(String oldUsername, String newUsername, String newPassword)
			throws InvalidInputException {
		String error = "";
		// cases where update Technician won't work
		if (oldUsername != newUsername) {
			error = "Changing username of technician is not allowed";
			throw new InvalidInputException(error.trim());
		}
		if (newUsername.length() == 0 && newPassword.length() == 0) {
			error = "The user name and the password cannot be empty";
		} else if (newUsername.length() == 0) {
			error = "The user name cannot be empty";
		} else if (newPassword.length() == 0) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}

		// else update technician
		Technician technician = findTechnician(oldUsername);
		technician.setPassword(newPassword);

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * Update a user's Account
	 * 
	 * @author Alexandre Saroufim
	 * @param oldUsername
	 * @param newUsername
	 * @param newPassword
	 * @throws InvalidInputException
	 */
	public static void updateUser(String oldUsername, String newUsername, String newPassword)
			throws InvalidInputException {
		// main method that calls all the other helper methods
		try {
			// checks user corresponds to which type of user
			if (oldUsername.equals("owner")) {
				updateOwner(oldUsername, newUsername, newPassword);
			}
			if (findCustomer(oldUsername) != null) {
				updateCustomer(oldUsername, newUsername, newPassword);
			}
			if (findTechnician(oldUsername) != null) {
				updateTechnician(oldUsername, newUsername, newPassword);
			}
			CarshopPersistence.save(CarShopApplication.getCarShop());
		}
		// if does not work throw an exception
		catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	public static List<Appointment> getApp() {
		return CarShopApplication.getCarShop().getAppointments();
	}

	public static String getStartDateApp(Appointment a) {
		return a.getServiceBooking(0).getTimeSlot().getStartDate().toString();

	}

	public static String getAppName(Appointment a) {
		return a.getBookableService().getName();
	}

	public static String getAppStartTime(Appointment a) {
		return a.getStartTime().toString();
	}

	/**
	 * 
	 * 
	 * 
	 * PART : Login and Garage time
	 * 
	 * @author Abdel Aziz Zayed
	 * 
	 * 
	 * 
	 */

	/**
	 * Attempt to login with username and password
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param username
	 * @param password
	 * @return true if login successful, false otherwise
	 */
	private static boolean login(String username, String password) {
		if (User.hasWithUsername(username)) {
			User user = User.getWithUsername(username);
			if (user.getPassword().equals(password)) {
				CarShopApplication.setCurrentUser(user);
				return true;
			}
		}
		CarShopApplication.setCurrentUser(null);
		return false;
	}

	/**
	 * Login with username and password or create account if it does not exist
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param username
	 * @param password
	 */
	public static void userLogin(String username, String password) throws InvalidInputException {
		CarShop carShop = CarShopApplication.getCarShop();
		if (!login(username, password)) {
			User login = null;
			if (username.equals("owner") && password.equals("owner")) {
				login = new Owner(username, password, carShop);
			} else if (username.equals("Tire-Technician"))
				login = new Technician(username, password, TechnicianType.Tire, carShop);
			else if (username.equals("Engine-Technician"))
				login = new Technician(username, password, TechnicianType.Engine, carShop);
			else if (username.equals("Transmission-Technician"))
				login = new Technician(username, password, TechnicianType.Transmission, carShop);
			else if (username.equals("Electronics-Technician"))
				login = new Technician(username, password, TechnicianType.Electronics, carShop);
			else if (username.equals("Fluids-Technician"))
				login = new Technician(username, password, TechnicianType.Fluids, carShop);
			else {
				throw new InvalidInputException("Username/password not found");
			}

			if (login instanceof Technician) {
				Technician tech = (Technician) login;
				Garage garage = new Garage(carShop, tech);

				if (carShop.hasBusiness()) {
					for (BusinessHour hour : carShop.getBusiness().getBusinessHours())
						garage.addBusinessHour(hour);
				}
			}
			CarShopApplication.setCurrentUser(login);
		}
	}

	/**
	 * Add garage hours for a specific technician
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param day   - day of the week available
	 * @param start - time at which the technician is available
	 * @param end   - time at which the technician is no longer available
	 * @param tech  - technician
	 * @throws InvalidInputException
	 */
	public static void addGarageHours(DayOfWeek day, Time start, Time end, Technician tech)
			throws InvalidInputException {

		if (!authorizedTechnician(tech))
			throw new InvalidInputException("You are not authorized to perform this operation");

		if (end.before(start) || end.equals(start))
			throw new InvalidInputException("Start time must be before end time");

		if (!authorizedHours(day, start, end))
			throw new InvalidInputException("The opening hours are not within the opening hours of the business");

		if (overlappingHours(tech.getGarage(), day, start, end))
			throw new InvalidInputException("The opening hours cannot overlap");

		CarShop carShop = CarShopApplication.getCarShop();
		tech.getGarage().addBusinessHour(new BusinessHour(day, start, end, carShop));

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * remove garage hours for a specific technician
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param day   - day of the week to remoev from
	 * @param start - start time at which the technician is not longer available
	 * @param end   - end time at which the technician is no longer available
	 * @param tech  - technician
	 * @throws InvalidInputException
	 */
	public static void removeGarageHours(DayOfWeek day, Time start, Time end, Technician tech)
			throws InvalidInputException {

		if (!authorizedTechnician(tech))
			throw new InvalidInputException("You are not authorized to perform this operation");

		BusinessHour hourToRemove = null;
		for (BusinessHour hour : tech.getGarage().getBusinessHours()) {
			if (hour.getDayOfWeek().equals(day) && hour.getStartTime().equals(start) && hour.getEndTime().equals(end)) {
				hourToRemove = hour;
				break;
			}
		}

		if (hourToRemove != null) {
			tech.getGarage().removeBusinessHour(hourToRemove);
			try {
				CarshopPersistence.save(CarShopApplication.getCarShop());
			} catch (RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}

	}

	/**
	 * check if the given technician is authorized to do any changes, as in he is
	 * the technician logged in
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param tech
	 * @return true if authorized to make changes
	 */
	private static boolean authorizedTechnician(Technician tech) {
		return CarShopApplication.getCurrentUser() instanceof Technician
				&& ((Technician) CarShopApplication.getCurrentUser()).getType() == tech.getType();
	}

	/**
	 * Check if the given day, start time and end time are within business hours
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param day
	 * @param start
	 * @param end
	 * @return true if the times are within business hours, false otherwise
	 */
	private static boolean authorizedHours(DayOfWeek day, Time start, Time end) {
		CarShop carShop = CarShopApplication.getCarShop();
		Business business = carShop.getBusiness();

		for (BusinessHour hour : business.getBusinessHours())
			if (hour.getDayOfWeek().equals(day)
					&& (hour.getStartTime().before(start) || hour.getStartTime().equals(start))
					&& (hour.getEndTime().after(end) || hour.getEndTime().equals(end)))
				return true;

		return false;
	}

	/**
	 * Check if the given times do not already overlap other times for a specific
	 * garage
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param garage
	 * @param day
	 * @param start
	 * @param end
	 * @return true if overlapping, false otherwise
	 */
	private static boolean overlappingHours(Garage garage, DayOfWeek day, Time start, Time end) {
		for (BusinessHour hour : garage.getBusinessHours())
			if (hour.getDayOfWeek().equals(day)
					&& (hour.getStartTime().before(start) || hour.getStartTime().equals(start))
					&& (hour.getEndTime().after(end) || hour.getEndTime().equals(end)))
				return true;

		return false;
	}

	/**
	 * PART : Service
	 * 
	 * @author simobenkirane
	 */

	/**
	 * @author simobenkirane
	 * @param carshop1
	 * @param username
	 * @param servicename
	 * @param duration
	 * @param techtype
	 * @throws Exception
	 */
	public static void addService(CarShop carshop1, String username, String servicename, String duration,
			String techtype) throws Exception {

		// CarShop carshop = CarShopApplication.getCarShop();
		CarShop carshop = carshop1;
		// User currentuser = CarShopApplication.getCurrentUser();
		User currentuser = User.getWithUsername(username);

		if (servicename.isEmpty() || servicename == null) {
			throw new Exception("servicename can not be null or empty");
		}

		// throw exception if user != to owner
		// how can i see who's logged in?? getUser??
		if (!username.equals("owner") && (currentuser instanceof Owner) != true) {

			throw new Exception("You are not authorized to perform this operation");
		}

		if (Integer.parseInt(duration) < 1) {
			throw new Exception("Duration must be positive ");
		}

		// throw exception if service already exists
		if (serviceExists(carshop, servicename) == true) {
			throw new Exception("Service " + servicename + " already exists");
		}

		if (Service.getWithName(servicename) != null) {
			throw new Exception("Service " + servicename + " already exists");
		}

		Garage garage = getGarage(carshop, techtype);

		if (garage == null) {
			throw new Exception("Garage of technician type " + techtype + " does not exist");
		}

		try {
			Service newServ = new Service(servicename, carshop, Integer.parseInt(duration), garage);

			carshop.addBookableService(newServ);

			garage.addService(newServ);
			CarshopPersistence.save(carshop);

		} catch (RuntimeException e) {
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
	public static void updateService(CarShop carshop1, String username, String servicename, String updatedServicename,
			String duration, String techtype) throws Exception {

		CarShop carshop = carshop1;
		User currentuser = User.getWithUsername(username);
		Garage garage = getGarage(carshop, techtype);

		if (servicename.isEmpty() || servicename == null) {
			throw new Exception("servicename can not be null or empty");
		}

		// throw exception if user != to owner
		// how can i see who's logged in?? getUser??
		if (!username.equals("owner") && (currentuser instanceof Owner) != true) {

			throw new Exception("You are not authorized to perform this operation");
		}

		if (Integer.parseInt(duration) < 1) {
			throw new Exception("Duration must be positive ");
		}

		Service sToUpdate = getService(carshop, servicename);

		if (sToUpdate == null) {
			throw new Exception("Service with name " + servicename + " was not found");
		}

		if (getService(carshop, updatedServicename) != null
				&& (getService(carshop, updatedServicename).getGarage()).equals(garage) != true) {
			throw new Exception("Service " + updatedServicename + " already exists");
		}

		if (garage == null) {
			throw new Exception("Garage of technician type " + techtype + " does not exist");
		}

		try {

			sToUpdate.setName(updatedServicename);
			sToUpdate.setDuration(Integer.parseInt(duration));
			sToUpdate.setGarage(garage);
			CarshopPersistence.save(carshop);

		} catch (RuntimeException e) {
			String error = e.getMessage();
			throw new Exception(error);
		}

	}

	public static List<TOService> getAllTOServices() {
		List<TOService> serviceTO = new LinkedList<TOService>();

		List<Service> services = getAllService(CarShopApplication.getCarShop());

		for (Service s : services) {
			TOService sTO = new TOService(s.getName(), s.getDuration(), s.getGarage().getTechnician().toString());
			sTO.setName(s.getName());
			sTO.setDuration(s.getDuration());
			sTO.setTechtype(s.getGarage().getTechnician().toString());

			serviceTO.add(sTO);
		}

		return serviceTO;
	}

	/**
	 * @author simobenkirane
	 * @param carshop
	 * @param techtype
	 * @return
	 */
	private static Garage getGarage(CarShop carshop, String techtype) {

		List<Technician> technicians = carshop.getTechnicians();

		for (Iterator<Technician> i = technicians.iterator(); i.hasNext();) {

			Technician t = i.next();
			if (t.getType().toString().equals(techtype)) {
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

		for (Iterator<BookableService> i = bookableServices.iterator(); i.hasNext();) {

			BookableService b = i.next();
			if (b.getName().equals(servicename) && (b instanceof Service)) {
				return (Service) b;
			}
		}

		return null;

	}

	/* author Simo Benkirane */
	// may run into isssues with servicecombo if they have the same name
	// checks if service by name servicename exists in a given carShop by iterating
	// through bookable services
	private static boolean serviceExists(CarShop carshop, String servicename) {

		List<BookableService> bookableServices = carshop.getBookableServices();

		for (Iterator<BookableService> i = bookableServices.iterator(); i.hasNext();) {

			BookableService b = i.next();
			if (b.getName().equals(servicename)) {
				return true;
			}
		}

		return false;

	}

	/**
	 * END PART : Service
	 */

	/**
	 * 
	 * 
	 * 
	 * PART : Service Combo
	 * 
	 * @author Aly Mohamed
	 * 
	 * 
	 * 
	 */

	/**
	 * @author Aly Mohamed
	 * @param Username
	 * @param list
	 * @param MainService
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static void DefineServiceCombo(CarShop carshop, String username, String name, String mainService,
			String services, String mandatory) throws Exception {
		// list has all services
		// m has all mandatory setting
		String[] list = services.split(",");
		String[] m = mandatory.split(",");

		// The current user is not the owner
		if (!carshop.getOwner().getUsername().equals(username))
			throw new Exception("You are not authorized to perform this operation");

		// the services are less than the minimum number of services in a service combo
		if (list.length <= 1)
			throw new Exception("A service Combo must contain at least 2 services");

		// The service combo with name name already exists
		if (isACombo(carshop, name))
			throw new Exception("Service combo " + name + " already exists");

		// the mainservice is not a service
		if (!isAService(carshop, mainService))
			throw new Exception("Service " + mainService + " does not exist");

		// checks if the main is included in the services that were entered
		boolean included = false;

		for (int i = 0; i < list.length; i++) {
			if (list[i].equals(mainService)) {
				if (!Boolean.parseBoolean(m[i]))
					throw new Exception("Main service must be mandatory");
				included = true;
			}
		}
		if (!included)
			throw new Exception("Main service must be included in the services");

		// checks if all input services exist in the system
		for (int i = 0; i < list.length; i++) {
			if (!isAService(carshop, list[i]))
				throw new Exception("Service " + list[i] + " does not exist");
		}

		// all inputs are correct

		// we create a new servicecombo
		ServiceCombo sc = new ServiceCombo(name, carshop);
		sc.setName(name);

		// add mainservice to it
		ComboItem c = new ComboItem(true, getServiceByName(carshop, mainService), sc);
		sc.addService(c); // is it already added?
		sc.setMainService(c);

		// add the other services
		for (int i = 1; i < list.length; i++) {
			if (!list[i].equals(mainService)) {
				sc.addService(new ComboItem(Boolean.parseBoolean(m[i]), getServiceByName(carshop, list[i]), sc));
			}
		}

		try {
			CarshopPersistence.save(carshop);
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	/**
	 * @author Aly Mohamed
	 * @param username
	 * @param oldName
	 * @param newName
	 * @param mainService
	 * @param Services
	 * @param ismandatory
	 * @return
	 * @throws Exception
	 */
	public static void updateServiceCombo(CarShop carshop, String username, String oldname, String newname,
			String mainservice, String allservices, String ismandatory) throws Exception {
		String[] services = allservices.split(",");
		String[] mand = ismandatory.split(",");

		// The current user is not the owner
		if (!carshop.getOwner().getUsername().equals(username))
			throw new Exception("You are not authorized to perform this operation");

		// the services are less than the minimum number of services in a service combo
		if (services.length <= 1)
			throw new Exception("A service Combo must contain at least 2 services");

		// the combo with name oldname couln't be found
		if (!isACombo(carshop, oldname))
			throw new Exception("Service combo " + oldname + " does not exist");

		// the mainservice is not a service
		if (!isAService(carshop, mainservice))
			throw new Exception("Service " + mainservice + " does not exist");

		// yhe service combo with name newname already exists
		if (isACombo(carshop, newname) && (newname.equals(oldname) == false))
			throw new Exception("Service " + newname + " already exists");

		// checks if the main is included and is mandatory in the services that were
		// entered
		boolean included = false;

		for (int i = 0; i < services.length; i++) {
			if (services[i].equals(mainservice)) {
				if (!Boolean.parseBoolean(mand[i]))
					throw new Exception("Main service must be mandatory");
				included = true;
			}
		}

		if (!included)
			throw new Exception("Main service must be included in the services");

		// 1 of the input services is not a real service
		for (int i = 0; i < services.length; i++) {
			if (!isAService(carshop, services[i]))
				throw new Exception("Service " + services[i] + " does not exist");
		}

		// all the inputs are correct

		// get the wanted combo
		ServiceCombo sc = getComboByName(carshop, oldname);
		sc.setName(newname);

		// if the mainservice isn't already in the combo
		if (!isinCombo(carshop, mainservice, newname)) {
			// create a new combo item in the combo and set it to main
			ComboItem mains = new ComboItem(true, getServiceByName(carshop, mainservice), sc);
			sc.setMainService(mains);
		} else {// get the service from the combo and set it to mandatory then set to main
			getserviceFromACombo(carshop, newname, mainservice).setMandatory(true);
			sc.setMainService(getserviceFromACombo(carshop, newname, mainservice));

		}

		// iterate on the input services
		// if the service is already in the combo, change its mandatory to the mandatory
		// setting in the input
		// else create a new combo item with the input information
		for (int i = 0; i < services.length; i++) {
			if (!services[i].equals(mainservice)) {
				if (!isinCombo(carshop, services[i], newname)) {
					sc.addService(
							new ComboItem(Boolean.parseBoolean(mand[i]), getServiceByName(carshop, services[i]), sc));
				} else {
					getserviceFromACombo(carshop, newname, services[i]).setMandatory(Boolean.parseBoolean(mand[i]));
				}
			}
		}

		// iterate on the comb we created
		// delete the services that were not in the input services
		for (int i = 0; i < sc.getServices().size(); i++) {
			if (!isinArray(sc.getServices().get(i).getService().getName(), services))
				sc.getServices().get(i).delete();
		}
		try {
			CarshopPersistence.save(carshop);
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * Author Aly Mohamed
	 * 
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public static boolean deleteServiceCombo(CarShop carshop, String comboname, String username) throws Exception {
		if (carshop.getOwner().getUsername().equals(username)) {

			if (isACombo(carshop, comboname)) {

				ServiceCombo c = getComboByName(carshop, comboname);
				if (!c.hasAppointments()) {

					carshop.removeBookableService(c);
					c.delete();
					return true;
				} else {
					throw new Exception("The Sevice Combo cannot be deleted, there are existing appointments");
				}
			} else
				throw new Exception("There is no service combo with that name");
		} else
			throw new Exception("You are not authorized to perform this operation");
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////// Helper Methods
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @param service
	 * @param combo
	 * @return
	 */
	private static boolean isinCombo(CarShop carshop, String service, String combo) {
		// checks if the input service is in the input combo
		ServiceCombo c = getComboByName(carshop, combo);
		for (int i = 0; i < c.getServices().size(); i++) {
			if (c.getService(i).getService().getName().equals(service)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @author Aly Mohamed
	 * @param service
	 * @param services
	 * @return
	 */
	private static boolean isinArray(String service, String[] services) {
		// checks if a service is in an array of services
		for (String s : services) {
			if (s.equals(service))
				return true;
		}
		return false;
	}

	/**
	 * returns one comboItem out of a Combo
	 * 
	 * @author Aly Mohamed
	 * @param carshop
	 * @param combo
	 * @param service
	 * @return
	 */
	private static ComboItem getserviceFromACombo(CarShop carshop, String combo, String service) {
		// gets a service from a serviceCombo
		ServiceCombo c = getComboByName(carshop, combo);
		for (int i = 0; i < c.getServices().size(); i++) {
			if (c.getService(i).getService().getName().equals(service)) {
				return c.getService(i);
			}
		}
		return null;
	}

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @return
	 */
	private static ArrayList<ServiceCombo> getAllCombo(CarShop carshop) {
		// gets all service combos in the system
		ArrayList<ServiceCombo> combos = new ArrayList<ServiceCombo>();
		for (int i = 0; i < carshop.getBookableServices().size(); i++) {
			if (carshop.getBookableServices().get(i) instanceof ServiceCombo)
				combos.add((ServiceCombo) carshop.getBookableServices().get(i));
		}
		return combos;
	}

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @param comboname
	 * @return
	 */
	private static boolean isACombo(CarShop carshop, String comboname) {
		// checks if an element is a servicecombo
		for (int i = 0; i < getAllCombo(carshop).size(); i++) {
			if (getAllCombo(carshop).get(i).getName().equals(comboname))
				return true;
		}
		return false;
	}

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @param comboname
	 * @return
	 * @throws Exception
	 */
	private static ServiceCombo getComboByName(CarShop carshop, String comboname) {
		// gets a combo by its name
		for (int i = 0; i < getAllCombo(carshop).size(); i++) {
			if (getAllCombo(carshop).get(i).getName().equals(comboname))
				return getAllCombo(carshop).get(i);
		}
		return null;
	}

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @return
	 */
	private static ArrayList<Service> getAllService(CarShop carshop) {
		// gets all the services in the system
		ArrayList<Service> services = new ArrayList<Service>();
		for (int i = 0; i < carshop.getBookableServices().size(); i++) {
			if (carshop.getBookableServices().get(i) instanceof Service)
				services.add((Service) carshop.getBookableServices().get(i));
		}
		return services;
	}

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @param servicename
	 * @return
	 */
	private static boolean isAService(CarShop carshop, String servicename) {
		// checks if the input is a service in the system
		for (int i = 0; i < getAllService(carshop).size(); i++) {
			if (getAllService(carshop).get(i).getName().equals(servicename))
				return true;
		}
		return false;
	}

	/**
	 * @aly Mohamed
	 * @param carshop
	 * @param servicename
	 * @return
	 * @throws Exception
	 */
	private static Service getServiceByName(CarShop carshop, String servicename) {
		// gets a service by its name
		for (int i = 0; i < getAllService(carshop).size(); i++) {
			if (getAllService(carshop).get(i).getName().equals(servicename))
				return getAllService(carshop).get(i);
		}
		return null;
	}

	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * Mehdi
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	static String resultError;

	static String result;

	static String StringType;

	///////////////////////////////////////////////////////////////////////////// HELPER
	///////////////////////////////////////////////////////////////////////////// METHODS
	///////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////////

	public static String returnResultError() {
		return resultError;
	}

	public static void setResultError(String string) {

		resultError = string;
	}

	public static String returnResult() {
		return result;
	}

	public static void setResult(String string) {

		result = string;
	}

	public static String returnStringType() {
		return StringType;
	}

	// public static Time StringToTime(String string) {
	// String hours="";
	// String minutes = "";
	// hours +=string.charAt(0);
	// hours +=string.charAt(1);
	// minutes +=string.charAt(3);
	// minutes +=string.charAt(4);
	//
	//
	// @SuppressWarnings("deprecation")
	// Time time= new
	// java.sql.Time(Integer.parseInt(hours),Integer.parseInt(minutes),0);
	//
	// return time;
	//
	//
	// }
	//
	public static Time StringToTime(String string) {
		if (string.length() < 11) {
			if (string.length() == 4) {
				string = "0" + string;
			}
			String hours = "";
			String minutes = "";
			hours += string.charAt(0);
			hours += string.charAt(1);
			minutes += string.charAt(3);
			minutes += string.charAt(4);

			@SuppressWarnings("deprecation")
			Time time = new Time(Integer.parseInt(hours), Integer.parseInt(minutes), 0);

			return time;
		} else {
			String s = string.substring(11);
			return Time.valueOf(s + ":00");
		}

	}

	public static Date stringToDate(String string) {
		String datee = "";

		for (int i = 0; i < 10; i++) {
			datee += string.charAt(i);
		}

		Date date = Date.valueOf(datee);
		return date;
	}

	///////////////////////////////////////////////////////////////////////////// HELPER
	///////////////////////////////////////////////////////////////////////////// METHODS
	///////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////////////////////////////////

	public static String[] viewBusinessInfor() {

		// returns an array of string with each info

		Business currentBusiness = CarShopApplication.getCarShop().getBusiness();
		String[] busiStrings = new String[4];
		busiStrings[0] = currentBusiness.getName();
		busiStrings[1] = currentBusiness.getAddress();
		busiStrings[2] = currentBusiness.getPhoneNumber();
		busiStrings[3] = currentBusiness.getEmail();
		return busiStrings;
	}

	public static void RemoveExistingBusinessHour(ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek daytoChange,
			Time time) throws Exception {

		CarShopController.setResult("not");

		for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getBusinessHours().size(); i++) {

			if (CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getDayOfWeek().equals(daytoChange)
					&& CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getStartTime().equals(time)) {

				CarShopApplication.getCarShop().getBusiness()
						.removeBusinessHour(CarShopApplication.getCarShop().getBusiness().getBusinessHour(i));
			}
		}

		try {

			if (!CarShopApplication.getCurrentUser().getUsername().equals("owner")) {
				CarShopController.setResult("");

				throw new Exception("No permission to update business information");
			}
			CarshopPersistence.save(CarShopApplication.getCarShop());
		}

		catch (RuntimeException e) {
			String error = e.getMessage();
			throw new Exception(error);
		}

	}

	/*
	 * Method to set basic business information Only the owner can set up and update
	 * information of the business
	 * 
	 */

	public static void AddBusinessHours(ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek day, Time newStartTime,
			Time newEndTime) throws InvalidInputException {

		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		boolean noOverlap = false;

		try {

			if (newEndTime.before(newStartTime)) {
				CarShopController.setResult("not be");
				CarShopController.setResultError("be");

				throw new InvalidInputException("Start time must be before end time");
			}

			/*
			 * For time to overlap, it means that neither thestarttime or endtime can be in
			 * between a business hour
			 * 
			 * so if the new Startime is before the startime, then the endtime must be
			 * before or equal to the new endtime
			 * 
			 * and if the new start time is after the startime, then the endtime must be
			 * before the new endtime
			 * 
			 * In both cases, if that's not true, the time is overlapping
			 * 
			 */

			if (!CarShopApplication.getCurrentUser().getUsername().equals("owner")) {
				CarShopController.setResult("not be");
				CarShopController.setResultError("be");
				throw new InvalidInputException("No permission to update business information");
			}
			int i = 0;
			int test = 0;

			while (i < CarShopApplication.getCarShop().getBusiness().getBusinessHours().size()) {

				if (day.equals(CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getDayOfWeek())) {
					test++;
					if (newStartTime
							.before(CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getStartTime())) {

						if (newEndTime
								.before(CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getStartTime())
								|| newEndTime.equals(CarShopApplication.getCarShop().getBusiness().getBusinessHour(i)
										.getStartTime())) {
							// we're good
							noOverlap = true;
							i++;
						} else {
							CarShopController.setResult("not be");
							CarShopController.setResultError("be");
							throw new InvalidInputException("The business hours cannot overlap");
						}
					}

					else {

						if (CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getEndTime()
								.before(newStartTime)
								|| CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getEndTime()
										.equals(newStartTime)) {

							noOverlap = true;
							i++;
						} else {
							// we're not good
							CarShopController.setResult("not be");
							CarShopController.setResultError("be");
							throw new InvalidInputException("The business hours cannot overlap");
						}
					}
				}
				i++;
			}

			if (test == 0) {
				if (newEndTime.after(newStartTime)) {
					noOverlap = true;
				}
			}

			if (CarShopApplication.getCarShop().getBusiness().getBusinessHours().size() == 0)
				noOverlap = true;
			System.out.println("overlap is : " + noOverlap);
			if (noOverlap) {
				System.out.println("we are no overlap");
				BusinessHour newBusinessHour = new BusinessHour(day, newStartTime, newEndTime,
						CarShopApplication.getCarShop());
				CarShopApplication.getCarShop().getBusiness().addBusinessHour(newBusinessHour);
			}

			CarshopPersistence.save(CarShopApplication.getCarShop());

		} catch (RuntimeException e) {
			String error = e.getMessage();
			throw new InvalidInputException(error);

		}

	}

	public static void SetUpBusinessInformation(String name, String adress, String phoneNumber, String email)
			throws InvalidInputException {

		// done just needs
		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		String regex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
		// +" ^[a-zA-Z0-9_+&*-]+(?:\\\\a-zA-Z0-9_+&*-]+)*@" +
		// "(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}$";

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);

		boolean validate = matcher.find();

		try {

			if (!CarShopApplication.getCurrentUser().getUsername().equals("owner")) {

				CarShopController.setResultError("be");
				CarShopController.setResult("not be");

				throw new InvalidInputException("No permission to set up business information");
			}

			if (!email.contains("@") || !email.contains(".com")) {
				CarShopController.setResultError("be");
				CarShopController.setResult("not be");
				throw new InvalidInputException("Invalid email");
			}

			if (CarShopApplication.getCarShop().getBusiness() == null) {
				Business newyBusiness = new Business(name, adress, phoneNumber, email, CarShopApplication.getCarShop());
				CarShopApplication.getCarShop().setBusiness(newyBusiness);
			} else {
				CarShopApplication.getCarShop().getBusiness().setEmail(email);
				CarShopApplication.getCarShop().getBusiness().setName(name);
				CarShopApplication.getCarShop().getBusiness().setAddress(adress);
				CarShopApplication.getCarShop().getBusiness().setPhoneNumber(phoneNumber);

			}

			CarshopPersistence.save(CarShopApplication.getCarShop());
		}

		catch (InvalidInputException e) {

			String error = e.getMessage();
			throw new InvalidInputException(error);
		}

	}

	public static void AddTimeSlot(String Type, Date StartDate, Time StartTime, Date EndDate, Time EndTime)
			throws Exception {

		StringType = Type;

		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		TimeSlot newTimeSlot = new TimeSlot(StartDate, StartTime, EndDate, EndTime, CarShopApplication.getCarShop());

		String date = "2021-02-01";
		String time = "11:00";
		Date currentDate = CarShopController.stringToDate(date);

		try {

			if (StartDate.before(currentDate)) {
				CarShopController.setResultError("be");
				CarShopController.setResult("not be");

				if (Type.equals("holiday"))
					throw new Exception("Holiday cannot start in the past");
				if (Type.equals("vacation"))
					throw new Exception("Vacation cannot start in the past");
			}

			// if startTime is before endtime, throw error

			if (EndDate.before(StartDate)) {
				CarShopController.setResultError("be");
				CarShopController.setResult("not be");

				throw new Exception("Start time must be before end time");
			}

			if (!CarShopApplication.getCurrentUser().getUsername().equals("owner")) {

				CarShopController.setResultError("be");
				CarShopController.setResult("not be");

				throw new Exception("No permission to update business information");
			}

			if (Type.equals("vacation")) {

				for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getVacations().size(); i++) {

					if (StartDate.after(CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate())
							&& (!StartDate.after(
									CarShopApplication.getCarShop().getBusiness().getVacation(i).getEndDate()))) {

						CarShopController.setResultError("be");
						CarShopController.setResult("not be");

						throw new Exception("Vacation times cannot overlap");

					}

					else {

						if ((StartDate
								.before(CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate()))
								&& (!EndDate.before(
										CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate()))) {
							CarShopController.setResultError("be");
							CarShopController.setResult("not be");

							throw new Exception("Vacation times cannot overlap");
						}

					}
				}

				for (int j = 0; j < CarShopApplication.getCarShop().getBusiness().getHolidays().size(); j++) {

					if ((StartDate.after(CarShopApplication.getCarShop().getBusiness().getHoliday(j).getStartDate())
							&& (!StartDate.after(
									CarShopApplication.getCarShop().getBusiness().getHoliday(j).getEndDate())))) {
						CarShopController.setResultError("be");
						CarShopController.setResult("not be");

						throw new Exception("Holiday and vacation times cannot overlap");
					} else {
						if ((StartDate
								.before(CarShopApplication.getCarShop().getBusiness().getHoliday(j).getStartDate()))
								&& (!EndDate.before(
										CarShopApplication.getCarShop().getBusiness().getHoliday(j).getStartDate()))) {

							CarShopController.setResultError("be");
							CarShopController.setResult("not be");

							throw new Exception("Holiday and vacation times cannot overlap");

						}

					}
				}

			}

			else {

				for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getVacations().size(); i++) {

					if (StartDate.after(CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate())
							&& (!StartDate.after(
									CarShopApplication.getCarShop().getBusiness().getVacation(i).getEndDate()))) {

						CarShopController.setResultError("be");
						CarShopController.setResult("not be");

						if (Type.equals("holiday"))
							throw new Exception("Holiday and vacation times cannot overlap");

					}

					else {

						if ((StartDate
								.before(CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate()))
								&& (!EndDate.before(
										CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate()))) {
							CarShopController.setResultError("be");
							CarShopController.setResult("not be");

							throw new Exception("Holiday and vacation times cannot overlap");
						}

					}
				}

				for (int j = 0; j < CarShopApplication.getCarShop().getBusiness().getHolidays().size(); j++) {

					if ((StartDate.after(CarShopApplication.getCarShop().getBusiness().getHoliday(j).getStartDate())
							&& (!StartDate.after(
									CarShopApplication.getCarShop().getBusiness().getHoliday(j).getEndDate())))) {
						CarShopController.setResultError("be");
						CarShopController.setResult("not be");

						throw new Exception("Holiday times cannot overlap");
					} else {
						if ((StartDate
								.before(CarShopApplication.getCarShop().getBusiness().getHoliday(j).getStartDate()))
								&& (!EndDate.before(
										CarShopApplication.getCarShop().getBusiness().getHoliday(j).getStartDate()))) {

							CarShopController.setResultError("be");
							CarShopController.setResult("not be");

							throw new Exception("Holiday times cannot overlap");

						}

					}
				}
			}
			if (Type.equals("vacation"))
				CarShopApplication.getCarShop().getBusiness().addVacation(newTimeSlot);
			else
				CarShopApplication.getCarShop().getBusiness().addHoliday(newTimeSlot);

			// throw new Exception( "Holiday and vacation times cannot overlap");

			CarshopPersistence.save(CarShopApplication.getCarShop());

		} catch (RuntimeException e) {
			String error = e.getMessage();
			throw new Exception(error);

		}

	}

	public static void UpdateBusinessHours(ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek daytoChange,
			Time startTimeToChange, ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek wantedDay, Time wantedStartTime,
			Time wantedEndTime) throws InvalidInputException {
		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getBusinessHours().size(); i++) {

			if (CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getDayOfWeek().equals(daytoChange)
					&& CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getStartTime()
							.equals(startTimeToChange)) {

				CarShopApplication.getCarShop().getBusiness()
						.removeBusinessHour(CarShopApplication.getCarShop().getBusiness().getBusinessHour(i));

				try {
					CarShopController.AddBusinessHours(wantedDay, wantedStartTime, wantedEndTime);

					CarshopPersistence.save(CarShopApplication.getCarShop());
				} catch (InvalidInputException e) {

					String error = e.getMessage();
					throw new InvalidInputException(error);
				}
			}

		}

	}

	public static void UpdateVacation(String Type, Date daytoChange, Time startTimeToChange, Date wantedDay,
			Time wantedStartTime, Date wantedEndDate, Time wantedEndTime) throws Exception {

		StringType = Type;

		String date = "2021-02-01";

		Date currentDate = CarShopController.stringToDate(date);

		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getVacations().size(); i++) {

			if (CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartDate().equals(daytoChange)
					&& CarShopApplication.getCarShop().getBusiness().getVacation(i).getStartTime()
							.equals(startTimeToChange)) {

				CarShopApplication.getCarShop().getBusiness()
						.removeVacation(CarShopApplication.getCarShop().getBusiness().getVacation(i));

				try {
					CarShopController.AddTimeSlot(Type, wantedDay, wantedStartTime, wantedEndDate, wantedEndTime);

					CarshopPersistence.save(CarShopApplication.getCarShop());
				} catch (InvalidInputException e) {

					String error = e.getMessage();
					throw new InvalidInputException(error);
				}
			}

		}
	}

	public static void UpdateHoliday(String Type, Date daytoChange, Time startTimeToChange, Date wantedDay,
			Time wantedStartTime, Date wantedEndDate, Time wantedEndTime) throws Exception {

		StringType = Type;

		String date = "2021-02-01";

		Date currentDate = CarShopController.stringToDate(date);

		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getHolidays().size(); i++) {

			if (CarShopApplication.getCarShop().getBusiness().getHoliday(i).getStartDate().equals(daytoChange)
					&& CarShopApplication.getCarShop().getBusiness().getHoliday(i).getStartTime()
							.equals(startTimeToChange)) {

				CarShopApplication.getCarShop().getBusiness()
						.removeHoliday(CarShopApplication.getCarShop().getBusiness().getHoliday(i));

				try {
					CarShopController.AddTimeSlot(Type, wantedDay, wantedStartTime, wantedEndDate, wantedEndTime);

					CarshopPersistence.save(CarShopApplication.getCarShop());
				} catch (InvalidInputException e) {

					String error = e.getMessage();
					throw new InvalidInputException(error);
				}
			}

		}
	}

	public static void RemoveExistingTimeSlot(String Type, Date startdaytoremove, Time startime, Date enddateToremove,
			Time endTime) throws Exception {

		CarShopController.setResult("not");

		for (int i = 0; i < CarShopApplication.getCarShop().getTimeSlots().size(); i++) {

			if (CarShopApplication.getCarShop().getTimeSlot(i).getStartDate().equals(startdaytoremove)
					&& CarShopApplication.getCarShop().getTimeSlot(i).getStartTime().equals(startime)
					&& CarShopApplication.getCarShop().getTimeSlot(i).getEndDate().equals(enddateToremove)
					&& CarShopApplication.getCarShop().getTimeSlot(i).getEndTime().equals(endTime)) {

				CarShopApplication.getCarShop().removeTimeSlot(CarShopApplication.getCarShop().getTimeSlot(i));
			}
		}

		try {

			if (!CarShopApplication.getCurrentUser().getUsername().equals("owner")) {
				CarShopController.setResult("");
				throw new Exception("No permission to update business information");
			}
			CarshopPersistence.save(CarShopApplication.getCarShop());
		}

		catch (RuntimeException e) {
			String error = e.getMessage();
			throw new Exception(error);
		}

	}

	public static void UpdateBusinessInformation(String name, String adress, String phoneNumber, String email)
			throws InvalidInputException {

		CarShopController.setResultError("not be");
		CarShopController.setResult("be");

		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
		// " ^[a-zA-Z0-9_+&*-]+(?:\\\\a-zA-Z0-9_+&*-]+)*@" +
		// "(?:[a-zA-Z0-9-]+\\\\.)+[a-zA-Z]{2,7}$";

		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(email);

		boolean validate = matcher.find();

		Business currentBusiness = CarShopApplication.getCarShop().getBusiness();

		currentBusiness.setName(name);
		currentBusiness.setAddress(adress);
		currentBusiness.setPhoneNumber(phoneNumber);
		currentBusiness.setEmail(email);

		try {

			if (!validate) {
				CarShopController.setResultError("be");
				CarShopController.setResult("not be");
				throw new InvalidInputException("Invalid email");
			}

			if (!CarShopApplication.getCurrentUser().getUsername().equals("owner")) {

				CarShopController.setResultError("be");
				CarShopController.setResult("not be");

				throw new InvalidInputException("No permission to update business information");
			}
			CarshopPersistence.save(CarShopApplication.getCarShop());
		}

		catch (RuntimeException e) {
			String error = e.getMessage();
			throw new InvalidInputException(error);

		}

	}

	/**
	 * 
	 * 
	 * 
	 * @author Abdullah Arafat
	 * 
	 * 
	 * 
	 */

	public static void cancelAppointment(String loggedInUser, String userOfAppointment, String appointmentType,
			String datePassed, String timePassed) throws Exception {

		// must be correct user
		if (!loggedInUser.equals(userOfAppointment)) {
			User loggedIn = User.getWithUsername(loggedInUser);
			if (loggedIn instanceof Customer)
				throw new Exception("A customer can only cancel their own appointments");
			else if (loggedIn instanceof Technician)
				throw new Exception("A technician cannot cancel an appointment");
			else if (loggedIn instanceof Owner)
				throw new Exception("An owner cannot cancel an appointment");
		}

		// cannot cancel on same day
		Date today = stringToDate(CarShopApplication.getSystemDate());
		Date date = stringToDate(datePassed);
		if (!date.before(today) && !date.after(today))
			throw new Exception("Cannot cancel an appointment on the appointment date");

		List<Appointment> arr = CarShopApplication.getCarShop().getAppointments();
		for (Appointment a : arr) {
			if (a.getCustomer().getUsername().equals(userOfAppointment)
					&& a.getBookableService().getName().equals(appointmentType)) {

				a.delete();
				break;
			}

		}

		List<List<Object>> l = CarShopApplication.getBookingTimes();

		for (int i = 0; i < CarShopApplication.getBookingTimes().size(); i++) {
			if (l.get(i).get(0).toString().equals(datePassed) && l.get(i).get(1).toString().equals(timePassed)
					&& l.get(i).get(4).toString().equals(userOfAppointment)) {
				l.remove(i);
			}
		}

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}

	public static void makeAppointment(String loggedInUser, String date, String serviceName, String optionalServices,
			String sTime) throws Exception {
		// type-ing data and initing
		boolean combo = optionalServices != null;
		Date sysDate = stringToDate(CarShopApplication.getSystemDate());
		Date d = stringToDate(date);
		User u = User.getWithUsername(loggedInUser);

		// must be a customer
		if (u instanceof Owner || u instanceof Technician)
			throw new Exception("Only customers can make an appointment");

		// cannot be in the past
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		if (d.before(sysDate))
			throw new Exception("There are no available slots for " + serviceName + " on " + date + " at " + sTime);

		// turn sTime into an array of times
		String[] sTimes = sTime.split(",");
		Time[] t = new Time[sTimes.length];
		for (int i = 0; i <= t.length - 1; i++) {
			t[i] = StringToTime(sTimes[i]);
		}

		// cannot be during a holiday
		List<TimeSlot> holidays = CarShopApplication.getCarShop().getBusiness().getHolidays();
		for (TimeSlot h : holidays) {
			for (Time time : t) {
				if ((d.after(h.getStartDate()) && d.before(h.getEndDate()))
						|| (!d.before(h.getStartDate()) && !d.after(h.getStartDate()) && time.after(h.getStartTime()))
						|| (!d.before(h.getEndDate()) && !d.after(h.getEndDate()) && time.before(h.getEndTime())))
					throw new Exception(
							"There are no available slots for " + serviceName + " on " + date + " at " + sTime);
			}
		}

		// not during business hour
		if (!checkIfInHours(CarShopApplication.getCarShop().getBusiness().getBusinessHours(), t, c))
			throw new Exception("There are no available slots for " + serviceName + " on " + date + " at " + sTime);

		// on create appointment
		List<List<Object>> prevBookings = CarShopApplication.getBookingTimes();
		if (prevBookings != null) {
			for (List<Object> booking : prevBookings) {
				Date day = (Date) booking.get(0);
				Time start = (Time) booking.get(1);
				Time end = (Time) booking.get(2);
				// for(Time time: t) {
				if (day.equals(d) && t[0].after(start) && t[0].before(end)) {
					throw new Exception(
							"There are no available slots for " + serviceName + " on " + date + " at " + sTime);
					// }
				}
			}
		}

		// to check, make sure you're not in any of those slots

		Appointment a;
		if (!combo) {
			CarShop carshop = CarShopApplication.getCarShop();
			BookableService s = Service.getWithName(serviceName);
			Service ser = (Service) s;
			if (!checkIfInHours(ser.getGarage().getBusinessHours(), t, c))
				throw new Exception("There are no available slots for " + serviceName + " on " + date + " at " + sTime);
			a = new Appointment((Customer) u, s, CarShopApplication.getCarShop());

			Time start = t[0];
			Time end = new Time(t[0].getTime() + (ser.getDuration() * 60 * 1000));

			// d date start starttime end endtime
			List<Object> bookingTime = new ArrayList<Object>();
			bookingTime.add(d);
			bookingTime.add(start);
			bookingTime.add(end);

			bookingTime.add(ser);

			CarShopApplication.getBookingTimes().add(bookingTime);
			CarShopApplication.setCurAppointment(a);

			TimeSlot ts = new TimeSlot(d, start, d, end, CarShopApplication.getCarShop());
			ServiceBooking serviceBooking = new ServiceBooking(ser, ts, a);
			// String test = serviceBooking.getTimeSlot().getStartDate().toString();

			carshop.addAppointment(a);

		} else {
			// services cannot overlap
			String[] oSerivces = optionalServices.split(",");
			boolean wascreated = false;
			List<BookableService> allServices = CarShopApplication.getCarShop().getBookableServices();
			for (BookableService bs : allServices) {
				if (bs instanceof ServiceCombo) {
					ServiceCombo sc = (ServiceCombo) bs;
					if (sc.getName().equals(serviceName)) {
						int counter = 0;
						Time startOfSer = t[0];
						Time endOfSer = t[0];
						// a = new Appointment((Customer) u, bs, CarShopApplication.getCarShop());
						for (ComboItem ci : sc.getServices()) {
							if (!checkIfInHours(ci.getService().getGarage().getBusinessHours(), t, c))
								throw new Exception("There are no available slots for " + serviceName + " on " + date
										+ " at " + sTime);

							// this isn't necessarily right
							if (counter + 1 < t.length) {
								Time timeAfterService = new Time(
										t[counter].getTime() + (ci.getService().getDuration() * 60 * 1000));
								if (timeAfterService.after(t[counter + 1]))
									throw new Exception("Time slots for two services are overlapping");
							}

							counter++;

						}

						a = new Appointment((Customer) u, bs, CarShopApplication.getCarShop());
						wascreated = true;

						Time end = t[0];
						for (ComboItem ci : sc.getServices()) {
							if (ci.getService().getName().equals(sc.getMainService().getService().getName())) {
								Time temp = new Time(end.getTime());
								end = new Time(end.getTime() + (ci.getService().getDuration() * 60 * 1000));
								TimeSlot ts = new TimeSlot(d, temp, d, end, CarShopApplication.getCarShop());
								ServiceBooking serviceBooking = new ServiceBooking(ci.getService(), ts, a);
							}
						}

						for (ComboItem ci : sc.getServices()) {
							if (!ci.getService().getName().equals(sc.getMainService().getService().getName())) {
								Time temp = new Time(end.getTime());
								end = new Time(end.getTime() + (ci.getService().getDuration() * 60 * 1000));
								TimeSlot ts = new TimeSlot(d, temp, d, end, CarShopApplication.getCarShop());
								ServiceBooking serviceBooking = new ServiceBooking(ci.getService(), ts, a);
							}
						}

						// ABE ADDED APRIL 2
						for (int i = 0; i <= oSerivces.length - 1; i++) {
							Service s = (Service) Service.getWithName(oSerivces[i]);
							TimeSlot ts = new TimeSlot(d, t[i + 1], d,
									new Time(t[i + 1].getTime() + (s.getDuration() * 60 * 1000)),
									CarShopApplication.getCarShop());
							ServiceBooking serviceBooking = new ServiceBooking(s, ts, a);
						}

						// Appointment a = new Appointment()
						// a = new Appointment((Customer) u, bs, CarShopApplication.getCarShop());

						CarShopApplication.setCurAppointment(a);
						CarShopApplication.getCarShop().addAppointment(a);

					}

				}
			}

			if (!wascreated) {
				Service service = (Service) BookableService.getWithName(serviceName);

				if (!checkIfInHours(service.getGarage().getBusinessHours(), t, c))
					throw new Exception(
							"There are no available slots for " + service.getName() + " on " + date + " at " + sTime);

				Appointment app = new Appointment((Customer) Customer.getWithUsername(loggedInUser),
						BookableService.getWithName(serviceName), CarShopApplication.getCarShop());

				// Service service = (Service) BookableService.getWithName(serviceName);
//				Time end = new Time(end.getTime() + (ci.getService().getDuration() * 60 * 1000));
//				System.out.println("sTime is :"+sTime.toString());
				Time endtime = new Time(
						StringToTime(sTime.split(",")[0]).getTime() + (service.getDuration() * 60 * 1000));

				TimeSlot timeslot = new TimeSlot(d, StringToTime(sTime.split(",")[0]), d, endtime,
						CarShopApplication.getCarShop());
				ServiceBooking booking = new ServiceBooking(service, timeslot, app);

				String[] services = optionalServices.split(",");

				for (int i = 0; i < optionalServices.split(",").length; i++) {
					// optionalServices

					Service serv = (Service) Service.getWithName(optionalServices.split(",")[i]);

					Time temp = new Time(endtime.getTime());
					endtime = new Time(endtime.getTime() + (service.getDuration() * 60 * 1000));

					if (!checkIfInHours(service.getGarage().getBusinessHours(), t, c))
						throw new Exception("There are no available slots for " + service.getName() + " on " + date
								+ " at " + sTime);

					timeslot = new TimeSlot(d, temp, d, endtime, CarShopApplication.getCarShop());
					booking = new ServiceBooking(service, timeslot, app);

					// String[] services = optionalServices.split(",");

				}

			}
		}

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
			System.out.println("saving");
		} catch (RuntimeException e) {
			System.out.println("not saving");
			throw new InvalidInputException(e.getMessage());
		}

	}

	public static boolean checkIfInHours(List<BusinessHour> hours, Time[] t, Calendar c) {
		boolean toReturn = false;
		for (BusinessHour b : hours) {
			if ((b.getDayOfWeek().toString() == "Monday" && c.get(Calendar.DAY_OF_WEEK) == 2)
					|| (b.getDayOfWeek().toString() == "Tuesday" && c.get(Calendar.DAY_OF_WEEK) == 3)
					|| (b.getDayOfWeek().toString() == "Wednesday" && c.get(Calendar.DAY_OF_WEEK) == 4)
					|| (b.getDayOfWeek().toString() == "Thursday" && c.get(Calendar.DAY_OF_WEEK) == 5)
					|| (b.getDayOfWeek().toString() == "Friday" && c.get(Calendar.DAY_OF_WEEK) == 6)
					|| (b.getDayOfWeek().toString() == "Saturday" && c.get(Calendar.DAY_OF_WEEK) == 7)
					|| (b.getDayOfWeek().toString() == "Sunday" && c.get(Calendar.DAY_OF_WEEK) == 1)) {
				for (Time time : t) {
					if (time.before(b.getEndTime()) && (time.equals(b.getStartTime()) || time.after(b.getStartTime())))
						toReturn = true;
				}
			}

		}

		return toReturn;

	}

	/**
	 * 
	 * DELIVERABLE 3
	 * 
	 * 
	 */

	public static void updateAppointment(CarShop carshop, Appointment a, String user, String newService, String newDate,
			String newTime, String newOptSer, String newOptSerTime) throws InvalidInputException {

		Appointment.updateAppointment(carshop, a, user, newService, newDate, newTime, newOptSer, newOptSerTime);

	}
	// if(!user.equals("owner") && findCustomer(user)==null) {
	//
	// }
	//
	// if(a.getAppointmentStatusFullName().equals("Booked")) {
	//
	// Date today = stringToDate(CarShopApplication.getSystemDate());
	//
	//
	// //we are on the day before
	// if(a.getServiceBooking(0).getTimeSlot().getStartDate().before(today)) {
	// if(newService!=null) updateService(carshop, a, newService);
	// if(newDate!=null) updateDate(a, newDate);
	// if(newTime!=null) updateTime(a, newTime);
	// if(newOptSer!=null) updateServices(a, newOptSer, newOptSerTime);
	// }
	//
	// else {
	//
	//
	// }
	// }
	//
	// if(user.equals("owner")) {
	//
	// }

	private static void updateServices(Appointment a, String newOptSer, String newOptSerTime) {
		// TODO Auto-generated method stub

	}

	private static void updateTime(Appointment a, String newTime) {
		// only use this method when there is only one service
		Time t = StringToTime(newTime);
		ServiceBooking sb = a.getServiceBooking(0);
		TimeSlot ts = sb.getTimeSlot();
		ts.setStartTime(t);
		ts.setEndTime(new Time(t.getTime() + (sb.getService().getDuration() * 60 * 1000)));
	}

	public static void updateDate(Appointment a, String newDate) {
		Date d = stringToDate(newDate);
		for (ServiceBooking sb : a.getServiceBookings()) {
			TimeSlot ts = sb.getTimeSlot();
			ts.setStartDate(d);
			ts.setEndDate(d);
		}

	}

	private static void updateService(CarShop carshop, Appointment appointment, String newService) {
		// TODO Auto-generated method stub
		if (isAService(carshop, newService)) {
			appointment.setBookableService(getServiceByName(carshop, newService));
			// do we need to edit the list of service bookings or is that automatic
		}
	}

	public static void RegisterNoShow(String string) {

		Time TimeAttempt = CarShopController.GetStartTimeOfWeirdFormat(string);
		Date dateAttempt = CarShopController.stringToDate(string);

		for (Appointment app : CarShopApplication.getCarShop().getAppointments()) {

			if (app.getServiceBooking(0).getTimeSlot().getStartDate().equals(dateAttempt)
					&& !app.getAppointmentStatusFullName().equals("InProgress")) {

				if (app.getServiceBooking(0).getTimeSlot().getEndTime().before(TimeAttempt)) {
					app.getCustomer().setNoShows(app.getCustomer().getNoShows() + 1);
					app.delete();
				}

			}

		}

	}

	public static Time GetStartTimeOfWeirdFormat(String string) {

		String hours = "";
		String minutes = "";
		hours += string.charAt(11);
		hours += string.charAt(12);
		minutes += string.charAt(14);
		minutes += string.charAt(15);

		@SuppressWarnings("deprecation")
		Time time = new java.sql.Time(Integer.parseInt(hours), Integer.parseInt(minutes), 0);

		return time;

	}

	public static void endAppointment(String date) {
		// TODO Auto-generated method stub
		CarShopApplication.setSystemDate(date);
		CarShopApplication.getCarShop().getAppointments()
				.get(CarShopApplication.getCarShop().getAppointments().size() - 1).endAppointment();
	}

	// Simo Benkirane
	public static void endAppointment(Appointment a) throws InvalidInputException {
		CarShop carshop = CarShopApplication.getCarShop();
		if (a == null) {
			throw new InvalidInputException("Appointment can't be null");
		}

		if (!carshop.getAppointments().contains(a)) {
			throw new InvalidInputException("Appointment doesn't exist");
		}

		if (!(CarShopApplication.getCurrentUser() instanceof Owner)) {
			throw new InvalidInputException("Not authorized for this action");
		}

		a.endAppointment();
	}

	public static void startAppointment(String date, String user) throws InvalidInputException {
		if (date == null)
			throw new InvalidInputException("Date cannot be null");

		if (!(user.equals("owner"))) {
			throw new InvalidInputException("Not authorized for this action");
		}

		CarShopApplication.setSystemDate(date);
		CarShop carShop = CarShopApplication.getCarShop();
		Appointment appointment = carShop.getAppointment(carShop.getAppointments().size() - 1);

		if (appointment != null) {
			// System.out.println("DATE: " + stringToDate(date) + " " + StringToTime(date));
			appointment.startAppointment(stringToDate(date), StringToTime(date));
		}

		try {
			CarshopPersistence.save(carShop);
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}

	// Bonus features

	/**
	 * @author Alex Saroufim and Aly Mohamed
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 */
	public static void signUpTechnician(String username, String password) throws InvalidInputException {
		CarShop carshop = CarShopApplication.getCarShop();
		String error = "";
		// Cases where SignUp won't work
		if (username.length() == 0 && password.length() == 0) {
			error = "The user name and the password cannot be empty";
		} else if (username.length() == 0) {
			error = "The user name cannot be empty";
		} else if (password.length() == 0) {
			error = "The password cannot be empty";
		}
		if (error.length() > 0) {
			throw new InvalidInputException(error.trim());
		}

		try {
			if (carshop.getOwner() != null && CarShopApplication.getCurrentUser() == carshop.getOwner()) {

				// If it passes all cases signup the customer
				Technician c = carshop.addTechnician(username, password,
						TechnicianType.valueOf(username.split(",")[0]));
			}

			if (carshop.getTechnicians().size() > 0
					&& findTechnician(CarShopApplication.getCurrentUser().getUsername()) != null) {
				error = "You must log out of the technician account before creating a customer account";
				throw new InvalidInputException(error.trim());
			}

			if (Technician.getWithUsername(username) != null) {
				error = "The username already exists";
				throw new InvalidInputException(error.trim());
			}

			CarshopPersistence.save(carshop);

		} catch (RuntimeException e) {

			throw new InvalidInputException(e.getMessage());
		}
	}

	/**
	 * @author Aly Mohamed
	 */
	public static void signOut() {
		CarShopApplication.setCurrentUser(null);
	}

	/**
	 * @author Alexandre Saroufim
	 * 
	 * @return
	 */
	public static List<TOTimeSlot> getTimeSlots() {
		ArrayList<TOTimeSlot> timeSlots = new ArrayList<TOTimeSlot>();
		for (TimeSlot timeSlot : CarShopApplication.getCarShop().getTimeSlots()) {
			TOTimeSlot toTimeSlot = new TOTimeSlot(timeSlot.getStartDate(), timeSlot.getStartTime(),
					timeSlot.getEndDate(), timeSlot.getEndTime());
			timeSlots.add(toTimeSlot);
		}
		return timeSlots;
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 * @return a list of info on the appointments
	 */
	public static List<TOAppointment> getAppointments() {

		CarShop shop = CarShopApplication.getCarShop();
		List<TOAppointment> apps = new ArrayList<>();

		for (Appointment app : shop.getAppointments()) {
			TOUser user = new TOUser(app.getCustomer().getUsername());
			String name = app.getBookableService().getName();

			apps.add(new TOAppointment(name, user));
		}

		return apps;

	}

	/**
	 * @author Abd-El-Aziz Zayed
	 * @return a list of info on the appointments
	 */
	public static List<TOAppointment> getCurrentAppointments() {

		CarShop shop = CarShopApplication.getCarShop();
		List<TOAppointment> apps = new ArrayList<>();
		String currentDate = CarShopApplication.getSystemDate();

		for (Appointment app : shop.getAppointments()) {

			boolean today = false;
			for (ServiceBooking booking : app.getServiceBookings()) {
				if (booking.getTimeSlot().getStartDate().toString().equals(currentDate)) {
					today = true;
					break;
				}
			}

			if (today) {
				TOUser user = new TOUser(app.getCustomer().getUsername());
				String name = app.getBookableService().getName();
				apps.add(new TOAppointment(name, user));
			}
		}

		return apps;

	}

	/**
	 * @author Aly
	 * @param u
	 * @return
	 */
	public static boolean isCustomer(User u) {

		return u instanceof Customer;
	}

	/**
	 * @author Alex
	 * @return
	 */
	public static User getCurrUser() {
		return CarShopApplication.getCurrentUser();
	}

	/**
	 * @author Aziz
	 * @param user
	 * @return
	 */
	public static boolean hasUsername(String user) {
		return User.hasWithUsername(user);
	}

	/**
	 * @author Aly
	 * @param dayValue
	 * @param startTimeG
	 * @param endTimeG
	 * @throws InvalidInputException
	 */
	public static void addGarageHours(String dayValue, String startTimeG, String endTimeG)
			throws InvalidInputException {
		CarShopController.addGarageHours(BusinessHour.DayOfWeek.valueOf(dayValue),
				CarShopController.StringToTime(startTimeG), CarShopController.StringToTime(endTimeG),
				(Technician) User.getWithUsername(CarShopApplication.getCurrentUser().getUsername()));
	}

	/**
	 * @author Aly
	 * @param dayValue
	 * @param startTimeG
	 * @param endTimeG
	 * @throws InvalidInputException
	 */
	public static void removeGarageHours(String dayValue, String startTimeG, String endTimeG)
			throws InvalidInputException {
		CarShopController.removeGarageHours(BusinessHour.DayOfWeek.valueOf(dayValue),
				CarShopController.StringToTime(startTimeG), CarShopController.StringToTime(endTimeG),
				(Technician) User.getWithUsername(CarShopApplication.getCurrentUser().getUsername()));
	}

	/**
	 * @author Aly
	 * @param mainService
	 * @param optionalServices
	 * @param oldTime
	 * @param newTime
	 * @param oldAppDate
	 * @param newAppDate
	 * @param optstimeslots
	 * @throws InvalidInputException
	 */
	public static void updateAppointment(String mainService, String optionalServices, String oldTime, String newTime,
			String oldAppDate, String newAppDate, String optstimeslots) {
		Appointment app = null;
		for (Appointment a : CarShopApplication.getCarShop().getAppointments()) {
			if (a.getStartTime().toString().equals(oldTime)
					&& a.getCustomer().getUsername().equals(CarShopApplication.getCurrentUser().getUsername())) {
				app = a;
			}
		}

		try {
			CarShopController.updateAppointment(CarShopApplication.getCarShop(), app,
					CarShopApplication.getCurrentUser().getUsername(), mainService, newAppDate, newTime,
					optionalServices, optstimeslots);
		} catch (Exception e) {
			if (app == null)
				System.out.println("nulllll");
		}
	}

	/**
	 * @author Aly
	 * @param day
	 * @param time
	 * @param time2
	 * @throws InvalidInputException
	 */
	public static void doAddBusinessHours(String day, Time time, Time time2) throws InvalidInputException {
		AddBusinessHours(BusinessHour.DayOfWeek.valueOf(day), time, time2);
		// CarShopController.doAddBusinessHours(BusinessHour.DayOfWeek.Monday,
		// CarShopController.StringToTime(startTimeMonday.getText()),
		// CarShopController.StringToTime(endTimeMonday.getText()));
	}

	/**
	 * @author Aly
	 * @param day
	 * @param time
	 * @param newDay
	 * @param stime
	 * @param etime
	 * @throws InvalidInputException
	 */
	public static void doUpdateBusinessHours(String day, String time, String newDay, String stime, String etime)
			throws InvalidInputException {
		UpdateBusinessHours(BusinessHour.DayOfWeek.valueOf(day), CarShopController.StringToTime(time),
				BusinessHour.DayOfWeek.valueOf(newDay), CarShopController.StringToTime(stime),
				CarShopController.StringToTime(etime));
	}
	// CarShopController.UpdateBusinessHours(TOBusinessHour.DayOfWeek.valueOf(oldDay.getText()),CarShopController.StringToTime(oldStartTimeBusiness.getText()),TOBusinessHour.DayOfWeek.valueOf(newDay.getText()),
	// CarShopController.StringToTime(newStartTimeBusiness.getText()),
	// CarShopController.StringToTime(newEndTimeBusiness.getText()));

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	public static void RegisterNoShow() {
		RegisterNoShow(CarShopApplication.getSystemDate());
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	public static void startAppointment() throws InvalidInputException {
		startAppointment(CarShopApplication.getSystemDate(), CarShopApplication.getCurrentUser().getUsername());
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	public static void endAppointment() throws InvalidInputException {
		CarShop shop = CarShopApplication.getCarShop();
		endAppointment(shop.getAppointments().get(shop.getAppointments().size() - 1));
	}

}