package ca.mcgill.ecse.carshop.view;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.TOTimeSlot;
import ca.mcgill.ecse.carshop.model.Appointment;
import ca.mcgill.ecse.carshop.model.CarShop;
//CHANGE ALLLLL THESE NO IMPORT FROM MODEL
import ca.mcgill.ecse.carshop.model.Customer;
import ca.mcgill.ecse.carshop.model.Technician;

//import de.jensd.fx.glyphs.GlyphsDude;
//import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class View extends Application {

	static Stage window;
	static Button loginb;
	static Button backb; 
	static Scene loginS;
	static Scene ownerS;
	static Scene signupS;
	static Label welcomel;
	static Label homel;
	static PasswordField password;
	static ImageView imagev;
	static HBox hbox;
	static Scene customerS;
	private static Scene serviceS;
	private static Scene appointmentS;
	private static Scene appointmentSOwner;
	private static Scene businessS;
	private static Scene serviceSOwner;
	private static Scene technicianS;
	private static Scene garageS;
	private static Scene viewAppointments;

	
	
	
	/**
	 * Alexandre part
	 * View Appointments
	 */
	//TOTimeSlots Table
		private static TableView<String> availableTime= new TableView<String>();
		private static TableView<String> bookedTime=new TableView<String>();;
		private static Text viewDate;
		private static DatePicker viewDatePicker;
		private static HBox topTable;
		private static ToggleButton dailyToggleButton;
		private static ToggleButton weeklyToggleButton;
		private static ToggleGroup toggleGroup;
		private static HBox timeSlotTables;
		private BorderPane viewAppCalPane;
		@SuppressWarnings("rawtypes")
		private TableColumn availableTSCol;
		private TableColumn<TOTimeSlot, Time> availableStartTimeCol;
		private TableColumn<TOTimeSlot, Date> availableDateCol;
		private TableColumn<TOTimeSlot, Time> availableEndTimeCol;
		@SuppressWarnings("rawtypes")
		private static TableColumn unavailableTSCol;
		private static TableColumn<TOTimeSlot, Date> unavailableDateCol;
		private static TableColumn<TOTimeSlot, Time> unavailableStartTimeCol;
		private static TableColumn<TOTimeSlot, Time> unavailableEndTimeCol;
		private static Button back;
	
	@Override
	public void start(Stage stage) throws Exception {
		window = stage;
		stage.setTitle("CarShop");
		
		loginS = loginScene();	
		
		ownerS = ownerScene();
		signupS = signupScene();
		//loginS.getStylesheets().add(getClass().getResource("NewStyle.css").toString());
		customerS = customerScene();
		technicianS = technicianScene();
		
		viewAppointments=viewAppointmentsScene();
		window.setScene(viewAppointments);	
		
		//scenes to do
		serviceS = serviceScene();
		appointmentS = appointmentScene();
		appointmentSOwner = appointmentSceneOwner();
		businessS = businessScene();
		serviceSOwner = serviceScene();
		garageS = garageScene();
		/**
		 * Alexandre
		 */
		
		

		window.show();

	}

	/**
	 * Alexandre
	 * @return
	 */
	private static ObservableList<String> getBookedTime() {

		ObservableList<String> list = FXCollections.observableArrayList();

//		for ( int i=0 ; i<CarShopController.getTimeSlots().size();i++) {
//			if(CarShopController.getTimeSlots().get(i) != null) {
//				list.add(CarShopController.getTimeSlots().get(i));
//			}
		
		for ( int i=0 ; i< CarShopController.getApp().size() ; i++) {
			if(viewDatePicker.getValue()!= null) {
				System.out.println(viewDatePicker.getValue());
				if (CarShopController.getStartDateApp(CarShopController.getApp().get(i)).equals(viewDatePicker.getValue())) {
					
					list.add(CarShopController.getApp().get(i).getBookableService().getName());
				}
			}
		}

		return list;
	}

	private static void refreshTOTimeSlots() {
		bookedTime.setItems(getBookedTime());
		//bookedTime.setItems(getUnavTOTimeSlotData());
	}
	
	public static Scene viewAppointmentsScene() {
		// TODO Auto-generated method stub
		viewDate = new Text("Date: ");
		viewDate.setFill(Color.rgb(0 ,0 , 0));
		viewDate.setFont(Font.font("Comforta", 15));
		viewDatePicker = new DatePicker();
		viewDatePicker.setPromptText("dd-mm-yyyy");
		dailyToggleButton = new ToggleButton("Daily view");
		dailyToggleButton.setSelected(true);
		weeklyToggleButton = new ToggleButton("weekly view");
		toggleGroup = new ToggleGroup();

		dailyToggleButton.setToggleGroup(toggleGroup);
		weeklyToggleButton.setToggleGroup(toggleGroup);

		topTable = new HBox(viewDate, viewDatePicker, dailyToggleButton, weeklyToggleButton);
		topTable.setAlignment(Pos.CENTER);

		unavailableTSCol = new TableColumn ("Booked Time Slots");

		unavailableDateCol = new TableColumn<TOTimeSlot, Date>("Date");
		unavailableDateCol.setMinWidth(250);
		unavailableDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));

		unavailableStartTimeCol = new TableColumn<TOTimeSlot, Time>("Start Time");
		unavailableStartTimeCol.setMinWidth(250);
		unavailableStartTimeCol.setCellValueFactory(new PropertyValueFactory<>("startTime"));

		unavailableEndTimeCol = new TableColumn<TOTimeSlot, Time>("End Time");
		unavailableEndTimeCol.setMinWidth(250);
		unavailableEndTimeCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));




		unavailableTSCol.getColumns().addAll(unavailableDateCol, unavailableStartTimeCol, unavailableEndTimeCol);
		bookedTime = new TableView<String>();
		
		bookedTime.getColumns().addAll(unavailableTSCol);

		for(int i =0; i<unavailableTSCol.getColumns().size(); i++) {
			((TableColumn) unavailableTSCol.getColumns().get(i)).setStyle("-fx-background-color:orange");

		}

		timeSlotTables = new HBox();
		
		timeSlotTables.getChildren().addAll( bookedTime);

		viewDatePicker.setOnAction(e->{
			refreshTOTimeSlots();

		});

		dailyToggleButton.setOnAction(e->{
			refreshTOTimeSlots();
		});

		weeklyToggleButton.setOnAction(e->{
			refreshTOTimeSlots();		
		});

		timeSlotTables.setAlignment(Pos.CENTER);
		Label l=new Label("View appointments in detail");
		l.setAlignment(Pos.CENTER);
		l.setStyle("-fx-color:orange");
		
		back = new Button("back");
		back.setOnAction(e->{
			if(CarShopApplication.getCurrentUser().getUsername().equals("owner")){
				
				
				
				 window = (Stage) ((Node) e.getSource()).getScene().getWindow();
				 
				 
				File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/OwnerS.fxml");
				String filepath = filep.getAbsolutePath();
				Parent fileP;
				try {
					fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
					Scene ownerS = new Scene(fileP, 800, 600);
					window.setScene(ownerS);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//customerS = new Scene(fileP, 800, 600);
				 
				 //window.setScene(View.viewAppointmentsScene());

			}
			
			
			if(CarShopController.isCustomer(CarShopController.getCurrUser())){
								
				 window = (Stage) ((Node) e.getSource()).getScene().getWindow();
				 
				 
				File filep = new File("src/main/java/ca/mcgill/ecse/carshop/view/CustomerS.fxml");
				String filepath = filep.getAbsolutePath();
				Parent fileP;
				try {
					fileP = FXMLLoader.load(new File(filepath).toURI().toURL());
					Scene customerS = new Scene(fileP, 800, 600);
					window.setScene(customerS);
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				//customerS = new Scene(fileP, 800, 600);
				 
				// window.setScene(View.viewAppointmentsScene());

			}
			
		});
		
		back.setAlignment(Pos.CENTER);
		HBox box = new HBox(back);
		
		
		VBox f = new VBox(topTable , timeSlotTables, box);
		
		
		viewAppointments= new Scene(f,800,600);
		return viewAppointments;
		
	}

	private Scene garageScene() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Scene technicianScene() {
		
		Label homel = new Label("How can we help you today?");
		
		homel.setStyle("-fx-font: 20 arial; -fx-font-weight: bold");
		homel.setId("title-label");
		GridPane.setConstraints(homel,50,5);
		
		
		Button garageb = new Button("Garage hours");
		//addsb.setTextFill();
		//.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.AUTOMOBILE,"60px"));
		
		
		garageb.setOnAction(e-> window.setScene(garageS));
			
		
		
		
			
		
		//BorderPane bp=new BorderPane();
		HBox box = new HBox(10);
		box.setSpacing(100);
		//bp.setCenter(GlyphsDude.createIcon(FontAwesomeIcon.AMBULANCE,"40px"));
		box.getChildren().addAll(garageb);
		GridPane.setConstraints(box, 20, 30);
		
		//BorderPane b = ;
		
		Button signoutb = new Button("Sign out");
		GridPane.setConstraints(signoutb,30,30);

		signoutb.setOnAction(e->{
			CarShopController.signOut();
			window.setScene(loginS);

		});

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);  
		grid.setHgap(10);


		grid.getChildren().addAll(homel, signoutb, box);
		
		//bp.setCenter(box);
		//BorderPane.setAlignment(box, Pos.TOP_CENTER);
		
		technicianS = new Scene(grid,1800,800);

		return technicianS;
	

}

	private Scene businessScene() {
		// TODO Auto-generated method stub
		return null;
	}

	private Scene appointmentSceneOwner() {
		// TODO Auto-generated method stub
		return null;
	}

	private Scene appointmentScene() {
		// TODO Auto-generated method stub
		return null;
	}

	private Scene serviceScene() {
		// TODO Auto-generated method stub
		return null;
	}

	private static Scene signupScene() {

		Label signupl = new Label("We are happy to see you at carshop");
		signupl.setId("title-label");
		GridPane.setConstraints(signupl, 0,1);


		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);  
		grid.setHgap(10); 

		
		// Name label
		Label nameLabel = new Label("Username");
		GridPane.setConstraints(nameLabel, 9, 5);


		// Name input
		TextField nameInput = new TextField();
		nameInput.setPromptText("Your name here");
		GridPane.setConstraints(nameInput, 10, 5);
		nameLabel.setOnMouseClicked(e -> nameInput.requestFocus() );

		// Password label
		Label passwordLabel = new Label("Password");
		passwordLabel.setOnMouseClicked(e -> password.requestFocus());
		GridPane.setConstraints(passwordLabel, 9, 6);



		// Password Input
		PasswordField password = new PasswordField();
		password.setPromptText("Password here");
		GridPane.setConstraints(password, 10, 6);



		// Password label
		Label confirml = new Label("Confirm password");
		confirml.setOnMouseClicked(e -> confirml.requestFocus());
		GridPane.setConstraints(confirml, 9, 7);


		// confirm password Input
		PasswordField confirmpassword = new PasswordField();
		confirmpassword.setPromptText("Confirm password");
		GridPane.setConstraints(confirmpassword, 10, 7);



		Button signupb = new Button("sign up");
		signupb.setOnAction(e->{

			String username = nameInput.getText();
			String pass = password.getText();
			String check = confirmpassword.getText();


			boolean done = true;

			if(Customer.hasWithUsername(username)) {
				//pop up that a user with this username exists already
				Alert nosignup = new Alert(Alert.AlertType.ERROR);
				nosignup.setHeaderText(null);
				nosignup.setContentText("Username is already taken");
				nosignup.show();
				done=false;
			}


			else {
				if(!pass.equals(check)) {
					Alert nosignup = new Alert(Alert.AlertType.ERROR);
					nosignup.setHeaderText(null);
					nosignup.setContentText("Passwords don't match");
					nosignup.show();
					done=false;
					//popup that password is not the same
				}
			}

			try {
				CarShopController.signUpCustomer(username, pass);

				if(done) window.setScene(loginS);

			} catch (InvalidInputException e1) {
				window.setScene(signupS);
			}


		});
		GridPane.setConstraints(signupb,10,8);

		grid.getChildren().addAll(signupl, nameLabel, nameInput, passwordLabel, password, signupb, confirmpassword, confirml);

		signupS = new Scene(grid,2000,800);
		return signupS;

	}


	private static Scene ownerScene() {
		Label homel = new Label("How can we help you today?");
		
		homel.setStyle("-fx-font: 20 arial; -fx-font-weight: bold");
		homel.setId("title-label");
		GridPane.setConstraints(homel,50,5);
		
		
		Button serviceb = new Button("Service");
		//addsb.setTextFill();
		//serviceb.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.AUTOMOBILE,"60px"));
		serviceb.setOnAction(e-> window.setScene(serviceSOwner));
		
		Button businessb = new Button("Business");
		//addsb.setTextFill();
		//businessb.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.DOLLAR,"60px"));
		businessb.setOnAction(e-> window.setScene(businessS));
		
		Button appointmentb = new Button("Appointments");
		//addsb.setTextFill();
		//appointmentb.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CALENDAR,"60px"));
		appointmentb.setOnAction(e-> window.setScene(appointmentSOwner));
		
		
		
		//BorderPane bp=new BorderPane();
		HBox box = new HBox(10);
		box.setSpacing(100);
		//bp.setCenter(GlyphsDude.createIcon(FontAwesomeIcon.AMBULANCE,"40px"));
		box.getChildren().addAll(serviceb,businessb,appointmentb);
		GridPane.setConstraints(box, 20, 30);
		
		//BorderPane b = ;
		
		Button signoutb = new Button("Sign out");
		GridPane.setConstraints(signoutb,30,30);

		signoutb.setOnAction(e->{
			CarShopController.signOut();
			window.setScene(loginS);

		});

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);  
		grid.setHgap(10);


		grid.getChildren().addAll(homel, signoutb,box);
		
		//bp.setCenter(box);
		//BorderPane.setAlignment(box, Pos.TOP_CENTER);
		
		ownerS = new Scene(grid,1800,800);

		return ownerS;
	}
	
	
	private static Scene customerScene() {
	
			Label homel = new Label("How can we help you today?");
			
			homel.setStyle("-fx-font: 20 arial; -fx-font-weight: bold");
			homel.setId("title-label");
			GridPane.setConstraints(homel,50,5);
			
			
			Button serviceb = new Button("Services");
			//addsb.setTextFill();
			//serviceb.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.AUTOMOBILE,"60px"));
			
			
			serviceb.setOnAction(e-> window.setScene(serviceS));
				
			
			
			Button appointmentb = new Button("Appointments");
			//addsb.setTextFill();
			//appointmentb.setGraphic(GlyphsDude.createIcon(FontAwesomeIcon.CALENDAR,"60px"));
			
			appointmentb.setOnAction(e-> window.setScene(appointmentS));
				
			
			//BorderPane bp=new BorderPane();
			HBox box = new HBox(10);
			box.setSpacing(100);
			//bp.setCenter(GlyphsDude.createIcon(FontAwesomeIcon.AMBULANCE,"40px"));
			box.getChildren().addAll(serviceb,appointmentb);
			GridPane.setConstraints(box, 20, 30);
			
			//BorderPane b = ;
			
			Button signoutb = new Button("Sign out");
			GridPane.setConstraints(signoutb,30,30);

			signoutb.setOnAction(e->{
				CarShopController.signOut();
				window.setScene(loginS);

			});

			GridPane grid = new GridPane();
			grid.setPadding(new Insets(10, 10, 10, 10));
			grid.setVgap(10);  
			grid.setHgap(10);


			grid.getChildren().addAll(homel, signoutb, box);
			
			//bp.setCenter(box);
			//BorderPane.setAlignment(box, Pos.TOP_CENTER);
			
			customerS = new Scene(grid,1800,800);

			return customerS;
		

	}
	


	private static Scene loginScene() throws FileNotFoundException {

		GridPane grid = new GridPane();
		grid.setPadding(new Insets(10, 10, 10, 10));
		grid.setVgap(10);  
		grid.setHgap(10); 


//		//Passing FileInputStream object as a parameter 
//		FileInputStream inputstream = new FileInputStream("C:\\Users\\alymo\\Desktop\\pic.jpg"); 
//		Image image = new Image(inputstream);
//		ImageView imageView = new ImageView(image);
//			imageView.setVisible(true);
//		stage.getIcons().add(new Image(getClass().getResourceAsStream("pic.jpg")));
//
//		GridPane.setConstraints(imageView,0,0);	


		Label welcomel = new Label("Welcome to our carshop");
		welcomel.setId("title-label");
		GridPane.setConstraints(welcomel,42,4);


		Label infol = new Label("carshop information");
		GridPane.setConstraints(infol, 44, 14);

		// Name label
		Label nameLabel = new Label("Username");
		GridPane.setConstraints(nameLabel, 40, 15);


		// Name input
		TextField nameInput = new TextField();
		nameInput.setPromptText("Your name here");
		GridPane.setConstraints(nameInput, 41, 15);
		nameLabel.setOnMouseClicked(e -> nameInput.requestFocus() );

		// Password label
		Label passwordLabel = new Label("Password");
		passwordLabel.setOnMouseClicked(e -> password.requestFocus());
		GridPane.setConstraints(passwordLabel, 40, 16);


		Hyperlink signuph = new Hyperlink("Don't have an account? Sign up");
		signuph.setBorder(null);


		GridPane.setConstraints(signuph,41,18);
		signuph.setOnAction(e->window.setScene(signupS));


		// Password Input
		PasswordField password = new PasswordField();
		password.setPromptText("Password here");
		//		password.setVisible(true);
		GridPane.setConstraints(password, 41, 16);


		//GridPane.setConstraints(nologin, 10, 20);


		Button loginb = new Button("login");
		//		loginb.setGraphic(imageView);
		loginb.setOnAction(e->{

			String username = nameInput.getText();
			String pass = password.getText();

			try {
				CarShopController.userLogin(username, pass);
				if(username.equals(CarShopApplication.getCarShop().getOwner().getUsername()) && pass.equals(CarShopApplication.getCarShop().getOwner().getPassword())) {
					window.setScene(ownerS);
				}
				else if(Customer.hasWithUsername(username)) {
					window.setScene(customerS);
				}
				else {//user is a technician
					window.setScene(technicianS);
				}
			}	

			catch(Exception exception) {

				Alert nologin = new Alert(Alert.AlertType.ERROR);
				nologin.setHeaderText(null);
				nologin.setContentText(exception.getMessage());
				nologin.show();
				//pop up error message
			}

		});
		GridPane.setConstraints(loginb,41,17);

		grid.getChildren().addAll(infol, signuph, welcomel, nameLabel, nameInput, passwordLabel, password, loginb);

		loginS = new Scene(grid,1800,800);

		return loginS;

	}


	public static void main(String[] args) {
		launch(args);
	}

	




}
