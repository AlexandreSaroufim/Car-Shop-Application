package ca.mcgill.ecse.carshop.features;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Date;
import java.sql.Time;
//import java.time.DayOfWeek;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.text.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
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
import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import ca.mcgill.ecse.carshop.model.Appointment;

public class CucumberStepDefinitions {

	private CarShop carshop;
	private Owner owner;
	private Business business;
	private Technician technician;
	// private Garage garage= new Garage();
	private String combooldname;
	private String error;
	private int errorCntr;
	private int customerNum = 0;
	private int customerNumPrev = 0;
	private static User currentUser;
	private String oldUsername;
	private String oldPassword;
	private int sizeCustomers = 0;
	private int numOfAccounts;
	private String oldservicename;

	public static String resultError = "not be";
	public static String resultString = "";
	private String[] businessInfor;
	private String type;
	private String removeResult = "not";
	private int prevAppointmentSize;

	@BeforeEach
	public void setUp() {
		CarShop carshop = CarShopApplication.getCarShop();
		carshop.delete();
		error = "";
	}

	/**
	 * Alex ' s part
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * @author Alexandre Saroufim
	 * @param username
	 * @return
	 */
	private static Customer findCust(String username) {
		Customer foundCustomer = null;
		for (Customer customer : CarShopApplication.getCarShop().getCustomers()) {
			if (customer.getUsername().equals(username)) {
				foundCustomer = customer;
				break;
			}
		}
		return foundCustomer;
	}

