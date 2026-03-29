import java.util.HashMap;
import java.util.Map;

/**
 * BookMyStay
 *
 * UC1: Welcome
 * UC2: Room Modeling
 * UC3: Inventory (HashMap)
 * UC4: Room Search (Read-Only)
 *
 * @author Hari
 * @version 4.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        uc1_welcomeMessage();

        // Initialize rooms
        Room single = new SingleRoom(1, 2000);
        Room doubleRoom = new DoubleRoom(2, 3500);
        Room suite = new SuiteRoom(3, 6000);

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(single.getRoomType(), 5);
        inventory.addRoom(doubleRoom.getRoomType(), 0); // unavailable
        inventory.addRoom(suite.getRoomType(), 2);

        // UC4: Search (READ ONLY)
        SearchService searchService = new SearchService();
        searchService.searchAvailableRooms(inventory, single, doubleRoom, suite);
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 4.0 ");
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

        public Map<String, Integer> getAllRooms() {
            return inventory;
        }
    }

    // ================= UC4 =================
    static class SearchService {

        // READ ONLY method
        public void searchAvailableRooms(RoomInventory inventory, Room... rooms) {

            System.out.println("\n--- Available Rooms ---");

            for (Room room : rooms) {

                int available = inventory.getAvailability(room.getRoomType());

                // Defensive check
                if (available > 0) {
                    room.displayDetails();
                    System.out.println("Available: " + available);
                    System.out.println("------------------------");
                }
            }
        }
    }
}