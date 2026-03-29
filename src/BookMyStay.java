import java.util.*;

/**
 * BookMyStay
 *
 * UC1 → UC11 Integrated System
 *
 * @author Hari
 * @version 11.0
 */
public class BookMyStay {

    public static void main(String[] args) {

        uc1_welcomeMessage();

        RoomInventory inventory = new RoomInventory();
        inventory.addRoom("Single Room", 2);
        inventory.addRoom("Suite Room", 1);

        BookingQueue queue = new BookingQueue();

        // Multiple users (simulated)
        queue.addRequest(new Reservation("Hari", "Single Room"));
        queue.addRequest(new Reservation("John", "Single Room"));
        queue.addRequest(new Reservation("Alex", "Suite Room"));
        queue.addRequest(new Reservation("Sam", "Suite Room")); // may fail

        BookingService bookingService = new BookingService();
        BookingHistory history = new BookingHistory();

        // 🔥 MULTI-THREADING
        Thread t1 = new Thread(() ->
                bookingService.processBookingsConcurrent(queue, inventory, history));

        Thread t2 = new Thread(() ->
                bookingService.processBookingsConcurrent(queue, inventory, history));

        Thread t3 = new Thread(() ->
                bookingService.processBookingsConcurrent(queue, inventory, history));

        t1.start();
        t2.start();
        t3.start();

        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }

        // Final Report
        BookingReportService reportService = new BookingReportService();
        reportService.generateReport(history);
    }

    // ================= UC1 =================
    public static void uc1_welcomeMessage() {
        System.out.println("====================================");
        System.out.println(" Welcome to Book My Stay Application ");
        System.out.println(" Version: 11.0 ");
        System.out.println("====================================");
    }

    // ================= INVENTORY =================
    static class RoomInventory {
        private Map<String, Integer> inventory = new HashMap<>();

        public synchronized void addRoom(String type, int count) {
            inventory.put(type, count);
        }

        public synchronized int getAvailability(String type) {
            return inventory.getOrDefault(type, 0);
        }

        public synchronized void updateAvailability(String type, int change)
                throws InvalidInventoryException {

            int newValue = getAvailability(type) + change;

            if (newValue < 0) {
                throw new InvalidInventoryException(
                        "Inventory cannot be negative for: " + type);
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

        public synchronized void addRequest(Reservation r) {
            queue.add(r);
        }

        public synchronized Reservation getNext() {
            return queue.poll();
        }

        public synchronized boolean isEmpty() {
            return queue.isEmpty();
        }
    }

    // ================= RECORD =================
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

    // ================= BOOKING SERVICE =================
    static class BookingService {

        private Set<String> allocatedRoomIds =
                Collections.synchronizedSet(new HashSet<>());

        public void processBookingsConcurrent(
                BookingQueue queue,
                RoomInventory inventory,
                BookingHistory history) {

            while (true) {

                Reservation r;

                // 🔒 CRITICAL SECTION (Queue access)
                synchronized (queue) {
                    if (queue.isEmpty()) break;
                    r = queue.getNext();
                }

                try {
                    processSingleBooking(r, inventory, history);
                } catch (BookingException e) {
                    System.out.println("ERROR: " + e.getMessage());
                }
            }
        }

        private void processSingleBooking(
                Reservation r,
                RoomInventory inventory,
                BookingHistory history)
                throws BookingException {

            // 🔒 CRITICAL SECTION (Allocation + Inventory)
            synchronized (inventory) {

                int available = inventory.getAvailability(r.roomType);

                if (available > 0) {

                    String roomId = generateRoomId(r.roomType);

                    if (!allocatedRoomIds.contains(roomId)) {

                        allocatedRoomIds.add(roomId);
                        inventory.updateAvailability(r.roomType, -1);

                        ReservationRecord record =
                                new ReservationRecord(roomId,
                                        r.customerName,
                                        r.roomType);

                        history.addRecord(record);

                        System.out.println(
                                Thread.currentThread().getName()
                                        + " CONFIRMED: "
                                        + r.customerName + " → " + roomId);
                    }
                } else {
                    System.out.println(
                            Thread.currentThread().getName()
                                    + " FAILED: " + r.customerName);
                }
            }
        }

        private String generateRoomId(String type) {
            return type.substring(0, 2).toUpperCase() + "-" +
                    UUID.randomUUID().toString().substring(0, 5);
        }
    }

    // ================= HISTORY =================
    static class BookingHistory {
        private List<ReservationRecord> history =
                Collections.synchronizedList(new ArrayList<>());

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

            System.out.println("\n--- Final Booking Report ---");

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

    // ================= EXCEPTIONS =================
    static class BookingException extends Exception {
        public BookingException(String msg) { super(msg); }
    }

    static class InvalidInventoryException extends BookingException {
        public InvalidInventoryException(String msg) { super(msg); }
    }
}