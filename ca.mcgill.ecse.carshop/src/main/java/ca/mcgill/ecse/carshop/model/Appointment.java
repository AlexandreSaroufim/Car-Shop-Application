/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.model;
import java.sql.Time;
import java.sql.Date;
import ca.mcgill.ecse.carshop.application.CarShopApplication;
import ca.mcgill.ecse.carshop.controller.InvalidInputException;
import ca.mcgill.ecse.carshop.controller.CarShopController;
import ca.mcgill.ecse.carshop.persistence.CarshopPersistence;
import java.io.Serializable;
import java.util.*;

// line 8 "../../../../../carshopStates.ump"
// line 109 "../../../../../CarshopPersistence.ump"
// line 102 "../../../../../carshop.ump"
public class Appointment implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Appointment State Machines
  public enum AppointmentStatus { Booked, Final, InProgress }
  private AppointmentStatus appointmentStatus;

  //Appointment Associations
  private Customer customer;
  private BookableService bookableService;
  private List<ServiceBooking> serviceBookings;
  private CarShop carShop;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Appointment(Customer aCustomer, BookableService aBookableService, CarShop aCarShop)
  {
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create appointment due to customer. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBookableService = setBookableService(aBookableService);
    if (!didAddBookableService)
    {
      throw new RuntimeException("Unable to create appointment due to bookableService. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    serviceBookings = new ArrayList<ServiceBooking>();
    boolean didAddCarShop = setCarShop(aCarShop);
    if (!didAddCarShop)
    {
      throw new RuntimeException("Unable to create appointment due to carShop. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    setAppointmentStatus(AppointmentStatus.Booked);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public String getAppointmentStatusFullName()
  {
    String answer = appointmentStatus.toString();
    return answer;
  }

  public AppointmentStatus getAppointmentStatus()
  {
    return appointmentStatus;
  }

  public boolean modifyAppointmentInfo(String user,String newService,String newDate,String newTime,String newOptSer,String newOptSerTime)
  {
    boolean wasEventProcessed = false;
    
    AppointmentStatus aAppointmentStatus = appointmentStatus;
    switch (aAppointmentStatus)
    {
      case Booked:
        if (atleastDayBefore(stringToDate(CarShopApplication.getSystemDate())))
        {
        // line 20 "../../../../../carshopStates.ump"
          try {
			updateAppointment(CarShopApplication.getCarShop(), this, user, newService, newDate, newTime, newOptSer, newOptSerTime);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          setAppointmentStatus(AppointmentStatus.Booked);
          wasEventProcessed = true;
          break;
        }
        if (!(atleastDayBefore(stringToDate(CarShopApplication.getSystemDate()))))
        {
        // line 29 "../../../../../carshopStates.ump"
          rejectUpdateAppointment();
          setAppointmentStatus(AppointmentStatus.Booked);
          wasEventProcessed = true;
          break;
        }
        break;
      case InProgress:
        if (newService.equals(null)&&newDate.equals(null)&&newTime.equals(null))
        {
        // line 75 "../../../../../carshopStates.ump"
          try {
			updateAppointment(CarShopApplication.getCarShop(), this, user, newService, newDate, newTime, newOptSer, newOptSerTime);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          setAppointmentStatus(AppointmentStatus.InProgress);
          wasEventProcessed = true;
          break;
        }
        if (!newService.equals(null)||!newDate.equals(null)||!newTime.equals(null))
        {
        // line 83 "../../../../../carshopStates.ump"
          rejectUpdateAppointment();
          setAppointmentStatus(AppointmentStatus.InProgress);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean doCancelAppointment(String loggedInUser,String userOfAppointment,String appointmentType,String datePassed,String timePassed) throws InvalidInputException
  {
    boolean wasEventProcessed = false;
    
    AppointmentStatus aAppointmentStatus = appointmentStatus;
    switch (aAppointmentStatus)
    {
      case Booked:
        if (atleastDayBefore(this.getServiceBooking(0).getTimeSlot().getStartDate()))
        {
        // line 37 "../../../../../carshopStates.ump"
          cancelAppointment(loggedInUser, userOfAppointment, appointmentType, appointmentType, timePassed);
          setAppointmentStatus(AppointmentStatus.Final);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean cancelAppointment()
  {
    boolean wasEventProcessed = false;
    
    AppointmentStatus aAppointmentStatus = appointmentStatus;
    switch (aAppointmentStatus)
    {
      case Booked:
        if (!(atleastDayBefore(this.getServiceBooking(0).getTimeSlot().getStartDate())))
        {
        // line 40 "../../../../../carshopStates.ump"
          rejectCancelAppointment();
          setAppointmentStatus(AppointmentStatus.Booked);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean startAppointment(Date systemDate,Time systemTime)
  {
    boolean wasEventProcessed = false;
    
    AppointmentStatus aAppointmentStatus = appointmentStatus;
    switch (aAppointmentStatus)
    {
      case Booked:
        if (isAfterStartTime(stringToDate(CarShopApplication.getSystemDate()),StringToTime(CarShopApplication.getSystemDate())))
        {
        // line 45 "../../../../../carshopStates.ump"
          startAppointment();
          setAppointmentStatus(AppointmentStatus.InProgress);
          wasEventProcessed = true;
          break;
        }
        if (!(isAfterStartTime(stringToDate(CarShopApplication.getSystemDate()),StringToTime(CarShopApplication.getSystemDate()))))
        {
        // line 49 "../../../../../carshopStates.ump"
          rejectStartAppointment();
          setAppointmentStatus(AppointmentStatus.Booked);
          wasEventProcessed = true;
          break;
        }
        break;
      case InProgress:
        // line 87 "../../../../../carshopStates.ump"
        rejectStartAppointment();
        setAppointmentStatus(AppointmentStatus.InProgress);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean noShow(Customer customer)
  {
    boolean wasEventProcessed = false;
    
    AppointmentStatus aAppointmentStatus = appointmentStatus;
    switch (aAppointmentStatus)
    {
      case Booked:
        if (stringToDate(CarShopApplication.getSystemDate()).equals(this.getStartTime())||isAfterStartTime(stringToDate(CarShopApplication.getSystemDate()),StringToTime(CarShopApplication.getSystemDate())))
        {
        // line 54 "../../../../../carshopStates.ump"
          addNoShow(this.getCustomer());
	      		removeAppointment();
          setAppointmentStatus(AppointmentStatus.Final);
          wasEventProcessed = true;
          break;
        }
        if (!(isAfterStartTime(stringToDate(CarShopApplication.getSystemDate()),StringToTime(CarShopApplication.getSystemDate()))))
        {
        // line 58 "../../../../../carshopStates.ump"
          rejectNoShow();
          setAppointmentStatus(AppointmentStatus.Booked);
          wasEventProcessed = true;
          break;
        }
        break;
      case InProgress:
        // line 91 "../../../../../carshopStates.ump"
        rejectNoShow();
        setAppointmentStatus(AppointmentStatus.Booked);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean endAppointment()
  {
    boolean wasEventProcessed = false;
    
    AppointmentStatus aAppointmentStatus = appointmentStatus;
    switch (aAppointmentStatus)
    {
      case InProgress:
        if (StringToTime(CarShopApplication.getSystemDate()).equals(this.getEndTime())||StringToTime(CarShopApplication.getSystemDate()).after(this.getEndTime()))
        {
        // line 66 "../../../../../carshopStates.ump"
          removeAppointment();
          setAppointmentStatus(AppointmentStatus.Final);
          wasEventProcessed = true;
          break;
        }
        if (!(StringToTime(CarShopApplication.getSystemDate()).equals(this.getEndTime()))||!(StringToTime(CarShopApplication.getSystemDate()).after(this.getEndTime())))
        {
        // line 69 "../../../../../carshopStates.ump"
          rejectEndAppointment();
          setAppointmentStatus(AppointmentStatus.InProgress);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setAppointmentStatus(AppointmentStatus aAppointmentStatus)
  {
    appointmentStatus = aAppointmentStatus;

    // entry actions and do activities
    switch(appointmentStatus)
    {
      case Final:
        delete();
        break;
    }
  }
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
  }
  /* Code from template association_GetOne */
  public BookableService getBookableService()
  {
    return bookableService;
  }
  /* Code from template association_GetMany */
  public ServiceBooking getServiceBooking(int index)
  {
    ServiceBooking aServiceBooking = serviceBookings.get(index);
    return aServiceBooking;
  }

  public List<ServiceBooking> getServiceBookings()
  {
    List<ServiceBooking> newServiceBookings = Collections.unmodifiableList(serviceBookings);
    return newServiceBookings;
  }

  public int numberOfServiceBookings()
  {
    int number = serviceBookings.size();
    return number;
  }

  public boolean hasServiceBookings()
  {
    boolean has = serviceBookings.size() > 0;
    return has;
  }

  public int indexOfServiceBooking(ServiceBooking aServiceBooking)
  {
    int index = serviceBookings.indexOf(aServiceBooking);
    return index;
  }
  /* Code from template association_GetOne */
  public CarShop getCarShop()
  {
    return carShop;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCustomer(Customer aCustomer)
  {
    boolean wasSet = false;
    if (aCustomer == null)
    {
      return wasSet;
    }

    Customer existingCustomer = customer;
    customer = aCustomer;
    if (existingCustomer != null && !existingCustomer.equals(aCustomer))
    {
      existingCustomer.removeAppointment(this);
    }
    customer.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBookableService(BookableService aBookableService)
  {
    boolean wasSet = false;
    if (aBookableService == null)
    {
      return wasSet;
    }

    BookableService existingBookableService = bookableService;
    bookableService = aBookableService;
    if (existingBookableService != null && !existingBookableService.equals(aBookableService))
    {
      existingBookableService.removeAppointment(this);
    }
    bookableService.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfServiceBookings()
  {
    return 0;
  }
  /* Code from template association_AddManyToOne */
  public ServiceBooking addServiceBooking(Service aService, TimeSlot aTimeSlot)
  {
    return new ServiceBooking(aService, aTimeSlot, this);
  }

  public boolean addServiceBooking(ServiceBooking aServiceBooking)
  {
    boolean wasAdded = false;
    if (serviceBookings.contains(aServiceBooking)) { return false; }
    Appointment existingAppointment = aServiceBooking.getAppointment();
    boolean isNewAppointment = existingAppointment != null && !this.equals(existingAppointment);
    if (isNewAppointment)
    {
      aServiceBooking.setAppointment(this);
    }
    else
    {
      serviceBookings.add(aServiceBooking);
    }
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeServiceBooking(ServiceBooking aServiceBooking)
  {
    boolean wasRemoved = false;
    //Unable to remove aServiceBooking, as it must always have a appointment
    if (!this.equals(aServiceBooking.getAppointment()))
    {
      serviceBookings.remove(aServiceBooking);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addServiceBookingAt(ServiceBooking aServiceBooking, int index)
  {  
    boolean wasAdded = false;
    if(addServiceBooking(aServiceBooking))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfServiceBookings()) { index = numberOfServiceBookings() - 1; }
      serviceBookings.remove(aServiceBooking);
      serviceBookings.add(index, aServiceBooking);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveServiceBookingAt(ServiceBooking aServiceBooking, int index)
  {
    boolean wasAdded = false;
    if(serviceBookings.contains(aServiceBooking))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfServiceBookings()) { index = numberOfServiceBookings() - 1; }
      serviceBookings.remove(aServiceBooking);
      serviceBookings.add(index, aServiceBooking);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addServiceBookingAt(aServiceBooking, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCarShop(CarShop aCarShop)
  {
    boolean wasSet = false;
    if (aCarShop == null)
    {
      return wasSet;
    }

    CarShop existingCarShop = carShop;
    carShop = aCarShop;
    if (existingCarShop != null && !existingCarShop.equals(aCarShop))
    {
      existingCarShop.removeAppointment(this);
    }
    carShop.addAppointment(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Customer placeholderCustomer = customer;
    this.customer = null;
    if(placeholderCustomer != null)
    {
      placeholderCustomer.removeAppointment(this);
    }
    BookableService placeholderBookableService = bookableService;
    this.bookableService = null;
    if(placeholderBookableService != null)
    {
      placeholderBookableService.removeAppointment(this);
    }
    while (serviceBookings.size() > 0)
    {
      ServiceBooking aServiceBooking = serviceBookings.get(serviceBookings.size() - 1);
      aServiceBooking.delete();
      serviceBookings.remove(aServiceBooking);
    }
    
    CarShop placeholderCarShop = carShop;
    this.carShop = null;
    if(placeholderCarShop != null)
    {
      placeholderCarShop.removeAppointment(this);
    }
  }


  /**
   * line 102 "../../../../../carshopStates.ump"
   */
  // line 109 "../../../../../carshopStates.ump"
   private void rejectNoShow(){
    throw new RuntimeException("Cannot add a no show");
  }


  /**
   * line 106 "../../../../../carshopStates.ump"
   */
  // line 114 "../../../../../carshopStates.ump"
   private void rejectStartAppointment(){
    throw new RuntimeException("Not yet time for appointment");
  }


  /**
   * line 112 "../../../../../carshopStates.ump"
   */
  // line 119 "../../../../../carshopStates.ump"
   private void rejectEndAppointment(){
    throw new RuntimeException("Cannot end an appointment in progress");
  }


  /**
   * line 117 "../../../../../carshopStates.ump"
   */
  // line 124 "../../../../../carshopStates.ump"
   private void rejectCancelAppointment(){
    throw new RuntimeException("Cannot cancel an appointment on the appointment date");
  }


  /**
   * line 121 "../../../../../carshopStates.ump"
   */
  // line 129 "../../../../../carshopStates.ump"
   private void rejectUpdateAppointment(){
    throw new RuntimeException("Cannot change appointment date and time on the appointment date");
  }


  /**
   * line 126 "../../../../../carshopStates.ump"
   * line 109 "../../../../../carshopStates.ump"
   */
  // line 135 "../../../../../carshopStates.ump"
   public static  void updateAppointment(CarShop carshop, Appointment a, String user, String newService, String newDate, String newTime, String newOptSer, String newOptSerTime) throws InvalidInputException{
    if(!user.equals("owner") && Customer.getWithUsername(user)==null) {
			
	   }		
		
		if(a.getAppointmentStatusFullName().equals("Booked")) {
			
			Date today = stringToDate(CarShopApplication.getSystemDate());
			
			
			//we are on the day before
			if(a.getServiceBooking(0).getTimeSlot().getStartDate().after(today)) {
				if(newDate!=null) updateDate(a, newDate, newTime);
				if(newTime!=null) updateTime(a, newTime, newDate);
				if(newService!=null) updateService(carshop, a, newService);
				if(newOptSer!=null) updateServices(a, newOptSer, newOptSerTime);
				if(newOptSer==null && newOptSerTime!=null) updateOptSerTime(a,newOptSerTime);
			}			
			
			else {
				
				
			}
		}
		
		if(user.equals("owner")) {
			
		}
  }

  // line 165 "../../../../../carshopStates.ump"
   private static  void updateOptSerTime(Appointment a, String newOptSerTime){
    Time t = StringToTime(newOptSerTime);
	a.getServiceBooking(a.getServiceBookings().size()-1).getTimeSlot().setStartTime(t);
	a.getServiceBooking(a.getServiceBookings().size()-1).getTimeSlot().setEndTime(new Time(t.getTime()+(((Service) a.getServiceBooking(1).getService()).getDuration()*60*1000)));
  }


  /**
   * line 138 "../../../../../carshopStates.ump"
   */
  // line 174 "../../../../../carshopStates.ump"
   private static  void updateServices(Appointment a, String newOptSer, String newOptSerTime) throws InvalidInputException{
    // TODO Auto-generated method stub
		   String[] services = newOptSer.split(",");
		   String[] times= newOptSerTime.split(",");
		   for(int i = 0; i<services.length;i++) {
			 //  a.getServiceBooking(i+1).setService((Service) Service.getWithName(newOptSer));
			   Service s = (Service) Service.getWithName(services[i]);
			   Date d = a.getServiceBooking(0).getTimeSlot().getStartDate();
			   Time start = CarShopController.StringToTime(times[i]);
			   //Time end = new Time(t[0].getTime() + (ser.getDuration() * 60 * 1000));
			   Time end = new Time(start.getTime() + s.getDuration()* 60 * 1000);
			   TimeSlot ts = new TimeSlot(d, start, d, end, CarShopApplication.getCarShop());
			   
			  
			   if(!isOverlapping(ts, a)) {
				   
				ServiceBooking sb = new ServiceBooking(s, ts, a);			
				ts.setEndTime(new Time(ts.getStartTime().getTime() +(sb.getService().getDuration()*60*1000)));
			   }
			   else {
				   throw new InvalidInputException("Cannot add these optional services");

			   }
			   
			   
			   }
  }

  // line 204 "../../../../../carshopStates.ump"
   private static  boolean isOverlapping(TimeSlot ts, Appointment a){
    for(ServiceBooking sb: a.getServiceBookings()) {
			   if(ts.getStartTime().after(sb.getTimeSlot().getStartTime()) && ts.getStartTime().before(sb.getTimeSlot().getEndTime())) {
				   return true;
			   }
			   if(ts.getEndTime().after(sb.getTimeSlot().getStartTime()) && ts.getEndTime().before(sb.getTimeSlot().getEndTime())) {
				   return true;
			   }
			  
		   }
		   return false;
  }


  /**
   * line 143 "../../../../../carshopStates.ump"
   */
  // line 221 "../../../../../carshopStates.ump"
   private static  void updateTime(Appointment a, String newTime, String newDate){
    //only use this method when there is only one service
		   boolean stillUpdate = true;
		   Time t = StringToTime(newTime);
		   if(newDate!=null) {
			   Date d = stringToDate(newDate);
			   
			// cannot be during a holiday
				List<TimeSlot> holidays = CarShopApplication.getCarShop().getBusiness().getHolidays();
				for (TimeSlot h : holidays) {
					//for (Time time : t) {
						if ((d.after(h.getStartDate()) && d.before(h.getEndDate()))
						|| (!d.before(h.getStartDate()) && !d.after(h.getStartDate()) && t.after(h.getStartTime()))
						|| (!d.before(h.getEndDate()) && !d.after(h.getEndDate()) && t.before(h.getEndTime()))) {
							stillUpdate = false;
						}
					//}
				}
				
				//CHECK VACATION
				for(TimeSlot v: a.getCarShop().getBusiness().getVacations()) {
					if ((d.after(v.getStartDate()) && d.before(v.getEndDate()))
					|| (!d.before(v.getStartDate()) && !d.after(v.getStartDate()) && t.after(v.getStartTime()))
					|| (!d.before(v.getEndDate()) && !d.after(v.getEndDate()) && t.before(v.getEndTime()))) {
						stillUpdate = false;
					}
				}
		   }
		   /*List<BusinessHour> hours = a.getCarShop().getBusiness().getBusinessHours();
		   Time[] tArr= new Time[1];
		   tArr[0] = CarShopController.StringToTime(newTime);//a.getServiceBooking(0).getTimeSlot().getStartTime();
		   Calendar c = Calendar.getInstance();
		   c.setTime(a.getServiceBooking(0).getTimeSlot().getStartDate());
		   stillUpdate = CarShopController.checkIfInHours(hours, tArr, c);*/
		   if(stillUpdate) {
			   
			   ServiceBooking sb  =a.getServiceBooking(0);
			   TimeSlot ts = sb.getTimeSlot();
			   ts.setStartTime(t);
			   ts.setEndTime(new Time(t.getTime() + (sb.getService().getDuration() * 60 * 1000)));
	   
		   }
  }


  /**
   * line 152 "../../../../../carshopStates.ump"
   */
  // line 266 "../../../../../carshopStates.ump"
   private static  void updateDate(Appointment a, String newDate, String newTime){
    Date d = stringToDate(newDate);
    boolean update = true;
    
    
 // cannot be during a holiday
    if(newTime!=null) {
		List<TimeSlot> holidays = CarShopApplication.getCarShop().getBusiness().getHolidays();
	    Time time = StringToTime(newTime);
		for (TimeSlot h : holidays) {
			//for (Time time : t) {
				if ((d.after(h.getStartDate()) && d.before(h.getEndDate()))
				|| (!d.before(h.getStartDate()) && !d.after(h.getStartDate()) && (time.equals(h.getStartTime())||time.after(h.getStartTime())))
				|| (!d.before(h.getEndDate()) && !d.after(h.getEndDate()) && time.before(h.getEndTime()))) {
					update = false;
				}
			//}
		}
		
		//CHECK VACATION
		for(TimeSlot v: a.getCarShop().getBusiness().getVacations()) {
			if ((d.after(v.getStartDate()) && d.before(v.getEndDate()))
			|| (!d.before(v.getStartDate()) && !d.after(v.getStartDate()) && time.after(v.getStartTime()))
			|| (!d.before(v.getEndDate()) && !d.after(v.getEndDate()) && time.before(v.getEndTime()))) {
				update = false;
			}
		}
    }
    if(update) {
		for(ServiceBooking sb : a.getServiceBookings()) {
			TimeSlot ts = sb.getTimeSlot();
			ts.setStartDate(d);
			ts.setEndDate(d);			
		}
    }
  }


  /**
   * line 162 "../../../../../carshopStates.ump"
   */
  // line 304 "../../../../../carshopStates.ump"
   private static  void updateService(CarShop carshop, Appointment appointment, String newService){
    // TODO Auto-generated method stub
	if(BookableService.getWithName(newService) instanceof Service) {
		//check time
		ServiceBooking sb = appointment.getServiceBooking(0);
		ServiceBooking sb2 = appointment.getServiceBooking(appointment.getServiceBookings().size()-1);
		
		
		TimeSlot ts = new TimeSlot(sb.getTimeSlot().getStartDate(), sb.getTimeSlot().getStartTime(), sb2.getTimeSlot().getEndDate(), sb2.getTimeSlot().getEndTime(), carshop);
	
		if(!isOverlapping(ts, appointment)) {
		
		
		
		List<BusinessHour> hours = appointment.getCarShop().getGarage(0).getBusinessHours();
		Time[] tArr= new Time[1];
		tArr[0] = new Time(appointment.getStartTime().getTime()+(((Service) BookableService.getWithName(newService)).getDuration()*60*1000));
		Calendar c = Calendar.getInstance();
		Date d = appointment.getServiceBooking(0).getTimeSlot().getStartDate();
		c.setTime(d);
		boolean stillUpdate = CarShopController.checkIfInHours(hours, tArr, c);
		
		if(stillUpdate) stillUpdate = !isOverlapping(new TimeSlot(d, appointment.getStartTime(), d, appointment.getEndTime(), carshop), appointment);
		
		if(stillUpdate) {

			BookableService tmp = BookableService.getWithName(newService);
			
			
			appointment.setBookableService(BookableService.getWithName(newService));
			appointment.getServiceBooking(0).setService((Service) Service.getWithName(newService));
			//ServiceBooking sb = appointment.getServiceBooking(0);
			//TimeSlot ts = sb.getTimeSlot();
			Time temp = new Time((ts.getStartTime().getTime() +(sb.getService().getDuration()*60*1000)));
			ts.setEndTime(new Time(ts.getStartTime().getTime() +(sb.getService().getDuration()*60*1000)));
		
			
		}
		}
		//do we need to edit the list of service bookings or is that automatic
	}
  }


  /**
   * line 187 "../../../../../carshopStates.ump"
   */
  // line 333 "../../../../../carshopStates.ump"
   public static  Date stringToDate(String string){
    String datee = "";

		for (int i = 0; i < 10; i++) {
			datee += string.charAt(i);
		}

		Date date = Date.valueOf(datee);
		return date;
  }


  /**
   * line 198 "../../../../../carshopStates.ump"
   */
  // line 345 "../../../../../carshopStates.ump"
   private void addNoShow(Customer customer){
    Integer noShows = customer.getNoShows();
		noShows++;
		customer.setNoShows(noShows);
  }


  /**
   * line 208 "../../../../../carshopStates.ump"
   */
  // line 352 "../../../../../carshopStates.ump"
   public Time getStartTime(){
    List<ServiceBooking> serviceBookings=this.getServiceBookings();
	
	if(serviceBookings.size()!=0) {
		String startTime = serviceBookings.get(0).getTimeSlot().getStartTime().toString();
	
		
		//	startTime.toString()
	   for(int i=1; i<serviceBookings.size();i++) {
			if(startTime.compareTo(serviceBookings.get(i).getTimeSlot().getStartTime().toString())>0) {
				startTime = serviceBookings.get(i).getTimeSlot().getStartTime().toString();
			}
		}
	   return StringToTime(startTime);
	}
	else return null;
  }


  /**
   * line 228 "../../../../../carshopStates.ump"
   */
  // line 371 "../../../../../carshopStates.ump"
   public Time getEndTime(){
    List<ServiceBooking> serviceBookings=this.getServiceBookings();
	
	if(serviceBookings.size()!=0) {
		String endTime = serviceBookings.get(0).getTimeSlot().getEndTime().toString();
	
		
		//	startTime.toString()
	   for(int i=1; i<serviceBookings.size();i++) {
			if(endTime.compareTo(serviceBookings.get(i).getTimeSlot().getEndTime().toString())<0) {
				endTime = serviceBookings.get(i).getTimeSlot().getStartTime().toString();
			}
		}
	   return StringToTime(endTime);
	}
	else return null;
  }


  /**
   * line 247 "../../../../../carshopStates.ump"
   */
  // line 390 "../../../../../carshopStates.ump"
   private static  Time StringToTime(String string){
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


  /**
   * 
   * line 112 "../../../../../carshopStates.ump"
   * line 272 "../../../../../carshopStates.ump"
   */
  // line 417 "../../../../../carshopStates.ump"
   private boolean isAfterStartTime(Date systemDate, Time systemTime){
    if(systemDate.equals(this.getServiceBooking(0).getTimeSlot().getStartDate()) && !systemTime.before(this.getStartTime())){
			return true;
		}
		else{
		 	return false;
		}
  }


  /**
   * 
   * line 121 "../../../../../carshopStates.ump"
   * line 282 "../../../../../carshopStates.ump"
   */
  // line 431 "../../../../../carshopStates.ump"
   private boolean atleastDayBefore(Date systemDate){
    if(systemDate.before(this.getServiceBooking(0).getTimeSlot().getStartDate())){
			return true;
		}
		else{
		 	return false;
		}
  }


  /**
   * line 291 "../../../../../carshopStates.ump"
   */
  // line 441 "../../../../../carshopStates.ump"
   private void startAppointment(){
    CarShopApplication.setCurAppointment(this);
  }


  /**
   * line 295 "../../../../../carshopStates.ump"
   */
  // line 446 "../../../../../carshopStates.ump"
   private void removeAppointment(){
    this.delete();
  }

  // line 452 "../../../../../carshopStates.ump"
   public static  void RegisterNoShow(String string) throws Exception{
    Time TimeAttempt = GetStartTimeOfWeirdFormat(string);
	Date dateAttempt = CarShopController.stringToDate(string);

		for (Appointment app: CarShopApplication.getCarShop().getAppointments()) {
			
			if (app.getServiceBooking(0).getTimeSlot().getStartDate().equals(dateAttempt) && !app.getAppointmentStatusFullName().equals("InProgress")) {
				
				if (app.getServiceBooking(0).getTimeSlot().getEndTime().before(TimeAttempt)) {
					app.getCustomer().setNoShows(app.getCustomer().getNoShows()+1);
					app.delete();
				}

			}
			
		}
  }

  // line 472 "../../../../../carshopStates.ump"
   public static  Time GetStartTimeOfWeirdFormat(String string){
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

  // line 487 "../../../../../carshopStates.ump"
   public static  void endAppointment(String date){
    // TODO Auto-generated method stub
		CarShopApplication.setSystemDate(date);
		CarShopApplication.getCarShop().getAppointments().get(CarShopApplication.getCarShop().getAppointments().size()-1).endAppointment();
  }


  /**
   * Simo Benkirane
   */
  // line 495 "../../../../../carshopStates.ump"
   public static  void endAppointment(CarShop carshop, Appointment a) throws Exception{
    if(a == null) {
				throw new Exception("Appointment can't be null");
			}
				
			if(!carshop.getAppointments().contains(a)) {
				throw new Exception("Appointment doesn't exist");
			}
				
			if(!(CarShopApplication.getCurrentUser() instanceof Owner)) {
				throw new Exception("Not authorized for this action");
			}
				
			a.endAppointment();
  }


  /**
   * tada
   */
  // line 515 "../../../../../carshopStates.ump"
   public static  void startAppointment(String date, String user) throws InvalidInputException{
    if (date == null)
			throw new InvalidInputException("Date cannot be null");

		if (!(user.equals("owner"))) {
			throw new InvalidInputException("Not authorized for this action");
		}

		CarShopApplication.setSystemDate(date);
		CarShop carShop = CarShopApplication.getCarShop();
		Appointment appointment = carShop.getAppointment(carShop.getAppointments().size() - 1);

		if (appointment != null) {
			System.out.println("DATE: " + stringToDate(date) + "     " + StringToTime(date));
			appointment.startAppointment(stringToDate(date), StringToTime(date));
		}

		try {
			CarshopPersistence.save(carShop);
		} catch (RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
  }

  // line 542 "../../../../../carshopStates.ump"
   public static  void cancelAppointment(String loggedInUser, String userOfAppointment, String appointmentType, String datePassed, String timePassed) throws InvalidInputException{
    // must be correct user
		if (!loggedInUser.equals(userOfAppointment)) {
			User loggedIn = User.getWithUsername(loggedInUser);
			if (loggedIn instanceof Customer)
				throw new InvalidInputException("A customer can only cancel their own appointments");
			else if (loggedIn instanceof Technician)
				throw new InvalidInputException("A technician cannot cancel an appointment");
			else if (loggedIn instanceof Owner)
				throw new InvalidInputException("An owner cannot cancel an appointment");
		}

		// cannot cancel on same day
		Date today = stringToDate(CarShopApplication.getSystemDate());
		Date date = stringToDate(datePassed);
		if (!date.before(today) && !date.after(today))
			throw new InvalidInputException("Cannot cancel an appointment on the appointment date");

		List<Appointment> arr = CarShopApplication.getCarShop().getAppointments();
		for (Appointment a : arr) {
			if (a.getCustomer().getUsername().equals(userOfAppointment) && a.getBookableService().getName().equals(appointmentType)) {

				a.delete();
				break;
			}

		}

		List<List<Object>> l =  CarShopApplication.getBookingTimes() ;



		for(int i=0; i<CarShopApplication.getBookingTimes().size();i++) {
			if(l.get(i).get(0).toString().equals(datePassed) 
					&& l.get(i).get(1).toString().equals(timePassed) &&
					l.get(i).get(4).toString().equals(userOfAppointment)) {
				l.remove(i);
			}
		}

		try {
			CarshopPersistence.save(CarShopApplication.getCarShop());
		}
		catch(RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
  }
  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 112 "../../../../../CarshopPersistence.ump"
  private static final long serialVersionUID = -787794828861295940L ;

  
}