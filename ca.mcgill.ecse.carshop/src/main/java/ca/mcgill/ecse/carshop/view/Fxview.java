package ca.mcgill.ecse.carshop.view;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.TOAppointment;
import ca.mcgill.ecse.carshop.controller.TOService;
import ca.mcgill.ecse.carshop.controller.TOBusinessHour;
import ca.mcgill.ecse.carshop.controller.TOBusiness;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Fxview extends Application {

	Stage window;
	Scene loginS;
	static Scene homeS;
	private ObservableList<Node> elementsOnScreen = null;
	@FXML
	private ComboBox<String> day;

	@FXML
	private ListView<String> appointments;

	@FXML
	private ListView<String> currentAppointments;

	@FXML
	private TextField servicename;
	@FXML
	private TextField duration;

	@FXML
	private ComboBox<String> cmb;

	@FXML
	private ComboBox<String> allServices;

	@Override
	public void start(Stage stage) throws Exception {
		window = stage;
		File home = new File("src/main/java/ca/mcgill/ecse/carshop/view/Test.fxml");
		String homepath = home.getAbsolutePath();

		Parent root = FXMLLoader.load(new File(homepath).toURI().toURL());
		homeS = new Scene(root, 800, 600);

		window.setTitle("FXML Welcome");
		window.setScene(homeS);
		window.show();
	}

	@FXML
	protected void logina(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File login = new File("src/main/java/ca/mcgill/ecse/carshop/view/loginS.fxml");
		String loginpath = login.getAbsolutePath();

		Parent loginP = FXMLLoader.load(new File(loginpath).toURI().toURL());
		loginS = new Scene(loginP, 800, 600);

		window.setScene(loginS);
		// System.out.println("works");
	}

	@FXML
	private TextField username;

	@FXML
	private PasswordField password;
	private Scene signupS;
	private Scene ownerS;
	private Scene customerS;
	private Scene technicianS;
	private String date;

	@FXML
	void gosignup(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File signup = new File("src/main/java/ca/mcgill/ecse/carshop/view/signupS.fxml");
		String signuppath = signup.getAbsolutePath();

		Parent signupP = FXMLLoader.load(new File(signuppath).toURI().toURL());
		signupS = new Scene(signupP, 800, 600);

		window.setScene(signupS);
	}

	@FXML
	void trylogin(ActionEvent event) throws MalformedURLException, IOException {
		String user = username.getText();
		String pass = password.getText();
		try {
			CarShopController.userLogin(user, pass);
			window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			if (user.equals(CarShopApplication.getCarShop().getOwner().getUsername())
					&& pass.equals(CarShopApplication.getCarShop().getOwner().getPassword())) {

				File ownerp = new File("src/main/java/ca/mcgill/ecse/carshop/view/ownerS.fxml");
				String ownerpath = ownerp.getAbsolutePath();

				Parent ownerP = FXMLLoader.load(new File(ownerpath).toURI().toURL());
				ownerS = new Scene(ownerP, 800, 600);

				window.setScene(ownerS);
				date = CarShopApplication.setSystemDate("2021-04-20+9:30");
			} else if (CarShopController.hasUsername(user) && !user.contains("Technician")) {
				File customerp = new File("src/main/java/ca/mcgill/ecse/carshop/view/CustomerS.fxml");
				String customerpath = customerp.getAbsolutePath();

				Parent customerP = FXMLLoader.load(new File(customerpath).toURI().toURL());
				customerS = new Scene(customerP, 800, 600);

				window.setScene(customerS);
			} else {

				File technicianp = new File("src/main/java/ca/mcgill/ecse/carshop/view/TechnicianS.fxml");
				String technicianpath = technicianp.getAbsolutePath();
				Parent technicianP = FXMLLoader.load(new File(technicianpath).toURI().toURL());
				technicianS = new Scene(technicianP, 800, 600);

				window.setScene(technicianS);

			}
			// System.out.println("succesful");
		} catch (InvalidInputException e) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
			// System.out.println("not succesful");
		}
	}

	@FXML
	private TextField username2;

	@FXML
	private PasswordField password2;

	@FXML
	private PasswordField confirmpassword2;
	private Scene accountS;
	private Scene apointmentsownerS;
	private Scene businessS;
	private Scene serviceS;
	private Scene garageS;

	@FXML
	void backtologin(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File login = new File("src/main/java/ca/mcgill/ecse/carshop/view/loginS.fxml");
		String loginpath = login.getAbsolutePath();

		CarShopController.signOut();

		Parent loginP = FXMLLoader.load(new File(loginpath).toURI().toURL());
		loginS = new Scene(loginP, 800, 600);

		window.setScene(loginS);
	}

	@FXML
	void trysignup(ActionEvent event) throws MalformedURLException, IOException {

		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File login = new File("src/main/java/ca/mcgill/ecse/carshop/view/loginS.fxml");
		String loginpath = login.getAbsolutePath();

		Parent loginP = FXMLLoader.load(new File(loginpath).toURI().toURL());
		loginS = new Scene(loginP, 800, 600);

		String user = username2.getText();
		String pass1 = password2.getText();
		String pass2 = confirmpassword2.getText();
		// pop ups
		try {
			CarShopController.signUpCustomer(user, pass1);
			if (pass1.equals(pass2)) {
				Alert box = new Alert(AlertType.CONFIRMATION);
				box.setHeaderText(null);
				box.setContentText("Welcome! You successfuly signed up");
				box.show();
				window.setScene(loginS);
			} else {
				Alert box = new Alert(AlertType.ERROR);
				box.setHeaderText(null);
				box.setContentText("Passwords don't match");
				box.show();
			}

		} catch (InvalidInputException e1) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e1.getMessage());
			box.show();
			window.setScene(((Node) event.getSource()).getScene());
		}
	}

	@FXML
	void accounta(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File file = new File("src/main/java/ca/mcgill/ecse/carshop/view/AccountS.fxml");
		String filepath = file.getAbsolutePath();

		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		accountS = new Scene(fileP, 800, 600);

		window.setScene(accountS);
	}

	@FXML
	void appointmenta(ActionEvent event) throws MalformedURLException, IOException {

		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File file = new File("src/main/java/ca/mcgill/ecse/carshop/view/MakeAppointment.fxml");
		String filepath = file.getAbsolutePath();

		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		apointmentsownerS = new Scene(fileP, 800, 600);

		window.setScene(apointmentsownerS);

	}

	@FXML
	void backhome(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File login = new File("src/main/java/ca/mcgill/ecse/carshop/view/Test.fxml");
		String loginpath = login.getAbsolutePath();

		CarShopController.signOut();

		Parent loginP = FXMLLoader.load(new File(loginpath).toURI().toURL());
		homeS = new Scene(loginP, 800, 600);

		window.setScene(homeS);
	}

	@FXML
	void businessa(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File file = new File("src/main/java/ca/mcgill/ecse/carshop/view/BusinessS.fxml");
		String filepath = file.getAbsolutePath();

		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		businessS = new Scene(fileP, 800, 600);

		window.setScene(businessS);
	}

	@FXML
	void servicea(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File file = new File("src/main/java/ca/mcgill/ecse/carshop/view/ServiceS.fxml");
		String filepath = file.getAbsolutePath();

		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		serviceS = new Scene(fileP, 800, 600);

		window.setScene(serviceS);
	}

	@FXML
	void signout(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File login = new File("src/main/java/ca/mcgill/ecse/carshop/view/loginS.fxml");
		String loginpath = login.getAbsolutePath();

		CarShopController.signOut();

		Parent loginP = FXMLLoader.load(new File(loginpath).toURI().toURL());
		loginS = new Scene(loginP, 800, 600);

		window.setScene(loginS);
	}

	@FXML
	void updategaragea(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File file = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateGarageS.fxml");
		String filepath = file.getAbsolutePath();

		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		garageS = new Scene(fileP, 800, 600);

		window.setScene(garageS);
	}

	@FXML
	private TextField startTimeG;

	@FXML
	private TextField endTimeG;

	private Node getElementbyID(String id) {

		for (Node node : elementsOnScreen) {
			if (node.getId() != null) {

				if (node.getId().equals(id)) {
					System.out.println("node: " + node.getId());
					return node;
				}
			}
		}

		return null;
	}

	@FXML
	void initScreenElementsG(MouseEvent event) throws MalformedURLException, IOException {

		// System.out.println();

		if (elementsOnScreen == null) {
			Pane db = (Pane) event.getSource();
			elementsOnScreen = db.getChildren();

			ComboBox cb = (ComboBox) getElementbyID("day");
			cb.getItems().removeAll(cb.getItems());
			cb.getItems().add("Monday");
			cb.getItems().add("Tuesday");
			cb.getItems().add("Wednesday");
			cb.getItems().add("Thursday");
			cb.getItems().add("Friday");
		}

	}

	/*
	 * @simo benkirane
	 */
	@FXML
	void submitAddService(ActionEvent event) throws Exception {

		cmb = (ComboBox<String>) getElementbyID("cmb");

		// addService
		System.out.println(cmb.getValue() + servicename.getText() + duration.getText());
		if (cmb.getValue() != null && !servicename.getText().isEmpty() && !duration.getText().isEmpty()) {

			try {

				if (CarShopApplication.getCurrentUser() == null) {
					statuslabel = (Label) getElementbyID("statuslabel");
					statuslabel.setText("No User logged in");
					throw new Exception("no user");
				}

				System.out.println(CarShopApplication.getCarShop().toString() + servicename.getText()
						+ duration.getText() + cmb.getValue());
				CarShopController.addService(CarShopApplication.getCarShop(),
						CarShopApplication.getCurrentUser().getUsername(), servicename.getText(), duration.getText(),
						cmb.getValue());
				statuslabel = (Label) getElementbyID("statuslabel");
				statuslabel.setText("Successful!");
			} catch (Exception e) {
				statuslabel = (Label) getElementbyID("statuslabel");
				statuslabel.setText(e.getMessage() + " Update Unsuccessful :(");
			}

		}

		System.out.println("smtg null");
	}

	@FXML
	void submitUpdateService(ActionEvent event) throws Exception {

		cmb = (ComboBox<String>) getElementbyID("cmb");
		allServices = (ComboBox<String>) getElementbyID("allServices");

		// addService
		System.out.println(cmb.getValue() + servicename.getText() + duration.getText());
		if (cmb.getValue() != null && !servicename.getText().isEmpty() && !duration.getText().isEmpty()) {

			try {

				if (CarShopApplication.getCurrentUser() == null) {
					statuslabel = (Label) getElementbyID("statuslabel");
					statuslabel.setText("No User logged in");
					throw new Exception("no user logged in");
				}
				System.out.println(CarShopApplication.getCarShop().toString() + allServices.getValue()
						+ servicename.getText() + duration.getText() + cmb.getValue());
				CarShopController.updateService(CarShopApplication.getCarShop(),
						CarShopApplication.getCurrentUser().getUsername(), allServices.getValue(),
						servicename.getText(), duration.getText(), cmb.getValue());
				statuslabel = (Label) getElementbyID("statuslabel");
				statuslabel.setText("Update Successful!");
			} catch (Exception e) {
				statuslabel = (Label) getElementbyID("statuslabel");
				statuslabel.setText(e.getMessage() + " Update Unsuccessful :(");
			}

		}

	}

	@FXML
	void returnToServiceOptions(ActionEvent event) throws Exception {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File addService = new File("src/main/java/ca/mcgill/ecse/carshop/view/Service.fxml");
		String addServicepath = addService.getAbsolutePath();

		Parent addServiceP = FXMLLoader.load(addService.toURI().toURL());

		Scene addServiceS = new Scene(addServiceP, 800, 600);

		elementsOnScreen = null;

		window.setScene(addServiceS);
	}

	@FXML
	void goToUpdateService(ActionEvent event) throws Exception {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File addService = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateService.fxml");
		String addServicepath = addService.getAbsolutePath();

		Parent addServiceP = FXMLLoader.load(addService.toURI().toURL());

		Scene addServiceS = new Scene(addServiceP, 800, 600);

		window.setScene(addServiceS);
	}

	// for Add Service!!
	@FXML
	void initScreenElements(MouseEvent event) throws MalformedURLException, IOException {

		if (elementsOnScreen == null) {
			AnchorPane db = (AnchorPane) event.getSource();
			elementsOnScreen = db.getChildren();

			ComboBox cb = (ComboBox) getElementbyID("cmb");
			cb.getItems().removeAll(cb.getItems());
			cb.getItems().add("Tire");
			cb.getItems().add("Fluids");
			cb.getItems().add("Transmission");
			cb.getItems().add("Electronics");
			cb.getItems().add("Engine");

			// updateService
			if (getElementbyID("allServices") != null) {

				ComboBox b = (ComboBox) getElementbyID("allServices");
				b.getItems().removeAll(b.getItems());

				List<TOService> allServicesTO = CarShopController.getAllTOServices();

				for (TOService sTO : allServicesTO) {
					b.getItems().add(sTO.getName());
				}
			}

		}

	}

	@FXML
	protected void printElement(ActionEvent event) throws MalformedURLException, IOException {
		System.out.println(event.getSource());

	}

	@FXML
	protected void switchToAddService(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File addService = new File("src/main/java/ca/mcgill/ecse/carshop/view/addService.fxml");
		String addServicepath = addService.getAbsolutePath();

		Parent addServiceP = FXMLLoader.load(addService.toURI().toURL());

		Scene addServiceS = new Scene(addServiceP, 800, 600);

		window.setScene(addServiceS);

		// TextField tff = (TextField) addServiceS.lookup("#label");
		// tff.setText("bojoe");

	}

	@FXML
	private Label statuslabel;
	private Scene updateGarageS2;

	@FXML
	void doUpdateGarage(ActionEvent event) {

		day = (ComboBox<String>) getElementbyID("day");

		// addService
		if (CarShopController.StringToTime(startTimeG.getText())
				.after(CarShopController.StringToTime(endTimeG.getText()))) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText("Start time is after end time");
			box.show();
		}

		if (day.getValue() != null && !startTimeG.getText().isEmpty() && !endTimeG.getText().isEmpty()) {

			try {
				System.out.println(startTimeG.getText() + " " + endTimeG.getText() + "  " + day.getValue() + " "
						+ CarShopApplication.getCurrentUser().getUsername());
				CarShopController.addGarageHours(day.getValue(), startTimeG.getText(), endTimeG.getText());
				Alert box = new Alert(AlertType.CONFIRMATION);
				box.setHeaderText(null);
				box.setContentText("Garage hours successfully changed");
				box.show();

			} catch (Exception e) {
				Alert box = new Alert(AlertType.ERROR);
				box.setHeaderText(null);
				box.setContentText(e.getMessage());
				box.show();
				System.out.println("problem " + e.getMessage());

			}
		} else
			System.out.println("smtg null");
	}

	@FXML
	void backtomenuT(ActionEvent event) throws MalformedURLException, IOException {

		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File technicianp = new File("src/main/java/ca/mcgill/ecse/carshop/view/TechnicianS.fxml");
		String technicianpath = technicianp.getAbsolutePath();
		Parent technicianP = FXMLLoader.load(new File(technicianpath).toURI().toURL());
		technicianS = new Scene(technicianP, 800, 600);

		window.setScene(technicianS);

	}

	@FXML
	void updategaragea2(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateGarageS2.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		updateGarageS2 = new Scene(fileP, 800, 600);

		window.setScene(updateGarageS2);

	}

	@FXML
	void doUpdateGarage2(ActionEvent event) {
		day = (ComboBox<String>) getElementbyID("day");

		if (CarShopController.StringToTime(startTimeG.getText())
				.after(CarShopController.StringToTime(endTimeG.getText()))) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText("Start time is after end time");
			box.show();
		}

		// addService
		// System.out.println(day.getValue());
		if (day.getValue() != null && !startTimeG.getText().isEmpty() && !endTimeG.getText().isEmpty()) {

			try {
				System.out.println(startTimeG.getText() + " " + endTimeG.getText() + "  " + day.getValue() + " "
						+ CarShopApplication.getCurrentUser().getUsername());
				CarShopController.removeGarageHours(day.getValue(), startTimeG.getText(), endTimeG.getText());
				Alert box = new Alert(AlertType.CONFIRMATION);
				box.setHeaderText(null);
				box.setContentText("Garage hours successfully changed");
				box.show();

			} catch (Exception e) {
				Alert box = new Alert(AlertType.CONFIRMATION);
				box.setHeaderText(null);
				box.setContentText(e.getMessage());
				box.show();
				System.out.println("problem " + e.getMessage());

			}
		} else
			System.out.println("smtg null");
	}

	// MakeAppointment

	// @FXML
	// private TextField startTimeG;
	//
	// @FXML
	// private TextField endTimeG;
	//
	// @FXML
	// private ComboBox<?> day;
	@FXML
	private TextField dateOfApp;

	@FXML
	private TextField optionalServicesS;

	@FXML
	private TextField mainServiceS;

	@FXML
	void backtomenu(ActionEvent event) throws MalformedURLException, IOException {

		if (CarShopApplication.getCurrentUser().getUsername()
				.equals(CarShopApplication.getCarShop().getOwner().getUsername())) {
			window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/OwnerS.fxml");
			String filepath = filep.getAbsolutePath();
			Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
			ownerS = new Scene(fileP, 800, 600);

			window.setScene(ownerS);
		}

		else if (CarShopController.hasUsername(CarShopApplication.getCurrentUser().getUsername())) {
			window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/CustomerS.fxml");
			String filepath = filep.getAbsolutePath();
			Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
			customerS = new Scene(fileP, 800, 600);

			window.setScene(customerS);
		} else {
			window = (Stage) ((Node) event.getSource()).getScene().getWindow();

			File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/TechnicianS.fxml");
			String filepath = filep.getAbsolutePath();
			Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
			technicianS = new Scene(fileP, 800, 600);

			window.setScene(technicianS);
		}
	}

	private Scene cancelAppointment;

	@FXML
	void cancelAppointmentS(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/CancelAppointment.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		cancelAppointment = new Scene(fileP, 800, 600);
		window.setScene(cancelAppointment);
	}

	@FXML
	private TextField curdate;

	@FXML
	void doMakeAppointmentS(ActionEvent event) throws InvalidInputException {

		// CarShopApplication.setSystemDate(curdate.getText());
		String mainService = mainServiceS.getText();
		String optionalServices = optionalServicesS.getText();
		String sTime = startTimeG.getText();
		String dayApp = dateOfApp.getText();
		String loggedUser = CarShopApplication.getCurrentUser().getUsername();
		try {
			CarShopController.makeAppointment(loggedUser, dayApp, mainService, optionalServices, sTime);
			Alert box = new Alert(AlertType.CONFIRMATION);
			box.setHeaderText(null);
			box.setContentText("Appointment successfuly booked");
			box.show();
		} catch (Exception e) {

			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();

		}

	}

	// @FXML
	// void initScreenElementsG(MouseEvent event) {
	//
	// }

	@FXML
	private TextField newStartTime;

	@FXML
	private TextField oldStartTime;

	@FXML
	private TextField newDateofApp;

	@FXML
	private TextField oldDateofApp;

	@FXML
	private TextField optstimeslots;
	private Scene updateAppointment;
	private Scene makeAppointment;

	@FXML
	void updateAppointmentS(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateAppointment.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		updateAppointment = new Scene(fileP, 800, 600);

		window.setScene(updateAppointment);
	}

	// Update Appointment

	@FXML
	private TextField appointmnentToUpdate;

	@FXML
	void doUpdateAppointmentS(ActionEvent event) {

		if (CarShopController.StringToTime(startTimeG.getText())
				.after(CarShopController.StringToTime(endTimeG.getText()))) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText("Start time is after end time");
			box.show();
		}

		String mainService = mainServiceS.getText();
		String optionalServices = optionalServicesS.getText();
		String oldTime = oldStartTime.getText();
		String newTime = newStartTime.getText();
		String oldAppDate = oldDateofApp.getText();
		String newAppDate = newDateofApp.getText();
		try {
			CarShopController.updateAppointment(mainService, optionalServices, oldTime, newTime, oldAppDate, newAppDate,
					optstimeslots.getText());
			Alert box = new Alert(AlertType.CONFIRMATION);
			box.setHeaderText(null);
			box.setContentText("The appointment was successfuly canceled");
			box.show();
		} catch (Exception e) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}
		System.out.println(mainService + " " + optionalServices + " " + oldTime + " " + newTime + " " + oldAppDate + " "
				+ newAppDate);

	}

	@FXML
	void makeAppointmentS(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/MakeAppointment.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		makeAppointment = new Scene(fileP, 800, 600);
		window.setScene(makeAppointment);
	}

	// Cancel Appointment

	@FXML
	private TextField customerOfAppointment;

	@FXML
	void doCancelAppointmentS(ActionEvent event) {

		String cust = customerOfAppointment.getText();
		String mainService = mainServiceS.getText();
		String sTime = startTimeG.getText();
		String dayApp = dateOfApp.getText();
		String loggedUser = CarShopApplication.getCurrentUser().getUsername();
		System.out.println(mainService + " " + cust + " " + sTime + " " + dayApp + " " + loggedUser);
		try {

			CarShopController.cancelAppointment(loggedUser, cust, mainService, dayApp, sTime);
			Alert box = new Alert(AlertType.CONFIRMATION);
			box.setHeaderText(null);
			box.setContentText("The appointment was successfuly canceled");
			box.show();
		} catch (Exception e) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}
	}

	@FXML
	void backtomenucustomer(ActionEvent event) throws MalformedURLException, IOException {

		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/CustomerS.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		customerS = new Scene(fileP, 800, 600);

		window.setScene(customerS);
	}

	@FXML
	void updateaccinfo(ActionEvent event) throws InvalidInputException {
		try {
			if (password2.equals(confirmpassword2)) {
				CarShopController.updateUser(CarShopApplication.getCurrentUser().getUsername(), username2.getText(),
						password2.getText());
				Alert box = new Alert(AlertType.CONFIRMATION);
				box.setHeaderText(null);
				box.setContentText("Successfully updated account information");
				box.show();
			} else {
				CarShopController.updateUser(CarShopApplication.getCurrentUser().getUsername(), username2.getText(),
						password2.getText());
				Alert box = new Alert(AlertType.ERROR);
				box.setHeaderText(null);
				box.setContentText("Could not update account information");
				box.show();
			}
		}

		catch (InvalidInputException e) {
			// System.out.println("not succesful");
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}
	}

	@FXML
	public void startAppointment(ActionEvent event) {
		CarShopController.RegisterNoShow();
	}

	@FXML
	public void endAppointment(ActionEvent event) {
		try {
			CarShopController.endAppointment();
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void noShowAppointment(ActionEvent event) {
		try {
			CarShopController.startAppointment();
		} catch (InvalidInputException e) {
			e.printStackTrace();
		}
	}

	@FXML
	void seeAppointments(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();

		File file = new File("src/main/java/ca/mcgill/ecse/carshop/view/appointments.fxml");
		String path = file.getAbsolutePath();

		Parent root = FXMLLoader.load(new File(path).toURI().toURL());
		Scene app = new Scene(root, 800, 600);

		window.setScene(app);
	}

	@SuppressWarnings("unchecked")
	@FXML
	void initAppointmentElements(MouseEvent event) throws MalformedURLException, IOException {

		if (elementsOnScreen == null) {
			AnchorPane db = (AnchorPane) event.getSource();
			elementsOnScreen = db.getChildren();

			Node node = getElementbyID("appointments");
			Node node2 = getElementbyID("currentAppointments");

			if (node instanceof ListView<?>) {
				appointments = (ListView<String>) node;
				appointments.getItems().removeAll(appointments.getItems());

				List<TOAppointment> apps = CarShopController.getAppointments();

				for (TOAppointment app : apps) {
					appointments.getItems().add(app.getServiceName() + " with " + app.getCustomer().getUsername());
				}

			} else
				System.out.println("WRONG FX:ID");

			if (node2 instanceof ListView<?>) {
				currentAppointments = (ListView<String>) node2;
				currentAppointments.getItems().removeAll(currentAppointments.getItems());

				List<TOAppointment> apps = CarShopController.getCurrentAppointments();

				for (TOAppointment app : apps) {
					appointments.getItems().add(app.getServiceName() + " with " + app.getCustomer().getUsername());
				}

			} else
				System.out.println("WRONG FX:ID");

		}

	}

	@FXML
	void goToViewAppointment(ActionEvent event) {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		window.setScene(View.viewAppointmentsScene());

	}

	// Mehdi ' s part
	@FXML
	private TextField startTimeMonday;

	@FXML
	private TextField endTimeMonday;

	@FXML
	private TextField startTimeTuesday;

	@FXML
	private TextField endTimeTuesday;

	@FXML
	private TextField startTimeWednesday;

	@FXML
	private TextField endTimeWednesday;

	@FXML
	private TextField startTimeThursday;

	@FXML
	private TextField endTimeThursday;

	@FXML
	private TextField startTimeFriday;

	@FXML
	private TextField endTimeFriday;

	@FXML
	private TextField startTimeSaturday;

	@FXML
	private TextField endTimeSaturday;

	@FXML
	private TextField startTimeSunday;

	@FXML
	private TextField endTimeSunday;

	// hyper link

	private Scene businessinfo;

	void backToBusinessInformation(ActionEvent event) throws IOException {

		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/BusinessS.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		businessinfo = new Scene(fileP, 800, 600);

		window.setScene(businessinfo);

	}

	@FXML
	void doAddBusinessHours(ActionEvent event) throws InvalidInputException {
		String user = CarShopApplication.getCurrentUser().getUsername();
		try {
			if (user.equals("owner")) {
				if (startTimeMonday != null && startTimeTuesday != null && startTimeWednesday != null
						&& startTimeThursday != null && startTimeFriday != null && startTimeSaturday != null
						&& startTimeSunday != null && endTimeMonday != null && endTimeTuesday != null
						&& endTimeWednesday != null && endTimeThursday != null && endTimeFriday != null
						&& endTimeSaturday != null) {

					if (startTimeMonday != null && endTimeMonday != null) {
						CarShopController.doAddBusinessHours("Monday",
								CarShopController.StringToTime(startTimeMonday.getText()),
								CarShopController.StringToTime(endTimeMonday.getText()));

					}

					if (startTimeTuesday != null && endTimeTuesday != null) {
						CarShopController.doAddBusinessHours("Tuesday",
								CarShopController.StringToTime(startTimeTuesday.getText()),
								CarShopController.StringToTime(endTimeTuesday.getText()));
					}

					if (startTimeWednesday != null && endTimeWednesday != null) {
						CarShopController.doAddBusinessHours("Wednesday",
								CarShopController.StringToTime(startTimeWednesday.getText()),
								CarShopController.StringToTime(endTimeWednesday.getText()));
					}

					if (startTimeThursday != null && endTimeThursday != null) {
						CarShopController.doAddBusinessHours("Thursday",
								CarShopController.StringToTime(startTimeThursday.getText()),
								CarShopController.StringToTime(endTimeThursday.getText()));
					}

					if (startTimeFriday != null && endTimeFriday != null) {
						CarShopController.doAddBusinessHours("Friday",
								CarShopController.StringToTime(startTimeFriday.getText()),
								CarShopController.StringToTime(endTimeFriday.getText()));
					}

					if (startTimeSaturday != null && endTimeSaturday != null) {
						CarShopController.doAddBusinessHours("Saturday",
								CarShopController.StringToTime(startTimeSaturday.getText()),
								CarShopController.StringToTime(endTimeSaturday.getText()));
					}

					if (startTimeSunday != null && endTimeSunday != null) {
						CarShopController.doAddBusinessHours("Sunday",
								CarShopController.StringToTime(startTimeSunday.getText()),
								CarShopController.StringToTime(endTimeSunday.getText()));
					}

					Alert box = new Alert(AlertType.CONFIRMATION);
					box.setHeaderText(null);
					box.setContentText("Business Information Successfully Added!");
					box.show();
					// window.setScene(loginS);
				}

			}
		} catch (InvalidInputException e) {
			// System.out.println("not succesful");
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}
	}

	// hyper link to updateBusinessHours
	private Scene updateBusinessHours;

	// hyper link to updateBusinessHours
	@FXML
	void UpdateBusinessHours(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateBusinessHours.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		updateBusinessHours = new Scene(fileP, 800, 600);
		window.setScene(updateBusinessHours);

	}

	// updateBusinessS

	@FXML
	private TextField BusinessName;

	@FXML
	private TextField BusinessAddress;

	@FXML
	private TextField BusinessPhoneNumber;

	@FXML
	private TextField BusinessEmail;

	@FXML
	void doUpdateBusinessInformation(ActionEvent event) throws InvalidInputException {
		String user = CarShopApplication.getCurrentUser().getUsername();
		try {
			if (user.equals("owner")) {
				if (BusinessEmail != null && BusinessPhoneNumber != null && BusinessAddress != null
						&& BusinessName != null) {
					CarShopController.UpdateBusinessInformation(BusinessName.getText(), BusinessAddress.getText(),
							BusinessPhoneNumber.getText(), BusinessEmail.getText());
					Alert box = new Alert(AlertType.CONFIRMATION);
					box.setHeaderText(null);
					box.setContentText("Business Information Successfully Updated!");
					box.show();
				}

			}
		} catch (InvalidInputException e) {
			// System.out.println("not succesful");
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}
	}

	private Scene Updatebusinessinfo;

	@FXML
	void UpdateBusinessInformation(ActionEvent event) throws IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateBusinessS.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		Updatebusinessinfo = new Scene(fileP, 800, 600);

		window.setScene(Updatebusinessinfo);
	}

	// update Business Hours

	@FXML
	private TextField oldDay;

	@FXML
	private TextField oldStartTimeBusiness;

	@FXML
	private TextField newDay;

	@FXML
	private TextField newEndTimeBusiness;

	@FXML
	private TextField newStartTimeBusiness;

	private Scene businessHours;

	@FXML
	private TextField currentdate;

	@FXML
	void doUpdateBusinessHours(ActionEvent event) throws InvalidInputException {

		String user = CarShopApplication.getCurrentUser().getUsername();
		try {
			if (user.equals("owner")) {
				if (newStartTimeBusiness != null && newEndTimeBusiness != null && newDay != null
						&& oldStartTimeBusiness != null && oldDay != null) {
					CarShopController.doUpdateBusinessHours(oldDay.getText(), oldStartTimeBusiness.getText(),
							newDay.getText(), newStartTimeBusiness.getText(), newEndTimeBusiness.getText());
					Alert box = new Alert(AlertType.CONFIRMATION);
					box.setHeaderText(null);
					box.setContentText("Business Hours Successfully Updated!");
					box.show();
				}

			}
		} catch (InvalidInputException e) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}

	}

	// addBusinessInformation
	@FXML
	void doAddBusinessInformation(ActionEvent event) throws InvalidInputException {
		String user = CarShopApplication.getCurrentUser().getUsername();
		try {
			if (user.equals("owner")) {
				if (BusinessEmail != null && BusinessPhoneNumber != null && BusinessAddress != null
						&& BusinessName != null) {
					CarShopController.SetUpBusinessInformation(BusinessName.getText(), BusinessAddress.getText(),
							BusinessPhoneNumber.getText(), BusinessEmail.getText());
					Alert box = new Alert(AlertType.CONFIRMATION);
					box.setHeaderText(null);
					box.setContentText("Business Information Successfully Added!");
					box.show();
				}
			}
		} catch (InvalidInputException e) {
			Alert box = new Alert(AlertType.ERROR);
			box.setHeaderText(null);
			box.setContentText(e.getMessage());
			box.show();
		}
	}

	@FXML
	void addbusinesshoursS(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/BusinessHours.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		businessHours = new Scene(fileP, 800, 600);

		window.setScene(businessHours);
	}

	@FXML

	void updatebusinesshourss(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateBusinessHours.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		businessHours = new Scene(fileP, 800, 600);
		window.setScene(businessHours);
	}

	@FXML
	void addbusinessinformationS(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/BusinessS.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		businessHours = new Scene(fileP, 800, 600);

		window.setScene(businessHours);
	}

	@FXML
	void updatebusinessinformation(ActionEvent event) throws MalformedURLException, IOException {
		window = (Stage) ((Node) event.getSource()).getScene().getWindow();
		File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/UpdateBusinessS.fxml");
		String filepath = filep.getAbsolutePath();
		Parent fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
		businessHours = new Scene(fileP, 800, 600);

		window.setScene(businessHours);
	}

	@FXML
	void setDate(MouseEvent event) {

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmm").format(Calendar.getInstance().getTime());

		String year = timeStamp.substring(0, 4);
		// System.out.println(year);

		String month = timeStamp.substring(4, 6);
		// System.out.println(month);

		String day = timeStamp.substring(6, 8);
		// System.out.println(day);

		String hour = timeStamp.substring(9, 11);
		// System.out.println(hour);

		String minute = timeStamp.substring(11, 13);
		// System.out.println(minute);

		String curDate = year + "-" + month + "-" + day + "+" + hour + ":" + minute;
		CarShopApplication.setSystemDate(curDate);
		// System.out.println(CarShopApplication.getSystemDate());

		String businessHours = "";
		for (int i = 0; i < CarShopApplication.getCarShop().getBusiness().getBusinessHours().size(); i++) {
			businessHours += CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getDayOfWeek().toString()
					+ ": "
					+ CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getStartTime().toString()
							.substring(0, 5)
					+ ", " + CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getEndTime().toString()
							.substring(0, 5)
					+ "\n";

		}
//		TOBusinessHour.DayOfWeek.values()
//		carshopinformation.set);
		carshopinformation.setText("Engineered minds for your car problems!\n\n"
				+ CarShopApplication.getCarShop().getBusiness().getAddress() + "\n"
				+ CarShopApplication.getCarShop().getBusiness().getEmail() + "\n"
				+ CarShopApplication.getCarShop().getBusiness().getPhoneNumber() + "\n" + "\nOpening hours:\n"
				+ businessHours);

	}

	@FXML
	private Label carshopinformation;

	@FXML
	void setInfo(MouseEvent event) {

		String businessHours;
//		for(int i= 0;i<CarShopApplication.getCarShop().getBusiness().getBusinessHours().size();i++) {
//		businessHours += CarShopApplication.getCarShop().getBusiness().getBusinessHour(i).getDayOfWeek().toString()
//		+ ": " + ;
//		
//		
//		}

		carshopinformation.setText(
				"Engineered minds for your car problems\n!" + CarShopApplication.getCarShop().getBusiness().getAddress()
						+ "\n" + CarShopApplication.getCarShop().getBusiness().getEmail() + "\n"
						+ CarShopApplication.getCarShop().getBusiness().getPhoneNumber() + "\n");
//		"Engineered minds for your car problems\n!" +	
	}

}
