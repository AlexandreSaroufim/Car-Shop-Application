package ca.mcgill.ecse.carshop.view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;

public class FxController implements Initializable{

	
	@FXML
	ComboBox<String> comboBox;
	
	 private ObservableList<String> dbTypeList = FXCollections.observableArrayList("SQLite");
	 
	    @Override
	    public void initialize(URL location, ResourceBundle resources) {
	        comboBox.setItems(dbTypeList);
	    }
	}

	
//	@Override
//	public void initialize(URL arg0, ResourceBundle arg1) {
//	
//		comboBox.getItems().removeAll(comboBox.getItems());
//	    comboBox.getItems().addAll("Option A", "Option B", "Option C");
//	    comboBox.getSelectionModel().select("Option B");
//		
//	}

	
	
//}
