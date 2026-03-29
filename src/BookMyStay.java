import java.io.*;
import java.util.*;

/**
 * BookMyStay
 *
 * UC1 → UC12 Integrated System
 *
 * @author Hari
 * @version 12.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        uc1_welcomeMessage();

        PersistenceService persistence = new PersistenceService();

        // 🔥 LOAD (Recovery)
        SystemState state = persistence.loadState();

        RoomInventory inventory = state.inventory;
        BookingHistory history = state.history;

        // If first run (no file)
        if (inventory == null) {
            inventory = new RoomInventory();
            inventory.addRoom("Single Room", 2);
            inventory.addRoom("Suite Room", 1);
        }

        if (history == null) {
            history = new BookingHistory();
        }

        BookingQueue queue = new BookingQueue();
        queue.addRequest(new Reservation("Hari", "Single Room"));
        queue.addRequest(new Reservation("John", "Suite Room"));

        BookingService bookingService = new BookingService();

        try {
            List<ReservationRecord> records =
                    bookingService.processBookings(queue, inventory);

            for (ReservationRecord r : records) {
                history.addRecord(r);
            }

        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        // 🔥 SAVE (Before exit)
        persistence.saveState(new SystemState(inventory, history));

        // Report
        new BookingReportService().generateReport(history);
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 12.0 ");
        System.out.println("====================================");
    }

    // ================= SYSTEM STATE =================
    static class SystemState implements Serializable {
        RoomInventory inventory;
        BookingHistory history;

        public SystemState(RoomInventory i, BookingHistory h) {
            this.inventory = i;
            this.history = h;
        }
    }

    // ================= INVENTORY =================
    static class RoomInventory implements Serializable {
        private Map<String, Integer> inventory = new HashMap<>();

        public void addRoom(String type, int count) {
            inventory.put(type, count);
        }

        public int getAvailability(String type) {
            return inventory.getOrDefault(type, 0);
        }

        public void updateAvailability(String type, int change)
                throws Exception {

            int newValue = getAvailability(type) + change;

            if (newValue < 0) {
                throw new Exception("Inventory cannot be negative");
            }

            inventory.put(type, newValue);
        }
    }

    // ================= QUEUE =================
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

    // ================= RECORD =================
    static class ReservationRecord implements Serializable {
        String reservationId;
        String customerName;
        String roomType;

        public ReservationRecord(String id, String name, String type) {
            this.reservationId = id;
            this.customerName = name;
            this.roomType = type;
        }
    }

    // ================= BOOKING =================
    static class BookingService {

        public List<ReservationRecord> processBookings(
                BookingQueue queue,
                RoomInventory inventory) throws Exception {

            List<ReservationRecord> confirmed = new ArrayList<>();

            System.out.println("\n--- Processing Bookings ---");

            while (!queue.isEmpty()) {

                Reservation r = queue.getNext();
                int available = inventory.getAvailability(r.roomType);

                if (available > 0) {

                    String roomId = generateRoomId(r.roomType);

                    inventory.updateAvailability(r.roomType, -1);

                    ReservationRecord record =
                            new ReservationRecord(roomId,
                                    r.customerName,
                                    r.roomType);

                    confirmed.add(record);

                    System.out.println("CONFIRMED: "
                            + r.customerName + " → " + roomId);

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

    // ================= HISTORY =================
    static class BookingHistory implements Serializable {
        private List<ReservationRecord> history = new ArrayList<>();

        public void addRecord(ReservationRecord r) {
            history.add(r);
        }

        public List<ReservationRecord> getAll() {
            return history;
        }
    }

    // ================= REPORT =================
    static class BookingReportService {
        public void generateReport(BookingHistory history) {

            System.out.println("\n--- Booking Report ---");

            for (ReservationRecord r : history.getAll()) {
                System.out.println(
                        r.reservationId + " | " +
                                r.customerName + " | " +
                                r.roomType
                );
            }

            System.out.println("Total Bookings: " +
                    history.getAll().size());
        }
    }

    // ================= UC12 =================
    static class PersistenceService {

        private final String FILE_NAME = "bookmyStay.dat";

        // SAVE
        public void saveState(SystemState state) {
            try (ObjectOutputStream out =
                         new ObjectOutputStream(
                                 new FileOutputStream(FILE_NAME))) {

                out.writeObject(state);
                System.out.println("\n💾 Data Saved Successfully");

            } catch (Exception e) {
                System.out.println("Save Error: " + e.getMessage());
            }
        }

        // LOAD
        public SystemState loadState() {

            try (ObjectInputStream in =
                         new ObjectInputStream(
                                 new FileInputStream(FILE_NAME))) {

                System.out.println("🔄 Loading Previous State...");
                return (SystemState) in.readObject();

            } catch (Exception e) {
                System.out.println("⚠️ No previous data found. Starting fresh.");
                return new SystemState(null, null);
            }
        }
    }
}