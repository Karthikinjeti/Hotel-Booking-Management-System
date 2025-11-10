import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

class Room {
    private int id;
    private String type; // e.g., "Single", "Double", "Deluxe"
    private double price;
    private boolean booked;
    private String guestName;

    public Room(int id, String type, double price) {
        this.id = id;
        this.type = type;
        this.price = price;
        this.booked = false;
        this.guestName = null;
    }

    public int getId() { return id; }
    public String getType() { return type; }
    public double getPrice() { return price; }
    public boolean isBooked() { return booked; }
    public String getGuestName() { return guestName; }

    public void book(String guestName) {
        this.booked = true;
        this.guestName = guestName;
    }

    public void cancel() {
        this.booked = false;
        this.guestName = null;
    }

    @Override
    public String toString() {
        return String.format("Room %d | %s | ₹%.2f | %s%s",
                id, type, price, (booked ? "BOOKED" : "AVAILABLE"),
                (booked ? " (Guest: " + guestName + ")" : ""));
    }
}

class Hotel {
    private List<Room> rooms;

    public Hotel() {
        rooms = new ArrayList<>();
    }

    public void addRoom(Room r) {
        rooms.add(r);
    }

    public List<Room> getAllRooms() {
        return rooms;
    }

    public List<Room> getAvailableRooms() {
        List<Room> avail = new ArrayList<>();
        for (Room r : rooms) if (!r.isBooked()) avail.add(r);
        return avail;
    }

    public Room findRoomById(int id) {
        for (Room r : rooms) if (r.getId() == id) return r;
        return null;
    }

    // Book by room id
    public boolean bookById(int id, String guestName) {
        Room r = findRoomById(id);
        if (r == null) return false;
        if (r.isBooked()) return false;
        r.book(guestName);
        return true;
    }

    // Book by type - first available
    public Room bookByType(String type, String guestName) {
        for (Room r : rooms) {
            if (!r.isBooked() && r.getType().equalsIgnoreCase(type)) {
                r.book(guestName);
                return r;
            }
        }
        return null;
    }

    // Cancel by id
    public boolean cancelById(int id) {
        Room r = findRoomById(id);
        if (r == null) return false;
        if (!r.isBooked()) return false;
        r.cancel();
        return true;
    }
}

public class HotelBookingSystem {
    private static Scanner scanner = new Scanner(System.in);
    private static Hotel hotel = new Hotel();

    public static void main(String[] args) {
        seedRooms(); // initial data
        System.out.println("=== Welcome to SyntecxHub Hotel Booking System ===");

        boolean exit = false;
        while (!exit) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    showAvailableRooms();
                    break;
                case 2:
                    bookRoomFlow();
                    break;
                case 3:
                    cancelBookingFlow();
                    break;
                case 4:
                    showAllRooms();
                    break;
                case 5:
                    System.out.println("Thank you for using the system. Goodbye!");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice. Enter a number between 1-5.");
            }
            System.out.println(); // blank line for readability
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("-------------------------------------------------");
        System.out.println("1. View available rooms");
        System.out.println("2. Book a room");
        System.out.println("3. Cancel booking");
        System.out.println("4. View all rooms");
        System.out.println("5. Exit");
        System.out.println("-------------------------------------------------");
    }

    private static void seedRooms() {
        // Example seed data: id, type, price
        hotel.addRoom(new Room(101, "Single", 999));
        hotel.addRoom(new Room(102, "Single", 999));
        hotel.addRoom(new Room(201, "Double", 1499));
        hotel.addRoom(new Room(202, "Double", 1499));
        hotel.addRoom(new Room(301, "Deluxe", 2499));
        hotel.addRoom(new Room(302, "Deluxe", 2799));
        // You can add more rooms or load from file later
    }

    private static void showAvailableRooms() {
        List<Room> avail = hotel.getAvailableRooms();
        if (avail.isEmpty()) {
            System.out.println("No rooms available right now.");
            return;
        }
        System.out.println("Available Rooms:");
        for (Room r : avail) System.out.println(r.toString());
    }

    private static void showAllRooms() {
        List<Room> all = hotel.getAllRooms();
        System.out.println("All Rooms:");
        for (Room r : all) System.out.println(r.toString());
    }

    private static void bookRoomFlow() {
        System.out.println("Booking Menu:");
        System.out.println("1. Book by Room ID");
        System.out.println("2. Book by Room Type (Single/Double/Deluxe)");
        int sub = readInt("Choose 1 or 2: ");
        scanner.nextLine(); // consume newline when needed

        if (sub == 1) {
            int id = readInt("Enter Room ID to book: ");
            scanner.nextLine();
            Room r = hotel.findRoomById(id);
            if (r == null) {
                System.out.println("Room ID not found.");
                return;
            }
            if (r.isBooked()) {
                System.out.println("Room is already booked.");
                return;
            }
            String guest = readNonEmptyString("Enter guest name: ");
            boolean ok = hotel.bookById(id, guest);
            if (ok) System.out.println("Booked successfully! " + r.toString());
            else System.out.println("Booking failed. Try again.");
        } else if (sub == 2) {
            String type = readNonEmptyString("Enter room type (Single/Double/Deluxe): ");
            String guest = readNonEmptyString("Enter guest name: ");
            Room booked = hotel.bookByType(type, guest);
            if (booked != null) {
                System.out.println("Booked successfully! " + booked.toString());
            } else {
                System.out.println("No available rooms of type: " + type);
            }
        } else {
            System.out.println("Invalid option.");
        }
    }

    private static void cancelBookingFlow() {
        int id = readInt("Enter Room ID to cancel booking: ");
        boolean ok = hotel.cancelById(id);
        if (ok) System.out.println("Booking cancelled successfully for room " + id);
        else {
            Room r = hotel.findRoomById(id);
            if (r == null) System.out.println("Room ID not found.");
            else System.out.println("Room is not currently booked.");
        }
    }

    // Helper read with validation
    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    System.out.println("Input cannot be empty. Please enter a number.");
                    continue;
                }
                int val = Integer.parseInt(line);
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again.");
            }
        }
    }

    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (s.isEmpty()) System.out.println("Input cannot be empty.");
            else return s;
        }
    }
}
