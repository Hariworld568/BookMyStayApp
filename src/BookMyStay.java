/**
 * BookMyStay
 *
 * This class represents the Hotel Booking Management System.
 * All use cases will be implemented inside this single class incrementally.
 *
 * UC1: Application Entry & Welcome Message
 *
 * @author Hari
 * @version 1.0
 */
public class BookMyStay {

    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {

        // Call Use Case 1
        uc1_welcomeMessage();

        // UC2
        uc2_roomInitialization();
    }

    /**
     * UC1: Displays welcome message
     */
    public static void uc1_welcomeMessage() {

        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 1.0 ");
        System.out.println("====================================");

        System.out.println("Application started successfully!");
    }
    public static void uc2_roomInitialization() {

        System.out.println("\n--- Available Room Types ---");

        // Create room objects
        Room single = new SingleRoom(1, 2000);
        Room doubleRoom = new DoubleRoom(2, 3500);
        Room suite = new SuiteRoom(3, 6000);

        // Static availability (simple variables)
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display details
        single.displayDetails(singleAvailable);
        doubleRoom.displayDetails(doubleAvailable);
        suite.displayDetails(suiteAvailable);
    }

    // ================================
    // ABSTRACT CLASS
    // ================================
    static abstract class Room {
        int beds;
        double price;

        public Room(int beds, double price) {
            this.beds = beds;
            this.price = price;
        }

        // Abstract method
        abstract String getRoomType();

        // Common method
        public void displayDetails(int availability) {
            System.out.println("Room Type: " + getRoomType());
            System.out.println("Beds: " + beds);
            System.out.println("Price: ₹" + price);
            System.out.println("Available: " + availability);
            System.out.println("-----------------------------");
        }
    }

    // ================================
    // CHILD CLASSES
    // ================================

    static class SingleRoom extends Room {

        public SingleRoom(int beds, double price) {
            super(beds, price);
        }

        @Override
        String getRoomType() {
            return "Single Room";
        }
    }

    static class DoubleRoom extends Room {

        public DoubleRoom(int beds, double price) {
            super(beds, price);
        }

        @Override
        String getRoomType() {
            return "Double Room";
        }
    }

    static class SuiteRoom extends Room {

        public SuiteRoom(int beds, double price) {
            super(beds, price);
        }

        @Override
        String getRoomType() {
            return "Suite Room";
        }
    }
}