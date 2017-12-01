import java.math.BigDecimal;
import java.sql.Connection;
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
	static ArrayList<Reservation> reservationList = new ArrayList<Reservation>();

	private static void PrintMenu() {
		System.out.println("+++ MAIN MENU +++\nPress the key numbers for following options");
		System.out.println("1 - Rooms and Rates");
		System.out.println("2 - Reservations");
		System.out.println("3 - Detailed Reservaion Information");
		System.out.println("4 - Revenue");
	}

	private static String SanitizeString(String input) {
		String arr[] = input.split(" ");
		String tempString = arr[0];
		String output = tempString.replaceAll("[^a-zA-Z]", "");
		return output;
	}

	private static void PrintReservationList() {
		for (Reservation reservation : reservationList) {
			System.out.println("============ " + reservationList.indexOf(reservation) + "=============");
			System.out.println("CODE:      " + reservation.CODE);
			System.out.println("Room:      " + reservation.Room);
			System.out.println("CheckIn:   " + reservation.CheckIn.toString());
			System.out.println("CheckOut:  " + reservation.CheckOut.toString());
			System.out.println("Rate:      " + reservation.Rate);
			System.out.println("LastName:  " + reservation.LastName);
			System.out.println("FirstName: " + reservation.FirstName);
			System.out.println("Adults:    " + reservation.Adults);
			System.out.println("Kids:      " + reservation.Kids);
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

			return true;
		} catch (Exception e) {
			System.out.println("Error Populating Rooms");
			e.printStackTrace();
			return false;
		}
	}

	private static void PopulateReservations(java.sql.Connection conn) {
		int CODE, Adults, Kids;
		String Room, LastName, FirstName;
		Date CheckIn, CheckOut;
		BigDecimal Rate;

		reservationList.clear();

		try {
			java.sql.Statement PopulateReservations = conn.createStatement();

			ResultSet rs = PopulateReservations.executeQuery("SELECT * FROM lab7_reservations");

			while (rs.next()) {
				CODE = rs.getInt("CODE");
				Room = rs.getString("Room");
				CheckIn = rs.getDate("CheckIn");
				CheckOut = rs.getDate("CheckOut");
				Rate = rs.getBigDecimal("Rate");
				LastName = rs.getString("LastName");
				FirstName = rs.getString("FirstName");
				Adults = rs.getInt("Adults");
				Kids = rs.getInt("Kids");

				reservationList
						.add(new Reservation(CODE, Room, CheckIn, CheckOut, Rate, LastName, FirstName, Adults, Kids));
			}

			PrintReservationList();

		} catch (Exception e) {
			System.out.println("Error Populating Reservation List...Exiting...");
			System.exit(-1);
		}
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

				System.out.println("============== ROOM NUMBER " + (roomList.indexOf(room) + 1) + " ==============");
				;
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

	public static void MakeReservation(java.sql.Connection conn, Scanner sc) {
		PopulateReservations(conn);

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
			DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
			checkIn = new java.sql.Date(df.parse(sc.next()).getTime());
			System.out.println("Enter deired check out date(mm/dd/yyyy)...");
			checkOut = new java.sql.Date(df.parse(sc.next()).getTime());
			System.out.println("Enter number of children...");
			children = sc.nextInt();
			System.out.println("Enter number of adults...");
			adults = sc.nextInt();

			// MakeReservation.executeQuery(sql)

		} catch (Exception e) {
			System.out.println("Error Making Reservation...Returning to Main Menu...");
			// e.printStackTrace();
		}
	}

	public static void DetailedReservationInfo(java.sql.Connection conn, Scanner sc) {
		try {
			String firstName = null, lastName = null, roomCode = null, tempDate, tempInt, p1, p2, p3, p4, p5, p6, p7;
			Date startDate = null, endDate = null;
			int reservationCode = -1, reservationCnt = 0;
			java.sql.Statement DetailedReservationInfo = conn.createStatement();
			DateFormat df = new SimpleDateFormat("mm/dd/yyyy");

			System.out.println(
					"Enter First Name (Or partail Name) to search, or leave empty for and press enter for any...");
			firstName = SanitizeString(sc.nextLine());

			System.out.println(
					"Enter Last Name (Or partail Name) to search, or leave empty for and press enter for any...");
			lastName = SanitizeString(sc.nextLine());

			System.out.println("Enter Start Date to search(mm/dd/yyyy), or leave empty for and press enter for any...");
			tempDate = sc.nextLine();
			if (tempDate.length() != 0)
				startDate = new java.sql.Date(df.parse(tempDate).getTime());

			System.out.println("Enter Start Date to search(mm/dd/yyyy), or leave empty for and press enter for any...");
			tempDate = sc.nextLine();
			if (tempDate.length() != 0)
				endDate = new java.sql.Date(df.parse(tempDate).getTime());

			System.out.println(
					"Enter Room Code (Or partial room code) to search, or leave empty for and press enter for any...");
			roomCode = SanitizeString(sc.nextLine());

			System.out.println(
					"Enter Reservation Code (Or partial reservation code) to search, or leave empty for and press enter for any...");
			tempInt = sc.nextLine();
			if (tempInt.length() != 0)
				reservationCode = Integer.parseInt(tempInt);

			p1 = "SELECT DISTINCT * FROM lab7_reservations, lab7_rooms WHERE Room = RoomCode";

			// If there is a first name given include it in query
			if (firstName.length() == 0)
				p2 = "";
			else
				p2 = " AND FirstName LIKE '" + firstName.toUpperCase() + "%'";

			// If there is a last name giver include it in query
			if (lastName.length() == 0)
				p3 = "";
			else
				p3 = " AND LastName LIKE '" + lastName.toUpperCase() + "%'";

			// If there is a start date include it in query
			if (startDate == null)
				p4 = "";
			else
				p4 = " AND CheckIn >= '" + startDate.toString() + "'";

			// If there is an end date include it in the query
			if (endDate == null)
				p5 = "";
			else
				p5 = " AND CheckOut <= '" + endDate.toString() + "'";

			// If there is room code include it in the query
			if (roomCode.length() == 0)
				p6 = "";
			else
				p6 = " AND Room LIKE '" + roomCode + "%'";

			// If there is a reservation code include it in the query
			if (reservationCode < 0)
				p7 = "";
			else
				p7 = " AND CAST(CODE AS CHAR) LIKE '" + reservationCode + "%'";

			ResultSet rs = DetailedReservationInfo.executeQuery(p1 + p2 + p3 + p4 + p5 + p6 + p7);

			System.out.println("\n\n\n\n");

			while (rs.next()) {
				System.out.println("========== Reservation Number " + (++reservationCnt) + " ==========");
				System.out.println("CODE:        " + rs.getInt("CODE"));
				System.out.println("Room:        " + rs.getString("Room"));
				System.out.println("Room Decor:  " + rs.getString("decor"));
				System.out.println("Room Name:   " + rs.getString("RoomName"));
				System.out.println("Check In:    " + rs.getDate("CheckIn"));
				System.out.println("Check Out:   " + rs.getDate("CheckOut"));
				System.out.println("Rate:        " + rs.getBigDecimal("Rate"));
				System.out.println("Last Name:   " + rs.getString("LastName"));
				System.out.println("First Name:  " + rs.getString("FirstName"));
				System.out.println("Adults:      " + rs.getInt("Adults"));
				System.out.println("Kids:        " + rs.getInt("Kids"));
				System.out.println("--------------------------------------------");

			}
			System.out.println("\n\n\n\n");

			// System.out.println(p1 + p2 + p3 + p4 + p5 + p6 + p7);

		} catch (Exception e) {
			System.out.println("Error Getting Detailed Reservation Info...Returning to Main Menu...");
			e.printStackTrace();
		}
	}

	public static void Revenue(java.sql.Connection conn) {
		try {
			String p1, p2;
			BigDecimal tempRev;
			float totalAnnualRev = 0;
			float[] totalRevs = new float[12];
			java.sql.Statement Revenue = conn.createStatement();

			p1 = "SELECT SUM(DATEDIFF(Checkout, CheckIn) * Rate) FROM lab7_reservations WHERE Room = '";
			p2 = "' AND YEAR(Checkout) = 2017 AND MONTH(Checkout) = ";


			for (Room room : roomList) {
				room.mr.clear();
				for (int i = 1; i <= 12; i++) {

					ResultSet rs = Revenue.executeQuery(p1 + room.RoomCode + p2 + i);
					rs.next();
					tempRev = rs.getBigDecimal(1);
					room.mr.add(tempRev);
					totalRevs[i - 1] += tempRev.floatValue();
				}
			}

			System.out.format(
					"+-----------++-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+------------+%n");
			System.out.format(
					"| Room Code || January   | February  | March     | April     | May       | June      | July      | August    | September | October   | November  | December  | Room Total |%n");
			System.out.format(
					"+___________++___________+___________+___________+___________+___________+___________+___________+___________+___________+___________+___________+___________+____________+%n");

			String leftAlignFormat = "| %-9s || %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-9.02f | %-10.02f |%n";

			for (Room room : roomList) {
				System.out.format(leftAlignFormat, room.RoomCode, room.mr.get(0), room.mr.get(1), room.mr.get(2),
						room.mr.get(3), room.mr.get(4), room.mr.get(5), room.mr.get(6), room.mr.get(7), room.mr.get(8),
						room.mr.get(9), room.mr.get(10), room.mr.get(11), room.getTotalRev());
				System.out.format(
						"+-----------++-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+------------+%n");

			}
			
			for (float mTotal : totalRevs) {
				totalAnnualRev += mTotal;
			}
			
			System.out.format(leftAlignFormat, "MONTHLY", totalRevs[0], totalRevs[1], totalRevs[2], totalRevs[3],
					totalRevs[4], totalRevs[5], totalRevs[6], totalRevs[7], totalRevs[8], totalRevs[9], totalRevs[10],
					totalRevs[11], totalAnnualRev);
			System.out.format(
					"+-----------++-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+-----------+------------+%n");

			System.out.println("\n\n\n\n");
			
		} catch (SQLException e) {
			System.out.println("Error Generating Revenue...Returning to Main Menu...");
			e.printStackTrace();
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
					sc.nextLine();
					DetailedReservationInfo(conn, sc);
					break;
				case 4:
					System.out.println("\n\n--- REVENUE ---");
					sc.nextLine();
					Revenue(conn);
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

