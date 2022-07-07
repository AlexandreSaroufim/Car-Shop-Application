package ca.mcgill.ecse.carshop.persistence;

import ca.mcgill.ecse.carshop.model.*;

public class CarshopPersistence {

	private static String filename = "data.carshop";

	public static void setFilename(String filename) {
		CarshopPersistence.filename = filename;
	}

	public static void save(CarShop carShop) {
		PersistenceObjectStream.setFilename(filename);
		PersistenceObjectStream.serialize(carShop);
	}

	public static CarShop load() {
		PersistenceObjectStream.setFilename(filename);
		CarShop carShop = (CarShop) PersistenceObjectStream.deserialize();
		// model cannot be loaded - create empty BTMS
		if (carShop == null) {
			carShop = new CarShop();
			System.out.println("-----CARSHOP was NULL, instantiated a new carshop-----");
		} else {
			carShop.reinitialize(); // edit carshop model
		}
		return carShop;
	}
}
