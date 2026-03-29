import java.util.*;

/**
 * BookMyStay
 *
 * UC1: Welcome
 * UC2: Room Modeling
 * UC3: Inventory (HashMap)
 * UC4: Room Search (Read-Only)
 * UC5: Booking Requests using Queue (FIFO)
 *
 * @author Hari
 * @version 5.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        uc1_welcomeMessage();

        // Rooms
        Room single = new SingleRoom(1, 2000);
        Room doubleRoom = new DoubleRoom(2, 3500);
        Room suite = new SuiteRoom(3, 6000);

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(single.getRoomType(), 5);
        inventory.addRoom(doubleRoom.getRoomType(), 2);
        inventory.addRoom(suite.getRoomType(), 1);

        // UC4: Search (READ ONLY)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, single, doubleRoom, suite);

        // UC5: Booking Request Queue
        BookingQueue bookingQueue = new BookingQueue();

        System.out.println("\n--- Booking Requests ---");

        bookingQueue.addRequest(new Reservation("Hari", "Single Room"));
        bookingQueue.addRequest(new Reservation("John", "Suite Room"));
        bookingQueue.addRequest(new Reservation("Priya", "Double Room"));

        bookingQueue.displayQueue();
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 5.0 ");
        System.out.println("====================================");
    }

    // ================= UC2 =================
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

    // ================= UC3 =================
    static class RoomInventory {
        private Map<String, Integer> inventory;

        public RoomInventory() {
            inventory = new HashMap<>();
        }

        public void addRoom(String type, int count) {
            inventory.put(type, count);
        }

        public int getAvailability(String type) {
            return inventory.getOrDefault(type, 0);
        }

        public void updateAvailability(String type, int change) {
            inventory.put(type, getAvailability(type) + change);
        }
    }

    // ================= UC4 =================
    static class SearchService {

        public void searchAvailableRooms(RoomInventory inventory, Room... rooms) {

            System.out.println("\n--- Available Rooms ---");

            for (Room room : rooms) {
                int available = inventory.getAvailability(room.getRoomType());

                if (available > 0) {
                    room.displayDetails();
                    System.out.println("Available: " + available);
                    System.out.println("------------------------");
                }
            }
        }
    }

    // ================= UC5 =================

    // Reservation (Booking Request)
    static class Reservation {
        String customerName;
        String roomType;

        public Reservation(String customerName, String roomType) {
            this.customerName = customerName;
            this.roomType = roomType;
        }

        public void display() {
            System.out.println(customerName + " requested " + roomType);
        }
    }

    // Queue Manager
    static class BookingQueue {

        private Queue<Reservation> queue;

        public BookingQueue() {
            queue = new LinkedList<>();
        }

        // Add request (FIFO)
        public void addRequest(Reservation reservation) {
            queue.add(reservation);
        }

        // Display queue
        public void displayQueue() {
            System.out.println("\n--- Current Booking Queue (FIFO) ---");

            for (Reservation r : queue) {
                r.display();
            }
        }
    }
}