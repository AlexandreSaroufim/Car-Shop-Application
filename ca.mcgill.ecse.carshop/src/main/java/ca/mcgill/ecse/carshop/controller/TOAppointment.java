/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 38 "../../../../../CarShopTransferObjects.ump"
public class TOAppointment
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOAppointment Attributes
  private String serviceName;

  //TOAppointment Associations
  private TOUser customer;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOAppointment(String aServiceName, TOUser aCustomer)
  {
    serviceName = aServiceName;
    if (!setCustomer(aCustomer))
    {
      throw new RuntimeException("Unable to create TOAppointment due to aCustomer. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setServiceName(String aServiceName)
  {
    boolean wasSet = false;
    serviceName = aServiceName;
    wasSet = true;
    return wasSet;
  }

  public String getServiceName()
  {
    return serviceName;
  }
  /* Code from template association_GetOne */
  public TOUser getCustomer()
  {
    return customer;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setCustomer(TOUser aNewCustomer)
  {
    boolean wasSet = false;
    if (aNewCustomer != null)
    {
      customer = aNewCustomer;
      wasSet = true;
    }
    return wasSet;
  }

  public void delete()
  {
    customer = null;
  }


  public String toString()
  {
    return super.toString() + "["+
            "serviceName" + ":" + getServiceName()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null");
  }
}