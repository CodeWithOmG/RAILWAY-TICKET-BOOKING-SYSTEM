import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

/**
 * Handles all database operations for Railway Booking System
 */
public class DatabaseManager {
    private static Connection connection;
    private static boolean connected = false;

    // Initialize database connection
    public static boolean connect() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/railway_booking?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            
            // Try the dedicated railway user first, then fallback to root with common passwords
            String[][] credentials = {
                {"railway_user", "railway123"},
                {"root", "root"},
                {"root", "password"},
                {"root", "admin"},
                {"root", "mysql"},
                {"root", ""}
            };
            
            for (String[] cred : credentials) {
                try {
                    connection = DriverManager.getConnection(url, cred[0], cred[1]);
                    connected = true;
                    System.out.println("✅ Database connected as user: " + cred[0]);
                    return true;
                } catch (SQLException e) {
                    // Try next credential combination
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Database connection failed: " + e.getMessage());
        }
        return false;
    }

    public static boolean isConnected() {
        return connected;
    }

    // Get all trains
    public static List<Object[]> getTrains() {
        List<Object[]> trains = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM trains WHERE status = 'active' ORDER BY train_number");
            while (rs.next()) {
                trains.add(new Object[]{
                    rs.getString("train_number"),
                    rs.getString("train_name"),
                    rs.getString("source_station"),
                    rs.getString("destination_station"),
                    rs.getTime("departure_time").toString().substring(0, 5),
                    rs.getTime("arrival_time").toString().substring(0, 5),
                    "₹" + rs.getBigDecimal("price"),
                    rs.getInt("available_seats"),
                    "Book"
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading trains: " + e.getMessage());
        }
        return trains;
    }

    // Search trains by route
    public static List<Object[]> searchTrains(String from, String to) {
        List<Object[]> trains = new ArrayList<>();
        try {
            PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM trains WHERE source_station = ? AND destination_station = ? AND status = 'active'"
            );
            ps.setString(1, from);
            ps.setString(2, to);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                trains.add(new Object[]{
                    rs.getString("train_number"),
                    rs.getString("train_name"),
                    rs.getString("source_station"),
                    rs.getString("destination_station"),
                    rs.getTime("departure_time").toString().substring(0, 5),
                    rs.getTime("arrival_time").toString().substring(0, 5),
                    "₹" + rs.getBigDecimal("price"),
                    rs.getInt("available_seats"),
                    "Book"
                });
            }
        } catch (SQLException e) {
            System.out.println("Error searching trains: " + e.getMessage());
        }
        return trains;
    }

    // Get all bookings
    public static List<Object[]> getBookings() {
        List<Object[]> bookings = new ArrayList<>();
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT b.pnr, CONCAT(p.first_name, ' ', p.last_name) as name, " +
                "t.train_name, b.journey_date, b.status " +
                "FROM bookings b " +
                "JOIN passengers p ON b.passenger_id = p.passenger_id " +
                "JOIN trains t ON b.train_id = t.train_id " +
                "ORDER BY b.created_at DESC"
            );
            while (rs.next()) {
                bookings.add(new Object[]{
                    rs.getString("pnr"),
                    rs.getString("name"),
                    rs.getString("train_name"),
                    rs.getDate("journey_date"),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error loading bookings: " + e.getMessage());
        }
        return bookings;
    }

    // Book a ticket
    public static boolean bookTicket(String pnr, String passengerName, int age, String trainNumber) {
        try {
            connection.setAutoCommit(false);

            // Split name
            String[] nameParts = passengerName.split(" ", 2);
            String firstName = nameParts[0];
            String lastName = nameParts.length > 1 ? nameParts[1] : "";

            // Insert passenger
            PreparedStatement psPassenger = connection.prepareStatement(
                "INSERT INTO passengers (first_name, last_name, age, gender) VALUES (?,?,?,'Male')",
                Statement.RETURN_GENERATED_KEYS
            );
            psPassenger.setString(1, firstName);
            psPassenger.setString(2, lastName);
            psPassenger.setInt(3, age);
            psPassenger.executeUpdate();

            ResultSet rs = psPassenger.getGeneratedKeys();
            rs.next();
            int passengerId = rs.getInt(1);

            // Get train details
            PreparedStatement psTrain = connection.prepareStatement("SELECT * FROM trains WHERE train_number = ?");
            psTrain.setString(1, trainNumber);
            ResultSet trainRs = psTrain.executeQuery();

            if (trainRs.next()) {
                int trainId = trainRs.getInt("train_id");
                BigDecimal fare = trainRs.getBigDecimal("price");

                // Insert booking
                PreparedStatement psBooking = connection.prepareStatement(
                    "INSERT INTO bookings (pnr, train_id, passenger_id, booking_date, journey_date, fare, status) " +
                    "VALUES (?,?,?,CURDATE(),CURDATE(),?,'confirmed')"
                );
                psBooking.setString(1, pnr);
                psBooking.setInt(2, trainId);
                psBooking.setInt(3, passengerId);
                psBooking.setBigDecimal(4, fare);
                psBooking.executeUpdate();

                // Update available seats
                PreparedStatement psUpdate = connection.prepareStatement(
                    "UPDATE trains SET available_seats = available_seats - 1 WHERE train_id = ?"
                );
                psUpdate.setInt(1, trainId);
                psUpdate.executeUpdate();

                connection.commit();
                connection.setAutoCommit(true);
                return true;
            }
        } catch (SQLException e) {
            try {
                connection.rollback();
                connection.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.out.println("Error booking ticket: " + e.getMessage());
        }
        return false;
    }

    public static void close() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}