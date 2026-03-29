import java.util.*;

/**
 * BookMyStay
 *
 * UC1 → UC7 Integrated System
 *
 * @author Hari
 * @version 7.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        uc1_welcomeMessage();

        // Rooms
        Room single = new SingleRoom(1, 2000);
        Room suite = new SuiteRoom(3, 6000);

        // Inventory
        RoomInventory inventory = new RoomInventory();
        inventory.addRoom(single.getRoomType(), 2);
        inventory.addRoom(suite.getRoomType(), 1);

        // Queue
        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Hari", "Single Room"));
        queue.addRequest(new Reservation("John", "Suite Room"));

        // Booking
        BookingService bookingService = new BookingService();
        List<String> confirmedIds = bookingService.processBookings(queue, inventory);

        // UC7: Add-On Services
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Create services
        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService breakfast = new AddOnService("Breakfast", 300);
        AddOnService spa = new AddOnService("Spa", 1000);

        System.out.println("\n--- Adding Services ---");

        // Attach services to reservations
        if (!confirmedIds.isEmpty()) {
            String res1 = confirmedIds.get(0);
            serviceManager.addService(res1, wifi);
            serviceManager.addService(res1, breakfast);

            String res2 = confirmedIds.size() > 1 ? confirmedIds.get(1) : null;
            if (res2 != null) {
                serviceManager.addService(res2, spa);
            }
        }

        // Display services + cost
        serviceManager.displayServices();
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 7.0 ");
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
    }

    static class SingleRoom extends Room {
        public SingleRoom(int beds, double price) { super(beds, price); }
        String getRoomType() { return "Single Room"; }
    }

    static class SuiteRoom extends Room {
        public SuiteRoom(int beds, double price) { super(beds, price); }
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
            return queue.poll();
        }

        public boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    // ================= UC6 =================
    static class BookingService {

        private Set<String> allocatedRoomIds = new HashSet<>();

        public List<String> processBookings(BookingQueue queue, RoomInventory inventory) {

            List<String> confirmedIds = new ArrayList<>();

            System.out.println("\n--- Processing Bookings ---");

            while (!queue.isEmpty()) {

                Reservation r = queue.getNext();
                int available = inventory.getAvailability(r.roomType);

                if (available > 0) {

                    String roomId = generateRoomId(r.roomType);

                    if (!allocatedRoomIds.contains(roomId)) {

                        allocatedRoomIds.add(roomId);
                        inventory.updateAvailability(r.roomType, -1);

                        confirmedIds.add(roomId);

                        System.out.println("CONFIRMED: " + r.customerName +
                                " → " + roomId);
                    }

                } else {
                    System.out.println("FAILED: " + r.customerName);
                }
            }
            return confirmedIds;
        }

        private String generateRoomId(String type) {
            return type.substring(0, 2).toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 5);
        }
    }

    // ================= UC7 =================

    // Service class
    static class AddOnService {
        String name;
        double cost;

        public AddOnService(String name, double cost) {
            this.name = name;
            this.cost = cost;
        }
    }

    // Manager class
    static class AddOnServiceManager {

        // Map<ReservationID, List of Services>
        private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

        // Add service to reservation
        public void addService(String reservationId, AddOnService service) {

            serviceMap
                    .computeIfAbsent(reservationId, k -> new ArrayList<>())
                    .add(service);
        }

        // Display services + cost
        public void displayServices() {

            System.out.println("\n--- Add-On Services ---");

            for (String resId : serviceMap.keySet()) {

                System.out.println("Reservation: " + resId);

                double total = 0;

                for (AddOnService s : serviceMap.get(resId)) {
                    System.out.println(" - " + s.name + " ₹" + s.cost);
                    total += s.cost;
                }

                System.out.println("Total Add-On Cost: ₹" + total);
                System.out.println("---------------------------");
            }
        }
    }
}