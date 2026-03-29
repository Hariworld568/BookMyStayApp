import java.util.*;

/**
 * BookMyStay
 *
 * UC1 → UC6 Integrated System
 *
 * @author Hari
 * @version 6.0
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
        inventory.addRoom(single.getRoomType(), 2);
        inventory.addRoom(doubleRoom.getRoomType(), 1);
        inventory.addRoom(suite.getRoomType(), 1);

        // UC4: Search
        SearchService search = new SearchService();
        search.searchAvailableRooms(inventory, single, doubleRoom, suite);

        // UC5: Queue
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Hari", "Single Room"));
        queue.addRequest(new Reservation("John", "Single Room"));
        queue.addRequest(new Reservation("Priya", "Single Room")); // extra (will test limit)
        queue.addRequest(new Reservation("Alex", "Suite Room"));

        queue.displayQueue();

        // UC6: Booking Processing
        BookingService bookingService = new BookingService();
        bookingService.processBookings(queue, inventory);
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 6.0 ");
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
        String getRoomType() { return "Single Room"; }
    }

    static class DoubleRoom extends Room {
        public DoubleRoom(int beds, double price) {
            super(beds, price);
        }
        String getRoomType() { return "Double Room"; }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom(int beds, double price) {
            super(beds, price);
        }
        String getRoomType() { return "Suite Room"; }
    }

    // ================= UC3 =================
    static class RoomInventory {
        private Map<String, Integer> inventory = new HashMap<>();

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
    static class Reservation {
        String customerName;
        String roomType;

        public Reservation(String name, String type) {
            this.customerName = name;
            this.roomType = type;
        }
    }

    static class BookingQueue {
        private Queue<Reservation> queue = new LinkedList<>();

        public void addRequest(Reservation r) {
            queue.add(r);
        }

        public Reservation getNext() {
            return queue.poll(); // FIFO remove
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }

        public void displayQueue() {
            System.out.println("\n--- Booking Queue ---");
            for (Reservation r : queue) {
                System.out.println(r.customerName + " -> " + r.roomType);
            }
        }
    }

    // ================= UC6 =================
    static class BookingService {

        // Track ALL allocated room IDs
        private Set<String> allocatedRoomIds = new HashSet<>();

        // Map RoomType → Room IDs
        private Map<String, Set<String>> roomAllocations = new HashMap<>();

        public void processBookings(BookingQueue queue, RoomInventory inventory) {

            System.out.println("\n--- Processing Bookings ---");

            while (!queue.isEmpty()) {

                Reservation r = queue.getNext();

                int available = inventory.getAvailability(r.roomType);

                if (available > 0) {

                    // Generate unique room ID
                    String roomId = generateRoomId(r.roomType);

                    // Ensure uniqueness
                    if (!allocatedRoomIds.contains(roomId)) {

                        allocatedRoomIds.add(roomId);

                        // Map room type → IDs
                        roomAllocations
                                .computeIfAbsent(r.roomType, k -> new HashSet<>())
                                .add(roomId);

                        // Update inventory immediately
                        inventory.updateAvailability(r.roomType, -1);

                        System.out.println("Booking CONFIRMED for " + r.customerName +
                                " | Room: " + roomId);

                    } else {
                        System.out.println("Duplicate ID detected (should not happen)");
                    }

                } else {
                    System.out.println("Booking FAILED for " + r.customerName +
                            " | No rooms available");
                }
            }
        }

        // Unique ID generator
        private String generateRoomId(String roomType) {
            return roomType.substring(0, 2).toUpperCase() + "-" + UUID.randomUUID().toString().substring(0, 5);
        }
    }
}