# Railway Booking System

## Overview
A complete railway ticket booking system built with Java Swing GUI and MySQL database integration. Features role-based access with user and admin modes, supporting both database-connected and standalone operation.

## Features
- **Dual Role System**: User mode (sample data) and Admin mode (database access)
- **User Authentication**: Login system with predefined credentials
- **Train Management**: View and manage train schedules
- **Ticket Booking**: Real-time booking with seat availability
- **Booking History**: Track and view booking records
- **Database Integration**: MySQL connectivity with automatic fallback
- **Responsive GUI**: Clean, modern Swing interface

## Technologies Used
- **Frontend**: Java Swing
- **Backend**: Java SE
- **Database**: MySQL 8.0
- **Connectivity**: JDBC
- **Build**: Standard Java compilation

## Prerequisites
- Java JDK 8 or higher
- MySQL Server 8.0+ (for admin features)
- Windows/Linux/Mac OS

## Quick Start

### 1. Database Setup (Optional - for Admin features)
```bash
# Start MySQL server and create database
mysql -u root -p < railway_booking_database.sql
```

### 2. Compile and Run
```bash
# Compile all Java files
javac *.java

# Run the application
java -cp ".;mysql-connector-java-legacy.jar" SimpleGUI
```

### 3. Login Credentials
**User Mode (Standalone)**:
- Role: User
- Username: `user123`
- Password: `password123`

**Admin Mode (Database)**:
- Role: Admin  
- Username: `admin`
- Password: `admin123`

## Project Structure
```
Project/
├── SimpleGUI.java              # Main application with GUI
├── DatabaseManager.java        # Database operations
├── SampleData.java            # Sample data for offline mode
├── TestDBConnection.java      # Database connection test
├── railway_booking_database.sql # Database schema
├── mysql-connector-java-legacy.jar # MySQL driver
└── README.md                  # This file
```

## Application Features

### User Mode
- View sample train schedules
- Book tickets with sample data
- View booking history
- No database required

### Admin Mode  
- Full database connectivity
- Manage real train data (34 trains)
- View actual booking records
- Database operations (insert, update, delete)

## Database Configuration

### Default Credentials
- **Host**: localhost:3306
- **Database**: railway_booking
- **Username**: railway_user
- **Password**: railway123

### Tables
- `trains` - Train schedules and information
- `bookings` - Booking records and passenger details
- `users` - User authentication (future enhancement)

## Troubleshooting

### Common Issues

1. **MySQL Connection Failed**
   - Ensure MySQL server is running
   - Verify credentials in DatabaseManager.java
   - Check if railway_user exists with correct privileges

2. **ClassPath Issues**
   - Ensure mysql-connector-java-legacy.jar is in project root
   - Use correct classpath syntax for your OS

3. **Compilation Errors**
   - Verify JDK version (Java 8+)
   - Check all .java files are in same directory

### Testing Database Connection
```bash
java -cp ".;mysql-connector-java-legacy.jar" TestDBConnection
```

## Development

### Adding New Features
1. Extend GUI components in SimpleGUI.java
2. Add database operations in DatabaseManager.java
3. Update sample data in SampleData.java if needed

### Code Structure
- **MVC Pattern**: GUI, Logic, and Data layers separated
- **Error Handling**: Graceful fallback to offline mode
- **Modular Design**: Easy to extend and maintain

## License
This project is for educational purposes. Feel free to modify and distribute.

## Support
For issues or questions:
1. Check the troubleshooting section
2. Verify database connectivity
3. Ensure all dependencies are present

## Version History
- **v1.0**: Initial release with basic booking system
- **v2.0**: Added role-based access and database integration
- **v3.0**: Improved GUI and error handling
- **v3.1**: Final cleanup with comprehensive documentation