/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.carshop.controller;

// line 28 "../../../../../CarShopTransferObjects.ump"
public class TOService
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOService Attributes
  private String name;
  private int duration;
  private String techtype;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOService(String aName, int aDuration, String aTechtype)
  {
    name = aName;
    duration = aDuration;
    techtype = aTechtype;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setDuration(int aDuration)
  {
    boolean wasSet = false;
    duration = aDuration;
    wasSet = true;
    return wasSet;
  }

  public boolean setTechtype(String aTechtype)
  {
    boolean wasSet = false;
    techtype = aTechtype;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public int getDuration()
  {
    return duration;
  }

  public String getTechtype()
  {
    return techtype;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "duration" + ":" + getDuration()+ "," +
            "techtype" + ":" + getTechtype()+ "]";
  }
}