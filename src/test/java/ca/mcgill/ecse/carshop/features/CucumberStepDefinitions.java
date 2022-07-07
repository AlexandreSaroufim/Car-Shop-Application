package ca.mcgill.ecse.carshop.features;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;


import ca.mcgill.ecse.carshop.*;
import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.model.BookableService;
import ca.mcgill.ecse.carshop.model.Business;
import ca.mcgill.ecse.carshop.model.CarShop;
import ca.mcgill.ecse.carshop.model.ComboItem;
import ca.mcgill.ecse.carshop.model.Customer;
import ca.mcgill.ecse.carshop.model.Garage;
import ca.mcgill.ecse.carshop.model.Owner;
import ca.mcgill.ecse.carshop.model.Service;
import ca.mcgill.ecse.carshop.model.ServiceCombo;
import ca.mcgill.ecse.carshop.model.Technician;
import ca.mcgill.ecse.carshop.model.Technician.TechnicianType;
import ca.mcgill.ecse.carshop.model.User;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class CucumberStepDefinitions {
	
	private static String filename = "testdata.carshop";

	private CarShop carshop;
	private Owner owner;
	private Business business;
	private Technician technician;
	private String oldservicename; 
	
	private String error;
	private int errorCntr;
	
	@Before
	public void setUp() {
		CarShop carshop = CarShopApplication.getCarShop();
		carshop.delete();
		//System.out.println("----------------------CARSHOP DELETED---------------------");
	}
	
	/**
	 * Author ALy Mohamed
	 */
	@Given("a Carshop system exists")
	public void a_carshop_system_exists() {//done
		if (carshop == null) {
			carshop = new CarShop();
			//System.out.println("-------NEW CARSHOP-----");
		}
		error="";
		errorCntr=0;
	}
	

	/**
	 * Author Aly mohamed
	 */
	@Given("an owner account exists in the system")
	public void an_owner_account_exists_in_the_system() {//done
		if(carshop.getOwner()==null) {
			//System.out.println(User.getWithUsername("owner"));
			owner = (Owner) User.getWithUsername("owner");
			carshop.setOwner(owner);
		}
	}
	
	/**
	 * Author Aly mohamed
	 */
	@Given("a business exists in the system")
	public void a_business_exists_in_the_system() {//done
		if(!carshop.hasBusiness()) {
			business = new Business("carshopApp","carshopAddress","carshopPhone","carshopEmail", carshop);
		}
	}
	
	/**
	 * Author Simo Benkirane
	 */
	@Given("the following technicians exist in the system:")
	public void the_following_technicians_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
		
		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
	    
		
		//System.out.println("TECHNICIANSSSSS-------");
	    for (Map<String, String> columns : rows) {
	    	String username = columns.get("username");
	    	String password = columns.get("password");
	    	TechnicianType type = TechnicianType.valueOf(columns.get("type"));
	    	
	    	//System.out.println(username + password + type);
	    	
	    	if(Technician.hasWithUsername(username) != true) {
	    		
	    		Technician techy = new Technician(username,password, type, carshop);
	    		System.out.println(techy.getCarShop().toString() + carshop.toString());
	    		carshop.addTechnician(techy);
	    	}
	    	carshop.addTechnician((Technician)Technician.getWithUsername(username));
	    }
	   // System.out.println("Done...TECHNICIANSSSSS-------:" + carshop.getTechnicians().size());
		
	}
	
	
	
	/**
	 * Author Aly Mohamed
	 */
	@Given("each technician has their own garage")
	public void each_technician_has_their_own_garage() {
		
		
		for(int i = 0; i<carshop.getTechnicians().size(); i++) {
			//System.out.println("IndiGarage...TECHNICIANSSSSS-------:" + carshop.getTechnicians().get(i).getGarage());
			if(!carshop.getTechnicians().get(i).hasGarage()) {
				carshop.getTechnicians().get(i).setGarage(new Garage(carshop, carshop.getTechnicians().get(i)));
			}
		}
	}
	
	
	/**
	 * @author simobenkirane
	 * @param username
	 */
	@Given("the Owner with username {string} is logged in")
	public void the_owner_with_username_is_logged_in(String username) {//check
		if(CarShopApplication.getCurrentUser()==null) {
		CarShopApplication.setCurrentUser(User.getWithUsername(username));
		}
		}
	
	
	/**
	 * @author simobenkirane
	 * @param username
	 * @param servicename
	 * @param duration
	 * @param techtype
	 * @throws Exception
	 */
	@When("{string} initiates the addition of the service {string} with duration {string} belonging to the garage of {string} technician")
	public void initiates_the_addition_of_the_service_with_duration_belonging_to_the_garage_of_technician(String username, String servicename, String duration, String techtype) throws Exception {
	    // Write code here that turns the phrase above into concrete actions
		//System.out.println("---pp----------WHENNN-----------addService:"+ carshop.getBookableServices());
	   try {

	    	CarShopController.addService(carshop,username,servicename, duration, techtype);
	    	//System.out.println("-------------WHENNN-----------addService:"+ carshop.getBookableServices());
	   }
	   catch(Exception e) {
	    	error += e.getMessage();
			errorCntr++;
	    }
	}
	
	/**
	 * @author simobenkirane
	 * @param servicename
	 */
	@Then("the service {string} shall exist in the system")
	public void the_service_shall_exist_in_the_system(String servicename) {
	    
		assertEquals(servicename,getService(carshop,servicename).getName());
		
	    
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
			//System.out.println("getService-----------: "+ b);
			if(b.getName().equals(servicename) && !(b instanceof ServiceCombo)) { 
				return (Service)b;
			}
		}
		
		return null;
		
	}
	
	
	/**
	 * @author simobenkirane
	 * @param username
	 */
	@Given("the user with username {string} is logged in")
	public void the_user_with_username_is_logged_in(String username) {
	    // Write code here that turns the phrase above into concrete actions
		User user = User.getWithUsername(username);
	    CarShopApplication.setCurrentUser(user);
	}
	
	
	
	/**
	 * @author simobenkirane
	 * @param servicename
	 * @param techtype
	 */
	@Then("the service {string} shall belong to the garage of {string} technician")
	public void the_service_shall_belong_to_the_garage_of_technician(String servicename, String techtype) {
	    // Write code here that turns the phrase above into concrete actions
		//Service s = getService(carshop,string);
		Service s = (Service) Service.getWithName(servicename);
		
		//System.out.println("then..belong---->" + carshop.getBookableServices());
		
		assertTrue(TechnicianType.valueOf(techtype).equals(s.getGarage().getTechnician().getType()));
	    
	}
	
	/**
	 * @author simobenkirane
	 * @param numServices
	 */
	@Then("the number of services in the system shall be {string}")
	public void the_number_of_services_in_the_system_shall_be(String numServices) {
	    // Write code here that turns the phrase above into concrete actions
		
		//System.out.println(numServices +" " + getAllServices(carshop).size() );
		assertTrue(Integer.parseInt(numServices) == (getAllServices(carshop).size()));
	    
	}
	
	
	/**
	 * @author simobenkirane
	 * @param carshop
	 * @return
	 */
	private static ArrayList<Service> getAllServices(CarShop carshop){

		ArrayList<Service> services = new ArrayList<Service>();
		for(int i = 0; i< carshop.getBookableServices().size(); i++) {
		if(carshop.getBookableServices().get(i) instanceof Service) services.add((Service) carshop.getBookableServices().get(i));
		}
		return services;
		}

	
	/**
	 * @author simobenkirane
	 * @param errorMessage
	 */
	@Then("an error message with content {string} shall be raised")
	public void an_error_message_with_content_shall_be_raised(String errorMessage) {
	    
		//System.out.println(error + ":--:"+ errorMessage);
		assertTrue(error.contains(errorMessage));
	}
	
	/**
	 * @author simobenkirane
	 * @param servicename
	 */
	@Then("the service {string} shall not exist in the system")
	public void the_service_shall_not_exist_in_the_system(String servicename) {
	   
		//should i remove it from its corresponding garage too?
		assertTrue(getService(carshop,servicename) == null);
	    
	}
	
	/**
	 * @author simobenkirane
	 * @param oldServicename
	 * @param serviceName
	 * @param duration
	 */
	@Then("the service {string} shall be updated to name {string}, duration {string}")
	public void the_service_shall_be_updated_to_name_duration(String oldServicename, String serviceName, String duration){
	    // Write code here that turns the phrase above into concrete actions
		
		assertEquals(oldservicename, oldServicename);
		
		Service s = getService(carshop,serviceName);
		
		//assertTrue(getService(carshop,string) == null);
		assertEquals(serviceName, s.getName() );
		
		//System.out.println(Integer.parseInt(duration) + "<-->" + s.getDuration());
		assertTrue(Integer.parseInt(duration) == s.getDuration() );
		
	    
	}
	
	/**
	 * @author simobenkirane
	 * @param dataTable
	 */
	@Given("the following customers exist in the system:")
	public void the_following_customers_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
	    // Write code here that turns the phrase above into concrete actions
		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
		
		for (Map<String, String> columns : rows) {
			
			String username = columns.get("username");
			String password = columns.get("password");
			
			
			if(Customer.hasWithUsername(username) != true) {
			Customer c = new Customer(username, password, carshop);
			carshop.addCustomer(c);
			}
			else {
				carshop.addCustomer((Customer)Customer.getWithUsername(username));
			}
			
		}
		

	}
	
	/**
	 * @author simobenkirane
	 * @param dataTable
	 */
	@Given("the following services exist in the system:")
	public void the_following_services_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
		
		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
	    
		//System.out.println("testiiiiii");
	    for (Map<String, String> columns : rows) {
	    	String name = columns.get("name");
	    	int duration = Integer.parseInt(columns.get("duration"));
	    	Garage garage = getGarage(carshop,columns.get("garage"));
	    	//System.out.println("---yoyo" + name + duration + (garage!=null) + carshop.getBookableServices().size() + carshop.getTechnicians().size());
	        
	    	if(Service.hasWithName(name) != true) {
	    		carshop.addBookableService(new Service(name,carshop, duration,garage));
	    	}
	    	else {
	    		Service.getWithName(name).delete();
	    		carshop.addBookableService(new Service(name,carshop, duration,garage));
	    	}
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
	 * @param username
	 * @param oldServicename
	 * @param newservicename
	 * @param duration
	 * @param techtype
	 */
	@When("{string} initiates the update of the service {string} to name {string}, duration {string}, belonging to the garage of {string} technician")
	public void initiates_the_update_of_the_service_to_name_duration_belonging_to_the_garage_of_technician(String username, String oldServicename, String newservicename, String duration, String techtype) {

		try {
			//System.out.println("initiateUpdate of " + string + "to" + string2 + string3 + string4 + string5 );
			oldservicename = oldServicename;
			CarShopController.updateService(carshop, username, oldServicename, newservicename, duration,techtype);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}
	
	/**
	 * @author simobenkirane
	 * @param carshop
	 * @param technician
	 * @return
	 */
	private static Technician getTechnician(CarShop carshop, String technician) {
		
		List<Technician> techies= carshop.getTechnicians();
		
		for(int i = 0; i<techies.size(); i++){
			
			if(techies.get(i).getUsername().equals(technician)) {
				return techies.get(i);
			}	
		}

		return null;
	}
	
	/**
	 * @author simobenkirane
	 * @param string
	 * @param dataTable
	 */
	@Then("the service {string} shall still preserve the following properties:")
	public void the_service_shall_still_preserve_the_following_properties(String string, io.cucumber.datatable.DataTable dataTable) {
		
		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
		Service s = getService(carshop, string);
		
		for (Map<String, String> columns : rows) {
			String name = columns.get("name");
			String duration = columns.get("duration");
			String garage = columns.get("garage");
			
			assertEquals(name, s.getName());
			assertEquals(Integer.parseInt(duration), s.getDuration());
			assertEquals(TechnicianType.valueOf(garage), s.getGarage().getTechnician().getType());
			
			
		}
		
		
	}
	

	
	

}