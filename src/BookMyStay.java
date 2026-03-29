import java.util.*;

/**
 * BookMyStay
 *
 * UC1 → UC8 Integrated System
 *
 * @author Hari
 * @version 8.0
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

        // Booking + History
        BookingService bookingService = new BookingService();
        BookingHistory history = new BookingHistory();

        List<ReservationRecord> confirmedRecords =
                bookingService.processBookings(queue, inventory);

        // Store into history (UC8)
        for (ReservationRecord r : confirmedRecords) {
            history.addRecord(r);
        }

        // UC7: Add-On Services
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        AddOnService wifi = new AddOnService("WiFi", 200);
        AddOnService breakfast = new AddOnService("Breakfast", 300);

        if (!confirmedRecords.isEmpty()) {
            String resId = confirmedRecords.get(0).reservationId;
            serviceManager.addService(resId, wifi);
            serviceManager.addService(resId, breakfast);
        }

        serviceManager.displayServices();

        // UC8: Reporting
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(history);
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 8.0 ");
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

    // New record class (important for history)
    static class ReservationRecord {
        String reservationId;
        String customerName;
        String roomType;

        public ReservationRecord(String id, String name, String type) {
            this.reservationId = id;
            this.customerName = name;
            this.roomType = type;
        }
    }

    static class BookingService {

        private Set<String> allocatedRoomIds = new HashSet<>();

        public List<ReservationRecord> processBookings(
                BookingQueue queue, RoomInventory inventory) {

            List<ReservationRecord> confirmed = new ArrayList<>();

            System.out.println("\n--- Processing Bookings ---");

            while (!queue.isEmpty()) {

                Reservation r = queue.getNext();
                int available = inventory.getAvailability(r.roomType);

                if (available > 0) {

                    String roomId = generateRoomId(r.roomType);

                    if (!allocatedRoomIds.contains(roomId)) {

                        allocatedRoomIds.add(roomId);
                        inventory.updateAvailability(r.roomType, -1);

                        ReservationRecord record =
                                new ReservationRecord(roomId, r.customerName, r.roomType);

                        confirmed.add(record);

                        System.out.println("CONFIRMED: " +
                                r.customerName + " → " + roomId);
                    }
                } else {
                    System.out.println("FAILED: " + r.customerName);
                }
            }
            return confirmed;
        }

        private String generateRoomId(String type) {
            return type.substring(0, 2).toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 5);
        }
    }

    // ================= UC7 =================

    static class AddOnService {
        String name;
        double cost;

        public AddOnService(String name, double cost) {
            this.name = name;
            this.cost = cost;
        }
    }

    static class AddOnServiceManager {
        private Map<String, List<AddOnService>> serviceMap = new HashMap<>();

        public void addService(String reservationId, AddOnService service) {
            serviceMap
                    .computeIfAbsent(reservationId, k -> new ArrayList<>())
                    .add(service);
        }

        public void displayServices() {

            System.out.println("\n--- Add-On Services ---");

            for (String id : serviceMap.keySet()) {

                double total = 0;
                System.out.println("Reservation: " + id);

                for (AddOnService s : serviceMap.get(id)) {
                    System.out.println(" - " + s.name + " ₹" + s.cost);
                    total += s.cost;
                }

                System.out.println("Total Add-On Cost: ₹" + total);
                System.out.println("---------------------");
            }
        }
    }

    // ================= UC8 =================

    // History storage
    static class BookingHistory {

        private List<ReservationRecord> history = new ArrayList<>();

        public void addRecord(ReservationRecord r) {
            history.add(r);
        }

        public List<ReservationRecord> getAll() {
            return history;
        }
    }

    // Reporting service
    static class BookingReportService {

        public void generateReport(BookingHistory history) {

            System.out.println("\n--- Booking Report ---");

            List<ReservationRecord> records = history.getAll();

            for (ReservationRecord r : records) {
                System.out.println(
                        r.reservationId + " | " +
                                r.customerName + " | " +
                                r.roomType
                );
            }

            System.out.println("\nTotal Bookings: " + records.size());
        }
    }
}