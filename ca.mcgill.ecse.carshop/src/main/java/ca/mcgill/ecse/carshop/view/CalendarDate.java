package ca.mcgill.ecse.carshop.view;

import java.time.LocalDate;
import com.jfoenix.controls.JFXButton;

public class CalendarDate extends JFXButton{

	private LocalDate d;
	public CalendarDate(String d){
        super(d);
    }
	
	public LocalDate getDate() {
        return d;
    }
	
	public void setDate(LocalDate date) {
        d = date;
    }
}
