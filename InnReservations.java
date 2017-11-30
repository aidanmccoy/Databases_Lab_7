import java.math.BigDecimal;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.Scanner;

public class InnReservations {
	static ArrayList<Room> roomList = new ArrayList<Room>();

	private static void PrintMenu() {
		System.out.println("+++ MAIN MENU +++\nPress the key numbers for following options");
		System.out.println("1 - Rooms and Rates");
		System.out.println("2 - Reservations");
		System.out.println("3 - Detailed Reservaion Information");
		System.out.println("4 - Revenue");
	}

	private static void GetRoomsAndRates(java.sql.Connection conn) {
		try {
			java.sql.Statement RoomsAndRates = conn.createStatement();
			int recStayLen = -1;
			Date recCheckOut = null, availCheckIn;
			String p1 = "SELECT Checkout, DATEDIFF(CheckOut, CheckIn) from lab7_reservations WHERE Room = '";
			String p2 = "' AND CheckOut = (SELECT MAX(CheckOut) FROM lab7_reservations where Room = '";
			String p3 = "' AND CheckOut < NOW())";
			

			for (Room room : roomList) {
				ResultSet rs = RoomsAndRates.executeQuery(p1 + room.getRoomCode() + p2 + room.getRoomCode() + p3);

				if (rs.next()) {
					recCheckOut = rs.getDate(1);
					recStayLen = rs.getInt(2);
				}
				
				System.out.println("============== ROOM NUMBER " + (roomList.indexOf(room) + 1) + " ==============");;
				System.out.println("RoomCode:     " + room.RoomCode);
				System.out.println("RoomName:     " + room.RoomName);
				System.out.println("Beds:         " + room.Beds);
				System.out.println("DedType:      " + room.bedType);
				System.out.println("MaxOcc:       " + room.maxOcc);
				System.out.println("BasePrice:    " + room.basePrice);
				System.out.println("Decor:        " + room.decor);
				System.out.println("Popularity:   " + room.popluarity);
				System.out.println("Avail Chk In: ");
				System.out.println("Rec Stay Len: " + recStayLen);
				System.out.println("Rec Chk Out:  " + recCheckOut.toString());
				System.out.println("-------------------------------------------");
			}
			System.out.println();

		} catch (SQLException e) {
			System.out.println("Error creating statement");
			e.printStackTrace();
		}
	}

	public static boolean PopulateRooms(java.sql.Connection conn) {
		String RoomCode, RoomName, bedType, decor;
		int Beds, maxOcc;
		BigDecimal basePrice;

		try {
			java.sql.Statement populateRooms = conn.createStatement();

			ResultSet rs = populateRooms.executeQuery("SELECT * FROM lab7_rooms");

			while (rs.next()) {
				RoomCode = rs.getString("RoomCode");
				RoomName = rs.getString("RoomName");
				Beds = rs.getInt("Beds");
				bedType = rs.getString("bedType");
				maxOcc = rs.getInt("maxOcc");
				basePrice = rs.getBigDecimal("basePrice");
				decor = rs.getString("decor");

				roomList.add(new Room(RoomCode, RoomName, Beds, bedType, maxOcc, basePrice, decor));
			}

			for (Room room : roomList) {
				rs = populateRooms
						.executeQuery("SELECT SUM(DATEDIFF(CheckOut, CheckIn)) FROM lab7_reservations WHERE Room = '"
								+ room.getRoomCode() + "' AND CheckIN > DATE_ADD(NOW(), INTERVAL -180 DAY)");
				rs.next();
				room.setPopluarity(rs.getFloat(1) / 180);
				// System.out.println(room.getPopluarity());
			}

			Collections.sort(roomList, new Comparator<Room>() {
				@Override
				public int compare(Room r1, Room r2) {
					if (r1.getPopluarity() > r2.getPopluarity())
						return -1;
					else
						return 1;
				}
			});

			// roomList.sort((o1,o2) -> o1.getPopluarity().compareTo(o2.getPopluarity()));

			/*
			 * for (Room room : roomList) { System.out.println("RoomCode: " +
			 * room.RoomCode); System.out.println("RoomName: " + room.RoomName);
			 * System.out.println("Beds: " + room.Beds); System.out.println("bedType: " +
			 * room.bedType); System.out.println("maxOcc: " + room.maxOcc);
			 * System.out.println("basePrice: " + room.basePrice);
			 * System.out.println("decor: " + room.decor); System.out.println("Popularity: "
			 * + room.popluarity);
			 * System.out.println("========================================="); }
			 */

			return true;
		} catch (Exception e) {
			System.out.println("Error Populating Rooms");
			e.printStackTrace();
			return false;
		}
	}

	public static void MakeReservation(java.sql.Connection conn, Scanner sc) {
		try {
			java.sql.Statement MakeReservation = conn.createStatement();
			String firstName, lastName, roomCode, bedType;
			Date checkIn, checkOut;
			int children, adults;
			
			System.out.println("Enter first name...");
			firstName = sc.next();
			System.out.println("Enter last name...");
			lastName = sc.next();
			System.out.println("Enter Room Code or Any for no preference...");
			roomCode = sc.next().toUpperCase();
			System.out.println("Enter desired bed type or Any for no preference...");
			bedType = sc.next();
			System.out.println("Enter desired check in date(mm/dd/yyyy)...");
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
			checkIn = (Date) df.parse(sc.next());
			System.out.println("Enter deired check out date(mm/dd/yyyy)...");
			checkOut = (Date) df.parse(sc.next());
			System.out.println("Enter number of children...");
			children = sc.nextInt();
			System.out.println("Enter number of adults...");	
			adults = sc.nextInt();
			
		} catch (Exception e) {
			System.out.println("Error Making Reservation...Returning to Main Menu...");
		}
	}
	
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int selection;
		java.sql.Connection conn = null;

		String jdbcUrl = System.getenv("URL");
		String dbUsername = System.getenv("USER");
		String dbPassword = System.getenv("PASS");

		System.out.print("HOST:" + jdbcUrl);
		System.out.print("--- USER:" + dbUsername);
		System.out.print("--- PASS:" + dbPassword);

		try {
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println(" --- Driver Found");
		} catch (ClassNotFoundException ex) {
			System.out.println(" --- Driver Not found...Exiting...");
			ex.printStackTrace();
			System.exit(-1);
		}

		try {
			conn = DriverManager.getConnection(jdbcUrl, dbUsername, dbPassword);
			// System.out.println(" --- Database Connected!");
		} catch (SQLException e1) {
			System.out.println(" --- Database connection failed...Exiting...");
			e1.printStackTrace();
			System.exit(-1);
		}

		if (!PopulateRooms(conn)) {
			System.out.println("Error setting up...Exiting...");
			System.exit(-1);
		}

		while (true) {
			try {
				PrintMenu();
				selection = sc.nextInt();
				switch (selection) {
				case 1:
					System.out.println("\n\n--- ROOMS AND RATES ---");
					GetRoomsAndRates(conn);
					break;
				case 2:
					System.out.println("\n\n--- RESERVATIONS ---");
					MakeReservation(conn, sc);
					break;
				case 3:
					System.out.println("\n\n--- DETAILED RESERVATION INFORMATION ---");

					break;
				case 4:
					System.out.println("\n\n--- REVENUE ---");

					break;
				default:
					System.out.println("INVALID SELECTION");
					break;

				}
			} catch (InputMismatchException e) {
				System.out.println("INVALID SELECTION");
				sc.next();
			}
		}
	}
}

