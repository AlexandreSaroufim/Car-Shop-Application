/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.model;
import java.io.Serializable;
import java.util.*;

// line 13 "../../../../../CarshopPersistence.ump"
// line 18 "../../../../../carshop.ump"
public abstract class User implements Serializable
{

  //------------------------
  // STATIC VARIABLES
  //------------------------

  private static Map<String, User> usersByUsername = new HashMap<String, User>();

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //User Attributes
  private String username;
  private String password;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public User(String aUsername, String aPassword)
  {
    password = aPassword;
    if (!setUsername(aUsername))
    {
      throw new RuntimeException("Cannot create due to duplicate username. See http://manual.umple.org?RE003ViolationofUniqueness.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setUsername(String aUsername)
  {
    boolean wasSet = false;
    String anOldUsername = getUsername();
    if (anOldUsername != null && anOldUsername.equals(aUsername)) {
      return true;
    }
    if (hasWithUsername(aUsername)) {
      return wasSet;
    }
    username = aUsername;
    wasSet = true;
    if (anOldUsername != null) {
      usersByUsername.remove(anOldUsername);
    }
    usersByUsername.put(aUsername, this);
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public String getUsername()
  {
    return username;
  }
  /* Code from template attribute_GetUnique */
  public static User getWithUsername(String aUsername)
  {
    return usersByUsername.get(aUsername);
  }
  /* Code from template attribute_HasUnique */
  public static boolean hasWithUsername(String aUsername)
  {
    return getWithUsername(aUsername) != null;
  }

  public String getPassword()
  {
    return password;
  }

  public void delete()
  {
    usersByUsername.remove(getUsername());
  }

  // line 19 "../../../../../CarshopPersistence.ump"
   public static  void reinitializeUsersByUsername(List<Customer> customers, Owner owner, List<Technician> techies){
    usersByUsername = new HashMap<String, User>();

		if (owner != null)
			usersByUsername.put(owner.getUsername(), owner);

		for (Customer customer : customers) {
			usersByUsername.put(customer.getUsername(), customer);
		}

		for (Technician techie : techies) {
			usersByUsername.put(techie.getUsername(), techie);
		}
  }


  public String toString()
  {
    return super.toString() + "["+
            "username" + ":" + getUsername()+ "," +
            "password" + ":" + getPassword()+ "]";
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 16 "../../../../../CarshopPersistence.ump"
  private static final long serialVersionUID = 4627175055218707245L ;

  
}