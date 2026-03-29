import java.util.*;

/**
 * BookMyStay
 *
 * UC1 → UC9 Integrated System
 *
 * @author Hari
 * @version 9.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        uc1_welcomeMessage();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Suite Room", 1);

        BookingQueue queue = new BookingQueue();

        // VALID
        queue.addRequest(new Reservation("Hari", "Single Room"));

        // INVALID (wrong room type)
        queue.addRequest(new Reservation("John", "Deluxe Room"));

        BookingService bookingService = new BookingService();
        BookingHistory history = new BookingHistory();

        try {
            List<ReservationRecord> records =
                    bookingService.processBookings(queue, inventory);

            for (ReservationRecord r : records) {
                history.addRecord(r);
            }

        } catch (BookingException e) {
            System.out.println("ERROR: " + e.getMessage());
        }

        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(history);
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 9.0 ");
        System.out.println("====================================");
    }

    // ================= UC3 =================
    static class RoomInventory {
        private Map<String, Integer> inventory = new HashMap<>();

        public void addRoom(String type, int count) {
            inventory.put(type, count);
        }

        public boolean isValidRoomType(String type) {
            return inventory.containsKey(type);
        }

        public int getAvailability(String type) {
            return inventory.getOrDefault(type, 0);
        }

        public void updateAvailability(String type, int change)
                throws InvalidInventoryException {

            int newValue = getAvailability(type) + change;

            if (newValue < 0) {
                throw new InvalidInventoryException(
                        "Inventory cannot be negative for: " + type);
            }

            inventory.put(type, newValue);
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
        private InvalidBookingValidator validator = new InvalidBookingValidator();

        public List<ReservationRecord> processBookings(
                BookingQueue queue, RoomInventory inventory)
                throws BookingException {

            List<ReservationRecord> confirmed = new ArrayList<>();

            System.out.println("\n--- Processing Bookings ---");

            while (!queue.isEmpty()) {

                Reservation r = queue.getNext();

                // 🔥 VALIDATION (UC9)
                validator.validate(r, inventory);

                int available = inventory.getAvailability(r.roomType);

                if (available > 0) {

                    String roomId = generateRoomId(r.roomType);

                    allocatedRoomIds.add(roomId);
                    inventory.updateAvailability(r.roomType, -1);

                    confirmed.add(new ReservationRecord(
                            roomId, r.customerName, r.roomType));

                    System.out.println("CONFIRMED: " +
                            r.customerName + " → " + roomId);

                } else {
                    System.out.println("FAILED: No rooms for " + r.customerName);
                }
            }
            return confirmed;
        }

        private String generateRoomId(String type) {
            return type.substring(0, 2).toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 5);
        }
    }

    // ================= UC8 =================
    static class BookingHistory {
        private List<ReservationRecord> history = new ArrayList<>();

        public void addRecord(ReservationRecord r) {
            history.add(r);
        }

        public List<ReservationRecord> getAll() {
            return history;
        }
    }

    static class BookingReportService {
        public void generateReport(BookingHistory history) {

            System.out.println("\n--- Booking Report ---");

            for (ReservationRecord r : history.getAll()) {
                System.out.println(
                        r.reservationId + " | " +
                                r.customerName + " | " +
                                r.roomType);
            }

            System.out.println("Total Bookings: " + history.getAll().size());
        }
    }

    // ================= UC9 =================

    // 🔴 Base Exception
    static class BookingException extends Exception {
        public BookingException(String message) {
            super(message);
        }
    }

    // 🔴 Invalid Room
    static class InvalidRoomTypeException extends BookingException {
        public InvalidRoomTypeException(String msg) {
            super(msg);
        }
    }

    // 🔴 Inventory Error
    static class InvalidInventoryException extends BookingException {
        public InvalidInventoryException(String msg) {
            super(msg);
        }
    }

    // 🔴 Validator
    static class InvalidBookingValidator {

        public void validate(Reservation r, RoomInventory inventory)
                throws BookingException {

            if (r.customerName == null || r.customerName.isEmpty()) {
                throw new BookingException("Customer name cannot be empty");
            }

            if (!inventory.isValidRoomType(r.roomType)) {
                throw new InvalidRoomTypeException(
                        "Invalid Room Type: " + r.roomType);
            }
        }
    }
}