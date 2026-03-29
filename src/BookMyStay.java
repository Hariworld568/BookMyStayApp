import java.util.HashMap;
import java.util.Map;

/**
 * BookMyStay
 *
 * Hotel Booking Management System
 *
 * UC1: Application Entry & Welcome Message
 * UC2: Room Modeling using Inheritance
 * UC3: Centralized Room Inventory using HashMap
 *
 * @author Hari
 * @version 3.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        // UC1
        uc1_welcomeMessage();

        // UC2 + UC3
        uc2_and_uc3_inventorySystem();
    }

    /**
     * UC1: Displays welcome message
     */
    public static void uc1_welcomeMessage() {

        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 3.0 ");
        System.out.println("====================================");
    }

    /**
     * UC2 + UC3: Room + Centralized Inventory
     */
    public static void uc2_and_uc3_inventorySystem() {

        System.out.println("\n--- Room Inventory ---");

        // Create Room Objects (Domain)
        Room single = new SingleRoom(1, 2000);
        Room doubleRoom = new DoubleRoom(2, 3500);
        Room suite = new SuiteRoom(3, 6000);

        // UC3: Centralized Inventory
        RoomInventory inventory = new RoomInventory();

        // Register rooms with availability
        inventory.addRoom(single.getRoomType(), 5);
        inventory.addRoom(doubleRoom.getRoomType(), 3);
        inventory.addRoom(suite.getRoomType(), 2);

        // Display Inventory
        inventory.displayInventory();

        // Example Update (simulate booking)
        System.out.println("\n--- After Booking 1 Single Room ---");
        inventory.updateAvailability("Single Room", -1);

        inventory.displayInventory();
    }

    // ================================
    // ABSTRACT ROOM CLASS (UC2)
    // ================================
    static abstract class Room {
        int beds;
        double price;

        public Room(int beds, double price) {
            this.beds = beds;
            this.price = price;
        }

        abstract String getRoomType();

        public void displayDetails() {
            System.out.println("Room Type: " + getRoomType());
            System.out.println("Beds: " + beds);
            System.out.println("Price: ₹" + price);
        }
    }

    // ================================
    // ROOM TYPES
    // ================================
    static class SingleRoom extends Room {
        public SingleRoom(int beds, double price) {
            super(beds, price);
        }

        String getRoomType() {
            return "Single Room";
        }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom(int beds, double price) {
            super(beds, price);
        }

        String getRoomType() {
            return "Double Room";
        }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom(int beds, double price) {
            super(beds, price);
        }

        String getRoomType() {
            return "Suite Room";
        }
    }

    // ================================
    // UC3: INVENTORY CLASS
    // ================================
    static class RoomInventory {

        private Map<String, Integer> inventory;

        // Constructor
        public RoomInventory() {
            inventory = new HashMap<>();
        }

        // Add room type
        public void addRoom(String roomType, int count) {
            inventory.put(roomType, count);
        }

        // Get availability
        public int getAvailability(String roomType) {
            return inventory.getOrDefault(roomType, 0);
        }

        // Update availability
        public void updateAvailability(String roomType, int change) {
            int current = getAvailability(roomType);
            inventory.put(roomType, current + change);
        }

        // Display inventory
        public void displayInventory() {
            for (String type : inventory.keySet()) {
                System.out.println(type + " Available: " + inventory.get(type));
            }
        }
    }
}