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
}