	/**
	 * @author Alexandre Saroufim
	 * @param username
	 * @return
	 */
	private static Technician findTech(String username) {
		Technician foundTechnician = null;
		for (Technician technician : CarShopApplication.getCarShop().getTechnicians()) {
			if (technician.getUsername().equals(username)) {
				foundTechnician = technician;
				break;
			}
		}

		return foundTechnician;

	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 */
	@Given("there is no existing username {string}")
	public void there_is_no_existing_username(String username) {
		// Write code here that turns the phrase above into concrete actions
		Customer customer = findCust(username);
		if (customer != null) {
			customer.delete();
		}

	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 * @param string2
	 */
	@When("the user provides a new username {string} and a password {string}")
	public void the_user_provides_a_new_username_and_a_password(String username, String password) {
		// Write code here that turns the phrase above into concrete actions
		try {
			customerNumPrev = customerNum;
			sizeCustomers = carshop.getCustomers().size();
			CarShopController.signUpCustomer(username, password);
			CarShopApplication.setCurrentUser(findCust(username));
			sizeCustomers++;
			customerNum++;
		} catch (InvalidInputException e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

	/**
	 * @author Alexandre Saroufim
	 */
	@Then("a new customer account shall be created")
	public void a_new_customer_account_shall_be_created() {
		// Write code here that turns the phrase above into concrete actions
		// assertEquals(customerNum, customerNumPrev+1);
		assertEquals(sizeCustomers, carshop.getCustomers().size());
	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 * @param string2
	 */
	@Then("the account shall have username {string} and password {string}")
	public void the_account_shall_have_username_and_password(String username, String password) {
		// Write code here that turns the phrase above into concrete actions
		User c = CarShopApplication.getCurrentUser();
		assertEquals(username, c.getUsername());
		assertEquals(password, c.getPassword());
	}

	/**
	 * @author Alexandre Saroufim
	 */
	@Then("no new account shall be created")
	public void no_new_account_shall_be_created() {
		// Write code here that turns the phrase above into concrete actions
		assertEquals(customerNum, customerNumPrev);
	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 */
	@Then("an error message {string} shall be raised")
	public void an_error_message_shall_be_raised(String errorMsg) {
		// Write code here that turns the phrase above into concrete actions
		assertTrue(error.contains(errorMsg));
	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 */
	@Given("there is an existing username {string}")
	public void there_is_an_existing_username(String username) {
		// Write code here that turns the phrase above into concrete actions
		if (username.equals("owner")) {
			if (carshop.getOwner() == null) {
				Owner ownerNew = new Owner(username, username, carshop);
			}
		} else if (username.contains("Technician")) {
			if (findTech(username) == null) {
				Technician technicianNew = new Technician(username, username, null, carshop);
			}
		} else {
			if (findCust(username) == null) {
				Customer c = new Customer(username, username, carshop);
			}
		}
	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 * @param string2
	 */
	@Given("an owner account exists in the system with username {string} and password {string}")
	public void an_owner_account_exists_in_the_system_with_username_and_password(String username, String password) {
		// Write code here that turns the phrase above into concrete actions
		if (carshop.getOwner() == null) {
			Owner o = new Owner(username, password, carshop);
			carshop.setOwner(o);
		}
	}

	/**
	 * done
	 * 
	 * @author Alexandre Saroufim
	 * @param string
	 */
	@Given("the user is logged in to an account with username {string}")
	public void the_user_is_logged_in_to_an_account_with_username(String username) {
		// Write code here that turns the phrase above into concrete actions
		if (username.equals("owner")) {
			if (carshop.getOwner() == null) {
				Owner o = new Owner(username, username, carshop);
				CarShopApplication.setCurrentUser(o);
			} else {
				CarShopApplication.setCurrentUser(carshop.getOwner());
			}
		} else if (username.contains("Technician")) {
			if (findTech(username) == null) {
				Technician technicianNew = new Technician(username, username, null, carshop);
				CarShopApplication.setCurrentUser(technicianNew);
				carshop.addTechnician(technicianNew);
			} else {
				CarShopApplication.setCurrentUser(findTech(username));
			}

		} else {
			if (findCust(username) == null) {
				Customer c = new Customer(username, username, carshop);
				carshop.addCustomer(c);
				CarShopApplication.setCurrentUser(c);
			} else {
				CarShopApplication.setCurrentUser(findCust(username));
			}
		}

	}

	/**
	 * @author Alexandre Saroufim
	 * @param dataTable
	 */
	@Given("the following customers exist in the system:")
	public void the_following_customers_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {// works
		for (int i = 0; i < dataTable.height(); i++) {
			if (!User.hasWithUsername(dataTable.cell(i, 0))) {
				carshop.addCustomer(dataTable.cell(i, 0), dataTable.cell(i, 1));
			}
		}
		numOfAccounts += getNumberOfAccounts();
	}

	/**
	 * @author Alexandre Saroufim
	 * @param string
	 * @param string2
	 */
	@When("the user tries to update account with a new username {string} and password {string}")
	public void the_user_tries_to_update_account_with_a_new_username_and_password(String username, String password) {
		// Write code here that turns the phrase above into concrete actions
		try {
			// Should add in my controller the main update
			currentUser = CarShopApplication.getCurrentUser();
			oldUsername = currentUser.getUsername();
			oldPassword = currentUser.getPassword();
			CarShopController.updateUser(currentUser.getUsername(), username, password);
		} catch (InvalidInputException e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	/**
	 * @author Alexandre Saroufim
	 */
	@Then("the account shall not be updated")
	public void the_account_shall_not_be_updated() {
		// Write code here that turns the phrase above into concrete actions
		assertEquals(oldUsername, currentUser.getUsername());
		assertEquals(oldPassword, currentUser.getPassword());
	}

	/**
	 * Aly ' s part
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * Author Aly Mohamed
	 */
	@Given("a Carshop system exists")
	public void a_carshop_system_exists() {// done
		carshop = CarShopApplication.getCarShop();
		error = "";
		errorCntr = 0;
	}

	/**
	 * Author Aly mohamed
	 */
	@Given("an owner account exists in the system")
	public void an_owner_account_exists_in_the_system() {// add if statement
		
//		if(!Owner.hasWithUsername("owner")) {
			owner = new Owner("owner", "owner", carshop);
			carshop.setOwner(owner);
	
//		}
	}
	/**
	 * Author Aly mohamed
	 */
	@Given("a business exists in the system")
	public void a_business_exists_in_the_system() {// done
		if (!carshop.hasBusiness()) {

			if (!carshop.hasBusiness())
				new Business("Business", "1234 Number Street", "123 456 7890", "business@business.com", carshop);
		}
	}

	/**
	 * @author Aly Mohamed
	 */
	@Given("the following technicians exist in the system:")
	public void the_following_technicians_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) { // works
		for (int i = 1; i < dataTable.height(); i++) {
			// we may need to check if it exists first, that won't affect anything because
			// before testing, we delete the carshop we work on..
			carshop.addTechnician(dataTable.cell(i, 0), dataTable.cell(i, 1),
					Technician.TechnicianType.valueOf(dataTable.cell(i, 0).split("-")[0]));
		}
		// System.out.println(carshop.getTechnicians());
	}

	/**
	 * @author Aly Mohamed
	 */
	@Given("each technician has their own garage")
	public void each_technician_has_their_own_garage() {// works
		for (int i = 0; i < carshop.getTechnicians().size(); i++) {
			if (!carshop.getTechnicians().get(i).hasGarage()) {
				carshop.getTechnicians().get(i).setGarage(new Garage(carshop, carshop.getTechnicians().get(i)));
			}
		}
		// System.out.println(carshop.getTechnicians());
		// System.out.println(carshop.getGarages());
	}

	/**
	 * @author Aly Mohamed
	 * @param dataTable
	 */
	@Given("the following services exist in the system:")
	public void the_following_services_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {// works
		List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

		for (Map<String, String> columns : rows) {
			Service service = new Service(columns.get("name"), carshop, Integer.parseInt(columns.get("duration")),
					getServiceGarage(carshop, columns.get("name")));
			carshop.addBookableService(service);
		}
		// System.out.println(getAllService(carshop));
	}

	/**
	 * @author Simo Benkirane
	 * @param string
	 */
	@Given("the Owner with username {string} is logged in")
	public void the_owner_with_username_is_logged_in(String username) {// check
		if (CarShopApplication.getCurrentUser() == null) {
			CarShopApplication.setCurrentUser(User.getWithUsername(username));
		}
		// System.out.println(CarShopApplication.getCurrentUser());
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @throws Exception
	 */
	@When("{string} initiates the definition of a service combo {string} with main service {string}, services {string} and mandatory setting {string}")
	public void initiates_the_definition_of_a_service_combo_with_main_service_services_and_mandatory_setting(
			String username, String comboname, String mainservice, String services, String mandatory) {
		// carshop, username, name, mainservice, services, mandatory setting
		try {
			CarShopController.DefineServiceCombo(carshop, username, comboname, mainservice, services, mandatory);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 */
	@Then("the service combo {string} shall exist in the system")
	public void the_service_combo_shall_exist_in_the_system(String comboname) {
		if (getComboByName(carshop, comboname) != null) {
			assertTrue(comboname.equals(getComboByName(carshop, comboname).getName()));
		} else {
			assertTrue(true == false);
		}

	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param string2
	 * @param string3
	 */
	@Then("the service combo {string} shall contain the services {string} with mandatory setting {string}")
	public void the_service_combo_shall_contain_the_services_with_mandatory_setting(String comboname, String services,
			String mandsetting) {
		// combo we're comparing with
		ServiceCombo combo = getComboByName(carshop, comboname);

		// expected services and mandatory
		String[] service = services.split(",");
		String[] mand = mandsetting.split(",");

		// contain the state of out system
		String[] curserv = new String[service.length];
		Boolean[] curmand = new Boolean[service.length];

		// compare the services
		for (int j = 0; j < service.length; j++) {
			curserv[j] = combo.getServices().get(j).getService().getName();
			curmand[j] = combo.getServices().get(j).getMandatory();
			assertEquals(curserv[j], service[j]);
			assertTrue(Boolean.parseBoolean(mand[j]) == curmand[j]);
		}
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param string2
	 * @throws Exception
	 */
	@Then("the main service of the service combo {string} shall be {string}")
	public void the_main_service_of_the_service_combo_shall_be(String comboname, String mainservice) {
		if (getComboByName(carshop, comboname).getMainService().getService() != null) {
			assertEquals(mainservice, getComboByName(carshop, comboname).getMainService().getService().getName());
		} else
			assertTrue(true == false);
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param string2
	 */
	@Then("the service {string} in service combo {string} shall be mandatory")
	public void the_service_in_service_combo_shall_be_mandatory(String servicename, String comboname) {// fix exception
		if (getserviceFromACombo(carshop, comboname, servicename) != null) {
			ComboItem item = getserviceFromACombo(carshop, comboname, servicename);
			assertEquals(item.getMandatory(), true);
		} else {// to stop getting a NullPointerException
			assertTrue(true == false);
		}
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 */
	@Then("the number of service combos in the system shall be {string}")
	public void the_number_of_service_combos_in_the_system_shall_be(String expectednumber) {// check if exception
		assertEquals(Integer.parseInt(expectednumber), getnumCombos(carshop));
	}

	/**
	 * @author Aly Mohamed
	 * @param dataTable
	 */
	@Given("the following service combos exist in the system:")
	public void the_following_service_combos_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {// works

		// iterate through the datatable rows
		for (int i = 1; i < dataTable.height(); i++) {

			// service combo to be created
			ServiceCombo combo = new ServiceCombo(dataTable.cell(i, 0), carshop);

			// array of services and array of mandatory setting for each service
			String[] service = dataTable.cell(i, 2).split(",");
			String[] mand = dataTable.cell(i, 3).split(",");

			// iterate through services in the datatable
			for (int j = 0; j < service.length; j++) {
				// if the service is the main service add it to the system and set as main
				// service
				if (service[j].equals(dataTable.cell(i, 1))) {
					ComboItem c = new ComboItem(true, getServiceByName(carshop, dataTable.cell(i, 1)), combo);
					combo.setMainService(c);
				}
				// else add the service as a normal service
				else
					combo.addService(Boolean.parseBoolean(mand[i]), getServiceByName(carshop, service[j]));
			}
		}
	}

	/**
	 * @author Aly Mohamed
	 * @param expectederr
	 */
	@Then("an error message with content {string} shall be raised")
	public void an_error_message_with_content_shall_be_raised(String expectederr) {
		assertTrue(error.contains(expectederr));
	}

	/**
	 * @author Aly Mohamed
	 */
	@Then("the service combo {string} shall not exist in the system")
	public void the_service_combo_shall_not_exist_in_the_system(String string) {
		assertTrue(getComboByName(carshop, string) == null);
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param dataTable
	 */
	@Then("the service combo {string} shall preserve the following properties:")
	public void the_service_combo_shall_preserve_the_following_properties(String string,
			io.cucumber.datatable.DataTable dataTable) {// should be working
		// combo that we are looking for
		ServiceCombo combo = getComboByName(carshop, string);

		// iterate on the datatable height
		for (int i = 1; i < dataTable.height(); i++) {

			assertEquals(combo.getName(), dataTable.cell(i, 0));
			assertEquals(combo.getMainService().getService().getName(), dataTable.cell(i, 1));

			// contain expected services and mandatory setting
			String[] services = dataTable.cell(i, 2).split(",");
			String[] mand = dataTable.cell(i, 3).split(",");

			// services and mandatory setting in our system
			String[] curserv = new String[services.length];
			Boolean[] curmand = new Boolean[services.length];

			// check if expected is the same as current
			for (int j = 0; j < services.length; j++) {
				curserv[j] = combo.getServices().get(j).getService().getName();
				curmand[j] = combo.getServices().get(j).getMandatory();
				assertTrue(curserv[j].equals(services[j]) && Boolean.parseBoolean(mand[j]) == curmand[j]);
			}
		}
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 */
	@Given("the user with username {string} is logged in")
	public void the_user_with_username_is_logged_in(String string) {
		boolean loggedIn = false;

		// is a user loggedin?
		for (int i = 0; i < carshop.getCustomers().size(); i++) {
			if (carshop.getCustomers().get(i).getUsername().equals(string)) {
				CarShopApplication.setCurrentUser(carshop.getCustomers().get(i));
				loggedIn = true;
			}
		}

		// is a technician loggedin?
		if (!loggedIn) {
			for (int j = 0; j < carshop.getTechnicians().size(); j++) {
				if (carshop.getTechnicians().get(j).getUsername().equals(string)) {
					CarShopApplication.setCurrentUser(carshop.getTechnicians().get(j));
					loggedIn = true;
				}
			}
		}

		// is the owner loggedin?
		if (!loggedIn) {
			if (carshop.getOwner().getUsername().equals(string)) {
				CarShopApplication.setCurrentUser(carshop.getOwner());
			}
		}
	}

	// Define ServiceCombo DONE

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @param string6
	 * @throws Exception
	 */
	@When("{string} initiates the update of service combo {string} to name {string}, main service {string} and services {string} and mandatory setting {string}")
	public void initiates_the_update_of_service_combo_to_name_main_service_and_services_and_mandatory_setting(
			String username, String oldname, String newname, String mainservice, String services, String mandatory) {
		// username oldName newName mainService Services ismandatory carshop oldname
		// newname mainservice allservices

		try {
			CarShopController.updateServiceCombo(carshop, username, oldname, newname, mainservice, services, mandatory);
			combooldname = oldname;
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 * @param string2
	 */

	@Then("the service combo {string} shall be updated to name {string}")
	public void the_service_combo_shall_be_updated_to_name(String comboname, String string2) {
		assertEquals(comboname, combooldname);
		assertTrue(getComboByName(carshop, string2) != null);
	}

	/**
	 * @author Aly Mohamed
	 */
	@After
	public void tearDown() {
		CarShopApplication.setCurrentUser(null);
		carshop.delete();
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Helper Methods //
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @author Aly Mohamed
	 * @param carshop
	 * @return
	 */
	private static ArrayList<Service> getAllService(CarShop carshop) {// checked

		ArrayList<Service> services = new ArrayList<Service>();
		for (int i = 0; i < carshop.getBookableServices().size(); i++) {
			if (carshop.getBookableServices().get(i) instanceof Service)
				services.add((Service) carshop.getBookableServices().get(i));
		}
		return services;
	}

	/**
	 * Author Aly Mohamed
	 * 
	 * @param carshop
	 * @param servicename
	 * @return
	 */
	private static Service getServiceByName(CarShop carshop, String servicename) {// checked
		for (int i = 0; i < getAllService(carshop).size(); i++) {
			if (getAllService(carshop).get(i).getName().equals(servicename))
				return getAllService(carshop).get(i);
		}
		return null;
	}

	/**
	 * returns one comboItem out of a Combo Author Aly Mohamed
	 * 
	 * @param carshop
	 * @param combo
	 * @param service
	 * @return
	 */
	private static ComboItem getserviceFromACombo(CarShop carshop, String combo, String service) {
		ServiceCombo c = getComboByName(carshop, combo);
		for (int i = 0; i < c.getServices().size(); i++) {
			if (c.getService(i).getService().getName().equals(service)) {
				return c.getService(i);
			}
		}
		return null;
	}

	/**
	 * returns an ArrayList of the service combos in the system Author Aly Mohamed
	 * 
	 * @param carshop
	 * @return
	 */
	private static ArrayList<ServiceCombo> getAllCombo(CarShop carshop) {

		ArrayList<ServiceCombo> combos = new ArrayList<ServiceCombo>();
		for (int i = 0; i < carshop.getBookableServices().size(); i++) {
			if (carshop.getBookableServices().get(i) instanceof ServiceCombo)
				combos.add((ServiceCombo) carshop.getBookableServices().get(i));
		}
		return combos;
	}

	/**
	 * Author Aly Mohamed
	 * 
	 * @param carshop
	 * @return
	 */
	private static int getnumCombos(CarShop carshop) {
		int count = 0;
		// ArrayList<ServiceCombo> combos = new ArrayList<ServiceCombo>();
		for (int i = 0; i < carshop.getBookableServices().size(); i++) {
			if (carshop.getBookableServices().get(i) instanceof ServiceCombo)
				count++;
		}
		return count;
	}

	/**
	 * returns a serviceCombo by gving as input its name Author Aly Mohamed
	 * 
	 * @param carshop
	 * @param comboname
	 * @return
	 */
	private static ServiceCombo getComboByName(CarShop carshop, String comboname) {
		for (int i = 0; i < getAllCombo(carshop).size(); i++) {
			if (getAllCombo(carshop).get(i).getName().equals(comboname)) {
				return getAllCombo(carshop).get(i);
			}
		}

		return null;
	}

	/**
	 * Author Aly Mohamed iterate through technicians, split their names, if
	 * corresponding with service name, return their garage
	 * 
	 * @param carshop
	 * @param service
	 * @return
	 */
	private static Garage getServiceGarage(CarShop carshop, String service) {

		String[] servicesplit = service.split("-");
		for (int i = 0; i < carshop.getTechnicians().size(); i++) {
			String[] techsplit = carshop.getTechnicians().get(i).getUsername().split("-");
			if (techsplit[0].equalsIgnoreCase(servicesplit[0])) {
				return carshop.getTechnicians().get(i).getGarage();
			}
		}
		return null;
	}

	/**
	 * Aziz ' s part
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@When("the user tries to log in with username {string} and password {string}")
	public void the_user_tries_to_log_in_with_username_and_password(String string, String string2) {
		try {
			CarShopController.userLogin(string, string2);
		} catch (InvalidInputException e) {
			error += e.getLocalizedMessage();
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the user should be successfully logged in")
	public void the_user_should_be_successfully_logged_in() {
		assertNotNull(CarShopApplication.getCurrentUser());
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the user should not be logged in")
	public void the_user_should_not_be_logged_in() {
		assertNull(CarShopApplication.getCurrentUser());
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the account shall have username {string}, password {string} and technician type {string}")
	public void the_account_shall_have_username_password_and_technician_type(String string, String string2,
			String string3) {
		assertEquals(User.getWithUsername(string).getPassword(), string2);
		assertTrue(User.getWithUsername(string) instanceof Technician);
		if (User.getWithUsername(string) instanceof Technician) {
			Technician tech = (Technician) User.getWithUsername(string);
			assertEquals(tech.getType().toString(), string3);
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("a new account shall be created")
	public void a_new_account_shall_be_created() {
		assertEquals(numOfAccounts + 1, getNumberOfAccounts());
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the corresponding garage for the technician shall be created")
	public void the_corresponding_garage_for_the_technician_shall_be_created() {
		assertTrue(CarShopApplication.getCurrentUser() instanceof Technician);
		if (CarShopApplication.getCurrentUser() instanceof Technician) {
			Technician tech = (Technician) CarShopApplication.getCurrentUser();
			assertTrue(tech.hasGarage());
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the garage should have the same opening hours as the business")
	public void the_garage_should_have_the_same_opening_hours_as_the_business() {
		assertTrue(CarShopApplication.getCurrentUser() instanceof Technician);
		if (CarShopApplication.getCurrentUser() instanceof Technician) {
			Technician tech = (Technician) CarShopApplication.getCurrentUser();
			assertTrue(tech.hasGarage());
			Garage garage = tech.getGarage();
			for (int i = 0; i < garage.getBusinessHours().size(); i++) {
				BusinessHour h1 = garage.getBusinessHour(i);
				BusinessHour h2 = tech.getCarShop().getBusiness().getBusinessHour(i);

				assertEquals(h1.getDayOfWeek(), h2.getDayOfWeek());
				assertEquals(h1.getStartTime(), h2.getStartTime());
				assertEquals(h1.getEndTime(), h2.getEndTime());
			}
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the user shall be successfully logged in")
	public void the_user_shall_be_successfully_logged_in() {
		assertNotNull(CarShopApplication.getCurrentUser());
	}

	/////////////// GARAGE OPENING HOURS TESTS /////////////////////

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Given("a business exists with the following information:")
	public void a_business_exists_with_the_following_information(io.cucumber.datatable.DataTable dataTable) {
		String name = dataTable.cell(1, 0);
		String address = dataTable.cell(1, 1);
		String phoneNumber = dataTable.cell(1, 2);
		String email = dataTable.cell(1, 3);

		if (carshop.hasBusiness()) {
			carshop.getBusiness().setName(name);
			carshop.getBusiness().setAddress(address);
			carshop.getBusiness().setPhoneNumber(phoneNumber);
			carshop.getBusiness().setEmail(email);
		} else
			new Business(name, address, phoneNumber, email, carshop);
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Given("the business has the following opening hours:")
	public void the_business_has_the_following_opening_hours(io.cucumber.datatable.DataTable dataTable) {
		Business business = carshop.getBusiness();
		for (int i = 1; i < dataTable.height(); i++)
			business.addBusinessHour(
					setupBusinessHour(dataTable.cell(i, 0), dataTable.cell(i, 1), dataTable.cell(i, 2)));
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@When("the user tries to add new business hours on {string} from {string} to {string} to garage belonging to the technician with type {string}")
	public void the_user_tries_to_add_new_business_hours_on_from_to_to_garage_belonging_to_the_technician_with_type(
			String string, String string2, String string3, String string4) {
		DayOfWeek day = DayOfWeek.valueOf(string);
		Time start = Time.valueOf(string2 + ":00");
		Time end = Time.valueOf(string3 + ":00");

		try {
			CarShopController.addGarageHours(day, start, end, getTechnicianByType(string4));
		} catch (InvalidInputException e) {
			error += e.getLocalizedMessage();
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the garage belonging to the technician with type {string} should have opening hours on {string} from {string} to {string}")
	public void the_garage_belonging_to_the_technician_with_type_should_have_opening_hours_on_from_to(String string,
			String string2, String string3, String string4) {

		DayOfWeek day = DayOfWeek.valueOf(string2);
		Time start = Time.valueOf(string3 + ":00");
		Time end = Time.valueOf(string4 + ":00");

		assertTrue(hasHours(getTechnicianByType(string).getGarage(), day, start, end));
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Given("there are opening hours on {string} from {string} to {string} for garage belonging to the technician with type {string}")
	public void there_are_opening_hours_on_from_to_for_garage_belonging_to_the_technician_with_type(String string,
			String string2, String string3, String string4) {
		Technician tech = getTechnicianByType(string4);
		if (tech != null) {
			tech.getGarage().addBusinessHour(setupBusinessHour(string, string2, string3));
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@When("the user tries to remove opening hours on {string} from {string} to {string} to garage belonging to the technician with type {string}")
	public void the_user_tries_to_remove_opening_hours_on_from_to_to_garage_belonging_to_the_technician_with_type(
			String string, String string2, String string3, String string4) {
		DayOfWeek day = DayOfWeek.valueOf(string);
		Time start = Time.valueOf(string2 + ":00");
		Time end = Time.valueOf(string3 + ":00");

		try {
			CarShopController.removeGarageHours(day, start, end, getTechnicianByType(string4));
		} catch (InvalidInputException e) {
			error += e.getLocalizedMessage();
		}
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 */
	@Then("the garage belonging to the technician with type {string} should not have opening hours on {string} from {string} to {string}")
	public void the_garage_belonging_to_the_technician_with_type_should_not_have_opening_hours_on_from_to(String string,
			String string2, String string3, String string4) {

		DayOfWeek day = DayOfWeek.valueOf(string2);
		Time start = Time.valueOf(string3 + ":00");
		Time end = Time.valueOf(string4 + ":00");

		assertFalse(hasHours(getTechnicianByType(string).getGarage(), day, start, end));
	}

	/// HELPER METHODS

	/**
	 * Checks if the given garage has the given business hours
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param garage
	 * @param day
	 * @param start
	 * @param end
	 * @return true if the garage has the given business hours, false otherwise
	 */
	private boolean hasHours(Garage garage, DayOfWeek day, Time start, Time end) {
		for (BusinessHour hour : garage.getBusinessHours())
			if (hour.getDayOfWeek().equals(day) && hour.getStartTime().equals(start) && hour.getEndTime().equals(end))
				return true;
		return false;
	}

	/**
	 * Creates a business hour from given info
	 * 
	 * @author Abd-El-Aziz Zayed
	 * @param dayStr
	 * @param startStr
	 * @param endStr
	 * @return returns the created business hour
	 */
	private BusinessHour setupBusinessHour(String dayStr, String startStr, String endStr) {
		DayOfWeek day = DayOfWeek.valueOf(dayStr);
		Time start = Time.valueOf(startStr + ":00");
		Time end = Time.valueOf(endStr + ":00");

		return new BusinessHour(day, start, end, carshop);
	}

	/**
	 * @author Abd-El-Aziz Zayed
	 * @return the total number of accounts in the system
	 */
	private int getNumberOfAccounts() {
		return carshop.getCustomers().size() + carshop.getTechnicians().size() + (carshop.hasOwner() ? 1 : 0);
	}

	/**
	 * Find the technician from the given type
	 * 
	 * @param typeStr
	 * @return the technician of type typeStr
	 */
	private Technician getTechnicianByType(String typeStr) {
		TechnicianType type = TechnicianType.valueOf(typeStr);

		for (Technician tech : carshop.getTechnicians())
			if (tech.getType().equals(type))
				return tech;
		return null;
	}

	/**
	 * Simo part..
	 */
	// /**
	// * @author simobenkirane
	// * @param username
	// */
	// @Given("the Owner with username {string} is logged in")
	// public void the_owner_with_username_is_logged_in(String username) {//check
	// if(CarShopApplication.getCurrentUser()==null) {
	// CarShopApplication.setCurrentUser(User.getWithUsername(username));
	// }
	// }

	/**
	 * @author simobenkirane
	 * @param username
	 * @param servicename
	 * @param duration
	 * @param techtype
	 * @throws Exception
	 */
	@When("{string} initiates the addition of the service {string} with duration {string} belonging to the garage of {string} technician")
	public void initiates_the_addition_of_the_service_with_duration_belonging_to_the_garage_of_technician(
			String username, String servicename, String duration, String techtype) throws Exception {
		// Write code here that turns the phrase above into concrete actions
		// System.out.println("---pp----------WHENNN-----------addService:"+
		// carshop.getBookableServices());
		try {

			CarShopController.addService(carshop, username, servicename, duration, techtype);
			// System.out.println("-------------WHENNN-----------addService:"+
			// carshop.getBookableServices());
		} catch (Exception e) {
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

		assertEquals(servicename, getService(carshop, servicename).getName());

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
			// System.out.println("getService-----------: "+ b);
			if (b.getName().equals(servicename) && !(b instanceof ServiceCombo)) {
				return (Service) b;
			}
		}

		return null;

	}

	// /**
	// * @author simobenkirane
	// * @param username
	// */
	// @Given("the user with username {string} is logged in")
	// public void the_user_with_username_is_logged_in(String username) {
	// // Write code here that turns the phrase above into concrete actions
	// User user = User.getWithUsername(username);
	// CarShopApplication.setCurrentUser(user);
	// }

	/**
	 * @author simobenkirane
	 * @param servicename
	 * @param techtype
	 */
	@Then("the service {string} shall belong to the garage of {string} technician")
	public void the_service_shall_belong_to_the_garage_of_technician(String servicename, String techtype) {
		// Write code here that turns the phrase above into concrete actions
		// Service s = getService(carshop,string);
		Service s = (Service) Service.getWithName(servicename);

		// System.out.println("then..belong---->" + carshop.getBookableServices());

		assertTrue(TechnicianType.valueOf(techtype).equals(s.getGarage().getTechnician().getType()));

	}

	/**
	 * @author simobenkirane
	 * @param numServices
	 */
	@Then("the number of services in the system shall be {string}")
	public void the_number_of_services_in_the_system_shall_be(String numServices) {
		// Write code here that turns the phrase above into concrete actions

		// System.out.println(numServices +" " + getAllServices(carshop).size() );
		assertTrue(Integer.parseInt(numServices) == (getAllServices(carshop).size()));

	}

	/**
	 * @author simobenkirane
	 * @param carshop
	 * @return
	 */
	private static ArrayList<Service> getAllServices(CarShop carshop) {

		ArrayList<Service> services = new ArrayList<Service>();
		for (int i = 0; i < carshop.getBookableServices().size(); i++) {
			if (carshop.getBookableServices().get(i) instanceof Service)
				services.add((Service) carshop.getBookableServices().get(i));
		}
		return services;
	}

	// /**
	// * @author simobenkirane
	// * @param errorMessage
	// */
	// @Then("an error message with content {string} shall be raised")
	// public void an_error_message_with_content_shall_be_raised(String
	// errorMessage) {
	//
	// //System.out.println(error + ":--:"+ errorMessage);
	// assertTrue(error.contains(errorMessage));
	// }

	/**
	 * @author simobenkirane
	 * @param servicename
	 */
	@Then("the service {string} shall not exist in the system")
	public void the_service_shall_not_exist_in_the_system(String servicename) {

		// should i remove it from its corresponding garage too?
		assertTrue(getService(carshop, servicename) == null);

	}

	/**
	 * @author simobenkirane
	 * @param oldServicename
	 * @param serviceName
	 * @param duration
	 */
	@Then("the service {string} shall be updated to name {string}, duration {string}")
	public void the_service_shall_be_updated_to_name_duration(String oldServicename, String serviceName,
			String duration) {
		// Write code here that turns the phrase above into concrete actions

		assertEquals(oldservicename, oldServicename);

		Service s = getService(carshop, serviceName);

		// assertTrue(getService(carshop,string) == null);
		assertEquals(serviceName, s.getName());

		// System.out.println(Integer.parseInt(duration) + "<-->" + s.getDuration());
		assertTrue(Integer.parseInt(duration) == s.getDuration());

	}

	// /**
	// * @author simobenkirane
	// * @param dataTable
	// */
	// @Given("the following customers exist in the system:")
	// public void
	// the_following_customers_exist_in_the_system(io.cucumber.datatable.DataTable
	// dataTable) {
	// // Write code here that turns the phrase above into concrete actions
	// List<Map<String, String>> rows = dataTable.asMaps(String.class,
	// String.class);
	//
	// for (Map<String, String> columns : rows) {
	//
	// String username = columns.get("username");
	// String password = columns.get("password");
	//
	//
	// if(Customer.hasWithUsername(username) != true) {
	// Customer c = new Customer(username, password, carshop);
	// carshop.addCustomer(c);
	// }
	// else {
	// carshop.addCustomer((Customer)Customer.getWithUsername(username));
	// }
	//
	// }
	//
	//
	// }

	// /**
	// * @author simobenkirane
	// * @param dataTable
	// */
	// @Given("the following services exist in the system:")
	// public void
	// the_following_services_exist_in_the_system(io.cucumber.datatable.DataTable
	// dataTable) {
	//
	// List<Map<String, String>> rows = dataTable.asMaps(String.class,
	// String.class);
	//
	// //System.out.println("testiiiiii");
	// for (Map<String, String> columns : rows) {
	// String name = columns.get("name");
	// int duration = Integer.parseInt(columns.get("duration"));
	// Garage garage = getGarage(carshop,columns.get("garage"));
	// //System.out.println("---yoyo" + name + duration + (garage!=null) +
	// carshop.getBookableServices().size() + carshop.getTechnicians().size());
	//
	// if(Service.hasWithName(name) != true) {
	// carshop.addBookableService(new Service(name,carshop, duration,garage));
	// }
	// else {
	// Service.getWithName(name).delete();
	// carshop.addBookableService(new Service(name,carshop, duration,garage));
	// }
	// }
	//
	// }

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
	 * @param username
	 * @param oldServicename
	 * @param newservicename
	 * @param duration
	 * @param techtype
	 */
	@When("{string} initiates the update of the service {string} to name {string}, duration {string}, belonging to the garage of {string} technician")
	public void initiates_the_update_of_the_service_to_name_duration_belonging_to_the_garage_of_technician(
			String username, String oldServicename, String newservicename, String duration, String techtype) {

		try {
			// System.out.println("initiateUpdate of " + string + "to" + string2 + string3 +
			// string4 + string5 );
			oldservicename = oldServicename;
			CarShopController.updateService(carshop, username, oldServicename, newservicename, duration, techtype);
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

		List<Technician> techies = carshop.getTechnicians();

		for (int i = 0; i < techies.size(); i++) {

			if (techies.get(i).getUsername().equals(technician)) {
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
	public void the_service_shall_still_preserve_the_following_properties(String string,
			io.cucumber.datatable.DataTable dataTable) {

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

	/**
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

	String test;
	String[] businessInformation;

	@Given("the business has a business hour on {string} with start time {string} and end time {string}")
	public void the_business_has_a_business_hour_on_with_start_time_and_end_time(String string, String string2,
			String string3) {
		System.out.print(test);
		// works , needs test

		Business currentBusiness = CarShopApplication.getCarShop().getBusiness();
		boolean hasSuchHour = false;
		if (currentBusiness.getBusinessHours().size() != 0) {
			for (BusinessHour hour : currentBusiness.getBusinessHours()) {
				if (hour.getDayOfWeek().equals(DayOfWeek.valueOf(string))
						&& hour.getStartTime().equals(Time.valueOf(string2 + ":00"))
						&& hour.getEndTime().equals(Time.valueOf(string3 + ":00"))) {
					hasSuchHour = true;
					return;
				}
			}
		}
		if (hasSuchHour == false) {
			ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek dayOfWeek = DayOfWeek.valueOf(string);
			Time startTime = Time.valueOf(string2 + ":00");
			Time endTime = Time.valueOf(string3 + ":00");
			BusinessHour nHour = new BusinessHour(dayOfWeek, startTime, endTime, CarShopApplication.getCarShop());
			currentBusiness.addBusinessHour(nHour);
		}
	}

	@Given("a {string} time slot exists with start time {string} at {string} and end time {string} at {string}")
	public void a_time_slot_exists_with_start_time_at_and_end_time_at(String string, String string2, String string3,
			String string4, String string5) throws ParseException {

		Business currentBusiness = CarShopApplication.getCarShop().getBusiness();
		List<TimeSlot> temp;
		Date startDate = Date.valueOf(string2);
		Time startTime = Time.valueOf(string3 + ":00");
		Date endDate = Date.valueOf(string4);
		Time endTime = Time.valueOf(string5 + ":00");

		if (string.equals("vacation")) {
			temp = currentBusiness.getVacations();
		} else {
			temp = currentBusiness.getHolidays();
		}
		if (temp == null) {
			TimeSlot slot = new TimeSlot(startDate, startTime, endDate, endTime, CarShopApplication.getCarShop());
			temp.add(slot);
			if (string.equals("vacation")) {
				currentBusiness.addVacation(slot);
			} else {
				currentBusiness.addHoliday(slot);
			}
			return;
		}
		boolean exist = false;

		for (TimeSlot slot : temp) {
			if (slot.getStartDate().equals(startDate) && slot.getStartTime().equals(startTime)
					&& slot.getEndDate().equals(endDate) && slot.getEndTime().equals(endTime)) {
				exist = true;
			}
		}
		if (exist == false) {
			TimeSlot slot = new TimeSlot(startDate, startTime, endDate, endTime, CarShopApplication.getCarShop());
			if (string.equals("vacation")) {
				currentBusiness.addVacation(slot);
			} else {
				currentBusiness.addHoliday(slot);
			}
		}
	}

	@When("the user tries to update the business information with new {string} and {string} and {string} and {string}")
	public void the_user_tries_to_update_the_business_information_with_new_and_and_and(String string, String string2,
			String string3, String string4) {
		// Write code here that turns the phrase above into concrete actions
		// code
		try {
			CarShopController.UpdateBusinessInformation(string, string2, string3, string4);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

	@Then("the business information shall {string} updated with new {string} and {string} and {string} and {string}")
	public void the_business_information_shall_updated_with_new_and_and_and(String string, String string2,
			String string3, String string4, String string5) {
		// Write code here that turns the phrase above into concrete actions
		System.out.println("MyERROR: " + error + "ERROR: " + string + " " + string2 + " " + string3 + " ");
		Business aBusiness = CarShopApplication.getCarShop().getBusiness();
		assertEquals(aBusiness.getName(), string2);
		assertEquals(aBusiness.getAddress(), string3);
		assertEquals(aBusiness.getPhoneNumber(), string4);
		assertEquals(aBusiness.getEmail(), string5);
		assertEquals(string, CarShopController.returnResult());

	}

	@Then("an error message {string} shall {string} raised")
	public void an_error_message_shall_raised(String string, String string2) {
		System.out.println("MyERROR: " + error + "+ " + "MyreturnResult" + CarShopController.returnResult()
				+ " Real ERROR: " + string + "  " + string2);
		assertEquals(string, error);
		assertEquals(string2, CarShopController.returnResultError());
	}

	@When("the user tries to add a new business hour on {string} with start time {string} and end time {string}")
	public void the_user_tries_to_add_a_new_business_hour_on_with_start_time_and_end_time(String string, String string2,
			String string3) {
		try {
			CarShopController.AddBusinessHours(ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek.valueOf(string),
					CarShopController.StringToTime(string2), CarShopController.StringToTime(string3));
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	@Then("a new business hour shall {string} created")
	public void a_new_business_hour_shall_created(String string) {
		// Write code here that turns the phrase above into concrete actions
		System.out.println("MyERROR: " + CarShopController.returnResult() + " RealERROR: " + string);
		assertEquals(string, CarShopController.returnResult());
	}

	@When("the user tries to change the business hour {string} at {string} to be on {string} starting at {string} and ending at {string}")
	public void the_user_tries_to_change_the_business_hour_at_to_be_on_starting_at_and_ending_at(String string,
			String string2, String string3, String string4, String string5) {
		// Write code here that turns the phrase above into concrete actions
		try {
			CarShopController.UpdateBusinessHours(ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek.valueOf(string),
					CarShopController.StringToTime(string2), DayOfWeek.valueOf(string3),
					CarShopController.StringToTime(string4), CarShopController.StringToTime(string5));

		} catch (Exception e) {
			;
			error += e.getMessage();
			errorCntr++;
		}
	}

	@Then("the business hour shall {string} be updated")
	public void the_business_hour_shall_be_updated(String string) {
		// Write code here that turns the phrase above into concrete actions
		System.out.println("MyERROR: " + CarShopController.returnResult() + " Real ERROR: ");
		assertEquals(string, CarShopController.returnResult());
	}

	@When("the user tries to remove the business hour starting {string} at {string}")
	public void the_user_tries_to_remove_the_business_hour_starting_at(String string, String string2) {
		// done

		try {
			CarShopController.RemoveExistingBusinessHour(
					ca.mcgill.ecse.carshop.model.BusinessHour.DayOfWeek.valueOf(string),
					CarShopController.StringToTime(string2));
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

	@Then("the business hour starting {string} at {string} shall {string} exist")
	public void the_business_hour_starting_at_shall_exist(String string, String string2, String string3) {
		// Write code here that turns the phrase above into concrete actions
		// done
		// System.out.println("MyERROR: " + error + "+ " +
		// CarShopController.returnResult() + " Real ERROR: " + string+ " " +string2 );
		assertEquals(string3, CarShopController.returnResult());

	}

	@Then("an error message {string} shall {string} be raised")
	public void an_error_message_shall_be_raised(String string, String string2) {
		// System.out.println("MyERROR: " + error + "+ " +
		// CarShopController.returnResult() + " Real ERROR: " + string+ " " +string2 );

		assertEquals(string2, CarShopController.returnResult());
		if (string2.equals("not") == false) {
			assertEquals(string, error);
		}

	}

	@When("the user tries to add a new {string} with start date {string} at {string} and end date {string} at {string}")
	public void the_user_tries_to_add_a_new_with_start_date_at_and_end_date_at(String string, String string2,
			String string3, String string4, String string5) {
		// change type before passing it in
		try {
			CarShopController.AddTimeSlot(string, CarShopController.stringToDate(string2),
					CarShopController.StringToTime(string3), CarShopController.stringToDate(string4),
					CarShopController.StringToTime(string5));
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	@Then("a new {string} shall {string} be added with start date {string} at {string} and end date {string} at {string}")
	public void a_new_shall_be_added_with_start_date_at_and_end_date_at(String string, String string2, String string3,
			String string4, String string5, String string6) {
		// Write code here that turns the phrase above into concrete actions

		System.out.println("ERROR: " + error + " " + CarShopController.returnResult() + " ERROR: " + string2);
		TimeSlot tempTimeSlot = null;
		Business newbusiness = CarShopApplication.getCarShop().getBusiness();
		List<TimeSlot> timeSlots;
		Date staDate = Date.valueOf(string3);
		Time staTime = Time.valueOf(string4 + ":00");
		Date enDate = Date.valueOf(string5);
		Time enTime = Time.valueOf(string6 + ":00");
		if (string.equals("vacation")) {

			timeSlots = newbusiness.getVacations();
		} else {
			timeSlots = newbusiness.getHolidays();
		}
		boolean exist = false;

		for (TimeSlot slot : timeSlots) {
			if (slot.getStartDate().equals(staDate) && slot.getStartTime().equals(staTime)
					&& slot.getEndDate().equals(enDate) && slot.getEndTime().equals(enTime)) {
				exist = true;
				tempTimeSlot = slot;
				break;
			}
		}
		if (exist == true) {
			assertEquals(string, CarShopController.returnStringType());
			assertEquals(string2, CarShopController.returnResult());
			assertEquals(Date.valueOf(string3), tempTimeSlot.getStartDate());
			assertEquals(Time.valueOf(string4 + ":00"), tempTimeSlot.getStartTime());
			assertEquals(Date.valueOf(string5), tempTimeSlot.getEndDate());
			assertEquals(Time.valueOf(string6 + ":00"), tempTimeSlot.getEndTime());
		}
	}

	@When("the user tries to change the {string} on {string} at {string} to be with start date {string} at {string} and end date {string} at {string}")
	public void the_user_tries_to_change_the_on_at_to_be_with_start_date_at_and_end_date_at(String string,
			String string2, String string3, String string4, String string5, String string6, String string7) {

		// done
		try {

			if (string.equals("vacation")) {
				CarShopController.UpdateVacation(string, CarShopController.stringToDate(string2),
						CarShopController.StringToTime(string3), CarShopController.stringToDate(string4),
						CarShopController.StringToTime(string5), CarShopController.stringToDate(string6),
						CarShopController.StringToTime(string7));
			} else {
				CarShopController.UpdateHoliday(string, CarShopController.stringToDate(string2),
						CarShopController.StringToTime(string3), CarShopController.stringToDate(string4),
						CarShopController.StringToTime(string5), CarShopController.stringToDate(string6),
						CarShopController.StringToTime(string7));
			}
		} catch (Exception e) {

			error += e.getMessage();
			errorCntr++;
		}
	}

	@Then("the {string} shall {string} updated with start date {string} at {string} and end date {string} at {string}")
	public void the_shall_updated_with_start_date_at_and_end_date_at(String string, String string2, String string3,
			String string4, String string5, String string6) {
		// Write code here that turns the phrase above into concrete actions
		// done
		// System.out.println("ERROR: " + error + " ERROR: " +string2 );
		boolean test = false;
		TimeSlot tempTimeSlot = null;
		Business currentBusiness = CarShopApplication.getCarShop().getBusiness();
		List<TimeSlot> timeSlots;
		Date startDate = Date.valueOf(string3);
		Time starTime = Time.valueOf(string4 + ":00");
		Date endDate = Date.valueOf(string5);
		Time endTime = Time.valueOf(string6 + ":00");
		if (string.equals("vacation")) {
			timeSlots = currentBusiness.getVacations();
		} else {
			timeSlots = currentBusiness.getHolidays();
		}
		for (TimeSlot slot : timeSlots) {
			if (slot.getStartDate().equals(startDate) && slot.getStartTime().equals(starTime)
					&& slot.getEndDate().equals(endDate) && slot.getEndTime().equals(endTime)) {
				tempTimeSlot = slot;
				test = true;
				break;
			}
		}
		if (test == true) {
			assertEquals(string, CarShopController.returnStringType());
			assertEquals(string2, CarShopController.returnResult());
			assertEquals(Date.valueOf(string3), tempTimeSlot.getStartDate());
			assertEquals(Time.valueOf(string4 + ":00"), tempTimeSlot.getStartTime());
			assertEquals(Date.valueOf(string5), tempTimeSlot.getEndDate());
			assertEquals(Time.valueOf(string6 + ":00"), tempTimeSlot.getEndTime());

		}
	}

	@When("the user tries to remove an existing {string} with start date {string} at {string} and end date {string} at {string}")
	public void the_user_tries_to_remove_an_existing_with_start_date_at_and_end_date_at(String string, String string2,
			String string3, String string4, String string5) {

		// Done
		try {
			CarShopController.RemoveExistingTimeSlot(string, CarShopController.stringToDate(string2),
					CarShopController.StringToTime(string3), CarShopController.stringToDate(string4),
					CarShopController.StringToTime(string5));

		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

	@Then("the {string} with start date {string} at {string} shall {string} exist")
	public void the_with_start_date_at_shall_exist(String string, String string2, String string3, String string4) {

		// done
		// System.out.println("MyERROR: " + CarShopController.returnResult() + " Real
		// ERROR: " + string4 );
		assertEquals(string4, CarShopController.returnResult());

	}

	@Given("no business exists")
	public void no_business_exists() {
		// Done
		// if there is a business, set it to null
		if (carshop.getBusiness() != null) {
			carshop.setBusiness(null);
		}
	}

	@When("the user tries to set up the business information with new {string} and {string} and {string} and {string}")
	public void the_user_tries_to_set_up_the_business_information_with_new_and_and_and(String string, String string2,
			String string3, String string4) {
		// Write code here that turns the phrase above into concrete actions
		try {
			CarShopController.SetUpBusinessInformation(string, string2, string3, string4);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	@Then("a new business with new {string} and {string} and {string} and {string} shall {string} created")
	public void a_new_business_with_new_and_and_and_shall_created(String string, String string2, String string3,
			String string4, String string5) {

		// done
		Business currentBusiness = CarShopApplication.getCarShop().getBusiness();
		String name = "", address = "", phoneNumber = "", email = "";
		if (currentBusiness != null) {
			name = currentBusiness.getName();
			address = currentBusiness.getAddress();
			phoneNumber = currentBusiness.getPhoneNumber();
			email = currentBusiness.getEmail();
		}
		if (CarShopController.returnResult().equals(("be"))) {
			assertEquals(string, name);
			assertEquals(string2, address);
			assertEquals(string3, phoneNumber);
			assertEquals(string4, email);
			assertEquals(string5, CarShopController.returnResult());
		}
	}

	@When("the user tries to access the business information")
	public void the_user_tries_to_access_the_business_information() {
		// Write code here that turns the phrase above into concrete actions'
		businessInformation = CarShopController.viewBusinessInfor();
	}

	@Then("the {string} and {string} and {string} and {string} shall be provided to the user")
	public void the_and_and_and_shall_be_provided_to_the_user(String string, String string2, String string3,
			String string4) {
		// System.out.print("Myerror: is "+ test+" "+ businessInfor[0]+ " " +
		// businessInfor[1] + " " + " " + businessInfor[2] + " REALERROR: "+ string + "
		// " + string2);
		assertEquals(businessInformation[0], string);
		assertEquals(businessInformation[1], string2);
		assertEquals(businessInformation[2], string3);
		assertEquals(businessInformation[3], string4);
	}

	/**
	 * 
	 * 
	 * 
	 * Abdullah Arafat
	 * 
	 * 
	 */

	// @Given("the business has the following opening hours")
	// public void
	// the_business_has_the_following_opening_hours(io.cucumber.datatable.DataTable
	// dataTable) {
	// Business business = carshop.getBusiness();
	// for(int i=1; i<dataTable.height(); i++) {
	//// business.addBusinessHour(
	//// setupBusinessHour(dataTable.cell(i, 0), dataTable.cell(i, 1),
	// dataTable.cell(i, 2)));
	// String day = dataTable.cell(i, 0);
	// Time start = CarShopController.StringToTime(dataTable.cell(i, 1));
	// Time end =CarShopController.StringToTime(dataTable.cell(i, 2));
	// BusinessHour bh = new BusinessHour(DayOfWeek.valueOf(day), start,
	// end,carshop);
	// carshop.addHour(bh);
	// }
	// }

	// @Given("the business has the following opening hours:")
	// public void
	// the_business_has_the_following_opening_hours(io.cucumber.datatable.DataTable
	// dataTable) {
	// Business business = carshop.getBusiness();
	// for (int i = 1; i < dataTable.height(); i++)
	// business.addBusinessHour(
	// setupBusinessHour(dataTable.cell(i, 0), dataTable.cell(i, 1),
	// dataTable.cell(i, 2)));
	// }

//	@Given("the following appointments exist in the system:")
//	public void the_following_appointments_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
//		//works
//		//List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
//		ServiceCombo sc = getComboByName(carshop, dataTable.cell(1, 1));
//
//		Customer u = (Customer) User.getWithUsername(dataTable.cell(1, 0));
//		Appointment a = new Appointment(u,sc, carshop);
//		carshop.addAppointment(a);
//
//	}

	@Given("the following appointments exist in the system:")
	public void the_following_appointments_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {

		Customer u = (Customer) User.getWithUsername(dataTable.cell(1, 0));

		for (int i = 1; i < dataTable.height(); i++) {
			Service s;
			Appointment a;
			if (BookableService.getWithName(dataTable.cell(i, 1)) instanceof Service) {
				s = (Service) Service.getWithName(dataTable.cell(i, 1));
				a = new Appointment(u, s, carshop);
			}

			else {
				String name = dataTable.cell(i, 1);
				if (name.contains("combo"))
					name = dataTable.cell(i, 1).split("-combo")[0];
				if (name.contains("basic"))
					name = dataTable.cell(i, 1).split("-basic")[0];
				s = (Service) Service.getWithName(name);
				a = new Appointment(u, BookableService.getWithName(dataTable.cell(i, 1)), carshop);
			}

			String[] split = dataTable.cell(i, 4).split(",");

			// gives start and end time seperated with '-'
			TimeSlot ts = new TimeSlot(CarShopController.stringToDate(dataTable.cell(1, 3)),
					CarShopController.StringToTime(split[0].split("-")[0]),
					CarShopController.stringToDate(dataTable.cell(1, 3)),
					CarShopController.StringToTime(split[0].split("-")[1]), carshop);
			ServiceBooking sb1 = new ServiceBooking((Service) s, ts, a);

			for (int j = 1; j < split.length; j++) {

				String[] split2 = split[j].split("-"); // gives start time and endtime then start then end

				TimeSlot ts2 = new TimeSlot(CarShopController.stringToDate(dataTable.cell(1, 3)),
						CarShopController.StringToTime(split2[0]), CarShopController.stringToDate(dataTable.cell(1, 3)),
						CarShopController.StringToTime(split2[1]), carshop);
				ServiceBooking sb2 = new ServiceBooking(
						(Service) Service.getWithName(dataTable.cell(i, 2).split(",")[j - 1]), ts2, a);
			}
		}
	}

	@Given("the system's time and date is {string}")
	public void the_system_s_time_and_date_is(String timeAndDate) {
		CarShopApplication.setSystemDate(timeAndDate);
		// update input names of all methods here

	}

	@Given("{string} is logged in to their account")
	public void is_logged_in_to_their_account(String string) {
		// if(CarShopApplication.getCurrentUser()==null) {
		CarShopApplication.setCurrentUser(User.getWithUsername(string));
		// }
	}

	@When("{string} attempts to cancel their {string} appointment on {string} at {string}")
	public void attempts_to_cancel_their_appointment_on_at(String username, String serviceName, String date,
			String time) {
		try {
			prevAppointmentSize = carshop.getAppointments().size();
			CarShopController.cancelAppointment(username, username, serviceName, date, time);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	@Then("{string}'s {string} appointment on {string} at {string} shall be removed from the system")
	public void s_appointment_on_at_shall_be_removed_from_the_system(String customer, String service, String date,
			String time) {
		// private method that takes thgese 4 strings and searches for appointment,
		// return the appointment or null
		/*
		 * for ( Appointment appointment : findCust(customer).getAppointments()) { if (
		 * appointment.getBookableService().getName().equals(service) ) { } }
		 */
		// assertTrue(carshop.getAppointments().contains(o));
	}

	@Then("there shall be {int} less appointment in the system")
	public void there_shall_be_less_appointment_in_the_system(Integer int1) {
		assertEquals(this.prevAppointmentSize - carshop.getAppointments().size(), int1);
	}

	@Then("the system shall report {string}")
	public void the_system_shall_report(String string) {
		assertTrue(error.contains(string));
	}

	@Then("{string} shall have a {string} appointment on {string} at {string} with the following properties")
	public void shall_have_a_appointment_on_at_with_the_following_properties(String username, String serviceName,
			String string3, String string4, io.cucumber.datatable.DataTable dataTable) {
		Customer u = (Customer) User.getWithUsername(username);
		System.out.println(u.getAppointments().size() + "HHHHH");
		boolean isTrue = false;
		for (Appointment a : u.getAppointments()) {
			System.out.println("Hello");
			if (a.getBookableService().getName().equals(serviceName)) {
				System.out.println("Hello2");

				isTrue = true;
			}
		}
		assertTrue(isTrue);
	}

	@Then("there shall be {int} more appointment in the system")
	public void there_shall_be_more_appointment_in_the_system(Integer int1) {
		System.out.println("HelloW:)");
		System.out.println(carshop.getAppointments().size());
		System.out.println(this.prevAppointmentSize);
		System.out.println(error);
		assertEquals(int1, carshop.getAppointments().size() - this.prevAppointmentSize);
	}

	@When("{string} attempts to cancel {string}'s {string} appointment on {string} at {string}")
	public void attempts_to_cancel_s_appointment_on_at(String string, String string2, String string3, String string4,
			String string5) {
		try {
			prevAppointmentSize = carshop.getAppointments().size();
			CarShopController.cancelAppointment(string, string2, string3, string4, string5);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	@Given("all garages has the following opening hours")
	public void all_garages_has_the_following_opening_hours(io.cucumber.datatable.DataTable dataTable) {
		for (int i = 1; i < dataTable.height(); i++) {

			String day = dataTable.cell(i, 0);
			Time start = CarShopController.StringToTime(dataTable.cell(i, 1));
			Time end = CarShopController.StringToTime(dataTable.cell(i, 2));
			BusinessHour b = new BusinessHour(DayOfWeek.valueOf(day), start, end, carshop);
			for (Garage g : carshop.getGarages()) {
				g.addBusinessHour(b);
			}

		}
	}

	@Given("the business has the following holidays")
	public void the_business_has_the_following_holidays(io.cucumber.datatable.DataTable dataTable) {
		for (int i = 1; i < dataTable.height(); i++) {
			Date sDate = CarShopController.stringToDate(dataTable.cell(i, 0));
			Date eDate = CarShopController.stringToDate(dataTable.cell(i, 1));
			Time start = CarShopController.StringToTime(dataTable.cell(i, 2));
			Time end = CarShopController.StringToTime(dataTable.cell(i, 3));
			TimeSlot t = new TimeSlot(sDate, start, eDate, end, carshop);
			carshop.getBusiness().addHoliday(t);
		}
	}

	@When("{string} schedules an appointment on {string} for {string} at {string}")
	public void schedules_an_appointment_on_for_at(String username, String date, String serviceName, String sTime) {
		try {
			prevAppointmentSize = carshop.getAppointments().size();
			CarShopController.makeAppointment(username, date, serviceName, null, sTime);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	@Then("{string} shall have a {string} appointment on {string} from {string} to {string}")
	public void shall_have_a_appointment_on_from_to(String username, String appointmentN, String string3,
			String string4, String string5) {
		// carshop.getAppointments()
		Customer c = (Customer) User.getWithUsername(username);
		for (Appointment a : c.getAppointments()) {
			if (a.getBookableService().getName().equals(appointmentN)) {

			}
		}
	}

	@When("{string} schedules an appointment on {string} for {string} with {string} at {string}")
	public void schedules_an_appointment_on_for_with_at(String string, String string2, String string3, String string4,
			String string5) {
		try {
			prevAppointmentSize = carshop.getAppointments().size();
			CarShopController.makeAppointment(string, string2, string3, string4, string5);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	//////////////////////////////// Del 3 Tests //////////////////////////////

	/**
	 * @author Aly Mohamed
	 * @param customer
	 * @return
	 */
	private Customer getCustomerByName(String customer) {
		for (int i = 0; i < carshop.getCustomers().size(); i++) {
			if (carshop.getCustomer(i).getUsername().equals(customer)) {
				return carshop.getCustomer(i);
			}
		}
		return null;
	}

	/**
	 * @author Aly Mohamed
	 * @param customer
	 * @param noshows
	 */
	@Given("{string} has {int} no-show records")
	public void has_no_show_records(String customer, Integer noshows) {
		getCustomerByName(customer).setNoShows(noshows);

	}

	// we already have it
	/**
	 * @author Aly Mohamed
	 * @param username
	 * @param service
	 * @param date
	 * @param time
	 * @param systemTime
	 */
	@When("{string} makes a {string} appointment for the date {string} and time {string} at {string}")
	public void makes_a_appointment_for_the_date_and_time_at(String username, String service, String date, String time,
			String systemTime) {
		try {
			CarShopController.makeAppointment(username, date, service, null, time);
			CarShopApplication.setSystemDate(systemTime);

		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	/**
	 * newtoadd
	 * 
	 * @author alexs
	 * @param username
	 * @param newService
	 * @param systemTime
	 */
	@When("{string} attempts to change the service in the appointment to {string} at {string}")
	public void attempts_to_change_the_service_in_the_appointment_to_at(String username, String newService,
			String systemTime) {
		try {
			CarShopApplication.setSystemDate(systemTime);
			Appointment a = carshop.getAppointment(carshop.getAppointments().size() - 1);
			CarShopController.updateAppointment(carshop, a, username, newService, null, null, null, null);

		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	/**
	 * @author Aly Mohamed and Alexandre Saroufim
	 */
	@Then("the appointment shall be booked")
	public void the_appointment_shall_be_booked() {
		// Appointment a = null;
		if (carshop.getAppointments().size() == 0) {
			assertTrue(false);
		} else {
			int last = carshop.getAppointments().size() - 1;
			assertEquals("Booked", carshop.getAppointment(last).getAppointmentStatusFullName());
		}
	}

	/**
	 * @author Aly Mohamed and Alexandre Saroufim
	 * @param service
	 */
	@Then("the service in the appointment shall be {string}")
	public void the_service_in_the_appointment_shall_be(String service) {
		int last = carshop.getAppointments().size() - 1;
		assertEquals(service, carshop.getAppointment(last).getBookableService().getName());
	}

	/**
	 * @author Aly Mohamed and Alexandre Saroufim
	 * @param date
	 * @param startTime
	 * @param endTime
	 */
	@Then("the appointment shall be for the date {string} with start time {string} and end time {string}")
	public void the_appointment_shall_be_for_the_date_with_start_time_and_end_time(String date, String startTime,
			String endTime) {

		String start = "";
		String end = "";

		Appointment curapp = carshop.getAppointment(carshop.getAppointments().size() - 1);

		if (curapp.getBookableService() instanceof ServiceCombo) {
			ServiceCombo s = (ServiceCombo) curapp.getBookableService();

			start += curapp.getServiceBooking(0).getTimeSlot().getStartTime().toString().substring(0, 5);
			end += curapp.getServiceBooking(0).getTimeSlot().getEndTime().toString().substring(0, 5);
			start += ",";
			end += ",";

			for (int i = s.getServices().size(); i < curapp.getServiceBookings().size(); i++) {
				start += curapp.getServiceBooking(i).getTimeSlot().getStartTime().toString().substring(0, 5);

				end += curapp.getServiceBooking(i).getTimeSlot().getEndTime().toString().substring(0, 5);
				if (i + 1 != curapp.getServiceBookings().size()) {
					start += ",";
					end += ",";

				}
			}
		} else {
			Service s = (Service) curapp.getBookableService();
			for (int i = 0; i < curapp.getServiceBookings().size(); i++) {
				start += curapp.getServiceBooking(i).getTimeSlot().getStartTime().toString().substring(0, 5);
				end += curapp.getServiceBooking(i).getTimeSlot().getEndTime().toString().substring(0, 5);
			}

		}

		int last = carshop.getAppointments().size() - 1;
		assertEquals(date, carshop.getAppointment(last).getServiceBooking(0).getTimeSlot().getStartDate().toString());
		assertEquals(startTime, start);
		assertEquals(endTime, end);

	}

	/**
	 * @author Aly Mohamed
	 * @param string
	 */
	@Then("the username associated with the appointment shall be {string}")
	public void the_username_associated_with_the_appointment_shall_be(String username) {
		int last = carshop.getAppointments().size() - 1;
		assertEquals(username, carshop.getAppointment(last).getCustomer().getUsername());
	}

	/**
	 * @author Aly Mohamed
	 * @param username
	 * @param noshows
	 */
	@Then("the user {string} shall have {int} no-show records")
	public void the_user_shall_have_no_show_records(String username, Integer noshows) {
		assertTrue(getCustomerByName(username).getNoShows() == noshows);
	}

	/**
	 * @author Aly Mohamed
	 * @param number
	 */
	@Then("the system shall have {int} appointments")
	public void the_system_shall_have_appointments(Integer number) {
		assertTrue(carshop.getAppointments().size() == number);

	}

	// updateAppointment
	@When("{string} attempts to update the date to {string} and time to {string} at {string}")
	public void attempts_to_update_the_date_to_and_time_to_at(String username, String updatedDate, String updatedTime,
			String systemTime) {
		try {
			CarShopApplication.setSystemDate(systemTime);
			Appointment a = carshop.getAppointment(carshop.getAppointments().size() - 1);
			CarShopController.updateAppointment(carshop, a, username, null, updatedDate, updatedTime, null, null);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	/**
	 * @author Aly Mohamed
	 * @param number
	 */
	@Then("the system shall have {int} appointment")
	public void the_system_shall_have_appointment(Integer number) {
		assertEquals(number, carshop.getAppointments().size());

	}

	// cancelAppt()
	@When("{string} attempts to cancel the appointment at {string}")
	public void attempts_to_cancel_the_appointment_at(String loggedInUser, String date) {
		Appointment last = carshop.getAppointments().get(carshop.getAppointments().size() - 1);
		try {
			CarShopApplication.setSystemDate(date);
			CarShopController.cancelAppointment(loggedInUser, loggedInUser, last.getBookableService().getName(),
					last.getServiceBooking(0).getTimeSlot().getStartDate().toString(), null);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

	@When("{string} makes a {string} appointment with service {string} for the date {string} and start time {string} at {string}")
	public void makes_a_appointment_with_service_for_the_date_and_start_time_at(String username, String service,
			String optionalServices, String date, String startTime, String endTime) {
		try {
			CarShopController.makeAppointment(username, date, service, optionalServices, startTime);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}
	}

	// updateAppt()
	@When("{string} attempts to add the optional service {string} to the service combo with start time {string} in the appointment at {string}")
	public void attempts_to_add_the_optional_service_to_the_service_combo_with_start_time_in_the_appointment_at(
			String user, String optSer, String startTime, String systemTime) {
		CarShopApplication.setSystemDate(systemTime);
		try {
			Appointment curApp = carshop.getAppointments().get(carshop.getAppointments().size() - 1);
			CarShopController.updateAppointment(carshop, curApp, user, null, null, null, optSer, startTime);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;

		}

	}

	/**
	 * @author Aly Mohamed
	 * @param serviceCombo
	 */
	@Then("the service combo in the appointment shall be {string}")
	public void the_service_combo_in_the_appointment_shall_be(String serviceCombo) {
		int last = carshop.getAppointments().size() - 1;
		assertEquals(serviceCombo, carshop.getAppointment(last).getBookableService().getName());
	}

	@Then("the service combo shall have {string} selected services")
	public void the_service_combo_shall_have_selected_services(String services) {

		Appointment curapp = carshop.getAppointment(carshop.getAppointments().size() - 1);
		assertTrue(isInCombo(carshop, curapp.getBookableService().getName(), services));

	}

	private static boolean isInCombo(CarShop carshop, String comboname, String services) {
		String[] service = services.split(",");
		for (String serv : service) {
			if (getserviceFromACombo(carshop, comboname, serv) == null) {
				return false;
			}
		}
		return true;
	}

	// updateAppt()
	@When("{string} attempts to update the date to {string} and start time to {string} at {string}")
	public void attempts_to_update_the_date_to_and_start_time_to_at(String user, String newdate, String newtime,
			String systemTime) {
		CarShopApplication.setSystemDate(systemTime);
		try {
			Appointment curApp = carshop.getAppointments().get(carshop.getAppointments().size() - 1);
			if (newtime.contains(",")) {
				String[] arr = newtime.split(",");
				CarShopController.updateAppointment(carshop, curApp, user, null, newdate, arr[0], null, arr[1]);
			} else {
				CarShopController.updateAppointment(carshop, curApp, user, null, newdate, newtime, null, null);
			}

		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

	// startAppt()
	@When("the owner starts the appointment at {string}")
	public void the_owner_starts_the_appointment_at(String string) {
		try {
			CarShopController.startAppointment(string, "owner");
			
			
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

		
	}

	@When("the owner attempts to end the appointment at {string}")
	public void the_owner_attempts_to_end_the_appointment_at(String string) {
		try {
			CarShopController.endAppointment(string);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	
	@When("the owner ends the appointment at {string}")
	public void the_owner_ends_the_appointment_at(String string) {
		try {
			CarShopController.endAppointment(string);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}
	}

	/**
	 * @author Aly Mohamed
	 */
	@Then("the appointment shall be in progress")
	public void the_appointment_shall_be_in_progress() {
		int last = carshop.getAppointments().size() - 1;
		assertTrue(carshop.getAppointment(last).getAppointmentStatusFullName().equals("InProgress"));
	}

	@When("the owner attempts to register a no-show for the appointment at {string}")
	public void the_owner_attempts_to_register_a_no_show_for_the_appointment_at(String string) {
		try {
			CarShopController.RegisterNoShow(string);
		} catch (Exception e) {
			error += e.getMessage();
			errorCntr++;
		}

	}

}